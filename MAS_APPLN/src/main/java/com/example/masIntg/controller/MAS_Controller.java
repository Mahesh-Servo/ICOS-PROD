package com.example.masIntg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.masIntg.entity.Memo_Entity;
import com.example.masIntg.service.Mas_Service;
import com.example.masIntg.utility.DBConnect;
import com.example.masIntg.utility.ReadPropertyFIle;

@RestController
@RequestMapping("/API")
public class MAS_Controller {

	private static final Logger logger = LoggerFactory.getLogger(MAS_Controller.class);
	Connection con = null;
	PreparedStatement statement = null;
	ResultSet rs = null;
	@Autowired
	Mas_Service mas;

	@SuppressWarnings("unchecked")
	@PostMapping("/MemoCount")
	public String getMemoCount(@RequestBody Map<String, String> Json_Map)
			throws IOException, ParseException, SQLException {
		logger.info("MEMO COUNT 1st API SERVICE CALLED ----------------> METHOD IN ");
		Map<String, Integer> GetMemoCount = null;

		JSONObject obj = new JSONObject();
		JSONArray arr = new JSONArray();
		String MemoCount = null;

		String EMP_ID = Json_Map.get("EMP_ID");
		logger.info("The Employee Id is: " + EMP_ID);

		try {

			GetMemoCount = mas.GetMemoCountForApprlPending(EMP_ID);
			MemoCount = "[{\"PendingCount\":" + GetMemoCount.get("MCA_MemoCount") + "}]";
		} catch (Exception Ex) {
			logger.info("Exception in CLASS-->MAS_Controller-->Method Name-->getMemoCount-->UserId-->" + EMP_ID
					+ "---------->", Ex.getMessage());
		}

		mas.API_RequestResponse_Insert(EMP_ID, Json_Map.toString(), MemoCount.toString(), "COUNT_SERVICE");
		return MemoCount;

	}

	@PostMapping("/pendingMemoList")
	public String getPendingMemoList(@RequestBody Map<String, String> Json_Map) throws SQLException {
		logger.info("MEMO PENDING LIST SERVICE CALLED ----------------> METHOD IN ");
		List<Memo_Entity> list = null;

		JSONArray arr = new JSONArray();
		String EMP_ID = Json_Map.get("EMP_ID");
		String Pinstid = "";
		String countlist = "";

		try {

			list = mas.GetMemoListForApprlPending(EMP_ID);

			for (Memo_Entity me : list) {
				Pinstid = me.getPINSTID();
				JSONObject obj = new JSONObject();
				int countrhs = mas.GetCountforRHSUser(EMP_ID,Pinstid,2);
				if(countrhs > 0) {
					logger.info("WE GOT RHS COUNT ONBOARDING MODULE---------------->"+countrhs);

					obj.put("COLUMN_NAME", "Pending Instance ID");
					obj.put("HEADER_NAME", "Pending Instance ID" + "|" + "USERNAME" + "|" + "Memo Raised On");
					obj.put("SEARCH_ON", "Pending Instance ID" + "|" + "USERNAME");
					obj.put("GET_DETAILS_BY", "Pending Instance ID");
					obj.put("ACTIONTAKEN", "Accept" + "|" + "Reject");
					obj.put("APPROVALTYPE", "UNICORE" + "|" + "MARVEL" + "|" + "SLICE" + "|" + "LCBD" + "|"  + "ONBOARDING");
					obj.put("FILTER_ON", "APPROVALTYPE");
					obj.put("FILTER_TEXT", "Select Module Name");
					obj.put("IS_FILTER_MANDATORY", "Yes");
					obj.put("FILTER_SELECTED", "ONBOARDING");
					obj.put("RECORD_SHOW_ON_PAGELOAD", "Yes");
					arr.put(obj);

				}else {				
				if (me.getPINSTID().contains("UNI")) {
					obj.put("COLUMN_NAME", "Pending Instance ID");
					obj.put("HEADER_NAME", "Pending Instance ID" + "|" + "USERNAME" + "|" + "Memo Raised On");
					obj.put("SEARCH_ON", "Pending Instance ID" + "|" + "USERNAME");
					obj.put("GET_DETAILS_BY", "Pending Instance ID");
					if (me.getPINSTID() != null) {
						if (me.getPINSTID().contains("UNI")) {
							if ("TO".equalsIgnoreCase(me.getApproverAccessType())) {
								obj.put("ACTIONTAKEN", "Accept" + "|" + "Rework" + "|" + "Reject");
								obj.put("APPROVALTYPE", "UNICORE" + "|" + "MARVEL" + "|" + "SLICE" + "|" + "LCBD" + "|" + "ONBOARDING" );
								obj.put("FILTER_ON", "APPROVALTYPE");
								obj.put("FILTER_TEXT", "Select Module Name");
								obj.put("IS_FILTER_MANDATORY", "Yes");
								obj.put("FILTER_SELECTED", "UNICORE");
								obj.put("RECORD_SHOW_ON_PAGELOAD", "Yes");
							} else {
								obj.put("ACTIONTAKEN", "Accept" + "|" + "Rework");
								obj.put("APPROVALTYPE", "UNICORE" + "|" + "MARVEL" + "|" + "SLICE" + "|" + "LCBD" + "|" + "ONBOARDING" );
								obj.put("FILTER_ON", "APPROVALTYPE");
								obj.put("FILTER_TEXT", "Select Module Name");
								obj.put("IS_FILTER_MANDATORY", "Yes");
								obj.put("FILTER_SELECTED", "UNICORE");
								obj.put("RECORD_SHOW_ON_PAGELOAD", "Yes");
							}

						} else {
							obj.put("ACTIONTAKEN", "Approved" + "|" + "Reject");
							obj.put("APPROVALTYPE", "UNICORE" + "|" + "MARVEL" + "|" + "SLICE" + "|" + "LCBD" + "|" + "ONBOARDING" );
							obj.put("FILTER_ON", "APPROVALTYPE");
							obj.put("FILTER_TEXT", "Select Module Name");
							obj.put("IS_FILTER_MANDATORY", "Yes");
							obj.put("FILTER_SELECTED", "UNICORE");
							obj.put("RECORD_SHOW_ON_PAGELOAD", "Yes");
						}
					}

					arr.put(obj);

				} else if (me.getPINSTID().contains("MCG") || me.getPINSTID().contains("MRVL")) { // AMIT_B added for marvel

					obj.put("COLUMN_NAME", "Pending Instance ID");
					obj.put("HEADER_NAME", "Pending Instance ID" + "|" + "USERNAME" + "|" + "Memo Raised On");
					obj.put("SEARCH_ON", "Pending Instance ID" + "|" + "USERNAME");
					obj.put("GET_DETAILS_BY", "Pending Instance ID");
					if (me.getPINSTID() != null) {
						if (me.getPINSTID().contains("MCG") || me.getPINSTID().contains("MRVL")) {
							if ("TO".equalsIgnoreCase(me.getApproverAccessType())) {
								obj.put("ACTIONTAKEN", "Accept" + "|" + "Reject");
								obj.put("APPROVALTYPE", "UNICORE" + "|" + "MARVEL" + "|" + "SLICE" + "|" + "LCBD" + "|" + "ONBOARDING" );
								obj.put("FILTER_ON", "APPROVALTYPE");
								obj.put("FILTER_TEXT", "Select Module Name");
								obj.put("IS_FILTER_MANDATORY", "Yes");
								obj.put("FILTER_SELECTED", "MARVEL");
								obj.put("RECORD_SHOW_ON_PAGELOAD", "Yes");
							} else {
								obj.put("ACTIONTAKEN", "Accept" + "|" + "Reject");
								obj.put("APPROVALTYPE", "UNICORE" + "|" + "MARVEL" + "|" + "SLICE" + "|" + "LCBD" + "|" + "ONBOARDING" );
								obj.put("FILTER_ON", "APPROVALTYPE");
								obj.put("FILTER_TEXT", "Select Module Name");
								obj.put("IS_FILTER_MANDATORY", "Yes");
								obj.put("FILTER_SELECTED", "MARVEL");
								obj.put("RECORD_SHOW_ON_PAGELOAD", "Yes");
							}
						} else {
							obj.put("ACTIONTAKEN", "Approved" + "|" + "Reject");
							obj.put("APPROVALTYPE", "UNICORE" + "|" + "MARVEL" + "|" + "SLICE" + "|" + "LCBD" + "|" + "ONBOARDING" );
							obj.put("FILTER_ON", "APPROVALTYPE");
							obj.put("FILTER_TEXT", "Select Module Name");
							obj.put("IS_FILTER_MANDATORY", "Yes");
							obj.put("FILTER_SELECTED", "MARVEL");
							obj.put("RECORD_SHOW_ON_PAGELOAD", "Yes");
						}
					}

					arr.put(obj);

				} else if (me.getPINSTID().contains("SLICE") || me.getPINSTID().contains("PSN")
						|| me.getPINSTID().contains("PRO")) { // AMIT_B added for SLICE_LIVE

					obj.put("COLUMN_NAME", "Pending Instance ID");
					obj.put("HEADER_NAME", "Pending Instance ID" + "|" + "USERNAME" + "|" + "Memo Raised On");
					obj.put("SEARCH_ON", "Pending Instance ID" + "|" + "USERNAME");
					obj.put("GET_DETAILS_BY", "Pending Instance ID");
					if (me.getPINSTID() != null) {
						if (me.getPINSTID().contains("SLICE") || me.getPINSTID().contains("PSN")
								|| me.getPINSTID().contains("PRO")) {
							if ("TO".equalsIgnoreCase(me.getApproverAccessType())) {
								obj.put("ACTIONTAKEN", "Accept" + "|" + "Reject");
								obj.put("APPROVALTYPE", "UNICORE" + "|" + "MARVEL" + "|" + "SLICE" + "|" + "LCBD" + "|" + "ONBOARDING" );
								obj.put("FILTER_ON", "APPROVALTYPE");
								obj.put("FILTER_TEXT", "Select Module Name");
								obj.put("IS_FILTER_MANDATORY", "Yes");
								obj.put("FILTER_SELECTED", "SLICE");
								obj.put("RECORD_SHOW_ON_PAGELOAD", "Yes");
							} else {
								obj.put("ACTIONTAKEN", "Accept" + "|" + "Reject");
								obj.put("APPROVALTYPE", "UNICORE" + "|" + "MARVEL" + "|" + "SLICE" + "|" + "LCBD" + "|" + "ONBOARDING" );
								obj.put("FILTER_ON", "APPROVALTYPE");
								obj.put("FILTER_TEXT", "Select Module Name");
								obj.put("IS_FILTER_MANDATORY", "Yes");
								obj.put("FILTER_SELECTED", "SLICE");
								obj.put("RECORD_SHOW_ON_PAGELOAD", "Yes");
							}
						} else {
							obj.put("ACTIONTAKEN", "Approved" + "|" + "Reject");
							obj.put("APPROVALTYPE", "UNICORE" + "|" + "MARVEL" + "|" + "SLICE" + "|" + "LCBD" + "|" + "ONBOARDING" );
							obj.put("FILTER_ON", "APPROVALTYPE");
							obj.put("FILTER_TEXT", "Select Module Name");
							obj.put("IS_FILTER_MANDATORY", "Yes");
							obj.put("FILTER_SELECTED", "SLICE");
							obj.put("RECORD_SHOW_ON_PAGELOAD", "Yes");
						}
					}

					arr.put(obj);

	} else if (me.getPINSTID().contains("LCBD") || me.getPINSTID().contains("SAN")) { // AMIT_B added for
																									// LCBD

					obj.put("COLUMN_NAME", "Pending Instance ID");
					obj.put("HEADER_NAME", "Pending Instance ID" + "|" + "USERNAME" + "|" + "Memo Raised On");
					obj.put("SEARCH_ON", "Pending Instance ID" + "|" + "USERNAME");
					obj.put("GET_DETAILS_BY", "Pending Instance ID");
					if (me.getPINSTID() != null) {
						if (me.getPINSTID().contains("LCBD")) {
							if ("TO".equalsIgnoreCase(me.getApproverAccessType()) || "THROUGH".equalsIgnoreCase(me.getApproverAccessType())) {
								logger.info(" TESTING approvere access we got for LCBD 2ND API------------------>"+me.getApproverAccessType());
								obj.put("ACTIONTAKEN", "Accept" + "|" + "Reject");
								obj.put("APPROVALTYPE", "UNICORE" + "|" + "MARVEL" + "|" + "SLICE" + "|" + "LCBD" + "|" +  "ONBOARDING");
								obj.put("FILTER_ON", "APPROVALTYPE");
								obj.put("FILTER_TEXT", "Select Module Name");
								obj.put("IS_FILTER_MANDATORY", "Yes");
								obj.put("FILTER_SELECTED", "LCBD");
								obj.put("RECORD_SHOW_ON_PAGELOAD", "Yes");
							} else {
								logger.info("approvere acces we got for LCBD 2ND API---------------->"+me.getApproverAccessType());
//								obj.put("ACTIONTAKEN", "Accept" + "|" + "Reject");
								obj.put("APPROVALTYPE", "UNICORE" + "|" + "MARVEL" + "|" + "SLICE" + "|" + "LCBD" + "|" +  "ONBOARDING");
								obj.put("FILTER_ON", "APPROVALTYPE");
								obj.put("FILTER_TEXT", "Select Module Name");
								obj.put("IS_FILTER_MANDATORY", "Yes");
								obj.put("FILTER_SELECTED", "LCBD");
								obj.put("RECORD_SHOW_ON_PAGELOAD", "Yes");
							}
						} else {
//							obj.put("ACTIONTAKEN", "Approved" + "|" + "Reject");
							obj.put("APPROVALTYPE", "UNICORE" + "|" + "MARVEL" + "|" + "SLICE" + "|" + "LCBD" + "|" +  "ONBOARDING");
							obj.put("FILTER_ON", "APPROVALTYPE");
							obj.put("FILTER_TEXT", "Select Module Name");
							obj.put("IS_FILTER_MANDATORY", "Yes");
							obj.put("FILTER_SELECTED", "LCBD");
							obj.put("RECORD_SHOW_ON_PAGELOAD", "Yes");
						}
					}

					arr.put(obj);
				} else {

					obj.put("COLUMN_NAME", "PINSTID");
					obj.put("HEADER_NAME", "PINSTID" + "|" + "USERNAME" + "|" + "DECISION_DATE");
					obj.put("SEARCH_ON", "PINSTID" + "|" + "USERNAME");
					obj.put("GET_DETAILS_BY", "PINSTID");
					if (me.getPINSTID() != null) {
						if (me.getPINSTID().contains("UNI")) {
							obj.put("ACTIONTAKEN", "Accept" + "|" + "Rework");

						} else {
							obj.put("ACTIONTAKEN", "Approved" + "|" + "Reject");
						}
					}

					arr.put(obj);
				}
			}
		}
		} catch (Exception e) {
			logger.info("Exception in Class==>MAS_Controller-->Method name-->getPendingMemoList-->EMPID-->" + EMP_ID
					+ "---------->", e.getMessage());
		}
		mas.API_RequestResponse_Insert(Pinstid, Json_Map.toString(), arr.toString(), "PENDING_MEMO_LIST");
		return arr.toString();

	}

	// amit_Babil-added(14-3-2023)
	@PostMapping("/pendingMemoDetailsForApproval")
	public String getpendingMemoDetailsForApproval(@RequestBody Map<String, String> Json_Map) throws SQLException {
		logger.info("PENDING MEMO DETAILS FOR APPROVAL ----------------> METHOD IN");
		List<Memo_Entity> list = null;
		String Pinstid = "";
		ResultSet rs = null;
		String lsql = "";
		JSONObject obj = new JSONObject();
		JSONArray arr = new JSONArray();
		String EMP_ID = Json_Map.get("EMP_ID");
		String APP_SR_NO = Json_Map.get("APP_SR_NO"); // CHANGED by amit_babil(21-4-23)
		String SEARCH_VALUE = Json_Map.get("SEARCH_VALUE");

		try (Connection con = DBConnect.getConnection();) {
			list = mas.GetPendingMemoDetails(EMP_ID, APP_SR_NO, SEARCH_VALUE);

			String BORROWER_NAME = "";
			String PROPOSAL_TYPE = "";
			String INDUSTRY = "";
			String CONSTITUTION = "";
			String CATEGORY = "";
			String FACILITY_REQUIRED= "";
			String LEAD_DATE = "";
			String CASEINITIATOR = "";
			String NATURE = "";
			String DATEINCORP = "";
			String PROGRAM = "";
			String PROGRAM_FINAL = "";
			String PD_MODEL = "";
			String TOTAL_FUND_BASED = "";
			String TOTAL_NON_FUND_BASED = "";
			String FACILITY = "";
			String COLLATERAL = "";
			String RECOMMEND = "";
			String UCC = "";
			String TAKEOVER = "";
			String TAKEOVER_AMT = "";
			String SUBJECT = "";
			String RECOURSE = "";
			String APPROVALPROPOSAL = "";
			String PACCLAUSE = "";
			String JAASELECT = "";
			String SCORE = "";
			String FACILITYTOTAL = "";
			String STATUS = "";
			String ENTITY = "";
			Double FUNDSCORE = 0.0;
			Double NONFUNDSCORE = 0.0;
			Double OTHERSCORE = 0.0;
			String TOTAL_REQ  = "";
			logger.info("LIST WE GOT FOR 4TH API--->"+list);
				logger.info("##---------------------if list is empty then executing onboarding logic------------------------##");
				int countrhs = mas.GetCountforRHSUser(EMP_ID,SEARCH_VALUE,4);
				logger.info("WE GOT count for onboarding IN 4th API------>"+countrhs);
				if(countrhs > 0) {
					logger.info("WE GOT RHS COUNT  ---------------->"+countrhs);
					
					
					lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MAS_ONBOARDING");
					logger.info("sql query for onboarding BASIC DETAILS"+lsql);
					statement = con.prepareStatement(lsql);
					statement.setString(1, SEARCH_VALUE);
					rs = statement.executeQuery();
					while (rs.next()) {
						BORROWER_NAME = rs.getString("ENTITY");
						INDUSTRY = rs.getString("INDUSTRY");
						CONSTITUTION = rs.getString("CONSTITUTION");
						PROPOSAL_TYPE = rs.getString("PROPOSALTYPE");
						LEAD_DATE = rs.getString("LEAD_DATE");
						CASEINITIATOR = rs.getString("CASEINITIATOR");
						NATURE = rs.getString("NATUR_OF_BUSINESS");
						DATEINCORP = rs.getString("DATE_OF_INCORPORATION");
						PROGRAM = rs.getString("TYPE_OF_PROGRAM");
					}
					
					if(PROGRAM.equalsIgnoreCase("GST_DETAILS")) {
						PROGRAM_FINAL = "GST";
					}else if(PROGRAM.equalsIgnoreCase("SUR_BANK")){
						PROGRAM_FINAL = "Banking";
					}else if(PROGRAM.equalsIgnoreCase("AGRIFUND")){
						PROGRAM_FINAL = "Agri Infra Fund";
					}else {
						PROGRAM_FINAL = PROGRAM;
					}
					
					String takeover = "No";
					lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MAS_ONBOARDING_FACILITY");
					logger.info("sql query for onboarding FACILITY"+lsql);
					statement = con.prepareStatement(lsql);
					statement.setString(1, SEARCH_VALUE);
					rs = statement.executeQuery();
					while (rs.next()) {
						TAKEOVER = rs.getString("TAKEOVER_LOAN");
						if("Yes".equalsIgnoreCase(TAKEOVER)) {
							logger.info("Yes we got");
							takeover="Yes";
							break;
						}
					}
					
					if("Yes".equalsIgnoreCase(takeover)) {
					
					lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MAS_ONBOARDING_TAKEOVER");
					logger.info("sql query for onboarding TAKEOVER"+lsql);
					statement = con.prepareStatement(lsql);
					statement.setString(1, SEARCH_VALUE);
					rs = statement.executeQuery();
					while (rs.next()) {
						TAKEOVER_AMT = rs.getString("TAKEOVER_AMOUNT");
					}
					}
					
					lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MAS_ONBOARDING_FUND");
					logger.info("sql query for onboarding FUND "+lsql);
					statement = con.prepareStatement(lsql);
					statement.setString(1, SEARCH_VALUE);
					statement.setString(2, SEARCH_VALUE);
					rs = statement.executeQuery();
					while (rs.next()) {
						TOTAL_FUND_BASED = rs.getString("TOTAL_FUND");
					}
					
					lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MAS_ONBOARDING_NONFUND");
					logger.info("sql query for onboarding FUND "+lsql);
					statement = con.prepareStatement(lsql);
					statement.setString(1, SEARCH_VALUE);
					statement.setString(2, SEARCH_VALUE);
					rs = statement.executeQuery();
					while (rs.next()) {
						TOTAL_NON_FUND_BASED = rs.getString("TOTAL_NONFUND");
					}
					
					lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MAS_ONBOARDING_TOTAL");
					logger.info("sql query for onboarding FUND "+lsql);
					statement = con.prepareStatement(lsql);
					statement.setString(1, SEARCH_VALUE);
					statement.setString(2, SEARCH_VALUE);
					rs = statement.executeQuery();
					while (rs.next()) {
						TOTAL_REQ = rs.getString("TOTAL");
					}
					
					obj.put("Raised by", CASEINITIATOR);
					obj.put("Lead ID", SEARCH_VALUE);
					obj.put("Lead Date", LEAD_DATE);
					obj.put("Borrower Name", BORROWER_NAME);
					obj.put("Constitution", CONSTITUTION);
					obj.put("Industry", INDUSTRY);
					obj.put("Nature of Business", NATURE);
					obj.put("Date of Incorporation", DATEINCORP);
					obj.put("Type of Proposal", PROPOSAL_TYPE);
					obj.put("Takeover case", takeover);
					obj.put("Takenover Amount in INR mn", TAKEOVER_AMT);
					obj.put("Requested Facility in INR mn", TOTAL_REQ);
					obj.put("Total Fund Based requirement", TOTAL_FUND_BASED);
					obj.put("Total Non-Fund Based requirement", TOTAL_NON_FUND_BASED);
					obj.put("Proposed Program", PROGRAM_FINAL);
					obj.put("Disclaimer", "I hereby declare that I have verified the signed borrower information form with online digital form filled by bank official. Borrower has given consent to share the information for credit appraisal.");


					obj.put("HEADER_NAME",
							"Raised by" + "|" + "Lead ID" + "|" + "Lead Date" + "|" + "Borrower Name"
									+ "|" + "Constitution" + "|" + "Industry" + "|" + "Nature of Business" + "|" 
									+ "Date of Incorporation" + "|" + "Type of Proposal" + "|" + "Takeover case" + "|" +
									"Takenover Amount in INR mn" + "|" + "Requested Facility in INR mn" + "|" +
									"Total Fund Based requirement" + "|" + "Total Non-Fund Based requirement" + "|" +
									"Proposed Program" + "|" + "Disclaimer");

					obj.put("ACTIONTAKEN", "Accept" + "|" + "Reject");
					obj.put("ViewMore", "No");
					obj.put("IsEnclosure", "No");
					obj.put("Enclosure", "No");
					obj.put("EnclosurePath",
							"https://caoduat.icicibank.com/Marvel/SRVDownloadFile?filePath=/caoduatapp/Oracleclosnas/Process/SMEAG_Dealer_Note/"
									+ SEARCH_VALUE.toUpperCase());

					arr.put(obj);

				}else {

			for (Memo_Entity me : list) {
				Pinstid = me.getPINSTID();
			//	JSONObject obj = new JSONObject();

				if (Pinstid.contains("UNI")) {
					lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MCA_GetBrowwerName");
					statement = con.prepareStatement(lsql);
					statement.setString(1, SEARCH_VALUE);
					rs = statement.executeQuery();
					while (rs.next()) {
						BORROWER_NAME = rs.getString("BORROWERNAME");
						PROPOSAL_TYPE = rs.getString("PROPOSALTYPE");
					}

					lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MCA_GetIndustry");
					statement = con.prepareStatement(lsql);
					statement.setString(1, SEARCH_VALUE);
					rs = statement.executeQuery();
					while (rs.next()) {
						INDUSTRY = rs.getString("INDUSTRY");
					}

					lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MCA_GetConstitution");
					statement = con.prepareStatement(lsql);
					statement.setString(1, SEARCH_VALUE);
					rs = statement.executeQuery();
					while (rs.next()) {
						CONSTITUTION = rs.getString("CONSTITUTION_SELECT");
					}

					lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MCA_GetCategory");
					statement = con.prepareStatement(lsql);
					statement.setString(1, SEARCH_VALUE);
					rs = statement.executeQuery();
					if (rs.next()) {
						CATEGORY = rs.getString("VARIABLE_SCORE");
					}

					lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MCA_PD_model");
					statement = con.prepareStatement(lsql);
					statement.setString(1, SEARCH_VALUE);
					rs = statement.executeQuery();
					if (rs.next()) {
						PD_MODEL = rs.getString("VARIABLE_SCORE");
					}

					lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MCA_Fund_based");
					statement = con.prepareStatement(lsql);
					statement.setString(1, SEARCH_VALUE);
					rs = statement.executeQuery();
					while (rs.next()) {
						TOTAL_FUND_BASED = rs.getString("SUM(PROPOSED_TOTAL)");
					}

					lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MCA_Non_Fund_based");
					statement = con.prepareStatement(lsql);
					statement.setString(1, SEARCH_VALUE);
					rs = statement.executeQuery();
					if (rs.next()) {
						TOTAL_NON_FUND_BASED = rs.getString("SUM(PROPOSED_TOTAL)");
					}

					lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MCA_facility");
					statement = con.prepareStatement(lsql);
					statement.setString(1, SEARCH_VALUE);
					rs = statement.executeQuery();
					if (rs.next()) {
						FACILITY = rs.getString("SUM(PROPOSED_TOTAL)");
					}

					lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MCA_Collateral");
					statement = con.prepareStatement(lsql);
					statement.setString(1, SEARCH_VALUE);
					rs = statement.executeQuery();
					if (rs.next()) {
						COLLATERAL = rs.getString("OVERALL_COLLATERAL");
					}

					lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MCA_Recommend");
					statement = con.prepareStatement(lsql);
					statement.setString(1, SEARCH_VALUE);
					rs = statement.executeQuery();
					if (rs.next()) {
						RECOMMEND = rs.getString("OTHER_DETAILS");
					}

					obj.put("Unicore Instance ID", me.getPINSTID());
					obj.put("USERNAME", me.getUSERNAME());
					obj.put("DECISION_DATE", me.getDECISION_DATE());
					obj.put("BORROWER_NAME", BORROWER_NAME);
					obj.put("INDUSTRY", INDUSTRY);
					obj.put("CONSTITUTION", CONSTITUTION);
					obj.put("TYPE_OF_PROPOSAL", PROPOSAL_TYPE);
					obj.put("Total_Fund_based_requirement", TOTAL_FUND_BASED);
					obj.put("Total_Non_fund_based_requirement", TOTAL_NON_FUND_BASED);
					obj.put("Total_Facility_Amount", FACILITY);
					obj.put("Overall_Collateral_Cover", COLLATERAL);
					obj.put("CATEGORY", CATEGORY);
					obj.put("PD_MODEL", PD_MODEL);
					obj.put("Recommendation", RECOMMEND);
					obj.put("HEADER_NAME",
							"Unicore Instance ID" + "|" + "USERNAME" + "|" + "DECISION_DATE" + "|" + "BORROWER_NAME"
									+ "|" + "INDUSTRY" + "|" + "CONSTITUTION" + "|" + "TYPE_OF_PROPOSAL" + "|"
									+ "Total_Fund_based_requirement" + "|" + "Total_Non_fund_based_requirement" + "|"
									+ "Total_Facility_Amount" + "|" + "Overall_Collateral_Cover" + "|" + "CATEGORY"
									+ "|" + "PD_MODEL" + "|" + "Recommendation");
					if (me.getPINSTID() != null) {
						if (me.getPINSTID().contains("UNI")) {
							if ("TO".equalsIgnoreCase(me.getApproverAccessType())) {
								obj.put("ACTIONTAKEN", "Accept" + "|" + "Rework" + "|" + "Reject");
							} else {
								obj.put("ACTIONTAKEN", "Accept" + "|" + "Rework");

							}
						} else {
							obj.put("ACTIONTAKEN", "Approved" + "|" + "Reject");
						}
					}
					obj.put("ViewMore", "No");
					obj.put("IsEnclosure", "No");
					obj.put("Enclosure", "No");
					obj.put("EnclosurePath",
							"https://caoduat.icicibank.com/Marvel/SRVDownloadFile?filePath=/caoduatapp/Oracleclosnas/Process/SMEAG_Dealer_Note/"
									+ SEARCH_VALUE.toUpperCase());

					arr.put(obj);
				} else if (Pinstid.contains("MCG") || me.getPINSTID().contains("MRVL")) {

					lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MCA_GetBrowwerNameMARVEL");
					statement = con.prepareStatement(lsql);
					statement.setString(1, SEARCH_VALUE);
					rs = statement.executeQuery();
					while (rs.next()) {
						BORROWER_NAME = rs.getString("BORROWERNAME");
						PROPOSAL_TYPE = rs.getString("PROPOSALTYPE");
					}

					lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MCA_GetIndustry");
					statement = con.prepareStatement(lsql);
					statement.setString(1, SEARCH_VALUE);
					rs = statement.executeQuery();
					while (rs.next()) {
						INDUSTRY = rs.getString("INDUSTRY");
					}

					lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MCA_GetConstitution");
					statement = con.prepareStatement(lsql);
					statement.setString(1, SEARCH_VALUE);
					rs = statement.executeQuery();
					while (rs.next()) {
						CONSTITUTION = rs.getString("CONSTITUTION_SELECT");

					}

					lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MCA_GetCategoryMARVEL"); // CHANGED FOR MARVEL
					statement = con.prepareStatement(lsql);
					statement.setString(1, SEARCH_VALUE);
					rs = statement.executeQuery();
					if (rs.next()) {
						CATEGORY = rs.getString("FINAL_SCORE_BAND");

					}

					lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MCA_Fund_basedMARVELSLICE");
					statement = con.prepareStatement(lsql);
					statement.setString(1, SEARCH_VALUE);
					rs = statement.executeQuery();
					while (rs.next()) {
						TOTAL_FUND_BASED = NullReplace(rs.getString("SUM(PROPOSED_LIMIT)")) == "" ? "0.0"
								: rs.getString("SUM(PROPOSED_LIMIT)");

					}

					lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MCA_Non_Fund_basedMARVELSLICE");
					statement = con.prepareStatement(lsql);
					statement.setString(1, SEARCH_VALUE);
					rs = statement.executeQuery();
					if (rs.next()) {
						TOTAL_NON_FUND_BASED = NullReplace(rs.getString("SUM(PROPOSED_LIMIT)")) == "" ? "0.0"
								: rs.getString("SUM(PROPOSED_LIMIT)");

					}

					lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MCA_RecommendMARVEL");
					statement = con.prepareStatement(lsql);
					statement.setString(1, SEARCH_VALUE);
					rs = statement.executeQuery();
					if (rs.next()) {
						RECOMMEND = rs.getString("RECOMENDATION");
					}

					lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MCA_facilityMARVEL");
					statement = con.prepareStatement(lsql);
					statement.setString(1, SEARCH_VALUE);
					rs = statement.executeQuery();
					if (rs.next()) {
						FACILITYTOTAL = NullReplace(rs.getString("TOTAL_FACILITY")) == "" ? "0.0"
								: rs.getString("TOTAL_FACILITY");

					}

					logger.info("TOTAL FACILITY AMOUNT---FOR MARVEL-->" + FACILITYTOTAL);

					obj.put("Marvel Instance ID", me.getPINSTID());
					obj.put("USERNAME", me.getUSERNAME());
					obj.put("DECISION_DATE", me.getDECISION_DATE());
					obj.put("BORROWER_NAME", BORROWER_NAME);
					obj.put("INDUSTRY", INDUSTRY);
					obj.put("CONSTITUTION", CONSTITUTION);
					obj.put("TYPE_OF_PROPOSAL", PROPOSAL_TYPE);
					obj.put("Total_Fund_based_requirement", TOTAL_FUND_BASED);
					obj.put("Total_Non_fund_based_requirement", TOTAL_NON_FUND_BASED);
					obj.put("Total_Facility_Amount", FACILITYTOTAL);
					obj.put("CATEGORY", CATEGORY);
					obj.put("Recommendation", RECOMMEND);
					obj.put("HEADER_NAME",
							"Marvel Instance ID" + "|" + "USERNAME" + "|" + "DECISION_DATE" + "|" + "BORROWER_NAME"
									+ "|" + "INDUSTRY" + "|" + "CONSTITUTION" + "|" + "TYPE_OF_PROPOSAL" + "|"
									+ "Total_Fund_based_requirement" + "|" + "Total_Non_fund_based_requirement" + "|"
									+ "Total_Facility_Amount" + "|" + "CATEGORY" + "|" + "Recommendation");
					if (me.getPINSTID() != null) {
						if (me.getPINSTID().contains("MCG") || me.getPINSTID().contains("MRVL")) {
							if ("TO".equalsIgnoreCase(me.getApproverAccessType())) {
								obj.put("ACTIONTAKEN", "Accept" + "|" + "Reject");
							} else {
								obj.put("ACTIONTAKEN", "Accept" + "|" + "Reject");

							}
						} else {
							obj.put("ACTIONTAKEN", "Approved" + "|" + "Reject");
						}
					}
					obj.put("ViewMore", "No");
					obj.put("IsEnclosure", "No");
					obj.put("Enclosure", "No");
					obj.put("EnclosurePath",
							"https://caoduat.icicibank.com/Marvel/SRVDownloadFile?filePath=/caoduatapp/Oracleclosnas/Process/SMEAG_Dealer_Note/"
									+ SEARCH_VALUE.toUpperCase());

					arr.put(obj);
				} else if (me.getPINSTID().contains("SLICE") || me.getPINSTID().contains("PSN")
						|| me.getPINSTID().contains("PRO")) {

					lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MCA_GetBrowwerNameSLICE");
					statement = con.prepareStatement(lsql);
					statement.setString(1, SEARCH_VALUE);
					rs = statement.executeQuery();
					while (rs.next()) {
						BORROWER_NAME = rs.getString("BORROWERNAME");
						PROPOSAL_TYPE = rs.getString("PROPOSALTYPE");
					}

					lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MCA_GetIndustry");
					statement = con.prepareStatement(lsql);
					statement.setString(1, SEARCH_VALUE);
					rs = statement.executeQuery();
					while (rs.next()) {
						INDUSTRY = rs.getString("INDUSTRY");
					}

					lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MCA_GetConstitution");
					statement = con.prepareStatement(lsql);
					statement.setString(1, SEARCH_VALUE);
					rs = statement.executeQuery();
					while (rs.next()) {
						CONSTITUTION = rs.getString("CONSTITUTION_SELECT");
					}

					lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MCA_GetCategorySLICE"); // CHANGED
																												// FOR
																												// SLICE
					statement = con.prepareStatement(lsql);
					statement.setString(1, SEARCH_VALUE);
					rs = statement.executeQuery();
					if (rs.next()) {
						CATEGORY = rs.getString("FINAL_SCORE_BAND");
					}

					lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MCA_Fund_basedMARVELSLICE");
					statement = con.prepareStatement(lsql);
					statement.setString(1, SEARCH_VALUE);
					rs = statement.executeQuery();
					while (rs.next()) {
						TOTAL_FUND_BASED = NullReplace(rs.getString("SUM(PROPOSED_LIMIT)")) == "" ? "0.0"
								: rs.getString("SUM(PROPOSED_LIMIT)");
					}

					lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MCA_Non_Fund_basedMARVELSLICE");
					statement = con.prepareStatement(lsql);
					statement.setString(1, SEARCH_VALUE);
					rs = statement.executeQuery();
					if (rs.next()) {
						TOTAL_NON_FUND_BASED = NullReplace(rs.getString("SUM(PROPOSED_LIMIT)")) == "" ? "0.0"
								: rs.getString("SUM(PROPOSED_LIMIT)");

					}

					lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MCA_RecommendSLICE");
					statement = con.prepareStatement(lsql);
					statement.setString(1, SEARCH_VALUE);
					rs = statement.executeQuery();
					if (rs.next()) {
						RECOMMEND = rs.getString("RECOMENDATION");
					}

					lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MCA_UCC");
					statement = con.prepareStatement(lsql);
					statement.setString(1, SEARCH_VALUE);
					rs = statement.executeQuery();
					if (rs.next()) {
						UCC = rs.getString("UCC");
					}

					lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MCA_SUBJECT");
					logger.info("Query MCA_SUBJECT FOR SLICE ----->  " + lsql);
					statement = con.prepareStatement(lsql);
					statement.setString(1, SEARCH_VALUE);
					rs = statement.executeQuery();
					if (rs.next()) {
						SUBJECT = rs.getString("SUBJECT");
					}

					lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MCA_PROPOSAL");
					statement = con.prepareStatement(lsql);
					statement.setString(1, SEARCH_VALUE);
					rs = statement.executeQuery();
					if (rs.next()) {
						APPROVALPROPOSAL = rs.getString("APPROVALPROPOSAL");
					}

					lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MCA_PAAM");
					statement = con.prepareStatement(lsql);
					statement.setString(1, SEARCH_VALUE);
					rs = statement.executeQuery();
					if (rs.next()) {
						PACCLAUSE = rs.getString("PACCLAUSE");
					}

					lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MCA_JAA");
					statement = con.prepareStatement(lsql);
					statement.setString(1, SEARCH_VALUE);
					rs = statement.executeQuery();
					if (rs.next()) {
						JAASELECT = rs.getString("JAASELECT");
					}

					lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MCA_RatingBand");
					statement = con.prepareStatement(lsql);
					statement.setString(1, SEARCH_VALUE);
					rs = statement.executeQuery();
					if (rs.next()) {
						SCORE = NullReplace(rs.getString("FINAL_SCORE_BAND"));
					}

					lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MCA_facilityMARVEL");
					statement = con.prepareStatement(lsql);
					statement.setString(1, SEARCH_VALUE);
					rs = statement.executeQuery();
					if (rs.next()) {
						FACILITYTOTAL = NullReplace(rs.getString("TOTAL_FACILITY")) == "" ? "0.0"
								: rs.getString("TOTAL_FACILITY");
					}

					logger.info("TOTAL FACILITY AMOUNT---FOR SLICE-->" + FACILITYTOTAL);

					obj.put("SLICE Instance ID", me.getPINSTID());
					obj.put("USERNAME", me.getUSERNAME());
					obj.put("DECISION_DATE", me.getDECISION_DATE());
					obj.put("BORROWER_NAME", BORROWER_NAME);
					obj.put("INDUSTRY", INDUSTRY);
					obj.put("CONSTITUTION", CONSTITUTION);
					obj.put("TYPE_OF_PROPOSAL", PROPOSAL_TYPE);
					obj.put("Total_Fund_based_requirement", TOTAL_FUND_BASED);
					obj.put("Total_Non_fund_based_requirement", TOTAL_NON_FUND_BASED);
					obj.put("Total_Facility_Amount", FACILITYTOTAL);
					obj.put("CATEGORY", CATEGORY);
					obj.put("UCC", UCC);
					obj.put("Subject", SUBJECT);
					obj.put("Proposal", APPROVALPROPOSAL);
					obj.put("PAAM/CAA", PACCLAUSE);
					obj.put("JAA", JAASELECT);
					obj.put("Rating Band", SCORE);
					obj.put("Recommendation", RECOMMEND);
					obj.put("HEADER_NAME",
							"SLICE Instance ID" + "|" + "USERNAME" + "|" + "DECISION_DATE" + "|" + "BORROWER_NAME" + "|"
									+ "INDUSTRY" + "|" + "CONSTITUTION" + "|" + "TYPE_OF_PROPOSAL" + "|"
									+ "Total_Fund_based_requirement" + "|" + "Total_Non_fund_based_requirement" + "|"
									+ "Total_Facility_Amount" + "|" + "CATEGORY" + "|" + "UCC" + "|" + "Subject" + "|"
									+ "Proposal" + "|" + "PAAM/CAA" + "|" + "JAA" + "|" + "Rating Band" + "|"
									+ "Recommendation");
					if (me.getPINSTID() != null) {
						if (me.getPINSTID().contains("SLICE") || me.getPINSTID().contains("PSN")
								|| me.getPINSTID().contains("PRO")) {
							if ("TO".equalsIgnoreCase(me.getApproverAccessType())) {
								obj.put("ACTIONTAKEN", "Accept" + "|" + "Reject");
							} else {
								obj.put("ACTIONTAKEN", "Accept" + "|" + "Reject");

							}
						} else {
							obj.put("ACTIONTAKEN", "Approved" + "|" + "Reject");
						}
					}
					obj.put("ViewMore", "No");
					obj.put("IsEnclosure", "No");
					obj.put("Enclosure", "No");
					obj.put("EnclosurePath",
							"https://caoduat.icicibank.com/Marvel/SRVDownloadFile?filePath=/caoduatapp/Oracleclosnas/Process/SMEAG_Dealer_Note/"
									+ SEARCH_VALUE.toUpperCase());

					arr.put(obj);
} else if (Pinstid.contains("LCBD") || me.getPINSTID().contains("SAN")) {

					lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MCA_GetBrowwerName_LCBD");
					statement = con.prepareStatement(lsql);
					statement.setString(1, SEARCH_VALUE);
					rs = statement.executeQuery();
					while (rs.next()) {
						BORROWER_NAME = rs.getString("COMP_NAME");
						INDUSTRY = rs.getString("INDUSTRY");
						UCC = rs.getString("UCC");

					}

					lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MCA_facilityLCBD");
					statement = con.prepareStatement(lsql);
					statement.setString(1, SEARCH_VALUE);
					rs = statement.executeQuery();
					if (rs.next()) {
						FACILITY_REQUIRED = rs.getString("FACILITY_REQUIRED");
						PROPOSAL_TYPE = rs.getString("FACILITY_TYPE");  //TYPEOFPROPOSAL
						RECOURSE = rs.getString("RECOURSE");  //PROPOSAL
						FACILITYTOTAL = rs.getString("TOTAL_AMOUNT_FACILITY");

					}

					lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MCA_RecommendLCBD");
					statement = con.prepareStatement(lsql);
					statement.setString(1, SEARCH_VALUE);
					rs = statement.executeQuery();
					if (rs.next()) {
						RECOMMEND = rs.getString("RECOM");
					}
					
					lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MCA_Acceptance_LCBD");
					statement = con.prepareStatement(lsql);
					statement.setString(1, SEARCH_VALUE);
					rs = statement.executeQuery();
					if (rs.next()) {
						STATUS = rs.getString("SALES_ACCEPTANCE_STATUS");
					}
					
					lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("MCA_SUBJECT_LCBD");
					statement = con.prepareStatement(lsql);
					statement.setString(1, SEARCH_VALUE);
					rs = statement.executeQuery();
					if (rs.next()) {
						SUBJECT = rs.getString("SUBJECT");
					}

					obj.put("LCBD Instance ID", me.getPINSTID());
					obj.put("USERNAME", me.getUSERNAME());
					obj.put("DECISION_DATE", me.getDECISION_DATE());
					obj.put("BORROWER_NAME", BORROWER_NAME);
					obj.put("INDUSTRY", INDUSTRY);
					obj.put("TYPE_OF_PROPOSAL", PROPOSAL_TYPE);
					obj.put("RECOURSE", RECOURSE);
					obj.put("PROPOSAL", FACILITY_REQUIRED);
					obj.put("TOTAL_FACILITY_AMOUNT", FACILITYTOTAL);
					obj.put("UCC", UCC);
					obj.put("ACCEPTANCE", STATUS);
					obj.put("SUBJECT", SUBJECT);
					obj.put("Recommendation", RECOMMEND);
					obj.put("HEADER_NAME",
							"LCBD Instance ID" + "|" + "USERNAME" + "|" + "DECISION_DATE" + "|" + "BORROWER_NAME" + "|"
									+ "INDUSTRY" + "|" + "TYPE_OF_PROPOSAL" + "|"
									+ "RECOURSE" + "|" + "PROPOSAL" + "|"
									+ "TOTAL_FACILITY_AMOUNT" + "|" + "UCC" + "|" + "ACCEPTANCE"+ "|" + "SUBJECT" + "|" + "Recommendation");
					if (me.getPINSTID() != null) {
						if (me.getPINSTID().contains("LCBD") || me.getPINSTID().contains("SAN")) {
							if ("TO".equalsIgnoreCase(me.getApproverAccessType()) || "THROUGH".equalsIgnoreCase(me.getApproverAccessType())) {
								obj.put("ACTIONTAKEN", "Accept" + "|" + "Reject");
							} else {
								logger.info("approvere acces we got for LCBD 4TH API---------------->"+me.getApproverAccessType());
//								obj.put("ACTIONTAKEN", "Accept" + "|" + "Reject");

							}
						} else {
//							obj.put("ACTIONTAKEN", "Approved" + "|" + "Reject");
						}
					}
					obj.put("ViewMore", "No");
					obj.put("IsEnclosure", "No");
					obj.put("Enclosure", "No");
					obj.put("EnclosurePath",
							"https://caoduat.icicibank.com/Marvel/SRVDownloadFile?filePath=/caoduatapp/Oracleclosnas/Process/SMEAG_Dealer_Note/"
									+ SEARCH_VALUE.toUpperCase());

					arr.put(obj);

				} else {

					obj.put("PINSTID", me.getPINSTID());
					obj.put("USERNAME", me.getUSERNAME());
					obj.put("DECISION_DATE", me.getDECISION_DATE());
					obj.put("HEANAME", "PINSTID" + "|" + "USERNAME" + "|" + "DECISION_DATE");
					if (me.getPINSTID() != null) {
						if (me.getPINSTID().contains("UNI")) {
							obj.put("ACTIONTAKEN", "Accept" + "|" + "Rework");

						} else {
							obj.put("ACTIONTAKEN", "Approved" + "|" + "Reject");
						}
					}
					obj.put("ViewMore", "No");
					obj.put("IsEnclosure", "No");

					arr.put(obj);
				}
			}
			}
		} catch (Exception e) {
			logger.info("Exception in class--> MAS_Controller-->method name-->getPendingMemoDetails-->pinstid-->"
					+ Pinstid + "---------->", e.getMessage());
		}
		mas.API_RequestResponse_Insert(Pinstid, Json_Map.toString(), arr.toString(), "PENDING_MEMO_DETAILS");
		return arr.toString();

	}
	// amit_Babil-ended

	@PostMapping("/pendingMemoMoreDetails")
	public String getPendingMemoMoreDetails() {
		return "getPendingMemoMoreDetails called";

	}

	// amit_Babil-added(16-03-2023)
	@PostMapping("/pendingMemoDetails")
	public String getPendingMemoDetails(@RequestBody Map<String, String> Json_Map) throws SQLException {
		logger.info("PENDING MEMO DETAILS ON ACTION 5th API EXECUTION STARTS----------------> METHOD IN");
		List<Memo_Entity> list = null;
		String processKey = "";
		int countrhs = 0;
		int countLCBD = 0;
		PreparedStatement statement = null;
		JSONArray arr = new JSONArray();
		JSONObject obj = new JSONObject();

		String EMP_ID = Json_Map.get("EMP_ID");
		String pinstId = Json_Map.get("SEARCH_VALUE");
		String APP_SR_NO = Json_Map.get("APP_SR_NO");
		String Action = Json_Map.get("ACTION"); //  we are accepting values - amit_babil
		String Remarks = Json_Map.get("REMARKS"); //  we are accepting values - amit_babil
		try {
		if (pinstId != null) {
		     countrhs = mas.GetCountforRHSUser(EMP_ID,pinstId,5);
				logger.info("COUNT FOR ONBOARDING WE GOT 5TH API----------------->"+countrhs);
			if(countrhs > 0) {
				logger.info("WE WILL UPDATE THE RHS USER DICISION-------->");
				Boolean flag2 = mas.updateRHSUser(EMP_ID,pinstId,Action,Remarks);
				if (flag2 == true) {
					obj.put("ActionResult", "SUCCESS");
					obj.put("ActionMessage", "This request submitted successfully");
				} else if (flag2 == false) {
					obj.put("ActionResult", "FAILURE");
					obj.put("ActionMessage", "Some exception is occurred, please try again");

				}
				logger.info("OBJECT AFTER ONBOARDING----------------->"+obj);
				arr.put(obj);
			}else {
			if (pinstId.contains("UNI")) {

				processKey = "UNICORE";
				logger.info(" PROCESS KEY WE GOT----------------->" + processKey);
			} else if (pinstId.contains("SLICE") || pinstId.contains("PSN") || pinstId.contains("PRO")) {
				processKey = "SLICE";
				logger.info(" PROCESS KEY WE GOT---------for slice live-------->" + processKey);
			} else if (pinstId.contains("MCG") || pinstId.contains("MRVL")) {
				processKey = "Marvel";
				logger.info(" PROCESS KEY WE GOT----------------->" + processKey);
			} else if (pinstId.contains("LCBD") || pinstId.contains("SAN")) {
					processKey = "LCBD";
					logger.info(" PROCESS KEY WE GOT----------------->" + processKey);
			}

			Boolean flag = mas.GetPendingMemoDetailsOnAction(EMP_ID, APP_SR_NO, pinstId, Action, Remarks, processKey);
			logger.info("last api controller logger flag" + flag);

			// amit_added
			if (pinstId.contains("UNI")) {
				if (flag == true) {
					obj.put("ActionResult", "SUCCESS");
					obj.put("ActionMessage", "This request submitted successfully");

				} else if (flag == false) {
					obj.put("ActionResult", "FAILURE");
					obj.put("ActionMessage", "Some exception is occurred, please try again");

				}
				arr.put(obj);

			} else if (pinstId.contains("MCG") || pinstId.contains("MRVL")) {
				if (flag == true) {
					obj.put("ActionResult", "SUCCESS");
					obj.put("ActionMessage", "This request submitted successfully");

				} else if (flag == false) {
					obj.put("ActionResult", "FAILURE");
					obj.put("ActionMessage", "Some exception is occurred, please try again");

				}
				arr.put(obj);

		}else if (pinstId.contains("LCBD") || pinstId.contains("SAN")) {

				if (flag == true) {
					
					if("Accept".equalsIgnoreCase(Action)) {
						countLCBD = mas.UpdateLCBD(pinstId);
						logger.info("VALAUE FOR LCBD UPDATED-->"+countLCBD);
					}
					logger.info("else user has rejected the case-->");

					obj.put("ActionResult", "SUCCESS");
					obj.put("ActionMessage", "This request submitted successfully");

				} else if (flag == false) {
					obj.put("ActionResult", "FAILURE");
					obj.put("ActionMessage", "Some exception is occurred, please try again");

				}
				arr.put(obj);
			} else if (pinstId.contains("SLICE") || pinstId.contains("PSN") || pinstId.contains("PRO")) {
				if (flag == true) {
					obj.put("ActionResult", "SUCCESS");
					obj.put("ActionMessage", "This request submitted successfully");

				} else if (flag == false) {
					obj.put("ActionResult", "FAILURE");
					obj.put("ActionMessage", "Some exception is occurred, please try again");

				}
				arr.put(obj);

			} else {

				if (flag == true) {
					obj.put("ActionResult", "SUCCESS");
					obj.put("ActionMessage", "This request submitted successfully");

				} else if (flag == false) {
					obj.put("ActionResult", "FAILURE");
					obj.put("ActionMessage", "Some exception is occurred, please try again");

				}

				arr.put(obj);
			}
			}
		}
		} catch (Exception e) {
			logger.info(
					"Exception in class-->MAS_Controller-->Method name--> getPendingMemoDetailsOnAction-->pinstid-->"
							+ pinstId + "-------->",e.getMessage());
		}

		mas.API_RequestResponse_Insert(pinstId, Json_Map.toString(), arr.toString(), "PENDING_MEMO_DETAILS_6TH");
		return arr.toString();

	}

	// amit changed url for 3 api

	@PostMapping("/searchPendingMemo") // changed
	public String getSearchPendingMemo(@RequestBody String Json_data) throws SQLException, JSONException {
		logger.info("MEMO PENDING LIST SERVICE BASED ON filterOnSearch ----------------> METHOD IN");
		List<Memo_Entity> list = null;
		String Pinstid = "";
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		ResultSet rs4 = null;

		JSONObject json = new JSONObject(Json_data);
		JSONArray arr = new JSONArray();
		String EMP_ID = json.get("EMP_ID").toString();
		String SEARCH_BY = json.get("SEARCH_BY").toString();
		String SEARCH_VALUE = json.get("SEARCH_VALUE").toString();
		JSONArray FILTER_LIST = json.getJSONArray("FILTER_LIST");
		logger.info("filterOnSearch FILTER_LIST :: " + FILTER_LIST);
		String FILTER_BY = FILTER_LIST.getJSONObject(0).get("FILTER_BY").toString();
		logger.info("filterOnSearch FILTER_BY :: " + FILTER_BY);
		String FILTER_VALUE = FILTER_LIST.getJSONObject(0).get("FILTER_VALUE").toString();
		logger.info("filterOnSearch FILTER_VALUE :: " + FILTER_VALUE);


		try (Connection con = DBConnect.getConnection();){

			list = mas.FilterOnSearch(EMP_ID, FILTER_VALUE);

			for (Memo_Entity me : list) {
				JSONObject obj = new JSONObject();
				Pinstid = me.getPINSTID();
				int countrhs = mas.GetCountforRHSUser(EMP_ID,Pinstid,3);
				if(countrhs > 0) {
					logger.info("WE GOT RHS COUNT  ----------------> Onboarding in filter "+countrhs);

					obj.put("Onboarding Instance ID", me.getPINSTID());
					obj.put("USERNAME", me.getUSERNAME());
					obj.put("DECISION_DATE", me.getDECISION_DATE());
					obj.put("COLUMN_NAME", "Onboarding Instance ID");
					obj.put("HEADER_NAME", "Onboarding Instance ID" + "|" + "USERNAME" + "|" + "DECISION_DATE");
					obj.put("SEARCH_ON", "Onboarding Instance ID" + "|" + "DECISION_DATE");
					obj.put("GET_DETAILS_BY", "Onboarding Instance ID");
					obj.put("ACTIONTAKEN", "Accept" + "|" + "Reject");
					arr.put(obj);

				}else {
				if (FILTER_VALUE.contains("UNICORE")) {
					obj.put("Unicore Instance ID", me.getPINSTID());
					obj.put("USERNAME", me.getUSERNAME());
					obj.put("DECISION_DATE", me.getDECISION_DATE());
					obj.put("COLUMN_NAME", "Unicore Instance ID");
					obj.put("HEADER_NAME", "Unicore Instance ID" + "|" + "USERNAME" + "|" + "DECISION_DATE");
					obj.put("SEARCH_ON", "Unicore Instance ID" + "|" + "DECISION_DATE");
					obj.put("GET_DETAILS_BY", "Unicore Instance ID");
					if (me.getPINSTID() != null) {
						if (FILTER_VALUE.contains("UNICORE")) {
							if ("TO".equalsIgnoreCase(me.getApproverAccessType())) {
								obj.put("ACTIONTAKEN", "Accept" + "|" + "Rework" + "|" + "Reject");
							} else {
								obj.put("ACTIONTAKEN", "Accept" + "|" + "Rework");

							}
						} else {
							obj.put("ACTIONTAKEN", "Approved" + "|" + "Reject");
						}
					}
					arr.put(obj);
				} else if (FILTER_VALUE.contains("MARVEL")) { // AMIT_B changed for marvel

					obj.put("Marvel Instance ID", me.getPINSTID());
					obj.put("USERNAME", me.getUSERNAME());
					obj.put("DECISION_DATE", me.getDECISION_DATE());
					obj.put("COLUMN_NAME", "Marvel Instance ID");
					obj.put("HEADER_NAME", "Marvel Instance ID" + "|" + "USERNAME" + "|" + "DECISION_DATE");
					obj.put("SEARCH_ON", "Marvel Instance ID" + "|" + "DECISION_DATE");
					obj.put("GET_DETAILS_BY", "Marvel Instance ID");
					if (me.getPINSTID() != null) {
						if (FILTER_VALUE.contains("MARVEL")) {
							if ("TO".equalsIgnoreCase(me.getApproverAccessType())) {
								obj.put("ACTIONTAKEN", "Accept" + "|" + "Reject");
							} else {
								obj.put("ACTIONTAKEN", "Accept" + "|" + "Reject");

							}
						} else {
							obj.put("ACTIONTAKEN", "Approved" + "|" + "Reject");
						}
					}
					arr.put(obj);
				} else if (FILTER_VALUE.contains("SLICE")) { // AMIT_B ADDED for SLICE_LIVE

					obj.put("SLICE Instance ID", me.getPINSTID());
					obj.put("USERNAME", me.getUSERNAME());
					obj.put("DECISION_DATE", me.getDECISION_DATE());
					obj.put("COLUMN_NAME", "SLICE Instance ID");
					obj.put("HEADER_NAME", "SLICE Instance ID" + "|" + "USERNAME" + "|" + "DECISION_DATE");
					obj.put("SEARCH_ON", "SLICE Instance ID" + "|" + "DECISION_DATE");
					obj.put("GET_DETAILS_BY", "SLICE Instance ID");
					if (me.getPINSTID() != null) {
						if (FILTER_VALUE.contains("SLICE")) {
							if ("TO".equalsIgnoreCase(me.getApproverAccessType())) {
								obj.put("ACTIONTAKEN", "Accept" + "|" + "Reject");
							} else {
								obj.put("ACTIONTAKEN", "Accept" + "|" + "Reject");

							}
						} else {
							obj.put("ACTIONTAKEN", "Approved" + "|" + "Reject");
						}
					}
					arr.put(obj);
					} else if (FILTER_VALUE.contains("LCBD")) { // AMIT_B ADDED for LCBD
					obj.put("LCBD Instance ID", me.getPINSTID());
					obj.put("USERNAME", me.getUSERNAME());
					obj.put("DECISION_DATE", me.getDECISION_DATE());
					obj.put("COLUMN_NAME", "LCBD Instance ID");
					obj.put("HEADER_NAME", "LCBD Instance ID" + "|" + "USERNAME" + "|" + "DECISION_DATE");
					obj.put("SEARCH_ON", "LCBD Instance ID" + "|" + "DECISION_DATE");
					obj.put("GET_DETAILS_BY", "LCBD Instance ID");
					if (me.getPINSTID() != null) {
						if (FILTER_VALUE.contains("LCBD")) {
							logger.info(" TESTING approvere access we got for LCBD 3RD API------------------>"+me.getApproverAccessType());

							if ("TO".equalsIgnoreCase(me.getApproverAccessType()) || "THROUGH".equalsIgnoreCase(me.getApproverAccessType())) {
								obj.put("ACTIONTAKEN", "Accept" + "|" + "Reject");
							} else {
								logger.info(" TESTING approvere access we got for LCBD 3RD API------------------>"+me.getApproverAccessType());

//								obj.put("ACTIONTAKEN", "Accept" + "|" + "Reject");

							}
						} else {
//							obj.put("ACTIONTAKEN", "Approved" + "|" + "Reject");
						}
					}
					arr.put(obj);

				} else {

					obj.put("PINSTID", me.getPINSTID());
					obj.put("USERNAME", me.getUSERNAME());
					obj.put("DECISION_DATE", me.getDECISION_DATE());
					obj.put("COLUMN_NAME", "PINSTID");
					obj.put("HEADER_NAME", "PINSTID" + "|" + "USERNAME" + "|" + "DECISION_DATE");
					obj.put("SEARCH_ON", "PINSTID" + "|" + "DECISION_DATE");
					obj.put("GET_DETAILS_BY", "PINSTID");
					if (me.getPINSTID() != null) {
						if (FILTER_VALUE.contains("UNICORE")) {
							obj.put("ACTIONTAKEN", "Accept" + "|" + "Rework");

						} else {
							obj.put("ACTIONTAKEN", "Approved" + "|" + "Reject");
						}
					}
					arr.put(obj);
				}

			}
			}
		} catch (Exception e) {
			logger.info(" Exception in class--> Mas_Controller->method name-->getPendingMemoList-->pinstid-->" + Pinstid
					+ "---------->", e.getMessage());	
						} 
		mas.API_RequestResponse_Insert(Pinstid, json.toString(), arr.toString(), "SEARCH_PENDING_MEMO");
		return arr.toString();

	}

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
}
