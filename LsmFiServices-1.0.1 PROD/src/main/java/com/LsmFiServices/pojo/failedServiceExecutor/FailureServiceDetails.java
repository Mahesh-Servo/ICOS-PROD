package com.LsmFiServices.pojo.failedServiceExecutor;

public class FailureServiceDetails {

    private String requestType;
    private String facility;
    private String serviceName;

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

    @Override
    public String toString() {
	return "FailureServiceDetails [requestType=" + requestType + ", facility=" + facility + ", serviceName="
		+ serviceName + "]";
    }

}
