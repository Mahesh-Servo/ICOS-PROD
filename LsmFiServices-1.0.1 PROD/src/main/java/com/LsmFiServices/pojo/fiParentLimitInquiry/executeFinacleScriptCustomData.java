package com.LsmFiServices.pojo.fiParentLimitInquiry;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement
@XmlSeeAlso({ Limit_Details.class })
public class executeFinacleScriptCustomData {

	private static final Logger logger = LoggerFactory.getLogger(executeFinacleScriptCustomData.class);

	public executeFinacleScriptCustomData() {
		logger.info("inside executeFinacleScriptCustomData");
	}

	@XmlElement(name = "Limit_Details")
	List<Limit_Details> Limit_Details;

	public List<Limit_Details> getLimit_Details() {
		return Limit_Details;
	}

	public void setLimit_Details(List<Limit_Details> limit_Details) {
		Limit_Details = limit_Details;
	}

	@Override
	public String toString() {
		return "executeFinacleScriptCustomData [" + Limit_Details + "]";
	}

}
