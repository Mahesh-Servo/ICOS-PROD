package com.LsmFiServices.pojo.serviceexecutiondetails;

public class ServiceDetailsToUI {

	private String requestType;
	private String status;
	private String message;
	private String dateTime;
	
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
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getDateTime() {
		return dateTime;
	}
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	@Override
	public String toString() {
		return "ServiceDetailsToUI [requestType=" + requestType + ", status=" + status + ", message=" + message
				+ ", dateTime=" + dateTime + "]";
	}
}
