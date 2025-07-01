package com.LsmFiServices.pojo.serviceexecutiondetails;

import java.util.List;

public class ResponseWrapper {

    private List<ServiceExecutionDetails> listPojo;
    private String message;

    public ResponseWrapper() {
	super();
    }

    public ResponseWrapper(List<ServiceExecutionDetails> listPojo, String message) {
	super();
	this.listPojo = listPojo;
	this.message = message;
    }

    public List<ServiceExecutionDetails> getListPojo() {
	return listPojo;
    }

    public void setListPojo(List<ServiceExecutionDetails> listPojo) {
	this.listPojo = listPojo;
    }

    public String getMessage() {
	return message;
    }

    public void setMessage(String message) {
	this.message = message;
    }

    @Override
    public String toString() {
	return "ResponseWrapper [listPojo=" + listPojo + ", message=" + message + "]";
    }
}
