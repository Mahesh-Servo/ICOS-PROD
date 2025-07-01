package com.LsmFiServices.pojo.lodgeCollateral;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class lodgeCollateralRequestPojo {
	
	private static final Logger logger = LoggerFactory.getLogger(lodgeCollateralRequestPojo.class);

	String Ceiling_Limit; //Nominal value of Security/Ceiling limit amount (Maximum value of security amount available to
	String Collateral_Class; // Collateral Class
	String Collateral_Code;  // Sub type Of Security/Collateral code
	String Gross_Val; // Apportioned Security Value /Assigned value to limit (at level 2 for WC)/ Term Loan number/ Derivative limit
	String Due_Dt; // Security Creation date (DD-MMM-YYYY)
	String Last_Val_Date; // Last valuation date (DD-MMM-YYYY)
	String NatureOfCharge; // Nature of Charge
	String notes; // LSM no or SR no
	String notes1;  // LSM no or SR no
	String notes2;  // LSM no or SR no
	String Receive_Dt;
	String Review_Dt;  // Review Date (Next due date for valuation of security) (DD-MMM-YYYY))
	String Policy_Amt; // Policy Amount
	String Policy_No;  //  Policy Number
	String FromDeriveVal; // Derived Value From/valuation basis
	String Value; // Apportioned Security Value /Assigned value to limit (at level 2 for WC)/ Term Loan number/ Derivative limit
	String Unit_Val ; // UNIT VALUE 
	String No_Of_Units; // NO. OF UNITS
	String Collateral_Value ; //  Apportioned Security Value /Assigned value to limit (at level 2 for WC)/ Term Loan number/
	String Security_Id;
	String Security_Name;
	String Security_Type;
	String Ucc_Based_CustId;
	String SubTypeSecurity;
	String TypeOfSecurity;
	String RequestType;
	String Security_Created;
	
	
	public String getSecurity_Created() {
		return Security_Created;
	}
	public void setSecurity_Created(String security_Created) {
		Security_Created = security_Created;
	}
	public String getRequestType() {
		return RequestType;
	}
	public void setRequestType(String requestType) {
		RequestType = requestType;
	}
	List<svtSecurityDetails> svtDtls;
	List<policySecurityDetails> policySecurityDtls;
	
	public String getSubTypeSecurity() {
		return SubTypeSecurity;
	}
	public void setSubTypeSecurity(String subTypeSecurity) {
		SubTypeSecurity = subTypeSecurity;
	}
	public String getTypeOfSecurity() {
		return TypeOfSecurity;
	}
	public void setTypeOfSecurity(String typeOfSecurity) {
		TypeOfSecurity = typeOfSecurity;
	}
	
	public String getCeiling_Limit() {
		return Ceiling_Limit;
	}
	public void setCeiling_Limit(String ceiling_Limit) {
		Ceiling_Limit = ceiling_Limit;
	}
	public String getCollateral_Class() {
		return Collateral_Class;
	}
	public void setCollateral_Class(String collateral_Class) {
		Collateral_Class = collateral_Class;
	}
	public String getCollateral_Code() {
		return Collateral_Code;
	}
	public void setCollateral_Code(String collateral_Code) {
		Collateral_Code = collateral_Code;
	}
	public String getGross_Val() {
		return Gross_Val;
	}
	public void setGross_Val(String gross_Val) {
		Gross_Val = gross_Val;
	}
	public String getDue_Dt() {
		return Due_Dt;
	}
	public void setDue_Dt(String due_Dt) {
		Due_Dt = due_Dt;
	}
	public String getLast_Val_Date() {
		return Last_Val_Date;
	}
	public void setLast_Val_Date(String last_Val_Date) {
		Last_Val_Date = last_Val_Date;
	}
	public String getNatureOfCharge() {
		return NatureOfCharge;
	}
	public void setNatureOfCharge(String natureOfCharge) {
		NatureOfCharge = natureOfCharge;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public String getReceive_Dt() {
		return Receive_Dt;
	}
	public void setReceive_Dt(String receive_Dt) {
		Receive_Dt = receive_Dt;
	}
	public String getReview_Dt() {
		return Review_Dt;
	}
	public void setReview_Dt(String review_Dt) {
		Review_Dt = review_Dt;
	}
	public String getPolicy_Amt() {
		return Policy_Amt;
	}
	public void setPolicy_Amt(String policy_Amt) {
		Policy_Amt = policy_Amt;
	}
	public String getPolicy_No() {
		return Policy_No;
	}
	public void setPolicy_No(String policy_No) {
		Policy_No = policy_No;
	}
	public String getFromDeriveVal() {
		return FromDeriveVal;
	}
	public void setFromDeriveVal(String fromDeriveVal) {
		FromDeriveVal = fromDeriveVal;
	}
	public String getValue() {
		return Value;
	}
	public void setValue(String value) {
		Value = value;
	}
	public String getUnit_Val() {
		return Unit_Val;
	}
	public void setUnit_Val(String unit_Val) {
		Unit_Val = unit_Val;
	}
	public String getNo_Of_Units() {
		return No_Of_Units;
	}
	public void setNo_Of_Units(String no_Of_Units) {
		No_Of_Units = no_Of_Units;
	}
	public String getNotes1() {
		return notes1;
	}
	public void setNotes1(String notes1) {
		this.notes1 = notes1;
	}
	public String getNotes2() {
		return notes2;
	}
	public void setNotes2(String notes2) {
		this.notes2 = notes2;
	}
	public String getCollateral_Value() {
		return Collateral_Value;
	}
	public void setCollateral_Value(String collateral_Value) {
		Collateral_Value = collateral_Value;
	}
	public List<svtSecurityDetails> getSvtDtls() {
		return svtDtls;
	}
	public void setSvtDtls(List<svtSecurityDetails> svtDtls) {
		this.svtDtls = svtDtls;
	}
	public List<policySecurityDetails> getPolicySecurityDtls() {
		return policySecurityDtls;
	}
	public void setPolicySecurityDtls(List<policySecurityDetails> policySecurityDtls) {
		this.policySecurityDtls = policySecurityDtls;
	}
	
	public String getSecurity_Id() {
		return Security_Id;
	}
	public void setSecurity_Id(String security_Id) {
		Security_Id = security_Id;
	}
	
	public String getSecurity_Name() {
		return Security_Name;
	}
	public void setSecurity_Name(String security_Name) {
		Security_Name = security_Name;
	}
	
	public String getSecurity_Type() {
		return Security_Type;
	}
	public void setSecurity_Type(String security_Type) {
		Security_Type = security_Type;
	}
	
	public String getUcc_Based_CustId() {
		return Ucc_Based_CustId;
	}
	public void setUcc_Based_CustId(String ucc_Based_CustId) {
		Ucc_Based_CustId = ucc_Based_CustId;
	}
	@Override
	public String toString() {
		return "lodgeCollateralRequestPojo [Ceiling_Limit=" + Ceiling_Limit + ", Collateral_Class=" + Collateral_Class
				+ ", Collateral_Code=" + Collateral_Code + ", Gross_Val=" + Gross_Val + ", Due_Dt=" + Due_Dt
				+ ", Last_Val_Date=" + Last_Val_Date + ", NatureOfCharge=" + NatureOfCharge + ", notes=" + notes
				+ ", notes1=" + notes1 + ", notes2=" + notes2 + ", Receive_Dt=" + Receive_Dt + ", Review_Dt="
				+ Review_Dt + ", Policy_Amt=" + Policy_Amt + ", Policy_No=" + Policy_No + ", FromDeriveVal="
				+ FromDeriveVal + ", Value=" + Value + ", Unit_Val=" + Unit_Val + ", No_Of_Units=" + No_Of_Units
				+ ", Collateral_Value=" + Collateral_Value + ", Security_Id=" + Security_Id + ", Security_Name="
				+ Security_Name + ", Security_Type=" + Security_Type + ", Ucc_Based_CustId=" + Ucc_Based_CustId
				+ ", SubTypeSecurity=" + SubTypeSecurity + ", TypeOfSecurity=" + TypeOfSecurity + ", RequestType="
				+ RequestType + ", Security_Created=" + Security_Created + ", svtDtls=" + svtDtls
				+ ", policySecurityDtls=" + policySecurityDtls + "]";
	}
}
