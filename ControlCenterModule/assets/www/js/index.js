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
        document.addEventListener("backbutton", onBackKeyDown, false);
    },
    // deviceready Event Handler
    //
    // The scope of 'this' is the event. In order to call the 'receivedEvent'
    // function, we must explicitly call 'app.receivedEvent(...);'
    onDeviceReady: function() {
        // 初始化快捷方式开关
        initShortcut();
        $("#button_th").css({"display":"block"});
    },

};
app.initialize();

// Handle the back button
function onBackKeyDown() {
    console.log("###### onBackKeyDown");
    slideDown();
}

var maxHeight,startY,endY,dragLength= 0,translateY,height,HEIGHT;
var HEIGHT=document.body.clientHeight;
var resizeTimer = null;
//应用点击事件
var ACTION_CLICK_HEART_APP = "0037";
$(function() {
    $("#background").bind('touchstart',function(){console.log("touchstart!");plugins.TouchEventPrevent.preventTouchSelf();});
    translateY=0;
	//为浏览器绑定事件，当浏览器窗口大小发生变化时执行doResize函数
	$(window).resize(function(){
       		resizeTimer = resizeTimer ? null : setTimeout(doResize,0);
    });
	$("#state_wifi").bind('click', changeWifiState);
	$("#state_mobiledata").bind('click', changeMobileDataState);
	$("#state_bluetooth").bind('click', changeBluetoothState);
	$("#state_light").bind('click', changeFlashLightState);
	$("#state_camera").bind('click', openCamera);

	//app图标点击动画
	$("#app .app_item img").bind('touchstart',function(event){
		$(this).css({"-webkit-transform":"scale3d(1.1,1.1,1)"});
		event.stopPropagation();
	})
	$("#app .app_item img").bind('touchend',function(event){
		$(this).css({"-webkit-transform":"scale3d(1,1,1)"});
		event.stopPropagation();
	})

	//状态栏图标点击动画
	$("#state .state_item div img").bind('touchstart',function(event){
		$(this).css({"-webkit-transform":"scale3d(1.1,1.1,1)"});
		event.stopPropagation();
	})
    $("#state .state_item div img").bind('touchend',function(event){
		$(this).css({"-webkit-transform":"scale3d(1,1,1)"});
		event.stopPropagation();
	})

	$(".link_item a").bind("click",function(event){
		plugins.AppsApi.startUrl(this.href, false, "", "");
		event.preventDefault();
		event.stopPropagation();
	})
	$(".link_table a").bind("click",function(event){
		plugins.AppsApi.startUrl(this.href, false, "", "");
		event.preventDefault();
		event.stopPropagation();
	})
	// 节操游戏链接，需要创建桌面快捷方式图标
	$("#game a").unbind("click").bind("click",function(event){
		var src=$(this).siblings().attr("src");
		var p=$(this).html();
		var url=$(this).attr("href");
		convertImgToBase64(src,function(base64Img) {
			plugins.AppsApi.startUrl(url, true, p, base64Img);
		});
		event.preventDefault();
        event.stopPropagation();
	});
	$("a").bind('click',function(event){
		//统计应用点击次数
    	plugins.EventStatistics.onEvent(ACTION_CLICK_HEART_APP);
    	event.stopPropagation();
    })

	//为控制中心图标绑定事件
	$("#button_th").bind('touchstart', dragStart);
	$("#button_th").bind('touchmove', drag);
	$("#button_th").bind('touchend',dragEnd);
})

//开始拖动时执行的函数
function dragStart(event){
	event.preventDefault();
	height=$('#slide').height();
    maxHeight=height;
	$("#background").unbind('touchstart');
	$('#button_th').css({'opacity':0});
	startY=event.originalEvent.targetTouches[0].pageY;
	if (translateY!=maxHeight) {
		translateY=HEIGHT-event.originalEvent.targetTouches[0].pageY;
		$("#slide").css({
			"transition-duration":"50ms",
			"-webkit-transform": "translate3d(0,"+"-"+translateY+"px"+",0)"
		});
	}else {
		$("#slide").css({
			"transition-duration":"0ms",
			"-webkit-transform": "translate3d(0,"+"-"+translateY+"px"+",0)"
		});
	}
	event.stopPropagation();
}

//拖动时执行的函数
function drag(event){
	endY=event.originalEvent.targetTouches[0].pageY;
	$("#slide").css({"transition-duration":"0ms"});
	dragLength=startY-endY;
	var newTranslateY=translateY+dragLength;
	if (newTranslateY<=maxHeight){
		$("#slide").css({
			"-webkit-transform": "translate3d(0,"+"-"+newTranslateY+"px"+",0)"
		});
		translateY=newTranslateY;
	}
	startY=endY;
	event.stopPropagation();
}

//控制中心打开事件
var ACTION_OPEN_CONTROL_CENTER = "0036";
//拖动结束时执行的函数
function dragEnd(event){
	$("#slide").css({
		"transition-duration":"150ms",
	});
	if(translateY<=height*0.25){
		slideDown();
	}else {
		slideUp();
		//统计控制中心打开次数
		plugins.EventStatistics.onEvent(ACTION_OPEN_CONTROL_CENTER);
	}
	event.stopPropagation();
}

//控制中心完全出来后重新为触摸区域绑定的拖动结束后执行的函数
function upDragEnd(event){

	$("#slide").css({
		"transition-duration":"150ms",
	});
	if (dragLength>0){
		slideUp();
	}else {
		slideDown();
	}
	event.stopPropagation();
}

//控制中心上移动画
function slideUp(){
	$('#button_th').css({'display':'none','opacity':0});
    //点击背景后将控制中心收起
    $("#background").click(function(){
        console.log("click background!!!");
        slideDown();
    });
	$("#button_th").unbind('touchstart');
	$("#button_th").unbind('touchmove');
	$("#button_th").unbind('touchend');
	$(".touch_item").unbind('touchstart');
	$(".touch_item").unbind('touchmove');
	$(".touch_item").unbind('touchend');
	$(".touch_item").bind('touchstart',dragStart);
	$(".touch_item").bind('touchmove',drag);
	$(".touch_item").bind('touchend',upDragEnd);
	$("#app .app_item img").bind('touchstart',function(event){
		$(this).css({"-webkit-transform":"scale3d(1.1,1.1,1)"});
		event.stopPropagation();
	})
	$("#app .app_item img").bind('touchend',function(event){
		$(this).css({"-webkit-transform":"scale3d(1,1,1)"});
		event.stopPropagation();
	})
	$("#slide").css({
		"-webkit-transform":"translate3d(0,-100%,0)",
	});
	$("img.touch").css({"-webkit-transform":"rotateZ(0deg)"});
	translateY=maxHeight;
}

//控制中心下移动画
function slideDown(){
	window.setTimeout("$('#button_th').css({'display':'block','opacity':1});",150);
	$("#background").unbind('click');
	$("#background").bind('touchstart',function(){console.log("touchstart!");plugins.TouchEventPrevent.preventTouchSelf();});
	$(".touch_item").unbind('touchstart');
	$(".touch_item").unbind('touchmove');
	$(".touch_item").unbind('touchend');
	$("#button_th").unbind('touchstart');
	$("#button_th").unbind('touchmove');
	$("#button_th").unbind('touchend');
	$("#button_th").bind('touchstart',dragStart);
	$("#button_th").bind('touchmove',drag);
	$("#button_th").bind('touchend',dragEnd);
	$("#slide").css({
		"-webkit-transform":"translate3d(0,0%,0)",
	});
	$("img.touch").css({"-webkit-transform":"rotateZ(180deg)"});
	translateY=0;
}

function convertImgToBase64(url, callback, outputFormat) {
	var canvas = document.createElement('CANVAS'),
	ctx = canvas.getContext('2d'),
	img = new Image;
	img.crossOrigin = 'Anonymous';
	img.onload = function(){
		canvas.height = img.height;
		canvas.width = img.width;
		ctx.drawImage(img,0,0);
		var dataURL = canvas.toDataURL(outputFormat || 'image/png');
		callback.call(this, dataURL);
		canvas = null;
	};
	img.src = url;
}

//初始化app栏,并为每个app绑定openApp函数
function bindWebFavoriteApp(data){
	var ul=document.getElementById("app");
	ul.innerHTML="";
	for (var i=0;i<data.app.length&&i<5;i++){
        var intent = data.app[i].intent;
        var bitmap = data.app[i].bitmap;
        if(bitmap != null) {
            var appimg = document.createElement("img");
            var div=document.createElement("div");
            var li=document.createElement("li");
            if (i==4){
                li.className="app_item last";
            }else {
                li.className="app_item";
            }
            appimg.src = "data:image/gif;base64,"+bitmap;
            $(appimg).bind("click",{intent:intent},openApp);
            div.appendChild(appimg);
            li.appendChild(div);
            ul.appendChild(li);
        }
    }
}

// 打开常用应用
function openApp(event) {
	console.log(event.data.intent);
	plugins.AppsApi.startApp(event.data.intent);
	event.stopPropagation();
}

//重置当前页面高度
function doResize(){
	HEIGHT=document.body.clientHeight;
	height=$("#slide").height();
    maxHeight = height;
}

function initShortcut() {
	// 初始化常用app
	plugins.AppsApi.bindFavoriteApp();

	// 初始化控制中心快捷方式图标
	wifiInit();
	mobileDataInit();
	bluetoothInit();
}

function wifiInit() {
	var index;
	var wifiImg=document.getElementById("state_wifi");
	plugins.WifiWizard.isWifiEnabled(function(res) {
		if(res) {
			index=wifiImg.src.lastIndexOf("off");
			if(index!=-1) {
				wifiImg.src=wifiImg.src.slice(0,index)+"on.png";
			}
		} else {
			index=wifiImg.src.lastIndexOf("on");
			if(index!=-1){
				wifiImg.src=wifiImg.src.slice(0,index)+"off.png";
			}
		}
	}, function(){});
}

function mobileDataInit() {
	var index;
	var netImg=document.getElementById("state_mobiledata");
	plugins.MobileDataWizard.isMobileDataEnabled(function(res){
		if (res){
				index=netImg.src.lastIndexOf("off");
				if(index!=-1){
					netImg.src=netImg.src.slice(0,index)+"on.png";
				}
			} else {
				index=netImg.src.lastIndexOf("on");
				if(index!=-1){
					netImg.src=netImg.src.slice(0,index)+"off.png";
				}
			}
	},function(){});
}

function bluetoothInit() {
	var index;
    var blueImg=document.getElementById("state_bluetooth");
    plugins.BluetoothStatus.isBlueEnabled(function(res){
    	if (res){
                index=blueImg.src.lastIndexOf("off");
                if(index!=-1){
                    blueImg.src=blueImg.src.slice(0,index)+"on.png";
                }
        	} else {
        	    index=blueImg.src.lastIndexOf("on");
        	    if(index!=-1){
            	    blueImg.src=blueImg.src.slice(0,index)+"off.png";
            	}
        	}
    },function(){});
}


//开启wifi
function setWifiOn(){
    plugins.WifiWizard.setWifiEnabled(true,function(res){},function(){});
}

//关闭wifi
function setWifiOff(){
    plugins.WifiWizard.setWifiEnabled(false,function(res){},function(){});
}

//开启流量
function setMobileDataOn(){
    plugins.MobileDataWizard.setMobileDataEnabled(true);
}

//关闭流量
function setMobileDataOff(){
    plugins.MobileDataWizard.setMobileDataEnabled(false);
}

//开启蓝牙
function setBluetoothOn(){
    plugins.BluetoothStatus.enableBT();
}

//关闭蓝牙
function setBluetoothOff(){
    plugins.BluetoothStatus.disableBT();
}

//开启手电
function setLightOn(){
    plugins.flashlight.switchOn();
}

//关闭手电
function setLightOff(){
    plugins.flashlight.switchOff();
}


//改变wifi状态
function changeWifiState(event){
    var index;
    if (this.src.lastIndexOf("on")!=-1){
        index=this.src.lastIndexOf("on");
        this.src=this.src.slice(0,index)+"off.png";
        setWifiOff();
    }else {
        index=this.src.lastIndexOf("off");
        this.src=this.src.slice(0,index)+"on.png";
        setWifiOn();
    }
	event.stopPropagation();
}

//改变流量状态
function changeMobileDataState(event){
    var index;
    if (this.src.lastIndexOf("on")!=-1){
        setMobileDataOff();
        index=this.src.lastIndexOf("on");
        this.src=this.src.slice(0,index)+"off.png";
    } else if(this.src.lastIndexOf("off")!=-1){
        setMobileDataOn();
        index=this.src.lastIndexOf("off");
        this.src=this.src.slice(0,index)+"on.png";
    }
	event.stopPropagation();
}

//改变蓝牙状态
function changeBluetoothState(event){
    var index;

    if (this.src.lastIndexOf("on")!=-1){
        setBluetoothOff();
        index=this.src.lastIndexOf("on");
        this.src=this.src.slice(0,index)+"off.png";
    } else if(this.src.lastIndexOf("off")!=-1){
        setBluetoothOn();
        index=this.src.lastIndexOf("off");
        this.src=this.src.slice(0,index)+"on.png";
	}
	event.stopPropagation();
}

//改变手电状态
function changeFlashLightState(event) {
	var index;
	if (this.src.lastIndexOf("on")!=-1) {
		setLightOff();
		index=this.src.lastIndexOf("on");
		this.src=this.src.slice(0,index)+"off.png";
	} else if (this.src.lastIndexOf("off")!=-1){
		setLightOn();
		index=this.src.lastIndexOf("off");
		this.src=this.src.slice(0,index)+"on.png";
	}

	event.stopPropagation();
}

// 打开照相机
function openCamera(event){
	console.log("######## openCamera");
	navigator.camera.openCamera();
	event.stopPropagation();
}

