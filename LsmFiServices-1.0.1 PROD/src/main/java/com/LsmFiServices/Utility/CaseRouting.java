package com.LsmFiServices.Utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CaseRouting {

	private static final Logger logger = LoggerFactory.getLogger(CaseRouting.class);

	public boolean getDecisionForFI(String pinstid) {
		List<String> statusList = new ArrayList<>();
		try (Connection con = DBConnect.getConnection();
				PreparedStatement pst = con.prepareStatement(Queries.GET_STATUS_FROM_EXECUTION_TABLE);) {
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
//			String deleteSchedulerPinstId = OperationUtillity.deleteSchedulerPinstId(pinstid);
//			logger.info("CaseRouting.getDecisionForFI().deleteSchedulerPinstId --> " + deleteSchedulerPinstId);
		}
		return !containsKeywords;

//		if (!statusList.contains("FAILURE") && !statusList.contains("FAILED") && !statusList.contains("F")
//				&& !statusList.contains("Request Sent...!") && !statusList.contains("") && 0 <= statusList.size()) {
//			return true;
//		} else {
//			return false;
//		}
	}

	public String updateFIStatusInEXT(String pinstId) {
		String status = "F";
		String message = "";
		if (getDecisionForFI(pinstId)) {
			status = "S";
		}
		int afftectedRows = 0;
		try (Connection con = DBConnect.getConnection();
				PreparedStatement ps = con.prepareStatement(Queries.UPDATE_STATUS_IN_EXT);) {
			ps.setString(1, status);
			ps.setString(2, pinstId);
			afftectedRows = ps.executeUpdate();
			if (afftectedRows > 0) {
				con.commit();
				logger.info("Status updated in EXT");
			} else {
				logger.info("Status not updated in EXT");
			}
		} catch (Exception e) {
			logger.info("OperationUtillity.updateFIStatusInEXT().Exception-->" + pinstId + "  \n" + e);
		}
		return message;
	}
}
