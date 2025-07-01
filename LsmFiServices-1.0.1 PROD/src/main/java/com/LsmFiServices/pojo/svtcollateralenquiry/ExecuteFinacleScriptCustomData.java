package com.LsmFiServices.pojo.svtcollateralenquiry;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({ FDDetails.class})
@XmlRootElement(name = "executeFinacleScriptCustomData")
public class ExecuteFinacleScriptCustomData {

	@XmlElement(name = "Cust_Id")
	private String custId;
	@XmlElement(name = "Scheme_Code")
	private String schemeCode;
	@XmlElement(name = "GL_Code")
	private String glCode;
	@XmlElement(name = "Status_Code")
	private String statusCode;
	
	@XmlElement(name = "FDDetails")
	private List<FDDetails> listOfFDDeatils;
	
	
	public String getCustId() {
		return custId;
	}
	public String getSchemeCode() {
		return schemeCode;
	}
	public String getGlCode() {
		return glCode;
	}
	public String getStatusCode() {
		return statusCode;
	}
	public List<FDDetails> getListOfFDDeatils() {
		return listOfFDDeatils;
	}
	@Override
	public String toString() {
		return "ExecuteFinalcleScriptCustomData [custId=" + custId + ", schemeCode=" + schemeCode + ", glCode=" + glCode
				+ ", statusCode=" + statusCode + ", listOfFDDeatils=" + listOfFDDeatils + "]";
	}
	
	
	
}
