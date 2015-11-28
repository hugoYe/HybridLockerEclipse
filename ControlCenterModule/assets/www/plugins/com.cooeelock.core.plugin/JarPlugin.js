cordova.define("cooeelock-plugin.JarPlugin", function(require, exports, module) {

var exec = require('cordova/exec');

var JarPlugin = function() {
};

var defaultPluginName = "proxydex.jar";
var defaultPluginUrl = "http://www.coolauncher.cn/locker/Plugins/JarPlugins/proxydex.jar";
var defaultPluginVersion = 1;


/* ------------------ 插件功能接口示例 ----------------- */
/**
 * @param winFunc
 *			    （function）函数类型参数，用于接收java端执行成功的返回结果, 可以为null
 * @param failFunc
 *			    （function）函数类型参数，用于接收java端执行失败的返回结果, 可以为null
 * @param execThreadType
 *            （String）接口执行的线程类型：
 *            为"EXEC_TYPE_UI"、"EXEC_TYPE_THREAD_POOL"、"EXEC_TYPE_NONE"中的任何一个
 * @param args 
 *			  剩下的为你所定义的任何类型参数
 *
 * */
/*
JarPlugin.example = function(winFunc, failFunc, execThreadType, args....) {

	try {
		if (execThreadType=="EXEC_TYPE_UI"||execThreadType=="EXEC_TYPE_THREAD_POOL"||execThreadType=="EXEC_TYPE_NONE"){
			exec(winFunc, failFunc, "JarPlugin", "example", [version, execThreadType, args....]);
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

JarPlugin.click = function(winFunc, failFunc, execThreadType) {

	try {
		if (execThreadType=="EXEC_TYPE_UI"||execThreadType=="EXEC_TYPE_THREAD_POOL"||execThreadType=="EXEC_TYPE_NONE"){
			exec(winFunc, failFunc, "JarPlugin", "example", [version, execThreadType]);
		} else {
			throw "execThreadType is not a valid value!";
		}
	} catch (err){
		console.log(err);
	}
};


module.exports = JarPlugin;
});
