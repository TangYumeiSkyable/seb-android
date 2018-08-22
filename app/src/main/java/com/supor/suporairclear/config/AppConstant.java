package com.supor.suporairclear.config;

import android.graphics.Typeface;

import com.supor.suporairclear.application.MainApplication;

/**
 * Created by guoxd on 2016/5/4.
 */
public interface AppConstant {
	public static int REGISTER = 0;
	public static int RESETPASSWORD = 1;

	public static final int POPUP_ADD = 0;
	public static final int POPUP_MORE = 1;


	public static int MOD_STANDARD_VALUE = 40;
	public static int MOD_STRONG_VALUE = 71;
	public static int MODIFYPASSWORD = 30;
	public static int MODIFYNICKNAME = 31;
	

	public static  int FLG_SPACEADD = 0;
	public static  int FLG_SPACEUPDATE = 1;
	
	public static int ERR_OVERDUE_1 = 3514;
	public static int ERR_OVERDUE_2 = 3515;
	public static int ERR_OVERDUE_3 = 3812;
	public static int ERR_OVERDUE_4 = 3516;
	public static int ERR_OVERDUE_5 = 3014;
	public static int ERR_OVERDUE_6 = 3015;
	public static int TIME_OUT = 1993;
	
	
	public static double FILTER_HEPA_HOURS = 60000;
	public static double FILTER_NANO_HOURS = 4320;
	public static double FILTER_ASH_HOURS = 480;
	
	public static String DEVICE_TYPE = "Rowenta XL in EU";
	
	public static int TOAST_DURATION = 1000;
	public static Typeface ubuntuRegular = Typeface.createFromAsset(MainApplication.getInstance().getAssets(),"fonts/Ubuntu-R.ttf");
	public static Typeface ubuntuMedium = Typeface.createFromAsset(MainApplication.getInstance().getAssets(),"fonts/Ubuntu-M.ttf");
	public static Typeface ubuntuLight = Typeface.createFromAsset(MainApplication.getInstance().getAssets(),"fonts/Ubuntu-L.ttf");
	public static Typeface pfBold =Typeface.DEFAULT_BOLD;//Typeface.createFromAsset(MainApplication.getInstance().getAssets(),"fonts/PingFang Bold.ttf");
	public static Typeface notoscan =Typeface.MONOSPACE;//Typeface.createFromAsset(MainApplication.getInstance().getAssets(),"fonts/NotoSansHans-Regular.otf");

	public static int TVOC_A = 0;
	public static int TVOC_B = 1;
	public static int TVOC_C = 2;
	public static int PM_1 = 1;
	public static int PM_2 = 2;
	public static int PM_3 = 3;

	public static int PM_START = 2;
	public static int PM_A = 100;
	public static int PM_B = 200;

	public static int PM_MAX = 500;

	public static int DEVICE_XS = 5561;

	public static int AIR_1 = 20;
	public static int AIR_2 = 50;
	public static int AIR_3 = 100;
	public static int AIR_4 = 150;
	public static int AIR_5 = 200;
	public static int AIR_6 = 300;

	public static final String LOCATION = "location";
	public static final String LOCATION_ACTION = "locationAction";
	public static final int DCPSERVICE_TOKEN_INVALID_CODE = 8888;


	public static final String SP_DCPSERVICE_TOKEN_INVALID_KEY = "DCPTokenInvalid";
}
