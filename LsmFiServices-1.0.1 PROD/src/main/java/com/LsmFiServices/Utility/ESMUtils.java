package com.LsmFiServices.Utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class ESMUtils {

    private static final Logger logger = LoggerFactory.getLogger(ESMUtils.class);

    public boolean caseTypeIsESM(String pinstid) {
	boolean flag = false;
	try (Connection con = DBConnect.getConnection();
		PreparedStatement ps = con.prepareStatement(Queries.GET_MEMO_TYPE)) {
	    ps.setString(1, pinstid);
	    try (ResultSet rs = ps.executeQuery()) {
		while (rs.next()) {
		    if ("ESM".equalsIgnoreCase(Optional.ofNullable(rs.getString("MEMO_TYPE")).orElse(""))) {
			flag = true;
		    }
		}
	    }
	} catch (Exception e) {
	    logger.info("ESMUtils.getESMFlag()->" + OperationUtillity.traceException(e));
	}
	return flag;
    }

    public boolean getESMFlag(String pinstid) {

	String queryToGetFlag = "SELECT MEMO_TYPE, FI_SERVICE_STATUS FROM LIMIT_SETUP_EXT WHERE DISHUBID = (SELECT DISHUBID FROM LIMIT_SETUP_EXT WHERE PINSTID = ?)";
	String memoType = "";
	String fiStatus = "";
	boolean flag = false;
	try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(queryToGetFlag)) {
	    ps.setString(1, pinstid);
	    try (ResultSet rs = ps.executeQuery()) {
		while (rs.next()) {
		    memoType = OperationUtillity.NullReplace(rs.getString("MEMO_TYPE"));
		    fiStatus = OperationUtillity.NullReplace(rs.getString("FI_SERVICE_STATUS"));
		}
	    }
	    if ("ESM".equalsIgnoreCase(memoType) && "S".equalsIgnoreCase(fiStatus)) {
		flag = true;
	    }
	} catch (Exception e) {
	    logger.info("ESMUtils.getESMFlag()->" + OperationUtillity.traceException(e));
	}
	return flag;
    }

    public boolean getFlagToSetLimit(String pinstid) {
	String queryToGetMemoType = "SELECT FI_SERVICE_STATUS AS FI_STATUS  FROM LIMIT_SETUP_EXT WHERE PINSTID = ?";
	boolean flag = false;
	try (Connection con = DBConnect.getConnection();
		PreparedStatement ps = con.prepareStatement(queryToGetMemoType)) {
	    ps.setString(1, pinstid);
	    try (ResultSet rs = ps.executeQuery()) {
		if (rs.next()) {
		    if ("S".equalsIgnoreCase(OperationUtillity.NullReplace(rs.getString("FI_SERVICE_STATUS")))) {
			flag = true;
		    }
		}
	    }
	} catch (Exception e) {
	    logger.info(OperationUtillity.traceException(e, pinstid));
	}
	return flag;
    }

    public boolean getESMFlagForLSM(String pinstid) {
	String queryTogetFlag = "SELECT FI_SERVICE_STATUS AS FI_STATUS FROM LIMIT_SETUP_EXT WHERE DISHUBID = (SELECT DISHUBID FROM LIMIT_SETUP_EXT WHERE PINSTID= ?) AND PINSTID!=? AND MEMO_TYPE=?";
	boolean flag = false;
	try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(queryTogetFlag)) {
	    ps.setString(1, pinstid);
	    ps.setString(2, pinstid);
	    ps.setString(3, "ESM");
	    try (ResultSet rs = ps.executeQuery()) {
		if (rs.next() && "S".equalsIgnoreCase(rs.getString("FI_STATUS"))) {
		    flag = true;
		}
	    }
	} catch (Exception e) {
	    logger.info(OperationUtillity.traceException(e, pinstid));
	}
	return flag;
    }

    public boolean getIncompleteESMFlag(String pinstid) {
	boolean flag = false;
	try (Connection con = DBConnect.getConnection();
		PreparedStatement ps = con.prepareStatement(Queries.GET_INCOMPLETE_LSM_FLAG)) {
	    ps.setString(1, pinstid);
	    ps.setString(2, "PARENT LIMIT NODE CREATION");
	    try (ResultSet rs = ps.executeQuery()) {
		if (rs.next() && "SUCCESS".equalsIgnoreCase(rs.getString("FI_STATUS"))) {
		    flag = true;
		}
	    }
	} catch (Exception e) {
	    logger.info(OperationUtillity.traceException(e, pinstid));
	}
	return flag;
    }

    public String getDishubId(String pinstid) {
	String dishubId = "";
	try (Connection con = DBConnect.getConnection();
		PreparedStatement ps = con.prepareStatement(Queries.GET_DISHUBID)) {
	    ps.setString(1, pinstid);
	    try (ResultSet rs = ps.executeQuery()) {
		if (rs.next()) {
		    dishubId = Optional.ofNullable(rs.getString("DISHUBID"))
			    .orElse("DISHUB ID NOT AVAILABLE FOR PINSTID :: " + pinstid);
		}
	    }
	} catch (Exception e) {
	    logger.info(OperationUtillity.traceException(e, pinstid));
	}
	return dishubId;
    }
}
