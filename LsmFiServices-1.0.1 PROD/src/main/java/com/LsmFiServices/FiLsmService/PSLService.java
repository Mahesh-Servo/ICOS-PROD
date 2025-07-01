package com.LsmFiServices.FiLsmService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Map;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import com.LsmFiServices.Utility.OperationUtillity;
import com.LsmFiServices.Utility.commonUtility;

public class PSLService {

	public static SOAPMessage createSOAPRequest(String PINSTID, Map<String, String> GetEXTData, int i)
			throws IOException, SOAPException, ParseException {
		
		String typeOfAdvnc = "";
		if (GetEXTData.get("FACILITY_NAME").equalsIgnoreCase("Cash Credit")) {
			typeOfAdvnc = "10";
		}
		
		String subSectorCode = OperationUtillity.NullReplace(GetEXTData.get("SUB_SECTOR_CODE"));
		if(subSectorCode.equalsIgnoreCase("Others")) {
			subSectorCode = "OTHER";
		}
		
		String soapMessage = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<FIXML xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
				+ "<Header>" 
				+ "		<RequestHeader>"
				+ "			<MessageKey>" 
				+ "			<RequestUUID>"+ commonUtility.createRequestUUID() + "</RequestUUID>"
				+ "				<ServiceRequestId>executeFinacleScript</ServiceRequestId>"
				+ "				<ServiceRequestVersion>10.2</ServiceRequestVersion>"
				+ "				<ChannelId>CLS</ChannelId>"	 
				+ "				<LanguageId></LanguageId>"
				+ "			</MessageKey>" 
				+ "			<RequestMessageInfo>"
				+ "				<BankId>BM3</BankId>"
				+ "				<TimeZone></TimeZone>" 
				+ "				<EntityId></EntityId>"
				+ "				<EntityType></EntityType>" 
				+ "				<ArmCorrelationId></ArmCorrelationId>"
				+ "				<MessageDateTime>" + LocalDateTime.now() + "</MessageDateTime>"
				+ "			</RequestMessageInfo>" 
				+ "			<Security>"
				+ "				<Token>"
				+ "					<PasswordToken>" 
				+ "						<UserId></UserId>"
				+ "						<Password></Password>" 
				+ "					</PasswordToken>"
				+ "				</Token>" 
				+ "				<FICertToken></FICertToken>"
				+ "				<RealUserLoginSessionId></RealUserLoginSessionId>"
				+ "				<RealUser></RealUser>" 
				+ "				<RealUserPwd></RealUserPwd>"
				+ "				<SSOTransferToken></SSOTransferToken>" 
				+ "			</Security>"
				+ "		</RequestHeader>"
				+ "	</Header>" 
				+ "	<Body>" 
				+ "		<executeFinacleScriptRequest>"
				+ "			<ExecuteFinacleScriptInputVO>"
				+ "				<requestId>FI_updateMISFields.scr</requestId>"
				+ "			</ExecuteFinacleScriptInputVO>" 
				+ "			<executeFinacleScript_CustomData>"
				+ "				<accId>" + GetEXTData.get("ACCOUNT_NO_" + i) + "</accId>"
				+ "				<subSecCode>" + subSectorCode+ "</subSecCode>"
				+ "				<BorrCtrgyCode></BorrCtrgyCode>" 
				+ "				<PurposeOfAdvn>2.1</PurposeOfAdvn>" 
				+ "           <ModeOfAdvn>"	+ GetEXTData.get("MODE_OF_ADVANCE") + "</ModeOfAdvn>" 
				+ "				<typeofadvn>" + typeOfAdvnc+ "</typeofadvn>" 
				+ "				<nature_of_advn>1</nature_of_advn>"//was 2.2
				+ "				<industry_type>" + GetEXTData.get("INDUSTRY_CODE") + "</industry_type>"
				+ "				<FreeCode1></FreeCode1>" 
				+ "				<FreeCode3></FreeCode3>"
				+ "			    <FreeCode4>" + GetEXTData.get("DISTRICT_CODE") + "</FreeCode4>"
				+ "				<FreeCode7></FreeCode7>" 
				+ "				<sectorCode>"+ GetEXTData.get("SECTOR_CODE") + "</sectorCode>" 
					+ "		    <acctOccpCode>"+ GetEXTData.get("OCCUPATION_CODE") + "</acctOccpCode>" 
				+ "			</executeFinacleScript_CustomData>"
				+ "		</executeFinacleScriptRequest>" 
				+ "	</Body>" 
				+ "</FIXML>"
				+ "]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";
		InputStream is = new ByteArrayInputStream(soapMessage.getBytes());
		SOAPMessage requestSoap = MessageFactory.newInstance().createMessage(null, is);
		MimeHeaders headers = requestSoap.getMimeHeaders();
		requestSoap.saveChanges();
		return requestSoap;
	}
}
