package com.LsmFiServices.Utility;

public class FailedServicesDetails {

	private String pinstid;
	private String requestType;
	private String status;
	private String serviceName;
	private String facility;

	public FailedServicesDetails() {
		super();
	}

	public String getPinstid() {
		return pinstid;
	}

	public void setPinstid(String pinstid) {
		this.pinstid = pinstid;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getFacility() {
		return facility;
	}

	public void setFacility(String facility) {
		this.facility = facility;
	}

	@Override
	public String toString() {
		return "FailedServicesDetails [pinstid=" + pinstid + ", requestType=" + requestType + ", status=" + status
				+ ", serviceName=" + serviceName + ", facility=" + facility + "]";
	}

}
