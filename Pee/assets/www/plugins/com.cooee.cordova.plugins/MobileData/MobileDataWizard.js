cordova.define("com.cooee.cordova.plugins.mobiledata.MobileDataWizard", function(require, exports, module) {
 /*
 * Copyright 2015 Matt Parsons
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

/*
 * This is the interface for the WifiWizard Phonegap plugin.
 */

var MobileDataWizard = {

    /**
     *  Gets 'true' or 'false' if WiFi is enabled or disabled
     * @param 	win	callback function
     * @param 	fail
     */
    isMobileDataEnabled: function(win, fail) {
        if (typeof win != "function") {
            console.log("isMobileDataEnabled first parameter must be a function to handle mobiledata status.");
            return;
        }
        cordova.exec(
            // Cordova can only return strings to JS, and the underlying plugin
            // sends a "1" for true and "0" for false.
            function(result) {
                win(result == "1");
            },
            fail, 'MobileDataWizard', 'isMobileDataEnabled', []
        );
    },

    clickMobileData: function() {
        cordova.exec(null, null, 'MobileDataWizard', 'clickMobileData', []);
    }
};

module.exports = MobileDataWizard;

});
