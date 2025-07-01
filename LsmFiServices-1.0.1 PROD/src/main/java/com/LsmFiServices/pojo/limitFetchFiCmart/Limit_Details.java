package com.LsmFiServices.pojo.limitFetchFiCmart;

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
    String LimitPrefix;
    @XmlElement
    String LimitSuffix;
    @XmlElement
    Integer Level;
    @XmlElement
    Long SanctionLimit;
    @XmlElement
    double UtilizedAmount;
    @XmlElement
    String ExtensionDate;
    @XmlElement
    String ExpiryDate;

    public Integer getSerialNo() {
	return SerialNo == null ? 0 : SerialNo;
    }

    public void setSerialNo(Integer serialNo) {
	SerialNo = serialNo;
    }

    public String getLimitPrefix() {
	return LimitPrefix == null ? "" : LimitPrefix;
    }

    public void setLimitPrefix(String limitPrefix) {
	LimitPrefix = limitPrefix;
    }

    public String getLimitSuffix() {
	return LimitSuffix == null ? "" : LimitSuffix;
    }

    public void setLimitSuffix(String limitSuffix) {
	LimitSuffix = limitSuffix;
    }

    public Integer getLevel() {
	return Level == null ? 0 : Level;
    }

    public void setLevel(Integer level) {
	Level = level;
    }

    public Long getSanctionLimit() {
	return SanctionLimit == null ? 0 : SanctionLimit;
    }

    public void setSanctionLimit(Long sanctionLimit) {
	SanctionLimit = sanctionLimit;
    }

    public double getUtilizedAmount() {
	return UtilizedAmount == 0 ? 0.0 : UtilizedAmount;
    }

    public void setUtilizedAmount(double utilizedAmount) {
	UtilizedAmount = utilizedAmount;
    }

    public String getExtensionDate() {
	return ExtensionDate;
    }

    public void setExtensionDate(String extensionDate) {
	ExtensionDate = extensionDate;
    }

    public String getExpiryDate() {
	return ExpiryDate;
    }

    public void setExpiryDate(String expiryDate) {
	ExpiryDate = expiryDate;
    }

    @Override
    public String toString() {
	return "Limit_Details [SerialNo=" + SerialNo + ", LimitPrefix=" + LimitPrefix + ", LimitSuffix=" + LimitSuffix
		+ ", Level=" + Level + ", SanctionLimit=" + SanctionLimit + ", UtilizedAmount=" + UtilizedAmount
		+ ", ExtensionDate=" + ExtensionDate + ", ExpiryDate=" + ExpiryDate + "]";
    }

}