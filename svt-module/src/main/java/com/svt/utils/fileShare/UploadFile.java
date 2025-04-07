package com.svt.utils.fileShare;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.svt.utils.common.OperationUtillity;

@RestController
@RequestMapping("svt")
public class UploadFile {

	private static final Logger logger = LoggerFactory.getLogger(UploadFile.class);

	// for UAT
	private static String CAOD_PATH = "/caoduatapp/Oracle/Middleware/user_projects/domains/CAOD";

	// // DNS Upload Url :
	// https://caoduat.icicibank.com/svt-module/svt/uploadFile
//	file = file
// filePath=Vivek

	@PostMapping("/uploadFile")
	public ResponseEntity uploadFile(@RequestParam(value = "file") MultipartFile file,
			@RequestParam("filePath") String filePath) {

		String directoryPath = filePath != null && !filePath.isEmpty() ? CAOD_PATH + "/" + filePath : CAOD_PATH;
		String path = directoryPath + "/" + file.getOriginalFilename();

		String fileUploadStatus;
		long fileSize = file.getSize();

		long maxFileSize = 150 * 1024 * 1024; // 50 MB in bytes
		if (fileSize > maxFileSize) {
			fileUploadStatus = "File size exceeds the limit of 150 MB.";
			return new ResponseEntity<>(fileUploadStatus, HttpStatus.BAD_REQUEST);
		}

//		StopWatch stopWatch = new StopWatch();
//		stopWatch.start();

		File directory = new File(directoryPath);
		if (!directory.exists()) {
			boolean dirCreated = directory.mkdirs();
			if (!dirCreated) {
				return new ResponseEntity<>("Failed to create the directory.", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

		try (FileOutputStream fout = new FileOutputStream(path);) {

			fout.write(file.getBytes());

//			stopWatch.stop();

			fileUploadStatus = "File Uploaded Successfully." // in " + stopWatch.getTime() / 1000 + " secs.
					+ System.lineSeparator() + "URL to Download the same file :" + System.lineSeparator()
					+ "https://caoduat.icicibank.com/svt-module/svt/downloadFile?filePath=" + filePath + "&fileName="
					+ file.getOriginalFilename();

		} catch (Exception e) {
			e.printStackTrace();
			logger.info(OperationUtillity.traceException(file.getOriginalFilename(), e));
			fileUploadStatus = "Error in uploading file: " + e.getMessage();
			return new ResponseEntity<>(fileUploadStatus, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(fileUploadStatus, HttpStatus.OK);
	}

}
