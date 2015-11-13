package com.iLoong.launcher.MList;

import java.lang.reflect.Field;

import android.content.Context;
import android.content.res.Resources;

public class MyR {

	static boolean InflateMyR(Context context, Class<?> c, Object obj,
			String defType) {
		try {
			Resources resourse = context.getResources();
			Field[] fields = c.getFields();
			for (Field f : fields) {
				String name = f.getName();
				int id = resourse.getIdentifier(name, defType,
						context.getPackageName());
				if (id == 0) {
					MELOG.v("ME_RTFSC", "name:" + name);
					return false;
				}
				f.set(obj, id);
			}
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			MELOG.v("ME_RTFSC", "Error:" + e.toString());
			e.printStackTrace();
			return false;
		}
	}

	static MyR R;

	public static MyR getMyR(Context context) {
		synchronized (MyR.class) {
			if (R == null) {
				MyR R = new MyR();
				if (!InflateMyR(context, color_t.class, R.color, "color")) {
					MELOG.v("ME_RTFSC", "  color_t.class  null");
					return null;
				}
				if (!InflateMyR(context, dimen_t.class, R.dimen, "dimen")) {
					MELOG.v("ME_RTFSC", "  dimen_t.class  null");
					return null;
				}
				if (!InflateMyR(context, drawable_t.class, R.drawable,
						"drawable")) {
					MELOG.v("ME_RTFSC", "  drawable_t.class  null");
					return null;
				}
				if (!InflateMyR(context, id_t.class, R.id, "id")) {
					MELOG.v("ME_RTFSC", "  id_t.class  null");
					return null;
				}
				if (!InflateMyR(context, layout_t.class, R.layout, "layout")) {
					MELOG.v("ME_RTFSC", "  layout_t.class  null");
					return null;
				}
				if (!InflateMyR(context, string_t.class, R.string, "string")) {
					MELOG.v("ME_RTFSC", "  string_t.class  null");
					return null;
				}
				MyR.R = R;
			}
		}
		return R;
	}

	public class color_t {

		public int cool_ml_blue_total_tab_top_line1 = 0x7f060001;
		public int cool_ml_page_header_back_color = 0x7f060003;
		public int cool_ml_page_header_bottom_line = 0x7f060004;
		public int cool_ml_white_skin_title_color = 0x7f060002;
	}

	public color_t color = new color_t();

	public class dimen_t {

		public int cool_ml_ToolBar_height = 0x7f070000;
		public int cool_ml_page_header_back_padding = 0x7f070002;
		public int cool_ml_page_header_back_top_padding = 0x7f070003;
		public int cool_ml_page_header_back_width = 0x7f070001;
		public int cool_ml_textsize_b = 0x7f070004;
	}

	public dimen_t dimen = new dimen_t();

	public class drawable_t {

		public int cool_ml_alert_list_bg = 0x7f020004;
		public int cool_ml_bg_item_bottom_normal = 0x7f020005;
		public int cool_ml_bg_item_top_normal = 0x7f020006;
		public int cool_ml_bottom_pressed = 0x7f020007;
		public int cool_ml_corner_list_bg = 0x7f020008;
		public int cool_ml_cutline = 0x7f020009;
		public int cool_ml_discalmer_bg = 0x7f02000a;
		public int cool_ml_discalmer_del = 0x7f02000b;
		public int cool_ml_discalmer_icon = 0x7f02000c;
		public int cool_ml_divider = 0x7f02000d;
		public int cool_ml_download_install = 0x7f02000e;
		public int cool_ml_icon_btn_list_download = 0x7f02000f;
		public int cool_ml_icon_btn_list_download_pause = 0x7f020010;
		public int cool_ml_icon_btn_list_install = 0x7f020011;
		public int cool_ml_icon_btn_list_pause = 0x7f020012;
		public int cool_ml_icon_btn_list_run = 0x7f020013;
		public int cool_ml_icon_btn_list_waiting_download = 0x7f020014;
		public int cool_ml_icon_listview_bg = 0x7f020015;
		public int cool_ml_know = 0x7f020016;
		public int cool_ml_know_small = 0x7f020017;
		public int cool_ml_ku_store = 0x7f020018;
		public int cool_ml_ku_store_small = 0x7f020019;
		public int cool_ml_list_item_color = 0x7f02004c;
		public int cool_ml_no_data = 0x7f02001a;
		public int cool_ml_normal_list_selector = 0x7f02001b;
		public int cool_ml_notify = 0x7f02001c;
		public int cool_ml_notify_small = 0x7f02001d;
		public int cool_ml_page_header_back_press = 0x7f02001e;
		public int cool_ml_pageselect_button_underline = 0x7f02001f;
		public int cool_ml_progress_bg = 0x7f020020;
		public int cool_ml_software = 0x7f020021;
		public int cool_ml_software_small = 0x7f020022;
		public int cool_ml_toolbar_download_btn = 0x7f020023;
		public int cool_ml_toolbar_downloading_btn = 0x7f020024;
		public int cool_ml_toolbar_downloading_btn_pressed = 0x7f020025;
		public int cool_ml_toolbar_search_btn_pressed = 0x7f020026;
		public int cool_ml_underline_press = 0x7f020027;
		public int cool_ml_underline_unpress = 0x7f020028;
		public int cool_ml_webview_loading_press = 0x7f020029;
		public int cool_ml_white_skin_top_line_img = 0x7f02002a;
		public int cool_ml_white_total_tab_bg = 0x7f02002b;
		public int cool_ml_wonderful_game = 0x7f02002c;
		public int cool_ml_wonderful_game_small = 0x7f02002d;
		public int cool_ml_you_may_love = 0x7f02002e;
		public int cool_ml_you_may_love_small = 0x7f02002f;
	}

	public drawable_t drawable = new drawable_t();

	public class id_t {

		public int cool_ml_InstallListTextView1 = 0x7f0b0030;
		public int cool_ml_InstallListTextView2 = 0x7f0b0031;
		public int cool_ml_appListTextView1 = 0x7f0b002e;
		public int cool_ml_appListTextView2 = 0x7f0b002f;
		public int cool_ml_back_text = 0x7f0b0035;
		public int cool_ml_discalmer_del = 0x7f0b001c;
		public int cool_ml_disclaimer_update = 0x7f0b001d;
		public int cool_ml_flDownloadList = 0x7f0b0014;
		public int cool_ml_flDownloadNULLData = 0x7f0b0013;
		public int cool_ml_flInsatllList = 0x7f0b0017;
		public int cool_ml_flInsatllNULLData = 0x7f0b0016;
		public int cool_ml_header_nav_layout_bottom_line = 0x7f0b003b;
		public int cool_ml_ivProcessImg = 0x7f0b003c;
		public int cool_ml_linearLayout1 = 0x7f0b000e;
		public int cool_ml_liner01 = 0x7f0b0021;
		public int cool_ml_lvDownload = 0x7f0b0015;
		public int cool_ml_lvInstall = 0x7f0b0018;
		public int cool_ml_mainwebviewframe = 0x7f0b000f;
		public int cool_ml_manager_appIco = 0x7f0b0023;
		public int cool_ml_manager_appIco1 = 0x7f0b0029;
		public int cool_ml_manager_appName = 0x7f0b0025;
		public int cool_ml_manager_appName1 = 0x7f0b002a;
		public int cool_ml_manager_appSize = 0x7f0b0027;
		public int cool_ml_manager_appSize1 = 0x7f0b002c;
		public int cool_ml_manager_appVersion1 = 0x7f0b002b;
		public int cool_ml_manager_button = 0x7f0b0028;
		public int cool_ml_manager_button1 = 0x7f0b002d;
		public int cool_ml_manager_progressBar = 0x7f0b0026;
		public int cool_ml_nav_layout_top_img = 0x7f0b0034;
		public int cool_ml_nav_layout_top_line1 = 0x7f0b0033;
		public int cool_ml_notification_image = 0x7f0b001e;
		public int cool_ml_notification_text = 0x7f0b0020;
		public int cool_ml_notification_title = 0x7f0b001f;
		public int cool_ml_page_header = 0x7f0b0032;
		public int cool_ml_rbutDownloadPage = 0x7f0b0019;
		public int cool_ml_rbutInstallPage = 0x7f0b001a;
		public int cool_ml_rela01 = 0x7f0b0022;
		public int cool_ml_rela02 = 0x7f0b0024;
		public int cool_ml_subwebviewframe = 0x7f0b0011;
		public int cool_ml_themeGridPager = 0x7f0b001b;
		public int cool_ml_title_text = 0x7f0b0036;
		public int cool_ml_toolbar_downcount = 0x7f0b0039;
		public int cool_ml_toolbar_download_Layout = 0x7f0b0037;
		public int cool_ml_toolbar_download_img = 0x7f0b0038;
		public int cool_ml_toolbar_search_img = 0x7f0b003a;
		public int cool_ml_webView1 = 0x7f0b0010;
		public int cool_ml_webView2 = 0x7f0b0012;
	}

	public id_t id = new id_t();

	public class layout_t {

		public int cool_ml_activity_main = 0x7f030004;
		public int cool_ml_apk_download_view = 0x7f030005;
		public int cool_ml_apk_install_view = 0x7f030006;
		public int cool_ml_apk_manager = 0x7f030007;
		public int cool_ml_disclaimer_dialog = 0x7f030008;
		public int cool_ml_dwonload_notification = 0x7f030009;
		public int cool_ml_manager_download_listview = 0x7f03000a;
		public int cool_ml_manager_install_listview = 0x7f03000b;
		public int cool_ml_onlongclick_listview = 0x7f03000c;
		public int cool_ml_onlongclick_listview_install = 0x7f03000d;
		public int cool_ml_page_header = 0x7f03000e;
		public int cool_ml_webview_loading_dlg = 0x7f03000f;
	}

	public layout_t layout = new layout_t();

	public class string_t {

		public int cool_ml_MeIcon_cannot_uninstall = 0x7f080023;
		public int cool_ml_MeIcon_uninstall = 0x7f080021;
		public int cool_ml_MeIcon_uninstalling = 0x7f080022;
		/**
		 * ME_RTFSC
		 */
		public int cool_ml_app_name1 = 0x7f080000;
		public int cool_ml_app_name2 = 0x7f080001;
		public int cool_ml_app_name3 = 0x7f080002;
		public int cool_ml_app_name4 = 0x7f080003;
		public int cool_ml_confirm_canel = 0x7f080015;
		public int cool_ml_confirm_content = 0x7f080013;
		public int cool_ml_confirm_ok = 0x7f080014;
		public int cool_ml_cooee_download_jixu = 0x7f080027;
		public int cool_ml_cooee_download_quxiao = 0x7f080028;
		public int cool_ml_cooee_install_jixu = 0x7f080029;
		public int cool_ml_cooee_install_quxiao = 0x7f08002a;
		public int cool_ml_disclaimer_desc = 0x7f080025;
		public int cool_ml_disclaimer_title = 0x7f080024;
		public int cool_ml_disclaimer_update = 0x7f080026;
		public int cool_ml_dl_failed = 0x7f08001c;
		public int cool_ml_dl_failed_text = 0x7f08001d;
		public int cool_ml_dl_ing = 0x7f080016;
		public int cool_ml_dl_ing_text = 0x7f080017;
		public int cool_ml_dl_installed = 0x7f08001e;
		public int cool_ml_dl_installed_text = 0x7f08001f;
		public int cool_ml_dl_stop = 0x7f080018;
		public int cool_ml_dl_stop_text = 0x7f080019;
		public int cool_ml_dl_sucess = 0x7f08001a;
		public int cool_ml_dl_sucess_text = 0x7f08001b;
		public int cool_ml_donwload_manager = 0x7f080008;
		public int cool_ml_donwloadorinstall_manager = 0x7f080006;
		public int cool_ml_download_failed = 0x7f08000a;
		public int cool_ml_download_jixu = 0x7f08000d;
		public int cool_ml_download_quxiao = 0x7f08000e;
		public int cool_ml_dummy_button = 0x7f080004;
		public int cool_ml_dummy_content = 0x7f080005;
		public int cool_ml_install_file_not_exsit = 0x7f080020;
		public int cool_ml_install_jixu = 0x7f08000f;
		public int cool_ml_install_manager = 0x7f080007;
		public int cool_ml_install_quxiao = 0x7f080010;
		public int cool_ml_loading_1 = 0x7f080009;
		public int cool_ml_more_content = 0x7f080012;
		public int cool_ml_network_not_available = 0x7f08000b;
		public int cool_ml_new_content = 0x7f080011;
		public int cool_ml_storage_not_available = 0x7f08000c;
	}

	public string_t string = new string_t();
}
