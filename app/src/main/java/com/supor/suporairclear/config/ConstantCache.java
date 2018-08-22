package com.supor.suporairclear.config;

import com.accloud.service.ACObject;
import com.rihuisoft.loginmode.utils.AppManager;
import com.supor.suporairclear.model.DeviceInfo;

import java.util.HashMap;
import java.util.List;

/**
 * Created by enyva on 16/6/7.
 */
public class ConstantCache {
    public static String location_province;
    public static String location_city;
    public static String
            location_district;
    public static Double location_longitude;
    public static Double location_latitude;

    public static List<ACObject> deviceList;
    public static HashMap<Long, String> deviceNameMap;
    public static HashMap<Long, DeviceInfo> deviceMap;

    public static String physicalDeviceId = "";
    public static String wifiPasswoed = "";
    
    public static AppManager appManager = new AppManager();
    
    public static short COMMEND_SN = 1;
    
    public static Long userTaskId_30 = 0l;
    public static Long userTaskId_10 = 0l;

    public static List<ACObject> deviceModeList;

    public static int subDomainId=Config.SUBDOMAINID;
    public static String bindModelName="";
    public static String bindModelPic="";
    public static ACObject weatherInfo;
    public static String subDomainName=Config.SUBMAJORDOMAIN;
    public static HashMap<Long, ACObject> deviceStatusList=new HashMap<Long, ACObject>();
    public static ACObject userProInfo;
}
