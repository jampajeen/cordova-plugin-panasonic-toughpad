/*
 * Copyright 2016 Thitipong Jampajeen <jampajeen@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.volho.example.cordova.plugin;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.widget.Toast;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.panasonic.toughpad.android.api.ToughpadApi;
import com.panasonic.toughpad.android.api.ToughpadApiListener;
import com.panasonic.toughpad.android.api.barcode.BarcodeData;
import com.panasonic.toughpad.android.api.barcode.BarcodeException;
import com.panasonic.toughpad.android.api.barcode.BarcodeListener;
import com.panasonic.toughpad.android.api.barcode.BarcodeReader;
import com.panasonic.toughpad.android.api.barcode.BarcodeReaderManager;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/*
* Main Cordova plugin class
*/
public class CordovaPluginPanasonicToughpad extends CordovaPlugin {

    private ToughpadBarcode barcode;

    public CordovaPluginPanasonicToughpad() { 
    }

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        Log.d("volho", "Cordova plugin initialized");
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        if ("scanBarcode".equals(action)) {
            this.scanBarcode(args.getString(0), callbackContext);
        } else if ("initAPI".equals(action)) {
            this.initAPI(args.getString(0), callbackContext);
        } else {
            return false;
        }
        return true;
    }

    public void initAPI(String message, final CallbackContext callbackContext) throws JSONException {
        if (this.barcode == null) {
            this.barcode = new ToughpadBarcode(this.cordova.getActivity());
        }

        try {
            this.barcode.setCallbackContext(callbackContext); // set CallbackContext 
            this.barcode.initApi();
            
        } catch (Exception e) {
            Log.d("volho", "Toughpad API initialize failed", e);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("success", false);
            jsonObject.put("message", "Toughpad API initialize failed");
            String json  = jsonObject.toString();
            callbackContext.error("Toughpad API initialize failed ");
        }
    }

    public void scanBarcode(String message, final CallbackContext callbackContext) throws JSONException {
        try {
            if (this.barcode.isSelectedReaderDeviceEnabled()) { // scan
                this.barcode.setCallbackContext(callbackContext); // set CallbackContext before scan
                this.barcode.toggleSoftwareTriggerReaderPress();
                Log.d("volho", "Toggle reader device");
            } else { // enable device
                this.barcode.selectLaserDevice();
                this.barcode.printSelectedReaderDeviceInfo();
                this.barcode.enableSelectedReaderDevice(true); // need event handler
                Log.d("volho", "Enabling reader device");
            }
        } catch (Exception e) {
            Log.e("volho", "Error barcode API function call", e);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("success", false);
            jsonObject.put("message", "Error barcode API function call");
            String json  = jsonObject.toString();
            callbackContext.error(json);
        }
    }
}

/*
* Barcode usage class class
*/
class ToughpadBarcode implements ToughpadApiListener, BarcodeListener {
    private boolean laserFlag;
    private List<BarcodeReader> readers;
    private BarcodeReader selectedReader;
    private Activity activity;
    private CallbackContext callbackContext;

    public ToughpadBarcode(Context context) {
        this.activity = (Activity) context;
    }

    public void initApi() {
        readers = null;
        selectedReader = null;
        ToughpadApi.initialize(this.activity, this);
    }
    
    public void destroyApi() {
        ToughpadApi.destroy();
    }

    private void handleError(final Exception ex) {
        Log.e("volho", "Toughpad API error", ex);
    }
    
    public void onApiConnected(int version) {
        Log.d("volho", "ToughpadBarcode.onApiConnected");

        Log.d("volho", "Toughpad API initialized");
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("success", true);
            jsonObject.put("message", "Toughpad API initialized");
            String json  = jsonObject.toString();
            this.callbackContext.success(json);
        } catch(JSONException e) {
            Log.e("volho", "JSON serialize failed, cannot send result", e);
        }

        readers = BarcodeReaderManager.getBarcodeReaders();

        Log.d("volho", "Available devices : ");
        for (BarcodeReader reader : readers) {
            Log.d("volho", " => " + reader.getDeviceName() + ", " + reader.getBarcodeType());
        }
    }

    public void onApiDisconnected() {
        Log.d("volho", "ToughpadBarcode.onApiDisconnected");
    }

    public void onRead(BarcodeReader bsObj, final BarcodeData result) {
        Log.d("volho", "ToughpadBarcode.onRead :");
        Log.d("volho", "ToughpadBarcode.onRead => result device: " + bsObj.getDeviceName());
        Log.d("volho", "ToughpadBarcode.onRead => result symbology: " + result.getSymbology());
        Log.d("volho", "ToughpadBarcode.onRead => result data: " + result.getTextData());
        
        if(this.callbackContext != null) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("success", true);
                jsonObject.put("device", bsObj.getDeviceName());
                jsonObject.put("symbology", result.getSymbology());
                jsonObject.put("data", URLEncoder.encode(result.getTextData(), "UTF-8"));
                jsonObject.put("message", "success");
                String json  = jsonObject.toString();
                this.callbackContext.success(json);
            } catch(JSONException e) {
                Log.e("volho", "JSON serialize failed, cannot send result", e);
            } catch(UnsupportedEncodingException e) {
                Log.e("volho", "URL encode failed, cannot send result", e);
            }
        } else {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("success", false);
                jsonObject.put("message", "Not found CallbackContext or null");
                String json  = jsonObject.toString();
                this.callbackContext.error(json);
            } catch(JSONException e) {
                Log.e("volho", "JSON serialize failed, cannot send result", e);
            }
        }
    }

    public void setCallbackContext(CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
    }

    public void toggleSoftwareTriggerReaderPress() {
        laserFlag = !laserFlag;
        pressSoftwareTrigger(laserFlag);
    }

    public void pressSoftwareTrigger(boolean flag) {
        try {
            selectedReader.pressSoftwareTrigger(flag);
        } catch (BarcodeException ex) {
            handleError(ex);
        }
    }

    public void enableHardwareTrigger(boolean flag) {
        try {
            selectedReader.setHardwareTriggerEnabled(flag);
        } catch (BarcodeException ex) {
            handleError(ex);
        }
    }

    public void disableReaderDevice(BarcodeReader reader) {
        try {
            selectedReader.disable();
            selectedReader.clearBarcodeListener();
            Log.d("volho", "Device : " + selectedReader.getDeviceName() + "is disabled");
        } catch (BarcodeException ex) {
            handleError(ex);
        }
    }

    public void enableReaderDevice(BarcodeReader reader) {
        EnableReaderTask task = new EnableReaderTask();
        task.execute(selectedReader);
    }

    public boolean hasSelectedReaderDevice() {
        return this.selectedReader != null;
    }

    public boolean isSelectedReaderDeviceEnabled() {
        return selectedReader != null && selectedReader.isEnabled();
    }

    public void enableSelectedReaderDevice(boolean flag) {

        if (selectedReader.isEnabled() && !flag) {
            disableReaderDevice(selectedReader);
        } else if (!selectedReader.isEnabled() && flag) {
            enableReaderDevice(selectedReader);
        } else {
            Log.e("volho", "Wrong logic/state for enabling reader device");
            Log.e("volho", " => selectedReader.isEnabled() : " + selectedReader.isEnabled());
            Log.e("volho", " => flag : " + flag);
        }
    }

    public void selectLaserDevice() {
        for (int i = 0; i < readers.size(); i++) {
            if (readers.get(i).getBarcodeType() != BarcodeReader.BARCODE_TYPE_CAMERA) {
                selectReader(i);
                break;
            }
        }
    }

    public void selectReader(int position) {
        selectedReader = readers.get(position);
        Log.d("volho", "Select Device: " + selectedReader.getDeviceName());
    }

    public void unselectReader() {
        selectedReader = null;
        Log.d("volho", "Unselect device");
    }

    public String getDeviceTypeString(BarcodeReader reader) {
        String deviceType = "Unknown";
        switch (reader.getBarcodeType()) {
            case BarcodeReader.BARCODE_TYPE_CAMERA:
                deviceType = "BARCODE_TYPE_CAMERA";
                break;
            case BarcodeReader.BARCODE_TYPE_ONE_DIMENSIONAL:
                deviceType = "BARCODE_TYPE_ONE_DIMENSIONAL";
                break;
            case BarcodeReader.BARCODE_TYPE_TWO_DIMENSIONAL:
                deviceType = "BARCODE_TYPE_TWO_DIMENSIONAL";
                break;
        }
        return deviceType;
    }
    
    public void printSelectedReaderDeviceInfo() {
        Log.d("volho", "Selected Device Info: ");
        Log.d("volho", " => HardwareTriggerAvailable : " + selectedReader.isHardwareTriggerAvailable());
        if (selectedReader.isHardwareTriggerAvailable()) {
            Log.d("volho", " => HardwareTriggerEnabled : " + selectedReader.isHardwareTriggerEnabled());
        }
        Log.d("volho", " => Selected reader is enabled/disabled : " + selectedReader.isEnabled());
        Log.d("volho", " => Selected Device Type : " + getDeviceTypeString(selectedReader));
        if (selectedReader.isExternal()) {
            Log.d("volho", " => This is external device");
        }
    }

    private class EnableReaderTask extends AsyncTask<BarcodeReader, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            Log.d("volho", "EnableReaderTask.onPreExecute");
        }

        @Override
        protected Boolean doInBackground(BarcodeReader... params) {
            Log.d("volho", "EnableReaderTask.doInBackground");
            try {
                params[0].enable(10000);
                params[0].addBarcodeListener(ToughpadBarcode.this);
                return true;
            } catch (BarcodeException ex) {
                handleError(ex);
                return false;
            } catch (TimeoutException ex) {
                handleError(ex);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Log.d("volho", "EnableReaderTask.onPostExecute");
            if (result) {
                Log.d("volho", "EnableReaderTask.onPostExecute => " + selectedReader.getDeviceName() + "is enabled.");
            }
        }
    }
}
