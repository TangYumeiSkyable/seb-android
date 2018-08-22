package com.supor.suporairclear.controller;

import com.accloud.cloudservice.PayloadCallback;
import com.accloud.cloudservice.VoidCallback;
import com.accloud.service.ACException;
import com.accloud.service.ACMsg;
import com.accloud.service.ACObject;
import com.rihuisoft.loginmode.utils.AppUtils;
import com.supor.suporairclear.config.Config;
import com.supor.suporairclear.config.ConstantCache;
import com.supor.suporairclear.config.DCPServiceUtils;
import com.supor.suporairclear.model.CommendInfo;

import java.util.List;

public class ACMsgHelper {

    /**
     * To achieve equipment information
     * @param deviceId
     * @result deviceDetails
     */
    public void queryDeviceInfo(long deviceId,final PayloadCallback<ACObject> callBack){
        try {
            ACMsg req = ACMsg.getACMsg(Config.SUBMAJORDOMAIN);
            req.setName("queryDeviceInfo");
            req.put("deviceId",deviceId);
            req.put("dcpToken", DCPServiceUtils.getDcpToken());

            DCPServiceUtils.sendToDCPService(req, new PayloadCallback<ACMsg>() {
            //AC.sendToService(Config.SUBMAJORDOMAIN, Config.MYSERVICENAME, 1, req, new PayloadCallback<ACMsg>() {
                @Override
                public void success(ACMsg acMsg) {
                    ACObject deviceDetails = acMsg.get("actionData");
                    callBack.success(deviceDetails);
                }

                @Override
                public void error(ACException e) {
                    e.printStackTrace();
                    callBack.error(e);
                }
            });
        } catch(Exception ee) {
            ee.printStackTrace();
        }

    }

    /**
     * Access to outlets
     * @result ServiceAddressList
     */
    public void queryServicePointInfo(String province,String city,String district,final PayloadCallback<List<ACObject>> callBack){
        try {
            ACMsg req = ACMsg.getACMsg(Config.SUBMAJORDOMAIN);
            req.setName("queryServicePointInfo");
            req.put("province",province);
            req.put("city",city);
            req.put("District",district);
            req.put("dcpToken", DCPServiceUtils.getDcpToken());
            DCPServiceUtils.sendToDCPService(req, new PayloadCallback<ACMsg>() {
            //AC.sendToService(Config.SUBMAJORDOMAIN, Config.MYSERVICENAME, 1, req, new PayloadCallback<ACMsg>() {
                @Override
                public void success(ACMsg acMsg) {
                    List<ACObject> deviceDetails = acMsg.getList("actionData");
                    callBack.success(deviceDetails);
                }

                @Override
                public void error(ACException e) {
                    e.printStackTrace();
                    callBack.error(e);
                }
            });
        } catch(Exception ee) {
            ee.printStackTrace();
        }

    }

    /**
     * Access to the message list
     * @result MeassageList
     */
    public void queryMessageList(final PayloadCallback<List<ACObject>> callBack){
        try {
            ACMsg req = ACMsg.getACMsg(Config.SUBMAJORDOMAIN);
            req.setName("queryMessageInfoList");
            req.put("dcpToken", DCPServiceUtils.getDcpToken());
            DCPServiceUtils.sendToDCPService(req, new PayloadCallback<ACMsg>() {
            //AC.sendToService(Config.SUBMAJORDOMAIN, Config.MYSERVICENAME, 1, req, new PayloadCallback<ACMsg>() {
                @Override
                public void success(ACMsg acMsg) {
                    List<ACObject> messageList = acMsg.getList("actionData");
                    callBack.success(messageList);
                }

                @Override
                public void error(ACException e) {
                    e.printStackTrace();
                    callBack.error(e);
                }
            });
        } catch(Exception ee) {
            ee.printStackTrace();
        }

    }

    /**
     * Access to the message list
     * @result MeassageList
     */
    public void queryMessage(final String messageId, final PayloadCallback<ACObject> callBack){
        try {
            ACMsg req = ACMsg.getACMsg(Config.SUBMAJORDOMAIN);
            req.setName("queryMessageInfo");
            req.put("messageId", messageId);
            req.put("dcpToken", DCPServiceUtils.getDcpToken());
            DCPServiceUtils.sendToDCPService(req, new PayloadCallback<ACMsg>() {
                @Override
                public void success(ACMsg acMsg) {
                    ACObject message = acMsg.get("actionData");
                    callBack.success(message);
                }

                @Override
                public void error(ACException e) {
                    e.printStackTrace();
                    callBack.error(e);
                }
            });
        } catch(Exception ee) {
            ee.printStackTrace();
        }

    }

    /**
     * Modify equipment city
     * @param deviceId
     * @result deviceDetails
     */
    public void changeDeviceCity(long deviceId,String city,final VoidCallback callBack){
        try {
            ACMsg req = ACMsg.getACMsg(Config.SUBMAJORDOMAIN);
            req.setName("updateDeviceCity");
            req.put("deviceId",deviceId);
            req.put("city",city);
            req.put("dcpToken", DCPServiceUtils.getDcpToken());
            DCPServiceUtils.sendToDCPService(req, new PayloadCallback<ACMsg>() {
                @Override
                public void success(ACMsg arg) {
                    callBack.success();
                }

                @Override
                public void error(ACException e) {
                    e.printStackTrace();
                    callBack.error(e);
                }
            });
        } catch(Exception ee) {
            ee.printStackTrace();
        }

    }

    /**
     * Modify the equipment working mode
     * @param deviceList
     * @param workMode
     * @result deviceDetails
     */
    public void updateWorkMode(List<ACObject> deviceList,String workMode,final VoidCallback callBack){
        try {
            ACMsg req = ACMsg.getACMsg(Config.SUBMAJORDOMAIN);
            req.setName("updateDeviceWorkmode");
            req.put("deviceList",deviceList);
            req.put("work_mode",workMode);
            req.put("dcpToken", DCPServiceUtils.getDcpToken());
            DCPServiceUtils.sendToDCPService(req, new PayloadCallback<ACMsg>() {
            //AC.sendToService(Config.SUBMAJORDOMAIN, Config.MYSERVICENAME, 1, req, new PayloadCallback<ACMsg>() {
                @Override
                public void success(ACMsg arg) {
                    callBack.success();
                }

                @Override
                public void error(ACException e) {
                    callBack.error(e);
                }
            });
        } catch(Exception ee) {
            ee.printStackTrace();
        }

    }

    /**
     * Modify the equipment working mode
     * @param deviceList
     * @result deviceDetails
     */
    public void queryAllFilterStatus(List<ACObject> deviceList,final PayloadCallback<List<ACObject>> callBack){
        try {
            ACMsg req = ACMsg.getACMsg(Config.SUBMAJORDOMAIN);
            req.setName("queryAllFilterStatus");
            req.put("deviceList",deviceList);
            req.put("dcpToken", DCPServiceUtils.getDcpToken());
            DCPServiceUtils.sendToDCPService(req, new PayloadCallback<ACMsg>() {
            //AC.sendToService(Config.SUBMAJORDOMAIN, Config.MYSERVICENAME, 1, req, new PayloadCallback<ACMsg>() {
                @Override
                public void success(ACMsg arg) {
                    List<ACObject> filterList = arg.getList("actionData");
                    callBack.success(filterList);
                }

                @Override
                public void error(ACException e) {
                    callBack.error(e);
                }
            });
        } catch(Exception ee) {
            ee.printStackTrace();
        }

    }

    /**
     * Get all the equipment regularly
     * @param deviceList
     * @result deviceDetails
     */
    public void queryAllDeviceTimer(List<ACObject> deviceList,final PayloadCallback<List<ACObject>> callBack){
        try {
            ACMsg req = ACMsg.getACMsg(Config.SUBMAJORDOMAIN);
            req.setName("queryAllDeviceTimer");
            req.put("deviceList",deviceList);
            req.put("dcpToken", DCPServiceUtils.getDcpToken());
            DCPServiceUtils.sendToDCPService(req, new PayloadCallback<ACMsg>() {
            //AC.sendToService(Config.SUBMAJORDOMAIN, Config.MYSERVICENAME, 1, req, new PayloadCallback<ACMsg>() {
                @Override
                public void success(ACMsg arg) {
                    List<ACObject> filterList = arg.getList("actionData");
                    callBack.success(filterList);
                }

                @Override
                public void error(ACException e) {
                    callBack.error(e);
                }
            });
        } catch(Exception ee) {
            ee.printStackTrace();
        }

    }

    /**
     * Modify the message read the logo
     * @param messageId
     * @result deviceDetails
     */
    public void updateMessageInfo(String messageId,final VoidCallback callBack){
        try {
            ACMsg req = ACMsg.getACMsg(Config.SUBMAJORDOMAIN);
            req.setName("updateMessageInfo");
            req.put("messageId", messageId);
            req.put("dcpToken", DCPServiceUtils.getDcpToken());
            DCPServiceUtils.sendToDCPService(req, new PayloadCallback<ACMsg>() {
            //AC.sendToService(Config.SUBMAJORDOMAIN, Config.MYSERVICENAME, 1, req, new PayloadCallback<ACMsg>() {
                @Override
                public void success(ACMsg arg) {
                    callBack.success();
                }

                @Override
                public void error(ACException e) {
                    callBack.error(e);
                }
            });
        } catch(Exception ee) {
            ee.printStackTrace();
        }

    }
    /**
     * For more information on the page
     * @param deviceList
     * @result moreInfo
     */
    public void queryMoreInfo(List<ACObject> deviceList, final PayloadCallback<ACObject> callBack){
        try {
            ACMsg req = ACMsg.getACMsg(Config.SUBMAJORDOMAIN);
            req.setName("queryMoreInfo");
            req.put("deviceList",deviceList);
            req.put("dcpToken", DCPServiceUtils.getDcpToken());
            DCPServiceUtils.sendToDCPService(req, new PayloadCallback<ACMsg>() {
            //AC.sendToService(Config.SUBMAJORDOMAIN, Config.MYSERVICENAME, 1, req, new PayloadCallback<ACMsg>() {
                @Override
                public void success(ACMsg arg) {
                    ACObject moreInfo = arg.get("actionData");
                    callBack.success(moreInfo);
                }

                @Override
                public void error(ACException e) {
                    callBack.error(e);
                }
            });
        } catch(Exception ee) {
            ee.printStackTrace();
        }

    }

    /**
     * Device control
     * @param commend
     * @result success error
     */
    public void controlDevice(CommendInfo commend, final VoidCallback callBack){
        try {
            ACMsg req = ACMsg.getACMsg(Config.SUBMAJORDOMAIN);
            req.setName("controlDeviceInfo");
            req.put("deviceId",commend.getDeviceId());
            req.put("subDomainName", ConstantCache.deviceMap.get(commend.getDeviceId()).getSubDomainName());
            req.put("commend",commend.getCommend());
            req.put("value",commend.getValue());
            req.put("sn", (long)AppUtils.getCommendSN());
            req.put("dcpToken", DCPServiceUtils.getDcpToken());
            DCPServiceUtils.sendToDCPService(req, new PayloadCallback<ACMsg>() {
            //AC.sendToService(Config.SUBMAJORDOMAIN, Config.MYSERVICENAME, 1, req, new PayloadCallback<ACMsg>() {
                @Override
                public void success(ACMsg arg) {
                    callBack.success();
                }

                @Override
                public void error(ACException e) {
                    callBack.error(e);
                }
            });
        } catch(Exception ee) {
            ee.printStackTrace();
        }

    }

    /**
     * Device control
     * @param commend
     * @result success error
     */
    public void controlDevice(String subDomainName,CommendInfo commend, final VoidCallback callBack){
        try {
            ACMsg req = ACMsg.getACMsg(subDomainName);
            req.setName("controlDeviceInfo");
            req.put("deviceId",commend.getDeviceId());
            req.put("commend",commend.getCommend());
            req.put("value",commend.getValue());
            req.put("sn", (long)AppUtils.getCommendSN());
            req.put("dcpToken", DCPServiceUtils.getDcpToken());
            DCPServiceUtils.sendToDCPService(req, new PayloadCallback<ACMsg>() {
                //AC.sendToService(Config.SUBMAJORDOMAIN, Config.MYSERVICENAME, 1, req, new PayloadCallback<ACMsg>() {
                @Override
                public void success(ACMsg arg) {
                    callBack.success();
                }

                @Override
                public void error(ACException e) {
                    callBack.error(e);
                }
            });
        } catch(Exception ee) {
            ee.printStackTrace();
        }

    }

    public void deleteMessageForUnbind(List<ACObject> userIdList, long deviceId, final VoidCallback callBack) {
        try {
            ACMsg req = ACMsg.getACMsg(Config.SUBMAJORDOMAIN);
            req.setName("deleteMessageForUnbind");
            req.put("userIdList", userIdList);
            req.put("deviceId", deviceId);
            req.put("dcpToken", DCPServiceUtils.getDcpToken());
            DCPServiceUtils.sendToDCPService(req, new PayloadCallback<ACMsg>() {
            //AC.sendToService(Config.SUBMAJORDOMAIN, Config.MYSERVICENAME, 1, req, new PayloadCallback<ACMsg>() {
                @Override
                public void success(ACMsg arg) {
                    callBack.success();
                }

                @Override
                public void error(ACException e) {
                    callBack.error(e);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * To obtain a list countries
     * @result countryList
     */
    public void queryCountryList(final PayloadCallback<List<ACObject>> callBack){
        try {
            ACMsg req = ACMsg.getACMsg(Config.SUBMAJORDOMAIN);
            req.setName("queryCountryList");
            req.put("dcpToken", DCPServiceUtils.getDcpToken());
            DCPServiceUtils.sendToDCPService(req, new PayloadCallback<ACMsg>() {
            //AC.sendToService(Config.SUBMAJORDOMAIN, Config.MYSERVICENAME, 1, req, new PayloadCallback<ACMsg>() {
                @Override
                public void success(ACMsg acMsg) {
                    List<ACObject> data = acMsg.getList("actionData");
                    callBack.success(data);
                }

                @Override
                public void error(ACException e) {
                    e.printStackTrace();
                    callBack.error(e);
                }
            });
        } catch(Exception ee) {
            ee.printStackTrace();
        }

    }
    /**
     * Access to the city list
     * @result cityList
     */
    public void queryCityList(String country, final PayloadCallback<List<ACObject>> callBack){
        try {
            ACMsg req = ACMsg.getACMsg(Config.SUBMAJORDOMAIN);
            req.setName("queryCityList");
            req.put("country", country);
            req.put("dcpToken", DCPServiceUtils.getDcpToken());
            DCPServiceUtils.sendToDCPService(req, new PayloadCallback<ACMsg>() {
            //AC.sendToService(Config.SUBMAJORDOMAIN, Config.MYSERVICENAME, 1, req, new PayloadCallback<ACMsg>() {
                @Override
                public void success(ACMsg acMsg) {
                    List<ACObject> data = acMsg.getList("actionData");
                    callBack.success(data);
                }

                @Override
                public void error(ACException e) {
                    e.printStackTrace();
                    callBack.error(e);
                }
            });
        } catch(Exception ee) {
            ee.printStackTrace();
        }

    }
    /**
     * Get the weather information
     * @result cityList
     */
    public void queryWeather(String city, double latitude, double longitude, final PayloadCallback<ACObject> callBack){
        try {
            ACMsg req = ACMsg.getACMsg(Config.SUBMAJORDOMAIN);
            req.setName("queryWeather");
            req.put("city", city);
            req.put("latitude", latitude);
            req.put("longitude", longitude);
            req.put("dcpToken", DCPServiceUtils.getDcpToken());
            DCPServiceUtils.sendToDCPService(req, new PayloadCallback<ACMsg>() {
            //AC.sendToService(Config.SUBMAJORDOMAIN, Config.MYSERVICENAME, 1, req, new PayloadCallback<ACMsg>() {
                @Override
                public void success(ACMsg acMsg) {
                    ACObject data = acMsg.get("actionData");
                    callBack.success(data);
                }

                @Override
                public void error(ACException e) {
                    e.printStackTrace();
                    callBack.error(e);
                }
            });
        } catch(Exception ee) {
            ee.printStackTrace();
        }

    }
}
