package com.svt.model.commonModel;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

//@Data
//@Getter
//@Setter
//@ToString
public class InnerPojo {
	String SubTypeSecurity;
	String TypeOfSecurity;
	String Product;
	String TypeOfCharge;
	String ValSubTypeSecMn;
	String SecurityValueMn;
	String PolicyNumber;
	String PolicyAmount;
	String LimitPrefix;
	String LimitSuffix;
	String NameofHoldingStock;
	String unitValue;
	String noOfUnits;

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

	public String getValSubTypeSecMn() {
		return ValSubTypeSecMn;
	}

	public void setValSubTypeSecMn(String valSubTypeSecMn) {
		ValSubTypeSecMn = valSubTypeSecMn;
	}

	public String getSecurityValueMn() {
		return SecurityValueMn;
	}

	public void setSecurityValueMn(String securityValueMn) {
		SecurityValueMn = securityValueMn;
	}

	public String getPolicyNumber() {
		return PolicyNumber;
	}

	public void setPolicyNumber(String policyNumber) {
		PolicyNumber = policyNumber;
	}

	public String getPolicyAmount() {
		return PolicyAmount;
	}

	public void setPolicyAmount(String policyAmount) {
		PolicyAmount = policyAmount;
	}

	public String getLimitPrefix() {
		return LimitPrefix;
	}

	public void setLimitPrefix(String limitPrefix) {
		LimitPrefix = limitPrefix;
	}

	public String getLimitSuffix() {
		return LimitSuffix;
	}

	public void setLimitSuffix(String limitSuffix) {
		LimitSuffix = limitSuffix;
	}
	
	public String getNameofHoldingStock() {
		return NameofHoldingStock;
	}

	public void setNameofHoldingStock(String nameofHoldingStock) {
		NameofHoldingStock = nameofHoldingStock;
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
		return "InnerPojo [SubTypeSecurity=" + SubTypeSecurity + ", TypeOfSecurity=" + TypeOfSecurity + ", Product="
				+ Product + ", TypeOfCharge=" + TypeOfCharge + ", ValSubTypeSecMn=" + ValSubTypeSecMn
				+ ", SecurityValueMn=" + SecurityValueMn + ", PolicyNumber=" + PolicyNumber + ", PolicyAmount="
				+ PolicyAmount + ", LimitPrefix=" + LimitPrefix + ", LimitSuffix=" + LimitSuffix
				+ ", NameofHoldingStock=" + NameofHoldingStock + ", unitValue=" + unitValue + ", noOfUnits=" + noOfUnits
				+ "]";
	}
	
}
