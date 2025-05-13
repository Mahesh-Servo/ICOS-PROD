package com.example.masIntg.utility;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@SuppressWarnings("deprecation")
@Controller
public class downloadLogFile {

	private String caodpath = System.getProperty("user.dir");

	private static final Logger logger = LoggerFactory.getLogger(downloadLogFile.class);

	@GetMapping("/download/{fileName:.+}")
	public ResponseEntity downloadLogFile(@PathVariable String fileName) {

		System.out.println("inside log file download");
		logger.info("inside log file download");

		String basepath = caodpath + "SRVLogs/API_LOGS/";

		Path path = Paths.get(basepath + fileName);

		System.out.println("file path of logger --> " + path);
		logger.info("file path of logger --> " + path);

		Resource resource = null;
		try {
			resource = new UrlResource(path.toUri());

			System.out.println("filename =" + resource.getFilename());
			logger.info("filename =" + resource.getFilename());

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream"))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}
}
