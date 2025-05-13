package com.example.masIntg.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class ReadPropertyFIle {
	
	private static ReadPropertyFIle obj = null;
	
	private ReadPropertyFIle() {}
    
	public static ReadPropertyFIle getInstance() {
		
		if(obj ==null) {
			obj = new ReadPropertyFIle();
			System.out.println("Getting values from property file");
		}
		
		return obj;
		
	}
	
	public Properties getPropConst() {
		
		Properties prop = new Properties();
		InputStream input = null;
		
		try {
			
			input = new FileInputStream(Constants.getFilepath()+ File.separator + "MAS_API.properties");
			System.out.println("path of  prop file"+input);
			prop.load(input);
		}
		catch (Exception e) {
			System.out.println("Error while fetching prop file"+e);
		
		}
		finally {
			if(input!=null) {
				try {
					input.close();
				}
				catch (Exception ex) {
					System.out.println("Error while closing prop connection"+ex);
				}
			}
		}
		return prop;
		
	}

}
