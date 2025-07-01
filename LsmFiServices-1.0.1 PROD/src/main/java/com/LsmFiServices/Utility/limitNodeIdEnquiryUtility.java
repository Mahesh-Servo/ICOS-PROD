package com.LsmFiServices.Utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Random;
import java.util.WeakHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.LsmFiServices.dao.limitNodeIdEnquiryDao;

@Service
public class limitNodeIdEnquiryUtility {

	private static final Logger logger = LoggerFactory.getLogger(limitNodeIdEnquiryUtility.class);

	@Autowired
	limitNodeIdEnquiryDao limitNodeIdEnquiryDao;

	@SuppressWarnings("unchecked")
	public WeakHashMap<String, String> limitNodeIdEnquiryUtilityRecur(String PINSTID, String LmtPrefix,
			String LmtSuffix, WeakHashMap<String, String> limitNodeIdEnquiryResMap) throws SQLException {

		try {
			String lastFourDigit = LmtPrefix.substring(LmtPrefix.indexOf("-") + 2, LmtPrefix.length());
			String UpdatedLastFourDigit = "";
			if (limitNodeIdEnquiryResMap.get("Status").equals("SUCCESS")) {
//				logger.info(
//						"[limitNodeIdEnquiryUtility].[limitNodeIdEnquiryUtility()].[do()].[Success] \n Pinstid() :: "
//								+ PINSTID + ",LmtPrefix :: " + LmtPrefix);

				limitNodeIdEnquiryResMap = limitNodeIdEnquiryDao.limitNodeIdEnquiryDaoImlp(PINSTID, LmtPrefix,
						LmtSuffix);

				logger.info("[limitNodeIdEnquiryUtility].[limitNodeIdEnquiryUtility()].[Pinstid()] :: " + PINSTID
						+ "\n [limitNodeIdEnquiryResMap] " + limitNodeIdEnquiryResMap);

				if (limitNodeIdEnquiryResMap.get("Status").equals("SUCCESS")) {

					int hiffenIndex = LmtPrefix.indexOf("-") + 2;
					lastFourDigit = generaterandomLmtPrefix();
					LmtPrefix = LmtPrefix.replaceAll(LmtPrefix.substring(hiffenIndex, LmtPrefix.length()),
							lastFourDigit);

					limitNodeIdEnquiryResMap = limitNodeIdEnquiryUtilityRecur(PINSTID, LmtPrefix, LmtSuffix,
							limitNodeIdEnquiryResMap);
				}
			}

			if (!lastFourDigit.equals(limitNodeIdEnquiryResMap.get(UpdatedLastFourDigit))
					&& (limitNodeIdEnquiryResMap.get("Status").equals("FAILURE")
							|| limitNodeIdEnquiryResMap.get("Status").equals("FAILED"))) {
				logger.info(
						"[limitNodeIdEnquiryUtility].[limitNodeIdEnquiryUtility()].[do()].[FAILURE] \n Pinstid() :: "
								+ PINSTID + ", [lastFourDigit] :: " + lastFourDigit);
				updatePrefixSuffixForLimitNode(PINSTID, lastFourDigit);
				limitNodeIdEnquiryResMap.put("UpdatedLastFourDigit", lastFourDigit);
				limitNodeIdEnquiryResMap.put("limitNodeId", "UPDATED");
				limitNodeIdEnquiryResMap.put("Status", "SUCCESS");
				try {
					OperationUtillity.API_RequestResponse_Insert(limitNodeIdEnquiryResMap.get("soapRequest"),limitNodeIdEnquiryResMap.get("limitNodeIdEnquiryResponse"),"LIMIT NODE ENQUIRY: " + LmtPrefix + "-" + LmtSuffix, PINSTID, limitNodeIdEnquiryResMap,
							"");
				} catch (Exception e) {
					logger.info(
							"[limitNodeIdEnquiryUtility].[limitNodeIdEnquiryUtility()] Exception parsing API_REQ_RES_map to string-->\n"+ OperationUtillity.traceException(e));
				}

				return limitNodeIdEnquiryResMap;
			}

			if (lastFourDigit.equals(limitNodeIdEnquiryResMap.get(UpdatedLastFourDigit))) {
				limitNodeIdEnquiryResMap.put("limitNodeId", "UPDATED");
				limitNodeIdEnquiryResMap.put("Status", "SUCCESS");
			} else {
				limitNodeIdEnquiryResMap.put("Status", "FAILED");
			}
		} catch (Exception ex) {
			logger.info("[Exception].limitNodeIdEnquiryUtility.limitNodeIdEnquiryUtility().Pinstid() :: "+OperationUtillity.traceException(ex));
		}
		logger.info("returning limitNodeIdEnquiryResMap   -------->" + Optional.of(limitNodeIdEnquiryResMap));

		return limitNodeIdEnquiryResMap;
	}

	private void updatePrefixSuffixForLimitNode(String PINSTID, String lastFourDigit) {
//		logger.info("[limitNodeIdEnquiryUtility].[limitNodeIdEnquiryUtility()].[updatePrefixSuffixForLimitNode]\n"
//				+ " PINSTID " + PINSTID + " [lastFourDigit] =  " + lastFourDigit);

		// updating limit facility prefix and suffix
		String limitTabquery = "select ANSWER,QUESTION_ID,PINSTID,QUESTION,FACILITY_NAME from lsm_limit_answers where pinstid=? and QUESTION in ('Limit Prefix')";
		String homeTabQuery = "select ANSWER,QUESTION_ID,PINSTID,QUESTION  from lsm_ms_answers where pinstid=? and QUESTION in ('Limit Prefix')";

		try (Connection con = DBConnect.getConnection()) {
			con.setAutoCommit(false);

			PreparedStatement pst = null;
			pst = con.prepareStatement(limitTabquery);
			pst.setString(1, PINSTID);

			try (ResultSet rs = pst.executeQuery()) {

				String updateQuery = "UPDATE lsm_limit_answers SET ANSWER = ? WHERE PINSTID=? AND QUESTION_ID=? AND FACILITY_NAME=?  ";
				PreparedStatement pstupdate = con.prepareStatement(updateQuery);

				while (rs.next()) {
					String OriginalLmtPrefix = rs.getString("ANSWER");
					String questionId = rs.getString("QUESTION_ID");
					String facilityName = rs.getString("FACILITY_NAME");

					int hiffenIndex = OriginalLmtPrefix.indexOf("-") + 2;
//					lastFourDigit = generaterandomLmtPrefix();
					String newLmtPrefix = OriginalLmtPrefix.replaceAll(
							OriginalLmtPrefix.substring(hiffenIndex, OriginalLmtPrefix.length()), lastFourDigit);

					pstupdate.setString(1, newLmtPrefix);
					pstupdate.setString(2, PINSTID);
					pstupdate.setString(3, questionId);
					pstupdate.setString(4, facilityName);
					pstupdate.addBatch();
				}
				int[] i = pstupdate.executeBatch();
//				logger.info(
//						"[limitNodeIdEnquiryUtility].[limitNodeIdEnquiryUtility()].[updatePrefixSuffixForLimitNode].[BatchUpdated]\n"
//								+ " PINSTID " + PINSTID + " [lastFourDigit] =  " + lastFourDigit
//								+ " [Limit Tab BATCH SIZE] = " + String.valueOf(i.length));
			}

			if (pst != null) {
				pst = null;
			}

			pst = con.prepareStatement(homeTabQuery);
			pst.setString(1, PINSTID);
			try (ResultSet rs = pst.executeQuery()) {

				String updateHomeTabQuery = "UPDATE lsm_ms_answers  SET ANSWER = ? WHERE PINSTID=? AND QUESTION_ID=? ";
				PreparedStatement pstupdate = con.prepareStatement(updateHomeTabQuery);

				while (rs.next()) {
					String OriginalLmtPrefix = rs.getString("ANSWER");
					String questionId = rs.getString("QUESTION_ID");
					String question = rs.getString("QUESTION");

					int hiffenIndex = OriginalLmtPrefix.indexOf("-") + 2;
//					lastFourDigit = generaterandomLmtPrefix();
					String newLmtPrefix = OriginalLmtPrefix.replaceAll(
							OriginalLmtPrefix.substring(hiffenIndex, OriginalLmtPrefix.length()), lastFourDigit);

					pstupdate.setString(1, newLmtPrefix);
					pstupdate.setString(2, PINSTID);
					pstupdate.setString(3, questionId);
				}
				int i = pstupdate.executeUpdate();
//				logger.info(
//						"[limitNodeIdEnquiryUtility].[limitNodeIdEnquiryUtility()].[updatePrefixSuffixForLimitNode].[HomeTab]\n"
//								+ " PINSTID " + PINSTID + " [lastFourDigit] =  " + lastFourDigit + " [Home Tab SIZE] = "
//								+ String.valueOf(i));
			} finally {
				if (pst != null) {
					pst.close();
				}
				con.commit();
				con.close();
			}

		} catch (SQLException ex) {
			logger.info(
					"[Exception].limitNodeIdEnquiryUtility].[limitNodeIdEnquiryUtility()].[updatePrefixSuffixForLimitNode()].[Pinstid()] :: "
							+ ex.getMessage());
		} finally {

		}

	}

	private String generaterandomLmtPrefix() {

		Random random = new Random();
		String randomLmtPrefix = String.format("%04d", random.nextInt(10000));

		return randomLmtPrefix;
	}

}
