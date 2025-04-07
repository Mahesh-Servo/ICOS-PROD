package com.svt.utils.fileShare;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.svt.utils.common.OperationUtillity;

@RestController
@RequestMapping("svt")
public class DownloadFile {

	private static final Logger logger = LoggerFactory.getLogger(DownloadFile.class);

	// for UAT
	private static String CAOD_PATH = "/caoduatapp/Oracle/Middleware/user_projects/domains/CAOD";

	// DNS Url :
	// https://caoduat.icicibank.com/svt-module/svt/downloadFile?fileName=LsmFiServices-1.0.1&filePat=Vivek
	// UAT 06.12.2024.war
	@GetMapping("/downloadFile")
	public ResponseEntity<Resource> downloadFile(@RequestParam("filePath") String filePath,
			@RequestParam(value = "fileName") String fileName) {

		String directoryPath = filePath != null && !filePath.isEmpty() ? CAOD_PATH + "/" + filePath : CAOD_PATH;
		Path path = Paths.get(directoryPath + File.separator + fileName);
		Resource resource = null;

		try {
			resource = new UrlResource(path.toUri());
		} catch (MalformedURLException e) {
			logger.info(OperationUtillity.traceException(fileName, e));
			return new ResponseEntity("An error occurred: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream"))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}
}
