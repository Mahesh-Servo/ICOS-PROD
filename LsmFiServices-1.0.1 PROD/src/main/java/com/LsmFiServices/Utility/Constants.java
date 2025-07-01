package com.LsmFiServices.Utility;

import java.io.File;

public class Constants {

    static ReadPropertyFIle bundle = ReadPropertyFIle.getInstance();

    public static String DRIVER = ReadPropertyFIle.getInstance().getPropConst().getProperty("driver");
    public static String DRIVER_URL = ReadPropertyFIle.getInstance().getPropConst().getProperty("driverUrl");
    public static String USERNAME = ReadPropertyFIle.getInstance().getPropConst().getProperty("userName");
    public static String PASSWORD = ReadPropertyFIle.getInstance().getPropConst().getProperty("passWord");
    public static final String DSFLAG = ReadPropertyFIle.getInstance().getPropConst().getProperty("dsflag");
    public static final String DATASOURCE = ReadPropertyFIle.getInstance().getPropConst().getProperty("datasource");
    public static final String INITIAL_CONTEXT_FACTORY = ReadPropertyFIle.getInstance().getPropConst().getProperty("INITIAL_CONTEXT_FACTORY");
    public static final String filePath = System.getProperty("user.dir") + File.separator + "SRVConfig";
    public static final String seperator = "\\";
    public static final String FIWebService = commonUtility.getFIServiceLink();
    public static final String API_URL = commonUtility.getFIServiceLink();
    public static final String PORT = "29010";

    public static String getFilepath() {
	final String filePath1 = System.getProperty("user.dir") + File.separator + "SRVConfig";
	return filePath1;
    }

    public static String getSeperator() {
	return seperator;
    }

}
