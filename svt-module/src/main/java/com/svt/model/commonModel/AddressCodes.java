package com.svt.model.commonModel;

public class AddressCodes {

    private String cityCode;
    private String stateCode;

    public AddressCodes() {
    }

    public String getCityCode() {
	return cityCode;
    }

    public void setCityCode(String cityCode) {
	this.cityCode = cityCode;
    }

    public String getStateCode() {
	return stateCode;
    }

    public void setStateCode(String stateCode) {
	this.stateCode = stateCode;
    }

    @Override
    public String toString() {
	return "AddressCodes [cityCode=" + cityCode + ", stateCode=" + stateCode + "]";
    }
}

