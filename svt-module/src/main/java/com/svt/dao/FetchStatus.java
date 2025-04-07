package com.svt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.svt.service.SvtUnlinkService;
import com.svt.utils.common.OperationUtillity;
import com.svt.utils.dataConnectivity.dbConnection;

@Repository
public class FetchStatus {

	private static final Logger logger = LoggerFactory.getLogger(SvtUnlinkService.class);

	PreparedStatement statement = null;
	ResultSet rs = null;

	public Boolean getStatusForService(String pinstid, String requestType,String processName) {
		Boolean resp = false;
		String table_name = "";
//		logger.info("FetchStatus.getStatusForService().RequestType : " + requestType);
		if("Monitoring".equalsIgnoreCase(processName)) {
			table_name ="MON_FI_EXECUTION_DETAILS";
		} else if("Limit_Setup".equalsIgnoreCase(processName)){  
			table_name = "LSM_FI_EXECUTION_DETAILS";
		}
		try (Connection con = dbConnection.getConnection()) {
			String lsql = "select  STATUS from "+table_name+" where PINSTID = ? and REQUEST_TYPE = ? and UPPER(STATUS)  != ? ";

			statement = con.prepareStatement(lsql);
			statement.setString(1, pinstid);
			statement.setString(2, requestType);
			statement.setString(3, "SUCCESS");

			rs = statement.executeQuery();
			if (rs.next()) {
				resp = true;
			} else {
				resp = false;
			}
			logger.info("FetchStatus.getStatusForService().Return Reponse : pinstid ["+pinstid+"] requestType ["+requestType+"] [exe status flag[" + resp);
		} catch (Exception e) {
			logger.info("FetchStatus.getStatusForService()" + OperationUtillity.traceException(pinstid, e));
		}
		return resp;
	}
}
