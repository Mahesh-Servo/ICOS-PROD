package com.LsmFiServices.pojo.fiParentLimitInquiry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement(name = "Limit_Details")
@XmlAccessorType(XmlAccessType.FIELD)
public class Limit_Details {

	private static final Logger logger = LoggerFactory.getLogger(Limit_Details.class);

	@XmlElement
	Integer SerialNo;
	@XmlElement
	String ParentNode;
	@XmlElement
	String ParentPrefix;
	@XmlElement
	String ParentSuffix;

	public Integer getSerialNo() {
		return SerialNo == null ? 0 : SerialNo;
	}

	public void setSerialNo(Integer serialNo) {
		SerialNo = serialNo;
	}

	public String getParentNode() {
		return ParentNode == null ? "" : ParentNode;
	}

	public void setParentNode(String parentNode) {
		ParentNode = parentNode;
	}

	public String getParentPrefix() {
		return ParentPrefix == null ? "" : ParentPrefix;
	}

	public void setParentPrefix(String parentPrefix) {
		ParentPrefix = parentPrefix;
	}

	public String getParentSuffix() {
		return ParentSuffix == null ? "" : ParentSuffix;
	}

	public void setParentSuffix(String parentSuffix) {
		ParentSuffix = parentSuffix;
	}

	@Override
	public String toString() {
		return "{SerialNo:" + SerialNo + ", ParentNode:'" + ParentNode + "', ParentPrefix:'" + ParentPrefix
				+ "', ParentSuffix:'" + ParentSuffix + "'}";
	}

}
