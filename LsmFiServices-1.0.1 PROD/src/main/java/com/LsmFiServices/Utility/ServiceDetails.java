package com.LsmFiServices.Utility;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.LsmFiServices.pojo.serviceexecutiondetails.ResponseWrapper;
import com.LsmFiServices.pojo.serviceexecutiondetails.ServiceDetailsUpdatePojo;
import com.LsmFiServices.pojo.serviceexecutiondetails.ServiceExecutionDetails;

@Service
public class ServiceDetails {

	private static final Logger logger = LoggerFactory.getLogger(ServiceDetails.class);

	@Autowired
	private LEI_URCCNumberEnquiryUtility utility;

	@Autowired
	private SVTFIServiceUtility svtFIServiceUtility;

	@Autowired
	private SVTLinkageServiceUtility svtLinkageUtils;

	@Autowired
	private ParentLimitNodeModificationUtility parentModUtility;

	@Autowired
	private ESMUtils esmUtils;

	@Autowired
	private CreateFacilityLimitList createFacilityLimitList;

	@Autowired
	private LEI_URCCNumberEnquiryUtility leiUrcUtil;

	@Autowired
	private DrawingPowerUtility drgPwrUtils;

	@Autowired
	private ParentLimitNodeModificationUtility parentLimitNodeModificationUtility;

	public int[] insertServiceExecutionDetails(String pinstId) {
		int[] afftectedRows = {};
		ResponseWrapper respWrapper = prepareServiceExecutionDetails(pinstId);

		if (!("".equalsIgnoreCase(respWrapper.getMessage())) && respWrapper.getMessage() != null) {
			return afftectedRows;
		}
		List<ServiceExecutionDetails> inputDataList = respWrapper.getListPojo();
		logger.info("Entered into ServiceDetails.insertServiceExecutionDetails()-->" + pinstId);
		try (Connection con = DBConnect.getConnection();
				PreparedStatement ps = con.prepareStatement(Queries.SAVE_SERVICE_EXECUTION_DETAILS)) {
			for (ServiceExecutionDetails ipDetails : inputDataList) {
				ps.setString(1, pinstId);
				ps.setString(2, ipDetails.getServiceName());
				ps.setString(3, ipDetails.getRequestType());
				ps.setString(4, ipDetails.getFacility());
				ps.setString(5, ipDetails.getAccountNumber());
				ps.setString(6, ipDetails.getRequest());
				ps.setString(7, ipDetails.getResponse());
				ps.setString(8, ipDetails.getStatus());
				ps.setString(9, ipDetails.getMessage());
				ps.setString(10, InetAddress.getLocalHost().getHostAddress());
				ps.addBatch();
			}
			afftectedRows = ps.executeBatch();
			if (afftectedRows.length > 0) {
				con.commit();
			}
		} catch (Exception e) {
			logger.info("ServiceDetails.insertServiceExecutionDetails() " + OperationUtillity.traceException(e));
		}
		return afftectedRows;
	}

	public Map<String, List<String>> getServiceInputDetails(String pinstId) {
		logger.info("Entered into ServiceDetails.getServiceInputDetails()" + pinstId);
		List<String> facilityList = RevisedServiceSequence.getAllFacilities(pinstId);
		Map<String, List<String>> map = new LinkedHashMap<>();
		for (String facility : facilityList) {
			List<String> accountNumbers = new LinkedList<>();
			try (Connection con = DBConnect.getConnection();
					PreparedStatement ps = con.prepareStatement(Queries.GET_SERVICE_INPUT_DETAILS)) {
				ps.setString(1, pinstId);
				ps.setString(2, facility);
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						accountNumbers.add(OperationUtillity.NullReplace(rs.getString("ACCOUNT_NUMBER")));
					}
				}
			} catch (Exception e) {
				logger.info("ServiceDetails.saveServiceDetails().Exception {}", OperationUtillity.traceException(e));
			}
			map.put(facility, accountNumbers);
		}
		logger.info("Pinstid :: " + pinstId + " & map check {} ", map);
		return map;
	}

	public ResponseWrapper prepareServiceExecutionDetails(String pinstId) {

		logger.info("ServiceDetails.prepareServiceExecutionDetails()" + pinstId);
		List<ServiceExecutionDetails> listOfPojo = new LinkedList<>();
		ResponseWrapper respWrapper = new ResponseWrapper();
		String proposalType = parentModUtility.getProposalType(pinstId);
		String limitType = parentLimitNodeModificationUtility.getLimitType(pinstId); // ADDED BY MAHESHV ON 27122024 FOR
																						// RENEWAL

		if (esmUtils.caseTypeIsESM(pinstId)) {
			try {
				logger.info(pinstId + " :: Case type is plain ESM");
				String childaction = "CREATION";
				List<Map<String, String>> childFacilityList = createFacilityLimitList.createChildList(pinstId);

				if (!"FRESH SANCTION".equalsIgnoreCase(proposalType)) {
					listOfPojo.add(new ServiceExecutionDetails(pinstId, "PARENT LIMIT NODE MODIFICATION", null, null,
							null, null, null, "This service must success for further execution", null));
				} else {
					listOfPojo.add(new ServiceExecutionDetails(pinstId, "PARENT LIMIT NODE CREATION", null, null, null,
							null, null, "This service must success for further execution", null));
				}
				for (Map<String, String> inputESMMap : childFacilityList) {
					String facility = inputESMMap.get("FACILITY_NAME");

					if (proposalType.contains("RENEWAL") || proposalType.contains("ENHANCEMENT")) {
						logger.info(
								"ServiceDetails.prepareServiceExecutionDetails() :: Child Service check for PINSTID "
										+ pinstId + " :: Into 1st condition");
						childaction = "MODIFICATION";
					}
					if (!facility.contains("CROSS CALL") && !"FRESH SANCTION".equalsIgnoreCase(proposalType)
							&& parentModUtility.isLimitAsPerSanctionIsZero(pinstId, facility)) {
						logger.info(
								"ServiceDetails.prepareServiceExecutionDetails() :: Child Service check for PINSTID "
										+ pinstId + " :: Into 2nd condition");
						childaction = "CREATION";
					} else if (facility.contains("CROSS CALL") && (!"FRESH SANCTION".equalsIgnoreCase(proposalType)
							&& parentModUtility.isLimitAsPerSanctionIsZeroForCrossCall(pinstId, facility))) {
						logger.info(
								"ServiceDetails.prepareServiceExecutionDetails() :: Child Service check for PINSTID "
										+ pinstId + " :: Into 3rd condition");
						childaction = "CREATION";
					}
					logger.info("ServiceDetails.prepareServiceExecutionDetails() :: Child Service check for PINSTID "
							+ pinstId + " :: action2 :: " + childaction);

					if (facility.contains("CROSS CALL")) {
						if (isLimitAmountZeroCrossCall(pinstId, facility))
							continue; // if limit amount is 0 then child and subsequent services wont execute
					} else {
						if (isLimitAmountZero(pinstId, facility)) {
							continue; // if limit amount is 0 then child and subsequent services wont execute
						}
					}
					listOfPojo.add(new ServiceExecutionDetails(pinstId, "CHILD LIMIT NODE " + childaction, null,
							inputESMMap.get("FACILITY_NAME"), null, null, null,
							"Parent service must be success for further execution", null));
				}

				respWrapper.setListPojo(listOfPojo);
				return respWrapper;
			} catch (SQLException e) {
				logger.info("ServiceDetails.prepareServiceExecutionDetails()-->" + OperationUtillity.traceException(e));
			}

		} else if (esmUtils.getESMFlagForLSM(pinstId)) {
			try {
				logger.info(pinstId + " :: Case type is LSM after ESM");
				Map<String, List<String>> inputMap = getServiceInputDetails(pinstId);
				listOfPojo.add(new ServiceExecutionDetails(pinstId, "PARENT LIMIT NODE MODIFICATION", null, null, null,
						null, null, "This service must success for further execution", null));
				logger.info("ServiceDetails.prepareServiceExecutionDetails() inputMap-->" + inputMap);
				for (Map.Entry<String, List<String>> entry : inputMap.entrySet()) {
					String facility = entry.getKey();
					if (((facility.contains("Cash Credit") || facility.contains("Overdraft")
							|| facility.contains("Drop Line Overdraft")) && !facility.contains("CROSS CALL"))) {
						logger.info("entry.getValue().size() <= 0 for facility ::" + facility + "::"
								+ entry.getValue().size());
						if (entry.getValue().size() == 0) {
							if (!isLimitAmountZero(pinstId, facility)) {
								respWrapper
										.setMessage("Kindly select an account number for '" + facility + "' facility.");
								return respWrapper;
							}
						}
					}
					if (facility.contains("CROSS CALL")) {
						if (isLimitAmountZeroCrossCall(pinstId, facility)
								&& !isTypeOfServiceClosedCrossCall(pinstId, facility))
							continue; // if limit amount is 0 then child and subsequent services wont execute
					} else {
						if (isLimitAmountZero(pinstId, facility) && !isTypeOfServiceClosed(pinstId, facility)) {
							continue; // if limit amount is 0 then child and subsequent services wont execute
						}
					}
					listOfPojo.add(new ServiceExecutionDetails(pinstId, "CHILD LIMIT NODE MODIFICATION", null, facility,
							null, null, null, "Parent service must be success for further execution", null));
					for (String accountNum : entry.getValue()) {
						listOfPojo.add(new ServiceExecutionDetails(pinstId, "SANCTION LIMIT NODE", null, facility,
								accountNum, null, null,
								"Child service for " + facility + " must be success for further execution", null));
						listOfPojo.add(new ServiceExecutionDetails(pinstId, "ACCOUNT AND LIMIT NODE LINKAGE", null,
								facility, accountNum, null, null, "Sanction service for " + facility + " : "
										+ accountNum + " must be success for further execution",
								null));

						if (!"Fresh Sanction".equalsIgnoreCase(proposalType)
								&& drgPwrUtils.getFlagForDrgPwrCheck(pinstId, facility) && !parentModUtility.isLimitAsPerSanctionIsZero(pinstId, facility)) {
							listOfPojo.addAll(drawingPowerCheckDetails(pinstId, facility, accountNum));
						}
						listOfPojo.add(new ServiceExecutionDetails(pinstId, "PSL", null, facility, accountNum, null,
								null, "Account & Limit Node Linkage service for " + facility + " : " + accountNum
										+ " must be success for further execution",
								null));
						listOfPojo.add(new ServiceExecutionDetails(pinstId, "RATE OF INTEREST", null, facility,
								accountNum, null, null, "Account & Limit Node Linkage service for " + facility + " : "
										+ accountNum + " must be success for further execution",
								null));
					}
				}
				listOfPojo.addAll(feeDetails(pinstId));
				if ("LOPS".equalsIgnoreCase(OperationUtillity.getCaseType(pinstId).get("CaseType"))) {
					listOfPojo.addAll(
							svtServiceDetails(pinstId, svtFIServiceUtility.getSVTDataListOfMap(pinstId, "common")));
				}
				listOfPojo.addAll(leiUrccDetails(pinstId));
			} catch (Exception e) {
				logger.info("ServiceDetails.prepareServiceExecutionDetails().Exception " + pinstId + " :"
						+ OperationUtillity.traceException(e));
			}
		} else {
			try {
				logger.info(pinstId + " :: Case type is plain LSM ");
				String action = "CREATION";
				Map<String, List<String>> inputMap = getServiceInputDetails(pinstId);
				// CHANGED 10-01-2025
				// if ("Renewal with Enhancement of limits".equalsIgnoreCase(proposalType) ||
				// "Pure Enhancement".equalsIgnoreCase(proposalType)) {
				if (!("First Limit setup".toUpperCase().equalsIgnoreCase(limitType))
						&& !("First Limit set up".toUpperCase().equalsIgnoreCase(limitType))) { // MODIFICATION FOR
																								// RENEWAL CASE
					action = "MODIFICATION";
				}
				listOfPojo.add(new ServiceExecutionDetails(pinstId, "PARENT LIMIT NODE " + action, null, null, null,
						null, null, "This service must success for further execution", null));
				logger.info("ServiceDetails.prepareServiceExecutionDetails() inputMap-->" + inputMap);
				for (Map.Entry<String, List<String>> entry : inputMap.entrySet()) {
					String facility = entry.getKey();
					String action2 = "CREATION";
					// CHANGED 10-01-2025
//			if ("Renewal with Enhancement of limits".equalsIgnoreCase(proposalType) || "Pure Enhancement".equalsIgnoreCase(proposalType)) {
					if (proposalType.contains("RENEWAL") || proposalType.contains("ENHANCEMENT")) {
						logger.info(
								"ServiceDetails.prepareServiceExecutionDetails() :: Child Service check for PINSTID "
										+ pinstId + " :: Into 1st condition");
						action2 = "MODIFICATION";
					}
					if (!facility.contains("CROSS CALL") && !"FRESH SANCTION".equalsIgnoreCase(proposalType)
							&& parentModUtility.isLimitAsPerSanctionIsZero(pinstId, facility)) {
						logger.info(
								"ServiceDetails.prepareServiceExecutionDetails() :: Child Service check for PINSTID "
										+ pinstId + " :: Into 2nd condition");
						action2 = "CREATION";
					} else if (facility.contains("CROSS CALL") && (!"FRESH SANCTION".equalsIgnoreCase(proposalType)
							&& parentModUtility.isLimitAsPerSanctionIsZeroForCrossCall(pinstId, facility))) {
						logger.info(
								"ServiceDetails.prepareServiceExecutionDetails() :: Child Service check for PINSTID "
										+ pinstId + " :: Into 3rd condition");
						action2 = "CREATION";
					}
					logger.info("ServiceDetails.prepareServiceExecutionDetails() :: Child Service check for PINSTID "
							+ pinstId + " :: action2 :: " + action2);
					logger.info("Facility check-->in service details -->" + facility);
					if (((facility.contains("Cash Credit") || facility.contains("Overdraft")
							|| facility.contains("Drop Line Overdraft")) && !facility.contains("CROSS CALL"))) {

						if (entry.getValue().size() <= 0) {
							if (!isLimitAmountZero(pinstId, facility)) {
								respWrapper
										.setMessage("Kindly select an account number for '" + facility + "' facility.");
								return respWrapper;
							}
						}
					}
					if (facility.contains("CROSS CALL")) {
						if (isLimitAmountZeroCrossCall(pinstId, facility)
								&& !isTypeOfServiceClosedCrossCall(pinstId, facility))
							continue; // if limit amount is 0 then child and subsequent services wont execute
					} else {
						if (isLimitAmountZero(pinstId, facility) && !isTypeOfServiceClosed(pinstId, facility)) {
							continue; // if limit amount is 0 then child and subsequent services wont execute
						}
					}

					listOfPojo.add(new ServiceExecutionDetails(pinstId, "CHILD LIMIT NODE " + action2, null, facility,
							null, null, null, "Parent service must be success for further execution", null));
					for (String accountNum : entry.getValue()) {
						listOfPojo.add(new ServiceExecutionDetails(pinstId, "SANCTION LIMIT NODE", null, facility,
								accountNum, null, null,
								"Child service for " + facility + " must be success for further execution", null));
						listOfPojo.add(new ServiceExecutionDetails(pinstId, "ACCOUNT AND LIMIT NODE LINKAGE", null,
								facility, accountNum, null, null, "Sanction service for " + facility + " : "
										+ accountNum + " must be success for further execution",
								null));

						if (!"Fresh Sanction".equalsIgnoreCase(proposalType)
								&& drgPwrUtils.getFlagForDrgPwrCheck(pinstId, facility) && !parentModUtility.isLimitAsPerSanctionIsZero(pinstId, facility)) {
							listOfPojo.addAll(drawingPowerCheckDetails(pinstId, facility, accountNum));
						}
						listOfPojo.add(new ServiceExecutionDetails(pinstId, "PSL", null, facility, accountNum, null,
								null, "Account & Limit Node Linkage service for " + facility + " : " + accountNum
										+ " must be success for further execution",
								null));
						listOfPojo.add(new ServiceExecutionDetails(pinstId, "RATE OF INTEREST", null, facility,
								accountNum, null, null, "Account & Limit Node Linkage service for " + facility + " : "
										+ accountNum + " must be success for further execution",
								null));
					}
				}
				listOfPojo.addAll(feeDetails(pinstId));
				if ("LOPS".equalsIgnoreCase(OperationUtillity.getCaseType(pinstId).get("CaseType"))) {
					listOfPojo.addAll(
							svtServiceDetails(pinstId, svtFIServiceUtility.getSVTDataListOfMap(pinstId, "common")));
				}
				listOfPojo.addAll(leiUrccDetails(pinstId));
			} catch (Exception e) {
				logger.info("ServiceDetails.prepareServiceExecutionDetails().Exception " + pinstId + " :"
						+ OperationUtillity.traceException(e));
			}
		}
		logger.info("linst of pojo check in prepareServiceExecutionDetails()-->" + listOfPojo);
		respWrapper.setListPojo(listOfPojo);
		return respWrapper;
	}

	public int getCountFromExecutionTable(String pinstId) {
		String query = "SELECT COUNT(REQUEST_TYPE) as COUNT FROM LSM_FI_EXECUTION_DETAILS WHERE PINSTID =  ?";
		int count = 0;
		try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(query);) {
			ps.setString(1, pinstId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					count = rs.getInt("COUNT");
				}
			}
		} catch (Exception e) {
			logger.info("ServiceDetails.getCountFromExecutionTable().Exception {}",
					OperationUtillity.traceException(e));
		}
		return count;
	}

	public int getCountFromExtTable(String pinstId) {
		int count = 0;
		try (Connection con = DBConnect.getConnection();
				PreparedStatement ps = con.prepareStatement(Queries.GET_FI_COUNT_FROM_EXT);) {
			ps.setString(1, pinstId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					count = rs.getInt("FI_EXECUTION_COUNT");
				}
			}
		} catch (Exception e) {
			logger.info("ServiceDetails.getCountFromExtTable().Exception " + pinstId + " :"
					+ OperationUtillity.traceException(e));
		}
		return count;
	}

	public int updateFIExecutionCountInExtTable(String pinstId, int countFromExecutionTable) {
		logger.info("ServiceDetails.updateFIExecutionCountInExtTable()-->" + pinstId + " countFromExecutionTable :"
				+ countFromExecutionTable);
		int count = 0;
		try (Connection con = DBConnect.getConnection();
				PreparedStatement ps = con.prepareStatement(Queries.UPDATE_COUNT_IN_EXT);) {
			ps.setInt(1, countFromExecutionTable);
			ps.setString(2, pinstId);
			int afftectedRows = ps.executeUpdate();
			if (afftectedRows > 0) {
				con.commit();
			}
		} catch (Exception e) {
			logger.info("ServiceDetails.updateFIExecutionCountInExtTable(){}", OperationUtillity.traceException(e));
		}
		return count;
	}

	public Pair<Boolean, String> saveCountInExt(String pinstId) {
		boolean flag = false;
		ResponseWrapper respWrapper = prepareServiceExecutionDetails(pinstId);
		logger.info("ResponseWrapper in ServiceDetails.saveCountInExt() ::\n" + respWrapper);
		logger.info("if(!(\"\".equalsIgnoreCase(respWrapper.getMessage())) || respWrapper.getMessage()!= null) ::\n"
				+ (!("".equalsIgnoreCase(respWrapper.getMessage())) && respWrapper.getMessage() != null));
		if (!("".equalsIgnoreCase(respWrapper.getMessage())) && respWrapper.getMessage() != null) {
			return Pair.of(false, respWrapper.getMessage());
		}

		int countOfPojo = respWrapper.getListPojo().size();
		int counFromExtTable = getCountFromExtTable(pinstId);

		logger.info("countFromExecutionTable check in saveCountInExt()-->" + countOfPojo);
		logger.info("counFromExtTable check in saveCountInExt()-->" + counFromExtTable);

		if (counFromExtTable == 0 || countOfPojo != counFromExtTable) {
			logger.info("into if of ServiceDetails.saveCountInExt()->" + pinstId);
			try (Connection con = DBConnect.getConnection();
					PreparedStatement ps = con.prepareStatement(Queries.FLUSH_DATA);) {
				ps.setString(1, pinstId);
				int c = ps.executeUpdate();
				int[] afftectedRows = insertServiceExecutionDetails(pinstId);
				logger.info("checking   rows in saveCountInExt()-->" + Arrays.toString(afftectedRows));
				logger.info("checking insertion affected rows in saveCountInExt()-->" + Arrays.toString(afftectedRows));
				logger.info("checking insertion affected rows in saveCountInExt()-->" + afftectedRows.length);
				int updatedRows = updateFIExecutionCountInExtTable(pinstId, countOfPojo);
				logger.info("checking updated rows in saveCountInExt()-->" + updatedRows);
				flag = true;
				if (c > 0) {
					con.commit();
				}
			} catch (Exception e) {
				logger.info("ServiceDetails.saveCountInExt().Exception " + pinstId,
						OperationUtillity.traceException(e));
			}
		} else {
			flag = true;
		}
		return Pair.of(flag, respWrapper.getMessage());
	}

	public int getCountFromFIExecutionTable(String pinstId) {
		int count = 0;
		try (Connection con = DBConnect.getConnection();
				PreparedStatement ps = con.prepareStatement(Queries.GET_FI_COUNT_EXECUTION_TABLE);) {
			ps.setString(1, pinstId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					count = rs.getInt("COUNTFROMEXECUTIONTABLE");
				}
			}
		} catch (Exception e) {
			logger.info("ServiceDetails.getCountFromExtTable().Exception " + pinstId + "{}",
					OperationUtillity.traceException(e));
		}
		return count;
	}

	public boolean isTypeOfServiceClosed(String pinstId, String facility) throws SQLException {
		boolean isTOSClosed = false;
		try (Connection con = DBConnect.getConnection();
				PreparedStatement ps = con.prepareStatement(Queries.GET_TYPE_OF_SERVICE)) {
			ps.setString(1, pinstId);
			ps.setString(2, facility);
			ps.setString(3, "226");
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					isTOSClosed = "Limit to be closed"
							.equalsIgnoreCase(OperationUtillity.NullReplace(rs.getString("TYPEOFSERVICE")));
				}
			}
		} catch (Exception e) {
			logger.error("ServiceDetails.isTypeOfServiceClosed(){}" + pinstId, e);
		}
		return isTOSClosed;
	}

	public boolean isTypeOfServiceClosedCrossCall(String pinstId, String facility) throws SQLException {
		boolean isTOSClosed = false;
		try (Connection con = DBConnect.getConnection();
				PreparedStatement ps = con.prepareStatement(Queries.GET_TYPE_OF_SERVICE_CROSS_CALL)) {
			ps.setString(1, pinstId);
			ps.setString(2, facility);
			ps.setString(3, "226");
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					isTOSClosed = "Limit to be closed"
							.equalsIgnoreCase(OperationUtillity.NullReplace(rs.getString("TYPEOFSERVICE")));
				}
			}
		} catch (Exception e) {
			logger.error("ServiceDetails.isTypeOfServiceClosedCrossCall(){}" + pinstId, e);
		}
		return isTOSClosed;
	}

	public boolean isLimitAmountZero(String pinstId, String facility) throws SQLException {
		boolean isLimitZero = false;
		try (Connection con = DBConnect.getConnection();
				PreparedStatement ps = con.prepareStatement(Queries.GET_FACILITY_LIMIT_AMOUNT)) {
			ps.setString(1, pinstId);
			ps.setString(2, facility);
			ps.setString(3, "127");
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					isLimitZero = commonUtility.isZero(rs.getString("FAC_LIMIT_AMOUNT"));
				}
			}
		} catch (Exception e) {
			logger.error("ServiceDetails.isLimitAmountZero(){}" + pinstId, e);
		}
		return isLimitZero;
	}

	public boolean isLimitAmountZeroCrossCall(String pinstId, String facility) throws SQLException {
		boolean isLimitZero = false;
		try (Connection con = DBConnect.getConnection();
				PreparedStatement ps = con.prepareStatement(Queries.GET_FACILITY_LIMIT_AMOUNT_CROSS_CALL)) {
			ps.setString(1, pinstId);
			ps.setString(2, facility);
			ps.setString(3, "127");
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					isLimitZero = commonUtility.isZero(rs.getString("FAC_LIMIT_AMOUNT"));
				}
			}
		} catch (Exception e) {
			logger.error("ServiceDetails.isLimitAmountZeroCrossCall(){}" + pinstId, e);
		}
		return isLimitZero;
	}

	public void updateInitialStatusInFiExecutionTable(ServiceDetailsUpdatePojo pojo) {
		logger.info("ServiceDetails.updateReqAndInitStatusInFiExecutionTable()->pojo2->" + pojo);
		String query = "";
		boolean flag = false;
		List<String> nameList = Arrays.asList("SANCTION LIMIT NODE", "ACCOUNT AND LIMIT NODE LINKAGE",
				"RATE OF INTEREST", "PSL", "STATUS CODE UPDATION", "SCHEME CODE UPDATION", "DRAWING POWER CHECK",
				"DRAWING POWER UPDATE");
		if ((pojo.getFacility() != null && !"".equals(pojo.getFacility()))
				&& !nameList.contains(pojo.getServiceName())) {
			logger.info("Into 1st query");
			query = "UPDATE LSM_FI_EXECUTION_DETAILS SET HOST_NAME=?, STATUS =?, REQUEST=?,RESPONSE=?, MESSAGE =?, REQUEST_TYPE = ?, DATETIME = SYSDATE, ACCOUNT_NUMBER = ?, RETRIGGER = ? WHERE PINSTID = ? AND SERVICE_NAME = ? AND FACILITY = '"
					+ pojo.getFacility() + "'";
		} else if (nameList.contains(pojo.getServiceName())) {
			flag = true;
			query = "UPDATE LSM_FI_EXECUTION_DETAILS   SET HOST_NAME=?, STATUS =?, REQUEST=?,RESPONSE=?, MESSAGE =?, REQUEST_TYPE = ?, DATETIME = SYSDATE, RETRIGGER = ?  WHERE ACCOUNT_NUMBER = ? AND PINSTID = ? AND SERVICE_NAME = ?";
			logger.info("Into 2nd query");
		} else {
			query = "UPDATE LSM_FI_EXECUTION_DETAILS   SET HOST_NAME=?, STATUS =?, REQUEST=?,RESPONSE=?, MESSAGE =?, REQUEST_TYPE = ?, DATETIME = SYSDATE, ACCOUNT_NUMBER = ?, RETRIGGER = ? WHERE PINSTID = ? AND SERVICE_NAME = ?";
			logger.info("Into 3rd query");
		}
		int afftectedRows = 0;
		try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(query);) {
			ps.setString(1, InetAddress.getLocalHost().getHostAddress());
			ps.setString(2, pojo.getStatus());
			ps.setString(3, pojo.getServiceRequest());
			ps.setString(4, pojo.getServiceResponse());
			ps.setString(5, pojo.getMessage());
			ps.setString(6, pojo.getRequestType());
			if (flag) {
				logger.info("------------------------- Flag is true ---------------------");
				ps.setString(7, String.valueOf(pojo.isReTrigger()));
				ps.setString(8, pojo.getAccountNumber());
			} else {
				ps.setString(7, pojo.getAccountNumber());
				ps.setString(8, String.valueOf(pojo.isReTrigger()));
			}
			ps.setString(9, pojo.getPinstId());
			ps.setString(10, pojo.getServiceName());
			afftectedRows = ps.executeUpdate();
			if (afftectedRows > 0) {
				con.commit();
				logger.info("Status Updated Successfully!!!");
			} else {
				logger.info("Status Not Updated !!!");
			}
		} catch (Exception e) {
			logger.info("ServiceDetails.updateInitialStatusInFiExecutionTable() {}\n"
					+ OperationUtillity.traceException(e));
		}
		logger.info("ServiceDetails.updateInitialStatusInFiExecutionTable()-->" + afftectedRows);
	}

	public List<ServiceExecutionDetails> svtServiceDetails(String pinstId, List<Map<String, String>> finalListOfMap) {
		List<ServiceExecutionDetails> listOfPojo = new ArrayList<>();
		for (Map<String, String> IndividualSecurityMapData : finalListOfMap) {
			if ("Fixed Deposit (Others)".equalsIgnoreCase(IndividualSecurityMapData.get("SECURITY_TYPE"))) {
				List<Map<String, String>> fdDetailsList = svtFIServiceUtility.getFixedDepositDetails(pinstId,
						IndividualSecurityMapData.get("SECURITY_NAME"));
				logger.info("FD DETAILS CHECK {}", fdDetailsList);
				if (!fdDetailsList.isEmpty()) {
					for (Map<String, String> fdMap : fdDetailsList) {
						listOfPojo.add(new ServiceExecutionDetails(pinstId, "SVT", null,
								fdMap.get("SECURITY_NAME") + " :: " + IndividualSecurityMapData.get("SECURITY_TYPE")
										+ " :: " + fdMap.get("FD_ACCOUNT_NUMBER"),
								fdMap.get("SECURITY_TYPE"), null, null, "All Child services must success", null));
						listOfPojo.addAll(svtLinkgaeDetails(pinstId, IndividualSecurityMapData));
					}
				}
			} else if ("Immovable Fixed Assets".equalsIgnoreCase(IndividualSecurityMapData.get("SECURITY_TYPE"))) {
				listOfPojo.add(new ServiceExecutionDetails(pinstId, "SVT", null,
						IndividualSecurityMapData.get("SECURITY_NAME"), IndividualSecurityMapData.get("SECURITY_TYPE"),
						null, null, "All Child services must success", null));
				listOfPojo.addAll(svtLinkgaeDetails(pinstId, IndividualSecurityMapData));
			}
		}
		return listOfPojo;
	}

	public List<ServiceExecutionDetails> svtLinkgaeDetails(String pinstId,
			Map<String, String> IndividualSecurityMapData) {
		List<ServiceExecutionDetails> listOfPojo = new LinkedList<>();
		try {
			List<String> products = svtLinkageUtils.getSecurityWiseProducts(pinstId,
					IndividualSecurityMapData.get("SECURITY_NAME"));
			if (!products.isEmpty()) {
				for (String product : products) {
					String facility = "SVTLinkageService : " + IndividualSecurityMapData.get("SECURITY_NAME") + " : "
							+ IndividualSecurityMapData.get("SECURITY_TYPE") + " : " + product
							+ " ::: SUB_TYPE_SECURITY - " + IndividualSecurityMapData.get("SUB_TYPE_SECURITY_SVT");
					listOfPojo.add(new ServiceExecutionDetails(pinstId, "SVT LINKAGE", null, facility, null, null, null,
							"SVT Service must success for further execution", null));
				}
			}
		} catch (Exception e) {
			logger.error("ServiceDetails.svtLinkgaeDetails(){}", e);
		}
		logger.info("svtLinkgaeDetails{}", listOfPojo);
		return listOfPojo;
	}

	public List<ServiceExecutionDetails> drawingPowerCheckDetails(String pinstId, String facility,
			String accountNumber) {
		List<ServiceExecutionDetails> listOfPojo = new ArrayList<>();
		listOfPojo.add(new ServiceExecutionDetails(pinstId, "DRAWING POWER CHECK", null, facility, accountNumber, null,
				null, "Account and Limit Node Linkgae Service must success for further execution", null));
		listOfPojo.add(new ServiceExecutionDetails(pinstId, "DRAWING POWER UPDATE", null, facility, accountNumber, null,
				null, "Drawing Power Updation Service must success for further execution", null));
		return listOfPojo;
	}

	public List<ServiceExecutionDetails> feeDetails(String pinstId) {
		List<Map<String, String>> getDataListOfMap = OperationUtillity.getFeeRecoveryData(pinstId, "common");
		List<ServiceExecutionDetails> listOfPojo = new ArrayList<>();
		for (Map<String, String> mapp : getDataListOfMap) {
			logger.info("mapp check in 1st fro-->" + mapp);
			for (Entry<String, String> entry : mapp.entrySet()) {
				logger.info("mapp check in 2ndfor -->" + mapp);
				if (entry.getKey().contains("Fee_Type")) {
					String s = entry.getKey().split("_")[2];
					logger.info("s check-->" + s);
					if (OperationUtillity.NullReplace(mapp.get("Fee_Type_" + s)).equalsIgnoreCase("Loan Processing Fee")
							|| OperationUtillity.NullReplace(mapp.get("Fee_Type_" + s))
									.equalsIgnoreCase("Valuation Charges")
							|| OperationUtillity.NullReplace(mapp.get("Fee_Type_" + s)).equalsIgnoreCase("Legal Fees")
							|| OperationUtillity.NullReplace(mapp.get("Fee_Type_" + s))
									.equalsIgnoreCase("Processing Fee")
							|| OperationUtillity.NullReplace(mapp.get("Fee_Type_" + s))
									.equalsIgnoreCase("Valuation Fee/Legal Fees (if there)")
							|| OperationUtillity.NullReplace(mapp.get("Fee_Type_" + s))
									.equalsIgnoreCase("Processing Fee/Valuation Fee")) {
						listOfPojo.add(new ServiceExecutionDetails(pinstId, "FEE RECOVERY", null,
								mapp.get("Fee_Type_" + s), null, null, null,
								"Account & Limit Node Linkage service must be success for further execution", null));
						break;
					}
				}
			}
		}
		return listOfPojo;
	}

	public List<ServiceExecutionDetails> leiUrccDetails(String pinstId) {
		List<ServiceExecutionDetails> listOfPojo = new ArrayList<>();
		List<Map<String, String>> uniqueCustIdsListofMap;
		try {
			uniqueCustIdsListofMap = utility.getStatusCodeDataMap(pinstId);
			if (uniqueCustIdsListofMap.size() == 0) {
				Map<String, String> map = leiUrcUtil.getLeiUrccNums(pinstId).get(0);
				listOfPojo.add(new ServiceExecutionDetails(pinstId, "LEI_URCC_NUMBER_ENQUIRY", null, map.get("CUSTID"),
						null, null, null, "Parent service must success for further execution", null));
				if (map.get("LEI_NUMBER") != null && !"".equals(map.get("LEI_NUMBER"))) {
					listOfPojo.add(new ServiceExecutionDetails(pinstId, "LEI NUMBER", null, map.get("CUSTID"),
							map.get("LEI_NUMBER"), null, null, "LEI Enquiry service must success for further execution",
							null));
					listOfPojo.add(new ServiceExecutionDetails(pinstId, "LEI VERIFICATION", null, map.get("CUSTID"),
							map.get("LEI_NUMBER"), null, null,
							"LEI Add/Update service must success for further execution", null));
				}
				if (map.get("URCC_NUMBER") != null && !"".equals(map.get("URCC_NUMBER"))) {
					listOfPojo.add(new ServiceExecutionDetails(pinstId, "URCC NUMBER", null, map.get("CUSTID"),
							map.get("URCC_NUMBER"), null, null,
							"URCC Enquiry service must success for further execution", null));
					listOfPojo.add(new ServiceExecutionDetails(pinstId, "URCC VERIFICATION", null, map.get("CUSTID"),
							map.get("URCC_NUMBER"), null, null,
							"URCC Add/Update service must success for further execution", null));
				}
			} else {
				Map<String, String> inputMap2 = utility.getLEI_URCC_InputDate(pinstId);
				for (Map<String, String> map : uniqueCustIdsListofMap) {
					listOfPojo.add(
							new ServiceExecutionDetails(pinstId, "LEI_URCC_NUMBER_ENQUIRY", null, map.get("CUSTID"),
									null, null, null, "Parent service must success for further execution", null));

					if (inputMap2.get("LEI_NUMBER") != null && !"".equals(inputMap2.get("LEI_NUMBER"))) {
						logger.info("Inserting LEI data for custid ->" + map.get("CUSTID"));
						listOfPojo.add(new ServiceExecutionDetails(pinstId, "LEI NUMBER", null, map.get("CUSTID"),
								inputMap2.get("LEI_NUMBER"), null, null,
								"LEI Enquiry service must success for further execution", null));
						listOfPojo.add(new ServiceExecutionDetails(pinstId, "LEI VERIFICATION", null, map.get("CUSTID"),
								inputMap2.get("LEI_NUMBER"), null, null,
								"LEI Add/Update service must success for further execution", null));
					}
					if (inputMap2.get("URCC_NUMBER") != null && !"".equals(inputMap2.get("URCC_NUMBER"))) {
						logger.info("Inserting URCC data for custid ->" + map.get("CUSTID"));
						listOfPojo.add(new ServiceExecutionDetails(pinstId, "URCC NUMBER", null, map.get("CUSTID"),
								inputMap2.get("URCC_NUMBER"), null, null,
								"URCC Enquiry service must success for further execution", null));
						listOfPojo.add(new ServiceExecutionDetails(pinstId, "URCC VERIFICATION", null,
								map.get("CUSTID"), inputMap2.get("URCC_NUMBER"), null, null,
								"URCC Add/Update service must success for further execution", null));
					}
				}
			}
		} catch (SQLException e) {
			logger.error("ServiceDetails.leiUrccDetails(){}", OperationUtillity.traceException(e));
		}
		return listOfPojo;
	}
}
