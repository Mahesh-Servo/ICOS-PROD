package com.svt.model.commonModel.serviceDetails;

public class ServiceDetails {

	private String pinstId;
	private String requestType;
	private String serviceName;
	private String facility;
	private String status;
	private String serviceRequest;
	private String serviceResponse;
	private String message;
//	private String accountNumber;
	private boolean reTrigger;

	public ServiceDetails(String pinstId, String requestType, String serviceName, String facility, String status,
			String serviceRequest, String serviceResponse, String message, boolean reTrigger) {
		super();
		this.pinstId = pinstId;
		this.requestType = requestType;
		this.serviceName = serviceName;
		this.facility = facility;
		this.status = status;
		this.serviceRequest = serviceRequest;
		this.serviceResponse = serviceResponse;
		this.message = message;
//		this.accountNumber = accountNumber;
		this.reTrigger = reTrigger;
	}

	public ServiceDetails() {
		super();
	}

	public String getPinstId() {
		return pinstId;
	}

	public void setPinstId(String pinstId) {
		this.pinstId = pinstId;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getServiceRequest() {
		return serviceRequest;
	}

	public void setServiceRequest(String serviceRequest) {
		this.serviceRequest = serviceRequest;
	}

	public String getServiceResponse() {
		return serviceResponse;
	}

	public void setServiceResponse(String serviceResponse) {
		this.serviceResponse = serviceResponse;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

//	public String getAccountNumber() {
//		return accountNumber;
//	}
//
//	public void setAccountNumber(String accountNumber) {
//		this.accountNumber = accountNumber;
//	}

	public boolean isReTrigger() {
		return reTrigger;
	}

	public void setReTrigger(boolean reTrigger) {
		this.reTrigger = reTrigger;
	}

	@Override
	public String toString() {
		return "ServiceDetails [pinstId=" + pinstId + ", requestType=" + requestType + ", serviceName=" + serviceName
				+ ", facility=" + facility + ", status=" + status + ", serviceRequest=" + serviceRequest
				+ ", serviceResponse=" + serviceResponse + ", message=" + message + ", reTrigger=" + reTrigger + "]";
	}

}
