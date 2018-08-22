package com.supor.suporairclear.controller;

import android.content.Context;
import android.util.Log;

import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACDeviceMsg;
import com.accloud.utils.PreferencesUtils;
import com.rihuisoft.loginmode.utils.AppUtils;
import com.supor.suporairclear.application.MainApplication;
import com.supor.suporairclear.config.Config;
import com.supor.suporairclear.model.DeviceMsg;

/**
 * Created by Xuri on 2015/1/24.
 */
public class SendToDevice {
    private static final int OPENLIGHT = 1;
    private static final int CLOSELIGHT = 0;
    private Context context;
    private String subDomain;

    public static final int BINARY = 0;
    public static final int KLV = 1;
    public static final int JSON = 2;
    
    public SendToDevice(Context context) {
        this.context = context;
        subDomain = PreferencesUtils.getString(MainApplication.getInstance(), "subDomain", Config.SUBMAJORDOMAIN);
    }

    @SuppressWarnings("deprecation")
	public void toDevice(Long deviceId,String commend,int value,PayloadCallback<ACDeviceMsg> callback) {
        /**
         * Command/messages sent through the cloud services to the equipment
         *
         * @param subDomain subdomain
         * @param deviceId  Logic device id
         * @param msg       The specific message content
         * @returnEquipment return to monitor the callback, the response message returned equipment
         */
    	Log.d("sendToDeviceWithOption", "deviceId:" + deviceId);
    	getDeviceMsg(commend,value);
//        AC.bindMgr().sendToDeviceWithOption(subDomain, deviceId, getDeviceMsg(commend,value), AC.LOCAL_FIRST, callback);
    }

    

    public ACDeviceMsg getDeviceMsg(String commend,int value) {
        //Note: the actual development time please select one of the kind of message format
    	byte[] control = commendCreate(commend,value);
    	if(control!=null){
	        switch (getFormatType()) {
	            case BINARY:
	                return new ACDeviceMsg(DeviceMsg.CODE, control);
	        }
    	}
        return null;
    }

    public boolean parseDeviceMsg(ACDeviceMsg msg) {
        //Note: the actual development time please select one of the kind of message format
        switch (getFormatType()) {
            case BINARY:
                byte[] bytes = msg.getContent();
                if (bytes != null){
                    DeviceMsg respMsg = new DeviceMsg(bytes);
                    return respMsg.getError()==0?true:false;
                }
//            case KLV:
//                ACKLVObject resp = msg.getKLVObject();
//                if (resp != null)
//                    return resp.get(1);
//            case JSON:
//                try {
//                    JSONObject object = new JSONObject(new String(msg.getContent()));
//                    return object.optBoolean("result");
//                } catch (Exception e) {
//                }
        }
        return false;
    }

    //Note: the actual developing message format for known without choice
    public int getFormatType() {
        return PreferencesUtils.getInt(context, "formatType", BINARY);
    }
    
    public byte[] commendCreate(String commend,int value){
    	byte[] control = new byte[]{};
    	byte[] attr_flags = new byte[2];
    	byte[] attr_vals =new byte[]{0,0,0,0,0,0,0,0};
    	byte[] sn = short2Byte(AppUtils.getCommendSN());
    	byte[] default_attr;
    	 
    	attr_flags[0] = 0;
    	attr_flags[1] = 0;
    	switch (commend){
    		case "on_off":
    			default_attr = new byte[]{(byte)0xFF,(byte)0xFF,0x00,0x1A,0x03,sn[1],0x00,0x00,0x01};
    			if(value == 1){
    				attr_flags[1] = Byte.parseByte("00000100",2);
    				attr_vals[0]=Byte.parseByte("00000001",2);
    			}else{
    				attr_flags[1] = Byte.parseByte("00000100",2);
    				attr_vals[0]=Byte.parseByte("00000000",2);
    			}
    			
    			break;
    		case "model":
    			default_attr = new byte[]{(byte)0xFF,(byte)0xFF,0x00,0x1A,0x03,sn[1],0x00,0x00,0x01};
    			// automatic
    			if (value == 0) {
    				attr_flags[1] = Byte.parseByte("00001000",2);
    				attr_vals[1]=Byte.parseByte("00000000",2);
    		    // sleep
    			} else if (value == 1) {
    				attr_flags[1] = Byte.parseByte("00001000",2);
    				attr_vals[1]=Byte.parseByte("00000001",2);
    			// manual
    			} else if (value == 2) {
    				attr_flags[1] = Byte.parseByte("00001000",2);
    				attr_vals[1]=Byte.parseByte("00000010",2);
    			}
    			break;
    		case "speed":
    			default_attr = new byte[]{(byte)0xFF,(byte)0xFF,0x00,0x1A,0x03,sn[1],0x00,0x00,0x01};
    			if (value == 1) {
    				attr_flags[1] = Byte.parseByte("00010000",2);
    				attr_vals[2]=Byte.parseByte("00000001",2);
    			} else if (value == 2) {
    				attr_flags[1] = Byte.parseByte("00010000",2);
    				attr_vals[2]=Byte.parseByte("00000010",2);
    			} else if (value == 3) {
    				attr_flags[1] = Byte.parseByte("00010000",2);
    				attr_vals[2]=Byte.parseByte("00000011",2);
    			} else {
    				attr_flags[1] = Byte.parseByte("00010000",2);
    				attr_vals[2]=Byte.parseByte("00000100",2);
    			}
    			break;
    		case "anion":
    			default_attr = new byte[]{(byte)0xFF,(byte)0xFF,0x00,0x1A,0x03,sn[1],0x00,0x00,0x01};
    			if (value == 0) {
    				attr_flags[0] = Byte.parseByte("00010000",2);
    				attr_vals[0]=Byte.parseByte("00000000",2);
    			} else if (value == 1) {
    				attr_flags[0] = Byte.parseByte("00010000",2);
    				attr_vals[0]=Byte.parseByte("01000000",2);
    			}
    			break;
    		case "light":
    			default_attr = new byte[]{(byte)0xFF,(byte)0xFF,0x00,0x1A,0x03,sn[1],0x00,0x00,0x01};
    			if (value == 0) {
    				attr_flags[0] = Byte.parseByte("00100000",2);
    				attr_vals[7]=Byte.parseByte("00000000",2);
    			} else if (value == 1) {
    				attr_flags[0] = Byte.parseByte("00100000",2);
    				attr_vals[7]=Byte.parseByte("00000001",2);
    			} else {
    				attr_flags[0] = Byte.parseByte("00100000",2);
    				attr_vals[7]=Byte.parseByte("00000010",2);
    			}
    			break;
    		case "pm25":
    			default_attr = new byte[]{(byte)0xFF,(byte)0xFF,0x00,0x06,0x03,sn[1],0x00,0x00, 0x02};
    			attr_flags = new byte[]{};
    	    	attr_vals =new byte[]{};
    			break;
			default:
				default_attr = new byte[]{(byte)0xFF,(byte)0xFF,0x00,0x1A,0x03,sn[1],0x00,0x00,0x01};
				break;
    			
    	}
    	
    	control = addCheckSum(default_attr, attr_flags, attr_vals);
    	Log.d("control", bytesToHexString(control));
    	return control;
    }
    
    public byte[] addCheckSum(byte[] control, byte[] attr_flags,byte[] attr_vals){
    	int len = control.length +  attr_flags.length + attr_vals.length + 1;
    	byte[] resultByte = new byte[len];
    	int sum =0;
    	int index=0;
//    	byte[] control = new byte[]{(byte)0xFF,(byte)0xFF,0x00,0x1A,0x03,sn[1],0x00,0x00,0x01};
    	for (int i=0;i<control.length;i++){
    		if(i>1){
    			sum += (int) control[i] & 0xFF;
    		}
    		resultByte[index]=control[i];
    		index+=1;
    	}
    	
    	for (int i=0;i<attr_flags.length;i++){
    		sum += (int) attr_flags[i] & 0xFF;
    		resultByte[index]=attr_flags[i];
    		index+=1;
    	}
    	
    	for (int i=0;i<attr_vals.length;i++){
    		sum += (int) attr_vals[i] & 0xFF;
    		resultByte[index]=attr_vals[i];
    		index+=1;
    	}
    	
    	byte[] checkSum = short2Byte((short) (sum%256));
    	resultByte[index]=checkSum[1];
    	return resultByte;
    }
    
    public static byte[] short2Byte(short a){  
        byte[] b = new byte[2];  
          
        b[0] = (byte) (a >> 8);  
        b[1] = (byte) (a);  
          
        return b;  
    }  
    public static String bytesToHexString(byte[] src){       
        StringBuilder stringBuilder = new StringBuilder();       
        if (src == null || src.length <= 0) {       
            return null;       
        }       
        for (int i = 0; i < src.length; i++) {       
            int v = src[i] & 0xFF;       
            String hv = Integer.toHexString(v);       
            if (hv.length() < 2) {       
                stringBuilder.append(0);       
            }       
            stringBuilder.append(hv);       
        }       
        return stringBuilder.toString();
    }
}
