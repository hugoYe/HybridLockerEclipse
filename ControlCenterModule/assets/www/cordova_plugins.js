cordova.define('cordova/plugin_list', function(require, exports, module) {
module.exports = [
    {
        "file": "plugins/cordova-plugin-whitelist/whitelist.js",
        "id": "cordova-plugin-whitelist.whitelist",
        "runs": true
    },
    {
        "file": "plugins/cordova-plugin-camera/www/CameraConstants.js",
        "id": "cordova-plugin-camera.Camera",
        "clobbers": [
            "Camera"
        ]
    },
    {
        "file": "plugins/cordova-plugin-camera/www/CameraPopoverOptions.js",
        "id": "cordova-plugin-camera.CameraPopoverOptions",
        "clobbers": [
            "CameraPopoverOptions"
        ]
    },
    {
        "file": "plugins/cordova-plugin-camera/www/Camera.js",
        "id": "cordova-plugin-camera.camera",
        "clobbers": [
            "navigator.camera"
        ]
    },
    {
        "file": "plugins/cordova-plugin-camera/www/CameraPopoverHandle.js",
        "id": "cordova-plugin-camera.CameraPopoverHandle",
        "clobbers": [
            "CameraPopoverHandle"
        ]
    },
    {
        "file": "plugins/com.cooee.cordova.plugins/FlashLight/Flashlight.js",
        "id": "com.cooee.cordova.plugins.Flashlight",
        "clobbers": [
            "plugins.flashlight"
        ]
    },
    {
        "file": "plugins/com.cooee.cordova.plugins/Bluetooth/BluetoothStatus.js",
        "id": "com.cooee.cordova.plugins.BluetoothStatus",
        "clobbers": [
            "plugins.BluetoothStatus"
        ]
    },
    {
        "file": "plugins/com.cooee.cordova.plugins/wifi/WifiWizard.js",
        "id": "com.cooee.cordova.plugins.wifiwizard.WifiWizard",
        "clobbers": [
            "plugins.WifiWizard"
        ]
    },
    {
        "file": "plugins/com.cooee.cordova.plugins/MobileData/MobileDataWizard.js",
        "id": "com.cooee.cordova.plugins.mobiledata.MobileDataWizard",
        "clobbers": [
            "plugins.MobileDataWizard"
        ]
    },
    {
        "file": "plugins/com.cooee.cordova.plugins/AppsApi/AppsApi.js",
        "id": "cordova-plugin-appsapi.AppsApi",
        "clobbers": [
            "plugins.AppsApi"
        ]
    },
    {
        "file": "plugins/com.cooee.cordova.plugins/TouchEventPrevent/TouchEventPrevent.js",
        "id": "cordova-plugin-toucheventprevent.TouchEventPrevent",
        "clobbers": [
            "plugins.TouchEventPrevent"
        ]
    },
    {
        "file": "plugins/com.cooee.cordova.plugins/EventStatistics/EventStatistics.js",
        "id": "cordova-plugin-eventstatistics.EventStatistics",
        "clobbers": [
            "plugins.EventStatistics"
        ]
    },
];
module.exports.metadata = 
// TOP OF METADATA
{
    "cordova-plugin-whitelist": "1.0.0",
    "cordova-plugin-camera": "1.2.0",
    "cordova-plugin-flashlight": "3.0.0",
    "cordova-plugin-bluetooth-status": "1.0.3",
    "com.pylonproducts.wifiwizard": "0.2.9",
    "cordova-plugin-appavailability": "0.4.2",
}
// BOTTOM OF METADATA
});