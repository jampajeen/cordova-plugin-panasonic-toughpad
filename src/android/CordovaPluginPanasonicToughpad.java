package com.volho.example.cordova.plugin;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.widget.Toast;

public class CordovaPluginPanasonicToughpad extends CordovaPlugin {

    public CordovaPluginPanasonicToughpad() {

    }

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if ("scanBarcode".equals(action)) {
            this.scanBarcode(args.getString(0), callbackContext);
        } else {
            return false;
        }
        return true;
    }

    public void scanBarcode(String message, final CallbackContext callbackContext) {
        Context context = this.cordova.getActivity().getApplicationContext();
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();

        callbackContext.success("This barcode function is ready for development!!");
    }

}