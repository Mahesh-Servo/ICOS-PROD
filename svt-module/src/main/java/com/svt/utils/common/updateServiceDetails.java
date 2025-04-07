package com.svt.utils.common;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.svt.model.commonModel.serviceDetails.ServiceDetails;
import com.svt.utils.dataConnectivity.dbConnection;

@Repository
public class updateServiceDetails {

	private static final Logger logger = LoggerFactory.getLogger(updateServiceDetails.class);

	public void updateInitialStatusInFiExecutionTable(ServiceDetails pojo) {

//		logger.info("\nServiceDetails.updateReqAndInitStatusInFiExecutionTable()->pojo2->" + pojo);

		String query = "UPDATE LSM_FI_EXECUTION_DETAILS   SET"
				+ " STATUS =?, REQUEST=?,RESPONSE=?, MESSAGE =?, DATETIME = SYSDATE, RETRIGGER = ?"
				+ " WHERE PINSTID = ? AND  REQUEST_TYPE = ? AND SERVICE_NAME = ?";
		
		if (pojo.getPinstId().contains("MON")) {
			 query = "UPDATE MON_FI_EXECUTION_DETAILS   SET"
						+ " STATUS =?, REQUEST=?,RESPONSE=?, MESSAGE =?, DATETIME = SYSDATE, RETRIGGER = ?"
						+ " WHERE PINSTID = ? AND  REQUEST_TYPE = ? AND SERVICE_NAME = ?";
		}

		int afftectedRows = 0;

		try (Connection con = dbConnection.getConnection(); PreparedStatement ps = con.prepareStatement(query);) {

			ps.setString(1, pojo.getStatus());
			ps.setString(2, pojo.getServiceRequest());
			ps.setString(3, pojo.getServiceResponse());
			ps.setString(4, pojo.getMessage());
			ps.setString(5, String.valueOf(pojo.isReTrigger()));

			ps.setString(6, pojo.getPinstId());
			ps.setString(7, pojo.getRequestType());
			ps.setString(8, pojo.getServiceName());

			afftectedRows = ps.executeUpdate();
			if (afftectedRows > 0) {
				con.commit();
				if (pojo.getPinstId().contains("MON")) {
					CommonDataUtility.updateMonitoringExtFiStatus(pojo.getPinstId());
				}
				logger.info("\nStatus Updated Successfully!!! for pinstid(" + pojo.getPinstId() + ") : Service("
						+ pojo.getServiceName() + ")" + ": RequestType(" + pojo.getRequestType() + ")");
			} else {
				logger.info("\nStatus Not Updated !!! for pinstid(" + pojo.getPinstId() + ") : Service("
						+ pojo.getServiceName() + ") : RequestType(" + pojo.getRequestType() + ")");
			}
		} catch (Exception e) {
			logger.info("\nServiceDetails.updateInitialStatusInFiExecutionTable() {}",
					OperationUtillity.traceException(pojo.getPinstId(), e));
		}

	}

}
