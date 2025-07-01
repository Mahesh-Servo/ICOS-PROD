package com.LsmFiServices.Utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadPropertyFIle {

    private static final Logger logger = LoggerFactory.getLogger(ReadPropertyFIle.class);
    private static ReadPropertyFIle obj = null;

    private ReadPropertyFIle() {
    }

    public static ReadPropertyFIle getInstance() {
	if (obj == null) {
	    obj = new ReadPropertyFIle();
	}
	return obj;
    }

    public Properties getPropConst() {
	Properties prop = new Properties();
	InputStream input = null;

	try {
	    input = new FileInputStream(Constants.getFilepath() + File.separator + "LSM_API.properties");
	    prop.load(input);
	} catch (Exception e) {
	    logger.info(OperationUtillity.traceException(e));
	} finally {
	    if (input != null) {
		try {
		    input.close();
		} catch (Exception ex) {
		    logger.info(OperationUtillity.traceException(ex));
		}
	    }
	}
	return prop;
    }
}
