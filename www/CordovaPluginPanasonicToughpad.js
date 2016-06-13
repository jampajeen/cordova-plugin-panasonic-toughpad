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
var exec = require('cordova/exec');
var channel = require('cordova/channel');

function CordovaPluginPanasonicToughpad() {
    this.available = false;

    var me = this;
    channel.onCordovaReady.subscribe(function() {

        me.available = true;
    });
}

CordovaPluginPanasonicToughpad.prototype.scanBarcode = function(message, successCallback, errorCallback) {
    console.log('Plugin called: scanBarcode(...)');
    exec( successCallback, errorCallback, "CordovaPluginPanasonicToughpad", "scanBarcode", [message]);
};

CordovaPluginPanasonicToughpad.prototype.initAPI = function(message, successCallback, errorCallback) {
    console.log('Plugin called: initAPI(...)');
    exec( successCallback, errorCallback, "CordovaPluginPanasonicToughpad", "initAPI", [message]);
};

module.exports = new CordovaPluginPanasonicToughpad();
