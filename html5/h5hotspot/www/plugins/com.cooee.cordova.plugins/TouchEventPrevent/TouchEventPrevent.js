cordova.define("cordova-plugin-toucheventprevent.TouchEventPrevent", function(require, exports, module) {
 var toucheventprevent = {

    preventTouchSelf : function(intent) {

        cordova.exec(
            null,
            null,
            'TouchEventPrevent',
            'preventTouchSelf', []
        );
    },

};

module.exports = toucheventprevent;
});
