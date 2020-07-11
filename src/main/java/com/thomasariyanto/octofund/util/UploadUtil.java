package com.thomasariyanto.octofund.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import net.bytebuddy.utility.RandomString;

@Component
public class UploadUtil {
	public String uploadFile(MultipartFile file, String folderPath) {
		String fileExtension = file.getContentType().split("/")[1];
		String newFileName = RandomString.make(20) + "." + fileExtension;

		String fileName = StringUtils.cleanPath(newFileName);
		
		Path path = Paths.get(StringUtils.cleanPath(folderPath) + fileName);
		
		try {
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return fileName;
	}
}
