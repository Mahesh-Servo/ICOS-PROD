package com.LsmFiServices.Utility.appruntime;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.LsmFiServices.Utility.DBConnect;
import com.LsmFiServices.Utility.OperationUtillity;
import com.LsmFiServices.Utility.Queries;

public class AppRuntimeLogUtils {

    private static final Logger log = LoggerFactory.getLogger(AppRuntimeLogUtils.class);

    public static void saveAppStartTime(LocalDateTime startTime, String hostname) {
	try (Connection con = DBConnect.getConnection();
		PreparedStatement ps = con.prepareStatement(Queries.SAVE_APP_START_TIME)) {
	    ps.setObject(1, startTime);
	    ps.setString(2, hostname);
	    boolean confirm = ps.execute();
	    if (confirm) {
		con.commit();
	    }
	} catch (Exception e) {
	    log.error("AppRuntimeLogUtils.saveRuntimeLogs().Exception \n" + OperationUtillity.traceException(e));
	}
    }

    public static void updateAppShutdownTime(LocalDateTime shutdownTime, String hostname, LocalDateTime startTime) {
	try (Connection con = DBConnect.getConnection();
		PreparedStatement ps = con.prepareStatement(Queries.UPDATE_APP_SHUTDOWN_TIME)) {
	    ps.setObject(1, shutdownTime);
	    ps.setString(2, hostname);
	    ps.setObject(3, startTime);
	    int count = ps.executeUpdate();
	    if (count > 0) {  
		con.commit();
	    }
	} catch (Exception e) {
	    log.error("AppRuntimeLogUtils.saveRuntimeLogs().Exception \n" + OperationUtillity.traceException(e));
	}
    }

}
