cordova.define("cooeelock-plugin.ApkPlugin", function(require, exports, module) {

var exec = require('cordova/exec');

var ApkPlugin = function() {
};

var version = 1;

/* ------------------ 插件功能接口示例 ----------------- */

/**
 * 
 * @param winFunc
 *			    （function）函数类型参数，用于接收java端执行成功的返回结果, 可以为null
 *
 *
 * @param failFunc
 *			    （function）函数类型参数，用于接收java端执行失败的返回结果, 可以为null
 *
 *
 * @param args 
 *			  剩下的为你所定义的任何类型参数
 *
 *
 * @param execThreadType
 *            （String）
 *            接口执行类型：为EXEC_TYPE_UI、EXEC_TYPE_THREAD_POOL、EXEC_TYPE_NONE中的任何一个
 *
 *
 * */
/*
ApkPlugin.example = function(winFunc, failFunc, execThreadType, args....) {

	try {
		if (execThreadType=="EXEC_TYPE_UI"||execThreadType=="EXEC_TYPE_THREAD_POOL"||execThreadType=="EXEC_TYPE_NONE"){
			exec(winFunc, failFunc, "ApkPlugin", "example", [version, execThreadType, args....]);
		}else {
			throw "execThreadType is not a valid value!";
		}
	} catch (err){
		console.log(err);
	}
};
*/

/* ------------------ 插件功能接口示例 ----------------- */


/* ------- 请按照示例来这里尽情的添加你想要的功能接口吧 --------*/
/* ------- 注意：接口第一个参数必须为execThreadType,第二个参数必须将插件版本号version传递下去 ------ */

ApkPlugin.click = function(winFunc, failFunc, execThreadType) {

	try {
		if (execThreadType=="EXEC_TYPE_UI"||execThreadType=="EXEC_TYPE_THREAD_POOL"||execThreadType=="EXEC_TYPE_NONE"){
			exec(winFunc, failFunc, "ApkPlugin", "example", [version, execThreadType]);
		} else {
			throw "execThreadType is not a valid value!";
		}
	} catch (err){
		console.log(err);
	}
};

/**
 *----下载插件的接口
 * 
 *----- url(String) : 下载链接 ---> example: "http://www.coolauncher.cn/locker/Plugins/ApkPlugins/proxy.apk"
 *----- package(String) : 插件包名 ---> example: "com.cooeeui.lock.apk.plugins"
 *----- name(String) : 插件名 ---> example: "Plugins"
 * 
 */
ApkPlugin.download = function( url,package,name) {
	exec(null, null, "ApkPlugin", "downLoadApk", [url,package,name]);
};


module.exports = ApkPlugin;
});
