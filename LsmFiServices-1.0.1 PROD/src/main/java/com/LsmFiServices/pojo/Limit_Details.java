package com.LsmFiServices.pojo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Limit_Details")
@XmlAccessorType(XmlAccessType.FIELD)
public class Limit_Details {


	@XmlElement
	Integer SerialNo;
	@XmlElement
	String ParentNode;
	@XmlElement
	String ParentPrefix;
	@XmlElement
	String ParentSuffix;

	public Integer getSerialNo() {
		return SerialNo;
	}

//	public void setSerialNo(Integer serialNo) {
//		SerialNo = serialNo;
//	}

	public String getParentNode() {
		return ParentNode;
	}

//	public void setParentNode(String parentNode) {
//		ParentNode = parentNode;
//	}

	public String getParentPrefix() {
		return ParentPrefix;
	}

//	public void setParentPrefix(String parentPrefix) {
//		ParentPrefix = parentPrefix;
//	}

	public String getParentSuffix() {
		return ParentSuffix;
	}

//	public void setParentSuffix(String parentSuffix) {
//		ParentSuffix = parentSuffix;
//	}

	@Override
	public String toString() {
		return "Limit_Details [SerialNo=" + SerialNo + ", ParentNode=" + ParentNode + ", ParentPrefix=" + ParentPrefix
				+ ", ParentSuffix=" + ParentSuffix + "]";
	}

}
