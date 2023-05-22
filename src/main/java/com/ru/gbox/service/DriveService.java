package com.ru.gbox.service;

import com.ru.gbox.models.Content;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DriveService {

    @Value("${drive.base.dir}")
    private String driveBaseDir;

    private static Logger logger = LoggerFactory.getLogger(DriveService.class);

    public List<Content> listContents(String reqDir, String email) {
        if (!StringUtils.hasLength(reqDir)){
            reqDir = "";
        }

        String userRootDirName = findRootDirFromEmail(email);
        String currentDirPath = driveBaseDir+userRootDirName+"/"+reqDir;
        File currentDir = new File(currentDirPath);

        if (!currentDir.exists()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Directory");
        }

        return Stream.of(currentDir.listFiles())
                .map( f -> {
                    Content content = new Content();
                    content.setName(f.getName());
                    content.setType(getContentType(f.toPath()));
                    content.setDir(f.isDirectory());

                    try {
                        BasicFileAttributes basicFileAttributes = Files.readAttributes(f.toPath(), BasicFileAttributes.class);
                        content.setSize(basicFileAttributes.size());
                        content.setLastModifiedAt(new Date(basicFileAttributes.lastModifiedTime().toMillis()));
                    } catch (IOException e) {
                        logger.error("Unable to parse File Attributes");
                        e.printStackTrace();
                    }
                    return content;
                })
                .collect(Collectors.toList());
    }

    private String findRootDirFromEmail(String email) {
        String userHash = Base64.getEncoder().encode(email.getBytes(StandardCharsets.UTF_8)).toString().replaceAll("[^a-zA-Z0-9]", "");
        File userRootDir = new File(driveBaseDir+email);
        if (!userRootDir.exists()) {
            userRootDir.mkdirs();
            logger.debug("User Root Directory created ...");
        }
        return userRootDir.getName();
    }

    private String getContentType (Path path) {
        String type = "UNKNOWN";
        try {
            type = Files.probeContentType(path);
        } catch (IOException e) {
            logger.error("Unable to detect file type: " + e.getMessage());
            return type;
        }
        return type;
    }

    public String createFolder(String reqDir, String folderName, String email) {
        String currentDir = getCurrentDir(reqDir, email);
        File newFolder = new File(currentDir + "/" + folderName);
        if (newFolder.exists()) {
            return "Folder Already Created";
        }
        return newFolder.mkdir() ? "Folder Created Successfully" : "Folder Creation Unsuccessfull, Please retry again...";
    }

    public String getCurrentDir(String reqDir, String email) {
        if (!StringUtils.hasLength(reqDir)){
            reqDir = "";
        }

        String userRootDirName = findRootDirFromEmail(email);
        String currentDirPath = driveBaseDir+userRootDirName+"/"+reqDir;
        File currentDir = new File(currentDirPath);
        if (!currentDir.exists()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Directory");
        }
        return currentDirPath;
    }

    @SneakyThrows
    public String uploadFile(String reqDir, MultipartFile multipartFile, String email) {
        String currentDir = getCurrentDir(reqDir, email);
        String filePath = currentDir + "/"  + multipartFile.getOriginalFilename();
        File file = new File(filePath);
        if (file.exists()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File Already exists");
        }
        multipartFile.transferTo(Path.of(filePath));

        return file.exists() ? "Uploaded Successfully" : "Upload Failed Please try again later";
    }

    @SneakyThrows
    public String uploadMultipleFiles(String reqDir, List<MultipartFile> multipartFiles, String email) {
        String currentDir = getCurrentDir(reqDir, email);
        Map<String, Integer> uploadRes = new HashMap();

        multipartFiles.forEach( mf -> {
            String filePath = currentDir + "/"  + mf.getOriginalFilename();
            File file = new File(filePath);
            if (file.exists()) {
                uploadRes.put(mf.getOriginalFilename(), 0);
            }
            try {
                mf.transferTo(Path.of(filePath));
                uploadRes.put(mf.getOriginalFilename(), 1);
            } catch (IOException e) {
                uploadRes.put(mf.getOriginalFilename(), -1);
            }
        });

        return uploadRes.toString();
    }

    @SneakyThrows
    public Resource getFileAsResurce(String reqDir, String fileName, String email) {
        String currentDir = getCurrentDir(reqDir, email);
        String filePath = currentDir + "/"  + fileName;
        ByteArrayResource fileResource = new ByteArrayResource(Files.readAllBytes(Path.of(filePath)));

        return fileResource;
    }
}
