package com.LsmFiServices.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import com.LsmFiServices.FiLsmService.RateOfInterestService;
import com.LsmFiServices.Utility.Constants;
import com.LsmFiServices.Utility.OperationUtillity;
import com.LsmFiServices.Utility.ServiceDetails;
import com.LsmFiServices.Utility.xmlToMap;
import com.LsmFiServices.pojo.serviceexecutiondetails.ServiceDetailsUpdatePojo;

@Controller
public class RateOfInterestDao {

	private static final Logger logger = LoggerFactory.getLogger(RateOfInterestDao.class);

	@Autowired
	private ServiceDetails serviceDetails;

	public @ResponseBody Map<String, String> RateOfInterestDaoImpl(String PINSTID,
			Map<String, String> rateOfInterestMap, int i) throws SOAPException, SQLException {

		Map<String, String> API_REQ_RES_map = new HashMap<>();
		String roiResponse = "";
		SOAPMessage soapRequest = null;
		String RequestUUID = "", HostTransaction = "", Status = "";
		ServiceDetailsUpdatePojo pojo = new ServiceDetailsUpdatePojo();
		String requestType = "";

		try {
			pojo.setPinstId(PINSTID);
			pojo.setServiceName("RATE OF INTEREST");
			pojo.setAccountNumber(rateOfInterestMap.get("ACCOUNT_NO_" + i));
			pojo.setFacility(rateOfInterestMap.get("FACILITY_NAME"));
			String roiBodyData = getSelectiveRespose(rateOfInterestMap, i);
			requestType = "RATE OF INTEREST : " + rateOfInterestMap.get("FACILITY_NAME") + " : Data =  " + roiBodyData;
			pojo.setRequestType(requestType);
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
			soapRequest = RateOfInterestService.createSOAPRequest(PINSTID, rateOfInterestMap, i);
			pojo.setServiceRequest(OperationUtillity.soapMessageToString(soapRequest));
			pojo.setStatus("Request Sent...!");
			pojo.setReTrigger(true);
			serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
			SOAPMessage soapResponse = soapConnection.call(soapRequest, Constants.FIWebService);
			roiResponse = OperationUtillity.soapMessageToString(soapResponse);
			roiResponse = StringEscapeUtils.unescapeXml(roiResponse);
			if (roiResponse.contains("<HostTransaction>")) {
				HostTransaction = roiResponse.substring(
						roiResponse.indexOf("<HostTransaction>") + "<HostTransaction>".length(),
						roiResponse.indexOf("</HostTransaction>"));
				if (HostTransaction.contains("<Status>")) {
					Status = HostTransaction.substring(HostTransaction.indexOf("<Status>") + "<Status>".length(),
							HostTransaction.indexOf("</Status>"));
				}
			}
			if (Status.equalsIgnoreCase("SUCCESS")) {
				API_REQ_RES_map = xmlToMap.successPacketDataToMapRateofInterest(PINSTID, roiResponse);
				pojo.setStatus(API_REQ_RES_map.get("Status"));
				pojo.setServiceResponse(roiResponse);
				pojo.setRequestType(requestType);
				pojo.setReTrigger(false);
				serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
				OperationUtillity.updateLimitTabDateForRoI(PINSTID);
			} else {
				API_REQ_RES_map = xmlToMap.packetDataToMap(PINSTID, roiResponse);
				pojo.setRequestType(requestType);
				pojo.setStatus(API_REQ_RES_map.get("Status"));
				pojo.setMessage(API_REQ_RES_map.get("ErrorDesc"));
				pojo.setServiceResponse(roiResponse);
				pojo.setReTrigger(true);
				serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
			}
			OperationUtillity.API_RequestResponse_Insert(OperationUtillity.soapMessageToString(soapRequest),
					roiResponse,
					"RATE OF INTEREST : " + rateOfInterestMap.get("FACILITY_NAME") + " : Data =  " + roiBodyData,
					PINSTID, API_REQ_RES_map, RequestUUID);
		} catch (Exception Ex) {
			API_REQ_RES_map.put("RequestUUID", "");
			API_REQ_RES_map.put("MessageDateTime", "");
			API_REQ_RES_map.put("Error_Code", "500");
			API_REQ_RES_map.put("message", Ex.getMessage());
			API_REQ_RES_map.put("Status", "FAILED");
			API_REQ_RES_map.put("request header", soapRequest.getSOAPHeader().toString());
			API_REQ_RES_map.put("request body", soapRequest.getSOAPBody().toString());
			logger.error("RateOfInterestDao.RateOfInterestDaoImpl()\n{}", OperationUtillity.traceException(Ex));
		}
		return API_REQ_RES_map;
	}

	public static String getSelectiveRespose(Map<String, String> rOIData, int i) {
		String selectiveResponse = "";
		try {
			selectiveResponse = "ACCOUNT_NUMBER : " + rOIData.get("ACCOUNT_NO_" + i) + ", RATE_CODE : "
					+ rOIData.get("RATE_CODE") + ", ROI_SPREAD : " + rOIData.get("ROI_SPREAD") + ", ROI_PEGGED_FLAG : "
					+ rOIData.get("ROI_PEGGED_FLAG") + ", PEGGING_FREQUENCY_IN_MONTHS : "
					+ rOIData.get("PEGGING_FREQUENCY") + ", NUMBER_OF_DAYS : " + rOIData.get("NUMBER_OF_DAYS")
					+ ", ROI_Spread : " + rOIData.get("ROI_SPREAD") + ", NUMBER_OF_DAYS_DATE : "
					+ rOIData.get("ROI_END_DATE");
		} catch (Exception e) {
			logger.info("RateOfInterestDao.getSelectiveRespose() :: " + OperationUtillity.traceException(e));
		}
		return selectiveResponse;
	}
}
