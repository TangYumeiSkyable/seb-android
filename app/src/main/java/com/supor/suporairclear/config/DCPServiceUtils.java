package com.supor.suporairclear.config;

import android.text.TextUtils;
import android.util.Log;

import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.common.ACConfiguration;
import com.accloud.service.ACException;
import com.accloud.service.ACMsg;
import com.accloud.service.ACUserInfo;
import com.accloud.utils.PreferencesUtils;
import com.google.gson.Gson;
import com.supor.suporairclear.application.MainApplication;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Created by liuxiaofeng on 25/04/2017.
 */

public class DCPServiceUtils {

    //UDS
    private static final String SUBDOMAIN_NAME = "rowentaxl";
    private static final String SERVICE_NAME = "SEBService";
    private static final int SERVICE_VERSION = 1;
    private static final String DCP_SERVICE_PATH_BASE = "dcp-";
    private static final String DCP_SERVICE_PATH_LOGIN = "login";
    private static final String DCP_SERVICE_PATH_RESET_PASSWORD = "resetPassword";
    private static final String DCP_SERVICE_PATH_UPDATE_PASSWORD = "updatePassword";
    private static final String DCP_SERVICE_PATH_REGISTER = "register";
    private static final String DCP_SERVICE_PATH_GET_PROFILE = "getProfile";
    private static final String DCP_SERVICE_PATH_UPDATE_PROFILE = "updateProfile";
    private static final String DCP_SERVICE_PATH_CHECK_TOKEN = "checkToken";
    private static final String DCP_SERVICE_PATH_LOGOUT = "logout";
    private static final String DCP_SERVICE_PATH_SYNC_CONTENT = "syncContent";

    //token
    private static final String DCP_SERVICE_PARAM_TOKEN_NAME = "dcpToken";
    private static final String DCP_SERVICE_DCP_UID = "dcpUid";
    private static final String DCPServiceParamMarketName = "dcpMarket";
    private static final String DCPServiceDCPLanguageName = "kDCPServiceDCPLanguageName";
    private static final String DCP_SERVICE_DCP_TOKEN_NAME = "kDCPServiceDCPTokenName";
    private static final String DCPServiceDCPMarketName = "kDCPServiceDCPMarketName";

    //login
    private static final String KEY_USER_NAME = "loginName";
    private static final String KEY_PASSWORD = "password";

    //register
    private static final String KEY_NICK_NAME = "nickName";
    private static final String KEY_VERIFY_CODE = "verifyCode";

    //resetPassword
    private static final String KEY_RESET = "reset";

    //checkToken
    private static final String KEY_TOKEN_VALID = "valid";
    private static final int DCP_SERVICE_TOKEN_INVALID_CODE = 8888;
    private static final int DCP_SERVICE_TOKEN_EMPTY_CODE = 9999;

    //callback
    private static DCPTokenInvalidCallback mDcpTokenInvalidCallback;

    public static void login(String userName, String password, final PayloadCallback<ACUserInfo> callback) {
        ACMsg request = new ACMsg();
        request.setName(reqName(DCP_SERVICE_PATH_LOGIN));
        request.put(KEY_USER_NAME, userName);
        request.put(KEY_PASSWORD, password);
        sendToServiceForLogin(request, callback);
    }

    public static void register(String nickName, String email, String password, String verCode, final PayloadCallback<ACUserInfo> callback) {
        ACMsg request = new ACMsg();
        request.setName(reqName(DCP_SERVICE_PATH_REGISTER));
        request.put(KEY_USER_NAME, email);
        request.put(KEY_PASSWORD, password);
        request.put(KEY_NICK_NAME, nickName);
        request.put(KEY_VERIFY_CODE, verCode);
        sendToServiceForLogin(request, callback);
    }

    public static void resetPassword(String userName, final PayloadCallback<Boolean> callback) {
        ACMsg request = new ACMsg();
        request.setName(reqName(DCP_SERVICE_PATH_RESET_PASSWORD));
        request.put(KEY_USER_NAME, userName);
        sendToDCPServiceWithoutSign(request, new PayloadCallback<ACMsg>() {
            @Override
            public void success(ACMsg acMsg) {
                callback.success(acMsg.getBoolean(KEY_RESET));
            }

            @Override
            public void error(ACException e) {
                callback.error(e);
            }
        });
    }

    public static void updatePassword(String userName, String password, final PayloadCallback<ACUserInfo> callback) {
        ACMsg request = new ACMsg();
        request.setName(reqName(DCP_SERVICE_PATH_UPDATE_PASSWORD));
        request.put("loginName", userName);
        request.put("password", password);
        sendToDCPService(request, new PayloadCallback<ACMsg>() {
            @Override
            public void success(ACMsg acMsg) {
                try {
                    saveLoginData(acMsg);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                ACUserInfo acUserInfo = new Gson().fromJson(new JSONObject(acMsg.getObjectData()).toString(), ACUserInfo.class);
                String nickName = (String) acMsg.getObjectData().get("nickName");
                if (!TextUtils.isEmpty(nickName)) {
                    acUserInfo.setName(nickName);
                }
                callback.success(acUserInfo);
            }

            @Override
            public void error(ACException e) {
                callback.error(e);
            }
        });
    }

    public static void changeNickName(String nickName, PayloadCallback<ACMsg> callback) {
        ACMsg request = new ACMsg();
        request.setName(reqName(DCP_SERVICE_PATH_UPDATE_PROFILE));
        request.put(KEY_NICK_NAME, nickName);
        sendToDCPServiceWithoutSign(request, callback);
    }

    public static void checkToken(final PayloadCallback<Boolean> callback) {
        if (TextUtils.isEmpty(getDcpToken())) {
            callback.error(new ACException(DCP_SERVICE_TOKEN_EMPTY_CODE, "dcpToken is empty"));
            return;
        }
        ACMsg request = new ACMsg();
        request.setName(reqName(DCP_SERVICE_PATH_CHECK_TOKEN));
        sendToDCPService(request, new PayloadCallback<ACMsg>() {
            @Override
            public void success(ACMsg acMsg) {
                boolean valid = acMsg.getBoolean(KEY_TOKEN_VALID);
                callback.success(valid);
            }

            @Override
            public void error(ACException e) {
                callback.error(e);
            }
        });
    }

    public static void logout(final PayloadCallback<ACMsg> callback) {
        ACMsg request = new ACMsg();
        request.setName(reqName(DCP_SERVICE_PATH_LOGOUT));
        sendToDCPServiceWithoutSign(request, new PayloadCallback<ACMsg>() {
            @Override
            public void success(ACMsg acMsg) {
                AC.accountMgr().logout();
                cleanDcpToken();
                callback.success(acMsg);
            }

            @Override
            public void error(ACException e) {
                callback.error(e);
            }
        });
    }

    public static void syncContent(DCPServiceContentType type, PayloadCallback<ACMsg> callback) {
        ACMsg request = new ACMsg();
        request.setName(reqName(DCP_SERVICE_PATH_SYNC_CONTENT));
        String contentType = "sync";
        switch (type) {
            case DCPServiceContentTypeTermOfUse:
                contentType = contentType + "TermOfUse";
                break;
            case DCPServiceContentTypeLegalNotice:
                contentType = contentType + "LegalNotice";
                break;
            case DCPServiceContentEXTERNAL_URL:
                contentType = contentType + "ExternalUrl";
                break;
            case DCPServiceContentAfterSales:
                contentType = contentType + "AfterSales";
                break;
            case DCPServiceContentHOTLINE:
                contentType = contentType + "HotLine";
                break;
            case DCPServiceContentCookies:
                contentType = contentType + "Cookies";
                break;
            case DCPServiceContentPersonalData:
                contentType = contentType + "PersonalData";
                break;
            case DCPServiceContentManual:
                contentType = contentType + "Manual";
                break;
            case DCPServiceContentAppliances:
                contentType = contentType + "Appliances";
                break;
            case DCPServiceContentLibraries:
                contentType = contentType + "Libraries";
                break;
            case DCPServiceContentAPPLIANCESHOP:
                contentType = contentType + "ApplianceShop";
                break;
        }
        request.put("contentType", contentType);
        request.put("lang", getLanguage());
        sendToDCPServiceWithoutSign(request, callback);
    }

    public enum DCPServiceContentType {
        DCPServiceContentTypeTermOfUse,
        DCPServiceContentTypeLegalNotice,
        DCPServiceContentAfterSales,
        DCPServiceContentCookies,
        DCPServiceContentPersonalData,
        DCPServiceContentManual,
        DCPServiceContentAppliances,
        DCPServiceContentEXTERNAL_URL,
        DCPServiceContentLibraries,
        DCPServiceContentHOTLINE,
        DCPServiceContentAPPLIANCESHOP,
    }

    private static String reqName(String servicePath) {
        return DCP_SERVICE_PATH_BASE + servicePath;
    }

    public static void sendToDCPService(ACMsg request, final PayloadCallback<ACMsg> callback) {
        request.put(DCP_SERVICE_PARAM_TOKEN_NAME, getDcpToken());
        request.put(DCP_SERVICE_DCP_UID, getDcpUid());
        request.put(DCPServiceParamMarketName, getMarket());
        AC.sendToService(SUBDOMAIN_NAME, SERVICE_NAME, SERVICE_VERSION, request, new PayloadCallback<ACMsg>() {
            @Override
            public void success(ACMsg acMsg) {
                Log.i("dubug_mill", "sendToService返回数据:" + acMsg.toString());
                whenSuccess(acMsg, callback);
            }

            @Override
            public void error(ACException e) {
                whenError(e, callback);
            }
        });
        return;
    }

    private static void sendToDCPServiceWithoutSign(ACMsg request, final PayloadCallback<ACMsg> callback) {
        request.put(DCP_SERVICE_PARAM_TOKEN_NAME, getDcpToken());
        request.put(DCP_SERVICE_DCP_UID, getDcpUid());
        request.put(DCPServiceParamMarketName, getMarket());
        AC.sendToServiceWithoutSign(SUBDOMAIN_NAME, SERVICE_NAME, SERVICE_VERSION, request, new PayloadCallback<ACMsg>() {
            @Override
            public void success(ACMsg acMsg) {
                whenSuccess(acMsg, callback);
            }

            @Override
            public void error(ACException e) {
                whenError(e, callback);
            }
        });
    }

    private static void whenSuccess(ACMsg responseMsg, PayloadCallback<ACMsg> callback) {
        if (responseMsg.isErr()) {
            if (responseMsg.getErrCode() == DCP_SERVICE_TOKEN_INVALID_CODE && mDcpTokenInvalidCallback != null) {
                mDcpTokenInvalidCallback.onDCPTokenInvalid();
            }
            callback.error(new ACException(responseMsg.getErrCode(), responseMsg.getErrMsg(), responseMsg.getErrDesc()));
            return;
        }
        callback.success(responseMsg);
    }

    private static void whenError(ACException e, PayloadCallback<ACMsg> callback) {
        if (e.getErrorCode() == DCP_SERVICE_TOKEN_INVALID_CODE && mDcpTokenInvalidCallback != null) {
            mDcpTokenInvalidCallback.onDCPTokenInvalid();
        }
        callback.error(e);
    }


    private static void sendToServiceForLogin(ACMsg request, final PayloadCallback<ACUserInfo> callback) {
        sendToDCPServiceWithoutSign(request, new PayloadCallback<ACMsg>() {
            @Override
            public void success(ACMsg acMsg) {
                try {
                    saveLoginData(acMsg);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                ACUserInfo acUserInfo = new Gson().fromJson(new JSONObject(acMsg.getObjectData()).toString(), ACUserInfo.class);
                String nickName = (String) acMsg.getObjectData().get("nickName");
                if (!TextUtils.isEmpty(nickName)) {
                    acUserInfo.setName(nickName);
                }
                callback.success(acUserInfo);
            }

            @Override
            public void error(ACException e) {
                callback.error(e);
            }
        });
    }

    private static void saveLoginData(ACMsg responseMsg) throws ParseException {
        if (responseMsg.contains("userId")) {
            PreferencesUtils.putLong(MainApplication.sContext, ACConfiguration.KEY_USERID, responseMsg.getLong("userId"));
        }
        String token = (String) responseMsg.getObjectData().get("token");
        if (!TextUtils.isEmpty(token)) {
            PreferencesUtils.putString(MainApplication.sContext, ACConfiguration.KEY_TOKEN, token);
        }
        String dcpUid = (String) responseMsg.getObjectData().get("dcpUid");
        if (!TextUtils.isEmpty(dcpUid)) {
            PreferencesUtils.putString(MainApplication.sContext, DCP_SERVICE_DCP_UID, dcpUid);
        }
        String tokenExpire = (String) responseMsg.getObjectData().get("tokenExpire");
        if (!TextUtils.isEmpty(tokenExpire)) {
            long tokenExpire_long = getDateFormat().parse(tokenExpire).getTime();
            PreferencesUtils.putLong(MainApplication.sContext, ACConfiguration.KEY_TOKEN_EXPIRE, tokenExpire_long);
        }
        String refreshToken = (String) responseMsg.getObjectData().get("refreshToken");
        if (!TextUtils.isEmpty(refreshToken)) {
            PreferencesUtils.putString(MainApplication.sContext, ACConfiguration.KEY_REFRESH_TOKEN, refreshToken);
        }
        String refreshTokenExpire = (String) responseMsg.getObjectData().get("refreshTokenExpire");
        if (!TextUtils.isEmpty(refreshTokenExpire)) {
            long refreshTokenExpire_long = getDateFormat().parse(refreshTokenExpire).getTime();
            PreferencesUtils.putLong(MainApplication.sContext, ACConfiguration.KEY_REFRESH_TOKEN_EXPIRE, refreshTokenExpire_long);
        }
        if (!TextUtils.isEmpty((String) responseMsg.getObjectData().get(DCP_SERVICE_PARAM_TOKEN_NAME))) {
            PreferencesUtils.putString(MainApplication.sContext, DCP_SERVICE_DCP_TOKEN_NAME, (String) responseMsg.getObjectData().get(DCP_SERVICE_PARAM_TOKEN_NAME));
        }
    }

    public static String getDcpToken() {
        String dcpToken = PreferencesUtils.getString(MainApplication.sContext, DCP_SERVICE_DCP_TOKEN_NAME);
        return TextUtils.isEmpty(dcpToken) ? "" : dcpToken;
    }

    public static String getDcpUid() {
        String dcpUid = PreferencesUtils.getString(MainApplication.sContext, DCP_SERVICE_DCP_UID);
        return TextUtils.isEmpty(dcpUid) ? "" : dcpUid;
    }

    public static void cleanDcpToken() {
        PreferencesUtils.putString(MainApplication.sContext, DCP_SERVICE_DCP_TOKEN_NAME, "");
    }

    public interface DCPTokenInvalidCallback {
        void onDCPTokenInvalid();
    }

    public static void setDcpTokenInvalidCallback(DCPTokenInvalidCallback dcpTokenInvalidCallback) {
        mDcpTokenInvalidCallback = dcpTokenInvalidCallback;
    }

    private static SimpleDateFormat getDateFormat() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format;
    }

    public static void setLanguage(String language) {
        PreferencesUtils.putString(MainApplication.sContext, DCPServiceDCPLanguageName, language);
    }

    public static String getLanguage() {
        String language = PreferencesUtils.getString(MainApplication.sContext, DCPServiceDCPLanguageName, "");
        return TextUtils.isEmpty(language) ? "fr" : language;
    }

    public static void setMarket(String market) {
        PreferencesUtils.putString(MainApplication.sContext, DCPServiceDCPMarketName, market);
    }

    public static String getMarket() {
        String market = PreferencesUtils.getString(MainApplication.sContext, DCPServiceDCPMarketName, "");
        return TextUtils.isEmpty(market) ? "GS_FR" : market;
    }
}