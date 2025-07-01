package com.LsmFiServices.Utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ParentLimitNodeModificationUtility {
    private final Logger logger = LoggerFactory.getLogger(ParentLimitNodeModificationUtility.class);

    public String getProposalType(String pinstid) {
	String proposalType = "";
	try (Connection con = DBConnect.getConnection();
		PreparedStatement pst = con.prepareStatement(
			"SELECT UPPER(PROPOSALTYPE) AS PROPOSAL_TYPE FROM LIMIT_SETUP_EXT WHERE PINSTID =?");) {
	    pst.setString(1, pinstid);
	    try (ResultSet rs = pst.executeQuery()) {
		while (rs.next()) {
		    proposalType = OperationUtillity.NullReplace(rs.getString("PROPOSAL_TYPE"));
		}
	    }
	} catch (SQLException e) {
	    logger.info("ParentLimitNodeModificationUtility.getProposalType(): " + OperationUtillity.traceException(e));
	}
	return proposalType;
    }

    public boolean isLimitAsPerSanctionIsZero(String pinstid, String facility) {
	boolean isLimitAsPerSanctionIsZero = false;
	logger.info("isLimitAsPerSanctionIsZero facility  : "+facility);
	facility = facility.contains("-") ? facility.substring(0,facility.indexOf("-")) : facility;
	try (Connection con = DBConnect.getConnection();
		PreparedStatement pst = con.prepareStatement(Queries.GET_EXISTING_LIMIT_FLAG)) {
	    pst.setString(1, pinstid);
	    pst.setString(2, facility);
	    pst.setString(3, "212");
	    try (ResultSet rs = pst.executeQuery()) {
		if (rs.next()) {
		    isLimitAsPerSanctionIsZero = commonUtility.isZero(rs.getString("EXISTING_LIMIT_AS_PER_SANCTION"));
		    //= commonUtility.isZero(Optional.ofNullable(rs.getString("EXISTING_LIMIT_AS_PER_SANCTION")).orElse("0.00"));
		}
	    }
	} catch (Exception e) {
	    logger.info("ParentLimitNodeModificationUtility.isLimitAsPerSanctionIsZero(){}",
		    OperationUtillity.traceException(e));
	}
	return isLimitAsPerSanctionIsZero;
    }

    public boolean isLimitAsPerSanctionIsZeroForCrossCall(String pinstid, String facility) {
	boolean isLimitAsPerSanctionIsZero = false;
	try (Connection con = DBConnect.getConnection();
		PreparedStatement pst = con.prepareStatement(Queries.GET_EXISTING_LIMIT_FLAG_FOR_CROSS_CALL)) {
	    pst.setString(1, pinstid);
	    pst.setString(2, facility);
	    pst.setString(3, "212");
	    try (ResultSet rs = pst.executeQuery()) {
		if (rs.next()) {
		    isLimitAsPerSanctionIsZero = commonUtility.isZero(Optional
			    .ofNullable(rs.getString("EXISTING_LIMIT_AS_PER_SANCTION_FOR_CROSS_CALL")).orElse("0.00"));
		}
	    }
	} catch (Exception e) {
	    logger.info("SVTFIServiceUtility.isLimitAsPerSanctionIsZeroForCrossCall(){}",
		    OperationUtillity.traceException(e));
	}
	return isLimitAsPerSanctionIsZero;
    }
    

     // SN BY MAHESHV ON 27122024 FOR RENEWAL
        public String getLimitType(String pinstid) {
        	String limitType = "";
        	try (Connection con = DBConnect.getConnection();
        		PreparedStatement pst = con.prepareStatement(
        			"SELECT UPPER(ANSWER) AS  ANSWER FROM LSM_MS_ANSWERS WHERE PINSTID = ? AND TAB_NAME = 'Home_Tab' AND QUESTION = 'Limit Type'");) {
        	    pst.setString(1, pinstid);
        	    try (ResultSet rs = pst.executeQuery()) {
        		while (rs.next()) {
        			limitType = OperationUtillity.NullReplace(rs.getString("ANSWER"));
        		}
        	    }
        	} catch (SQLException e) {
        	    logger.info("ParentLimitNodeModificationUtility.getProposalType(): " + OperationUtillity.traceException(e));
        	}
        	return limitType;
            }
       // EN BY MAHESHV ON 27122024 FOR RENEWAL

}
