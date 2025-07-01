package com.LsmFiServices.pojo.leinumenquiry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "AllPhoneDetail")
public class AllPhoneDetail {

	private static final Logger logger = LoggerFactory.getLogger(AllPhoneDetail.class);
	@XmlElement(name = "SerialNo")
	private int serialNo;
	@XmlElement(name = "phoneEmailType")
	private String phoneEmailType;
	@XmlElement(name = "phoneOrEmail")
	private String phoneOrEmail;
	@XmlElement(name = "phoneNumber")
	private String phoneNumber;
	@XmlElement(name = "phoneLocCode")
	private String phoneLocCode;
	@XmlElement(name = "phoneCityCode")
	private String phoneCityCode;
	@XmlElement(name="phoneCntryCode")
	private String phoneCntryCode;
	@XmlElement(name = "email")
	private String email;
	@XmlElement(name = "preferredFlg")
	private String preferredFlg;
	public static Logger getLogger() {
		return logger;
	}
	public int getSerialNo() {
		return serialNo;
	}
	public String getPhoneEmailType() {
		return phoneEmailType;
	}
	public String getPhoneOrEmail() {
		return phoneOrEmail;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public String getPhoneLocCode() {
		return phoneLocCode;
	}
	public String getPhoneCityCode() {
		return phoneCityCode;
	}
	public String getPhoneCntryCode() {
		return phoneCntryCode;
	}
	public String getEmail() {
		return email;
	}
	public String getPreferredFlg() {
		return preferredFlg;
	}
	@Override
	public String toString() {
		return "AllPhoneDetail [serialNo=" + serialNo + ", phoneEmailType=" + phoneEmailType + ", phoneOrEmail="
				+ phoneOrEmail + ", phoneNumber=" + phoneNumber + ", phoneLocCode=" + phoneLocCode + ", phoneCityCode="
				+ phoneCityCode + ", phoneCntryCode=" + phoneCntryCode + ", email=" + email + ", preferredFlg="
				+ preferredFlg + "]";
	}

	
	
}
