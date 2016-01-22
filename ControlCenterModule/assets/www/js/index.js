/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
var app = {
    // Application Constructor
    initialize: function() {
        this.bindEvents();
    },
    // Bind Event Listeners
    //
    // Bind any events that are required on startup. Common events are:
    // 'load', 'deviceready', 'offline', and 'online'.
    bindEvents: function() {
        document.addEventListener('deviceready', this.onDeviceReady, false);
        console.log("###### plugins.deviceready.onEvent begin");
        document.addEventListener("backbutton", onBackKeyDown, false);
    },
    // deviceready Event Handler
    //
    // The scope of 'this' is the event. In order to call the 'receivedEvent'
    // function, we must explicitly call 'app.receivedEvent(...);'
    onDeviceReady: function() {
        plugins.JarPlugin.startJarplugin();
				console.log("###### plugins.JarPlugin.startJarplugin");
	      init();
    },

};
app.initialize();

// Handle the back button
function onBackKeyDown() {
    console.log("###### onBackKeyDown");
    slideUp();
}

var maxHeight,startY,endY,startX,endX,dragLength= 0,horizonLength=0;
var translateX,translateY,newTranslateX,newTranslateY,height,contentHeight;
var keyArr=[],country,pageNum= 0,currentPage=1;
var longTouch;
var topArea;
var colorArr=["a","b","c","d","e","f","g","h"];
var ring,ringRight;
var ACTION_LOAD_SUCCESS = "0037";
var ACTION_CLICK_KEYWORD = "0036";

function init() {
	$("#background").bind('touchstart', function () {
		console.log("touchstart!");
		plugins.TouchEventPrevent.preventTouchSelf();
	});
	$("#background").bind('click',function(event){
		plugins.AppsApi.resetLight();
		event.stopPropagation();
	})
	topArea = $(".top-area");
	ring = $(".ring");
	ring.bind({
		'touchstart': dragStart,
		'touchmove': drag,
		'touchend': dragEnd
	});
	translateX = 0;
	getData();
	$(".refresh").bind('click', refreshData);
}



//开始拖动时执行的函数
function dragStart(event){
	event.preventDefault();
	$("body").css({'background':"rgba(0,0,0,0.5)"});
	startX=event.originalEvent.targetTouches[0].pageX;
	$("#background").unbind('touchstart');
	startY=event.originalEvent.targetTouches[0].pageY;
	dragLength=0;
	horizonLength=0;
	plugins.AppsApi.resetLight();
	if (Math.abs(translateY)==maxHeight){
		longTouch=window.setTimeout("ring.unbind('touchstart');ring.unbind('touchmove');ring.unbind('touchend');ring.bind({'touchmove':horizonMove});ring.bind({'touchend':horizonEnd});",500);
		topArea.css({
			"transition-duration":"0ms",
			"-webkit-transform": "translate3d(0,"+translateY+"px"+",0)"
		});
	}else {
		topArea.css({
			"transition-duration":"0ms",
			"-webkit-transform": "translate3d(0,-70px,0)"
		});
	}
	newTranslateY=translateY;
	event.stopPropagation();
}

//拖动时执行的函数
function drag(event){
	endY=event.originalEvent.targetTouches[0].pageY;
	if(Math.abs(newTranslateY-translateY)>2){
		clearTimeout(longTouch);
	}
	topArea.css({"transition-duration":"0ms"});
	dragLength=endY-startY;
	if (translateY+dragLength<=0){
		topArea.css({
			"-webkit-transform": "translate3d(0,"+(translateY+dragLength)+"px"+",0)"
		});
		newTranslateY=translateY+dragLength;
	}
	event.stopPropagation();
}

//拖动结束时执行的函数
function dragEnd(event){
	clearTimeout(longTouch);
	translateY=newTranslateY;
	topArea.css({
		"transition-duration":"150ms"
	});
	if(dragLength>=0||Math.abs(translateY)<70){
		slideDown();
	}else {
		slideUp();
	}
	event.stopPropagation();
}

//控制中心完全出来后重新为触摸区域绑定的拖动结束后执行的函数
function upDragEnd(event){
	translateY=newTranslateY;
	topArea.css({
		"transition-duration":"150ms"
	});
	if (dragLength<=0||Math.abs(translateY)<70){
		slideUp();
	}else{
		slideDown();
	}
	event.stopPropagation();
}


function horizonMove(event){
	endX=event.originalEvent.targetTouches[0].pageX;
	horizonLength=(endX-startX)*2;
	newTranslateX=translateX+horizonLength;
	ring.css({
		"-webkit-transform":"scale(0.5)"+" "+"translate3d("+newTranslateX+"px"+",0,0)"
	})
	translateX=newTranslateX;
	startX=endX;
	event.stopPropagation();
}

function horizonEnd(event){
	$("body").css({'background':"rgba(0,0,0,0)"});
	$("#background").bind('touchstart',function(){console.log("touchstart!");plugins.TouchEventPrevent.preventTouchSelf();});
	ring.unbind('touchmove',horizonMove);
	ring.unbind('touchend',horizonEnd);
	ring.bind({'touchstart':dragStart,
		'touchmove':drag,
		'touchend':dragEnd
	});
	localStorage.ringRight=ringRight-parseInt(translateX)/2;
	event.stopPropagation();
}

//热点上移动画
function slideUp(){
	$("body").css({'background':"rgba(0,0,0,0)"});
	$("#background").bind('touchstart',function(){console.log("touchstart!");plugins.TouchEventPrevent.preventTouchSelf();});
	topArea.css({
		"-webkit-transform":"translate3d(0,-100%,0)"
	});
	window.setTimeout("topArea.css({'transition-duration':'0ms','animation':'swing-up-down 0.7s linear','-webkit-animation':'swing-up-down 0.7s linear'})",150);
	window.setTimeout("topArea.css({'animation':'none','-webkit-animation':'none'})",700);
	ring.unbind("touchend").bind("touchend",dragEnd);
	translateY=-maxHeight;
}

//热点下移动画
function slideDown(){
	$("#background").unbind('touchstart');
	topArea.css({
		"-webkit-transform":"translate3d(0,-70px,0)"
	});
	window.setTimeout("topArea.css({'transition-duration':'0ms','animation':'swing-down-up 0.7s linear','-webkit-animation':'swing-down-up 0.7s linear'})",150);
	window.setTimeout("topArea.css({'animation':'none','-webkit-animation':'none'})",700);
	ring.unbind("touchend").bind("touchend",upDragEnd);
	translateY=-70;
}


//获取热点数据
function getData(){
	$.ajax({
		type:"GET",
		url:"json/hotspot.json",
		async:false,
		success:function(data){
			keyArr=JSON.parse(data)["keyword"];
			country=JSON.parse(data)["geo_country"];
		},
		error:function(XMLHttpRequest, textStatus){
			console.log(textStatus);
		}
	})
	initData();
}


//初始化热点区域
function initData(){
	if (keyArr.length>0){
		pageNum=Math.ceil(keyArr.length/8);
		$(".hotspot .content").html("");
		var subArr;
		if (currentPage==pageNum){
			subArr=keyArr.slice((currentPage-1)*8);
		}else {
			subArr=keyArr.slice((currentPage-1)*8,(currentPage-1)*8+8);
		}
		var colorArr=randomClass({data:{num:subArr.length}});
		for (var i=0;i<subArr.length;i++){
			$("<div></div>").html(subArr[i]).addClass("content-item "+colorArr[i]).appendTo(".hotspot .content");
		}
		var href=(country=='CN')?"http://m.baidu.com/s?from=1013461a&word=":"http://searchmobileonline.com/?pubid=204793810&q=";
		$(".content-item").bind("click",function(event){
			href=href+$(this).html();
			plugins.AppsApi.startUrl(href, false, "", "");
			plugins.EventStatistics.onEvent(ACTION_CLICK_KEYWORD);
			event.stopPropagation();
		})

		try {
			if(localStorage.ringRight){
				ring.css("right",localStorage.ringRight+"px");
			}
			ringRight=parseInt(ring.css("right"));
		}catch(err){
			console.log(err);
		}

		plugins.EventStatistics.onEvent(ACTION_LOAD_SUCCESS);

		ring.css({"-webkit-transform":"scale(0.5) translate3d(0,0,0)","transition-duration":"150ms"});
		window.setTimeout("ring.css({'transition-duration':'0ms','animation':'swing 3s ease-in-out 1','-webkit-animation':'swing 3s ease-in-out 1'})",150);

		height=topArea.innerHeight();
		contentHeight=topArea.height();
		maxHeight=height;
		translateY=-maxHeight;

	}
}

//刷新数据
function refreshData(event){
	plugins.AppsApi.resetLight();
	if (currentPage<pageNum){
		currentPage++;
	}else if(currentPage==pageNum){
		currentPage=1;
	}
	var subArr;
	if (currentPage==pageNum){
		subArr=keyArr.slice((currentPage-1)*8);
	}else {
		subArr=keyArr.slice((currentPage-1)*8,(currentPage-1)*8+8);
	}
	var colorArr=randomClass({data:{num:subArr.length}});
	$(".hotspot .content").html("");
	for (var i=0;i<subArr.length;i++){
		$("<div></div>").html(subArr[i]).addClass("content-item "+colorArr[i]).appendTo(".hotspot .content");
	}
	var href=(country=='CN')?"http://m.baidu.com/s?from=1013461a&word=":"http://searchmobileonline.com/?pubid=204793810&q=";
	$(".content-item").bind("click",function(event){
		href=href+$(this).html();
		plugins.AppsApi.startUrl(href, false, "", "");
		plugins.EventStatistics.onEvent(ACTION_CLICK_KEYWORD);
		event.stopPropagation();
	})
	height=topArea.innerHeight();
	contentHeight=topArea.height();
	maxHeight=height;
	event.stopPropagation();
}

//重排颜色列表
function randomClass(event){
	var newArr=colorArr.slice(0,event.data.num);
	newArr.sort(function(){return 0.5-Math.random()});
	return newArr;
}
