package com.rihuisoft.loginmode.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * <P>
 *
 * <P>
 * 
 * @author Lien Li
 * @version 1.00
 * 
 * */
public class StringUtils {

	/** */
	public static final String ENCODING_UTF8 = "utf-8";
	

	/** 
     * Test for a legitimate email address
     * @param email 
     * @return True legal False illegal
     */  
    public static boolean isEmail(String email) {

        Pattern emailPattern = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        Matcher matcher = emailPattern.matcher(email);
        if(matcher.find()){
            return true;
        }
        return false;
    }
	/**
	 * Password is legal
	 * @param newPwd
	 * @return True legal False illegal
	 */
	public static boolean isPassword(String newPwd) {
		int i = newPwd.matches(".*\\d+.*") ? 1 : 0;
		int j = newPwd.matches(".*[a-z]+.*") ? 1 : 0;
		int k = newPwd.matches(".*[A-Z]+.*") ? 1 : 0;
		if (i + j + k == 3) {
			return true;
		}
		return false;
	}


	/**
	 * Determine whether legitimate password (begin with a letter, allowing 7 ~ 30 bytes, allow alphanumeric underscore)
	 *
	 * @param password：account
	 * @return boolean
	 */
	public static  boolean isAccountRegex(String password) {
//		String passwordPatterns= "(?![0-9A-Z]+$)(?![0-9a-z]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,30}$";
		String passwordPatterns= "^(?=.*[0-9].*)(?=.*[A-Z].*)(?=.*[a-z].*).{8,200}$";
		return Pattern.matches(passwordPatterns, password);
	}


	/**
	 * To determine whether a phone legally
	 * @param phone
	 *
	 * @return boolean true or false
	 * */
    public static boolean isPhoneNumber(String phone) {
        String regExp = "^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(phone);
        return m.find();
    }
    

    public static boolean isNumeric(String str){
    	   Pattern pattern = Pattern.compile("[0-9]*");
    	   Matcher isNum = pattern.matcher(str);
    	   if( !isNum.matches() ){
    	       return false;
    	   }
    	   return true;
    	}

	/**
	 *
	 * 
	 * @return Access to the string
	 * */
	public static StringBuffer getBuffer() {
		return new StringBuffer(50);
	}

	/**
	 * 
	 * Access to the string
	 * 
	 * @param length
	 *            鎸囧畾闀垮害
	 * 
	 * @return
	 * */
	public static StringBuffer getBuffer(int length) {
		return new StringBuffer(length);
	}

	/**
	 *
	 * 
	 * @param str
	 *          To determine whether a string is empty
	 * @return boolean true or false
	 * */
	public static boolean isEmpty(String str) {
		if (str == null || str == "" || str.trim().equals(""))
			return true;
		return false;
	}

	/**
	 *
	 * @param str
	 *           To determine whether a string is empty
	 * @return boolean true or false
	 * */
	public static boolean isNumEmpty(String str) {
		if (str == null || str == "" || str.trim().equals("")
				|| str.trim().equals("0"))
			return true;
		return false;
	}

	/**
	 * Access code
	 * @param data
	 *
	 * @return String
	 * */
	public static String encode(String data) {
		try {
			return URLEncoder.encode(data, ENCODING_UTF8);
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}

	/**
	 * String to int
	 * @param num
	 *
	 * @return int
	 * */
	public static int toInt(String num) {
		try {
			return Integer.parseInt(num);
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 *
	 * String to long
	 * @param num
	 *
	 * @return long
	 * */
	public static long toLong(String num) {
		try {
			return Long.parseLong(num);
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 *
	 * String to float
	 * @param num
	 *
	 * @return long
	 * */
	public static float toFloat(String num) {
		try {
			return Float.parseFloat(num);
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * String to boolean
	 * 
	 * @param num
	 *
	 * @return boolean
	 * */
	public static boolean toBoolean(String num) {
		if (isEmpty(num))
			return false;
		if (num.equals("true"))
			return true;
		return false;
	}

	/**
	 * sql
	 * 
	 * @param keyWord
	 *
	 * @return * */
	public static String sqliteEscape(String keyWord) {
		keyWord = keyWord.replace("/", "//");
		keyWord = keyWord.replace("'", "''");
		keyWord = keyWord.replace("[", "/[");
		keyWord = keyWord.replace("]", "/]");
		keyWord = keyWord.replace("%", "/%");
		keyWord = keyWord.replace("&", "/&");
		keyWord = keyWord.replace("_", "/_");
		keyWord = keyWord.replace("(", "/(");
		keyWord = keyWord.replace(")", "/)");
		return keyWord;
	}

	/**
	 * sql
	 * @param keyWord
	 *
	 * @return
	 * */
	public static String sqliteUnEscape(String keyWord) {
		keyWord = keyWord.replace("//", "/");
		keyWord = keyWord.replace("''", "'");
		keyWord = keyWord.replace("/[", "[");
		keyWord = keyWord.replace("/]", "]");
		keyWord = keyWord.replace("/%", "%");
		keyWord = keyWord.replace("/&", "&");
		keyWord = keyWord.replace("/_", "_");
		keyWord = keyWord.replace("/(", "(");
		keyWord = keyWord.replace("/)", ")");
		return keyWord;
	}

	/**
	 * Get the date
	 * 
	 * @param longDate
	 *
	 * @param format
	 *        */
	public static String getStrDate(String longDate, String format) {
		if (isEmpty(longDate))
			return "";
		long time = Long.parseLong(longDate);
		Date date = new Date(time);
		return getStrDate(date, format);
	}

	/**
	 * Get the date
	 * 
	 * @param time
	 *
	 * @param format
	 *            */
	public static String getStrDate(long time, String format) {
		Date date = new Date(time);
		return getStrDate(date, format);
	}

	/**
	 * Get the date
	 * 
	 * @param date
	 *
	 * @param formate
	 *           */
	public static String getStrDate(Date date, String formate) {
		SimpleDateFormat dd = new SimpleDateFormat(formate);
		return dd.format(date);
	}

	/**
	 * 
	 * Get the date *
	 * @return  *
	 * */
	public static String getStrDate() {
		SimpleDateFormat dd = new SimpleDateFormat("yyyy-MM-dd");
		return dd.format(new Date());
	}

	/**
	 * To get a random number
	 * 
	 * @param num
	 *
	 * @return String
	 * 
	 * */
	public static String getRandomStr(int num) {
		StringBuffer temp = new StringBuffer();
		Random rand = new Random();
		for (int i = 0; i < num; i++) {
			temp.append(rand.nextInt(10));
		}
		return temp.toString();
	}
	
	public static String getJoinStr(String saperator, List<String> valueList)
	{
		if(valueList.size() == 0){
			return "once";
		}else{
		StringBuilder returnValue  = new StringBuilder();
		returnValue.append("week[");
		for(int i = 0; i < valueList.size(); i++)
		{
			returnValue.append(valueList.get(i));
			if(i != valueList.size() - 1)
			{
				returnValue.append(saperator);
			}
		}
		returnValue.append("]");
		return returnValue.toString();
		}
	}
	
	public static String getTimerCycleStr(String timerCycle){

		if(null != timerCycle & !"".equals(timerCycle)){
			if("once".equals(timerCycle)){
				return "鎵ц涓�";
			}else{

				timerCycle = timerCycle.replace("week[", "").replace("]", "");
		    	String[] cycleArray = timerCycle.split(",");
		    	
		    	StringBuilder result = new StringBuilder();		
	    	for(int i = 0; i < cycleArray.length; i++)
	    	{
	    		
	    		if(cycleArray[i].equals("0"))
	    		{
	    			result.append("鍛ㄦ棩");
	    			if(i < cycleArray.length -1)
	    			{
	    				result.append(" ");
	    			}
	    		} else if(cycleArray[i].equals("1"))
	    		{
	    			result.append("鍛ㄤ竴");
	    			if(i < cycleArray.length -1)
	    			{
	    				result.append(" ");
	    			}
	    		} else if(cycleArray[i].equals("2"))
	    		{
	    			result.append("鍛ㄤ簩");
	    			if(i < cycleArray.length -1)
	    			{
	    				result.append(" ");
	    			}
	    		} else if(cycleArray[i].equals("3"))
	    		{
	    			result.append("鍛ㄤ笁");
	    			if(i < cycleArray.length -1)
	    			{
	    				result.append(" ");
	    			}
	    		} else if(cycleArray[i].equals("4"))
	    		{
	    			result.append("鍛ㄥ洓");
	    			if(i < cycleArray.length -1)
	    			{
	    				result.append(" ");
	    			}
	    		} else if(cycleArray[i].equals("5"))
	    		{
	    			result.append("鍛ㄤ簲");
	    			if(i < cycleArray.length -1)
	    			{
	    				result.append(" ");
	    			}
	    		}
	    		 else if(cycleArray[i].equals("6"))
	     		{
	     			result.append("鍛ㄥ叚");
	     			if(i < cycleArray.length -1)
	    			{
	    				result.append(" ");
	    			}
	     		}
	    	}
	    	return result.toString();		
			}
		}else{
			return "";
		}
    	
    }

	public static String shotTime(String timeString){
		String[] cycleArray = timeString.split(" ");	
		String[] cycleArray1 = cycleArray[1].split(":");
		return String.format("%s:%s", cycleArray1[0],cycleArray1[1]);
	}
	public static String[] splitTime(String times){
		String[] cycleArray = times.split(" ");	
		String[] cycleArray1 = cycleArray[1].split(":");
		return cycleArray1;
	}
	
	
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static boolean choisDatetime(String str1,String str2) {	//str1>str2 true		
        try {
            Date date1 = sdf.parse(str1);
            Date date2 = sdf.parse(str2);
            long diff = date1.getTime() - date2.getTime();//
            return diff>0 ? true:false;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
	public static SimpleDateFormat getSimpleDateFormat(){
		return sdf;
	}
	static Calendar cc;
	public static String addStringDay(String today,int day) {	//str1>str2 true		
        try {
        	cc=Calendar.getInstance();
        	if(day==0){ return sdf.format(cc.getTime());
            }else{           
            cc.setTime(sdf.parse(today));
            cc.add(Calendar.DAY_OF_MONTH, day);//add 1 day
            return sdf.format(cc.getTime());
            }            	
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

	public static boolean isHanzi(String city){
		for (int i = 0; i < city.length(); i++) {
			// is hanji
			if (java.lang.Character.toString(city.toCharArray()[i]).matches(
					"[\\u4E00-\\u9FA5]+")) {
				return true;
			}
		}
		return false;
	}
}
