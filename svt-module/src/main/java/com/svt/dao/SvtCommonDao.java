package com.svt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.svt.model.commonModel.InnerPojo;
import com.svt.model.commonModel.MainPojo;
import com.svt.utils.common.OperationUtillity;
import com.svt.utils.dataConnectivity.dbConnection;

@Component
public class SvtCommonDao {
	private static final Logger logger = LoggerFactory.getLogger(SvtCommonDao.class);

	

	public static Map<String, String[]> fetchLimitPrefixSuffix(String pinstid, Connection con) {

		Map<String, String[]> limitData = new HashMap<String, String[]>();
		try (PreparedStatement pstmt = con.prepareStatement(
				"SELECT listagg(ANSWER,',' ) within group (order by question) AS PREFIX_SUFFIX ,FACILITY_NAME"
						+ " FROM LSM_LIMIT_ANSWERS where PINSTID = ? "
						+ "and QUESTION in ('Limit Prefix','Limit Suffix' ) group by facility_name ");) {
			pstmt.setString(1, pinstid);

			try (ResultSet rs = pstmt.executeQuery()) {

				while (rs.next()) {
					String[] prfxSffx = new String[3];
					prfxSffx[0] = OperationUtillity.NullReplace(rs.getString("PREFIX_SUFFIX")).split(",")[0];
					prfxSffx[1] = OperationUtillity.NullReplace(rs.getString("PREFIX_SUFFIX")).split(",")[1];
					limitData.put(OperationUtillity.NullReplace(rs.getString("FACILITY_NAME")), prfxSffx);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("LsmFiServices.SvtUnlinkDao.fetchLimitPrefixSuffix()"
					+ OperationUtillity.traceException(pinstid, e));
		}
		return limitData;
	}

	@SuppressWarnings("unchecked")
	public Boolean checkForRenewalCase(String pinstid, String processName) {
		Boolean flag = false;
		try (Connection con = dbConnection.getConnection()) {
			String checkRenewalQuery = "";
			switch (processName) {
			case "Monitoring":
				checkRenewalQuery = "";
				break;

			case "Limit_Setup":
				checkRenewalQuery = "SELECT PROPOSALTYPE FROM LIMIT_SETUP_EXT WHERE PINSTID = ? AND PROPOSALTYPE LIKE '%Renewal%'";
				break;
			}

			PreparedStatement pst = con.prepareStatement(checkRenewalQuery);
			pst.setString(1, pinstid);

			try (ResultSet rs = pst.executeQuery()) {
				if (rs.next()) {
					flag = true;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				logger.info("SvtCommonDao.checkRenewal()" + OperationUtillity.traceException(pinstid, ex));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.info("SvtCommonDao.checkRenewal()" + OperationUtillity.traceException(pinstid, ex));
		}
		return flag;
	}

}
