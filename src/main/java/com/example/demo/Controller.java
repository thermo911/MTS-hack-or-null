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

    @RequestMapping("/check")
    public String checkOne(@RequestParam(value = "url") String url, Model model) {
        logger.info(url);

        String result = requestService.getResultOne(url);
        if (result != null) {
            logger.info("Service result for {} : {}", url, result);
        } else {
            logger.error("FUCK");
        }
        model.addAttribute("url", url);
        model.addAttribute("result", result);
        return "result_one";
    }

    @PostMapping("/check_all")
    public String checkAll(@RequestParam("file") MultipartFile file, Model model) throws IOException {
        logger.info("File name {}, file content type {}, file size {}",
                file.getOriginalFilename(), file.getContentType(), file.getSize());
        String filename = fileService.saveFile(file.getInputStream());
        logger.info("File saved: {}", filename);

        // fileService.parseFile(filename);
        filename = requestService.getResultAll(filename);

        model.addAttribute("original_filename", file.getOriginalFilename());
        model.addAttribute("filename", filename);
        return "result_all";
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

}
