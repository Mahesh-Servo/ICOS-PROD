package com.LsmFiServices.pojo.leinumenquiry;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({ AllDocDetail.class, AllPhoneDetail.class })
@XmlRootElement(name = "executeFinacleScriptCustomData")
public class ExecuteFinacleScriptResponse {

	@XmlElement(name = "AllDocDetail")
	private List<AllDocDetail> allDocDetails;
	@XmlElement(name = "AllPhoneDetail")
	private List<AllPhoneDetail> allPhoneDetails;
	@XmlElement(name = "CustPermAddr1")
	private String custPermAddr1;
	@XmlElement(name = "CustPermAddr2")
	private String custPermAddr2;
	@XmlElement(name = "CustPermAddr3")
	private String custPermAddr3;
	@XmlElement(name = "CustPermCityCode")
	private String custPermCityCode;
	@XmlElement(name = "CustPermStateCode")
	private String custPermStateCode;
	@XmlElement(name = "CustPermPinCode")
	private Long custPermPinCode;
	@XmlElement(name = "CustPermCntryCode")
	private String custPermCntryCode;
	@XmlElement(name = "CustComuAddr1")
	private String custComuAddr1;
	@XmlElement(name = "CustComuAddr2")
	private String custComuAddr2;
	@XmlElement(name = "CustComuAddr3")
	private String custComuAddr3;
	@XmlElement(name = "CustComuCityCode")
	private String custComuCityCode;
	@XmlElement(name = "CustComuStateCode")
	private String custComuStateCode;
	@XmlElement(name = "CustComuPinCode")
	private Long custComuPinCode;
	@XmlElement(name = "CustComuCntryCode")
	private String custComuCntryCode;
	@XmlElement(name = "CustSex")
	private String custSex;
	@XmlElement(name = "Nationality")
	private String nationality;
	@XmlElement(name = "DateOfBirth")
	private String dateOfBirth;
	@XmlElement(name = "CustMaritalStatus")
	private String custMaritalStatus;
	@XmlElement(name = "fatherName")
	private String fatherName;
	@XmlElement(name = "firstName")
	private String firstName;
	@XmlElement(name = "middleName")
	private String middleName;
	@XmlElement(name = "lastName")
	private String lastName;
	@XmlElement(name = "maidenName")
	private String maidenName;
	@XmlElement(name = "motherMaidenName")
	private String motherMaidenName;
	@XmlElement(name = "placeOfBirth")
	private String placeOfBirth;
	@XmlElement(name = "custMotherName")
	private String custMotherName;
	@XmlElement(name = "spouseName")
	private String spouseName;
	@XmlElement(name = "foreignIndicia")
	private String foreignIndicia;
	@XmlElement(name = "foreignTaxRep")
	private String foreignTaxRep;
	@XmlElement(name = "acctHoldFATCA")
	private String acctHoldFATCA;
	@XmlElement(name = "acctHoldCRS")
	private String acctHoldCRS;
	@XmlElement(name = "BOIDreq")
	private String boidReq;
	@XmlElement(name = "ResidingCountry")
	private String residingCountry;
	@XmlElement(name = "Citizenship")
	private String citizenship;
	
	public List<AllDocDetail> getAllDocDetails() {
		return allDocDetails;
	}
	public List<AllPhoneDetail> getAllPhoneDetails() {
		return allPhoneDetails;
	}
	public String getCustPermAddr1() {
		return custPermAddr1;
	}
	public String getCustPermAddr2() {
		return custPermAddr2;
	}
	public String getCustPermAddr3() {
		return custPermAddr3;
	}
	public String getCustPermCityCode() {
		return custPermCityCode;
	}
	public String getCustPermStateCode() {
		return custPermStateCode;
	}
	public Long getCustPermPinCode() {
		return custPermPinCode;
	}
	public String getCustPermCntryCode() {
		return custPermCntryCode;
	}
	public String getCustComuAddr1() {
		return custComuAddr1;
	}
	public String getCustComuAddr2() {
		return custComuAddr2;
	}
	public String getCustComuAddr3() {
		return custComuAddr3;
	}
	public String getCustComuCityCode() {
		return custComuCityCode;
	}
	public String getCustComuStateCode() {
		return custComuStateCode;
	}
	public Long getCustComuPinCode() {
		return custComuPinCode;
	}
	public String getCustComuCntryCode() {
		return custComuCntryCode;
	}
	public String getCustSex() {
		return custSex;
	}
	public String getNationality() {
		return nationality;
	}
	public String getDateOfBirth() {
		return dateOfBirth;
	}
	public String getCustMaritalStatus() {
		return custMaritalStatus;
	}
	public String getFatherName() {
		return fatherName;
	}
	public String getFirstName() {
		return firstName;
	}
	public String getMiddleName() {
		return middleName;
	}
	public String getLastName() {
		return lastName;
	}
	public String getMaidenName() {
		return maidenName;
	}
	public String getMotherMaidenName() {
		return motherMaidenName;
	}
	public String getPlaceOfBirth() {
		return placeOfBirth;
	}
	public String getCustMotherName() {
		return custMotherName;
	}
	public String getSpouseName() {
		return spouseName;
	}
	public String getForeignIndicia() {
		return foreignIndicia;
	}
	public String getForeignTaxRep() {
		return foreignTaxRep;
	}
	public String getAcctHoldFATCA() {
		return acctHoldFATCA;
	}
	public String getAcctHoldCRS() {
		return acctHoldCRS;
	}
	public String getBoidReq() {
		return boidReq;
	}
	public String getResidingCountry() {
		return residingCountry;
	}
	public String getCitizenship() {
		return citizenship;
	}
	@Override
	public String toString() {
		return "ExecuteFinacleScriptResponse [allDocDetails=" + allDocDetails + ", allPhoneDetails=" + allPhoneDetails
				+ ", custPermAddr1=" + custPermAddr1 + ", custPermAddr2=" + custPermAddr2 + ", custPermAddr3="
				+ custPermAddr3 + ", custPermCityCode=" + custPermCityCode + ", custPermStateCode=" + custPermStateCode
				+ ", custPermPinCode=" + custPermPinCode + ", custPermCntryCode=" + custPermCntryCode
				+ ", custComuAddr1=" + custComuAddr1 + ", custComuAddr2=" + custComuAddr2 + ", custComuAddr3="
				+ custComuAddr3 + ", custComuCityCode=" + custComuCityCode + ", custComuStateCode=" + custComuStateCode
				+ ", custComuPinCode=" + custComuPinCode + ", custComuCntryCode=" + custComuCntryCode + ", custSex="
				+ custSex + ", nationality=" + nationality + ", dateOfBirth=" + dateOfBirth + ", custMaritalStatus="
				+ custMaritalStatus + ", fatherName=" + fatherName + ", firstName=" + firstName + ", middleName="
				+ middleName + ", lastName=" + lastName + ", maidenName=" + maidenName + ", motherMaidenName="
				+ motherMaidenName + ", placeOfBirth=" + placeOfBirth + ", custMotherName=" + custMotherName
				+ ", spouseName=" + spouseName + ", foreignIndicia=" + foreignIndicia + ", foreignTaxRep="
				+ foreignTaxRep + ", acctHoldFATCA=" + acctHoldFATCA + ", acctHoldCRS=" + acctHoldCRS + ", boidReq="
				+ boidReq + ", residingCountry=" + residingCountry + ", citizenship=" + citizenship + "]";
	}



}
