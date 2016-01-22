package com.coco.lock2.app.info;

import java.io.IOException;
import java.io.InputStream;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.coco.lock2.app.locktemplate.MediaHandle;
import com.coco.lock2.app.view.UnlockAnim;

import android.content.Context;
import android.util.Xml;

public class ParaseXml {

	private Context mcontext;
	private UnlockAnim unlockAnim;

	public ParaseXml(Context context) {
		mcontext = context;
	}

	/**
	 * 参数fileName：为xml文档路径
	 **/
	public Variables PullParseXML(String xmlPath) {
		try {
			XmlPullParser xmlPullParser = Xml.newPullParser();
			// 设置输入的xml文件
			InputStream inputStream = mcontext.getResources().getAssets()
					.open(xmlPath);
			xmlPullParser.setInput(inputStream, "UTF-8");
			// 开始
			int eventType = xmlPullParser.getEventType();
			try {
				int i = 0;
				while (eventType != XmlPullParser.END_DOCUMENT) {
					String nodeName = xmlPullParser.getName();
					switch (eventType) {
					// 文档开始
					case XmlPullParser.START_DOCUMENT:
						break;
					// 开始节点
					case XmlPullParser.START_TAG:
						// 判断节点
						if ("UnlockShock".equals(nodeName)) {
							MediaHandle.UnlockShock = Boolean
									.parseBoolean(xmlPullParser
											.getAttributeValue(null, "shock"));
						}
						if ("Background".equals(nodeName)) {
							Variables.mAlign = xmlPullParser.getAttributeValue(
									null, "align");
							Variables.systemWallpaper = xmlPullParser
									.getAttributeValue(null, "systemWallpaper")
									.equals("true");
						}
						if ("Time".equals(nodeName)) {
							Variables.Time_horizontal_align = xmlPullParser
									.getAttributeValue(null,
											"time_horizontal_align");
							Variables.Time_vertical_align = xmlPullParser
									.getAttributeValue(null,
											"time_vertical_align");
							Variables.Time_x = Integer.parseInt(xmlPullParser
									.getAttributeValue(null, "time_x"));
							Variables.Time_y = Integer.parseInt(xmlPullParser
									.getAttributeValue(null, "time_y"));
							Variables.Horizontal_align = xmlPullParser
									.getAttributeValue(null, "horizontal_align");
							Variables.Vertical_align = xmlPullParser
									.getAttributeValue(null, "vertical_align");
							Variables.Am_pm_x = Integer.parseInt(xmlPullParser
									.getAttributeValue(null, "x"));
							Variables.Am_pm_y = Integer.parseInt(xmlPullParser
									.getAttributeValue(null, "y"));
							Variables.Time_color = xmlPullParser
									.getAttributeValue(null, "time_color");
							Variables.Time_fontSize = Integer
									.parseInt(xmlPullParser.getAttributeValue(
											null, "time_fontSize"));
							Variables.Am_pm_color = xmlPullParser
									.getAttributeValue(null, "color");
							Variables.Am_pm_fontSize = Integer
									.parseInt(xmlPullParser.getAttributeValue(
											null, "fontSize"));
							Variables.Time_useImage = xmlPullParser
									.getAttributeValue(null, "useImage")
									.equals("true");
						}
						if ("Date".equals(nodeName)) {
							Variables.Date_horizontal_align = xmlPullParser
									.getAttributeValue(null, "horizontal_align");
							Variables.Date_vertical_align = xmlPullParser
									.getAttributeValue(null, "vertical_align");
							Variables.Date_dis = Integer.parseInt(xmlPullParser
									.getAttributeValue(null, "dis"));
							Variables.Date_x = Integer.parseInt(xmlPullParser
									.getAttributeValue(null, "x"));
							Variables.Date_y = Integer.parseInt(xmlPullParser
									.getAttributeValue(null, "y"));
							Variables.Date_color = xmlPullParser
									.getAttributeValue(null, "color");
							Variables.Date_fontSize = Integer
									.parseInt(xmlPullParser.getAttributeValue(
											null, "fontSize"));
							Variables.Data_useImage = xmlPullParser
									.getAttributeValue(null, "useImage")
									.equals("true");
						}
						if ("Text".equals(nodeName)) {
							Variables.Battery_horizontal_align = xmlPullParser
									.getAttributeValue(null, "horizontal_align");
							Variables.Battery_vertical_align = xmlPullParser
									.getAttributeValue(null, "vertical_align");
							Variables.Battery_x = Integer
									.parseInt(xmlPullParser.getAttributeValue(
											null, "x"));
							Variables.Battery_y = Integer
									.parseInt(xmlPullParser.getAttributeValue(
											null, "y"));
							Variables.Battery_color = xmlPullParser
									.getAttributeValue(null, "color");
							Variables.Battery_fontSize = Integer
									.parseInt(xmlPullParser.getAttributeValue(
											null, "fontSize"));
						}
						if ("Phone".equals(nodeName)) {
							Variables.Phone_color = xmlPullParser
									.getAttributeValue(null, "color");
							Variables.Phone_fontSize = Integer
									.parseInt(xmlPullParser.getAttributeValue(
											null, "fontSize"));
						}
						if ("Misscall".equals(nodeName)) {
							Variables.Call_image_x = Integer
									.parseInt(xmlPullParser.getAttributeValue(
											null, "x"));
							Variables.Call_image_y = Integer
									.parseInt(xmlPullParser.getAttributeValue(
											null, "y"));
							Variables.Call_vertical_align = xmlPullParser
									.getAttributeValue(null, "vertical_align");
						}
						if ("Unread".equals(nodeName)) {
							Variables.Message_image_x = Integer
									.parseInt(xmlPullParser.getAttributeValue(
											null, "x"));
							Variables.Message_image_y = Integer
									.parseInt(xmlPullParser.getAttributeValue(
											null, "y"));
							Variables.Message_vertical_align = xmlPullParser
									.getAttributeValue(null, "vertical_align");
						}
						if ("Unlocker".equals(nodeName)) {
							Variables.Unlock_style = Integer
									.parseInt(xmlPullParser.getAttributeValue(
											null, "style"));
							Variables.Unlock_enter = Boolean
									.parseBoolean(xmlPullParser
											.getAttributeValue(null, "enter"));
						}
						if ("NormalState".equals(nodeName)) {
							Variables.Image_state = "normal";
						}
						if ("Image".equals(nodeName)) {
							if (Variables.Image_state.equals("normal")) {
								Variables.Unlock_horizontal_align = xmlPullParser
										.getAttributeValue(null,
												"horizontal_align");
								Variables.Start_x = Integer
										.parseInt(xmlPullParser
												.getAttributeValue(null, "x"));
								Variables.Start_y = Integer
										.parseInt(xmlPullParser
												.getAttributeValue(null, "y"));
							} else if (Variables.Image_state.equals("frame")) {
								Variables.Anim_horizontal_align[i] = xmlPullParser
										.getAttributeValue(null,
												"horizontal_align");
								Variables.Anim_vertical_align[i] = xmlPullParser
										.getAttributeValue(null,
												"vertical_align");
								int millis = Integer
										.parseInt(xmlPullParser
												.getAttributeValue(null,
														"delayMillis"));
								if (millis == 0) {
									Variables.Anim_delayMillis[i] = 0;
								} else {
									Variables.Anim_delayMillis[i] = (1000 / millis);
								}
								Variables.Anim_type[i] = xmlPullParser
										.getAttributeValue(null, "type");
								Variables.Anim_name[i] = xmlPullParser
										.getAttributeValue(null, "src");
								Variables.Anim_x[i] = Integer
										.parseInt(xmlPullParser
												.getAttributeValue(null, "x"));
								Variables.Anim_y[i] = Integer
										.parseInt(xmlPullParser
												.getAttributeValue(null, "y"));
								Variables.Anim_frame_num[i] = Integer
										.parseInt(xmlPullParser
												.getAttributeValue(null, "num"));
								i++;
							}
						}
						if ("PressedState".equals(nodeName)) {

						}
						if ("LockAnim".equals(nodeName)) {
							unlockAnim = new UnlockAnim();
							unlockAnim.horizontal_align = xmlPullParser
									.getAttributeValue(null, "horizontal_align");
							unlockAnim.vertical_align = xmlPullParser
									.getAttributeValue(null, "vertical_align");
							int millis = Integer.parseInt(xmlPullParser
									.getAttributeValue(null, "delayMillis"));
							if (millis == 0) {
								unlockAnim.delayMillis = millis;
							} else {
								unlockAnim.delayMillis = (1000 / millis);
							}
							unlockAnim.x = Integer.parseInt(xmlPullParser
									.getAttributeValue(null, "x"));
							unlockAnim.y = Integer.parseInt(xmlPullParser
									.getAttributeValue(null, "y"));
							unlockAnim.name = xmlPullParser.getAttributeValue(
									null, "src");
							unlockAnim.frame_num = Integer
									.parseInt(xmlPullParser.getAttributeValue(
											null, "num"));
						}
						if ("Enter".equals(nodeName)) {
							Variables.Enter_num = Integer
									.parseInt(xmlPullParser.getAttributeValue(
											null, "num"));
							if (Variables.Enter_num > 0) {
								i = 0;
								Variables.Enter_name = new String[Variables.Enter_num];
								Variables.Enter_type = new String[Variables.Enter_num];
								Variables.Enter_ClassName = new String[Variables.Enter_num];
								Variables.Enter_PackageName = new String[Variables.Enter_num];
								Variables.Enter_x = new int[Variables.Enter_num];
								Variables.Enter_y = new int[Variables.Enter_num];
								Variables.Enter_dir = new String[Variables.Enter_num];
								Variables.Enter_unlockAnim = new UnlockAnim[Variables.Enter_num];
							}
						}
						if (("EnterImage".equals(nodeName))
								&& (Variables.Enter_num > 0)) {
							Variables.Enter_name[i] = xmlPullParser
									.getAttributeValue(null, "src");
							Variables.Enter_type[i] = xmlPullParser
									.getAttributeValue(null, "type");
							Variables.Enter_dir[i] = xmlPullParser
									.getAttributeValue(null, "dir");
							Variables.Enter_ClassName[i] = xmlPullParser
									.getAttributeValue(null, "className");
							Variables.Enter_PackageName[i] = xmlPullParser
									.getAttributeValue(null, "packageName");
							Variables.Enter_x[i] = Integer
									.parseInt(xmlPullParser.getAttributeValue(
											null, "x"));
							Variables.Enter_y[i] = Integer
									.parseInt(xmlPullParser.getAttributeValue(
											null, "y"));
						}
						if (("EnterLockAnim".equals(nodeName))
								&& (Variables.Enter_num > 0)) {
							Variables.Enter_unlockAnim[i] = new UnlockAnim();
							Variables.Enter_unlockAnim[i].horizontal_align = xmlPullParser
									.getAttributeValue(null, "horizontal_align");
							Variables.Enter_unlockAnim[i].vertical_align = xmlPullParser
									.getAttributeValue(null, "vertical_align");
							int millis = Integer.parseInt(xmlPullParser
									.getAttributeValue(null, "delayMillis"));
							if (millis == 0) {
								Variables.Enter_unlockAnim[i].delayMillis = 0;
							} else {
								Variables.Enter_unlockAnim[i].delayMillis = (1000 / millis);
							}
							Variables.Enter_unlockAnim[i].x = Integer
									.parseInt(xmlPullParser.getAttributeValue(
											null, "x"));
							Variables.Enter_unlockAnim[i].y = Integer
									.parseInt(xmlPullParser.getAttributeValue(
											null, "y"));
							Variables.Enter_unlockAnim[i].name = xmlPullParser
									.getAttributeValue(null, "src");
							Variables.Enter_unlockAnim[i].frame_num = Integer
									.parseInt(xmlPullParser.getAttributeValue(
											null, "num"));
							i++;
						}
						if ("FrameAnimation".equals(nodeName)) {
							Variables.Anim_num = Integer.parseInt(xmlPullParser
									.getAttributeValue(null, "num"));
							if (Variables.Anim_num > 0) {
								i = 0;
								Variables.Image_state = "frame";
								Variables.Anim_horizontal_align = new String[Variables.Anim_num];
								Variables.Anim_vertical_align = new String[Variables.Anim_num];
								Variables.Anim_delayMillis = new int[Variables.Anim_num];
								Variables.Anim_type = new String[Variables.Anim_num];
								Variables.Anim_name = new String[Variables.Anim_num];
								Variables.Anim_x = new int[Variables.Anim_num];
								Variables.Anim_y = new int[Variables.Anim_num];
								Variables.Anim_frame_num = new int[Variables.Anim_num];
							}
						}
						if ("EndPoint".equals(nodeName)) {
							Variables.End_dis = Integer.parseInt(xmlPullParser
									.getAttributeValue(null, "dis"));
						}
						if ("Seqencing".equals(nodeName)) {
							i = 0;
							Variables.Layer_num = Integer
									.parseInt(xmlPullParser.getAttributeValue(
											null, "num"));
							Variables.Layer_type = new String[Variables.Layer_num];
							Variables.Layer_name = new String[Variables.Layer_num];
						}
						if ("Layer".equals(nodeName)) {
							Variables.Layer_type[i] = xmlPullParser
									.getAttributeValue(null, "type");
							Variables.Layer_name[i] = xmlPullParser
									.getAttributeValue(null, "name");
							i++;
						}
						break;
					// 结束节点
					case XmlPullParser.END_TAG:
						break;
					default:
						break;
					}
					eventType = xmlPullParser.next();
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	public UnlockAnim getUnlockAnim() {
		return this.unlockAnim;
	}
}
