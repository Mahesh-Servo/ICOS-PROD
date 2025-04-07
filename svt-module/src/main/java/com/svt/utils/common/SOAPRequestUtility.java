package com.svt.utils.common;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class SOAPRequestUtility {

	private static final Logger logger = LoggerFactory.getLogger(SOAPRequestUtility.class);

	public static String soapResponse(String soapRequestMessage) throws IOException, SOAPException {

		InputStream is = new ByteArrayInputStream(soapRequestMessage.getBytes());
		SOAPMessage requestSoap = MessageFactory.newInstance().createMessage(null, is);
		MimeHeaders headers = requestSoap.getMimeHeaders();
		requestSoap.saveChanges();

		SOAPMessage soapRequest = requestSoap;

//		SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
//		SOAPConnection soapConnection = soapConnectionFactory.createConnection();
		SOAPConnection connection = SOAPConnectionFactory.newInstance().createConnection();
		URL endpoint = new URL(new URL("https://"), Constants.FIWebService, new URLStreamHandler() {
			@Override
			protected URLConnection openConnection(URL url) throws IOException {
				URL target = new URL(url.toString());
				HttpURLConnection connection = (HttpURLConnection) target.openConnection();
				// Connection settings
				connection.setConnectTimeout(75000); //  changed as before it was 15 sec
				connection.setReadTimeout(75000); // changed as before it was 15 secs
				return (connection);
			}
		});
//		logger.info("[SOAPRequestUtility].[soapResponse()] :: " + String.valueOf(endpoint));
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		long startSoapResponse = System.currentTimeMillis();

		SOAPMessage soapResponse = connection.call(soapRequest, endpoint);

		long endSoapResponse = System.currentTimeMillis();
		stopWatch.stop();

		logger.info(
				"\n[SOAPRequestUtility].[soapResponse()].[Elapsed Time in Seconds from Stopwatch for soapResponse ] :: "
						+ stopWatch.getTime() / 1000);
		logger.info(
				"\n[SOAPRequestUtility].[soapResponse()].[Elapsed Time in Seconds  from System Time for soapResponse ] :: "
						+ String.valueOf((endSoapResponse - startSoapResponse) / 1000));

		String soapResponseString = OperationUtillity.soapMessageToString(soapResponse);

		soapResponseString = StringEscapeUtils.unescapeXml(soapResponseString);
		
		logger.info("SOAPRequestUtility].[soapResponse()].soapResponseString ["+soapResponseString+"]");
		return soapResponseString;
	}

}
