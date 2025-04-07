package com.svt.utils.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.svt.utils.dataConnectivity.dbConnection;

@Repository
public class CommonDataUtility {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CommonDataUtility.class);

	public static String getCollateralCode(String typeOfSecurity) {
		String collateralCode = "";

		String queryForCollateralCode = "SELECT DISTINCT ICORE_CODE AS COLLATERAL_CODE FROM UNICORE_MS_SUBTYPE_SVT WHERE SUB_TYPE_SECURITY_SVT = ? ";
		try (Connection con = dbConnection.getConnection()) {
			try (PreparedStatement CltrlCdPs = con.prepareStatement(queryForCollateralCode);) {
				CltrlCdPs.setString(1, typeOfSecurity);
				try (ResultSet CltrlCdRs = CltrlCdPs.executeQuery()) {
					while (CltrlCdRs.next()) {
						collateralCode = OperationUtillity.NullReplace(CltrlCdRs.getString("COLLATERAL_CODE"));
					}
				}
			}
		} catch (Exception ex) {
			logger.info("CommonDataUtility.getCollateralCode()" + OperationUtillity.traceException(typeOfSecurity, ex));
		}
		return collateralCode;
	}

	public static void updateMonitoringExtFiStatus(String pinstid) {
		String failServiceCountQuery = "SELECT COUNT(STATUS) AS FAIL_COUNT FROM LSM_FI_EXECUTION_DETAILS WHERE PINSTID = ? AND UPPER(STATUS) LIKE 'F%' AND UPPER(SERVICE_NAME) != ?";
		try (Connection con = dbConnection.getConnection()) {
			try (PreparedStatement pstmt = con.prepareStatement(failServiceCountQuery);) {
				pstmt.setString(1, pinstid);
				pstmt.setString(2, "CRM_SERVICE");
				try (ResultSet rs = pstmt.executeQuery()) {
					if (rs.next()) {
						con.setAutoCommit(false);
						String fiServiceStatus = "";
						if (rs.getInt("FAIL_COUNT") > 0) {
							fiServiceStatus = "F";
						} else if (rs.getInt("FAIL_COUNT") == 0) {
							fiServiceStatus = "S";
						}
						if (!"".equals(fiServiceStatus) && !fiServiceStatus.isEmpty()) {

							String updateFiStatusExt = "UPDATE ICOS_MONT_EXT SET FI_SERVICE_STATUS = ? WHERE PINSTID = ?";
							try (PreparedStatement ps = con.prepareStatement(updateFiStatusExt);) {

								ps.setString(1, fiServiceStatus);
								ps.setString(2, pinstid);
								int i = ps.executeUpdate();
								if (i > 0) {
									con.commit();
								}
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			logger.info(OperationUtillity.traceException(pinstid, ex));
		}
	}
}
