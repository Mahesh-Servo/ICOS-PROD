package com.LsmFiServices.Utility;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import javax.xml.soap.SOAPMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class commonUtility {

	private static final Logger logger = LoggerFactory.getLogger(commonUtility.class);

	public static boolean isZero(String value) {
		if (value == null || value.isEmpty()) {
			return true;
		} else {
			double number = Double.parseDouble(value);
			return number == 0.0;
		}

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

		String newtrannum = formatyyyymmdd + "" + now.get(Calendar.HOUR_OF_DAY) + "" + now.get(Calendar.MINUTE) + ""
				+ now.get(Calendar.SECOND) + "" + now.get(Calendar.MILLISECOND);

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
		String formatddmmyyyy = String.format("%d-%02d-%02d", date, month, year);

		String newtrannum = formatyyyymmdd + "" + now.get(Calendar.HOUR_OF_DAY) + "" + now.get(Calendar.MINUTE) + ""
				+ now.get(Calendar.SECOND) + "" + now.get(Calendar.MILLISECOND);

		String sysdate = formatyyyymmdd;

		return sysdate;
	}

	public static String createRequestUUID() {

		String requestId;
		Calendar now = Calendar.getInstance();
		int date = now.get(Calendar.DATE);
		int month = now.get(Calendar.MONTH) + 1;
		int year = now.get(Calendar.YEAR);

		String formatyyyymmdd = String.format("%d-%02d-%02d", year, month, date);
		String formatddmmyyyy = String.format("%d%02d%02d", date, month, year);

		requestId = "CLS_" + formatddmmyyyy + "" + now.get(Calendar.HOUR_OF_DAY) + "" + now.get(Calendar.MINUTE) + ""
				+ now.get(Calendar.SECOND) + "" + now.get(Calendar.MILLISECOND);

		return requestId;
	}

	public static String sysTimeWithT() {
		LocalTime now = LocalTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'T'HH:mm:ss.SSS");
		return now.format(formatter);
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
	public static String customDateInDDMMYYYY(String input) {
		if (input == null || input.isEmpty()) {
			return "";
		}
		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		LocalDate date = LocalDate.parse(input, inputFormatter);
		return date.format(outputFormatter);

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

	public static String getFIServiceLink() {
		String serviceLink = "";
		try (Connection con = DBConnect.getConnection();
				PreparedStatement statement = con.prepareStatement(Queries.PROVIDER_URL_QUERY);) {
			statement.setString(1, "FI_SERVICE");
			try (ResultSet rs = statement.executeQuery();) {
				if (rs.next()) {
					serviceLink = OperationUtillity.NullReplace(rs.getString("CONFIGURATION_VALUE"));
				}
			}
		} catch (Exception ex) {
			logger.info("commonUtility.getFIServiceLink()\n" + OperationUtillity.traceException(ex));
		}
		return serviceLink;
	}

	public static String getCustid(String pinstid, Connection con) {
		String custId = null;

		String custIdQuery = ReadPropertyFIle.getInstance().getPropConst().getProperty("cifId");
		try (PreparedStatement statement = con.prepareStatement(custIdQuery);) {
			statement.setString(1, pinstid);
			try (ResultSet rs = statement.executeQuery();) {
			while (rs.next()) {
				custId = OperationUtillity.NullReplace(rs.getString("ANSWER"));
			}
		  }
		} catch (Exception ex) {
			logger.info("commonUtility.getCustid()\n" + OperationUtillity.traceException(ex));
		}
		return custId;
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
			logger.info("commonUtility.changedDateFormat()\n" + OperationUtillity.traceException(e));
		}
		return outputDate;
	}

	public static String removeSpecialCharacters(String input) {
		if (input != null) {
			input = input.replaceAll("[^a-zA-Z0-9& ]", " ");
		}
		return input;
	}

	public static String replaceAnd(String input) {
		if (input != null) {
			input = input.replace("&", "&amp;");
		}
		return input;
	}

	public static String getStatus(String response) {
		String hostTransaction = "";
		String status = "";
		try {
			if (response.contains("<HostTransaction>")) {
				hostTransaction = response.substring(
						response.indexOf("<HostTransaction>") + "<HostTransaction>".length(),
						response.indexOf("</HostTransaction>"));
				if (hostTransaction.contains("<Status>")) {
					status = hostTransaction.substring(hostTransaction.indexOf("<Status>") + "<Status>".length(),
							hostTransaction.indexOf("</Status>"));
				}
			}
		} catch (Exception e) {
			logger.error("commonUtility.getStatus() {}", OperationUtillity.traceException(e));
		}
		return status.trim();
	}
}
