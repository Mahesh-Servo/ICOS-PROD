package com.LsmFiServices.pojo.svtModuleCommon;

import java.util.List;

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
	String LimitSuffix;
	String LimitPrefix;
	String PolicyNo;
	String PolicyAmount;
	String SecurityCreated;
	String RequestType;
	List<InnerPojo> InnerPojo;
	String requestId;
	String dateAndTime;

	String coltrlSrlNum;
	String coltrlLinkage;

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

	public String getPolicyNo() {
		return PolicyNo;
	}

	public void setPolicyNo(String policyNo) {
		PolicyNo = policyNo;
	}

	public String getPolicyAmount() {
		return PolicyAmount;
	}

	public void setPolicyAmount(String policyAmount) {
		PolicyAmount = policyAmount;
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

	public void setInnerPojo(List<InnerPojo> InnerPojo) {
		this.InnerPojo = InnerPojo;
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

	@Override
	public String toString() {
		return "MainPojo [SecurityId=" + SecurityId + ", SecurityName=" + SecurityName + ", CollateralId="
				+ CollateralId + ", CollateralCode=" + CollateralCode + ", SubTypeSecurity=" + SubTypeSecurity
				+ ", TypeOfSecurity=" + TypeOfSecurity + ", Product=" + Product + ", TypeOfCharge=" + TypeOfCharge
				+ ", ValueOfSubTypeSecuirty=" + ValueOfSubTypeSecuirty + ", SecurityValueInMn=" + SecurityValueInMn
				+ ", LimitSuffix=" + LimitSuffix + ", LimitPrefix=" + LimitPrefix + ", PolicyNo=" + PolicyNo
				+ ", PolicyAmount=" + PolicyAmount + ", SecurityCreated=" + SecurityCreated + ", RequestType="
				+ RequestType + ", InnerPojo=" + InnerPojo + ", requestId=" + requestId + ", dateAndTime=" + dateAndTime
				+ ", coltrlSrlNum=" + coltrlSrlNum + ", coltrlLinkage=" + coltrlLinkage + "]";
	}

}
