package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

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


        if (url.startsWith("http://"))
            url = url.substring(7);
        if (url.startsWith("https://"))
            url = url.substring(8);
        if (!isValidUrl(url)) {
            return ResponseEntity.badRequest().body("incorrect data");
        }
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
    public String checkAll(@RequestParam("file") MultipartFile file) throws Exception {
        logger.info("File name {}, file content type {}, file size {}",
                file.getOriginalFilename(), file.getContentType(), file.getSize());
        String filename;
        try {
            filename = fileService.saveFile(file.getInputStream());
        } catch (IOException e) {
            logger.error("Error caught while saving {}", file.getOriginalFilename());
            throw new Exception(e);
        }

        logger.info("File saved: {}", filename);
        filename = requestService.getResultAll(filename);

        if (filename.equals("error")) {
            logger.error("Service error!");
            throw new Exception();
        }

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

    @ExceptionHandler
    public ModelAndView exceptionHandle(Exception e) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return modelAndView;
    }

    private boolean isValidUrl(String url) {
        if (url.length() < 4)
            return false;
        boolean hasDots = false;
        for (char c : url.toCharArray()) {
            if (c == '.') hasDots = true;
            if (c == '/') return false;
        }
        return hasDots;
    }

}
