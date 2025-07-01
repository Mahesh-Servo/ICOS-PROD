package com.LsmFiServices.pojo.leinumenquiry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "AllDocDetail")
public class AllDocDetail {

//	private static final Logger logger = LoggerFactory.getLogger(AllDocDetail.class);
	@XmlElement(name = "SerialNo")
	private int serialNo;
	@XmlElement(name = "DocCode")
	private String docCode;
	@XmlElement(name = "DocCodeDescr")
	private String docCodeDescr;
	@XmlElement(name = "DocType")
	private String docType;
	@XmlElement(name = "DocTypeDescr")
	private String docTypeDescr;
	@XmlElement(name = "RefNumber")
	private String refNumber;
	@XmlElement(name = "CountryIssue")
	private String countryIssue;
	@XmlElement(name = "PlaceIssue")
	private String placeIssue;
	@XmlElement(name = "IssueDate")
	private String issueDate;
	@XmlElement(name = "ExpiryDate")
	private String expiryDate;
	@XmlElement(name = "DelFlg")
	private String delFlg;
	@XmlElement(name = "Remarks")
	private String remarks;
	
	public int getSerialNo() {
		return serialNo;
	}
	public String getDocCode() {
		return docCode;
	}
	public String getDocCodeDescr() {
		return docCodeDescr;
	}
	public String getDocType() {
		return docType;
	}
	public String getDocTypeDescr() {
		return docTypeDescr;
	}
	public String getRefNumber() {
		return refNumber;
	}
	public String getCountryIssue() {
		return countryIssue;
	}
	public String getPlaceIssue() {
		return placeIssue;
	}
	public String getIssueDate() {
		return issueDate;
	}
	public String getExpiryDate() {
		return expiryDate;
	}
	public String getDelFlg() {
		return delFlg;
	}
	public String getRemarks() {
		return remarks;
	}
	@Override
	public String toString() {
		return "AllDocDetail [serialNo=" + serialNo + ", docCode=" + docCode + ", docCodeDescr=" + docCodeDescr
				+ ", docType=" + docType + ", docTypeDescr=" + docTypeDescr + ", refNumber=" + refNumber
				+ ", countryIssue=" + countryIssue + ", placeIssue=" + placeIssue + ", issueDate=" + issueDate
				+ ", expiryDate=" + expiryDate + ", delFlg=" + delFlg + ", remarks=" + remarks + "]";
	}
	
	

}
