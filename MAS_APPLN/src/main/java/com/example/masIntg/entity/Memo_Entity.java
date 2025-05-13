package com.example.masIntg.entity;

public class Memo_Entity {
	
	String PINSTID;
	String USERNAME;
	String DECISION_DATE;
	String COLUMN_NAME;
	String HEADER_NAME;
	String SEARCH_ON;
	String GET_DETAILS_BY;
	String ACTIONTAKEN;
	public String getApproverAccessType() {
		return approverAccessType;
	}
	public void setApproverAccessType(String approverAccessType) {
		this.approverAccessType = approverAccessType;
	}

	String ActionResult;  //amit_added(16-3-2023)
	String approverAccessType;
	
	public String getPINSTID() {
		return PINSTID;
	}
	public void setPINSTID(String pINSTID) {
		PINSTID = pINSTID;
	}
	public String getUSERNAME() {
		return USERNAME;
	}
	public void setUSERNAME(String uSERNAME) {
		USERNAME = uSERNAME;
	}
	public String getDECISION_DATE() {
		return DECISION_DATE;
	}
	public void setDECISION_DATE(String dECISION_DATE) {
		DECISION_DATE = dECISION_DATE;
	}
	public String getCOLUMN_NAME() {
		return COLUMN_NAME;
	}
	public void setCOLUMN_NAME(String cOLUMN_NAME) {
		COLUMN_NAME = cOLUMN_NAME;
	}
	public String getHEADER_NAME() {
		return HEADER_NAME;
	}
	public void setHEADER_NAME(String hEADER_NAME) {
		HEADER_NAME = hEADER_NAME;
	}
	public String getSEARCH_ON() {
		return SEARCH_ON;
	}
	public void setSEARCH_ON(String sEARCH_ON) {
		SEARCH_ON = sEARCH_ON;
	}
	public String getGET_DETAILS_BY() {
		return GET_DETAILS_BY;
	}
	public void setGET_DETAILS_BY(String gET_DETAILS_BY) {
		GET_DETAILS_BY = gET_DETAILS_BY;
	}
	public String getACTIONTAKEN() {
		return ACTIONTAKEN;
	}
	public void setACTIONTAKEN(String aCTIONTAKEN) {
		ACTIONTAKEN = aCTIONTAKEN;
	}
	
	public String getActionResult() {
		return ActionResult;
	}
	public void setActionResult(String actionResult) {
		ActionResult = actionResult;
	}
	@Override
	public String toString() {
		return "Memo_Entity [PINSTID=" + PINSTID + ", USERNAME=" + USERNAME + ", DECISION_DATE=" + DECISION_DATE
				+ ", COLUMN_NAME=" + COLUMN_NAME + ", HEADER_NAME=" + HEADER_NAME + ", SEARCH_ON=" + SEARCH_ON
				+ ", GET_DETAILS_BY=" + GET_DETAILS_BY + ", ACTIONTAKEN=" + ACTIONTAKEN + ", ActionResult="
				+ ActionResult + ", approverAccessType=" + approverAccessType + "]";
	}
	
	
   
	
}
