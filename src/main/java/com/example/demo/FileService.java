package com.example.demo;

import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

import static java.nio.file.StandardOpenOption.*;

@Service
public class FileService {

    private final String PATH = "data";

    public String saveFile(InputStream is) {
        String filename = UUID.randomUUID().toString();

        try (OutputStream os = Files.newOutputStream(
                Path.of(PATH, filename), CREATE, WRITE, TRUNCATE_EXISTING)) {

            is.transferTo(os);
        } catch (IOException e) {
            e.printStackTrace();
            filename = null;
        }
        return filename;
    }

    public void parseFile(String filename) {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Files.newInputStream(
                    Path.of(PATH, filename), READ)
                )
        )) {
            String s;
            while ((s = br.readLine()) != null) {
                System.out.println(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Optional<byte[]> getFile(String filename) {
        try {
            return Optional.of(Files.readAllBytes(Path.of(PATH, filename)));
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}
