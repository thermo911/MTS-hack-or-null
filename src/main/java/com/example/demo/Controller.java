package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@org.springframework.stereotype.Controller
@RequestMapping("/home")
public class Controller {

    private final RequestService requestService;
    private final FileService fileService;

    public static final Logger logger = LoggerFactory.getLogger("Controller");

    @Autowired
    public Controller(RequestService service, FileService fileService) {
        this.requestService = service;
        this.fileService = fileService;
    }

    @GetMapping
    public String homePage() {
        return "home";
    }

    @GetMapping("/check/{url}")
    @ResponseBody
    public ResponseEntity<String> checkOne(@PathVariable("url") String url) {
        logger.info("URL received: {}", url);

        String result = requestService.getResultOne(url);
        if (result != null) {
            logger.info("Service result for {} : {}", url, result);
        } else {
            logger.error("Can't get result for {}", url);
        }

        return ResponseEntity.ok().body(result);
    }

    @PostMapping("/check_csv")
    public String checkAll(@RequestParam("file") MultipartFile file) {
        logger.info("File name {}, file content type {}, file size {}",
                file.getOriginalFilename(), file.getContentType(), file.getSize());
        String filename;
        try {
            filename = fileService.saveFile(file.getInputStream());
        } catch (IOException e) {
            logger.error("Error caught while saving {}", file.getOriginalFilename());
            return "error";
        }

        logger.info("File saved: {}", filename);
        filename = requestService.getResultAll(filename);

        return String.format("redirect:/home/file/%s", filename);
    }

    @GetMapping("/file/{filename}")
    @ResponseBody
    public ResponseEntity<byte[]> fileWithResults(@PathVariable("filename") String filename)
            throws Exception {
        String contentType = "application/vnd.ms-excel";
        byte[] data = fileService.getFile(filename).orElseThrow(Exception::new);
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(data);
    }

//    @GetMapping("/test")
//    @ResponseBody
//    public ResponseEntity<String> test() {
//        System.out.println("777777777777777777777");
//        return ResponseEntity.ok().body("test");
//    }

}
