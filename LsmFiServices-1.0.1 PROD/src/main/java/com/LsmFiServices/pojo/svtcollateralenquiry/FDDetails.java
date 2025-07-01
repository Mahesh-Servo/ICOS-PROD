package com.LsmFiServices.pojo.svtcollateralenquiry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "FDDetails")
public class FDDetails {


	@XmlElement(name="sr_num")
	private String srNum;
	@XmlElement(name="Deposit_AcctNum")
	private String despositAcNo;
	@XmlElement(name="Collateral_Id")
	private String collateralId;
	

	public String getSrNum() {
		return srNum;
	}
	public void setSrNum(String srNum) {
		this.srNum = srNum;
	}
	public String getDespositAcNo() {
		return despositAcNo;
	}
	public void setDespositAcNo(String despositAcNo) {
		this.despositAcNo = despositAcNo;
	}
	public String getCollateralId() {
		return collateralId;
	}
	public void setCollateralId(String collateralId) {
		this.collateralId = collateralId;
	}
	
	@Override
	public String toString() {
		return "FDDetails [srNum=" + srNum + ", despositAcNo=" + despositAcNo + ", collateralId=" + collateralId + "]";
	}
}
