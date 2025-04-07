package com.svt.utils.common;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import javax.xml.soap.SOAPMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import com.svt.utils.dataConnectivity.dbConnection;

@Repository
public class commonUtility {

	private static final Logger logger = LoggerFactory.getLogger(commonUtility.class);

	public static String getFIServiceLink() {
		String serviceLink = "";
		try (Connection con = dbConnection.getConnection();
				PreparedStatement statement = con.prepareStatement(
						"SELECT CONFIGURATION_VALUE FROM ICOS_CONFIGURATION WHERE CONFIGURATION_PARAM= ? ");) {
//			statement.setString(1, "FI_WebService_31_07_2024");
			statement.setString(1, "FI_MW_APEEGEE");
			try (ResultSet rs = statement.executeQuery();) {
				if (rs.next()) {
					serviceLink = rs.getString("CONFIGURATION_VALUE");
				}
			}
		} catch (Exception ex) {
			logger.info("commonUtility.getFIServiceLink().Exception->" + OperationUtillity.traceException(ex));
		}
		return serviceLink;
	}

	public static String changedDateFormat(String inputDate) {
		String outputDate = "";
		Date date = null;
		try {
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
			date = inputFormat.parse(inputDate);
			SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
			outputDate = outputFormat.format(date);
		} catch (ParseException e) {
			OperationUtillity.traceException(e);
		}
		return outputDate;
	}

	public static String removeSpecialCharacters(String input) {
		if (input != null) {
			input = input.replaceAll("[^a-zA-Z0-9 ]", " ");
		}
		return input;
	}

	public static String soapMessageToString(SOAPMessage message) {

		String result = null;

		if (message != null) {
			ByteArrayOutputStream baos = null;
			try {
				baos = new ByteArrayOutputStream();
				message.writeTo(baos);
				result = baos.toString();
			} catch (Exception e) {
				logger.info("commonUtility.soapMessageToString()->" + OperationUtillity.traceException(e));
			} finally {
				if (baos != null) {
					try {
						baos.close();
					} catch (Exception e) {
						logger.info("commonUtility.soapMessageToString().Exception\n"
								+ OperationUtillity.traceException(e));
					}
				}
			}
		}
		return result;

	}

	public static String dateFormat() {
		Calendar now = Calendar.getInstance();

		int date = now.get(Calendar.DATE);
		int month = now.get(Calendar.MONTH) + 1;
		int year = now.get(Calendar.YEAR);

		String formatyyyymmdd = String.format("%d-%02d-%02d", year, month, date);
		//String formatddmmyyyy = String.format("%d-%02d-%02d", date, month, year);

//		String newtrannum = formatyyyymmdd + "" + now.get(Calendar.HOUR_OF_DAY) + "" + now.get(Calendar.MINUTE) + ""
//				+ now.get(Calendar.SECOND) + "" + now.get(Calendar.MILLISECOND);

		String sysdateandtime = formatyyyymmdd + "T" + now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE)
				+ ":" + now.get(Calendar.SECOND) + "." + now.get(Calendar.MILLISECOND);

		return sysdateandtime;
	}

	public static String dateFormatdate() {
		Calendar now = Calendar.getInstance();

		int date = now.get(Calendar.DATE);
		int month = now.get(Calendar.MONTH) + 1;
		int year = now.get(Calendar.YEAR);

		String formatyyyymmdd = String.format("%d-%02d-%02d", year, month, date);
	//	String formatddmmyyyy = String.format("%d-%02d-%02d", date, month, year);

//		String newtrannum = formatyyyymmdd + "" + now.get(Calendar.HOUR_OF_DAY) + "" + now.get(Calendar.MINUTE) + ""
//				+ now.get(Calendar.SECOND) + "" + now.get(Calendar.MILLISECOND);

		String sysdate = formatyyyymmdd;

		return sysdate;
	}

	public static String createRequestUUID() {

		Calendar now = Calendar.getInstance();

		int date = now.get(Calendar.DATE);
		int month = now.get(Calendar.MONTH) + 1;
		int year = now.get(Calendar.YEAR);

		//String formatyyyymmdd = String.format("%d-%02d-%02d", year, month, date);
		String formatddmmyyyy = String.format("%d%02d%02d", date, month, year);

		String requestId = "CLS_" + formatddmmyyyy + "" + now.get(Calendar.HOUR_OF_DAY) + "" + now.get(Calendar.MINUTE)
				+ "" + now.get(Calendar.SECOND) + "" + now.get(Calendar.MILLISECOND);

		return requestId;
	}

	public static String sysTimeWithT() {
		Calendar now = Calendar.getInstance();

//		String currentTime = now.get(Calendar.HOUR_OF_DAY) + "" + now.get(Calendar.MINUTE) + ""
//				+ now.get(Calendar.SECOND) + "" + now.get(Calendar.MILLISECOND);

		String SysTime = "T" + now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE) + ":"
				+ now.get(Calendar.SECOND) + "." + now.get(Calendar.MILLISECOND);

		return SysTime;
	}

	public static String millionString(String inputValue) {
		Double d = 0d;

		if (inputValue != "" && inputValue != null) {
			d = Double.parseDouble(inputValue);
		}

		Double value = d * 1000000;
		DecimalFormat df = new DecimalFormat("0.###");
		String a = df.format(value);

		return a;
	}

	public static String rotateDate(String value) {

		String formatedVal = "";
		if (value != null) {
			if (!value.equals("") && !value.equals("null")) {
				if (value.contains("-")) {
					String[] obj = value.split("-");
					formatedVal = obj[2] + "-" + obj[1] + "-" + obj[0];
				}
			}
		}
		return formatedVal;
	}

	// dd-MM-yyyy
	public static String customDateFormat() {
		String customFormatedDate = "";
		LocalDate date = LocalDate.now();
		DateTimeFormatter formater = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		customFormatedDate = date.format(formater);
		return customFormatedDate; // dd-MM-yyyy

	}

	// for manipulating DB date to custom format
	public static String dateBySlashSeperated(String datefromDB) {
		String slashSaperatedDate = "";
		if (datefromDB != null && datefromDB != "") {
			String[] rawDate = datefromDB.split("-");
			slashSaperatedDate = rawDate[2] + "/" + rawDate[1] + "/" + rawDate[0];
			System.out.println(slashSaperatedDate);
		}
		return slashSaperatedDate;
	}
	
	public static String customDateInDDMMYYYY(String input) {
		if (input == null || input.isEmpty()) {
			return "";
		}
		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		LocalDate date = LocalDate.parse(input, inputFormatter);
		return date.format(outputFormatter);

	}
	
	public static String getResponseFromMW(String encryptedData,String endPointUrl) throws Exception {
		logger.info("commonUtility.getResponseFromMW() --> IN requestpacket ["+ encryptedData+"]");
		//String endpointURL = "https://mwuat.icicibankltd.com/v1/api/FI-INT";
		StringBuilder response = new StringBuilder();
		
		URL url = new URL(endPointUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setConnectTimeout(30000);
		conn.setRequestProperty("Content-Type","application/xml");
		conn.setUseCaches(false);
		conn.setDoOutput(true);
		
		try(OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream())) {
			logger.info("commonUtility.sendEncryptedRequest() --> before Write ");
			wr.write(encryptedData);
		}
		logger.info("commonUtility.sendEncryptedRequest() --> before RespCode ");
		int RespCode = conn.getResponseCode();
		logger.info("commonUtility.sendEncryptedRequest() --> After RespCode "+RespCode);
		
		if(RespCode == HttpURLConnection.HTTP_OK) {
			try(BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
				String inputLine ;
				
				while((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				logger.info("commonUtility.sendEncryptedRequest() --> Final response "+response);	
			}
		} else {
			try(BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
				String inputLine ;
				
				while((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				logger.info("commonUtility.sendEncryptedRequest() --> ELSE Final response "+response);	
			}
		}
		return response.toString();
	}
	 
}
