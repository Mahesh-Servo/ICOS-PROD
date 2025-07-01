package com.LsmFiServices.Utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.LsmFiServices.FiLsmService.ChildLimitNodeModificationService;
import com.LsmFiServices.dao.AccountAndLimitNodeLinkageDao;
import com.LsmFiServices.dao.ChildLimitNodeCreationDao;
import com.LsmFiServices.dao.PSLDao;
import com.LsmFiServices.dao.RateOfInterestDao;
import com.LsmFiServices.dao.SanctionedLimitDao;

@Component
public class ServicesSequenceLogics {

    private static final Logger logger = LoggerFactory.getLogger(ServicesSequenceLogics.class);

    @Autowired
    private CreateFacilityLimitList createFacilityLimitList;

    @Autowired
    private ChildLimitNodeModificationService childLimitNodeModificationService;

    @Autowired
    private SanctionedLimitDao sanctionedLimitDao;

    @Autowired
    private ChildLimitNodeCreationDao childLimitNodeCreationDao;

    @Autowired
    private ParentLimitNodeModificationUtility parentLimitNodeModificationUtility;

    @Autowired
    private AccountAndLimitNodeLinkageDao accountAndLimitNodeLinkageDao;

    @Autowired
    private PSLDao PSLDao;

    @Autowired
    private RateOfInterestDao RateOfInterestDao;

    public List<Map<String, String>> childFacilityList = new ArrayList<>();

    public Map<String, Map<String, String>> executeFromParentOnwards(String PINSTID,
	    Map<String, String> parentLimitNodeResponseMap) {

	logger.info("Calling ServicesSequenceLogics.executeFromParentOnwards() for--> " + PINSTID);
	Map<String, Map<String, String>> fiServiceResMap = new HashMap<>();
	Map<String, String> RateOfInterest = new LinkedHashMap<>();
	Map<String, String> sanctionedLimit = new LinkedHashMap<>();
	Map<String, String> accountAndLimitNode = new LinkedHashMap<>();
	Map<String, String> PSL = new LinkedHashMap<>();
	Map<String, String> childLimitNodeResponseMap = new LinkedHashMap<>();
	try {
	    String proposalType = parentLimitNodeModificationUtility.getProposalType(PINSTID);
	    childFacilityList = createFacilityLimitList.createChildList(PINSTID);
	    for (Map<String, String> facilitywiseChildLimitNodeMap : childFacilityList) {

		logger.info("flag check for crosscall--->",
			checkStatusOfIndividualServiceFlag("CHILD LIMIT NODE MODIFICATION : "
				+ OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")),
				PINSTID)
				&& checkStatusOfIndividualServiceFlag("CHILD LIMIT NODE CREATION : " + OperationUtillity
					.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")), PINSTID)
				&& checkStatusOfIndividualServiceFlag(
					"CHILD LIMIT NODE MODIFICATION : " + OperationUtillity.NullReplace(
						facilitywiseChildLimitNodeMap.get("FACILITY_NAME") + " CROSS CALL"),
					PINSTID)
				&& checkStatusOfIndividualServiceFlag(
					"CHILD LIMIT NODE CREATION : " + OperationUtillity.NullReplace(
						facilitywiseChildLimitNodeMap.get("FACILITY_NAME") + " CROSS CALL"),
					PINSTID));

		if (checkStatusOfIndividualServiceFlag(
			"CHILD LIMIT NODE MODIFICATION : "
				+ OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")),
			PINSTID)
			&& checkStatusOfIndividualServiceFlag("CHILD LIMIT NODE CREATION : "
				+ OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")),
				PINSTID)
			&& checkStatusOfIndividualServiceFlag("CHILD LIMIT NODE MODIFICATION : " + OperationUtillity
				.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME") + " CROSS CALL"),
				PINSTID)
			&& checkStatusOfIndividualServiceFlag("CHILD LIMIT NODE CREATION : " + OperationUtillity
				.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME") + " CROSS CALL"),
				PINSTID)) {

		    if (parentLimitNodeResponseMap.get("Status").equalsIgnoreCase("success")) {
			if ("Renewal with Enhancement of limits".toUpperCase().equalsIgnoreCase(proposalType)
				|| "Pure Enhancement".toUpperCase().equalsIgnoreCase(proposalType)) {
			    childLimitNodeResponseMap = childLimitNodeModificationService
				    .childLimitNodeModificationService(PINSTID, facilitywiseChildLimitNodeMap);
			    fiServiceResMap.put(
				    "childLimitNodeModification" + " : "
					    + OperationUtillity
						    .NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")),
				    childLimitNodeResponseMap);
			    if (!childLimitNodeResponseMap.get("Status").equalsIgnoreCase("success")) {
				return fiServiceResMap;
			    }
			} else {
			    childLimitNodeResponseMap = childLimitNodeCreationDao.childLimitNodeCreationDaoImlp(PINSTID,
				    facilitywiseChildLimitNodeMap);
			    fiServiceResMap.put(
				    "childLimitNodeResponseMap" + " : "
					    + OperationUtillity
						    .NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")),
				    childLimitNodeResponseMap);
			    if (!childLimitNodeResponseMap.get("Status").equalsIgnoreCase("success")) {
				return fiServiceResMap;
			    }
			}
		    } else {
			logger.info("into childLimitNodeResponseMap else---->");
			return fiServiceResMap;
		    }
		} else {
		    logger.info("into the else of  childLimitNodeResponseMap.put(\"Status\", \"Success\");");
		    childLimitNodeResponseMap.put("Status", "Success");
		    logger.info("childLimitNodeResponseMap--->" + childLimitNodeResponseMap);
		}
		Set<String> keys = null;
		int counter = -1;
		keys = facilitywiseChildLimitNodeMap.keySet();
		Iterator<String> ids = keys.iterator();
		while (ids.hasNext()) {
		    if ((ids.next()).contains("ACCOUNT_NO_")) {
			counter++;
		    }
		}
		logger.info("facilitywiseChildLimitNodeMap check -->" + facilitywiseChildLimitNodeMap);
		for (int i = 0; i <= counter; i++) {
		    logger.info("Account No Inside for loop :" + facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i));
		    if (OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i)) != null
			    && !OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i))
				    .equals("")) {
			String DISTRICT_CODE = OperationUtillity
				.NullReplace(createFacilityLimitList.getDistrictCodeFromMaster(
					facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i), PINSTID));
			facilitywiseChildLimitNodeMap.put("DISTRICT_CODE", DISTRICT_CODE);
			String INDUSTRY_CODE = PSLSoapRequestDataMap.getIndustryCodeFromMaster(PINSTID);
			logger.info("[PSLController].[PSL()] [INDUSTRY_CODE] :: " + INDUSTRY_CODE);
			facilitywiseChildLimitNodeMap.put("INDUSTRY_CODE", INDUSTRY_CODE);

			// If child limit succeeds then sanctioned limit will be executed
			// checking for sanction
			if (checkStatusOfIndividualServiceFlag(
				"SANCTION LIMIT :"
					+ OperationUtillity
						.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME"))
					+ " :  "
					+ OperationUtillity
						.NullReplace(facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i))
					+ "",
				PINSTID)) {
			    if (childLimitNodeResponseMap.get("Status").equalsIgnoreCase("success")) {
				sanctionedLimit = sanctionedLimitDao.sanctionedLimitDaoImlp(PINSTID,
					facilitywiseChildLimitNodeMap, i);
				logger.info("ServicesSequenceLogics.executeFromParentOnwards().sanctionedLimit -->"
					+ sanctionedLimit);
				fiServiceResMap.put(
					"sanctionedLimit : "
						+ OperationUtillity
							.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME"))
						+ " : "
						+ OperationUtillity.NullReplace(
							facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i))
						+ "",
					sanctionedLimit);
				if (!sanctionedLimit.get("Status").equalsIgnoreCase("success")) {
				    return fiServiceResMap;
				}
			    } else {
				logger.info("into sanctionedLimit else---->");
				return fiServiceResMap;
			    }
			} else {
			    sanctionedLimit.put("Status", "Success");
			}
			// checking for account linkage
			if (checkStatusOfIndividualServiceFlag(
				"ACCOUNT AND LIMIT NODE LINKAGE : " + facilitywiseChildLimitNodeMap.get("FACILITY_NAME")
					+ " Account No : " + facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i) + "",
				PINSTID)) {
			    if (sanctionedLimit.get("Status").equalsIgnoreCase("success")) {
				accountAndLimitNode = accountAndLimitNodeLinkageDao
					.accountAndLimitNodeLinkageDaoImpl(PINSTID, facilitywiseChildLimitNodeMap, i);
				logger.info(
					"ServicesSequenceLogics.executeFromParentOnwards() accountAndLimitNode check "
						+ accountAndLimitNode);
				fiServiceResMap.put(
					"accountAndLimitNodeLinkage : "
						+ OperationUtillity
							.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME"))
						+ " : "
						+ OperationUtillity.NullReplace(
							facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i)),
					accountAndLimitNode);
				if (!accountAndLimitNode.get("Status").equalsIgnoreCase("success")) {
				    return fiServiceResMap;
				}
			    } else {
				logger.info("into accountAndLimitNode else---->");
				return fiServiceResMap;
			    }
			} else {
			    accountAndLimitNode.put("Status", "Success");
			}
			if (checkStatusOfIndividualServiceFlag(
				"PSL : " + facilitywiseChildLimitNodeMap.get("FACILITY_NAME") + " Account No : "
					+ facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i) + " ",
				PINSTID)) {

			    if (accountAndLimitNode.get("Status").equalsIgnoreCase("success")) {
				Map<String, String> plsMap = new LinkedHashMap<String, String>();
				plsMap = createFacilityLimitList.getPSLOtherData(PINSTID, OperationUtillity
					.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")));

				Set<String> keySet = plsMap.keySet();
				Iterator<String> plsI = keySet.iterator();
				while (plsI.hasNext()) {
				    String key = plsI.next();
				    facilitywiseChildLimitNodeMap.put(key, plsMap.get(key));
				}
				PSL = PSLDao.PSLDaoImlp(PINSTID, facilitywiseChildLimitNodeMap, i);
				logger.info("ServicesSequenceLogics.executeFromParentOnwards() PSL check " + PSL);
				fiServiceResMap
					.put("PSL : "
						+ OperationUtillity
							.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME"))
						+ " : "
						+ OperationUtillity.NullReplace(
							facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i))
						+ "", PSL);
				if (!PSL.get("Status").equalsIgnoreCase("success")) {
				    return fiServiceResMap;
				}
			    } else {
				logger.info("into PSL else---->");
				return fiServiceResMap;
			    }
			} else {
			    PSL.put("Status", "Success");
			}

			String ip = "RATE OF INTEREST : " + facilitywiseChildLimitNodeMap.get("FACILITY_NAME")
				+ " : Data =  ACCOUNT_NUMBER : " + facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i)
				+ ", RATE_CODE : " + facilitywiseChildLimitNodeMap.get("RATE_CODE") + ", ROI_SPREAD : "
				+ facilitywiseChildLimitNodeMap.get("ROI_SPREAD") + ", ROI_PEGGED_FLAG : "
				+ facilitywiseChildLimitNodeMap.get("ROI_PEGGED_FLAG")
				+ ", PEGGING_FREQUENCY_IN_MONTHS : "
				+ facilitywiseChildLimitNodeMap.get("PEGGING_FREQUENCY") + ", NUMBER_OF_DAYS : "
				+ facilitywiseChildLimitNodeMap.get("NUMBER_OF_DAYS") + ", ROI_Spread : "
				+ facilitywiseChildLimitNodeMap.get("ROI_SPREAD") + ", NUMBER_OF_DAYS_DATE : "
				+ facilitywiseChildLimitNodeMap.get("ROI_END_DATE");
			logger.info("Request Type check -->" + ip);
			if (checkStatusOfIndividualServiceFlag(ip, PINSTID)) {

			    if (PSL.get("Status").equalsIgnoreCase("success")) {
				RateOfInterest = RateOfInterestDao.RateOfInterestDaoImpl(PINSTID,
					facilitywiseChildLimitNodeMap, i);
				logger.info("ServicesSequenceLogics.executeFromParentOnwards() RateOfInterest check "
					+ RateOfInterest);

				fiServiceResMap.put(
					"RateOfInterest : "
						+ OperationUtillity
							.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME"))
						+ " : "
						+ OperationUtillity.NullReplace(
							facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i)),
					RateOfInterest);
				if (!RateOfInterest.get("Status").equalsIgnoreCase("success")) {
				    return fiServiceResMap;
				}
			    } else {
				logger.info("into RateOfInterest else---->");
				return fiServiceResMap;
			    }
			} else {
			    RateOfInterest.put("Status", "Success");
			}
		    }
		} // for loop of account numbers
	    } // for loop of facilities
	} catch (Exception e) {
	    logger.info("ServicesSequenceLogics.executeFromParentOnwards()-->\n" + OperationUtillity.traceException(e));
	}
	return fiServiceResMap;
    }

    public Map<String, Map<String, String>> executeFromCOnwards(String PINSTID) {

	logger.info("Calling ServicesSequenceLogics.executeFromParentOnwards() for--> " + PINSTID);
	Map<String, Map<String, String>> fiServiceResMap = new HashMap<>();
	Map<String, String> RateOfInterest = new LinkedHashMap<>();
	Map<String, String> sanctionedLimit = new LinkedHashMap<>();
	Map<String, String> accountAndLimitNode = new LinkedHashMap<>();
	Map<String, String> PSL = new LinkedHashMap<>();
	Map<String, String> childLimitNodeResponseMap = new LinkedHashMap<>();
	try {
	    String proposalType = parentLimitNodeModificationUtility.getProposalType(PINSTID);
	    childFacilityList = createFacilityLimitList.createChildList(PINSTID);
	    for (Map<String, String> facilitywiseChildLimitNodeMap : childFacilityList) {

		logger.info("child flag check in executeFromParentOnwards-->" + (checkStatusOfIndividualServiceFlag(
			"CHILD LIMIT NODE MODIFICATION : "
				+ OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")),
			PINSTID)
			&& checkStatusOfIndividualServiceFlag("CHILD LIMIT NODE CREATION : "
				+ OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")),
				PINSTID)));

		logger.info("flag check for crosscall--->",
			checkStatusOfIndividualServiceFlag("CHILD LIMIT NODE MODIFICATION : "
				+ OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")),
				PINSTID)
				&& checkStatusOfIndividualServiceFlag("CHILD LIMIT NODE CREATION : " + OperationUtillity
					.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")), PINSTID)
				&& checkStatusOfIndividualServiceFlag(
					"CHILD LIMIT NODE MODIFICATION : " + OperationUtillity.NullReplace(
						facilitywiseChildLimitNodeMap.get("FACILITY_NAME") + " CROSS CALL"),
					PINSTID)
				&& checkStatusOfIndividualServiceFlag(
					"CHILD LIMIT NODE CREATION : " + OperationUtillity.NullReplace(
						facilitywiseChildLimitNodeMap.get("FACILITY_NAME") + " CROSS CALL"),
					PINSTID));

//				if (checkStatusOfIndividualServiceFlag("CHILD LIMIT NODE MODIFICATION : "+ OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")), PINSTID)
//						&& checkStatusOfIndividualServiceFlag("CHILD LIMIT NODE CREATION : "+ OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")),PINSTID)) {
		if (checkStatusOfIndividualServiceFlag(
			"CHILD LIMIT NODE MODIFICATION : "
				+ OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")),
			PINSTID)
			&& checkStatusOfIndividualServiceFlag("CHILD LIMIT NODE CREATION : "
				+ OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")),
				PINSTID)
			&& checkStatusOfIndividualServiceFlag("CHILD LIMIT NODE MODIFICATION : " + OperationUtillity
				.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME") + " CROSS CALL"),
				PINSTID)
			&& checkStatusOfIndividualServiceFlag("CHILD LIMIT NODE CREATION : " + OperationUtillity
				.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME") + " CROSS CALL"),
				PINSTID)) {

		    logger.info("Proposal type condition checking--->"
			    + ("Renewal with Enhancement of limits".toUpperCase().equalsIgnoreCase(proposalType)
				    || "Pure Enhancement".toUpperCase().equalsIgnoreCase(proposalType)));

		    if ("Renewal with Enhancement of limits".toUpperCase().equalsIgnoreCase(proposalType)
			    || "Pure Enhancement".toUpperCase().equalsIgnoreCase(proposalType)) {
			childLimitNodeResponseMap = childLimitNodeModificationService
				.childLimitNodeModificationService(PINSTID, facilitywiseChildLimitNodeMap);
			fiServiceResMap.put(
				"childLimitNodeModification" + " : "
					+ OperationUtillity
						.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")),
				childLimitNodeResponseMap);
			logger.info("ServicesSequenceLogics.executeFromCOnwards().childLimitNodeModification-->"
				+ childLimitNodeResponseMap);
			if (!childLimitNodeResponseMap.get("Status").equalsIgnoreCase("success")) {
			    return fiServiceResMap;
			}
		    } else {
			childLimitNodeResponseMap = childLimitNodeCreationDao.childLimitNodeCreationDaoImlp(PINSTID,
				facilitywiseChildLimitNodeMap);
			logger.info("ServicesSequenceLogics.executeFromCOnwards().childLimitNodeResponseMap-->"
				+ childLimitNodeResponseMap);
			fiServiceResMap.put(
				"childLimitNodeResponseMap" + " : "
					+ OperationUtillity
						.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")),
				childLimitNodeResponseMap);
			if (!childLimitNodeResponseMap.get("Status").equalsIgnoreCase("success")) {
			    return fiServiceResMap;
			}
		    }
		} else {
		    logger.info("into the else of  childLimitNodeResponseMap.put(\"Status\", \"Success\");");
		    childLimitNodeResponseMap.put("Status", "Success");
		    logger.info("childLimitNodeResponseMap--->" + childLimitNodeResponseMap);
		}
		Set<String> keys = null;
		int counter = -1;
		keys = facilitywiseChildLimitNodeMap.keySet();
		Iterator<String> ids = keys.iterator();
		while (ids.hasNext()) {
		    if ((ids.next()).contains("ACCOUNT_NO_")) {
			counter++;
		    }
		}
		for (int i = 0; i <= counter; i++) {
		    logger.info("Account No Inside for loop :" + facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i));
		    if (OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i)) != null
			    && !OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i))
				    .equals("")) {
			String DISTRICT_CODE = OperationUtillity
				.NullReplace(createFacilityLimitList.getDistrictCodeFromMaster(
					facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i), PINSTID));
			facilitywiseChildLimitNodeMap.put("DISTRICT_CODE", DISTRICT_CODE);
			String INDUSTRY_CODE = PSLSoapRequestDataMap.getIndustryCodeFromMaster(PINSTID);
			logger.info("[PSLController].[PSL()] [INDUSTRY_CODE] :: " + INDUSTRY_CODE);
			facilitywiseChildLimitNodeMap.put("INDUSTRY_CODE", INDUSTRY_CODE);

			// If child limit succeeds then sanctioned limit will be executed
			if (checkStatusOfIndividualServiceFlag(
				"SANCTION LIMIT :"
					+ OperationUtillity
						.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME"))
					+ " :  "
					+ OperationUtillity
						.NullReplace(facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i))
					+ "",
				PINSTID)) {

			    if (childLimitNodeResponseMap.get("Status").equalsIgnoreCase("success")) {
				sanctionedLimit = sanctionedLimitDao.sanctionedLimitDaoImlp(PINSTID,
					facilitywiseChildLimitNodeMap, i);
				logger.info("Sanction map response -->" + sanctionedLimit);
				fiServiceResMap.put(
					"sanctionedLimit : "
						+ OperationUtillity
							.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME"))
						+ " : "
						+ OperationUtillity.NullReplace(
							facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i))
						+ "",
					sanctionedLimit);
				if (!sanctionedLimit.get("Status").equalsIgnoreCase("success")) {
				    return fiServiceResMap;
				}
			    } else {
				logger.info("into sanctionedLimit else---->");
				return fiServiceResMap;
			    }
			} else {
			    sanctionedLimit.put("Status", "Success");
			}
			// checking for account linkage
			if (checkStatusOfIndividualServiceFlag(
				"ACCOUNT AND LIMIT NODE LINKAGE : " + facilitywiseChildLimitNodeMap.get("FACILITY_NAME")
					+ " Account No : " + facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i) + "",
				PINSTID)) {
			    if (sanctionedLimit.get("Status").equalsIgnoreCase("success")) {
				accountAndLimitNode = accountAndLimitNodeLinkageDao
					.accountAndLimitNodeLinkageDaoImpl(PINSTID, facilitywiseChildLimitNodeMap, i);
				logger.info("ServicesSequenceLogics.executeFromCOnwards() accountAndLimitNode-->"
					+ accountAndLimitNode);
				fiServiceResMap.put(
					"accountAndLimitNodeLinkage : "
						+ OperationUtillity
							.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME"))
						+ " : "
						+ OperationUtillity.NullReplace(
							facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i)),
					accountAndLimitNode);
				if (!accountAndLimitNode.get("Status").equalsIgnoreCase("success")) {
				    return fiServiceResMap;
				}
			    } else {
				logger.info("into accountAndLimitNode else---->");
				return fiServiceResMap;
			    }
			} else {
			    accountAndLimitNode.put("Status", "Success");
			}
			if (checkStatusOfIndividualServiceFlag(
				"PSL : " + facilitywiseChildLimitNodeMap.get("FACILITY_NAME") + " Account No : "
					+ facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i) + " ",
				PINSTID)) {

			    if (accountAndLimitNode.get("Status").equalsIgnoreCase("success")) {
				Map<String, String> plsMap = new LinkedHashMap<String, String>();
				plsMap = createFacilityLimitList.getPSLOtherData(PINSTID, OperationUtillity
					.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")));

				Set<String> keySet = plsMap.keySet();
				Iterator<String> plsI = keySet.iterator();
				while (plsI.hasNext()) {
				    String key = plsI.next();
				    facilitywiseChildLimitNodeMap.put(key, plsMap.get(key));
				}
				PSL = PSLDao.PSLDaoImlp(PINSTID, facilitywiseChildLimitNodeMap, i);
				logger.info("ServicesSequenceLogics.executeFromCOnwards() PSL-->" + PSL);
				fiServiceResMap
					.put("PSL : "
						+ OperationUtillity
							.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME"))
						+ " : "
						+ OperationUtillity.NullReplace(
							facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i))
						+ "", PSL);
				if (!PSL.get("Status").equalsIgnoreCase("success")) {
				    return fiServiceResMap;
				}
			    } else {
				logger.info("into PSL else---->");
				return fiServiceResMap;
			    }
			} else {
			    PSL.put("Status", "Success");
			}

			String ip = "RATE OF INTEREST : " + facilitywiseChildLimitNodeMap.get("FACILITY_NAME")
				+ " : Data =  ACCOUNT_NUMBER : " + facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i)
				+ ", RATE_CODE : " + facilitywiseChildLimitNodeMap.get("RATE_CODE") + ", ROI_SPREAD : "
				+ facilitywiseChildLimitNodeMap.get("ROI_SPREAD") + ", ROI_PEGGED_FLAG : "
				+ facilitywiseChildLimitNodeMap.get("ROI_PEGGED_FLAG")
				+ ", PEGGING_FREQUENCY_IN_MONTHS : "
				+ facilitywiseChildLimitNodeMap.get("PEGGING_FREQUENCY") + ", NUMBER_OF_DAYS : "
				+ facilitywiseChildLimitNodeMap.get("NUMBER_OF_DAYS") + ", ROI_Spread : "
				+ facilitywiseChildLimitNodeMap.get("ROI_SPREAD") + ", NUMBER_OF_DAYS_DATE : "
				+ facilitywiseChildLimitNodeMap.get("ROI_END_DATE");
			if (checkStatusOfIndividualServiceFlag(ip, PINSTID)) {

			    if (PSL.get("Status").equalsIgnoreCase("success")) {
				RateOfInterest = RateOfInterestDao.RateOfInterestDaoImpl(PINSTID,
					facilitywiseChildLimitNodeMap, i);
				fiServiceResMap.put(
					"RateOfInterest : "
						+ OperationUtillity
							.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME"))
						+ " : "
						+ OperationUtillity.NullReplace(
							facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i)),
					RateOfInterest);
				logger.info("ServicesSequenceLogics.executeFromCOnwards() RateOfInterest-->"
					+ RateOfInterest);
				if (!RateOfInterest.get("Status").equalsIgnoreCase("success")) {
				    return fiServiceResMap;
				}
			    } else {
				logger.info("into RateOfInterest else---->");
				return fiServiceResMap;
			    }
			} else {
			    RateOfInterest.put("Status", "Success");
			}
		    }
		}
//				}
	    }
//			if (RateOfInterest.get("Status").equalsIgnoreCase("success")) {
//				feeRecoveryResult = feeService.feeRecoveryService(PINSTID, "common");
//				logger.info("ServicesSequenceLogics.executeFromCOnwards() feeRecoveryResult-->"+feeRecoveryResult);
//				fiServiceResMap.put("Fee_Recovery_Service", feeRecoveryResult);
//				if(!feeRecoveryResult.get("Status").equalsIgnoreCase("success")){
//					return fiServiceResMap;
//				}
//			} else {
//				logger.info("into feeRecoveryResult else---->");
//				return fiServiceResMap;
//			}
//			if (feeRecoveryResult.get("Status").equalsIgnoreCase("success")) {
//				SVTServiceResult = svtService.SVTService(PINSTID, "common");
//				logger.info("ServicesSequenceLogics.executeFromCOnwards() SVTServiceResult-->"+SVTServiceResult);
//				fiServiceResMap.put("SVTServiceResult", SVTServiceResult);
//				if(!SVTServiceResult.get("Status").equalsIgnoreCase("success")){
//					return fiServiceResMap;
//				}
//			} else {
//				logger.info("into SVTServiceResult else---->");
//				return fiServiceResMap;
//			}
//			
//			if (RateOfInterest.get("Status").equalsIgnoreCase("success")) {
//				feeRecoveryResult = feeService.feeRecoveryService(PINSTID, "common");
//				SVTServiceResult = svtService.SVTService(PINSTID, "common");
//				fiServiceResMap.put("FeeRecoveryService", feeRecoveryResult);
//				fiServiceResMap.put("SVTService", SVTServiceResult);
//			} 
	} catch (Exception e) {
	    logger.info("ServicesSequenceLogics.executeFromCOnwards()-->\n" + OperationUtillity.traceException(e));
	}
	return fiServiceResMap;
    }

    public Map<String, Map<String, String>> executeFromSanctionOnwards(String PINSTID) {

	logger.info("Calling ServicesSequenceLogics.executeFromSanctionLimitOnwards() for--> " + PINSTID);
	Map<String, Map<String, String>> fiServiceResMap = new HashMap<>();
	Map<String, String> RateOfInterest = new LinkedHashMap<>();
	Map<String, String> sanctionedLimit = new LinkedHashMap<>();
	Map<String, String> accountAndLimitNode = new LinkedHashMap<>();
	Map<String, String> PSL = new LinkedHashMap<>();
	// Map<String, String> feeRecoveryResult = new LinkedHashMap<>();
	// Map<String, String> SVTServiceResult = new LinkedHashMap<>();
	Map<String, String> childLimitNodeResponseMap = new LinkedHashMap<>();
	try {
	    String proposalType = parentLimitNodeModificationUtility.getProposalType(PINSTID);
	    childFacilityList = createFacilityLimitList.createChildList(PINSTID);
	    for (Map<String, String> facilitywiseChildLimitNodeMap : childFacilityList) {

		logger.info("Flag check in executeFromSanctionOnwards()" + (checkStatusOfIndividualServiceFlag(
			"CHILD LIMIT NODE MODIFICATION : "
				+ OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")),
			PINSTID)
			&& checkStatusOfIndividualServiceFlag("CHILD LIMIT NODE CREATION : "
				+ OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")),
				PINSTID)));
//				if (checkStatusOfIndividualServiceFlag("CHILD LIMIT NODE MODIFICATION : "+ OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")), PINSTID)
//						&& checkStatusOfIndividualServiceFlag("CHILD LIMIT NODE CREATION : "+ OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")),PINSTID)) {

		if (checkStatusOfIndividualServiceFlag(
			"CHILD LIMIT NODE MODIFICATION : "
				+ OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")),
			PINSTID)
			&& checkStatusOfIndividualServiceFlag("CHILD LIMIT NODE CREATION : "
				+ OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")),
				PINSTID)
			&& checkStatusOfIndividualServiceFlag("CHILD LIMIT NODE MODIFICATION : " + OperationUtillity
				.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME") + " CROSS CALL"),
				PINSTID)
			&& checkStatusOfIndividualServiceFlag("CHILD LIMIT NODE CREATION : " + OperationUtillity
				.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME") + " CROSS CALL"),
				PINSTID)) {
		    logger.info("Proposal type condition checking--->"
			    + ("Renewal with Enhancement of limits".toUpperCase().equalsIgnoreCase(proposalType)
				    || "Pure Enhancement".toUpperCase().equalsIgnoreCase(proposalType)));

//					if (parentLimitNodeResponseMap.get("Status").equalsIgnoreCase("success")) {
		    if ("Renewal with Enhancement of limits".toUpperCase().equalsIgnoreCase(proposalType)
			    || "Pure Enhancement".toUpperCase().equalsIgnoreCase(proposalType)) {
			childLimitNodeResponseMap = childLimitNodeModificationService
				.childLimitNodeModificationService(PINSTID, facilitywiseChildLimitNodeMap);
			logger.info("ServicesSequenceLogics.executeFromSanctionOnwards() childLimitNodeModification-->"
				+ childLimitNodeResponseMap);
			fiServiceResMap.put(
				"childLimitNodeModification" + " : "
					+ OperationUtillity
						.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")),
				childLimitNodeResponseMap);
			if (!childLimitNodeResponseMap.get("Status").equalsIgnoreCase("success")) {
			    return fiServiceResMap;
			}
		    } else {
			childLimitNodeResponseMap = childLimitNodeCreationDao.childLimitNodeCreationDaoImlp(PINSTID,
				facilitywiseChildLimitNodeMap);
			logger.info("ServicesSequenceLogics.executeFromSanctionOnwards() childLimitNodeResponseMap-->"
				+ childLimitNodeResponseMap);
			fiServiceResMap.put(
				"childLimitNodeResponseMap" + " : "
					+ OperationUtillity
						.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")),
				childLimitNodeResponseMap);
		    }
		} else {
		    logger.info("into the else of  childLimitNodeResponseMap.put(\"Status\", \"Success\");");
		    childLimitNodeResponseMap.put("Status", "Success");
		    logger.info("childLimitNodeResponseMap--->" + childLimitNodeResponseMap);
		}
		Set<String> keys = null;
		int counter = -1;
		keys = facilitywiseChildLimitNodeMap.keySet();
		Iterator<String> ids = keys.iterator();
		while (ids.hasNext()) {
		    if ((ids.next()).contains("ACCOUNT_NO_")) {
			counter++;
		    }
		}
		for (int i = 0; i <= counter; i++) {
		    logger.info("Account No Inside for loop :" + facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i));
		    if (OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i)) != null
			    && !OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i))
				    .equals("")) {
			String DISTRICT_CODE = OperationUtillity
				.NullReplace(createFacilityLimitList.getDistrictCodeFromMaster(
					facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i), PINSTID));
			facilitywiseChildLimitNodeMap.put("DISTRICT_CODE", DISTRICT_CODE);
			String INDUSTRY_CODE = PSLSoapRequestDataMap.getIndustryCodeFromMaster(PINSTID);
			logger.info("[PSLController].[PSL()] [INDUSTRY_CODE] :: " + INDUSTRY_CODE);
			facilitywiseChildLimitNodeMap.put("INDUSTRY_CODE", INDUSTRY_CODE);

			// If child limit succeeds then sanctioned limit will be executed

			// checking for sanction
			if (checkStatusOfIndividualServiceFlag(
				"SANCTION LIMIT :"
					+ OperationUtillity
						.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME"))
					+ " :  "
					+ OperationUtillity
						.NullReplace(facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i))
					+ "",
				PINSTID)) {
			    if (childLimitNodeResponseMap.get("Status").equalsIgnoreCase("success")) {
				sanctionedLimit = sanctionedLimitDao.sanctionedLimitDaoImlp(PINSTID,
					facilitywiseChildLimitNodeMap, i);
				logger.info("ServicesSequenceLogics.executeFromSanctionOnwards() sanctionedLimit-->"
					+ sanctionedLimit);
				fiServiceResMap.put(
					"sanctionedLimit : "
						+ OperationUtillity
							.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME"))
						+ " : "
						+ OperationUtillity.NullReplace(
							facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i))
						+ "",
					sanctionedLimit);
				if (!sanctionedLimit.get("Status").equalsIgnoreCase("success")) {
				    return fiServiceResMap;
				}
			    } else {
				logger.info("into sanctionedLimit else---->");
				return fiServiceResMap;
			    }
			}
			// checking for account linkage
			if (checkStatusOfIndividualServiceFlag(
				"ACCOUNT AND LIMIT NODE LINKAGE : " + facilitywiseChildLimitNodeMap.get("FACILITY_NAME")
					+ " Account No : " + facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i) + "",
				PINSTID)) {
			    if (sanctionedLimit.get("Status").equalsIgnoreCase("success")) {
				accountAndLimitNode = accountAndLimitNodeLinkageDao
					.accountAndLimitNodeLinkageDaoImpl(PINSTID, facilitywiseChildLimitNodeMap, i);
				logger.info("ServicesSequenceLogics.executeFromSanctionOnwards() accountAndLimitNode-->"
					+ accountAndLimitNode);
				fiServiceResMap.put(
					"accountAndLimitNodeLinkage : "
						+ OperationUtillity
							.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME"))
						+ " : "
						+ OperationUtillity.NullReplace(
							facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i)),
					accountAndLimitNode);
				if (!accountAndLimitNode.get("Status").equalsIgnoreCase("success")) {
				    return fiServiceResMap;
				}
			    } else {
				logger.info("into accountAndLimitNode else---->");
				return fiServiceResMap;
			    }
			}
			if (checkStatusOfIndividualServiceFlag(
				"PSL : " + facilitywiseChildLimitNodeMap.get("FACILITY_NAME") + " Account No : "
					+ facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i) + " ",
				PINSTID)) {

			    if (accountAndLimitNode.get("Status").equalsIgnoreCase("success")) {
				Map<String, String> plsMap = new LinkedHashMap<String, String>();
				plsMap = createFacilityLimitList.getPSLOtherData(PINSTID, OperationUtillity
					.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")));

				Set<String> keySet = plsMap.keySet();
				Iterator<String> plsI = keySet.iterator();
				while (plsI.hasNext()) {
				    String key = plsI.next();
				    facilitywiseChildLimitNodeMap.put(key, plsMap.get(key));
				}
				PSL = PSLDao.PSLDaoImlp(PINSTID, facilitywiseChildLimitNodeMap, i);
				logger.info("ServicesSequenceLogics.executeFromSanctionOnwards() PSL-->" + PSL);
				fiServiceResMap
					.put("PSL : "+ OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME"))
						+ " : "+ OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i))
						+ "", PSL);
				if (!PSL.get("Status").equalsIgnoreCase("success")) {
				    return fiServiceResMap;
				}
			    } else {
				logger.info("into PSL else---->");
				return fiServiceResMap;
			    }
			}

			String ip = "RATE OF INTEREST : " + facilitywiseChildLimitNodeMap.get("FACILITY_NAME")
				+ " : Data =  ACCOUNT_NUMBER : " + facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i)
				+ ", RATE_CODE : " + facilitywiseChildLimitNodeMap.get("RATE_CODE") + ", ROI_SPREAD : "
				+ facilitywiseChildLimitNodeMap.get("ROI_SPREAD") + ", ROI_PEGGED_FLAG : "
				+ facilitywiseChildLimitNodeMap.get("ROI_PEGGED_FLAG")
				+ ", PEGGING_FREQUENCY_IN_MONTHS : "
				+ facilitywiseChildLimitNodeMap.get("PEGGING_FREQUENCY") + ", NUMBER_OF_DAYS : "
				+ facilitywiseChildLimitNodeMap.get("NUMBER_OF_DAYS") + ", ROI_Spread : "
				+ facilitywiseChildLimitNodeMap.get("ROI_SPREAD") + ", NUMBER_OF_DAYS_DATE : "
				+ facilitywiseChildLimitNodeMap.get("ROI_END_DATE");

			if (checkStatusOfIndividualServiceFlag(ip, PINSTID)) {

			    if (PSL.get("Status").equalsIgnoreCase("success")) {

				RateOfInterest = RateOfInterestDao.RateOfInterestDaoImpl(PINSTID,
					facilitywiseChildLimitNodeMap, i);
				logger.info("ServicesSequenceLogics.executeFromSanctionOnwards() RateOfInterest-->"
					+ RateOfInterest);
				fiServiceResMap.put(
					"RateOfInterest : "
						+ OperationUtillity
							.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME"))
						+ " : "
						+ OperationUtillity.NullReplace(
							facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i)),
					RateOfInterest);
				if (!RateOfInterest.get("Status").equalsIgnoreCase("success")) {
				    return fiServiceResMap;
				}
			    } else {
				logger.info("into RateOfInterest else---->");
				return fiServiceResMap;
			    }
			}

		    }
		}
//				}
	    }
//			if (RateOfInterest.get("Status").equalsIgnoreCase("success")) {
//				feeRecoveryResult = feeService.feeRecoveryService(PINSTID, "common");
//				logger.info("ServicesSequenceLogics.executeFromSanctionOnwards() feeRecoveryResult-->"+feeRecoveryResult);
//				fiServiceResMap.put("Fee_Recovery_Service", feeRecoveryResult);
//			} else {
//				logger.info("into feeRecoveryResult else---->");
//				return fiServiceResMap;
//			}
//			if (feeRecoveryResult.get("Status").equalsIgnoreCase("success")) {
//				SVTServiceResult = svtService.SVTService(PINSTID, "common");
//				logger.info("ServicesSequenceLogics.executeFromSanctionOnwards() SVTServiceResult-->"+SVTServiceResult);
//				fiServiceResMap.put("SVTServiceResult", SVTServiceResult);
//			} else {
//				logger.info("into SVTServiceResult else---->");
//				return fiServiceResMap;
//			}
//			
//			if (RateOfInterest.get("Status").equalsIgnoreCase("success")) {
//				feeRecoveryResult = feeService.feeRecoveryService(PINSTID, "common");
//				SVTServiceResult = svtService.SVTService(PINSTID, "common");
//				fiServiceResMap.put("FeeRecoveryService", feeRecoveryResult);
//				fiServiceResMap.put("SVTService", SVTServiceResult);
//			} 
	} catch (Exception e) {
	    logger.info(
		    "ServicesSequenceLogics.executeFromSanctionOnwards()->\n" + OperationUtillity.traceException(e));
	}
	return fiServiceResMap;
    }

    public Map<String, Map<String, String>> executeFromAccountEnquiryOnwards(String PINSTID) {

	logger.info("Calling ServicesSequenceLogics.executeFromSanctionLimitOnwards() for--> " + PINSTID);
	Map<String, Map<String, String>> fiServiceResMap = new HashMap<>();
	Map<String, String> RateOfInterest = new LinkedHashMap<>();
	Map<String, String> sanctionedLimit = new LinkedHashMap<>();
	Map<String, String> accountAndLimitNode = new LinkedHashMap<>();
	Map<String, String> PSL = new LinkedHashMap<>();
	Map<String, String> childLimitNodeResponseMap = new LinkedHashMap<>();
	try {
	    String proposalType = parentLimitNodeModificationUtility.getProposalType(PINSTID);
	    childFacilityList = createFacilityLimitList.createChildList(PINSTID);
	    for (Map<String, String> facilitywiseChildLimitNodeMap : childFacilityList) {

		logger.info("Flag check in executeFromAccountEnquiryOnwards()" + (checkStatusOfIndividualServiceFlag(
			"CHILD LIMIT NODE MODIFICATION : "
				+ OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")),
			PINSTID)
			&& checkStatusOfIndividualServiceFlag("CHILD LIMIT NODE CREATION : "
				+ OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")),
				PINSTID)));

//				if (checkStatusOfIndividualServiceFlag("CHILD LIMIT NODE MODIFICATION : "+ OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")), PINSTID)
//						&& checkStatusOfIndividualServiceFlag("CHILD LIMIT NODE CREATION : "+ OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")),PINSTID)) {
		if (checkStatusOfIndividualServiceFlag(
			"CHILD LIMIT NODE MODIFICATION : "
				+ OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")),
			PINSTID)
			&& checkStatusOfIndividualServiceFlag("CHILD LIMIT NODE CREATION : "
				+ OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")),
				PINSTID)
			&& checkStatusOfIndividualServiceFlag("CHILD LIMIT NODE MODIFICATION : " + OperationUtillity
				.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME") + " CROSS CALL"),
				PINSTID)
			&& checkStatusOfIndividualServiceFlag("CHILD LIMIT NODE CREATION : " + OperationUtillity
				.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME") + " CROSS CALL"),
				PINSTID)) {
		    logger.info("Proposal type condition checking--->"
			    + ("Renewal with Enhancement of limits".toUpperCase().equalsIgnoreCase(proposalType)
				    || "Pure Enhancement".toUpperCase().equalsIgnoreCase(proposalType)));

		    if ("Renewal with Enhancement of limits".toUpperCase().equalsIgnoreCase(proposalType)
			    || "Pure Enhancement".toUpperCase().equalsIgnoreCase(proposalType)) {
			childLimitNodeResponseMap = childLimitNodeModificationService
				.childLimitNodeModificationService(PINSTID, facilitywiseChildLimitNodeMap);
			fiServiceResMap.put(
				"childLimitNodeModification" + " : "
					+ OperationUtillity
						.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")),
				childLimitNodeResponseMap);
			logger.info(
				"ServicesSequenceLogics.executeFromAccountEnquiryOnwards() childLimitNodeModification-->"
					+ childLimitNodeResponseMap);
			if (!childLimitNodeResponseMap.get("Status").equalsIgnoreCase("success")) {
			    return fiServiceResMap;
			}
		    } else {
			childLimitNodeResponseMap = childLimitNodeCreationDao.childLimitNodeCreationDaoImlp(PINSTID,
				facilitywiseChildLimitNodeMap);
			logger.info(
				"ServicesSequenceLogics.executeFromAccountEnquiryOnwards() childLimitNodeResponseMap-->"
					+ childLimitNodeResponseMap);
			fiServiceResMap.put(
				"childLimitNodeResponseMap" + " : "
					+ OperationUtillity
						.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")),
				childLimitNodeResponseMap);
			if (!childLimitNodeResponseMap.get("Status").equalsIgnoreCase("success")) {
			    return fiServiceResMap;
			}
		    }
		} else {
		    logger.info("into the else of  childLimitNodeResponseMap.put(\"Status\", \"Success\");");
		    childLimitNodeResponseMap.put("Status", "Success");
		    logger.info("childLimitNodeResponseMap--->" + childLimitNodeResponseMap);
		}
		try {
		    Set<String> keys = null;
		    int counter = -1;
		    keys = facilitywiseChildLimitNodeMap.keySet();
		    Iterator<String> ids = keys.iterator();
		    while (ids.hasNext()) {
			if ((ids.next()).contains("ACCOUNT_NO_")) {
			    counter++;
			}
		    }
		    for (int i = 0; i <= counter; i++) {
			logger.info(
				"Account No Inside for loop :" + facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i));
			if (OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i)) != null
				&& !OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i))
					.equals("")) {
			    try {
				String DISTRICT_CODE = OperationUtillity
					.NullReplace(createFacilityLimitList.getDistrictCodeFromMaster(
						facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i), PINSTID));
				facilitywiseChildLimitNodeMap.put("DISTRICT_CODE", DISTRICT_CODE);
				String INDUSTRY_CODE = PSLSoapRequestDataMap.getIndustryCodeFromMaster(PINSTID);
				logger.info("[PSLController].[PSL()] [INDUSTRY_CODE] :: " + INDUSTRY_CODE);
				facilitywiseChildLimitNodeMap.put("INDUSTRY_CODE", INDUSTRY_CODE);

				// If child limit succeeds then sanctioned limit will be executed

				// checking for sanction
				if (checkStatusOfIndividualServiceFlag(
					"SANCTION LIMIT :"
						+ OperationUtillity
							.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME"))
						+ " :  "
						+ OperationUtillity.NullReplace(
							facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i))
						+ "",
					PINSTID)) {
				    if (childLimitNodeResponseMap.get("Status").equalsIgnoreCase("success")) {
					sanctionedLimit = sanctionedLimitDao.sanctionedLimitDaoImlp(PINSTID,
						facilitywiseChildLimitNodeMap, i);
					logger.info(
						"ServicesSequenceLogics.executeFromAccountEnquiryOnwards() sanctionedLimit-->"
							+ sanctionedLimit);
					fiServiceResMap.put("sanctionedLimit : "
						+ OperationUtillity
							.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME"))
						+ " : "
						+ OperationUtillity.NullReplace(
							facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i))
						+ "", sanctionedLimit);
					if (!sanctionedLimit.get("Status").equalsIgnoreCase("success")) {
					    return fiServiceResMap;
					}
				    } else {
					logger.info("into sanctionedLimit else---->");
					return fiServiceResMap;
				    }
				}
				// checking for account linkage
				if (checkStatusOfIndividualServiceFlag("ACCOUNT AND LIMIT NODE LINKAGE : "
					+ facilitywiseChildLimitNodeMap.get("FACILITY_NAME") + " Account No : "
					+ facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i) + "", PINSTID)) {
				    if (sanctionedLimit.get("Status").equalsIgnoreCase("success")) {
					accountAndLimitNode = accountAndLimitNodeLinkageDao
						.accountAndLimitNodeLinkageDaoImpl(PINSTID,
							facilitywiseChildLimitNodeMap, i);
					logger.info(
						"ServicesSequenceLogics.executeFromAccountEnquiryOnwards() accountAndLimitNode-->"
							+ accountAndLimitNode);
					fiServiceResMap.put(
						"accountAndLimitNodeLinkage : "
							+ OperationUtillity.NullReplace(
								facilitywiseChildLimitNodeMap.get("FACILITY_NAME"))
							+ " : "
							+ OperationUtillity.NullReplace(
								facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i)),
						accountAndLimitNode);
					if (!accountAndLimitNode.get("Status").equalsIgnoreCase("success")) {
					    return fiServiceResMap;
					}
				    } else {
					logger.info("into accountAndLimitNode else---->");
					return fiServiceResMap;
				    }
				}
				if (checkStatusOfIndividualServiceFlag(
					"PSL : " + facilitywiseChildLimitNodeMap.get("FACILITY_NAME") + " Account No : "
						+ facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i) + " ",
					PINSTID)) {
				    if (accountAndLimitNode.get("Status").equalsIgnoreCase("success")) {
					Map<String, String> plsMap = new LinkedHashMap<String, String>();
					plsMap = createFacilityLimitList.getPSLOtherData(PINSTID, OperationUtillity
						.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")));

					Set<String> keySet = plsMap.keySet();
					Iterator<String> plsI = keySet.iterator();
					while (plsI.hasNext()) {
					    String key = plsI.next();
					    facilitywiseChildLimitNodeMap.put(key, plsMap.get(key));
					}
					PSL = PSLDao.PSLDaoImlp(PINSTID, facilitywiseChildLimitNodeMap, i);
					logger.info("ServicesSequenceLogics.executeFromAccountEnquiryOnwards() PSL-->"
						+ PSL);
					fiServiceResMap.put(
						"PSL : " + OperationUtillity
							.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME"))
							+ " : "
							+ OperationUtillity.NullReplace(
								facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i))
							+ "",
						PSL);
					if (!PSL.get("Status").equalsIgnoreCase("success")) {
					    return fiServiceResMap;
					}
				    } else {
					logger.info("into PSL else---->");
					return fiServiceResMap;
				    }
				}

				String ip = "RATE OF INTEREST : " + facilitywiseChildLimitNodeMap.get("FACILITY_NAME")
					+ " : Data =  ACCOUNT_NUMBER : "
					+ facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i) + ", RATE_CODE : "
					+ facilitywiseChildLimitNodeMap.get("RATE_CODE") + ", ROI_SPREAD : "
					+ facilitywiseChildLimitNodeMap.get("ROI_SPREAD") + ", ROI_PEGGED_FLAG : "
					+ facilitywiseChildLimitNodeMap.get("ROI_PEGGED_FLAG")
					+ ", PEGGING_FREQUENCY_IN_MONTHS : "
					+ facilitywiseChildLimitNodeMap.get("PEGGING_FREQUENCY") + ", NUMBER_OF_DAYS : "
					+ facilitywiseChildLimitNodeMap.get("NUMBER_OF_DAYS") + ", ROI_Spread : "
					+ facilitywiseChildLimitNodeMap.get("ROI_SPREAD") + ", NUMBER_OF_DAYS_DATE : "
					+ facilitywiseChildLimitNodeMap.get("ROI_END_DATE");

				if (checkStatusOfIndividualServiceFlag(ip, PINSTID)) {

				    if (PSL.get("Status").equalsIgnoreCase("success")) {

					RateOfInterest = RateOfInterestDao.RateOfInterestDaoImpl(PINSTID,
						facilitywiseChildLimitNodeMap, i);
					logger.info(
						"ServicesSequenceLogics.executeFromAccountEnquiryOnwards() RateOfInterest-->"
							+ RateOfInterest);
					fiServiceResMap.put(
						"RateOfInterest : "
							+ OperationUtillity.NullReplace(
								facilitywiseChildLimitNodeMap.get("FACILITY_NAME"))
							+ " : "
							+ OperationUtillity.NullReplace(
								facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i)),
						RateOfInterest);
					if (!RateOfInterest.get("Status").equalsIgnoreCase("success")) {
					    return fiServiceResMap;
					}
				    } else {
					logger.info("into RateOfInterest else---->");
					return fiServiceResMap;
				    }
				}
			    } catch (Exception e1) {
				logger.info("There is exception --->" + OperationUtillity.traceException(e1));
			    }
			}
		    }
		} catch (Exception ex) {
		    logger.info("There is problem while executing services account wise "
			    + OperationUtillity.traceException(ex));
		}
//				}
	    }
	} catch (Exception e) {
	    logger.info("ServicesSequenceLogics.executeFromAccountEnquiryOnwards()-->"
		    + OperationUtillity.traceException(e));
	}
	return fiServiceResMap;
    }

    public Map<String, Map<String, String>> executeFromPSLOnwards(String PINSTID) {

	logger.info("Calling ServicesSequenceLogics.executeFromPSLOnwards() for--> " + PINSTID);
	Map<String, Map<String, String>> fiServiceResMap = new HashMap<>();
	Map<String, String> RateOfInterest = new LinkedHashMap<>();
	Map<String, String> sanctionedLimit = new LinkedHashMap<>();
	Map<String, String> accountAndLimitNode = new LinkedHashMap<>();
	Map<String, String> PSL = new LinkedHashMap<>();
	Map<String, String> childLimitNodeResponseMap = new LinkedHashMap<>();
	try {
	    String proposalType = parentLimitNodeModificationUtility.getProposalType(PINSTID);
	    childFacilityList = createFacilityLimitList.createChildList(PINSTID);
	    for (Map<String, String> facilitywiseChildLimitNodeMap : childFacilityList) {

		logger.info("ServicesSequenceLogics.executeFromPSLOnwards().facilitywiseChildLimitNodeMap check-->"
			+ facilitywiseChildLimitNodeMap);
		logger.info("Checking Child Flag in executeFromPSLOnwards--->" + (checkStatusOfIndividualServiceFlag(
			"CHILD LIMIT NODE MODIFICATION : "
				+ OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")),
			PINSTID)
			&& checkStatusOfIndividualServiceFlag("CHILD LIMIT NODE CREATION : "
				+ OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")),
				PINSTID)));

		if (checkStatusOfIndividualServiceFlag(
			"CHILD LIMIT NODE MODIFICATION : "
				+ OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")),
			PINSTID)
			&& checkStatusOfIndividualServiceFlag("CHILD LIMIT NODE CREATION : "
				+ OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")),
				PINSTID)
			&& checkStatusOfIndividualServiceFlag("CHILD LIMIT NODE MODIFICATION : " + OperationUtillity
				.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME") + " CROSS CALL"),
				PINSTID)
			&& checkStatusOfIndividualServiceFlag("CHILD LIMIT NODE CREATION : " + OperationUtillity
				.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME") + " CROSS CALL"),
				PINSTID)) {

		    logger.info("Proposal type condition checking--->"
			    + ("Renewal with Enhancement of limits".toUpperCase().equalsIgnoreCase(proposalType)
				    || "Pure Enhancement".toUpperCase().equalsIgnoreCase(proposalType)));

		    if ("Renewal with Enhancement of limits".toUpperCase().equalsIgnoreCase(proposalType)
			    || "Pure Enhancement".toUpperCase().equalsIgnoreCase(proposalType)) {
			childLimitNodeResponseMap = childLimitNodeModificationService
				.childLimitNodeModificationService(PINSTID, facilitywiseChildLimitNodeMap);
			fiServiceResMap.put("childLimitNodeModification" + " : "+ OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")),
				childLimitNodeResponseMap);
			logger.info("ServicesSequenceLogics.executeFromPSLOnwards().childLimitNodeModification-->"
				+ childLimitNodeResponseMap);
			if (!childLimitNodeResponseMap.get("Status").equalsIgnoreCase("success")) {
			    return fiServiceResMap;
			}
		    } else {
			childLimitNodeResponseMap = childLimitNodeCreationDao.childLimitNodeCreationDaoImlp(PINSTID,
				facilitywiseChildLimitNodeMap);
			logger.info("ServicesSequenceLogics.executeFromPSLOnwards().childLimitNodeResponseMap-->"
				+ childLimitNodeResponseMap);
			fiServiceResMap.put("childLimitNodeResponseMap" + " : "
					+ OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")),
				childLimitNodeResponseMap);
			if (!childLimitNodeResponseMap.get("Status").equalsIgnoreCase("success")) {
			    return fiServiceResMap;
			}
		    }
		} else {
		    logger.info("into the else of  childLimitNodeResponseMap.put(\"Status\", \"Success\");");
		    childLimitNodeResponseMap.put("Status", "Success");
		    logger.info("childLimitNodeResponseMap--->" + childLimitNodeResponseMap);
		}
		try {
		    Set<String> keys = null;
		    int counter = -1;
		    keys = facilitywiseChildLimitNodeMap.keySet();
		    Iterator<String> ids = keys.iterator();
		    while (ids.hasNext()) {
			if ((ids.next()).contains("ACCOUNT_NO_")) {
			    counter++;
			}
		    }
		    for (int i = 0; i <= counter; i++) {
			logger.info(
				"Account No Inside for loop :" + facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i));
			if (OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i)) != null
				&& !OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i))
					.equals("")) {
			    try {
				String DISTRICT_CODE = OperationUtillity
					.NullReplace(createFacilityLimitList.getDistrictCodeFromMaster(
						facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i), PINSTID));
				facilitywiseChildLimitNodeMap.put("DISTRICT_CODE", DISTRICT_CODE);
				String INDUSTRY_CODE = PSLSoapRequestDataMap.getIndustryCodeFromMaster(PINSTID);
				logger.info("[PSLController].[PSL()] [INDUSTRY_CODE] :: " + INDUSTRY_CODE);
				facilitywiseChildLimitNodeMap.put("INDUSTRY_CODE", INDUSTRY_CODE);

				// If child limit succeeds then sanctioned limit will be executed

				// checking for sanction
				logger.info("For RequestType-->" + "SANCTION LIMIT :"
					+ OperationUtillity
						.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME"))
					+ " : " + OperationUtillity
						.NullReplace(facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i)));
				logger.info("For Sanction flag -->" + checkStatusOfIndividualServiceFlag(
					"SANCTION LIMIT :"
						+ OperationUtillity
							.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME"))
						+ " : "
						+ OperationUtillity.NullReplace(
							facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i))
						+ "",
					PINSTID));
				if (checkStatusOfIndividualServiceFlag(
					"SANCTION LIMIT :"
						+ OperationUtillity
							.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME"))
						+ " :  "
						+ OperationUtillity.NullReplace(
							facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i))
						+ "",
					PINSTID)) {
				    if (childLimitNodeResponseMap.get("Status").equalsIgnoreCase("success")) {
					sanctionedLimit = sanctionedLimitDao.sanctionedLimitDaoImlp(PINSTID,
						facilitywiseChildLimitNodeMap, i);
					logger.info("ServicesSequenceLogics.executeFromPSLOnwards().sanctionedLimit-->"
						+ sanctionedLimit);
					fiServiceResMap.put("sanctionedLimit : "
						+ OperationUtillity
							.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME"))
						+ " : "
						+ OperationUtillity.NullReplace(
							facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i))
						+ "", sanctionedLimit);
					if (!sanctionedLimit.get("Status").equalsIgnoreCase("success")) {
					    return fiServiceResMap;
					}
				    } else {
					sanctionedLimit.put("Status", "Success");
					logger.info("into sanctionedLimit else---->");
					return fiServiceResMap;
				    }
				}
				// checking for account linkage
				logger.info("ServicesSequenceLogics.executeFromPSLOnwards().accountAndLimitNode ------>"
					+ i + "<------>" + facilitywiseChildLimitNodeMap);
				logger.info("ServicesSequenceLogics.executeFromPSLOnwards().accountAndLimitNode "
					+ "ACCOUNT AND LIMIT NODE LINKAGE : "
					+ facilitywiseChildLimitNodeMap.get("FACILITY_NAME") + " Account No : "
					+ facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i));
				logger.info(
					"ServicesSequenceLogics.executeFromPSLOnwards().account linkage flag check-->"
						+ (checkStatusOfIndividualServiceFlag(
							"ACCOUNT AND LIMIT NODE LINKAGE : "
								+ facilitywiseChildLimitNodeMap.get("FACILITY_NAME")
								+ " Account No : "+ facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i)
								+ "",
							PINSTID)));
				if (checkStatusOfIndividualServiceFlag("ACCOUNT AND LIMIT NODE LINKAGE : "
					+ facilitywiseChildLimitNodeMap.get("FACILITY_NAME") + " Account No : "
					+ facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i) + "", PINSTID)) {
				    if (sanctionedLimit.get("Status").equalsIgnoreCase("success")) {
					logger.info(
						"ServicesSequenceLogics.executeFromPSLOnwards().accountAndLimitNode "
							+ i + " -->" + facilitywiseChildLimitNodeMap);
					accountAndLimitNode = accountAndLimitNodeLinkageDao
						.accountAndLimitNodeLinkageDaoImpl(PINSTID,
							facilitywiseChildLimitNodeMap, i);
					fiServiceResMap.put(
						"accountAndLimitNodeLinkage : "
							+ OperationUtillity.NullReplace(
								facilitywiseChildLimitNodeMap.get("FACILITY_NAME"))
							+ " : "
							+ OperationUtillity.NullReplace(
								facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i)),
						accountAndLimitNode);
					if (!accountAndLimitNode.get("Status").equalsIgnoreCase("success")) {
					    return fiServiceResMap;
					}
				    } else {
					accountAndLimitNode.put("Status", "Success");
					logger.info("into accountAndLimitNode else---->");
					return fiServiceResMap;
				    }
				} else {
				    accountAndLimitNode.put("Status", "Success");
				}
				logger.info("ServicesSequenceLogics.executeFromPSLOnwards()-->\n" + "PSL : "
					+ facilitywiseChildLimitNodeMap.get("FACILITY_NAME") + " Account No : "
					+ facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i));
				if (checkStatusOfIndividualServiceFlag(
					"PSL : " + facilitywiseChildLimitNodeMap.get("FACILITY_NAME") + " Account No : "
						+ facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i) + " ",
					PINSTID)) {
				    if (accountAndLimitNode.get("Status").equalsIgnoreCase("success")) {
					Map<String, String> plsMap = new LinkedHashMap<String, String>();
					plsMap = createFacilityLimitList.getPSLOtherData(PINSTID, OperationUtillity
						.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")));

					Set<String> keySet = plsMap.keySet();
					Iterator<String> plsI = keySet.iterator();
					while (plsI.hasNext()) {
					    String key = plsI.next();
					    facilitywiseChildLimitNodeMap.put(key, plsMap.get(key));
					}
					PSL = PSLDao.PSLDaoImlp(PINSTID, facilitywiseChildLimitNodeMap, i);
					logger.info("ServicesSequenceLogics.executeFromPSLOnwards().PSL-->" + PSL);
					fiServiceResMap.put(
						"PSL : " + OperationUtillity
							.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME"))
							+ " : "
							+ OperationUtillity.NullReplace(
								facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i))
							+ "",
						PSL);
					if (!PSL.get("Status").equalsIgnoreCase("success")) {
					    return fiServiceResMap;
					}
				    } else {
					logger.info("into PSL else---->");
					return fiServiceResMap;
				    }
				} else {
				    PSL.put("Status", "Success");
				}

//									String ip = "RATE OF INTEREST : "+facilitywiseChildLimitNodeMap.get("FACILITY_NAME") +" : Data =  ACCOUNT_NUMBER : "+facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i)+", RATE_CODE : "+facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i)+", ROI_SPREAD : "+facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i)+", ROI_PEGGED_FLAG : "+facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i)+", PEGGING_FREQUENCY_IN_MONTHS : "+facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i)+", NUMBER_OF_DAYS : "+facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i)+", ROI_Spread : "+facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i)+", ROI_START_DATE : "+LocalDateTime.now()+", NUMBER_OF_DAYS_DATE : "+facilitywiseChildLimitNodeMap.get("NUMBER_OF_DAYS");

				String ip = "RATE OF INTEREST : " + facilitywiseChildLimitNodeMap.get("FACILITY_NAME")
					+ " : Data =  ACCOUNT_NUMBER : "
					+ facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i) + ", RATE_CODE : "
					+ facilitywiseChildLimitNodeMap.get("RATE_CODE") + ", ROI_SPREAD : "
					+ facilitywiseChildLimitNodeMap.get("ROI_SPREAD") + ", ROI_PEGGED_FLAG : "
					+ facilitywiseChildLimitNodeMap.get("ROI_PEGGED_FLAG")
					+ ", PEGGING_FREQUENCY_IN_MONTHS : "
					+ facilitywiseChildLimitNodeMap.get("PEGGING_FREQUENCY") + ", NUMBER_OF_DAYS : "
					+ facilitywiseChildLimitNodeMap.get("NUMBER_OF_DAYS") + ", ROI_Spread : "
					+ facilitywiseChildLimitNodeMap.get("ROI_SPREAD") + ", NUMBER_OF_DAYS_DATE : "
					+ facilitywiseChildLimitNodeMap.get("ROI_END_DATE");

				logger.info("ServicesSequenceLogics.executeFromPSLOnwards()-->" + ip);
				if (checkStatusOfIndividualServiceFlag(ip, PINSTID)) {

				    if (PSL.get("Status").equalsIgnoreCase("success")) {
					RateOfInterest = RateOfInterestDao.RateOfInterestDaoImpl(PINSTID,
						facilitywiseChildLimitNodeMap, i);
					logger.info("ServicesSequenceLogics.executeFromPSLOnwards().RateOfInterest-->"
						+ RateOfInterest);
					fiServiceResMap.put(
						"RateOfInterest : "
							+ OperationUtillity.NullReplace(
								facilitywiseChildLimitNodeMap.get("FACILITY_NAME"))
							+ " : "
							+ OperationUtillity.NullReplace(
								facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i)),
						RateOfInterest);
					if (!RateOfInterest.get("Status").equalsIgnoreCase("success")) {
					    return fiServiceResMap;
					}
				    } else {
					RateOfInterest.put("Status", "Success");
					logger.info("into RateOfInterest else---->");
					return fiServiceResMap;
				    }
				} else {
				    RateOfInterest.put("Status", "Success");
				}
			    } catch (Exception e1) {
				logger.info("There is exception --->" + OperationUtillity.traceException(e1));
			    }
			}
		    }
		} catch (Exception ex) {
		    logger.info("There is problem while executing services account wise "
			    + OperationUtillity.traceException(ex));
		}
//			}
	    }
	} catch (Exception e) {
	    logger.info("Exception in ServicesSequenceLogics.executeFromPSLOnwards()-->"
		    + OperationUtillity.traceException(e));
	}
	return fiServiceResMap;
    }

    public Map<String, Map<String, String>> executeFromRateIntrestLinkageOnwards(String PINSTID) {

	logger.info("Calling ServicesSequenceLogics.executeFromRateIntrestLinkageOnwards() for--> " + PINSTID);
	Map<String, Map<String, String>> fiServiceResMap = new HashMap<>();
	Map<String, String> RateOfInterest = new LinkedHashMap<>();
	Map<String, String> sanctionedLimit = new LinkedHashMap<>();
	Map<String, String> accountAndLimitNode = new LinkedHashMap<>();
	Map<String, String> PSL = new LinkedHashMap<>();
	Map<String, String> childLimitNodeResponseMap = new LinkedHashMap<>();
	try {
	    String proposalType = parentLimitNodeModificationUtility.getProposalType(PINSTID);
	    childFacilityList = createFacilityLimitList.createChildList(PINSTID);
	    logger.info("ServicesSequenceLogics.executeFromRateIntrestLinkageOnwards().childFacilityList  ---->"
		    + childFacilityList);
	    for (Map<String, String> facilitywiseChildLimitNodeMap : childFacilityList) {

		logger.info("Falg Check for child in executeFromRateIntrestLinkageOnwards-->"
			+ (checkStatusOfIndividualServiceFlag("CHILD LIMIT NODE MODIFICATION : "
				+ OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")),
				PINSTID)
				&& checkStatusOfIndividualServiceFlag(
					"CHILD LIMIT NODE CREATION : " + OperationUtillity
						.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")),
					PINSTID)));

		if (checkStatusOfIndividualServiceFlag(
			"CHILD LIMIT NODE MODIFICATION : "
				+ OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")),
			PINSTID)
			&& checkStatusOfIndividualServiceFlag("CHILD LIMIT NODE CREATION : "
				+ OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")),
				PINSTID)
			&& checkStatusOfIndividualServiceFlag("CHILD LIMIT NODE MODIFICATION : " + OperationUtillity
				.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME") + " CROSS CALL"),
				PINSTID)
			&& checkStatusOfIndividualServiceFlag("CHILD LIMIT NODE CREATION : " + OperationUtillity
				.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME") + " CROSS CALL"),
				PINSTID)) {
		    logger.info("Proposal type condition checking--->"
			    + ("Renewal with Enhancement of limits".toUpperCase().equalsIgnoreCase(proposalType)
				    || "Pure Enhancement".toUpperCase().equalsIgnoreCase(proposalType)));

		    if ("Renewal with Enhancement of limits".toUpperCase().equalsIgnoreCase(proposalType)
			    || "Pure Enhancement".toUpperCase().equalsIgnoreCase(proposalType)) {
			childLimitNodeResponseMap = childLimitNodeModificationService
				.childLimitNodeModificationService(PINSTID, facilitywiseChildLimitNodeMap);
			logger.info(
				"ServicesSequenceLogics.executeFromRateIntrestLinkageOnwards().childLimitNodeModification->"
					+ childLimitNodeResponseMap);
			fiServiceResMap.put(
				"childLimitNodeModification" + " : "
					+ OperationUtillity
						.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")),
				childLimitNodeResponseMap);
			if (!childLimitNodeResponseMap.get("Status").equalsIgnoreCase("success")) {
			    return fiServiceResMap;
			}
		    } else {
			childLimitNodeResponseMap = childLimitNodeCreationDao.childLimitNodeCreationDaoImlp(PINSTID,
				facilitywiseChildLimitNodeMap);
			logger.info(
				"ServicesSequenceLogics.executeFromRateIntrestLinkageOnwards().childLimitNodeResponseMap->"
					+ childLimitNodeResponseMap);
			fiServiceResMap.put(
				"childLimitNodeResponseMap" + " : "
					+ OperationUtillity
						.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")),
				childLimitNodeResponseMap);
			if (!childLimitNodeResponseMap.get("Status").equalsIgnoreCase("success")) {
			    return fiServiceResMap;
			}
		    }
		} else {
		    logger.info("into the else of  childLimitNodeResponseMap.put(\"Status\", \"Success\");");
		    childLimitNodeResponseMap.put("Status", "Success");
		    logger.info("childLimitNodeResponseMap--->" + childLimitNodeResponseMap);
		}
		logger.info(
			"ServicesSequenceLogics.executeFromRateIntrestLinkageOnwards()-->childFacilityList outside of  try childLimitNodeResponseMap-->"
				+ childLimitNodeResponseMap);
		try {

		    logger.info(
			    "ServicesSequenceLogics.executeFromRateIntrestLinkageOnwards()-->childFacilityList before try-->");
		    Set<String> keys = null;
		    int counter = -1;
		    keys = facilitywiseChildLimitNodeMap.keySet();
		    Iterator<String> ids = keys.iterator();
		    while (ids.hasNext()) {
			if ((ids.next()).contains("ACCOUNT_NO_")) {
			    counter++;
			}
		    }
		    logger.info("ServicesSequenceLogics.executeFromRateIntrestLinkageOnwards()-->childFacilityList-->"
			    + counter);
		    for (int i = 0; i <= counter; i++) {
			logger.info(
				"Account No Inside for loop :" + facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i));
			if (OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i)) != null
				&& !OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i))
					.equals("")) {
			    try {
				String DISTRICT_CODE = OperationUtillity
					.NullReplace(createFacilityLimitList.getDistrictCodeFromMaster(
						facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i), PINSTID));
				facilitywiseChildLimitNodeMap.put("DISTRICT_CODE", DISTRICT_CODE);
				String INDUSTRY_CODE = PSLSoapRequestDataMap.getIndustryCodeFromMaster(PINSTID);
				logger.info("[PSLController].[PSL()] [INDUSTRY_CODE] :: " + INDUSTRY_CODE);
				facilitywiseChildLimitNodeMap.put("INDUSTRY_CODE", INDUSTRY_CODE);

				// If child limit succeeds then sanctioned limit will be executed

				// checking for sanction
				if (checkStatusOfIndividualServiceFlag(
					"SANCTION LIMIT :"
						+ OperationUtillity
							.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME"))
						+ " :  "
						+ OperationUtillity.NullReplace(
							facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i))
						+ "",
					PINSTID)) {
				    if (childLimitNodeResponseMap.get("Status").equalsIgnoreCase("success")) {
					sanctionedLimit = sanctionedLimitDao.sanctionedLimitDaoImlp(PINSTID,
						facilitywiseChildLimitNodeMap, i);
					logger.info(
						"ServicesSequenceLogics.executeFromRateIntrestLinkageOnwards().sanctionedLimit->"
							+ sanctionedLimit);
					fiServiceResMap.put("sanctionedLimit : "
						+ OperationUtillity
							.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME"))
						+ " : "
						+ OperationUtillity.NullReplace(
							facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i))
						+ "", sanctionedLimit);
					if (!sanctionedLimit.get("Status").equalsIgnoreCase("success")) {
					    return fiServiceResMap;
					}
				    } else {
					logger.info("into sanctionedLimit else---->");
					return fiServiceResMap;
				    }
				} else {
				    sanctionedLimit.put("Status", "Success");
				}
				// checking for account linkage
				if (checkStatusOfIndividualServiceFlag("ACCOUNT AND LIMIT NODE LINKAGE : "
					+ facilitywiseChildLimitNodeMap.get("FACILITY_NAME") + " Account No : "
					+ facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i) + "", PINSTID)) {
				    if (sanctionedLimit.get("Status").equalsIgnoreCase("success")) {
					accountAndLimitNode = accountAndLimitNodeLinkageDao.accountAndLimitNodeLinkageDaoImpl(PINSTID,facilitywiseChildLimitNodeMap, i);
					logger.info("ServicesSequenceLogics.executeFromRateIntrestLinkageOnwards().accountAndLimitNode->"+ accountAndLimitNode);
					fiServiceResMap.put("accountAndLimitNodeLinkage : "+ OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME"))
							+ " : "+ OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i)),accountAndLimitNode);
					if (!accountAndLimitNode.get("Status").equalsIgnoreCase("success")) {
					    return fiServiceResMap;
					}
				    } else {
					logger.info("into accountAndLimitNode else---->");
					return fiServiceResMap;
				    }
				} else {
				    accountAndLimitNode.put("Status", "Success");
				}
				if (checkStatusOfIndividualServiceFlag("PSL : " + facilitywiseChildLimitNodeMap.get("FACILITY_NAME") + " Account No : "
						+ facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i) + " ",PINSTID)) {

				    if (accountAndLimitNode.get("Status").equalsIgnoreCase("success")) {
					Map<String, String> plsMap = new LinkedHashMap<String, String>();
					plsMap = createFacilityLimitList.getPSLOtherData(PINSTID, OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")));

					Set<String> keySet = plsMap.keySet();
					logger.info("keySet--->" + keySet.toString());
					Iterator<String> plsI = keySet.iterator();
					logger.info("plsI iterator--->" + plsI.toString());

					while (plsI.hasNext()) {
					    String key = plsI.next();
					    facilitywiseChildLimitNodeMap.put(key, plsMap.get(key));
					}
					PSL = PSLDao.PSLDaoImlp(PINSTID, facilitywiseChildLimitNodeMap, i);
					logger.info(
						"ServicesSequenceLogics.executeFromRateIntrestLinkageOnwards().PSL->"
							+ PSL);
					fiServiceResMap.put(
						"PSL : " + OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME"))
							+ " : "+ OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i))+ "",
						PSL);
					if (!PSL.get("Status").equalsIgnoreCase("success")) {
					    return fiServiceResMap;
					}
				    } else {
					logger.info("into PSL else---->");
					return fiServiceResMap;
				    }
				} else {
				    PSL.put("Status", "Success");
				}

//									String ip = "RATE OF INTEREST : "+facilitywiseChildLimitNodeMap.get("FACILITY_NAME") +" : Data =  ACCOUNT_NUMBER : "+facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i)+", RATE_CODE : "+facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i)+", ROI_SPREAD : "+facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i)+", ROI_PEGGED_FLAG : "+facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i)+", PEGGING_FREQUENCY_IN_MONTHS : "+facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i)+", NUMBER_OF_DAYS : "+facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i)+", ROI_Spread : "+facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i)+", ROI_START_DATE : "+LocalDateTime.now()+", NUMBER_OF_DAYS_DATE : "+facilitywiseChildLimitNodeMap.get("NUMBER_OF_DAYS");

				String ip = "RATE OF INTEREST : " + facilitywiseChildLimitNodeMap.get("FACILITY_NAME")
					+ " : Data =  ACCOUNT_NUMBER : "
					+ facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i) + ", RATE_CODE : "
					+ facilitywiseChildLimitNodeMap.get("RATE_CODE") + ", ROI_SPREAD : "
					+ facilitywiseChildLimitNodeMap.get("ROI_SPREAD") + ", ROI_PEGGED_FLAG : "
					+ facilitywiseChildLimitNodeMap.get("ROI_PEGGED_FLAG")
					+ ", PEGGING_FREQUENCY_IN_MONTHS : "
					+ facilitywiseChildLimitNodeMap.get("PEGGING_FREQUENCY") + ", NUMBER_OF_DAYS : "
					+ facilitywiseChildLimitNodeMap.get("NUMBER_OF_DAYS") + ", ROI_Spread : "
					+ facilitywiseChildLimitNodeMap.get("ROI_SPREAD") + ", NUMBER_OF_DAYS_DATE : "
					+ facilitywiseChildLimitNodeMap.get("ROI_END_DATE");

				if (checkStatusOfIndividualServiceFlag(ip, PINSTID)) {

				    if (PSL.get("Status").equalsIgnoreCase("success")) {
					RateOfInterest = RateOfInterestDao.RateOfInterestDaoImpl(PINSTID,
						facilitywiseChildLimitNodeMap, i);

					logger.info(
						"ServicesSequenceLogics.executeFromRateIntrestLinkageOnwards().RateOfInterest->"
							+ RateOfInterest);
					fiServiceResMap.put(
						"RateOfInterest : "
							+ OperationUtillity.NullReplace(
								facilitywiseChildLimitNodeMap.get("FACILITY_NAME"))
							+ " : "
							+ OperationUtillity.NullReplace(
								facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i)),
						RateOfInterest);
					if (!RateOfInterest.get("Status").equalsIgnoreCase("success")) {
					    return fiServiceResMap;
					}
				    } else {
					logger.info("into RateOfInterest else---->");
					return fiServiceResMap;
				    }
				} else {
				    RateOfInterest.put("Status", "Success");
				}
			    } catch (Exception e1) {
				logger.info("There is exception --->" + OperationUtillity.traceException(e1));
			    }
			}
		    }
		} catch (Exception ex) {
		    logger.info(
			    "There is problem while executing ServicesSequenceLogics.executeFromRateIntrestLinkageOnwards() "
				    + OperationUtillity.traceException(ex));
		}
//			}
	    }
	} catch (Exception e) {
	    logger.info("ServicesSequenceLogics.executeFromRateIntrestLinkageOnwards()-->"
		    + OperationUtillity.traceException(e));
	}
	return fiServiceResMap;
    }

    public Map<String, Map<String, String>> executeFromCrossCallOnwards(String PINSTID) {

	logger.info("Calling ServicesSequenceLogics.executeFromRateIntrestLinkageOnwards() for--> " + PINSTID);
	Map<String, Map<String, String>> fiServiceResMap = new HashMap<>();
	Map<String, String> RateOfInterest = new LinkedHashMap<>();
	Map<String, String> sanctionedLimit = new LinkedHashMap<>();
	Map<String, String> accountAndLimitNode = new LinkedHashMap<>();
	Map<String, String> PSL = new LinkedHashMap<>();
	Map<String, String> childLimitNodeResponseMap = new LinkedHashMap<>();
	try {
	    String proposalType = parentLimitNodeModificationUtility.getProposalType(PINSTID);
	    childFacilityList = createFacilityLimitList.createChildList(PINSTID);
	    logger.info("ServicesSequenceLogics.executeFromRateIntrestLinkageOnwards().childFacilityList  ---->"
		    + childFacilityList);
	    for (Map<String, String> facilitywiseChildLimitNodeMap : childFacilityList) {

		logger.info("Falg Check for child in executeFromRateIntrestLinkageOnwards-->"
			+ (checkStatusOfIndividualServiceFlag("CHILD LIMIT NODE MODIFICATION : "
				+ OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")),
				PINSTID)
				&& checkStatusOfIndividualServiceFlag(
					"CHILD LIMIT NODE CREATION : " + OperationUtillity
						.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")),
					PINSTID)));

		if (checkStatusOfIndividualServiceFlag(
			"CHILD LIMIT NODE MODIFICATION : "
				+ OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")),
			PINSTID)
			&& checkStatusOfIndividualServiceFlag("CHILD LIMIT NODE CREATION : "
				+ OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")),
				PINSTID)
			&& checkStatusOfIndividualServiceFlag("CHILD LIMIT NODE MODIFICATION : " + OperationUtillity
				.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME") + " CROSS CALL"),
				PINSTID)
			&& checkStatusOfIndividualServiceFlag("CHILD LIMIT NODE CREATION : " + OperationUtillity
				.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME") + " CROSS CALL"),
				PINSTID)) {
		    logger.info("Proposal type condition checking--->"
			    + ("Renewal with Enhancement of limits".toUpperCase().equalsIgnoreCase(proposalType)
				    || "Pure Enhancement".toUpperCase().equalsIgnoreCase(proposalType)));

		    if ("Renewal with Enhancement of limits".toUpperCase().equalsIgnoreCase(proposalType)
			    || "Pure Enhancement".toUpperCase().equalsIgnoreCase(proposalType)) {
			childLimitNodeResponseMap = childLimitNodeModificationService
				.childLimitNodeModificationService(PINSTID, facilitywiseChildLimitNodeMap);
			logger.info(
				"ServicesSequenceLogics.executeFromRateIntrestLinkageOnwards().childLimitNodeModification->"
					+ childLimitNodeResponseMap);
			fiServiceResMap.put(
				"childLimitNodeModification" + " : "
					+ OperationUtillity
						.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")),
				childLimitNodeResponseMap);
			if (!childLimitNodeResponseMap.get("Status").equalsIgnoreCase("success")) {
			    return fiServiceResMap;
			}
		    } else {
			childLimitNodeResponseMap = childLimitNodeCreationDao.childLimitNodeCreationDaoImlp(PINSTID,
				facilitywiseChildLimitNodeMap);
			logger.info(
				"ServicesSequenceLogics.executeFromRateIntrestLinkageOnwards().childLimitNodeResponseMap->"
					+ childLimitNodeResponseMap);
			fiServiceResMap.put(
				"childLimitNodeResponseMap" + " : "
					+ OperationUtillity
						.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")),
				childLimitNodeResponseMap);
			if (!childLimitNodeResponseMap.get("Status").equalsIgnoreCase("success")) {
			    return fiServiceResMap;
			}
		    }
		} else {
		    logger.info("into the else of  childLimitNodeResponseMap.put(\"Status\", \"Success\");");
		    childLimitNodeResponseMap.put("Status", "Success");
		    logger.info("childLimitNodeResponseMap--->" + childLimitNodeResponseMap);
		}
		logger.info(
			"ServicesSequenceLogics.executeFromRateIntrestLinkageOnwards()-->childFacilityList outside of  try childLimitNodeResponseMap-->"
				+ childLimitNodeResponseMap);
		try {

		    logger.info(
			    "ServicesSequenceLogics.executeFromRateIntrestLinkageOnwards()-->childFacilityList before try-->");
		    Set<String> keys = null;
		    int counter = -1;
		    keys = facilitywiseChildLimitNodeMap.keySet();
		    Iterator<String> ids = keys.iterator();
		    while (ids.hasNext()) {
			if ((ids.next()).contains("ACCOUNT_NO_")) {
			    counter++;
			}
		    }
		    logger.info("ServicesSequenceLogics.executeFromRateIntrestLinkageOnwards()-->childFacilityList-->"
			    + counter);
		    for (int i = 0; i <= counter; i++) {
			logger.info(
				"Account No Inside for loop :" + facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i));
			if (OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i)) != null
				&& !OperationUtillity.NullReplace(facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i))
					.equals("")) {
			    try {
				String DISTRICT_CODE = OperationUtillity
					.NullReplace(createFacilityLimitList.getDistrictCodeFromMaster(
						facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i), PINSTID));
				facilitywiseChildLimitNodeMap.put("DISTRICT_CODE", DISTRICT_CODE);
				String INDUSTRY_CODE = PSLSoapRequestDataMap.getIndustryCodeFromMaster(PINSTID);
				logger.info("[PSLController].[PSL()] [INDUSTRY_CODE] :: " + INDUSTRY_CODE);
				facilitywiseChildLimitNodeMap.put("INDUSTRY_CODE", INDUSTRY_CODE);

				// If child limit succeeds then sanctioned limit will be executed

				// checking for sanction
				if (checkStatusOfIndividualServiceFlag(
					"SANCTION LIMIT :"
						+ OperationUtillity
							.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME"))
						+ " :  "
						+ OperationUtillity.NullReplace(
							facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i))
						+ "",
					PINSTID)) {
				    if (childLimitNodeResponseMap.get("Status").equalsIgnoreCase("success")) {
					sanctionedLimit = sanctionedLimitDao.sanctionedLimitDaoImlp(PINSTID,
						facilitywiseChildLimitNodeMap, i);
					logger.info(
						"ServicesSequenceLogics.executeFromRateIntrestLinkageOnwards().sanctionedLimit->"
							+ sanctionedLimit);
					fiServiceResMap.put("sanctionedLimit : "
						+ OperationUtillity
							.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME"))
						+ " : "
						+ OperationUtillity.NullReplace(
							facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i))
						+ "", sanctionedLimit);
					if (!sanctionedLimit.get("Status").equalsIgnoreCase("success")) {
					    return fiServiceResMap;
					}
				    } else {
					logger.info("into sanctionedLimit else---->");
					return fiServiceResMap;
				    }
				} else {
				    sanctionedLimit.put("Status", "Success");
				}
				// checking for account linkage
				if (checkStatusOfIndividualServiceFlag("ACCOUNT AND LIMIT NODE LINKAGE : "
					+ facilitywiseChildLimitNodeMap.get("FACILITY_NAME") + " Account No : "
					+ facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i) + "", PINSTID)) {
				    if (sanctionedLimit.get("Status").equalsIgnoreCase("success")) {
					accountAndLimitNode = accountAndLimitNodeLinkageDao
						.accountAndLimitNodeLinkageDaoImpl(PINSTID,
							facilitywiseChildLimitNodeMap, i);
					logger.info(
						"ServicesSequenceLogics.executeFromRateIntrestLinkageOnwards().accountAndLimitNode->"
							+ accountAndLimitNode);
					fiServiceResMap.put(
						"accountAndLimitNodeLinkage : "
							+ OperationUtillity.NullReplace(
								facilitywiseChildLimitNodeMap.get("FACILITY_NAME"))
							+ " : "
							+ OperationUtillity.NullReplace(
								facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i)),
						accountAndLimitNode);
					if (!accountAndLimitNode.get("Status").equalsIgnoreCase("success")) {
					    return fiServiceResMap;
					}
				    } else {
					logger.info("into accountAndLimitNode else---->");
					return fiServiceResMap;
				    }
				} else {
				    accountAndLimitNode.put("Status", "Success");
				}
				if (checkStatusOfIndividualServiceFlag(
					"PSL : " + facilitywiseChildLimitNodeMap.get("FACILITY_NAME") + " Account No : "
						+ facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i) + " ",
					PINSTID)) {

				    if (accountAndLimitNode.get("Status").equalsIgnoreCase("success")) {
					Map<String, String> plsMap = new LinkedHashMap<String, String>();
					plsMap = createFacilityLimitList.getPSLOtherData(PINSTID, OperationUtillity
						.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME")));

					Set<String> keySet = plsMap.keySet();
					logger.info("keySet--->" + keySet.toString());
					Iterator<String> plsI = keySet.iterator();
					logger.info("plsI iterator--->" + plsI.toString());
					while (plsI.hasNext()) {
					    String key = plsI.next();
					    facilitywiseChildLimitNodeMap.put(key, plsMap.get(key));
					}
					PSL = PSLDao.PSLDaoImlp(PINSTID, facilitywiseChildLimitNodeMap, i);
					logger.info(
						"ServicesSequenceLogics.executeFromRateIntrestLinkageOnwards().PSL->"
							+ PSL);
					fiServiceResMap.put(
						"PSL : " + OperationUtillity
							.NullReplace(facilitywiseChildLimitNodeMap.get("FACILITY_NAME"))
							+ " : "
							+ OperationUtillity.NullReplace(
								facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i))
							+ "",
						PSL);
					if (!PSL.get("Status").equalsIgnoreCase("success")) {
					    return fiServiceResMap;
					}
				    } else {
					logger.info("into PSL else---->");
					return fiServiceResMap;
				    }
				} else {
				    PSL.put("Status", "Success");
				}

				String ip = "RATE OF INTEREST : " + facilitywiseChildLimitNodeMap.get("FACILITY_NAME")
					+ " : Data =  ACCOUNT_NUMBER : "
					+ facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i) + ", RATE_CODE : "
					+ facilitywiseChildLimitNodeMap.get("RATE_CODE") + ", ROI_SPREAD : "
					+ facilitywiseChildLimitNodeMap.get("ROI_SPREAD") + ", ROI_PEGGED_FLAG : "
					+ facilitywiseChildLimitNodeMap.get("ROI_PEGGED_FLAG")
					+ ", PEGGING_FREQUENCY_IN_MONTHS : "
					+ facilitywiseChildLimitNodeMap.get("PEGGING_FREQUENCY") + ", NUMBER_OF_DAYS : "
					+ facilitywiseChildLimitNodeMap.get("NUMBER_OF_DAYS") + ", ROI_Spread : "
					+ facilitywiseChildLimitNodeMap.get("ROI_SPREAD") + ", NUMBER_OF_DAYS_DATE : "
					+ facilitywiseChildLimitNodeMap.get("ROI_END_DATE");

				if (checkStatusOfIndividualServiceFlag(ip, PINSTID)) {

				    if (PSL.get("Status").equalsIgnoreCase("success")) {
					RateOfInterest = RateOfInterestDao.RateOfInterestDaoImpl(PINSTID,
						facilitywiseChildLimitNodeMap, i);

					logger.info(
						"ServicesSequenceLogics.executeFromRateIntrestLinkageOnwards().RateOfInterest->"
							+ RateOfInterest);
					fiServiceResMap.put(
						"RateOfInterest : "
							+ OperationUtillity.NullReplace(
								facilitywiseChildLimitNodeMap.get("FACILITY_NAME"))
							+ " : "
							+ OperationUtillity.NullReplace(
								facilitywiseChildLimitNodeMap.get("ACCOUNT_NO_" + i)),
						RateOfInterest);
					if (!RateOfInterest.get("Status").equalsIgnoreCase("success")) {
					    return fiServiceResMap;
					}
				    } else {
					logger.info("into RateOfInterest else---->");
					return fiServiceResMap;
				    }
				} else {
				    RateOfInterest.put("Status", "Success");
				}
			    } catch (Exception e1) {
				logger.info("There is exception --->" + OperationUtillity.traceException(e1));
			    }
			}
		    }
		} catch (Exception ex) {
		    logger.info(
			    "There is problem while executing ServicesSequenceLogics.executeFromRateIntrestLinkageOnwards() "
				    + OperationUtillity.traceException(ex));
		}
//			}
	    }
	} catch (Exception e) {
	    logger.info(
		    "ServicesSequenceLogics.executeFromCrossCallOnwards()-->" + OperationUtillity.traceException(e));
	}
	return fiServiceResMap;
    }

    public static boolean checkStatusOfIndividualServiceFlag(String requestType, String pinstid) {

	boolean flag = true;
	try (Connection con = DBConnect.getConnection()) {
	    String lsmNumberQuery = "SELECT STATUS FROM LSM_SERVICE_REQ_RES WHERE PINSTID =? AND REQUESTTYPE=? ORDER BY DATETIME DESC FETCH FIRST ROW ONLY";
	    try (PreparedStatement stmt = con.prepareStatement(lsmNumberQuery)) {
		stmt.setString(1, pinstid);
		stmt.setString(2, requestType);
		try (ResultSet rs = stmt.executeQuery()) {
		    if (rs.next()) {
			if ("SUCCESS".equalsIgnoreCase(OperationUtillity.NullReplace(rs.getString("STATUS")))) {
			    flag = false;
			}
		    }
		}
	    } catch (Exception e) {
		logger.error("Error while getting LSM_NUMBER: " + OperationUtillity.traceException(e));
	    }
	} catch (Exception ex) {
	    logger.error("Error while getting data in getSVTCommonData(): " + OperationUtillity.traceException(ex));
	}
	return flag;
    }
}
