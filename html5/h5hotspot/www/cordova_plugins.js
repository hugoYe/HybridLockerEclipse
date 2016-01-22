cordova.define('cordova/plugin_list', function(require, exports, module) {
module.exports = [
    {
        "file": "plugins/cordova-plugin-whitelist/whitelist.js",
        "id": "cordova-plugin-whitelist.whitelist",
        "runs": true
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
    {
        "file": "plugins/com.cooeelock.core.plugin/JarPlugin.js",
        "id": "cooeelock-plugin.JarPlugin",
        "clobbers": [
            "plugins.JarPlugin"
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