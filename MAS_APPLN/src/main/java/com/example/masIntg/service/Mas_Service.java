package com.example.masIntg.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringJoiner;

import javax.naming.NamingException;

import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.masIntg.DBFetch.ProgramName;
import com.example.masIntg.entity.Memo_Entity;
import com.example.masIntg.utility.DBConnect;
import com.example.masIntg.utility.ReadPropertyFIle;
import com.google.gson.Gson;
import com.servo.error.InvalidStatusException;
import com.servo.error.SRVStatus;
import com.servo.output.Complex;
import com.servo.output.ConnectionInfo;
import com.servo.output.ProcessInstanceInfo;
import com.servo.output.StoredStatement;
import com.servo.service.*;

import ejb.connection.EJBContext;

@Service
public class Mas_Service {

	private static final Logger logger = LoggerFactory.getLogger(Mas_Service.class);
	
	@Autowired
	ProgramName pname;

	PreparedStatement statement = null;
	ResultSet rs = null;

	String MemoCount = "";

	private SRVActivityService activitySer;

	public Map<String, String> API_RequestResponse_Insert(String PINSTID, String request, String Response,
			String ServiceName) throws SQLException {

		Map<String, String> EXTData = new HashMap();

		PreparedStatement statement = null;
		String lsql = null;

		try (Connection con = DBConnect.getConnection();) {
			if (con == null) {
				EXTData.put("Result", "Fail");
				EXTData.put("Message", "DB connection not established");
				return EXTData;
			} else {
				lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MCA_REQ_RESP_INSERT");
				statement = con.prepareStatement(lsql);
				statement.setString(1, NullReplace(PINSTID));
				statement.setString(2, NullReplace(request));
				statement.setString(3, NullReplace(ServiceName));
				statement.setString(4, NullReplace(Response));
				int int_count = statement.executeUpdate();

				if (int_count > 0) {
					con.commit();
				}
				if (statement != null) {
					statement.close();
				}
			}

		} catch (Exception e) {
			logger.info("Exception in Class-->Mas_Service--> method name --> API_RequestResponse_Insert--> pinstid --> "
					+ PINSTID + "--->" + e.getMessage());
		}
		return EXTData;

	}

	public Map<String, Integer> GetMemoCountForApprlPending(String EMP_ID) throws SQLException {
		logger.info("METHOD INSIDE SERVICE ----> GetMemoCountForApprlPending");
		Map<String, Integer> MemoCount = new HashMap<>();

		Integer MCA_MemoCount1 = null;
		Integer MCA_MemoCount2 = null;
		Integer MCA_MemoCount3 = null;
		Integer MCA_MemoCount4 = null;
		Integer MCA_MemoCount7 = 0;  //very IMP
		Integer sum = null;
		String lsql = null;
		String lsql1 = null; // added for marvel
		String lsql2 = null; // added for slice

		try (Connection con = DBConnect.getConnection();) {

			if (con == null) {
				logger.info("Connection null in MEMO COUNT--> ");
			} else {

				lsql = "SELECT COUNT(1) FROM SMEAG_USERMAPPING WHERE USERID = ?"
						+ " and decision is null and type_approver = ? and  pinstid in (select A.processinstanceid from srv_ru_execution A,UNICORE_EXT B where A.processinstanceid = b.pinstid and a.activityname = ?"
						+ " and b.APPROVER_FILTER like ?)";
				logger.info("1ST API Query--- MCA_MemoCount FOR UNICORE--> " + lsql);
				statement = con.prepareStatement(lsql);
				statement.setString(1, EMP_ID);
				statement.setString(2, "New");
				statement.setString(3, "Pending For Approval");
				statement.setString(4, "%" + EMP_ID + "%");
				rs = statement.executeQuery();

				while (rs.next()) {
					MCA_MemoCount1 = rs.getInt("COUNT(1)");
					logger.info("TOTAL COUNT FOR UNICORE IDs---> " + MCA_MemoCount1);
				}

				// added for marvel (01-08-2023)

				lsql1 = "SELECT COUNT(1) FROM SMEAG_USERMAPPING WHERE USERID = ?"
						+ " and decision is null and type_approver = ? and  pinstid in (select A.processinstanceid from srv_ru_execution A,MCG_EXT B where A.processinstanceid = b.pinstid and a.activityname = ? "
						+ "and b.APPROVER_FILTER like ? and LIVESTATUS = ?)";
				logger.info("1ST API Query ---MCA_MemoCount FOR MARVEL--> " + lsql1);
				statement = con.prepareStatement(lsql1);
				statement.setString(1, EMP_ID);
				statement.setString(2, "New");
				statement.setString(3, "Pending_for_Approval");
				statement.setString(4, "%" + EMP_ID + "%");
				statement.setString(5, "Y");
				rs = statement.executeQuery();

				while (rs.next()) {
					MCA_MemoCount2 = rs.getInt("COUNT(1)");
					logger.info("TOTAL COUNT FOR MARVEL IDs---> " + MCA_MemoCount2);

				}

				// added for SLICE-LIVE (30-08-2023)

				lsql2 = "SELECT COUNT(1) FROM SMEAG_USERMAPPING WHERE USERID = ?"
						+ " and decision is null and type_approver = ? and  pinstid in (select A.processinstanceid from srv_ru_execution A,SMEAG_EXT B where A.processinstanceid = b.pinstid and a.activityname = ? "
						+ "and b.APPROVER_FILTER like ?)";
				logger.info("1ST API Query---MCA_MemoCount FOR SLICE-LIVE --> " + lsql2);
				statement = con.prepareStatement(lsql2);
				statement.setString(1, EMP_ID);
				statement.setString(2, "New");
				statement.setString(3, "Pending_for_approval");
				statement.setString(4, "%" + EMP_ID + "%");
				rs = statement.executeQuery();

				while (rs.next()) {
					MCA_MemoCount3 = rs.getInt("COUNT(1)");
					logger.info("TOTAL COUNT FOR SLICE-LIVE  IDs---> " + MCA_MemoCount3);
				}

				// LCBD
				lsql = "SELECT COUNT(1) FROM SMEAG_USERMAPPING  WHERE USERID = ? and decision is null and APPROVER_ACCESS_TYPE != ? AND TYPE_APPROVER = ? and pinstid in (select A.processinstanceid from srv_ru_execution A,LCBD_EXT B where A.processinstanceid = b.pinstid and a.activityname = ? and b.APPROVER_FILTER like ?)";
				statement = con.prepareStatement(lsql);
				statement.setString(1, EMP_ID);
				statement.setString(2, "INFO");
				statement.setString(3, "New");
				statement.setString(4, "Pending_for_Approval");
				statement.setString(5, "%" + EMP_ID + "%");
				rs = statement.executeQuery();

				while (rs.next()) {
					MCA_MemoCount4 = Integer.valueOf(rs.getInt("COUNT(1)"));
				}
				logger.info("TOTAL COUNT FOR LCBD  IDs---> " + MCA_MemoCount4);

				// ONBOARDING
				Map<String, String> atmap = pname.GetActiveTab(EMP_ID);
				for (Map.Entry<String,String> at :  atmap.entrySet()) {
					if ("DOC_Initiate".equalsIgnoreCase(at.getValue()) || "RM_INITIATE".equalsIgnoreCase(at.getValue())) {
						logger.info("when its initiate for api 1---> "+at.getKey());
						lsql = "SELECT COUNT(1) FROM ICOS_BASIC_DETAILS WHERE PINSTID = ? AND RHS_ID = ? and RHS_DECISION is null and RHS_APPROVER_ACCESS_TYPE = ?";

					} else {
						logger.info("when its DOC SUMMERY for api 1---> "+at.getKey());
						lsql = "SELECT COUNT(1) FROM ICOS_BASIC_DETAILS WHERE PINSTID = ? AND RHS_ID = ? and RHS_DECISION_DOC is null and RHS_APPROVER_ACCESS_TYPE_DOC = ?";
					}

					logger.info("query for ONBOARDING COUNT---> " + lsql);
					statement = con.prepareStatement(lsql);
					statement.setString(1,at.getKey());
					statement.setString(2, EMP_ID);
					statement.setString(3, "Pending_For_Approval");
//					statement.setString(4, "Pending with RHS");
					rs = statement.executeQuery();

					while (rs.next()) {
						logger.info("TOTAL COUNT FOR inside while---> " + MCA_MemoCount7);
						MCA_MemoCount7 = MCA_MemoCount7 + rs.getInt("COUNT(1)");
						logger.info("TOTAL COUNT FOR MCA_MemoCount7---> " + MCA_MemoCount7);

					}
				}
				logger.info("TOTAL COUNT FOR ONBOARDING---> " + MCA_MemoCount7);

				// amit_b added to append total count
				sum = MCA_MemoCount1 + MCA_MemoCount2 + MCA_MemoCount3 + MCA_MemoCount4 + MCA_MemoCount7;

				logger.info("TOTAL SUM COUNT OF UNICORE/SLICE/MARVEL/ONBOARDING---> " + sum);
				MemoCount.put("MCA_MemoCount", sum);

				if (statement != null) {
					statement.close();
				}

				if (rs != null) {
					rs.close();
				}
			}
		} catch (Exception e) {
			logger.info(
					"Exception occured in class-->Mas_Service-->method name--> GetMemoCountForApprlPending-->EMP_ID-->"
							+ EMP_ID + "-->" + e.getMessage());
		}
		return MemoCount;

	}

	public List<Memo_Entity> GetMemoListForApprlPending(String EMP_ID) throws SQLException {
		logger.info("METHOD IN ----> GetMemoListForApprlPending");
		List<Memo_Entity> list = new ArrayList<>();

		String lsql = null;
		String lsql2 = null;
		String lsql3 = null;

		try (Connection con = DBConnect.getConnection();) {

			if (con == null) {
				logger.info("conn is null for getting pending LIST--> ");
			} else {
				// LOGIC FOR UNICORE
				lsql = "SELECT PINSTID,USERNAME, TO_CHAR(DECISION_DATE,'YYYY-MM-DD') AS DECISION_DATE,DECISION,APPROVER_ACCESS_TYPE FROM SMEAG_USERMAPPING WHERE USERID = ?"
						+ " and decision is null and type_approver = ? and  pinstid in (select A.processinstanceid from srv_ru_execution A,UNICORE_EXT B where A.processinstanceid = b.pinstid and a.activityname = ?"
						+ "and b.APPROVER_FILTER like ?)";
				logger.info("2ND API Query----MCA_MemoApprlPendList FOR UNICORE--> " + lsql);
				statement = con.prepareStatement(lsql);
				statement.setString(1, EMP_ID);
				statement.setString(2, "New");
				statement.setString(3, "Pending For Approval");
				statement.setString(4, "%" + EMP_ID + "%");
				rs = statement.executeQuery();

				while (rs.next()) {
					Memo_Entity me = new Memo_Entity();
					me.setPINSTID(NullReplace(rs.getString("PINSTID")).toString());
					me.setUSERNAME(NullReplace(rs.getString("USERNAME")).toString());
					me.setDECISION_DATE(NullReplace(rs.getString("DECISION_DATE")).toString());
					me.setACTIONTAKEN(NullReplace(rs.getString("DECISION")).toString());
					me.setApproverAccessType(NullReplace(rs.getString("APPROVER_ACCESS_TYPE")).toString());
					list.add(me);
				}

				// LOGIC FOR MARVEL

				lsql2 = "SELECT PINSTID,USERNAME, TO_CHAR(DECISION_DATE,'YYYY-MM-DD') AS DECISION_DATE,DECISION,APPROVER_ACCESS_TYPE FROM SMEAG_USERMAPPING WHERE USERID = ?"
						+ " and decision is null and type_approver = ? and  pinstid in (select A.processinstanceid from srv_ru_execution A,MCG_EXT B where A.processinstanceid = b.pinstid and a.activityname = ?"
						+ "and b.APPROVER_FILTER like ? and LIVESTATUS = ?)";
				logger.info("2ND API Query--MCA_MemoApprlPendList FOR MARVEL--> " + lsql2);
				statement = con.prepareStatement(lsql2);
				statement.setString(1, EMP_ID);
				statement.setString(2, "New");
				statement.setString(3, "Pending_for_Approval");
				statement.setString(4, "%" + EMP_ID + "%");
				statement.setString(5, "Y");
				rs = statement.executeQuery();

				while (rs.next()) {
					Memo_Entity me = new Memo_Entity();
					me.setPINSTID(NullReplace(rs.getString("PINSTID")).toString());
					me.setUSERNAME(NullReplace(rs.getString("USERNAME")).toString());
					me.setDECISION_DATE(NullReplace(rs.getString("DECISION_DATE")).toString());
					me.setACTIONTAKEN(NullReplace(rs.getString("DECISION")).toString());
					me.setApproverAccessType(NullReplace(rs.getString("APPROVER_ACCESS_TYPE")).toString());
					list.add(me);
				}

				// LOGIC FOR SLICE_LIVE
				lsql3 = "SELECT PINSTID,USERNAME, TO_CHAR(DECISION_DATE,'YYYY-MM-DD') AS DECISION_DATE,DECISION,APPROVER_ACCESS_TYPE FROM SMEAG_USERMAPPING WHERE USERID = ?"
						+ " and decision is null and type_approver = ? and  pinstid in (select A.processinstanceid from srv_ru_execution A,SMEAG_EXT B where A.processinstanceid = b.pinstid and a.activityname = ?"
						+ " and b.APPROVER_FILTER like ?)";
				logger.info("2ND API Query---MCA_MemoApprlPendList FOR SLICE_LIVE--> " + lsql3);
				statement = con.prepareStatement(lsql3);
				statement.setString(1, EMP_ID);
				statement.setString(2, "New");
				statement.setString(3, "Pending_for_approval");
				statement.setString(4, "%" + EMP_ID + "%");
				rs = statement.executeQuery();

				while (rs.next()) {
					Memo_Entity me = new Memo_Entity();
					me.setPINSTID(NullReplace(rs.getString("PINSTID")).toString());
					me.setUSERNAME(NullReplace(rs.getString("USERNAME")).toString());
					me.setDECISION_DATE(NullReplace(rs.getString("DECISION_DATE")).toString());
					me.setACTIONTAKEN(NullReplace(rs.getString("DECISION")).toString());
					me.setApproverAccessType(NullReplace(rs.getString("APPROVER_ACCESS_TYPE")).toString());
					list.add(me);
				}

				logger.info("2nd API--- FOR LCBD--> ");

				lsql = "SELECT PINSTID,USERNAME, TO_CHAR(DECISION_DATE,'YYYY-MM-DD') AS DECISION_DATE,DECISION,APPROVER_ACCESS_TYPE FROM SMEAG_USERMAPPING  WHERE USERID = ? and decision is null and TYPE_APPROVER = ? AND APPROVER_ACCESS_TYPE != ? and pinstid in (select A.processinstanceid from srv_ru_execution A,LCBD_EXT B where A.processinstanceid = b.pinstid and a.activityname = ? and b.APPROVER_FILTER like ?)";

				statement = con.prepareStatement(lsql);
				statement.setString(1, EMP_ID);
				statement.setString(2, "New");
				statement.setString(3, "INFO");
				statement.setString(4, "Pending_for_Approval");
				statement.setString(5, "%" + EMP_ID + "%");
				rs = statement.executeQuery();

				while (rs.next()) {
					Memo_Entity me = new Memo_Entity();
					me.setPINSTID(NullReplace(rs.getString("PINSTID")).toString());
					me.setUSERNAME(NullReplace(rs.getString("USERNAME")).toString());
					me.setDECISION_DATE(NullReplace(rs.getString("DECISION_DATE")).toString());
					me.setACTIONTAKEN(NullReplace(rs.getString("DECISION")).toString());
					me.setApproverAccessType(NullReplace(rs.getString("APPROVER_ACCESS_TYPE")).toString());
					list.add(me);
				}

				// ONBOARDING - LIVE

				logger.info(" 2nd API --FOR ONBOARDING--> ");
				Map<String, String> atmap = pname.GetActiveTab(EMP_ID);
				for (Map.Entry<String,String> at :  atmap.entrySet()) {
					if ("DOC_Initiate".equalsIgnoreCase(at.getValue()) || "RM_INITIATE".equalsIgnoreCase(at.getValue())) {
						lsql = "SELECT PINSTID,RHS_NAME,TO_CHAR(RHS_DECISION_DATE,'YYYY-MM-DD') AS RHS_DECISION_DATE,RHS_DECISION,RHS_APPROVER_ACCESS_TYPE FROM ICOS_BASIC_DETAILS WHERE PINSTID = ? AND RHS_ID = ? and RHS_DECISION is null and RHS_APPROVER_ACCESS_TYPE = ?";

						statement = con.prepareStatement(lsql);
						statement.setString(1,at.getKey());
						statement.setString(2, EMP_ID);
						statement.setString(3, "Pending_For_Approval");
//						statement.setString(4, "Pending with RHS");
						rs = statement.executeQuery();

						while (rs.next()) {
							Memo_Entity me = new Memo_Entity();
							me.setPINSTID(NullReplace(rs.getString("PINSTID")).toString());
							me.setUSERNAME(NullReplace(rs.getString("RHS_NAME")).toString());
							me.setDECISION_DATE(NullReplace(rs.getString("RHS_DECISION_DATE")).toString());
							me.setACTIONTAKEN(NullReplace(rs.getString("RHS_DECISION")).toString());
							me.setApproverAccessType(NullReplace(rs.getString("RHS_APPROVER_ACCESS_TYPE")).toString());
							list.add(me);
						}

					} else {
						lsql = "SELECT PINSTID,RHS_NAME,TO_CHAR(RHS_DECISION_DATE,'YYYY-MM-DD') AS RHS_DECISION_DATE,RHS_DECISION_DOC,RHS_APPROVER_ACCESS_TYPE_DOC FROM ICOS_BASIC_DETAILS WHERE PINSTID = ? AND  RHS_ID = ? and RHS_DECISION_DOC is null and RHS_APPROVER_ACCESS_TYPE_DOC = ?";

						statement = con.prepareStatement(lsql);
						statement.setString(1,at.getKey());
						statement.setString(2, EMP_ID);
						statement.setString(3, "Pending_For_Approval");
//						statement.setString(4, "Pending with RHS");
						rs = statement.executeQuery();

						while (rs.next()) {
							Memo_Entity me = new Memo_Entity();
							me.setPINSTID(NullReplace(rs.getString("PINSTID")).toString());
							me.setUSERNAME(NullReplace(rs.getString("RHS_NAME")).toString());
							me.setDECISION_DATE(NullReplace(rs.getString("RHS_DECISION_DATE")).toString());
							me.setACTIONTAKEN(NullReplace(rs.getString("RHS_DECISION_DOC")).toString());
							me.setApproverAccessType(
									NullReplace(rs.getString("RHS_APPROVER_ACCESS_TYPE_DOC")).toString());
							list.add(me);
						}
					}
				}
				
				logger.info("MCA_MemoApprlPendList --> " + list);
				if (statement != null) {
					statement.close();
				}

				if (rs != null) {
					rs.close();
				}
			}
		} catch (Exception e) {

			logger.info("Exception in class-->Mas_Service-->method -->GetMemoCountForApprlPending-->pinstid -->"
					+ EMP_ID + "Exception is --->" + e.getMessage());
		}
		return list;

	}

	// amit_Babil-added(14-3-2023)
	public List<Memo_Entity> GetPendingMemoDetails(String EMP_ID, String APP_SR_NO, String SEARCH_VALUE)
			throws SQLException {
		logger.info("METHOD IN ----> GetPendingMemoDetails");
		List<Memo_Entity> list = new ArrayList<>();

		String lsql = null;

		try (Connection con = DBConnect.getConnection();) {

			if (con == null) {
				logger.info("conn is null for --> GetPendingMemoDetails");
			} else {
				if (!(APP_SR_NO == null || APP_SR_NO == "")) {
					logger.info("executing 4th API when username is OBTAINED in APP_SR_NO parameter --> ");
					lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MCA_GetPendingMemoDetails_APP");
				} else {
					logger.info("executing 4th API when username is NOT OBTAINED in APP_SR_NO parameter--> ");
					lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MCA_GetPendingMemoDetails");
				}
				statement = con.prepareStatement(lsql);
				statement.setString(1, SEARCH_VALUE);
				if (!(APP_SR_NO == null || APP_SR_NO == "")) {
					logger.info("when APP_SR_NO is not null --> ");
					statement.setString(2, APP_SR_NO);
				} else {
					statement.setString(2, EMP_ID); // vIMP added by mahesh sir
				}

				rs = statement.executeQuery();

				while (rs.next()) {
					Memo_Entity me = new Memo_Entity();
					me.setPINSTID(NullReplace(rs.getString("PINSTID")).toString());
					me.setUSERNAME(NullReplace(rs.getString("USERNAME")).toString());
					me.setDECISION_DATE(NullReplace(rs.getString("DECISION_DATE")).toString());
					me.setACTIONTAKEN(NullReplace(rs.getString("DECISION")).toString());
					me.setApproverAccessType(NullReplace(rs.getString("APPROVER_ACCESS_TYPE")).toString());
					list.add(me);
				}
				if (statement != null) {
					statement.close();
				}

				if (rs != null) {
					rs.close();
				}
			}
		} catch (Exception e) {

			logger.info("Exception in class-->Mas_Service-->method name -->GetPendingMemoDetails-->pinstid-->"
					+ SEARCH_VALUE + "---->" + e.getMessage());
		}
		return list;

	}
	// amit_babil-ended

	// amit-babil-added(16-3-2023)
	public Boolean GetPendingMemoDetailsOnAction(String EMP_ID, String APP_SR_NO, String pinstId, String Action,
			String Remarks, String processKey) throws SQLException, InvalidStatusException {
		logger.info("METHOD IN ----> GetPendingMemoDetailsOnAction");
		Boolean flag = false;
		List<Memo_Entity> list = new ArrayList<>();
		String lsql1 = null;
		String responsevalue = "";
		String activityId = "";
		Integer activityId1 = 0;
		String username = "";
		String istreamsIP = "";
		String istreamsPort = "";
		EJBContext con1 = new EJBContext();
		SRVActivityService activitySer = null;

		try (Connection con = DBConnect.getConnection();) {
			Properties pro=new Properties();
			FileInputStream inp=new FileInputStream(System.getProperty("user.dir")+File.separator+"DMSConfig"+File.separator+"ConfigProperty.properties");
			if(inp==null) {
				logger.info("Proerty not found====> " );
			}else {
				pro.load(inp);
				istreamsIP = pro.getProperty("istreamsIP");
				istreamsPort = pro.getProperty("istreamsPort");
				inp.close();
			}
			if (con == null) {
				logger.info("conn is null for GetPendingMemoDetailsOnAction-->");
			} else {
				if (pinstId.contains("UNI")) {
					lsql1 = ReadPropertyFIle.getInstance().getPropConst()
							.getProperty("MCA_UpdatePendingMemoDetailsOnActionNew");
					logger.info("query for unicore for ActivityID  ----> " + lsql1);

					statement = con.prepareStatement(lsql1);
					statement.setString(1, pinstId);
					rs = statement.executeQuery();
					if (rs.next()) {
						activityId = rs.getString("ACTIVITYID");
					}
					logger.info("GETTING UNICORE ACTIVITYID FOR MAS ----> " + activityId);
					if (!(activityId.equals(null))) {
						activityId1 = Integer.parseInt(activityId);
					}
					rs.close();
					statement.close();
				} else if (pinstId.contains("MCG") || pinstId.contains("MRVL")) {
					String lsqlquery = ReadPropertyFIle.getInstance().getPropConst()
							.getProperty("MCA_GET_ACTIVITYID_MARVEL");
					logger.info("query for marvel for ActivityID  ----> " + lsqlquery);
					statement = con.prepareStatement(lsqlquery);
					statement.setString(1, pinstId);
					rs = statement.executeQuery();
					if (rs.next()) {
						activityId = rs.getString("ACTIVITYID");
					}
					logger.info("GETTING MARVEL ACTIVITYID FOR MAS ----> " + activityId);
					if (!(activityId.equals(null))) {
						activityId1 = Integer.parseInt(activityId);
					}
					rs.close();
					statement.close();
				} else if (pinstId.contains("SLICE") || pinstId.contains("PSN") || pinstId.contains("PRO")) {
					String lsqlquery = ReadPropertyFIle.getInstance().getPropConst()
							.getProperty("MCA_GET_ACTIVITYID_SLICE");
					logger.info("query for slice for ActivityID  ----> " + lsqlquery);
					statement = con.prepareStatement(lsqlquery);
					statement.setString(1, pinstId);
					rs = statement.executeQuery();
					if (rs.next()) {
						activityId = rs.getString("ACTIVITYID");
					}
					logger.info("GETTING SLICE ACTIVITYID FOR MAS ----> " + activityId);
					if (!(activityId.equals(null))) {
						activityId1 = Integer.parseInt(activityId);
					}
					rs.close();
					statement.close();
				}else if (pinstId.contains("LCBD"))
		          {
		             String lsqlquery = ReadPropertyFIle.getInstance().getPropConst()
		               .getProperty("MCA_GET_ACTIVITYID_MARVEL");

		             statement = con.prepareStatement(lsqlquery);
		            statement.setString(1, pinstId);
		             rs = statement.executeQuery();
		             if (rs.next()) {
		               activityId = rs.getString("ACTIVITYID");
		               logger.info("GETTING LCBD ACTIVITYID FOR MAS MARVEL--> " + activityId);
		            }

		             if (!activityId.equals(null)) {
		               activityId1 = Integer.valueOf(Integer.parseInt(activityId));
		            }
		             rs.close();
		             statement.close();
		           }

				logger.info("BEFORE SUBMITTING CASE --> ");
				logger.info("user WITH USERID  -------> " + EMP_ID);
				logger.info("istreamIP of LIVE--> "+istreamsIP);
				logger.info("istreamPort of LIVE--> "+istreamsPort);
				logger.info("PINSTID OF USER --> " + pinstId);
				logger.info("ACTIVITYID --> " + activityId1.toString());
				logger.info("Process_Name--> " + processKey);
				logger.info("decision OF USER--> " + Action);


				String sessionId = null;
				int Userid = 0;
				SRVGetSession st = new SRVGetSession();
				sessionId = Long.toString(st.getSRVSession());
				logger.info("sessionId from jar --> " + sessionId);
				String getSession = "SELECT USERID FROM SRV_GE_USERINFO WHERE UPPER(USERNAME)=?";
				try (Connection con3 = DBConnect.getConnection();PreparedStatement statement = con3.prepareStatement(getSession);) {
				statement.setString(1, "ICOS_USER"); 
				try (ResultSet rs = statement.executeQuery();) {
					if (rs.next()) {
						Userid = rs.getInt("USERID");
						logger.info("USERID OBTAINED FROM QUERY--> " + Userid);
					}
				}
				} catch (Exception ex) {
					logger.info("Exception in GetPendingMemoDetailsOnAction for userid and session id---> " + ex.fillInStackTrace());
				}
				logger.info("sessionId !!====> " + sessionId);
				logger.info("Userid !!====>" + Userid);

				logger.info(" before executing TothroughAssign Service ----->>>");
				try {

//	            URL addressURL = new URL("http://10.78.11.210:7003/UNICORE/ToThroughAssignee?ActivityId="+activityId1.toString()+"&processName="+processKey+"&userName="+EMP_ID+"&pinstid="+pinstId+"&remarks="+Remarks+"&decision="+Action);
					URL addressURL = new URL("http://"+istreamsIP+ ":" + istreamsPort
							+ "/UNICORE/ToThroughAssignee?ActivityId="
							+ activityId1.toString() + "&processName=" + processKey + "&userName=" + EMP_ID
							+ "&pinstid=" + pinstId + "&remarks=" + Remarks + "&decision=" + Action
							+ "&submitBy=MASAPI&sessionid=" + sessionId + "&userId=" + Userid);
					HttpURLConnection connection1 = (HttpURLConnection) addressURL.openConnection();
					connection1.setDoOutput(true);
					connection1.setDoInput(true);
					connection1.setRequestMethod("POST");
					connection1.setConnectTimeout(30000);
					OutputStream ost = connection1.getOutputStream();
					BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ost, "UTF-8"));
					writer.write("");
					writer.flush();
					writer.close();
					ost.close();
					int status = connection1.getResponseCode();
					logger.info("STATUS From ToThroughAssigne is--> " + status);

					BufferedReader in = new BufferedReader(new InputStreamReader(connection1.getInputStream()));
					String inputLine = "";
					while ((inputLine = in.readLine()) != null) {
						responsevalue += inputLine;
						logger.info("Response value from ToThroughAssigne is --> " + responsevalue);

					}
				} catch (Exception ex) {
					logger.info("Exception inside ToThrough Assigne Service pinstid -->" + pinstId
							+ "-->Exception  is -->" + ex.getMessage());

				}

				logger.info(" After executing TothroughAssign Service FOR MAS --->>>");
				logger.info("BEFORE EXECUTING SANCTION DATE  --> ");

				try {
					if (responsevalue.contains("Accepted") && processKey.equals("UNICORE")) {
						URL addressURL1 = new URL("http://"+istreamsIP+ ":"+ istreamsPort +"/UNICORE/AmmendatoryCal?pinstId=" + pinstId
								+ "&flag=getSanctionDateforPSN");
						HttpURLConnection connection = (HttpURLConnection) addressURL1.openConnection();
						connection.setDoOutput(true);
						connection.setDoInput(true);
						connection.setRequestMethod("POST");
						connection.setConnectTimeout(30000);
						OutputStream ost1 = connection.getOutputStream();
						BufferedWriter writer1 = new BufferedWriter(new OutputStreamWriter(ost1, "UTF-8"));
						writer1.write("");
						writer1.flush();
						writer1.close();
						ost1.close();
						int updatestatus = connection.getResponseCode();
						logger.info("STATUS IS from Ammendatory call--> " + updatestatus);

						BufferedReader inn = new BufferedReader(new InputStreamReader(connection.getInputStream()));
						String inputLine = "";
						responsevalue = "";
						while ((inputLine = inn.readLine()) != null) {
							responsevalue += inputLine;
							logger.info("Response value for sanction date of Unicore's AmendatoryCal ----> "
									+ responsevalue);

						}
					}
					flag = Boolean.TRUE;
				} catch (Exception Ex) {
					logger.info("Exception inside sanction date of Unicore's Service --->pinstid -->" + pinstId
							+ "-->Exception  is -->" + Ex.getMessage());
				}

			}

			if (statement != null) {
				statement.close();
			}

			if (rs != null) {
				rs.close();
			}

		} catch (Exception e) {		
			API_RequestResponse_Insert(EMP_ID, "GetPendingMemoDetailsOnAction method", e.getMessage(),
					"Exception in Sixth MAS Api Service");
			logger.info(
					"Exception occured in class-->Mas_Service-->method -->GetPendingMemoDetailsOnAction--->PINSTID-->"
							+ pinstId + "--->" + e.getMessage());
		}
		logger.info("METHOD OUT ----> After getting the flag --> GetPendingMemoDetailsOnAction-->" + flag);
		return flag;

	}

	// LCBD UPDATE EXT
	public int UpdateLCBD(String pinstid) {
		int countRHS = 0;
		String updateLCBD = "";
		try (Connection con = DBConnect.getConnection();) {
			updateLCBD = "update LCBD_EXT set CAL_TYPE = ? WHERE PINSTID=?";
			logger.info("UPDATE QUERY FOR LCBD_EXT AFTER ACCEPT-->");
			statement = con.prepareStatement(updateLCBD);
			statement.setString(1, "PROCESSED_CAL");
			statement.setString(2, pinstid);
			int i = statement.executeUpdate();
			logger.info("DATA UPDATED IN LCBD TABLE-->" + i);

		} catch (Exception ex) {
			logger.info("Exception in Mas Service -->Method --> UpdateLCBD--> Exception" + ex.getMessage());
		}
		return countRHS;
	}

	// to get onboarding count from icos_basic_details
	public int GetCountforRHSUser(String EMP_ID, String pinstid, int j) {
		int countRHS = 0;
		String sqllist = "";
		try (Connection con = DBConnect.getConnection();) {

			Map<String, String> atmap = pname.GetActiveTab(EMP_ID);
			for (Map.Entry<String,String> at :  atmap.entrySet()) {
				if ("DOC_Initiate".equalsIgnoreCase(at.getValue()) || "RM_INITIATE".equalsIgnoreCase(at.getValue())) {
					logger.info("FOR INITIATE GETCOUNT METHOD-->");

					sqllist = "SELECT count(pinstid) from ICOS_BASIC_DETAILS WHERE RHS_ID = ? AND PINSTID = ?"
							+ " and RHS_DECISION is null" + " and RHS_APPROVER_ACCESS_TYPE = ?";
				} else {
					logger.info("FOR SUMMARY GETCOUNT METHOD-->");

					sqllist = "SELECT count(pinstid) from ICOS_BASIC_DETAILS WHERE RHS_ID = ? AND PINSTID = ?"
							+ " and RHS_DECISION_DOC is null" + " and RHS_APPROVER_ACCESS_TYPE_DOC = ?";
				}

				logger.info("query for ONBOARDING API NO " + j + "---> " + sqllist);
				statement = con.prepareStatement(sqllist);
				statement.setString(1, EMP_ID);
				statement.setString(2, pinstid);
				statement.setString(3, "Pending_For_Approval");
//				statement.setString(4, "Pending with RHS");

				rs = statement.executeQuery();
				while (rs.next()) {
					countRHS = countRHS + rs.getInt("count(pinstid)");
					logger.info("count of RHS ONBOARDING--> GetCountforRHSUser-->" + countRHS);

				}
			}
		} catch (Exception ex) {
			logger.info("Exception in Mas Service --> GetCountforRHSUser-for ONBOARDING->" + ex.getMessage());
		}
		return countRHS;
	}

	//
	public boolean updateRHSUser(String EMP_ID, String pinstid, String Action, String remarks) {
		Boolean flag = false;
		String updateRHS = "";
		String information = "";
		String updateTab = "";
		 String activeTab = "";
		String queryexecuted = "";
		try (Connection con = DBConnect.getConnection();) {
			logger.info("INSIDE updateRHSUser 5th API-->");

			activeTab = pname.GetActiveTabForUpdate(EMP_ID,pinstid);
			
			String sqllist = "SELECT  INITIAL_INFORMATION from ICOS_BASIC_DETAILS WHERE  PINSTID = ?";
			logger.info("query for ONBOARDING API NO  ---> " + sqllist);
		   statement = con.prepareStatement(sqllist);
		   statement.setString(1, pinstid);
		   rs = statement.executeQuery();
		   while (rs.next()) {
			information = rs.getString("INITIAL_INFORMATION");
			logger.info("information from icos_basic-->" + information);

		}
			
				if ("DOC_Initiate".equalsIgnoreCase(activeTab) || "RM_INITIATE".equalsIgnoreCase(activeTab)) {

					updateRHS = "update ICOS_BASIC_DETAILS set RHS_DECISION= ?,RHS_APPROVER_ACCESS_TYPE = ?,RHS_DECISION_DATE = SYSDATE,RHS_REMARK = ?,STATUS = ?,SENDMAIL_FLAG = ? where RHS_ID = ? AND PINSTID=?";
					logger.info("UPDATE QUERY FOR 5HT API-->");
					statement = con.prepareStatement(updateRHS);
					statement.setString(1, Action);
					if ("Accept".equalsIgnoreCase(Action)) {
						statement.setString(2, "APPROVED");
					} else {
						logger.info("if user rejects case-->");
						statement.setString(2, "NOT APPROVED");
					}
					statement.setString(3, remarks);
					if("Accept".equalsIgnoreCase(Action)) {
					statement.setString(4, "Approved by Regional Head Sales");
					}else {
						statement.setString(4, "Rejected by Regional Head Sales");	
					}
					statement.setString(5, "");
					statement.setString(6, EMP_ID);
					statement.setString(7, pinstid);
					int i = statement.executeUpdate();
					logger.info("DATA UPDATED IN RHS USER TABLE ICOS_BASIC_DETAILS ONBOARDING-->" + i);

					if ("Accept".equalsIgnoreCase(Action)) {

						updateTab = "update ICOS_CM_ONBOARD_DETAILS set ACTIVE_TAB = 'RM_SUMMARY' WHERE PINSTID=?";
						logger.info("UPDATE QUERY FOR 5HT API Sending to NEXT TRAY-->" + updateTab);
						statement = con.prepareStatement(updateTab);
						statement.setString(1, pinstid);
						int J = statement.executeUpdate();
						logger.info("tab updated to RM_SUMMERY TOWARDS ICOS_CM_ONBOARD_DETAILS -->" + J);
						queryexecuted = pname.insertToRHSAudit(pinstid, EMP_ID,
								"RHS Consent is Done Decision is Accept", activeTab);
						queryexecuted = pname.insertToRHSAudit(pinstid, EMP_ID, "Active Tab change to RM Summary",
								activeTab);

					} else {
						queryexecuted = pname.insertToRHSAudit(pinstid, EMP_ID,
								"RHS Consent is Done Decision is Reject ", activeTab);
					}

				} else {

			if("Yes".equalsIgnoreCase(information)) {
						
						updateRHS = "update ICOS_BASIC_DETAILS set RHS_DECISION_DOC= ?,RHS_APPROVER_ACCESS_TYPE_DOC = ?,RHS_DECISION_DATE_DOC = SYSDATE,RHS_REMARK_DOC = ?,STATUS = ?, SENDMAIL_DOC_FLAG = ?,INITIAL_INFORMATION = ?,CUSTOMERCONSENT = ?  where RHS_ID = ? AND PINSTID=?";
						logger.info("UPDATE QUERY FOR 5HT API-->");
						statement = con.prepareStatement(updateRHS);
						statement.setString(1, Action);
						if ("Accept".equalsIgnoreCase(Action)) {
							statement.setString(2, "APPROVED");
						} else {
							logger.info("if user rejects case-->");
							statement.setString(2, "NOT APPROVED");
						}
						statement.setString(3, remarks);
						if("Accept".equalsIgnoreCase(Action)) {
							statement.setString(4, "Approved by Regional Head Sales");
							}else {
								statement.setString(4, "Rejected by Regional Head Sales");	
							}
						statement.setString(5, "");
						statement.setString(6, "");
						statement.setString(7, "");
						statement.setString(8, EMP_ID);
						statement.setString(9, pinstid);
						int i = statement.executeUpdate();
						logger.info("DATA UPDATED IN RHS_DOC USER TABLE ICOS_BASIC_DETAILS ONBOARDING-->" + i);

						if ("Accept".equalsIgnoreCase(Action)) {
							queryexecuted = pname.insertToRHSAudit(pinstid, EMP_ID, "RHS Consent is Done Decision is Accept",
									activeTab);
						} else {
							queryexecuted = pname.insertToRHSAudit(pinstid, EMP_ID,
									"RHS Consent is Done Decision is Reject ",  activeTab);
						}
						
					}else {
						
						updateRHS = "update ICOS_BASIC_DETAILS set RHS_DECISION_DOC= ?,RHS_APPROVER_ACCESS_TYPE_DOC = ?,RHS_DECISION_DATE_DOC = SYSDATE,RHS_REMARK_DOC = ?,STATUS = ?, SENDMAIL_DOC_FLAG = ? where RHS_ID = ? AND PINSTID=?";
						logger.info("UPDATE QUERY FOR 5HT API-->");
						statement = con.prepareStatement(updateRHS);
						statement.setString(1, Action);
						if ("Accept".equalsIgnoreCase(Action)) {
							statement.setString(2, "APPROVED");
						} else {
							logger.info("if user rejects case-->");
							statement.setString(2, "NOT APPROVED");
						}
						statement.setString(3, remarks);
						if("Accept".equalsIgnoreCase(Action)) {
							statement.setString(4, "Approved by Regional Head Sales");
							}else {
								statement.setString(4, "Rejected by Regional Head Sales");	
							}
						statement.setString(5, "");
						statement.setString(6, EMP_ID);
						statement.setString(7, pinstid);
						int i = statement.executeUpdate();
						logger.info("DATA UPDATED IN RHS_DOC USER TABLE ICOS_BASIC_DETAILS ONBOARDING-->" + i);

						if ("Accept".equalsIgnoreCase(Action)) {
							queryexecuted = pname.insertToRHSAudit(pinstid, EMP_ID, "RHS Consent is Done Decision is Accept",
									activeTab);
						} else {
							queryexecuted = pname.insertToRHSAudit(pinstid, EMP_ID,
									"RHS Consent is Done Decision is Reject ",  activeTab);
						}
					}
			
					String updateSaveflag = "update ICOS_CM_ONBOARD_SAVEFLAG set DOCSUMCUSTOMERCONSET = 'N' where PINSTID = ?";
					logger.info("query for updateSaveflag   ---> " + updateSaveflag);
					statement = con.prepareStatement(updateSaveflag);
					statement.setString(1, pinstid);
					int i = statement.executeUpdate();
					logger.info("sumgst updated   ---> " + i);
				}

			flag = Boolean.TRUE;
			logger.info("Flag after updating ICOS_BASIC_DETAILS ONBOARDING-->" + flag);
		} catch (Exception ex) {
			logger.info("Exception in Mas Service ONBOARDING--> updateRHSUser-->" + ex.getMessage());
		}
		return flag;
	}

	// amit_Babil-ended
	public static String NullReplace(String value) {
		try {
			if (value == "null" || value == null) {
				value = "";
			}
		} catch (Exception ex) {
			value = "0";
			ex.printStackTrace();
			logger.info("Error occurred NullReplace " + ex.fillInStackTrace());
		}
		return value;
	}

	public static boolean CallSubmitInstance(Connection conn, String userName, String userPassword, String pinstid,
			int activityId, String processName) throws Exception {
		Long sessionId = null;
		boolean status = false;
		String istreamsIP="";
		EJBContext con = new EJBContext();
		Properties pro=new Properties();
		FileInputStream inp=new FileInputStream(System.getProperty("user.dir")+File.separator+"DMSConfig"+File.separator+"ConfigProperty.properties");
		if(inp==null) {
			logger.info("Proerty not found====> " );
		}else {
			pro.load(inp);
			istreamsIP=pro.getProperty("istreamsIP");
			inp.close();
		}
//            SRVSessionService sessionService = (SRVSessionService) con.lookup("SRVSessionServiceImpl", "SRV-RAD-EJB", "10.78.11.210", "7003");
		SRVSessionService sessionService = (SRVSessionService) con.lookup("SRVSessionServiceImpl", "SRV-RAD-EJB",
				istreamsIP, "29010");

//            ConnectionInfo crst = sessionService.connection(userName, userPassword, Character.MIN_VALUE, Character.MIN_VALUE, null, "10.78.11.210", "desktop");
		ConnectionInfo crst = sessionService.connection(userName, userPassword, Character.MIN_VALUE,
				Character.MIN_VALUE, null, istreamsIP, "desktop");
		sessionId = crst.getSessionId();
		int Userid = crst.getUserId();
		logger.info("sessionId====> " + sessionId);
		logger.info("Userid====>" + Userid);

		String lockinstanceid = "update SRV_RU_EXECUTION set LOCKEDBYNAME=?,LOCKEDBYID=?,LOCKEDTIME=sysdate where PROCESSINSTANCEID=?";
		PreparedStatement pstmt = conn.prepareStatement(lockinstanceid);
		pstmt.setString(1, userName);
		pstmt.setInt(2, Userid);
		pstmt.setString(3, pinstid);
		int countLocked = pstmt.executeUpdate();
		// update execution
		pstmt.close();

		logger.info("Total rows effected====>" + countLocked);

		status = SubmitInstance(pinstid, activityId, "Y", sessionId, processName);
		lockinstanceid = "update SRV_RU_EXECUTION set LOCKEDBYNAME='',LOCKEDBYID='',LOCKEDTIME='' where PROCESSINSTANCEID=?";
		pstmt = conn.prepareStatement(lockinstanceid);
//            pstmt.setString(1, userName);
//            pstmt.setInt(2, Userid);
		pstmt.setString(1, pinstid);
		pstmt.executeUpdate();
		// update execution
		pstmt.close();
		if (status) {
			logger.info("submitted successfully");

		} else {
			logger.info("Error in routing--");

		}

		sessionService.disconnection("oracleDB", sessionId, sessionId + "");

		return status;
	}

	public static Boolean SubmitInstance(String pinstId, int activityId, String isSubmit, Long SessionID,
			String processName) throws Exception {
		logger.info("[SRVAddMasterData] AddToTrustMaster Started");

		boolean status = Boolean.FALSE;
		String istreamsIP="";
		SRVActivityService activityService = null;
		Gson json = new Gson();
		EJBContext con = new EJBContext();
		Properties pro=new Properties();
		FileInputStream inp=new FileInputStream(System.getProperty("user.dir")+File.separator+"DMSConfig"+File.separator+"ConfigProperty.properties");
		if(inp==null) {
			logger.info("Proerty not found====> " );
		}else {
			pro.load(inp);
			istreamsIP=pro.getProperty("istreamsIP");
			inp.close();
		}
		activityService = (SRVActivityService) con.lookup("SRVActivityServiceImpl", "SRV-RAD-EJB", istreamsIP,
				"29010");
		logger.info("activityService is " + activityService);

		ProcessInstanceInfo pinfo = activityService.saveProcessInstanceDataEx("ORACLE", SessionID, processName, pinstId,
				1, activityId, isSubmit, false, "", "", null, null);
		logger.info("[DLPUtils]postIntegrationSubmitInstance: pinfo :  " + pinfo.toString());

		if (pinfo.getStatus() == SRVStatus.SUCCESS) {

			URL addressURL = new URL("http://"+istreamsIP+":29010/UNICORE/AmmendatoryCal?pinstId=" + pinstId
					+ "&flag=getSanctionDateforPSN");
			HttpURLConnection connection1 = (HttpURLConnection) addressURL.openConnection();
			connection1.setDoOutput(true);
			connection1.setDoInput(true);
			connection1.setRequestMethod("POST");
			connection1.setConnectTimeout(120000);
			OutputStream ost = connection1.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ost, "UTF-8"));
			writer.write("");
			writer.flush();
			writer.close();
			ost.close();
			int updatestatus = connection1.getResponseCode();
			logger.info("STATUS IS --> " + updatestatus);

			BufferedReader in = new BufferedReader(new InputStreamReader(connection1.getInputStream()));
			String inputLine = "";
			String responsevalue = "";
			while ((inputLine = in.readLine()) != null) {
				responsevalue += inputLine;
				logger.info("Response value for sanction date is --> " + responsevalue);

			}
			status = Boolean.TRUE;

		} else {
			status = Boolean.FALSE;
			return status;
		}

		logger.info("[SRVAddMasterData] AddToTrustMaster Finished:  ");

		return status;
	}

	public List<Memo_Entity> FilterOnSearch(String EMP_ID, String FILTER_VALUE) throws SQLException {

		logger.info("METHOD IN 3RD service----> filterOnSearch");
		List<Memo_Entity> list = new ArrayList<>();

		String lsql = null;

		try (Connection con = DBConnect.getConnection();) {

			if (con == null) {
				logger.info("DB connection not established as conn is null--> ");
			} else {
				if (FILTER_VALUE.contains("UNICORE")) {
					logger.info(" 3rd API --FOR UNICORE--> ");
					lsql = "SELECT PINSTID,USERNAME, TO_CHAR(DECISION_DATE,'YYYY-MM-DD') AS DECISION_DATE,DECISION,APPROVER_ACCESS_TYPE FROM SMEAG_USERMAPPING WHERE USERID = ?"
							+ " and decision is null and type_approver = ? and  pinstid in (select A.processinstanceid from srv_ru_execution A,UNICORE_EXT B where A.processinstanceid = b.pinstid and a.activityname = ?"
							+ " and b.APPROVER_FILTER like ?)";
					statement = con.prepareStatement(lsql);
					statement.setString(1, EMP_ID);
					statement.setString(2, "New");
					statement.setString(3, "Pending For Approval");
					statement.setString(4, "%" + EMP_ID + "%");

				} else if (FILTER_VALUE.contains("MARVEL")) {

					logger.info("3rd API--- FOR MARVEL--> ");
					lsql = "SELECT PINSTID,USERNAME, TO_CHAR(DECISION_DATE,'YYYY-MM-DD') AS DECISION_DATE,DECISION,APPROVER_ACCESS_TYPE FROM SMEAG_USERMAPPING WHERE USERID = ?"
							+ " and decision is null and type_approver = ? and  pinstid in (select A.processinstanceid from srv_ru_execution A,MCG_EXT B where A.processinstanceid = b.pinstid and a.activityname = ?"
							+ " and b.APPROVER_FILTER like ? and LIVESTATUS = ?)";
					statement = con.prepareStatement(lsql);
					statement.setString(1, EMP_ID);
					statement.setString(2, "New");
					statement.setString(3, "Pending_for_Approval");
					statement.setString(4, "%" + EMP_ID + "%");
					statement.setString(5, "Y");

				} else if (FILTER_VALUE.contains("SLICE")) {
					logger.info("3rd API--- FOR SLICE-LIVE--> ");
					lsql = "SELECT PINSTID,USERNAME, TO_CHAR(DECISION_DATE,'YYYY-MM-DD') AS DECISION_DATE,DECISION,APPROVER_ACCESS_TYPE FROM SMEAG_USERMAPPING WHERE USERID = ?"
							+ " and decision is null and type_approver = ? and  pinstid in (select A.processinstanceid from srv_ru_execution A,SMEAG_EXT B where A.processinstanceid = b.pinstid and a.activityname = ? "
							+ "and b.APPROVER_FILTER like ?)";
					statement = con.prepareStatement(lsql);
					statement.setString(1, EMP_ID);
					statement.setString(2, "New");
					statement.setString(3, "Pending_for_approval");
					statement.setString(4, "%" + EMP_ID + "%");
				}else if (FILTER_VALUE.contains("LCBD")) {
					logger.info("3rd API--- FOR LCBD--> ");
					lsql = "SELECT PINSTID,USERNAME, TO_CHAR(DECISION_DATE,'YYYY-MM-DD') AS DECISION_DATE,DECISION,APPROVER_ACCESS_TYPE FROM SMEAG_USERMAPPING  WHERE USERID = ? and decision is null and APPROVER_ACCESS_TYPE != ? and type_approver = ? and  pinstid in (select A.processinstanceid from srv_ru_execution A,LCBD_EXT B where A.processinstanceid = b.pinstid and a.activityname = ? and b.APPROVER_FILTER like ?)";

					statement = con.prepareStatement(lsql);
					statement.setString(1, EMP_ID);
					statement.setString(2, "INFO");
					statement.setString(3, "New");
					statement.setString(4, "Pending_for_Approval");
					statement.setString(5, "%" + EMP_ID + "%");
				}else if (FILTER_VALUE.contains("ONBOARDING")) {
					logger.info("3rd API--- FOR ONBOARDING--> ");

					Map<String, String> atmap = pname.GetActiveTab(EMP_ID);
					for (Map.Entry<String,String> at :  atmap.entrySet()) {
						if ("DOC_Initiate".equalsIgnoreCase(at.getValue()) || "RM_INITIATE".equalsIgnoreCase(at.getValue())) {
							logger.info("For initiate--> ");

							lsql = "SELECT PINSTID,RHS_NAME,TO_CHAR(RHS_DECISION_DATE,'YYYY-MM-DD') AS RHS_DECISION_DATE,RHS_DECISION,RHS_APPROVER_ACCESS_TYPE FROM ICOS_BASIC_DETAILS WHERE PINSTID = ? AND RHS_ID = ? and RHS_DECISION is null and RHS_APPROVER_ACCESS_TYPE = ?";
							statement = con.prepareStatement(lsql);
							statement.setString(1,at.getKey());
							statement.setString(2, EMP_ID);
							statement.setString(3, "Pending_For_Approval");
//							statement.setString(4, "Pending with RHS");
							rs = statement.executeQuery();

							while (rs.next()) {
								Memo_Entity me = new Memo_Entity();
								me.setPINSTID(NullReplace(rs.getString("PINSTID")).toString());
								me.setUSERNAME(NullReplace(rs.getString("RHS_NAME")).toString());
								me.setDECISION_DATE(NullReplace(rs.getString("RHS_DECISION_DATE")).toString());
								me.setACTIONTAKEN(NullReplace(rs.getString("RHS_DECISION")).toString());
								me.setApproverAccessType(
										NullReplace(rs.getString("RHS_APPROVER_ACCESS_TYPE")).toString());
								list.add(me);
							}
						} else {
							logger.info("for doc summary--> ");

							lsql = "SELECT PINSTID,RHS_NAME,TO_CHAR(RHS_DECISION_DATE,'YYYY-MM-DD') AS RHS_DECISION_DATE,RHS_DECISION_DOC,RHS_APPROVER_ACCESS_TYPE_DOC FROM ICOS_BASIC_DETAILS WHERE PINSTID = ? AND RHS_ID = ? and RHS_DECISION_DOC is null and RHS_APPROVER_ACCESS_TYPE_DOC = ?";
							statement = con.prepareStatement(lsql);
							statement.setString(1,at.getKey());
							statement.setString(2, EMP_ID);
							statement.setString(3, "Pending_For_Approval");
//							statement.setString(4, "Pending with RHS");
							rs = statement.executeQuery();

							while (rs.next()) {
								Memo_Entity me = new Memo_Entity();
								me.setPINSTID(NullReplace(rs.getString("PINSTID")).toString());
								me.setUSERNAME(NullReplace(rs.getString("RHS_NAME")).toString());
								me.setDECISION_DATE(NullReplace(rs.getString("RHS_DECISION_DATE")).toString());
								me.setACTIONTAKEN(NullReplace(rs.getString("RHS_DECISION_DOC")).toString());
								me.setApproverAccessType(
										NullReplace(rs.getString("RHS_APPROVER_ACCESS_TYPE_DOC")).toString());
								list.add(me);

							}
						}
					}
				}

				if (FILTER_VALUE.contains("UNICORE") || FILTER_VALUE.contains("MARVEL")
						|| FILTER_VALUE.contains("SLICE") || FILTER_VALUE.contains("LCBD")) {
					rs = statement.executeQuery();

					while (rs.next()) {
						Memo_Entity me = new Memo_Entity();
						me.setPINSTID(NullReplace(rs.getString("PINSTID")).toString());
						me.setUSERNAME(NullReplace(rs.getString("USERNAME")).toString());
						me.setDECISION_DATE(NullReplace(rs.getString("DECISION_DATE")).toString());
						me.setACTIONTAKEN(NullReplace(rs.getString("DECISION")).toString());
						me.setApproverAccessType(NullReplace(rs.getString("APPROVER_ACCESS_TYPE")).toString());
						list.add(me);
					}
				} 
				
//				else if (FILTER_VALUE.contains("ONBOARDING")) {
//					logger.info("getting data for --ONBOARDING --> ");
//					rs = statement.executeQuery();
//
//					while (rs.next()) {
//						Memo_Entity me = new Memo_Entity();
//						me.setPINSTID(NullReplace(rs.getString("PINSTID")).toString());
//						me.setUSERNAME(NullReplace(rs.getString("RHS_NAME")).toString());
//						me.setDECISION_DATE(NullReplace(rs.getString("RHS_DECISION_DATE")).toString());
//						me.setACTIONTAKEN(NullReplace(rs.getString("RHS_DECISION")).toString());
//						me.setApproverAccessType(NullReplace(rs.getString("RHS_APPROVER_ACCESS_TYPE")).toString());
//						list.add(me);
//					}
//					logger.info("MCA_FilterBasedOnSearch_USM FOR --ONBOARDING --> " + list);
//				}

//				logger.info("FilterBasedOnSearch_USM --> " + list);
				if (statement != null) {
					statement.close();
				}

				if (rs != null) {
					rs.close();
				}
			}
		} catch (Exception e) {
			logger.info("Exception occured in class->Mas_Service-->Method--> FilterBasedOnSearch 3rd API "
					+ e.getMessage());
		}
		return list;

	}
}
