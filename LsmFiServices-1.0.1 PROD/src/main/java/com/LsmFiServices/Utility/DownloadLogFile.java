package com.LsmFiServices.Utility;

import java.io.File;
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
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DownloadLogFile {

    private static final Logger logger = LoggerFactory.getLogger(DownloadLogFile.class);

    // for UAT
//	private static String caodPath = "/caoduatapp/Oracle/Middleware/user_projects/domains/CAOD/";

    // for Production
    private static String caodPath = "/corp_wrkflw1/weblogic/wlsdomain/wrkflw_domain/";
//	private String caodpath = System.getProperty("user.dir") + File.separator;

    @GetMapping("/downloadFile")
    public ResponseEntity downloadFile(@RequestParam(value = "filepath") String filepath,
	    @RequestParam(value = "fileName") String fileName) {

//		filepath = "SRVLogs/API_LOGS";
//		fileName = "LSM_API.log";

	String basepath = caodPath + File.separator + filepath;
	Path path = Paths.get(caodPath + File.separator + fileName);

	Resource resource = null;
	try {
	    resource = new UrlResource(path.toUri());
	} catch (MalformedURLException e) {
	    System.out.println("downloadLogFile.downloadFile()");
	    e.printStackTrace();
	}
	return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream"))
		.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
		.body(resource);
    }

}
