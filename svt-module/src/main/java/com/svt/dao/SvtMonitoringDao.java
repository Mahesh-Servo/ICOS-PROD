//package com.svt.dao;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Repository;
//import com.svt.utils.common.OperationUtillity;
//import com.svt.dao.CommonDaoForInqDlnkWtdrwl;
//import com.svt.controllers.InquiryController;
//import com.svt.model.commonModel.InnerPojo;
//import com.svt.model.commonModel.MainPojo;
//import com.svt.model.commonModel.ServiceExecutionDetails;
//import com.svt.utils.common.commonUtility;
//import com.svt.utils.dataConnectivity.dbConnection;
//
//@Repository
//public class SvtMonitoringDao {
//
//	@Autowired
//	commonUtility common;
//
//	@Autowired
//	InquiryController inquiryController;
//
//	@Autowired
//	CommonDaoForInqDlnkWtdrwl commonDaoForInqDlnkWtdrwl;
//	
//	@Autowired
//	InquiryDao inquiryDao;
//
//	private static final Logger logger = LoggerFactory.getLogger(SvtMonitoringDao.class);
//
//	public List<ServiceExecutionDetails> prepareExecutionForSVTMON(String pinstid,
//			String processName, String subTypeSecurity,String limitType) throws SQLException {
//
//		logger.info("SvtMonitoringDao.prepareExecutionForSVTMON().pinstid [" + pinstid + "] START ");
//
//		List<ServiceExecutionDetails> listOfPojo = new ArrayList<>();
//
//		if (!("First Limit setup".toUpperCase().equalsIgnoreCase(limitType)) && !("First Limit set up".toUpperCase().equalsIgnoreCase(limitType))) {  //MODIFICATION FOR RENEWAL CASE
//			logger.info(" if case is renewal--" + limitType);
//			
//			Map<String, String[]> limitData = new HashMap<String, String[]>();
//			
//			int count_inq = 0;
//			try (Connection con = dbConnection.getConnection()) {
//			     limitData =SvtMonitoringDao.fetchLimitPrefixSuffix(pinstid,processName);
//			}
//			
//			for (Map.Entry<String, String[]> entry : limitData.entrySet()) {
//	            String facilityname = entry.getKey();
//	            String[] pref_suff = entry.getValue();
//	           
//			List<Map<String, String>> listOfCC = inquiryDao.checkInquiryDetailsInMasterNew(pinstid,processName,pref_suff,facilityname);
//
//			logger.info("\n[110]ServiceDetails.svtModuleCommon().pinstid(" + pinstid + ").listOfCC = "+ listOfCC.toString());
//			
//			for (Map<String, String> map : listOfCC) {
//
//				if (map.containsValue("FAILED")) {
//					listOfPojo.add(new ServiceExecutionDetails(pinstid, "SVT INQUIRY : "+ facilityname +" : " + pref_suff[0].trim() + "/" + pref_suff[1].trim(),
//							"SVT INQUIRY", "SVT INQUIRY : "+ facilityname +" : " + pref_suff[0].trim() + "/" + pref_suff[1].trim(), null, null, null,
//							"All Child services must success", null, commonUtility.createRequestUUID()));
//				}
//				if (map.containsValue("SUCCESS")) {
//					if(count_inq==0) {
//					listOfPojo.add(new ServiceExecutionDetails(pinstid, "SVT INQUIRY : "+ facilityname +" : " + pref_suff[0].trim() + "/" + pref_suff[1].trim(),
//							"SVT INQUIRY", "SVT INQUIRY : "+ facilityname +" : " + pref_suff[0].trim() + "/" + pref_suff[1].trim(), null, null, null,
//							"All Child services must success", null, commonUtility.createRequestUUID()));
//					}
//					listOfPojo.add(new ServiceExecutionDetails(pinstid, "SVT DELINK",
//							"SVT DELINK : "+ facilityname +" : " + map.get("COLLATERAL_ID"),
//							"SVT DELINK : "+ facilityname +" : " + map.get("COLLATERAL_ID"), null, null, null,
//							"All Child services must success", null, commonUtility.createRequestUUID()));
//					listOfPojo.add(new ServiceExecutionDetails(pinstid, "SVT WITHDRAWAL",
//							"SVT WITHDRAWAL : "+ facilityname +" : "  + map.get("SEC_TYPE") + " : " + map.get("COLLATERAL_ID"),
//							"SVT WITHDRAWAL : "+ facilityname +" : "  + map.get("SEC_TYPE") + " : " + map.get("COLLATERAL_ID"), null, null, null,
//							"All Child services must success", null, commonUtility.createRequestUUID()));
//				}
//				count_inq++;
//			}
//		 }
//		}
//		
//		ArrayList<MainPojo> mainPojoList = commonDaoForInqDlnkWtdrwl.fetchCommonData(pinstid, processName);
//
//		if (subTypeSecurity.equals("ALL")) {
//
//			for (MainPojo mainPojo : mainPojoList) {
//
//				if ("Yes".equalsIgnoreCase(mainPojo.getSecurity_Created())) {
//
//					for (InnerPojo innerPojo : mainPojo.getInnerPojo()) {
//
//						String requestUuid = commonUtility.createRequestUUID();
//						String dateAndTime = commonUtility.dateFormat();
//
//						mainPojo.setSubTypeSecurity(innerPojo.getSubTypeSecurity());
//						mainPojo.setTypeOfSecurity(innerPojo.getTypeOfSecurity());
//						mainPojo.setProduct(innerPojo.getProduct());
//						mainPojo.setLimitPrefix(innerPojo.getLimitPrefix());
//						mainPojo.setLimitSuffix(innerPojo.getLimitSuffix());
//						mainPojo.setCollateralId("");
//						mainPojo.setRequestId(requestUuid);
//						mainPojo.setDateAndTime(dateAndTime);
//						mainPojo.setPolicy_No(innerPojo.getPolicyNumber());
//
//						logger.info("\n[100]SvtMonitoringDao.prepareExecutionForSVTMON().pinstid(" + pinstid
//								+ ").mainPojo[InnerPojo] = " + mainPojo.toString());
//						
//						String rqstType = "";
//						if (mainPojo.getSubTypeSecurity().equalsIgnoreCase("Other")
//								|| mainPojo.getSubTypeSecurity().equalsIgnoreCase("Others")) {
//							mainPojo.setSubTypeSecurity("Other");
//						}
//
//						if (mainPojo.getSubTypeSecurity().equalsIgnoreCase("LIFE_INSURANCE")
//								|| mainPojo.getSubTypeSecurity().equalsIgnoreCase("Mutual_funds_Units")) {
//
//							rqstType = OperationUtillity.NullReplace(mainPojo.getSecurityName()) + " : "
//									+ OperationUtillity.NullReplace(mainPojo.getSubTypeSecurity()) + " : "
//									+ OperationUtillity.NullReplace(mainPojo.getTypeOfSecurity()) + " : "
//									+ OperationUtillity.NullReplace(mainPojo.getProduct()) + " : "
//									+ OperationUtillity.NullReplace(mainPojo.getPolicy_No());
//
//						} else {
//
//							rqstType = OperationUtillity.NullReplace(mainPojo.getSecurityName()) + " : "
//									+ OperationUtillity.NullReplace(mainPojo.getSubTypeSecurity()) + " : "
//									+ OperationUtillity.NullReplace(mainPojo.getTypeOfSecurity()) + " : "
//									+ OperationUtillity.NullReplace(mainPojo.getProduct());
//						}
//					   logger.info("SvtMonitoringDao.prepareExecutionForSVTMON().pinstid(" + pinstid + ").rqstType = " + rqstType);
//
//					   if (!("First Limit setup".toUpperCase().equalsIgnoreCase(limitType)) && !("First Limit set up".toUpperCase().equalsIgnoreCase(limitType))) {  //MODIFICATION FOR RENEWAL CASE
//							listOfPojo.add(new ServiceExecutionDetails(pinstid, "LODGE COLLATERAL",
//									"LODGE COLLATERAL : " + rqstType, "LODGE COLLATERAL : " + rqstType, null, null, null,
//									"All Child services must success", null, commonUtility.createRequestUUID()));
//							listOfPojo.add(new ServiceExecutionDetails(pinstid, "LODGE COLLATERAL LINKAGE",
//									"LODGE COLLATERAL LINKAGE : " + rqstType, "LODGE COLLATERAL LINKAGE : " + rqstType,
//									null, null, null, "All Child services must success", null,
//									commonUtility.createRequestUUID()));
//					   } else {
//							   if(!checkIsExecutedOnceLC(pinstid,rqstType) && !checkIsExecutedOnceLCInLsm(pinstid,rqstType)) {
//							   listOfPojo.add(new ServiceExecutionDetails(pinstid, "LODGE COLLATERAL",
//										"LODGE COLLATERAL : " + rqstType, "LODGE COLLATERAL : " + rqstType, null, null, null,
//										"All Child services must success", null, commonUtility.createRequestUUID()));
//								listOfPojo.add(new ServiceExecutionDetails(pinstid, "LODGE COLLATERAL LINKAGE",
//										"LODGE COLLATERAL LINKAGE : " + rqstType, "LODGE COLLATERAL LINKAGE : " + rqstType,
//										null, null, null, "All Child services must success", null,
//										commonUtility.createRequestUUID()));
//							   }
//					      }
//						}
//					}
//				}
//			}
//		
//		logger.info("SvtMonitoringDao.prepareExecutionForSVTMON().pinstid [" + pinstid + "] Final  listOfPojo "+ listOfPojo);
//		return listOfPojo;
//	}
//
//	public void insertMonFiExecutionData(String pinstid, List<ServiceExecutionDetails> listOfPojo,String limitType) {
//		Boolean flag = false;
//		int[] afftectedRows = {};
//		int afftectedRows1 = 0;
//		
//		if (listOfPojo.size() > 0) {
//			
//			if (!("First Limit setup".toUpperCase().equalsIgnoreCase(limitType))
//					&& !("First Limit set up".toUpperCase().equalsIgnoreCase(limitType))) { // MODIFICATION FOR RENEWAL 05032025
//		
//				try (Connection con = dbConnection.getConnection();
//					PreparedStatement ps1 = con.prepareStatement(
//							"DELETE FROM MON_FI_EXECUTION_DETAILS WHERE PINSTID = ?")) {
//				ps1.setString(1, pinstid);
//			 afftectedRows1  = ps1.executeUpdate();
//			
//			logger.info("SvtMonitoringDao.insertMonFiExecutionData().pinstid(" + pinstid + ").afftectedRows1["
//					+ afftectedRows1 + "]");
//			
//			}  catch (Exception e) {
//				logger.info("SvtMonitoringDao.insertMonFiExecutionData() " + OperationUtillity.traceException(e));
//			}
//           }
//
//			try (Connection con = dbConnection.getConnection();
//					PreparedStatement ps1 = con.prepareStatement(
//							"INSERT INTO MON_FI_EXECUTION_DETAILS (PINSTID,SERVICE_NAME,REQUEST_TYPE,FACILITY,ACCOUNT_NUMBER,REQUEST,RESPONSE,STATUS,MESSAGE,DATETIME,REQUEST_UUID) VALUES (?,?,?,?,?,?,?,?,?,SYSDATE,?)")) {
//				for (ServiceExecutionDetails ipDetails : listOfPojo) {
//
//					logger.info("SvtMonitoringDao.prepareExecutionForSVTMON().pinstid(" + ipDetails.getPinstId()
//							+ ").facility(" + ipDetails.getFacility() + ").ServiceName(" + ipDetails.getServiceName()
//							+ ").RequestType(" + ipDetails.getRequestType() + ")");
//
//					ps1.setString(1, ipDetails.getPinstId());
//					ps1.setString(2, ipDetails.getServiceName());
//					ps1.setString(3, ipDetails.getRequestType());
//					ps1.setString(4, ipDetails.getFacility());
//					ps1.setString(5, ipDetails.getAccountNumber());
//					ps1.setString(6, ipDetails.getRequest());
//					ps1.setString(7, ipDetails.getResponse());
//					ps1.setString(8, ipDetails.getStatus());
//					ps1.setString(9, ipDetails.getMessage());
//					ps1.setString(10, ipDetails.getRequestuuid());
//					ps1.addBatch();
//				}
//				afftectedRows = ps1.executeBatch();
//				if (afftectedRows.length > 0) {
//					con.commit();
//				}
//			} catch (Exception e) {
//				logger.info("SvtMonitoringDao.insertMonFiExecutionData() ERROR [" + OperationUtillity.traceException(e));
//			}
//
//			logger.info("SvtMonitoringDao.insertMonFiExecutionData().pinstid(" + pinstid + ").afftectedRows["
//					+ afftectedRows + "]");
//		}
//	}
//	
//	
//	public Boolean checkIsExecutedOnceLC(String pinstid,String reqType) {
//		Boolean flag = false;
//		int exeCount =0;
//		try (Connection con = dbConnection.getConnection()) {
//			String IsExecutedOnceQuery = "";
//			IsExecutedOnceQuery = "SELECT COUNT(1) AS CNT FROM MON_FI_EXECUTION_DETAILS WHERE PINSTID = ?  AND REQUEST_TYPE  LIKE ?  ";
//			PreparedStatement pst = con.prepareStatement(IsExecutedOnceQuery);
//			pst.setString(1, pinstid);
//			pst.setString(2, "%"+reqType);
//			try (ResultSet rs = pst.executeQuery()) {
//				if (rs.next()) {
//					exeCount = rs.getInt("CNT");
//				}
//			} catch (Exception ex) {
//				ex.printStackTrace();
//				logger.info("CommonDaoForInqDlnkWtdrwl.checkIsExecutedOnce()" + OperationUtillity.traceException(pinstid, ex));
//			}
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			logger.info("CommonDaoForInqDlnkWtdrwl.checkIsExecutedOnce()" + OperationUtillity.traceException(pinstid, ex));
//		}
//		logger.info("CommonDaoForInqDlnkWtdrwl.checkIsExecutedOnce().exeCount [" + exeCount + "]");
//		if(exeCount > 0) {
//			flag = true;
//		}
//		return flag;
//	}
//	
//	public Boolean checkIsExecutedOnceLCInLsm(String pinstid,String reqType) {
//		Boolean flag = false;
//		int exeCount =0;
//		String newPinstid = pinstid.replace("MON","LSM");
//		try (Connection con = dbConnection.getConnection()) {
//			String IsExecutedOnceQuery = "";
//			IsExecutedOnceQuery = "SELECT COUNT(1) AS CNT FROM LSM_FI_EXECUTION_DETAILS WHERE PINSTID = ?  AND REQUEST_TYPE  LIKE ?  ";
//			PreparedStatement pst = con.prepareStatement(IsExecutedOnceQuery);
//			pst.setString(1, newPinstid);
//			pst.setString(2, "%"+reqType);
//			try (ResultSet rs = pst.executeQuery()) {
//				if (rs.next()) {
//					exeCount = rs.getInt("CNT");
//				}
//			} catch (Exception ex) {
//				ex.printStackTrace();
//				logger.info("CommonDaoForInqDlnkWtdrwl.checkIsExecutedOnce()" + OperationUtillity.traceException(pinstid, ex));
//			}
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			logger.info("CommonDaoForInqDlnkWtdrwl.checkIsExecutedOnce()" + OperationUtillity.traceException(pinstid, ex));
//		}
//		logger.info("CommonDaoForInqDlnkWtdrwl.checkIsExecutedOnce().exeCount [" + exeCount + "]");
//		if(exeCount > 0) {
//			flag = true;
//		}
//		return flag;
//	}
//	
//    public String getLimitType(String pinstid,String processName) {
//    	String limitType = "";
//    	String pinstidnew = "";
//		if("Monitoring".equalsIgnoreCase(processName)) {
//			pinstidnew =pinstid.replace("MON","LSM");
//		} else if("Limit_Setup".equalsIgnoreCase(processName)){  
//			pinstidnew =pinstid;
//		}
//    	
//    	try (Connection con = dbConnection.getConnection();
//    		PreparedStatement pst = con.prepareStatement(
//    			"SELECT UPPER(ANSWER) AS  ANSWER FROM LSM_MS_ANSWERS WHERE PINSTID = ? AND TAB_NAME = 'Home_Tab' AND QUESTION = 'Limit Type'");) {
//    	    pst.setString(1, pinstidnew);
//    	    try (ResultSet rs = pst.executeQuery()) {
//    		while (rs.next()) {
//    			limitType = OperationUtillity.NullReplace(rs.getString("ANSWER"));
//    		}
//    	    }
//    	} catch (SQLException e) {
//    	    logger.info("ParentLimitNodeModificationUtility.getProposalType(): " + OperationUtillity.traceException(e));
//    	}
//    	return limitType;
//        }
//    
//public static Map<String, String[]> fetchLimitPrefixSuffix(String pinstid,String processName) {
//		
//		String pinstidnew = "";
//		if("Monitoring".equalsIgnoreCase(processName)) {
//			pinstidnew =pinstid.replace("MON","LSM");
//		} else if("Limit_Setup".equalsIgnoreCase(processName)){  
//			pinstidnew =pinstid;
//		}
//
//		Map<String, String[]> limitData = new HashMap<String, String[]>();
//		try (Connection con = dbConnection.getConnection()) {
//		try (PreparedStatement pstmt = con.prepareStatement(
//				"SELECT listagg(ANSWER,',' ) within group (order by question) AS PREFIX_SUFFIX ,FACILITY_NAME"
//						+ " FROM LSM_LIMIT_ANSWERS where PINSTID = ? "
//						+ "and QUESTION in ('Limit Prefix','Limit Suffix' ) group by facility_name ");) {
//			pstmt.setString(1, pinstidnew);
//			try (ResultSet rs = pstmt.executeQuery()) {
//				while (rs.next()) {
//
//					String[] prfxSffx = null;
//					if (OperationUtillity.NullReplace(rs.getString("PREFIX_SUFFIX")) != "" && OperationUtillity.NullReplace(rs.getString("PREFIX_SUFFIX")).contains(",")) {
//						prfxSffx = OperationUtillity.NullReplace(rs.getString("PREFIX_SUFFIX")).split(",");
//						limitData.put(OperationUtillity.NullReplace(rs.getString("FACILITY_NAME")), prfxSffx);
//					}
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.info("\nCommonDaoForInqDlnkWtdrwl.fetchLimitPrefixSuffix() pp "
//					+ OperationUtillity.traceException(pinstid, e));
//		}
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.info("\nCommonDaoForInqDlnkWtdrwl.fetchLimitPrefixSuffix()"
//					+ OperationUtillity.traceException(pinstid, e));
//		}
//		return limitData;
//	}
//
//public static Map<String, String[]> fetchLimitPrefixSuffixInd(String pinstid,String processName,String facility) {
//	
//	String pinstidnew = "";
//	if("Monitoring".equalsIgnoreCase(processName)) {
//		pinstidnew =pinstid.replace("MON","LSM");
//	} else if("Limit_Setup".equalsIgnoreCase(processName)){  
//		pinstidnew =pinstid;
//	}
//
//	Map<String, String[]> limitData = new HashMap<String, String[]>();
//	try (Connection con = dbConnection.getConnection()) {
//	try (PreparedStatement pstmt = con.prepareStatement(
//			"SELECT listagg(ANSWER,',' ) within group (order by question) AS PREFIX_SUFFIX ,FACILITY_NAME"
//					+ " FROM LSM_LIMIT_ANSWERS where PINSTID = ? "
//					+ "and QUESTION in ('Limit Prefix','Limit Suffix' )AND facility_name = ? group by facility_name ");) {
//		pstmt.setString(1, pinstidnew);
//		pstmt.setString(2, facility);
//		try (ResultSet rs = pstmt.executeQuery()) {
//			while (rs.next()) {
//
//				String[] prfxSffx = null;
//				if (OperationUtillity.NullReplace(rs.getString("PREFIX_SUFFIX")) != "" && OperationUtillity.NullReplace(rs.getString("PREFIX_SUFFIX")).contains(",")) {
//					prfxSffx = OperationUtillity.NullReplace(rs.getString("PREFIX_SUFFIX")).split(",");
//					limitData.put(OperationUtillity.NullReplace(rs.getString("FACILITY_NAME")), prfxSffx);
//				}
//			}
//		}
//	} catch (Exception e) {
//		e.printStackTrace();
//		logger.info("\nCommonDaoForInqDlnkWtdrwl.fetchLimitPrefixSuffix() pp "
//				+ OperationUtillity.traceException(pinstid, e));
//	}
//	} catch (Exception e) {
//		e.printStackTrace();
//		logger.info("\nCommonDaoForInqDlnkWtdrwl.fetchLimitPrefixSuffix()"
//				+ OperationUtillity.traceException(pinstid, e));
//	}
//	return limitData;
//}
//
//public static Boolean checkIsDW(String pinstid) {
//	Boolean flag = true;
//	int exeCount =0;
//	Map<String, String> dwmap = new HashMap<String, String>();
//	try (Connection con = dbConnection.getConnection()) {
//		String IsExecutedOnceQuery = "";
//		IsExecutedOnceQuery = "SELECT COUNT(1) AS CNT FROM LSM_FI_EXECUTION_DETAILS WHERE PINSTID = ?  AND (REQUEST_TYPE  LIKE ?  OR  REQUEST_TYPE  LIKE ?)";
//		PreparedStatement pst = con.prepareStatement(IsExecutedOnceQuery);
//		pst.setString(1, pinstid);
//		pst.setString(2, "SVT DELINK%");
//		pst.setString(3, "SVT WITHDRAWAL%");
//		try (ResultSet rs = pst.executeQuery()) {
//			if (rs.next()) {
//				exeCount = rs.getInt("CNT");
//			}
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			logger.info("CommonDaoForInqDlnkWtdrwl.checkIsDW()" + OperationUtillity.traceException(pinstid, ex));
//		}
//	} catch (Exception ex) {
//		ex.printStackTrace();
//		logger.info("CommonDaoForInqDlnkWtdrwl.checkIsDW()" + OperationUtillity.traceException(pinstid, ex));
//	}
//	logger.info("CommonDaoForInqDlnkWtdrwl.checkIsDW().exeCount [" + exeCount + "]");
//	
//	if(exeCount > 0) {
//		
//		try (Connection con = dbConnection.getConnection()) {
//			String IsExecutedOnceQuery = "";
//			IsExecutedOnceQuery = "SELECT REQUEST_TYPE,STATUS FROM LSM_FI_EXECUTION_DETAILS WHERE PINSTID = ?  AND (REQUEST_TYPE  LIKE ?  OR  REQUEST_TYPE  LIKE ?) AND STATUS != ?";
//			PreparedStatement pst = con.prepareStatement(IsExecutedOnceQuery);
//			pst.setString(1, pinstid);
//			pst.setString(2, "SVT DELINK%");
//			pst.setString(3, "SVT WITHDRAWAL%");
//			pst.setString(4, "SUCCESS");
//			try (ResultSet rs = pst.executeQuery()) {
//				while (rs.next()) {
//					dwmap.put(rs.getString("REQUEST_TYPE"),rs.getString("STATUS"));
//				}
//			} catch (Exception ex) {
//				ex.printStackTrace();
//				logger.info("CommonDaoForInqDlnkWtdrwl.checkIsDW()" + OperationUtillity.traceException(pinstid, ex));
//			}
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			logger.info("CommonDaoForInqDlnkWtdrwl.checkIsDW()" + OperationUtillity.traceException(pinstid, ex));
//		}
//	}
//	logger.info("CommonDaoForInqDlnkWtdrwl.checkIsDW() Final Map :: " + dwmap.toString());
//	
//	if(exeCount > 0 && dwmap.size() > 0 ) {
//		flag = false;
//	}
//	logger.info("CommonDaoForInqDlnkWtdrwl.checkIsDW() Final flag :: " + flag);
//	return flag;
//}
//	
//}
