package com.svt.model.commonModel;

public class ServiceExecutionDetails {

	private String pinstId;
	private String serviceName;
	private String requestType;
	private String facility;
	private String accountNumber;
	private String request; 
	private String response;
	private String status;
	private String message;
	private String requestuuid;
	public ServiceExecutionDetails(String pinstId, String serviceName, String requestType, String facility,
			String accountNumber, String request, String response, String status, String message, String requestuuid) {
		super();
		this.pinstId = pinstId;
		this.serviceName = serviceName;
		this.requestType = requestType;
		this.facility = facility;
		this.accountNumber = accountNumber;
		this.request = request;
		this.response = response;
		this.status = status;
		this.message = message;
		this.requestuuid = requestuuid;
	}
	public String getPinstId() {
		return pinstId;
	}
	public void setPinstId(String pinstId) {
		this.pinstId = pinstId;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getRequestType() {
		return requestType;
	}
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
	public String getFacility() {
		return facility;
	}
	public void setFacility(String facility) {
		this.facility = facility;
	}
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public String getRequest() {
		return request;
	}
	public void setRequest(String request) {
		this.request = request;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getRequestuuid() {
		return requestuuid;
	}
	public void setRequestuuid(String requestuuid) {
		this.requestuuid = requestuuid;
	}
	@Override
	public String toString() {
		return "ServiceExecutionDetails [pinstId=" + pinstId + ", serviceName=" + serviceName + ", requestType="
				+ requestType + ", facility=" + facility + ", accountNumber=" + accountNumber + ", request=" + request
				+ ", response=" + response + ", status=" + status + ", message=" + message + ", requestuuid="
				+ requestuuid + "]";
	}
	
}

