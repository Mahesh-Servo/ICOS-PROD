package com.example.masIntg.utility;

import java.io.File;

public class Constants {
	
	static ReadPropertyFIle bundle = ReadPropertyFIle.getInstance();
	
	public static String DRIVER = ReadPropertyFIle.getInstance().getPropConst().getProperty("driver");
	public static String DRIVER_URL = ReadPropertyFIle.getInstance().getPropConst().getProperty("driverUrl");
	public static String USERNAME = ReadPropertyFIle.getInstance().getPropConst().getProperty("userName");
	public static String PASSWORD = ReadPropertyFIle.getInstance().getPropConst().getProperty("passWord");
	
	public static final String DSFLAG = ReadPropertyFIle.getInstance().getPropConst().getProperty("dsflag");
	
	public static final String DATASOURCE = ReadPropertyFIle.getInstance().getPropConst().getProperty("datasource");
	
//	public static final String INITIAL_CONTEXT_FACTORY = ReadPropertyFIle.getInstance().getPropConst().getProperty("INITIAL_CONTEXT_FACTORY");
//	
//	public static final String PROVIDER_URL = ReadPropertyFIle.getInstance().getPropConst().getProperty("PROVIDER_URL");
	
//    public static final String filePath = "D:\\MAHESH\\MAS PROPERTY\\";
	
//	public static final String filePath = "/caoduatapp/Oracle/Middleware/user_projects/domains/CAOD/SRVConfig/API_PROP/";
	
	public static final String seperator = "\\";
	
	public static final String FIWebService = ReadPropertyFIle.getInstance().getPropConst().getProperty("FIWebService");

	public static String getFilepath() {
		
//		final String filePath1 = System.getProperty("user.dir") + File.separator + "SRVConfig" + File.separator + "API_PROP";  //uat
		final String filePath1 = System.getProperty("user.dir") + File.separator + "SRVConfig";    //live

		return filePath1;
	}

	public static String getSeperator() {
		return seperator;
	}

}
