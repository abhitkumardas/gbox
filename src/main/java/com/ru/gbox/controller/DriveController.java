package com.ru.gbox.controller;

import com.ru.gbox.service.DriveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/drive")
public class DriveController {

    @Autowired
    private DriveService driveService;

    @GetMapping("/list")
    public ResponseEntity listContents(@RequestParam String reqDir, Authentication authentication) {
        return ResponseEntity.ok(driveService.listContents(reqDir, authentication.getName()));
    }

    @PostMapping("/create-folder")
    public ResponseEntity createFolder(@RequestParam String reqDir, @RequestParam String folderName, Authentication authentication) {
        return ResponseEntity.ok(driveService.createFolder(reqDir, folderName, authentication.getName()));
    }

    @PostMapping("/upload-file")
    public ResponseEntity uploadFile(@RequestParam String reqDir, @RequestParam MultipartFile file, Authentication authentication) {
        return ResponseEntity.ok(driveService.uploadFile(reqDir, file, authentication.getName()));
    }

    @PostMapping("/upload-files")
    public ResponseEntity uploadFiles(@RequestParam String reqDir, @RequestParam List<MultipartFile> files, Authentication authentication) {
        return ResponseEntity.ok(driveService.uploadMultipleFiles(reqDir, files, authentication.getName()));
    }

    @GetMapping(value = "/resource-file", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody Resource getFileViaByteArrayResource(@RequestParam String reqDir, @RequestParam String fileName,
                                                              Authentication authentication) throws IOException, URISyntaxException {
        return driveService.getFileAsResurce(reqDir, fileName, authentication.getName());
    }
}
