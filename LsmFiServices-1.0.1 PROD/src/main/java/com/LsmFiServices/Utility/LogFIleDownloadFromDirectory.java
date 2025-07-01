package com.LsmFiServices.Utility;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogFIleDownloadFromDirectory {
	private static final Logger logger = LoggerFactory.getLogger(LogFIleDownloadFromDirectory.class);
//	
//    @GetMapping("/folders")
//    public List<String> getFolders() {
//    	logger.info("Enetred into LogFIleDownloadFromDirectory.getFolders()-->");
//        // Change this path to the directory where your sub-folders are located
////        String directoryPath = "path_to_your_directory";
//        String directoryPath = "/caoduatapp/Oracle/Middleware/user_projects/domains/CAOD/SRVLogs/";
//        logger.info("Enetred into LogFIleDownloadFromDirectory.getFolders().path -->"+directoryPath);
//        try {
//            return Files.list(Paths.get(directoryPath))
//                        .filter(Files::isDirectory)
//                        .map(path -> path.getFileName().toString())
//                        .collect(Collectors.toList());
//        } catch (IOException e) {
//            e.printStackTrace();
//            logger.info("FileController.getFolders() :: \n"+OperationUtillity.traceException(e));
//            return null;
//        }
//    }
//
//    @GetMapping("/files/{folderName}")
//    public List<String> getFiles(@PathVariable String folderName) {
//        // Change this path to the directory where your files are located
////        String directoryPath = "path_to_your_directory/" + folderName;
//        String directoryPath = "/caoduatapp/Oracle/Middleware/user_projects/domains/CAOD/SRVLogs/"+ folderName;
//        
//        try {
//            return Files.list(Paths.get(directoryPath))
//                        .filter(Files::isRegularFile)
//                        .map(path -> path.getFileName().toString())
//                        .collect(Collectors.toList());
//        } catch (IOException e) {
//            e.printStackTrace();
//            logger.info("FileController.getFiles() :: \n"+OperationUtillity.traceException(e));
//            return null;
//        }
//    }
//
//    @GetMapping("/download/{folderName}/{fileName}")
//    public StreamingResponseBody downloadFile(@PathVariable String folderName, @PathVariable String fileName) throws IOException {
//        // Change this path to the directory where your files are located
//        String filePath = "/caoduatapp/Oracle/Middleware/user_projects/domains/CAOD/SRVLogs/" + folderName + "/" + fileName;
//        
//        File file = new File(filePath);
//        InputStream inputStream = new FileInputStream(file);
//        
//        return outputStream -> {
//            int nRead;
//            byte[] data = new byte[1024];
//            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
//                outputStream.write(data, 0, nRead);
//            }
//            inputStream.close();
//        };
//    }
//    
//    
		
	    private final String BASE_FOLDER_PATH = "/caoduatapp/Oracle/Middleware/user_projects/domains/CAOD/SRVLogs/";

	    @GetMapping("/folders")
	    public List<String> getFolders() {
	        try {
	        	System.out.println("Bharat Test--->");
				File folder = new File(BASE_FOLDER_PATH);
				return Arrays.asList(folder.list((file, name) -> new File(file, name).isDirectory()));
			} catch (Exception e) {
				e.printStackTrace();
				 logger.info("FileController.getFolders() :: \n"+OperationUtillity.traceException(e));
				return null;
			}
	    }

	    @GetMapping("/files/{folderName}")
	    public List<String> getFilesInFolder(@PathVariable String folderName) {
	        try {
				File folder = new File(BASE_FOLDER_PATH + folderName);
				return Arrays.asList(folder.list((file, name) -> new File(file, name).isFile()));
			} catch (Exception e) {
				e.printStackTrace();
				 logger.info("FileController.getFilesInFolder() :: \n"+OperationUtillity.traceException(e));
				return null;
			}
	    }
}
