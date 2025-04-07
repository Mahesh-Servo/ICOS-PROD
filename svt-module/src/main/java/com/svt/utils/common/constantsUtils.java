package com.svt.utils.common;

import java.io.File;


public class constantsUtils {

	static ReadPropertyFIle bundle = ReadPropertyFIle.getInstance();

	public static String driver = ReadPropertyFIle.getInstance().getPropConst().getProperty("driver");
	public static String driverUrl = ReadPropertyFIle.getInstance().getPropConst().getProperty("driverUrl");
	public static String userName = ReadPropertyFIle.getInstance().getPropConst().getProperty("userName");
	public static String password = ReadPropertyFIle.getInstance().getPropConst().getProperty("passWord");
	public static final String dsFlag = ReadPropertyFIle.getInstance().getPropConst().getProperty("dsflag");
	public static final String dataSource = ReadPropertyFIle.getInstance().getPropConst().getProperty("datasource");
	public static final String initialContextFactory = ReadPropertyFIle.getInstance().getPropConst()
			.getProperty("INITIAL_CONTEXT_FACTORY");
	public static final String filePath = System.getProperty("user.dir") + File.separator + "SRVConfig";
	public static final String seperator = "\\";
	public static final String fiWebServiceUrl = commonUtility.getFIServiceLink();

	public static String getFilepath() {
		final String filePath1 = System.getProperty("user.dir") + File.separator + "SRVConfig";
		return filePath1;
	}

	public static String getSeperator() {
		return seperator;
	}

}
