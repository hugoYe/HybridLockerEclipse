cordova.define("com.cooee.cordova.plugins.BluetoothStatus", function(require, exports, module) {

var exec = require('cordova/exec');

var BluetoothStatus = function() {
};

BluetoothStatus.initPlugin = function() {
    //wait for device to be ready
    exec(null, null, "BluetoothStatus", "initPlugin", []);
};

BluetoothStatus.enableBT = function() {
    exec(null, null, "BluetoothStatus", "enableBT", []);
};

BluetoothStatus.disableBT = function() {
    exec(null, null, "BluetoothStatus", "disableBT", []);
};

BluetoothStatus.promptForBT = function() {
    exec(null, null, "BluetoothStatus", "promptForBT", []);
};
BluetoothStatus.isBlueEnabled =function(win,fail) {
    if (typeof win != "function") {
                console.log("isBlueEnabled first parameter must be a function to handle wifi status.");
                return;
            }
            cordova.exec(
                // Cordova can only return strings to JS, and the underlying plugin
                // sends a "1" for true and "0" for false.
                function(result) {
                    win(result == "1");
                },
                fail, 'BluetoothStatus', 'isBlueEnabled', []
            );
}

BluetoothStatus.hasBT = false;
BluetoothStatus.hasBTLE = false;
BluetoothStatus.BTenabled = false;
BluetoothStatus.iosAuthorized = true;

module.exports = BluetoothStatus;
});
