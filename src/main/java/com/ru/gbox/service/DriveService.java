package com.ru.gbox.service;

import com.ru.gbox.models.Content;
import com.ru.gbox.models.UploadRes;
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

    public UploadRes createFolder(String reqDir, String folderName, String email) {
        String currentDir = getCurrentDir(reqDir, email);
        File newFolder = new File(currentDir + "/" + folderName);
        UploadRes uploadRes = new UploadRes();
        uploadRes.setFileName(folderName);
        if (newFolder.exists()) {
            uploadRes.setStatus(0);
        } else {
            newFolder.mkdir();
            uploadRes.setStatus(1);
        }
        return uploadRes;
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

    public UploadRes uploadFile(String reqDir, MultipartFile multipartFile, String email) {
        String currentDir = getCurrentDir(reqDir, email);
        String filePath = currentDir + "/"  + multipartFile.getOriginalFilename();
        File file = new File(filePath);
        if (file.exists()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File Already exists");
        }

        UploadRes ur = new UploadRes();
        ur.setFileName(multipartFile.getOriginalFilename());
        try {
            multipartFile.transferTo(Path.of(filePath));
            ur.setStatus(1);
        } catch (IOException e) {
            ur.setStatus(1);
            e.printStackTrace();
        }
        return ur;
    }

    @SneakyThrows
    public List<UploadRes> uploadMultipleFiles(String reqDir, List<MultipartFile> multipartFiles, String email) {
        String currentDir = getCurrentDir(reqDir, email);
        List<UploadRes> uploadResList = new ArrayList<>();

        multipartFiles.forEach( mf -> {
            String filePath = currentDir + "/"  + mf.getOriginalFilename();
            File file = new File(filePath);
            UploadRes ur = new UploadRes();
            ur.setFileName(mf.getOriginalFilename());

            if (file.exists()) {
                ur.setStatus(0);
            } else {
                try {
                    mf.transferTo(Path.of(filePath));
                    ur.setStatus(1);
                } catch (IOException e) {
                    ur.setStatus(-1);
                }
            }

            uploadResList.add(ur);
        });

        return uploadResList;
    }

    @SneakyThrows
    public Resource getFileAsResurce(String reqDir, String fileName, String email) {
        String currentDir = getCurrentDir(reqDir, email);
        String filePath = currentDir + "/"  + fileName;
        ByteArrayResource fileResource = new ByteArrayResource(Files.readAllBytes(Path.of(filePath)));

        return fileResource;
    }
}
