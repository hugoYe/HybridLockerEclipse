cordova.define("cordova-plugin-appsapi.AppsApi", function(require, exports, module) {
 var appAvailability = {
    
    check: function(urlScheme, successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
            "AppsApi",
            "checkAvailability",
            [urlScheme]
        );
    },
    
    checkBool: function(urlScheme, callback) {
        cordova.exec(
            function(success) { callback(success); },
            function(error) { callback(error); },
            "AppsApi",
            "checkAvailability",
            [urlScheme]
        );
    },

    startApp : function(intent) {

        var androidIntent = intent;

        // fire
        cordova.exec(
            null,
            null,
            'AppsApi',
            'startActivity', [androidIntent]
        );
    },

	/**
	  * js中用来启动配置的应用程序
	  * 
	  * intentUri(String) : 需要启动的intent ---> example: "#Intent;action=android.intent.action.MAIN;category=android.intent.category.LAUNCHER;launchFlags=0x10000000;component=com.coco.lock2.app.Pee/.InsideLockActivity;end"
	  * appId(long) : 需要配置的appid(微入口所需要配置的参数) ---> example : 10009
	  * createShortcut(boolean) : 是否需要创建桌面快捷方式 ，配合title及imgBase64使用
	  * title(String) : 创建桌面快捷方式的名称 ---> example: "百度"
	  * imgBase64(String) : 创建桌面快捷方式的图标 ---> example: "data:image/gif;base64, werfjls.."
	  * 
	  */
    startShortcut : function(intentUri, appId, createShortcut, title, imgBase64) {
        cordova.exec(
              null,
              null,
              'AppsApi',
              'startShortcut', [intentUri, appId, createShortcut, title, imgBase64]
          );
    },


	 /**
	  * js中用来启动url链接的接口
	  * 
	  * url(String) : 需要启动的链接  ---> example: "http://m.baidu.com"
	  * createShortcut(boolean) : 是否需要创建桌面快捷方式 ，配合title及imgBase64使用
	  * title(String) : 创建桌面快捷方式的名称 ---> example: "百度"
	  * imgBase64(String) : 创建桌面快捷方式的图标 ---> example: "data:image/gif;base64, werfjls.."
	  * 
	  */
    startUrl : function(url, createShortcut, title, imgBase64) {
        cordova.exec(
              null,
              null,
              'AppsApi',
              'startUrl', [url, createShortcut, title, imgBase64]
          );
    },

    bindFavoriteApp : function() {
        cordova.exec(null, null, 'AppsApi', 'bindFavoriteApp', []);
    },
    
    resetLight : function() {
    	cordova.exec(null, null, 'AppsApi', 'resetLight', []);
    }
    
};

module.exports = appAvailability;
});
