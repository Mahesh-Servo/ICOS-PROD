package com.LsmFiServices;

import java.net.InetAddress;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.LsmFiServices.Utility.OperationUtillity;
import com.LsmFiServices.Utility.appruntime.AppRuntimeLogUtils;

@SpringBootApplication
//@EnableScheduling
public class LsmFiServicesApplication {

    private static final Logger log = LoggerFactory.getLogger(LsmFiServicesApplication.class);

    private static LocalDateTime startDateTIme;
    private static String hostName;

    public static void main(String[] args) {
	log.info("LsmFiServicesApplication is starting @ " + LocalDateTime.now());
	startDateTIme = LocalDateTime.now();
	hostName = getHostName();
	AppRuntimeLogUtils.saveAppStartTime(startDateTIme, hostName);
	getAppShutdownDetails();
	SpringApplication.run(LsmFiServicesApplication.class, args);
    }

    public static String getHostName() {
	try {
	    log.info("AppRuntimeLogs.getHostName() :: " + InetAddress.getLocalHost().getHostName());
	    log.info("AppRuntimeLogs.InetAddress() :: " + InetAddress.getLocalHost().toString());
	    return InetAddress.getLocalHost().getHostName();
	} catch (Exception e) {
	    log.error("AppRuntimeLogs.getHostName() \n " + OperationUtillity.traceException(e));
	    return "Invalid Host";
	}
    }

    public static void getAppShutdownDetails() {
	Runtime.getRuntime().addShutdownHook(new Thread(() -> {
	    AppRuntimeLogUtils.updateAppShutdownTime(LocalDateTime.now(), hostName, startDateTIme);
	    log.info("LsmFiServicesApplication is shutting down @ " + LocalDateTime.now());
	}));
    }
}
