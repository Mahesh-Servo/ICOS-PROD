package com.svt.utils.common;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.xml.soap.SOAPMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.svt.utils.dataConnectivity.dbConnection;

@Repository
public class OperationUtillity {

	private static final Logger logger = LoggerFactory.getLogger(OperationUtillity.class);

	public static String customDateFormat(String inputDate) {
		// String inputDate = "2024-12-05"; // example input date in yyyy-MM-dd format
		String outputDate = "";
		// Define the input date format (yyyy-MM-dd)
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
		// Define the output date format (dd-MM-yyyy)
		SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
		if (!NullReplace(inputDate).equals("")) {
			try {
				Date date = inputFormat.parse(inputDate);
				outputDate = outputFormat.format(date);
			} catch (ParseException e) {
				logger.info("OperationUtillity.customDateFormat()" + traceException(inputDate, e));
			}
		}
		return outputDate;
	}

	public String getuserName(String pinstid) throws SQLException {

		String username = null;

		try (Connection con = dbConnection.getConnection();) {
			if (con == null) {
				username = "username not fetched due to error in establishing connection";
				return username;
			} else {
				String lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("USER_NAME");
				PreparedStatement statement = con.prepareStatement(lsql);
				statement.setString(1, pinstid);
				ResultSet rs = statement.executeQuery();

				while (rs.next()) {
					username = rs.getString("LOCKEDBYNAME");
				}
				if (statement != null) {
					statement.close();
				}
				if (rs != null) {
					rs.close();
				}
				if (con != null) {
					con.close();
				}
			}
		} catch (Exception e) {
			logger.info("OperationUtillity.getuserName().Exception\n" + OperationUtillity.traceException(pinstid, e));
		} finally {
		}
		return username;
	}

	// common for All
	public static Map<String, String> API_RequestResponse_Insert(String request, String account_Opening_Response,
			String req_type, String PINSTID, Map<String, String> API_REQ_RES_map, String requestUUID)
			throws SQLException {

		Map<String, String> EXTData = new HashMap<>();
		Connection con = null;
		PreparedStatement statement = null;
		String lsql = null;

		try {

			con = dbConnection.getConnection();
			if (con == null) {
				EXTData.put("Result", "Fail");
				EXTData.put("Message", "DB connection not established");
				return EXTData;
			} else {
				con.setAutoCommit(false);
				lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("API_REQ_RESP_INSERT");

				statement = con.prepareStatement(lsql);
				statement.setString(1, NullReplace(PINSTID));
				statement.setString(2, NullReplace(request));
				statement.setString(3, NullReplace(account_Opening_Response));
				statement.setString(4, NullReplace(req_type));
				statement.setString(5, NullReplace(API_REQ_RES_map.get("Status")));
				statement.setString(6, NullReplace(API_REQ_RES_map.get("ErrorDesc")));
				int int_count = statement.executeUpdate();

				if (int_count > 0) {

					// insrtLtstRcrdForLsmSrvic(String request, String accOpeningRes, String
					// reqType, String pinstId,
					// Map<String, String> apiReqRes, String requestUuid, Connection con)
					EXTData.put("LsmSrvcLtstRowCnt", insrtLtstRcrdForLsmSrvic(request, account_Opening_Response,
							req_type, PINSTID, API_REQ_RES_map, requestUUID, con).toString());
					con.commit();
				}
				if (statement != null) {
					statement.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			con.rollback();
			logger.info(
					"\nOperationUtillity.API_RequestResponse_Insert()" + OperationUtillity.traceException(PINSTID, e));
		} finally {
			if (statement != null) {
				statement.close();
			}
			if (con != null) {
				con.close();
			}
		}
		return EXTData;
	}

	public static Map<String, String> insertFiReqResMonitoring(String request, String account_Opening_Response,
			String req_type, String PINSTID, Map<String, String> API_REQ_RES_map, String requestUUID)
			throws SQLException {

		Map<String, String> EXTData = new HashMap<>();
		Connection con = null;
		PreparedStatement statement = null;
		String lsql = null;

		try {

			con = dbConnection.getConnection();
			if (con == null) {
				EXTData.put("Result", "Fail");
				EXTData.put("Message", "DB connection not established");
				return EXTData;
			} else {
				con.setAutoCommit(false);
				lsql = "INSERT INTO MONT_SERVICE_REQ_RES "
						+ "(PINSTID, REQUEST_MESSAGE, RESPONSE_MESSSAGE, REQUESTTYPE, STATUS, DATETIME, RECORDID, MESSAGE) "
						+ "VALUES (?,?,?,?,?,SYSDATE, AUTO_INCREMENT_RECORDID.nextval,?)";

				statement = con.prepareStatement(lsql);
				statement.setString(1, NullReplace(PINSTID));
				statement.setString(2, NullReplace(request));
				statement.setString(3, NullReplace(account_Opening_Response));
				statement.setString(4, NullReplace(req_type));
				statement.setString(5, NullReplace(API_REQ_RES_map.get("Status")));
				statement.setString(6, NullReplace(API_REQ_RES_map.get("ErrorDesc")));
				int int_count = statement.executeUpdate();

				if (int_count > 0) {
					EXTData.put("LsmSrvcLtstRowCnt", insrtLtstRcrdForMonitoringSrvic(request, account_Opening_Response,
							req_type, PINSTID, API_REQ_RES_map, requestUUID, con).toString());
					con.commit();
				}
				if (statement != null) {
					statement.close();
				}
			}
		} catch (Exception e) {
			con.rollback();
			e.printStackTrace();
			logger.info("OperationUtillity.insertFiReqResMonitoring().exception "
					+ OperationUtillity.traceException(PINSTID, e));
		} finally {
			if (statement != null) {
				statement.close();
			}
			if (con != null) {
				con.close();
			}
		}
		return EXTData;
	}

	public static Object insrtLtstRcrdForMonitoringSrvic(String request, String accOpeningRes, String reqType,
			String pinstId, Map<String, String> apiReqRes, String requestUuid, Connection con) throws SQLException {

		String insrtQuery = "INSERT INTO MONT_SERVICE_LTST_REQ_RES "
				+ "(PINSTID, REQUEST_MESSAGE, RESPONSE_MESSSAGE, REQUESTTYPE, STATUS, DATETIME, RECORDID, MESSAGE) "
				+ "VALUES (?,?,?,?,?,SYSDATE, AUTO_INCREMENT_RECORDID.nextval,?)";
		String deleteQuery = "DELETE FROM MONT_SERVICE_LTST_REQ_RES WHERE PINSTID=? AND REQUESTTYPE=?";

		if (con == null) {
			return "connection not established";
		} else {
//				con.setAutoCommit(false);

			try (PreparedStatement pstmt = con.prepareStatement(deleteQuery)) {
				pstmt.setString(1, pinstId);
				pstmt.setString(2, reqType);
				pstmt.executeUpdate();
			} catch (Exception e) {
				logger.info("\nOperationUtillity.insrtLtstRcrdForLsmSrvic().deleteQuery : "
						+ OperationUtillity.traceException(pinstId, e));
			}

			try (PreparedStatement statement = con.prepareStatement(insrtQuery)) {
				statement.setString(1, NullReplace(pinstId));
				statement.setString(2, NullReplace(request));
				statement.setString(3, NullReplace(accOpeningRes));
				statement.setString(4, NullReplace(reqType));
				statement.setString(5, NullReplace(apiReqRes.get("Status")));
				statement.setString(6, NullReplace(apiReqRes.get("ErrorDesc")));
				int intCount = statement.executeUpdate();

				if (intCount > 0) {
//						con.commit();
					return "" + intCount + " row inserted for " + request + "";
				}

			} catch (Exception e) {
//					con.rollback();
				e.printStackTrace();
				logger.info("\nOperationUtillity.insrtLtstRcrdForLsmSrvic().insrtQuery : "
						+ OperationUtillity.traceException(pinstId, e));
			}
		}
		return "";
	}

	public static String soapMessageToString(SOAPMessage message) {

		String result = null;
		if (message != null) {
			ByteArrayOutputStream baos = null;
			try {
				baos = new ByteArrayOutputStream();
				message.writeTo(baos);
				result = baos.toString();
			} catch (Exception e) {
//					System.out.println("soapMessageToString Error " + e.printStackTrace());
				logger.info("soapMessageToString closing connection Error " + traceException(e));
			} finally {
				if (baos != null) {
					try {
						baos.close();
					} catch (Exception e) {
						logger.info("soapMessageToString closing connection Error " + traceException(e));
						System.out.println("soapMessageToString closing connection Error ");
					}
				}
			}
		}
		System.out.println("OperationUtillity.soapMessageToString()");
		return result;
	}

	public static Object insrtLtstRcrdForLsmSrvic(String request, String accOpeningRes, String reqType, String pinstId,
			Map<String, String> apiReqRes, String requestUuid, Connection con) throws SQLException {

		String insrtQuery = "INSERT INTO LSM_SERVICE_LATEST_REQ_RES "
				+ "(PINSTID, REQUEST_MESSAGE, RESPONSE_MESSSAGE, REQUESTTYPE, STATUS, DATETIME, RECORDID, MESSAGE) "
				+ "VALUES (?,?,?,?,?,SYSDATE, AUTO_INCREMENT_RECORDID.nextval,?)";
		String deleteQuery = "DELETE FROM LSM_SERVICE_LATEST_REQ_RES WHERE PINSTID=? AND REQUESTTYPE=?";

		if (con == null) {
			return "connection not established";
		} else {
//				con.setAutoCommit(false);
			try (PreparedStatement pstmt = con.prepareStatement(deleteQuery)) {
				pstmt.setString(1, pinstId);
				pstmt.setString(2, reqType);
				pstmt.executeUpdate();
			} catch (Exception e) {
				e.printStackTrace();
				logger.info("\nOperationUtillity.insrtLtstRcrdForLsmSrvic().deleteQuery : "
						+ OperationUtillity.traceException(pinstId, e));
			}

			try (PreparedStatement statement = con.prepareStatement(insrtQuery)) {
				statement.setString(1, NullReplace(pinstId));
				statement.setString(2, NullReplace(request));
				statement.setString(3, NullReplace(accOpeningRes));
				statement.setString(4, NullReplace(reqType));
				statement.setString(5, NullReplace(apiReqRes.get("Status")));

				if (NullReplace(reqType).contains("FEE RECOVERY")
						&& NullReplace(apiReqRes.get("Status")).equals("SUCCESS")) {
					statement.setString(6, NullReplace(apiReqRes.get("Response1")));
				} else if (NullReplace(reqType).contains("FEE RECOVERY")
						&& NullReplace(apiReqRes.get("Status")).equals("FAILED")) {
					statement.setString(6, NullReplace(apiReqRes.get("Response1")));
				} else {
					statement.setString(6, NullReplace(apiReqRes.get("ErrorDesc")));
				}
				int intCount = statement.executeUpdate();

				if (intCount > 0) {
//						con.commit();
					return "" + intCount + " row inserted for " + request + "";
				}

			} catch (Exception e) {
//					con.rollback();
				e.printStackTrace();
				logger.info("\nOperationUtillity.insrtLtstRcrdForLsmSrvic().insrtQuery : "
						+ OperationUtillity.traceException(pinstId, e));
			}
		}
		return "";
	}

	public static String NullReplace(String value) {
		try {
			if (value == "null" || value == null) {
				value = "";
			}
		} catch (Exception ex) {
			value = "0";
			ex.printStackTrace();
			System.out.println("NullReplace Error " + traceException(ex));
		}
		return value;
	}

	public static String traceException(Exception exception) {
		StringBuilder stackTrace = new StringBuilder();
		stackTrace.append(System.lineSeparator());
		stackTrace.append(System.lineSeparator() + "EXCEPTION :");
		stackTrace.append(System.lineSeparator() + "Message : " + exception.getMessage());
		StackTraceElement[] elementArr = exception.getStackTrace();
		
		for (StackTraceElement element : elementArr) {
			if (element.getClassName().startsWith("com.svt")) {
				stackTrace.append(System.lineSeparator()).append("Class: ").append(element.getClassName())
						.append(System.lineSeparator()).append("Method: ").append(element.getMethodName())
						.append(System.lineSeparator()).append("Line: ").append(element.getLineNumber())
						.append(System.lineSeparator());
				break;
			}
		}
//		stackTrace.append(System.lineSeparator());
		return stackTrace.toString();
	}

	public static String traceException(String pinstid, Exception exception) {
		StringBuilder stackTrace = new StringBuilder();
		stackTrace.append(System.lineSeparator());
		stackTrace.append(System.lineSeparator() + "EXCEPTION [PINSTID =" + pinstid + "]");
		stackTrace.append(System.lineSeparator() + "Message : " + exception.getMessage());
//		StackTraceElement[] elementArr = Thread.currentThread().getStackTrace();
		StackTraceElement[] elementArr = exception.getStackTrace();

		for (StackTraceElement element : elementArr) {
			if (element.getClassName().startsWith("com.svt")) {
				stackTrace.append(System.lineSeparator()).append("Class: ").append(element.getClassName())
						.append(System.lineSeparator()).append("Method: ").append(element.getMethodName())
						.append(System.lineSeparator()).append("Line: ").append(element.getLineNumber())
						.append(System.lineSeparator());
				break;
			}
		}
		return stackTrace.toString();
	}
	
	public static String getSystemDateFormat() {
		String customFormatedDate = "";
		LocalDate date = LocalDate.now();
		DateTimeFormatter formater = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		customFormatedDate = date.format(formater);
		return customFormatedDate; // dd-MM-yyyy
	}
}
