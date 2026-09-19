package com.learning.core.exceptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileReaderExample {

    public static void main(String[] args) {
        Path path = Path.of("example.txt");

        try(BufferedReader reader = Files.newBufferedReader(path)) {
            String line = reader.readLine();
            System.out.println(line);
        }catch(IOException exception) {
            System.out.println("Unable to read file");
        }
    }
}