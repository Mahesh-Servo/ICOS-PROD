package com.LsmFiServices.pojo.LeiUrccVerification;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "XML")
	@XmlAccessorType(XmlAccessType.FIELD)
	public class LeiUrccVerificationResponse {
  
	  @XmlElement(name = "HostTransaction")
	    private HostTransaction hostTransaction;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	class ResponseHeader {

	    @XmlElement(name = "RequestMessageKey")
	    private RequestMessageKey requestMessageKey;

	    @XmlElement(name = "ResponseMessageInfo")
	    private ResponseMessageInfo responseMessageInfo;

	    @XmlElement(name = "UBUSTransaction")
	    private UBUSTransaction ubusTransaction;

	    @XmlElement(name = "HostTransaction")
	    private HostTransaction hostTransaction;

		public RequestMessageKey getRequestMessageKey() {
			return requestMessageKey;
		}

		public ResponseMessageInfo getResponseMessageInfo() {
			return responseMessageInfo;
		}

		public UBUSTransaction getUbusTransaction() {
			return ubusTransaction;
		}

		public HostTransaction getHostTransaction() {
			return hostTransaction;
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	class RequestMessageKey {

	    @XmlElement(name = "GlobalUUID")
	    private String globalUUID;

	    @XmlElement(name = "RequestUUID")
	    private String requestUUID;

	    @XmlElement(name = "ServiceRequestId")
	    private String serviceRequestId;

	    @XmlElement(name = "ServiceId")
	    private String serviceId;

	    @XmlElement(name = "ServiceRequestVersion")
	    private String serviceRequestVersion;

	    @XmlElement(name = "ServiceVersion")
	    private String serviceVersion;

	    @XmlElement(name = "ChannelId")
	    private String channelId;

	    @XmlElement(name = "OriginatorId")
	    private String originatorId;

	    @XmlElement(name = "OriginatorInstanceId")
	    private String originatorInstanceId;

	    @XmlElement(name = "OriginatorVersion")
	    private String originatorVersion;

	    @XmlElement(name = "LanguageId")
	    private String languageId;

		public String getGlobalUUID() {
			return globalUUID;
		}

		public String getRequestUUID() {
			return requestUUID;
		}

		public String getServiceRequestId() {
			return serviceRequestId;
		}

		public String getServiceId() {
			return serviceId;
		}

		public String getServiceRequestVersion() {
			return serviceRequestVersion;
		}

		public String getServiceVersion() {
			return serviceVersion;
		}

		public String getChannelId() {
			return channelId;
		}

		public String getOriginatorId() {
			return originatorId;
		}

		public String getOriginatorInstanceId() {
			return originatorInstanceId;
		}

		public String getOriginatorVersion() {
			return originatorVersion;
		}

		public String getLanguageId() {
			return languageId;
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	class ResponseMessageInfo {

	    @XmlElement(name = "BankId")
	    private String bankId;

	    @XmlElement(name = "TimeZone")
	    private String timeZone;

	    @XmlElement(name = "MessageDateTime")
	    private String messageDateTime;

		public String getBankId() {
			return bankId;
		}

		public String getTimeZone() {
			return timeZone;
		}

		public String getMessageDateTime() {
			return messageDateTime;
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	class UBUSTransaction {

	    @XmlElement(name = "Id")
	    private String id;

	    @XmlElement(name = "Status")
	    private String status;

		public String getId() {
			return id;
		}

		public String getStatus() {
			return status;
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	class HostTransaction {

	    @XmlElement(name = "Id")
	    private String id;

	    @XmlElement(name = "Status")
	    private String status;

		public String getId() {
			return id;
		}

		public String getStatus() {
			return status;
		}
	    
	}


	@XmlAccessorType(XmlAccessType.FIELD)
	class ErrorDetail {

	    @XmlElement(name = "ErrorCode")
	    private String errorCode;

	    @XmlElement(name = "ErrorDesc")
	    private String errorDesc;

	    @XmlElement(name = "ErrorSource")
	    private String errorSource;

	    @XmlElement(name = "ErrorType")
	    private String errorType;

		public String getErrorCode() {
			return errorCode;
		}

		public String getErrorDesc() {
			return errorDesc;
		}

		public String getErrorSource() {
			return errorSource;
		}

		public String getErrorType() {
			return errorType;
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	class FIBusinessException {

	    @XmlElement(name = "ErrorDetail")
	    private ErrorDetail errorDetail;

		public ErrorDetail getErrorDetail() {
			return errorDetail;
		}
	}
