cordova.define("cooeelock-plugin.CooeelockPlugin", function(require, exports, module) {

var exec = require('cordova/exec');

var CooeelockPlugin = function() {
};


CooeelockPlugin.test = function(arg1, arg2, arg3) {
    exec(null, null, "CooeelockPlugin", "test", [arg1, arg2, arg3]);
};


module.exports = CooeelockPlugin;
});
