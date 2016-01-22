package com.coco.lock2.app.info;

public class Variables {

	public static int Am_pm_x;
	public static int Am_pm_y;
	public static int[] Anim_delayMillis;
	public static int[] Anim_frame_num;
	public static String[] Anim_horizontal_align;
	public static String[] Anim_name;
	public static int Anim_num;
	public static String[] Anim_type;
	public static String[] Anim_vertical_align;
	public static int[] Anim_x;
	public static int[] Anim_y;
	public static String Battery_color;
	public static String Battery_horizontal_align;
	public static String Battery_vertical_align;
	public static int Battery_x;
	public static int Battery_y;
	public static int Call_image_x;
	public static int Call_image_y;
	public static String Call_vertical_align;
	public static int Date_dis;
	public static String Date_horizontal_align;
	public static String Date_vertical_align;
	public static int Date_x;
	public static int Date_y;
	public static int End_dis;
	public static String Horizontal_align;
	public static String Image_state;
	public static String[] Layer_name;
	public static int Layer_num;
	public static String[] Layer_type;
	public static int Message_image_x;
	public static int Message_image_y;
	public static String Message_vertical_align;
	public static String Phone_color;
	public static int Start_x;
	public static int Start_y;
	public static String Time_horizontal_align;
	public static String Time_vertical_align;
	public static int Time_x;
	public static int Time_y;
	public static boolean Unlock_enter;
	public static String Unlock_horizontal_align;
	public static int Unlock_style;
	public static String Vertical_align;
	public static boolean isbuiltin = true;
	public static String mAlign;
	public static int screen_height;
	public static int screen_height_scale;
	public static int screen_width;
	public static int screen_width_scale;

	static {
		Unlock_enter = false;
		Image_state = "";
	}

	public static int getAnimType(int paramInt) {
		if (Anim_type[paramInt].charAt(0) != '2') {
			return Anim_type[paramInt].charAt(0) - '0';
		} else {
			return new Integer(Anim_type[paramInt]);
		}
	}

	public static float getScreenScaleX() {
		return 1.0F * screen_width / 720.0F;
	}

	public static float getScreenScaleY() {
		return 1.0F * screen_height / 1280.0F;
	}
}
