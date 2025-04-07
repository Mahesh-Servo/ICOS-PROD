package com.svt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.svt.model.commonModel.InnerPojo;
import com.svt.model.commonModel.MainPojo;
import com.svt.utils.common.OperationUtillity;
import com.svt.utils.dataConnectivity.dbConnection;

@Repository
public class SvtUnlinkDao {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SvtUnlinkDao.class);

	public ArrayList<MainPojo> fetchunlinkCollateraldata(String pinstId, String processName) {
		System.out.println("SvtUnlinkDao.fetchunlinkCollateraldata().pinstid(" + pinstId + ")");
		return null;

	}

	public static List<InnerPojo> fectListOfProducts(String pinstId, MainPojo unlinkCollateralReqPj,
			String SubTypeSecurityData, Map<String, String[]> limitData, Connection con) {
		return null;
	}

	public String getCollateralCode(String temp) {
		return temp;

	}

}
