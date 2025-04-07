package com.svt.utils.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.svt.utils.dataConnectivity.dbConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CaseRouting {

	private static final Logger logger = LoggerFactory.getLogger(CaseRouting.class);

	public boolean getDecisionForFI(String pinstid) {
		List<String> statusList = new ArrayList<>();
		try (Connection con = dbConnection.getConnection();
				PreparedStatement pst = con.prepareStatement("SELECT STATUS FROM MON_FI_EXECUTION_DETAILS WHERE PINSTID = ?");) {
			pst.setString(1, pinstid);
			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {
					statusList.add(OperationUtillity.NullReplace(rs.getString("STATUS")));
				}
			}
		} catch (SQLException e) {
			logger.info("OperationUtillity.getCaseType().Exception\n " + OperationUtillity.traceException(e));
		}
		logger.info("OperationUtillity.getCaseType().pinstid :: " + pinstid);
		logger.info("OperationUtillity.getCaseType().statusList :: " + statusList);
		logger.info("OperationUtillity.getCaseType().statusList size :: " + statusList.size());
		List<String> keywords = Arrays.asList("failure", "request sent", "must", "should", "failed", "fails", "fail",
				"not");

		boolean containsKeywords = statusList.stream().anyMatch(
				value -> keywords.stream().anyMatch(keyword -> value.toLowerCase().contains(keyword.toLowerCase())));

		logger.info("CaseRouting.getDecisionForFI() :: Contains any of the keywords: " + containsKeywords);
		if (!containsKeywords) {
		//	String deleteSchedulerPinstId = OperationUtillity.deleteSchedulerPinstId(pinstid);
			//logger.info("CaseRouting.getDecisionForFI().deleteSchedulerPinstId --> " + deleteSchedulerPinstId);
		}
		return !containsKeywords;

	}

	public String updateFIStatusInEXT(String pinstId) {
		String status = "F";
		String message = "FAILED";
		if (getDecisionForFI(pinstId)) {
			status = "S";
			message = "SUCCESS";
		}
		int afftectedRows = 0;
		try (Connection con = dbConnection.getConnection();
				PreparedStatement ps = con.prepareStatement("UPDATE ICOS_MONT_EXT SET FI_SERVICE_STATUS = ? WHERE PINSTID = ? ");) {
			ps.setString(1, status);
			ps.setString(2, pinstId);
			afftectedRows = ps.executeUpdate();
			if (afftectedRows > 0) {
				con.commit();
				logger.info("Status updated in EXT for PINSTID :: " + pinstId + " STATUS :: " + status);
			} else {
				logger.info("Status not updated in EXT for PINSTID :: " + pinstId + " STATUS :: " + status);
			}
		} catch (Exception e) {
			logger.info("OperationUtillity.updateFIStatusInEXT().Exception-->" + pinstId + "  \n" + e);
		}
		return message;
	}
}
