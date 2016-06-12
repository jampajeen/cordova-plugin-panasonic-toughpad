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

module.exports = new CordovaPluginPanasonicToughpad();