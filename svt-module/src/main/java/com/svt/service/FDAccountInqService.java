package com.svt.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.svt.utils.dataConnectivity.dbConnection;
import com.svt.controllers.FDAccountInquiryController;
import com.svt.utils.common.Constants;
import com.svt.utils.common.OperationUtillity;
import com.svt.utils.common.commonUtility;
import com.svt.utils.common.xmlToMap;
import com.svt.crypto.EncryptDecryptUtils;
import com.svt.crypto.HybridEncrDecr;
import com.svt.crypto.PayloadEncryptDecrypt;
import org.json.JSONObject;

@Service
public class FDAccountInqService {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FDAccountInquiryController.class);

	@Autowired
	private xmlToMap xmlToMapData;	

	public Map<String, String> FDAccInquiryService(String pinstid, String fdrNo, String ReviewDate) {

		Map<String, String> finalResult = new HashMap();
		logger.info("FDAccountInqService.FDAccInquiryService.pinstid [" + pinstid + "] FDRNO = [" + fdrNo + "] IN ");

		finalResult = executeFDAccountInquiryService(pinstid, fdrNo, ReviewDate);

		logger.info("FDAccountInqService.FDAccInquiryService.pinstid [" + pinstid + "] FDRNO = [" + fdrNo
				+ "] OUT FINAL RESULT [" + finalResult + "]");
		return finalResult;
	}

	public Map<String, String> executeFDAccountInquiryService(String pinstid, String fdrNo, String ReviewDate) {
		logger.info("FDAccountInqService.executeFDAccountInquiryService.pinstid [" + pinstid + "] FDRNO = [" + fdrNo
				+ "] IN ");
		String result = "", maturityDateRes = "", TDAcctId = "", SchmType = "", SchmCode = "", amountValue = "";
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate reviewDate = null, maturityDate = null;
		Map<String, String> API_REQ_RES_map = new HashMap<>();
		String soapRequestPacket = null;
		String Account_Inq_Response = "", HostTransaction = "", Status = "";
		try {
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
			soapRequestPacket = createFDAccountInquiryPacket(fdrNo);
			//String requestPacket = OperationUtillity.soapMessageToString(soapRequestPacket);
			
			logger.info("FDAccountInqService.executeFDAccountInquiryService.pinstid [" + pinstid + "] FDRNO = [" + fdrNo
					+ "] soapRequestPacket = [" + soapRequestPacket + "]");
//			SOAPMessage soapResponse = soapConnection.call(soapRequestPacket, Constants.FIWebService);

			//amit- start 
//			String encryptedRequestPacket =PayloadEncryptDecrypt.encrypt(OperationUtillity.soapMessageToString(soapRequestPacket), EncryptDecryptUtils.getAssymetricKey("RESPONSE_PUBLIC_KEY"), "FD_Enquiry");
//			logger.info("executeFDAccountInquiryService.Encrypted Rqeuest Packet ==> " + encryptedRequestPacket);
//
//            String Response = "";
//            String posidexResponse = "";
//            String cpcsResponse = "";
//            URL myURL = new URL("https://mwuat.icicibankltd.com/v1/api/FI-INT");
//            HttpURLConnection myConnection = null;
//            myConnection = (HttpURLConnection) myURL.openConnection();
//            myConnection.setDoOutput(true);
//            myConnection.setInstanceFollowRedirects(false);
//            myConnection.setRequestMethod("POST");
////            myConnection.setRequestProperty("apikey", "NGBRrfpMO7aUnvV6YKEADpc6S1ENiXTmYQf3YCUjXDITNApO"); //Bhushan
//            myConnection.setRequestProperty("Content-Type", "application/xml");
//            myConnection.setUseCaches(false);
//            try (OutputStream wr = myConnection.getOutputStream()) {
//                byte[] input = requestPacket.getBytes();
//                wr.write(input, 0, input.length);
//            } catch (Exception e) {
//            	logger.info("Error in writting request==>");
//                e.printStackTrace();
//            }
//            StringBuilder sb = new StringBuilder();
//            String abb = new String();
//            try {
//            int HttpResponseCode = myConnection.getResponseCode();
//            logger.info("Http Rsponse Code==> " + HttpResponseCode);
//            logger.info("Response Input Stream==> " + myConnection.getInputStream());
//                String encryptedResonsePacket = EncryptDecryptUtils.convertResponseToString(myConnection.getInputStream());
//                JSONObject json = new JSONObject(encryptedResonsePacket);
//                String encryptedKey = (String) json.get("encryptedKey");
//                logger.info("encrypted key from response==> " + encryptedKey);
//                String encryptedContent = (String) json.get("encryptedData");
//                logger.info("encrypted contentData from response==> " + encryptedContent);
////                Account_Inq_Response = PayloadEncryptDecrypt.decrypt(encryptedKey, "", encryptedContent, EncryptDecryptUtils.getAssymetricKey("RESPONSE_PRIVATE_KEY"));
////                logger.info("decrypted contentData from response==> " + posidexResponse);
//            } catch (Exception e) {
//            	logger.info("Exception in Decrypting Posidex Response-->"+e.getMessage());
//                e.printStackTrace();
//                }
			//amit-ends
			
//		String EncryptedPacket = HybridEncrDecr.getEncryptedRequestPacket(OperationUtillity.soapMessageToString(soapRequestPacket));
		Account_Inq_Response = commonUtility.getResponseFromMW(soapRequestPacket,Constants.FIWebService);
		logger.info("Middleware.Response.pinstid [" + pinstid + "] Account_Inq_Response = [" + Account_Inq_Response + "]");
		//	Account_Inq_Response = OperationUtillity.soapMessageToString(Account_Inq_Response);
			Account_Inq_Response = StringEscapeUtils.unescapeXml(Account_Inq_Response);
			if (Account_Inq_Response.contains("<HostTransaction>")) {
				HostTransaction = Account_Inq_Response.substring(
						Account_Inq_Response.indexOf("<HostTransaction>") + "<HostTransaction>".length(),
						Account_Inq_Response.indexOf("</HostTransaction>"));
				if (HostTransaction.contains("<Status>")) {
					Status = HostTransaction.substring(HostTransaction.indexOf("<Status>") + "<Status>".length(),
							HostTransaction.indexOf("</Status>"));
				}
			}
			if (Status.equalsIgnoreCase("SUCCESS")) {
				API_REQ_RES_map = xmlToMapData.successPacketDataToMapSanctionLimit(pinstid, Account_Inq_Response);
				if (Account_Inq_Response.contains("<MaturityDt>")) {
					maturityDateRes = Account_Inq_Response.substring(
							Account_Inq_Response.indexOf("<MaturityDt>") + "<MaturityDt>".length(),
							Account_Inq_Response.indexOf("</MaturityDt>"));
				}

				if (Account_Inq_Response.contains("<amountValue>")) {
					amountValue = Account_Inq_Response.substring(
							Account_Inq_Response.indexOf("<amountValue>") + "<amountValue>".length(),
							Account_Inq_Response.indexOf("</amountValue>"));

					if (!amountValue.equals("0.00") && Float.parseFloat(amountValue) != 0) {
						amountValue = String.valueOf(Float.parseFloat(amountValue) / 1000000);
					}
				}
				API_REQ_RES_map.put("amountValue", amountValue);

				if (Account_Inq_Response.contains("<TDAcctId>")) {
					TDAcctId = Account_Inq_Response.substring(
							Account_Inq_Response.indexOf("<TDAcctId>") + "<TDAcctId>".length(),
							Account_Inq_Response.indexOf("</TDAcctId>"));
					if (TDAcctId.contains("<SchmCode>")) {
						SchmCode = TDAcctId.substring(TDAcctId.indexOf("<SchmCode>") + "<SchmCode>".length(),
								TDAcctId.indexOf("</SchmCode>"));
					}
					if (TDAcctId.contains("<SchmType>")) {
						SchmType = TDAcctId.substring(TDAcctId.indexOf("<SchmType>") + "<SchmType>".length(),
								TDAcctId.indexOf("</SchmType>"));
					}
				}
				logger.info("FDAccountInqService.executeFDAccountInquiryService.pinstid [" + pinstid + "] FDRNO = ["
						+ fdrNo + "] maturityDateRes = [" + maturityDateRes + "] SchmCode = [" + SchmCode
						+ "] SchmType = [" + SchmType + "]");
				int schmCodeCount = getSchmCodeCnt(SchmCode);
				if (schmCodeCount > 0) {
					result = "Non-callable FD scheme code came in response -> [" + SchmCode + "]";
					API_REQ_RES_map.put("FinalResult", result);
					return API_REQ_RES_map;
				}
				if (!("TDA".equalsIgnoreCase(SchmType))) {
					result = "SchemeType is non TDA -> response -> [" + SchmType + "]";
					API_REQ_RES_map.put("FinalResult", result);
					return API_REQ_RES_map;
				}
				maturityDateRes = maturityDateRes.substring(0, 10);
				logger.info("FDAccountInqService.executeFDAccountInquiryService.pinstid [" + pinstid + "] FDRNO = ["
						+ fdrNo + "] maturityDateRes after substring = [" + maturityDateRes + "]");
				reviewDate = LocalDate.parse(ReviewDate, format);
				maturityDate = LocalDate.parse(maturityDateRes, format);
				logger.info("FDAccountInqService.executeFDAccountInquiryService.pinstid [" + pinstid + "] FDRNO = ["
						+ fdrNo + "] reviewDate = [" + reviewDate + "] maturityDate = [" + maturityDate + "]");
				if (reviewDate.isAfter(maturityDate)) {
					result = "FD security review date should not be greater than FD matuirty date [" + maturityDateRes
							+ "]";
					API_REQ_RES_map.put("FinalResult", result);
					return API_REQ_RES_map;
				}
				result = "SUCCESS";

			} else {
				API_REQ_RES_map = xmlToMapData.packetDataToMap(pinstid, Account_Inq_Response);
				result = "FD is Non Active [" + API_REQ_RES_map.get("ErrorDesc") + "]";
			}
		} catch (Exception Ex) {
			API_REQ_RES_map.put("RequestUUID", "");
			API_REQ_RES_map.put("MessageDateTime", "");
			API_REQ_RES_map.put("Error_Code", API_REQ_RES_map.get("ErrorCode"));
			API_REQ_RES_map.put("message", API_REQ_RES_map.get("ErrorDesc"));
			API_REQ_RES_map.put("Status", API_REQ_RES_map.get("Status"));
			API_REQ_RES_map.put("Error_At", "FD Account Enquiry");
			API_REQ_RES_map.put("Request", soapRequestPacket.toString());
			API_REQ_RES_map.put("Response", Account_Inq_Response);
			API_REQ_RES_map.put("FinalResult", Ex.getMessage());
			logger.info("FDAccountInqService.executeFDAccountInquiryService() Exception ->"
					+ OperationUtillity.traceException(pinstid, Ex));
		}

		try {
			logger.info("FDAccountInqService.executeFDAccountInquiryService() (FDR No =" + fdrNo
					+ ").API_REQ_RES_map=\n" + API_REQ_RES_map);
			OperationUtillity.API_RequestResponse_Insert(soapRequestPacket,
					Account_Inq_Response, "SVT FD ACCOUNT INQUIRY", pinstid, API_REQ_RES_map, "");
			// result = new ObjectMapper().writeValueAsString(API_REQ_RES_map);
		} catch (Exception e) {
			logger.info(
					"FDAccountInqService.executeFDAccountInquiryService() Exception while inserting API req and res ->"
							+ OperationUtillity.traceException(pinstid, e));
		}

		logger.info("FDAccountInqService.executeFDAccountInquiryService.pinstid [" + pinstid + "] FDRNO = [" + fdrNo
				+ "] Final result = [" + result + "]");
//	return result;
		API_REQ_RES_map.put("FinalResult", result);
		return API_REQ_RES_map;
	}

	public static String createFDAccountInquiryPacket(String fdrNo) throws IOException, SOAPException {

		String rqstPacket = "";
		try {
			String requestUuid = commonUtility.createRequestUUID();
			String dateAndTime = commonUtility.dateFormat();

			rqstPacket = "<XML>"
					+ "<MessageType>1200</MessageType>"
					+ "<ProcCode>FI0177</ProcCode>"
					+ "<FIXML xsi:schemaLocation = \"http://www.finacle.com/fixml TDAccInq.xsd\" xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n>"
					+ "<Header>\r\n" + "<RequestHeader>\r\n" + "<MessageKey>\r\n" + "<RequestUUID>" + requestUuid
					+ "</RequestUUID> \r\n" + "<ServiceRequestId>TDAcctInq</ServiceRequestId> \r\n"
					+ "<ServiceRequestVersion>10.2</ServiceRequestVersion> \r\n" + "<ChannelId>CLS</ChannelId> \r\n"
					+ "<LanguageId></LanguageId>\r\n" + "</MessageKey>\r\n" + "<RequestMessageInfo>\r\n"
					+ "<BankId>BM3</BankId> \r\n" + "<TimeZone></TimeZone>\r\n" + "<EntityId></EntityId>\r\n"
					+ "<EntityType></EntityType>\r\n" + "<ArmCorrelationId></ArmCorrelationId>\r\n"
					+ "<MessageDateTime>" + dateAndTime + "</MessageDateTime>\r\n" + "</RequestMessageInfo>\r\n"
					+ "<Security>\r\n" + "<Token>\r\n" + "<PasswordToken>\r\n" + "<UserId>\r\n" + "</UserId>\r\n"
					+ "<Password>\r\n" + "</Password>\r\n" + "</PasswordToken>\r\n" + "</Token>\r\n"
					+ "<FICertToken>\r\n" + "</FICertToken>\r\n" + "<RealUserLoginSessionId>\r\n"
					+ "</RealUserLoginSessionId>\r\n" + "<RealUser>\r\n" + "</RealUser>\r\n" + "<RealUserPwd>\r\n"
					+ "</RealUserPwd>\r\n" + "<SSOTransferToken>\r\n" + "</SSOTransferToken>\r\n" + "</Security>\r\n"
					+ "</RequestHeader>\r\n" + "</Header>\r\n" + "<Body>\r\n" + "<TDAcctInqRq>\r\n" + "	 <TDAcctId>"
					+ "       <AcctId>" + fdrNo + "</AcctId>" + "   </TDAcctId> \r\n" + "</TDAcctInqRq>\r\n"
					+ "</Body>\r\n"
					+ "</FIXML></XML>";
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Exception in createFDAccountInquiryPacket " + OperationUtillity.traceException(e));
		}

//		InputStream is = new ByteArrayInputStream(rqstPacket.getBytes());
//		SOAPMessage requestSoap = MessageFactory.newInstance().createMessage(null, is);
//		requestSoap.saveChanges();
		return rqstPacket;
	}

	public static int getSchmCodeCnt(String schmCode) { // ICO - 10080
		int schmCodeCnt = 0;
		try (Connection con = dbConnection.getConnection();
				PreparedStatement pst = con
						.prepareStatement("SELECT COUNT(1) AS CNT FROM FD_INQ_SCHMCODE_MS WHERE SCHM_CODE = ? ");) {
			pst.setString(1, schmCode);
			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {
					schmCodeCnt = rs.getInt("CNT");
				}
			}
			logger.info("OperationUtillity.getSchmCodeCnt :: schmCodeCnt :: " + schmCodeCnt);
		} catch (SQLException e) {
			logger.info("OperationUtillity.getSchmCodeCnt :: Exception :: " + OperationUtillity.traceException(e));
		}
		return schmCodeCnt;
	}
}
