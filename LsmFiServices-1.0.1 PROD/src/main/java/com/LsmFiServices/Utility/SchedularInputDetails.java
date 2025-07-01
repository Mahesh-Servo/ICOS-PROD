package com.LsmFiServices.Utility;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;
import javax.xml.soap.SOAPException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;

import com.LsmFiServices.FiLsmController.AccountAndLimitNodeLinkageController;
import com.LsmFiServices.FiLsmController.ChildLimitNodeCreationController;
import com.LsmFiServices.FiLsmController.ChildLimitNodeModificationController;
import com.LsmFiServices.FiLsmController.DrawingPowerCheckController;
import com.LsmFiServices.FiLsmController.DrawingPowerUpdationController;
import com.LsmFiServices.FiLsmController.FeeRecoveryController;
import com.LsmFiServices.FiLsmController.LEIUpdationController;
import com.LsmFiServices.FiLsmController.LEI_URCC_NumberEnquiryController;
import com.LsmFiServices.FiLsmController.PSLController;
import com.LsmFiServices.FiLsmController.ParentLimitNodeCreationController;
import com.LsmFiServices.FiLsmController.ParentLimitNodeModificationController;
import com.LsmFiServices.FiLsmController.RateLinksController;
import com.LsmFiServices.FiLsmController.SVTController;
import com.LsmFiServices.FiLsmController.SVTLinkageController;
import com.LsmFiServices.FiLsmController.SanctionedLimitController;
//import com.LsmFiServices.FiLsmController.SchemeAndGLCodeController;
//import com.LsmFiServices.FiLsmController.StatusCodeControllerN6ew;
import com.LsmFiServices.FiLsmController.URCCUpdationController;
import com.LsmFiServices.FiLsmController.leiUrcVerificationController;
import com.LsmFiServices.pojo.failedServiceExecutor.FailureServiceDetails;

@Component
@Scope("singleton")
public class SchedularInputDetails {
	private static final Logger logger = LoggerFactory.getLogger(SchedularInputDetails.class);

	@Autowired
	private AccountAndLimitNodeLinkageController accountAndLimitNodeLinkageController;

	@Autowired
	private ChildLimitNodeCreationController childLimitNodeCreationController;

	@Autowired
	private ChildLimitNodeModificationController childLimitNodeModificationController;

	@Autowired
	private DrawingPowerCheckController drawingPowerCheckController;

	@Autowired
	private DrawingPowerUpdationController drawingPowerUpdationController;

	@Autowired
	private FeeRecoveryController feeRecoveryController;

	@Autowired
	private LEI_URCC_NumberEnquiryController LEI_URCC_NumberEnquiryController;

	@Autowired
	private LEIUpdationController LEIUpdationController;

	@Autowired
	private leiUrcVerificationController leiUrcVerificationController;

	@Autowired
	private ParentLimitNodeCreationController parentLimitNodeCreationController;

	@Autowired
	private ParentLimitNodeModificationController parentLimitNodeModificationController;

	@Autowired
	private PSLController PSLController;

	@Autowired
	private RateLinksController rateLinksController;

	@Autowired
	private SanctionedLimitController sanctionedLimitController;

//	@Autowired
//	private SchemeAndGLCodeController schemeAndGLCodeController;

//	@Autowired
//	private StatusCodeControllerNew statusCodeControllerNew;

	@Autowired
	private URCCUpdationController URCCUpdationController;

	@Autowired
	private SVTController SVTController;

	@Autowired
	private SVTLinkageController SVTLinkageController;

//	@Autowired
//	private RestTemplate template;

	public Map<String, List<FailedServicesDetails>> failedServiceDetails() throws SQLException {
		String failedServiceQuery = "SELECT PINSTID, SERVICE_NAME,REQUEST_TYPE, STATUS FROM LSM_FI_EXECUTION_DETAILS WHERE STATUS IN ('FAILURE','Request Sent...!','Request Sent..!','SVT WITHDRAWAL FAILED Hence not executed','SVT DELINK FAILED Hence not executed\r\n"
				+ "','SVT INQUIRY FAILED Hence not executed','LODGE COLLATERAL FAILS,SVT WITHDRAWAL FAILED Hence not executed') AND REQUEST_TYPE != 'CRM_SERVICE' AND PINSTID IN (SELECT PINSTID FROM LSM_SCHEDULER_PINSTIDS WHERE FI_EXECUTION_DATE_TIME < (SYSDATE - INTERVAL '30' MINUTE)) ORDER BY DATETIME DESC";
		Map<String, List<FailedServicesDetails>> failedServiceDetailsMap = new HashMap<>();
		try (Connection con = DBConnect.getConnection()) {
			try (PreparedStatement pst = con.prepareStatement(failedServiceQuery); ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {
					FailedServicesDetails failedServicePj = new FailedServicesDetails();
					String pinstid = OperationUtillity.NullReplace(rs.getString("PINSTID"));
					String requestType = OperationUtillity.NullReplace(rs.getString("REQUEST_TYPE"));
					String serviceName = Optional.ofNullable(rs.getString("SERVICE_NAME")).orElse("");
					failedServicePj.setPinstid(pinstid);
					failedServicePj.setRequestType(requestType);
					failedServicePj.setStatus(OperationUtillity.NullReplace(rs.getString("STATUS")));
					failedServicePj.setServiceName(serviceName);
					failedServicePj.setFacility(requestTypeSplitter(requestType, serviceName).getFacility());
					failedServiceDetailsMap.computeIfAbsent(pinstid, k -> new ArrayList<>()).add(failedServicePj);
				}
			}
		} catch (Exception ex) {
			logger.error("SchedularInputDetails.failedServiceDetails()" + OperationUtillity.traceException(ex));
		}
//		logger.info("SchedularInputDetails.failedServiceDetails() failedServiceDetailsMap {}\n",
//				failedServiceDetailsMap);
		logger.info("SchedularInputDetails.failedServiceDetails() executed {}\n");
		return failedServiceDetailsMap;
	}

	public FailureServiceDetails requestTypeSplitter(String requestType, String serviceName) {
		FailureServiceDetails faildDetails = new FailureServiceDetails();
		List<String> nameList = Arrays.asList("CHILD LIMIT NODE MODIFICATION", "CHILD LIMIT NODE CREATION",
				"SANCTION LIMIT NODE", "ACCOUNT AND LIMIT NODE LINKAGE", "RATE OF INTEREST", "PSL", "SVT",
				"SVT LINKAGE");
		if (requestType.contains(":")) {
			if (requestType.contains("URCC NUMBER") || requestType.contains("LEI NUMBER")) {
				faildDetails.setServiceName(requestType.split(":")[0].split(",")[0].trim());
			} else if (nameList.contains(serviceName)) {
				faildDetails.setFacility(requestType.split(":")[1].trim());
			} else if (requestType.contains("LEI_URCC_NUMBER_ENQUIRY")) {
				faildDetails.setFacility(requestType.split("::")[1].trim());
			} else if (requestType.contains("FEE RECOVERY SERVICE")) {
				faildDetails.setFacility(requestType);
			} else if (requestType.contains("DRAWING POWER CHECK") || requestType.contains("DRAWING POWER UPDATE")) {
				faildDetails.setFacility(requestType.split("::")[2].trim());
			} else if (requestType.contains("SVT DELINK") || requestType.contains("SVT WITHDRAWAL")) {
				faildDetails.setFacility(requestType.split(":")[4].trim());
			}
		} else {// parent case
			faildDetails.setServiceName(requestType);
		}
		return faildDetails;
	}

//	@Scheduled(cron = "0 */30 * * * *")
	public void executeSchedulerService() throws Exception {
		logger.info("SchedularInputDetails.executeSchedulerService() :: {}\n", LocalDateTime.now().toString());
		try {
			for (Map.Entry<String, List<FailedServicesDetails>> individualMap : failedServiceDetails().entrySet()) {
				for (FailedServicesDetails individualRequestPojo : individualMap.getValue()) {

					try {
						executeServices(individualRequestPojo);
						insertSchedulerData(individualRequestPojo);
					} catch (InterruptedException e) {
						logger.error("SchedularInputDetails.executeSchedulerService(){}",
								OperationUtillity.traceException(e));
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					} catch (SOAPException e) {
						e.printStackTrace();
					}
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();

		}
	}

	public void executeServices(FailedServicesDetails failedServicesDetails) throws Exception {
		logger.info("SchedularInputDetails.executeServices() :: Executing for PINSTID :: "
				+ failedServicesDetails.getPinstid() + " and SERVICE_NAME :: "
				+ failedServicesDetails.getServiceName());
		HttpServletRequest httpRequest = null;

		switch (failedServicesDetails.getServiceName()) {
		case "PARENT LIMIT NODE CREATION":
			parentLimitNodeCreationController.parentLimitNode(failedServicesDetails.getPinstid(), httpRequest);
			break;
		case "PARENT LIMIT NODE MODIFICATION":
			parentLimitNodeModificationController.parentLimitNodeModification(failedServicesDetails.getPinstid(),
					httpRequest);
			break;
		case "CHILD LIMIT NODE CREATION":
			childLimitNodeCreationController.ChildLimitNodeCreation(failedServicesDetails.getPinstid(),
					failedServicesDetails.getFacility(), httpRequest);
			break;
		case "CHILD LIMIT NODE MODIFICATION":
			childLimitNodeModificationController.childLimitNodeModification(failedServicesDetails.getPinstid(),
					failedServicesDetails.getFacility(), httpRequest);
			break;
		case "ACCOUNT AND LIMIT NODE LINKAGE":
			accountAndLimitNodeLinkageController.accountAndLimitNodeLinkage(failedServicesDetails.getPinstid(),
					failedServicesDetails.getFacility());
			break;
		case "DRAWING POWER CHECK":
			drawingPowerCheckController.executeDrawingPowerCheckService(failedServicesDetails.getPinstid(),
					failedServicesDetails.getFacility());
			break;
		case "DRAWING POWER UPDATE":
			drawingPowerUpdationController.drgPwrUpdationHandler(failedServicesDetails.getPinstid(),
					failedServicesDetails.getFacility());
			break;
//		case "FEE RECOVERY":
//			feeRecoveryController.feeRecovery(failedServicesDetails.getPinstid(), failedServicesDetails.getFacility());
//			break;
		case "LEI NUMBER":
			String operation = failedServicesDetails.getRequestType().split(" ")[2];
			LEIUpdationController.runLEIUpdation(failedServicesDetails.getPinstid(), operation, new HashMap<>());
			break;
		case "LEI VERIFICATION":
			leiUrcVerificationController.leiUrcVerificationCntlr(failedServicesDetails.getPinstid(),
					failedServicesDetails.getFacility());
			break;
		case "LEI_URCC_NUMBER_ENQUIRY":
			LEI_URCC_NumberEnquiryController.runLEI_URCCNumberEnquiry(failedServicesDetails.getPinstid(),
					failedServicesDetails.getFacility());
			break;
//		case "LODGE COLLATERAL":
//			String svtLodge = "http://" + InetAddress.getLocalHost().getHostAddress() + ":" + Constants.PORT
//					+ "/svt-module/lodgeCollateralController?PINSTID=" + failedServicesDetails.getPinstid()
//					+ "&processName=Limit_Setup&securityName=ALL&subTypeSecurity=ALL&typeOfSvt=ALL&product=ALL";
//			logger.info(
//					"SchedularInputDetails.executeServices() :: Constructed URL for LODGE COLLATERAL :: " + svtLodge);
//			template.getForObject(svtLodge, String.class);
//			break;
		case "PSL":
			PSLController.executePSLServiceIndividually(failedServicesDetails.getPinstid(),
					failedServicesDetails.getFacility());
			break;
		case "RATE OF INTEREST":
			rateLinksController.rateOfInterest(failedServicesDetails.getPinstid(), failedServicesDetails.getFacility());
			break;
		case "SANCTION LIMIT NODE":
			sanctionedLimitController.runSanctionedLimitIndividually(failedServicesDetails.getPinstid(),
					failedServicesDetails.getFacility());
			break;
//		case "SCHEME CODE UPDATION":
//			schemeAndGLCodeController.executeSchemeCodeUpdation(failedServicesDetails.getPinstid(),
//					failedServicesDetails.getRequestType());
//			break;
//		case "STATUS CODE UPDATION":
//			statusCodeControllerNew.statusCodeUpdate(failedServicesDetails.getPinstid(),
//					failedServicesDetails.getRequestType());
//			break;
		case "SVT":
//			String svtUrl = "http://" + InetAddress.getLocalHost().getHostAddress() + ":" + Constants.PORT
//					+ "/svt-module/common?PINSTID=" + failedServicesDetails.getPinstid()
//					+ "&processName=Limit_Setup&securityName=ALL&subTypeSecurity=ALL&typeOfSvt=ALL&product=ALL";
//			logger.info("SchedularInputDetails.executeServices() :: Constructed URL for SVT :: " + svtUrl);
//			template.getForObject(svtUrl, String.class);
//			lodgeCollateralController.lodgeCollateralCtrl(failedServicesDetails.getPinstid(), "ALL", "ALL", "ALL", "ALL", "Limit_Setup", req);
			SVTController.runSVTService(failedServicesDetails.getPinstid(), failedServicesDetails.getFacility());
			break;
//		case "SVT DELINK":
//			Map<String, String> prefixSuffixMapDelink = new HashMap<>();
//			prefixSuffixMapDelink = OperationUtillity.fetchLimitPrefixSuffix(failedServicesDetails.getPinstid(),
//					failedServicesDetails.getFacility());
//			String svtDelinkUrl = "http://" + InetAddress.getLocalHost().getHostAddress() + ":" + Constants.PORT
//					+ "/svt-module/unlink?pinstid=" + failedServicesDetails.getPinstid()
//					+ "&processName=Limit_Setup&securityName=ALL&subTypeSecurity=ALL&typeOfSvt=ALL&product=ALL&limitPrefix="
//					+ prefixSuffixMapDelink.get("limitPrefix") + "&limitSuffix="
//					+ prefixSuffixMapDelink.get("limitSuffix");
//			logger.info("SchedularInputDetails.executeServices() :: Constructed URL for SVT DELINK :: " + svtDelinkUrl);
//			template.getForObject(svtDelinkUrl, String.class);
//			break;
		case "SVT LINKAGE":
//			String svtLinkageUrl = "http://" + InetAddress.getLocalHost().getHostAddress() + ":" + Constants.PORT
//					+ "/svt-module/lodgeCollateralLinkage?PINSTID=" + failedServicesDetails.getPinstid()
//					+ "&processName=Limit_Setup&securityName=ALL&subTypeSecurity=ALL&typeOfSvt=ALL&product=ALL";
//			template.getForObject(svtLinkageUrl, String.class);
//			SVTLinkageController.runSVTLinkageService(failedServicesDetails.getPinstid(),
//					failedServicesDetails.getRequestType());
			SVTLinkageController.runSVTLinkageService(failedServicesDetails.getPinstid(),
					failedServicesDetails.getFacility());
			break;
//		case "SVT WITHDRAWAL":
//			Map<String, String> prefixSuffixMapWithdraw = new HashMap<>();
//			prefixSuffixMapWithdraw = OperationUtillity.fetchLimitPrefixSuffix(failedServicesDetails.getPinstid(),
//					failedServicesDetails.getFacility());
//			String svtWithdrawalUrl = "http://" + InetAddress.getLocalHost().getHostAddress() + ":" + Constants.PORT
//					+ "/svt-module/withdrawal?PINSTID=" + failedServicesDetails.getPinstid()
//					+ "&processName=Limit_Setup&securityName=ALL&subTypeSecurity=ALL&typeOfSvt=ALL&product=ALL&limitPrefix="
//					+ prefixSuffixMapWithdraw.get("limitPrefix") + "&limitSuffix="
//					+ prefixSuffixMapWithdraw.get("limitSuffix");
//			logger.info("SchedularInputDetails.executeServices() :: Constructed URL for SVT WITHDRAWAL :: "
//					+ svtWithdrawalUrl);
//			template.getForObject(svtWithdrawalUrl, String.class);
//			break;
		case "URCC NUMBER":
			String operation1 = failedServicesDetails.getRequestType().split(" ")[2];
			URCCUpdationController.runURCCUpdation(failedServicesDetails.getPinstid(), operation1, new HashMap<>());
			break;
		case "URCC VERIFICATION":
			leiUrcVerificationController.leiUrcVerificationCntlr(failedServicesDetails.getPinstid(),
					failedServicesDetails.getFacility());
			break;
		default:
			break;
		}
	}

	public void insertSchedulerData(FailedServicesDetails individualRequestPojo) throws SQLException {

		String statement = "INSERT INTO LSM_SCHEDULER_DATA (PINSTID, SERVICE_NAME, REQUEST_TYPE, HOST_NAME, DATE_TIME) VALUES (?, ?, ?,?, SYSDATE)";
		try (Connection con = DBConnect.getConnection()) {
			try (PreparedStatement pst = con.prepareStatement(statement)) {
				pst.setString(1, OperationUtillity.NullReplace(individualRequestPojo.getPinstid()));
				pst.setString(2, OperationUtillity.NullReplace(individualRequestPojo.getServiceName()));
				pst.setString(3, OperationUtillity.NullReplace(individualRequestPojo.getRequestType()));
				pst.setString(4, InetAddress.getLocalHost().getHostAddress());
				int int_count = pst.executeUpdate();
				if (int_count > 0) {
					con.commit();
				}
			}
		} catch (Exception e) {
			logger.error("SchedularInputDetails.LSM_Insert_Scheduler_Data().exception: ",
					OperationUtillity.traceException(e));
		}
	}
}
