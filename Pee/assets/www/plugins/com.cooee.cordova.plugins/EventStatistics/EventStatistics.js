cordova.define("cordova-plugin-eventstatistics.EventStatistics", function(require, exports, module) {
 var EventStatistics = {
    
	/**
	  * js中用统计操作事件类型的接口
	  * 
	  * eventType(String) : 统计的事件类型 ---> example: "0036"
	  * 
	  */
	onEvent : function(eventType) {
        console.log("onEvent-js111")
        cordova.exec(
            null,
            null,
            'EventStatistics',
            'onEvent', [eventType]
        );
    },
};

module.exports = EventStatistics;
});