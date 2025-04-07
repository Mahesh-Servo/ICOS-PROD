package com.svt.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.svt.dao.InquiryDao;
import com.svt.model.commonModel.InnerPojo;
import com.svt.model.commonModel.MainPojo;
import com.svt.utils.common.OperationUtillity;
import com.svt.utils.common.commonUtility;

@Service
public class InquiryService {

	private static final Logger logger = LoggerFactory.getLogger(InquiryService.class);

	@Autowired
	InquiryDao inquiryDao;

	public List<Map<String, String>> inquiryServiceImpl(String pinstid, String securityName, String subTypeSecurity,
			String typeOfSvt, String Product, String limitPrefix, String limitSuffix, String processName) {

		List<Map<String, String>> resultMap = new ArrayList<>();

		if (!Product.equals("ALL")) {

			List<Map<String, String>>  checkFromMaster = inquiryDao.checkInquiryDetailsInMaster(pinstid, securityName,
					subTypeSecurity, typeOfSvt, Product, limitPrefix, limitSuffix, processName);
			
			resultMap.addAll(checkFromMaster);

		} else {
			List<Map<String, String>> checkFromMaster = inquiryDao.checkInquiryDetailsInMaster(pinstid, securityName,
					subTypeSecurity, typeOfSvt, Product, limitPrefix, limitSuffix, processName);

			resultMap.addAll(checkFromMaster);
		}
		return resultMap;
	}

	public String createInquirydataPacket(MainPojo inquiryRequestPojo) {

		String rqstPacket = "";
		try {
			String requestUuid = commonUtility.createRequestUUID();
			String dateAndTime = commonUtility.dateFormat();

			rqstPacket = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<FIXML xsi:schemaLocation=\"http://www.finacle.com/fixml executeFinacleScript.xsd\" xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n"
					+ "<Header>\r\n" 
					+ "<RequestHeader>\r\n" 
					+ "<MessageKey>\r\n"
					+ "<RequestUUID>SESON1624565432175</RequestUUID> \r\n"
					+ "<ServiceRequestId>executeFinacleScript</ServiceRequestId> \r\n"
					+ "<ServiceRequestVersion>10.2</ServiceRequestVersion> \r\n"
					+ "<ChannelId>CLS</ChannelId> \r\n"
					+ "<LanguageId></LanguageId>\r\n" 
					+ "</MessageKey>\r\n" 
					+ "<RequestMessageInfo>\r\n"
					+ "<BankId>BM3</BankId> \r\n" 
					+ "<TimeZone>\r\n" + "</TimeZone>\r\n" 
					+ "<EntityId>\r\n"+ "</EntityId>\r\n" 
					+ "<EntityType>\r\n" + "</EntityType>\r\n" 
					+ "<ArmCorrelationId>\r\n"+ "</ArmCorrelationId>\r\n" 
					+ "<MessageDateTime>2023-10-05T11:52:31.724</MessageDateTime>\r\n"
					+ "</RequestMessageInfo>\r\n" 
					+ "<Security>\r\n" 
					+ "<Token>\r\n" 
					+ "<PasswordToken>\r\n"
					+ "<UserId>\r\n" + "</UserId>\r\n" 
					+ "<Password>\r\n" + "</Password>\r\n" 
					+ "</PasswordToken>\r\n"
					+ "</Token>\r\n" 
					+ "<FICertToken>\r\n" + "</FICertToken>\r\n" 
					+ "<RealUserLoginSessionId>\r\n"+ "</RealUserLoginSessionId>\r\n" 
					+ "<RealUser>\r\n" + "</RealUser>\r\n" 
					+ "<RealUserPwd>\r\n"+ "</RealUserPwd>\r\n" 
					+ "<SSOTransferToken>\r\n" + "</SSOTransferToken>\r\n" 
					+ "</Security>\r\n"
					+ "</RequestHeader>\r\n" 
					+ "</Header>\r\n"
					+ "<Body>\r\n" 
					+ "<executeFinacleScriptRequest>\r\n"
					+ "		<ExecuteFinacleScriptInputVO>\r\n" 
					+ "				<requestId>FI_cmn_Inquiry.scr</requestId> \r\n"
					+ "		</ExecuteFinacleScriptInputVO>\r\n" 
					+ "<executeFinacleScript_CustomData>\r\n"
					+ "		<serviceCode>ODDETAILS</serviceCode> \r\n" 
					+ "		<accountId>095251001149</accountId> \r\n"
					+ "</executeFinacleScript_CustomData>\r\n" 
					+ "</executeFinacleScriptRequest>\r\n" 
					+ "</Body>\r\n"
					+ "</FIXML>\r\n" + "]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";
			return rqstPacket;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Exception in InqueryscreateunlinkCollateraldataPacket" + OperationUtillity.traceException(e));
		}
		return rqstPacket;
	}

}
