package com.svt.model.commonModel;

import java.util.List;

import com.svt.model.commonModel.AddressCodes;

//@Data
//@Getter
//@Setter
//@ToString
public class MainPojo {

	String SecurityId;
	String SecurityName;
	String CollateralId;
	String CollateralCode;
	String SubTypeSecurity;
	String TypeOfSecurity;
	String Product;
	String TypeOfCharge;
	String ValueOfSubTypeSecuirty;
	String SecurityValueInMn;
	String ValSubTypeSecInMn;
	String LimitSuffix;
	String LimitPrefix;
	String SecurityCreated;
	String RequestType;
	List<InnerPojo> InnerPojo;
	String requestId;
	String dateAndTime;
	String Ceiling_Limit; // Nominal value of Security/Ceiling limit amount (Maximum value of security
	// amount available to
	private String coltrlSrlNum;
	private String coltrlLinkage;
	private String Collateral_Class; // Collateral Class
	private String Collateral_Code; // Sub type Of Security/Collateral code
	private String Gross_Val; // Apportioned Security Value /Assigned value to limit (at level 2 for WC)/ Term
	// Loan number/ Derivative limit
	private String Due_Dt; // Security Creation date (DD-MMM-YYYY)
	private String Last_Val_Date; // Last valuation date (DD-MMM-YYYY)
	private String NatureOfCharge; // Nature of Charge
	private String notes; // LSM no or SR no
	private String notes1; // LSM no or SR no
	private String notes2; // LSM no or SR no
	private String Receive_Dt;
	private String Review_Dt; // Review Date (Next due date for valuation of security) (DD-MMM-YYYY))
	private String Policy_Amt; // Policy Amount
	private String Policy_No; // Policy Number
	private String FromDeriveVal; // Derived Value From/valuation basis
	private String Value; // Apportioned Security Value /Assigned value to limit (at level 2 for WC)/ Term
// Loan number/ Derivative limit
	private String Unit_Val; // UNIT VALUE
	private String No_Of_Units; // NO. OF UNITS
	private String Collateral_Value; // Apportioned Security Value /Assigned value to limit (at level 2 for WC)/ Term
	private String Security_Id;
	private String Security_Name;
	private String Security_Type;
	private String Ucc_Based_CustId;
	private String Security_Created;
	private String products;
	private String ReferenceCode;
	private String ReferenceDescription;
	private String TypeOfSecurityCreation;
	private String AddressLine1;
	private String AddressLine2;
	private String Area;
	private String Road;
	private String Landmark;
	private String Pincode;
	private String City;
	private String State;
	private String District;
	private String Country;
	private AddressCodes addressCode;
	private String Propertyowner;
	private String LsmNumber;
	private String LodgeColllateralID;
	private String NameofHoldingStock;
	private String unitValue;
	private String noOfUnits;

	public String getTypeOfSecurityCreation() {
		return TypeOfSecurityCreation;
	}

	public void setTypeOfSecurityCreation(String typeOfSecurityCreation) {
		TypeOfSecurityCreation = typeOfSecurityCreation;
	}

	public String getReferenceCode() {
		return ReferenceCode;
	}

	public void setReferenceCode(String referenceCode) {
		ReferenceCode = referenceCode;
	}

	public String getReferenceDescription() {
		return ReferenceDescription;
	}

	public void setReferenceDescription(String referenceDescription) {
		ReferenceDescription = referenceDescription;
	}

	public String getSecurityId() {
		return SecurityId;
	}

	public void setSecurityId(String securityId) {
		SecurityId = securityId;
	}

	public String getSecurityName() {
		return SecurityName;
	}

	public void setSecurityName(String securityName) {
		SecurityName = securityName;
	}

	public String getCollateralId() {
		return CollateralId;
	}

	public void setCollateralId(String collateralId) {
		CollateralId = collateralId;
	}

	public String getCollateralCode() {
		return CollateralCode;
	}

	public void setCollateralCode(String collateralCode) {
		CollateralCode = collateralCode;
	}

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

	public String getProduct() {
		return Product;
	}

	public void setProduct(String product) {
		Product = product;
	}

	public String getTypeOfCharge() {
		return TypeOfCharge;
	}

	public void setTypeOfCharge(String typeOfCharge) {
		TypeOfCharge = typeOfCharge;
	}

	public String getValueOfSubTypeSecuirty() {
		return ValueOfSubTypeSecuirty;
	}

	public void setValueOfSubTypeSecuirty(String valueOfSubTypeSecuirty) {
		ValueOfSubTypeSecuirty = valueOfSubTypeSecuirty;
	}

	public String getSecurityValueInMn() {
		return SecurityValueInMn;
	}

	public void setSecurityValueInMn(String securityValueInMn) {
		SecurityValueInMn = securityValueInMn;
	}

	public String getLimitSuffix() {
		return LimitSuffix;
	}

	public void setLimitSuffix(String limitSuffix) {
		LimitSuffix = limitSuffix;
	}

	public String getLimitPrefix() {
		return LimitPrefix;
	}

	public void setLimitPrefix(String limitPrefix) {
		LimitPrefix = limitPrefix;
	}

	public String getSecurityCreated() {
		return SecurityCreated;
	}

	public void setSecurityCreated(String securityCreated) {
		SecurityCreated = securityCreated;
	}

	public String getRequestType() {
		return RequestType;
	}

	public void setRequestType(String requestType) {
		RequestType = requestType;
	}

	public List<InnerPojo> getInnerPojo() {
		return InnerPojo;
	}

	public void setInnerPojo(List<InnerPojo> innerPojo) {
		InnerPojo = innerPojo;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getDateAndTime() {
		return dateAndTime;
	}

	public void setDateAndTime(String dateAndTime) {
		this.dateAndTime = dateAndTime;
	}

	public String getCeiling_Limit() {
		return Ceiling_Limit;
	}

	public void setCeiling_Limit(String ceiling_Limit) {
		Ceiling_Limit = ceiling_Limit;
	}

	public String getColtrlSrlNum() {
		return coltrlSrlNum;
	}

	public void setColtrlSrlNum(String coltrlSrlNum) {
		this.coltrlSrlNum = coltrlSrlNum;
	}

	public String getColtrlLinkage() {
		return coltrlLinkage;
	}

	public void setColtrlLinkage(String coltrlLinkage) {
		this.coltrlLinkage = coltrlLinkage;
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

	public String getCollateral_Value() {
		return Collateral_Value;
	}

	public void setCollateral_Value(String collateral_Value) {
		Collateral_Value = collateral_Value;
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

	public String getSecurity_Created() {
		return Security_Created;
	}

	public void setSecurity_Created(String security_Created) {
		Security_Created = security_Created;
	}

	public String getProducts() {
		return products;
	}

	public void setProducts(String products) {
		this.products = products;
	}

	public String getAddressLine1() {
		return AddressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		AddressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return AddressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		AddressLine2 = addressLine2;
	}

	public String getArea() {
		return Area;
	}

	public void setArea(String area) {
		Area = area;
	}

	public String getRoad() {
		return Road;
	}

	public void setRoad(String road) {
		Road = road;
	}

	public String getLandmark() {
		return Landmark;
	}

	public void setLandmark(String landmark) {
		Landmark = landmark;
	}

	public String getPincode() {
		return Pincode;
	}

	public void setPincode(String pincode) {
		Pincode = pincode;
	}

	public String getCity() {
		return City;
	}

	public void setCity(String city) {
		City = city;
	}

	public String getState() {
		return State;
	}

	public void setState(String state) {
		State = state;
	}

	public String getDistrict() {
		return District;
	}

	public void setDistrict(String district) {
		District = district;
	}

	public String getCountry() {
		return Country;
	}

	public void setCountry(String country) {
		Country = country;
	}
	public String getValSubTypeSecInMn() {
		return ValSubTypeSecInMn;
	}

	public void setValSubTypeSecInMn(String valSubTypeSecInMn) {
		ValSubTypeSecInMn = valSubTypeSecInMn;
	}

	public AddressCodes getAddressCode() {
		return addressCode;
	}

	public void setAddressCode(AddressCodes addressCode) {
		this.addressCode = addressCode;
	}

	public String getPropertyowner() {
		return Propertyowner;
	}

	public void setPropertyowner(String propertyowner) {
		Propertyowner = propertyowner;
	}

	public String getLsmNumber() {
		return LsmNumber;
	}

	public void setLsmNumber(String lsmNumber) {
		LsmNumber = lsmNumber;
	}

	
	public String getNameofHoldingStock() {
		return NameofHoldingStock;
	}

	public void setNameofHoldingStock(String nameofHoldingStock) {
		NameofHoldingStock = nameofHoldingStock;
	}

	public String getLodgeColllateralID() {
		return LodgeColllateralID;
	}

	public void setLodgeColllateralID(String lodgeColllateralID) {
		LodgeColllateralID = lodgeColllateralID;
	}
	
	public String getUnitValue() {
		return unitValue;
	}

	public void setUnitValue(String unitValue) {
		this.unitValue = unitValue;
	}

	public String getNoOfUnits() {
		return noOfUnits;
	}

	public void setNoOfUnits(String noOfUnits) {
		this.noOfUnits = noOfUnits;
	}

	@Override
	public String toString() {
		return "MainPojo [SecurityId=" + SecurityId + ", SecurityName=" + SecurityName + ", CollateralId="
				+ CollateralId + ", CollateralCode=" + CollateralCode + ", SubTypeSecurity=" + SubTypeSecurity
				+ ", TypeOfSecurity=" + TypeOfSecurity + ", Product=" + Product + ", TypeOfCharge=" + TypeOfCharge
				+ ", ValueOfSubTypeSecuirty=" + ValueOfSubTypeSecuirty + ", SecurityValueInMn=" + SecurityValueInMn
				+ ", ValSubTypeSecInMn=" + ValSubTypeSecInMn + ", LimitSuffix=" + LimitSuffix + ", LimitPrefix="
				+ LimitPrefix + ", SecurityCreated=" + SecurityCreated + ", RequestType=" + RequestType + ", InnerPojo="
				+ InnerPojo + ", requestId=" + requestId + ", dateAndTime=" + dateAndTime + ", Ceiling_Limit="
				+ Ceiling_Limit + ", coltrlSrlNum=" + coltrlSrlNum + ", coltrlLinkage=" + coltrlLinkage
				+ ", Collateral_Class=" + Collateral_Class + ", Collateral_Code=" + Collateral_Code + ", Gross_Val="
				+ Gross_Val + ", Due_Dt=" + Due_Dt + ", Last_Val_Date=" + Last_Val_Date + ", NatureOfCharge="
				+ NatureOfCharge + ", notes=" + notes + ", notes1=" + notes1 + ", notes2=" + notes2 + ", Receive_Dt="
				+ Receive_Dt + ", Review_Dt=" + Review_Dt + ", Policy_Amt=" + Policy_Amt + ", Policy_No=" + Policy_No
				+ ", FromDeriveVal=" + FromDeriveVal + ", Value=" + Value + ", Unit_Val=" + Unit_Val + ", No_Of_Units="
				+ No_Of_Units + ", Collateral_Value=" + Collateral_Value + ", Security_Id=" + Security_Id
				+ ", Security_Name=" + Security_Name + ", Security_Type=" + Security_Type + ", Ucc_Based_CustId="
				+ Ucc_Based_CustId + ", Security_Created=" + Security_Created + ", products=" + products
				+ ", ReferenceCode=" + ReferenceCode + ", ReferenceDescription=" + ReferenceDescription
				+ ", TypeOfSecurityCreation=" + TypeOfSecurityCreation + ", AddressLine1=" + AddressLine1
				+ ", AddressLine2=" + AddressLine2 + ", Area=" + Area + ", Road=" + Road + ", Landmark=" + Landmark
				+ ", Pincode=" + Pincode + ", City=" + City + ", State=" + State + ", District=" + District
				+ ", Country=" + Country + ", addressCode=" + addressCode + ", Propertyowner=" + Propertyowner
				+ ", LsmNumber=" + LsmNumber + ", LodgeColllateralID=" + LodgeColllateralID + ", NameofHoldingStock="
				+ NameofHoldingStock + ", unitValue=" + unitValue + ", noOfUnits=" + noOfUnits + "]";
	}
	
}
