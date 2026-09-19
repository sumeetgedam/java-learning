package com.learning.core.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class FileDemo {

    public static void main(String[] args) {
        Path file = Path.of("data", "example.txt");

        System.out.println("file = " + file);
        System.out.println("file.getFileName() = " + file.getFileName());
        System.out.println("file.getParent() = " + file.getParent());

        Path filePath = Path.of("data", "message.txt");

        try {
            Files.createDirectories(filePath.getParent());
            Files.writeString(filePath, "Hello from Java");
        } catch (IOException e) {
            System.out.println("File operation failed");
        }

        try {
            String content = Files.readString(filePath);
            System.out.println("content = " + content);
        } catch (IOException e) {
            System.out.println("Unable to read file");
        }

        try {
            System.out.println("Files.exists(filePath) = " + Files.exists(filePath));
            System.out.println("Files.isRegularFile(filePath) = " + Files.isRegularFile(filePath));
            System.out.println("Files.size(filePath) = " + Files.size(filePath));

            System.out.println("Files.isDirectory(filePath) = " + Files.isDirectory(filePath));
            System.out.println("Files.isReadable(filePath) = " + Files.isReadable(filePath));
            System.out.println("Files.isWritable(filePath) = " + Files.isWritable(filePath));

            Path copy = Path.of("data", "message-copy.txt");
            Files.copy(filePath, copy);
            Files.move(copy, Path.of("data", "moved-message.txt"));
//            Files.deleteIfExists(filePath);
        }catch (IOException e) {
            System.out.println("File methods issue");
        }


//        Files.copy(source, target, REPLACE_EXISTING);

        try {
            List<String> lines = Files.readAllLines(filePath);
            for (String line : lines) {
                System.out.println("line = " + line);
            }
        }catch (IOException exception){
            System.out.println("Unable to read lines");
        }

        try(BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Unable to read file");
        }

        List<String> lines = List.of(
                "Java",
                "Spring",
                "Kafka"
        );
        try {
            Files.write(file, lines);
        } catch (IOException e) {
            System.out.println("Unable to write file");
        }

        Path directory  = Path.of("data");
        try(Stream<Path> paths = Files.list(directory)) {
            paths.forEach(System.out::println);
        }catch (IOException exception) {
            System.out.println("Unable to list directory");
        }
    }
}
