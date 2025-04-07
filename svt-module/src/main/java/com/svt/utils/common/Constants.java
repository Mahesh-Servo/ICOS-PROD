package com.svt.utils.common;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Constants {

    static ReadPropertyFIle bundle = ReadPropertyFIle.getInstance();

    public static String DRIVER = ReadPropertyFIle.getInstance().getPropConst().getProperty("driver");
    public static String DRIVER_URL = ReadPropertyFIle.getInstance().getPropConst().getProperty("driverUrl");
    public static String USERNAME = ReadPropertyFIle.getInstance().getPropConst().getProperty("userName");
    public static String PASSWORD = ReadPropertyFIle.getInstance().getPropConst().getProperty("passWord");
    public static final String DSFLAG = ReadPropertyFIle.getInstance().getPropConst().getProperty("dsflag");
    public static final String DATASOURCE = ReadPropertyFIle.getInstance().getPropConst().getProperty("datasource");
    public static final String INITIAL_CONTEXT_FACTORY = ReadPropertyFIle.getInstance().getPropConst()
	    .getProperty("INITIAL_CONTEXT_FACTORY");
    public static final String PROVIDER_URL = ReadPropertyFIle.getInstance().getPropConst().getProperty("PROVIDER_URL");
    public static final String filePath = System.getProperty("user.dir") + File.separator + "SRVConfig";
    public static final String seperator = "\\";
    public static final String FIWebService = commonUtility.getFIServiceLink();
    public static final String insertServiceExecutionDetailsQuery = "INSERT INTO LSM_FI_EXECUTION_DETAILS (PINSTID,SERVICE_NAME,REQUEST_TYPE,FACILITY,ACCOUNT_NUMBER,REQUEST,RESPONSE,STATUS,MESSAGE,DATETIME) VALUES (?,?,?,?,?,?,?,?,?,SYSDATE)";
    public static final String getServiceInputDetailsQuery = "SELECT QUESTION, QUESTION_ID,ANSWER AS ACCOUNT_NUMBER FROM LSM_LIMIT_ANSWERS WHERE PINSTID = ? AND FACILITY_NAME = ? AND QUESTION_ID LIKE '149%'";
//    List<String> listFacility = Arrays.asList("", "", "", "", "", "");

    public static String getFilepath() {
	final String filePath1 = System.getProperty("user.dir") + File.separator + "SRVConfig";
	return filePath1;
    }

    public static String getSeperator() {
	return seperator;
    }

}
