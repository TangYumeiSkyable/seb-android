package com.supor.suporairclear.model;

/**
 * Created by enyva on 16/6/2.
 */
public class DeviceMsg {
    public static final int CODE = 66;
    public static final int RESP_CODE = 102;
    public static final int REPORT_CODE = 210;

    private byte[] payload;


    public DeviceMsg(byte[] payload) {
        this.payload = payload;
    }

    /**
     * Switch state: 0 1 open
     */
    public Long getSwitch(){
        String binary = byteToBit(this.payload[9]);
        return Long.valueOf(binary.substring(0,1));
    }

    /**
     * Composite filter replacement identity: 0 don't need one
     */
    public Long getFilterChange1(){
        String binary = byteToBit(this.payload[9]);
        return Long.valueOf(binary.substring(1,2));
    }
    /**
     * NaneCapture filter replacement identity: 0 don't need one
     */
    public Long getFilterChange2(){
        String binary = byteToBit(this.payload[9]);
        return Long.valueOf(binary.substring(2,3));
    }
    /**
     * Front screen replacement identity: 0 don't need one
     */
    public Long getFilterChange3(){
        String binary = byteToBit(this.payload[9]);
        return Long.valueOf(binary.substring(3,4));
    }
    /**
     * Mesh X change: 0 don't need one
     */
    public Long getFilterChange4(){
        String binary = byteToBit(this.payload[9]);
        return Long.valueOf(binary.substring(4,5));
    }
    /**
     * Mesh Y replace logo: 0 don't need one
     */
    public Long getFilterChange5(){
        String binary = byteToBit(this.payload[9]);
        return Long.valueOf(binary.substring(5,6));
    }
    /**
     * Reset the composite filter: bool
     */
    public Boolean getRestFilter1(){
        String binary = byteToBit(this.payload[9]);
        return Long.valueOf(binary.substring(6,7))==1?true:false;
    }
    /**
     * Reset NaneCapture mesh: bool
     */
    public Boolean getRestFilter2(){
        String binary = byteToBit(this.payload[9]);
        return Long.valueOf(binary.substring(binary.length()-1))==1?true:false;
    }
    /**
     * Reset the front mesh: bool
     */
    public Boolean getRestFilter3(){
        String binary = byteToBit(this.payload[10]);
        return Long.valueOf(binary.substring(0,1))==1?true:false;
    }
    /**
     * Reset the X screen: bool
     */
    public Boolean getRestFilter4(){
        String binary = byteToBit(this.payload[10]);
        return Long.valueOf(binary.substring(1,2))==1?true:false;
    }
    /**
     * To reset Y strainer: bool
     */
    public Boolean getRestFilter5(){
        String binary = byteToBit(this.payload[10]);
        return Long.valueOf(binary.substring(2,3))==1?true:false;
    }

    /**
     *Anion switch: 0 1 open
     */
    public Long getIonozierSwitch(){
        String binary = byteToBit(this.payload[10]);
        return Long.valueOf(binary.substring(3,4));
    }

    public Long forbiddenFlag(){
        String binary = byteToBit(this.payload[10]);
        return Long.valueOf(binary.substring(4,5));
    }
    /**
     * Pm25 numerical
     */
    public Long getPm25Value(){
        return Long.valueOf(bytesToInt2(this.payload,11));
    }

    /**
     * Good TVOC 1: optimal 2:3: light pollution 4:5: moderate pollution seriously polluted
     */
    public char getSmellLevel(){
        return byteToChar(this.payload[13]);
    }

    /**
         * modeAutomatic 0:1:2: sleep manually
     */
    public char getMode(){
        return byteToChar(this.payload[14]);
    }

    /**
     * speed Profile 2:1: breeze medium-speed 3: high-speed 4: done
     */
    public char getSpeed(){
        return byteToChar(this.payload[15]);
    }

    /**
     * filtertime1 Unit: hours
     */
    public Long getFilterTime1(){
        return bytesToLong(this.payload,16);
    }

    /**
     * filtertime2 Unit: hours
     */
    public Long getFilterTime2(){
        return bytesToLong(this.payload,20);
    }

    /**
     * filtertime3 Unit: hours
     */
    public Long getFilterTime3(){
        return bytesToLong(this.payload,24);
    }

    /**
     * filtertime4 Unit: hours
     */
    public Long getFilterTime4(){
        return bytesToLong(this.payload,28);
    }

    /**
     * filtertime5 Unit: hours
     */
    public Long getFilterTime5(){
        return bytesToLong(this.payload,32);
    }

    /**
     * time_on_value Unit: minutes
     */
    public int getTimeOnValue(){
        return bytesToInt2(this.payload,36);
    }

    /**
     * time_off_valueUnit: minutes
     */
    public int getTimeOffValue(){
        return bytesToInt2(this.payload,38);
    }

    /**
     * BKLight_Level 0: mode 1 1:2:2 model 3
     */
    public int getBKLight(){
        return byteToChar(this.payload[40]);
    }

    /**
     * error 1: the checksum error 2: command not to identify 3: other errors
     */
    public int getError(){
        return byteToChar(this.payload[41]);
    }

    /**
     * Byte turn -
     */
    public static String byteToBit(byte b) {
        return "" +(byte)((b >> 7) & 0x1) +
                (byte)((b >> 6) & 0x1) +
                (byte)((b >> 5) & 0x1) +
                (byte)((b >> 4) & 0x1) +
                (byte)((b >> 3) & 0x1) +
                (byte)((b >> 2) & 0x1) +
                (byte)((b >> 1) & 0x1) +
                (byte)((b >> 0) & 0x1);
    }

    /**
     * Turn int value byte array
     */
    public static int bytesToInt2(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF)
                |(src[offset+1] & 0xFF <<8));
        return value;
    }


    /**
     * Byte transfer char numerical
     */
    public static char byteToChar(byte b) {
        char c = (char) ((b & 0xFF));
        return c;
    }

    /**
     * Turn long numerical byte array
     */
    public static Long bytesToLong(byte[] src, int offset) {
        int num;
        num = src[offset] & 0xFF;
        num |= ((src[offset+1] << 8) & 0xFF00);
        num |= ((src[offset+2] << 16) & 0xFF0000);
        num |= ((src[offset+3] << 24) & 0xFF000000);
        return Long.valueOf(num);
    }

}
