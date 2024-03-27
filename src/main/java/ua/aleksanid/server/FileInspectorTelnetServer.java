package ua.aleksanid.server;

import ua.aleksanid.file.FileTreeInspector;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FileInspectorTelnetServer extends TelnetServer {

    private final FileTreeInspector fileTreeInspector;

    public FileInspectorTelnetServer(File root) {
        this.fileTreeInspector = new FileTreeInspector(root);
    }

    @Override
    public void handleRequest(Socket incoming) {
        InputStream inputStream;
        OutputStream outputStream;
        try {
            inputStream = incoming.getInputStream();
            outputStream = incoming.getOutputStream();
        } catch (IOException e) {
            throw new UncheckedIOException("Error receiving streams from socket", e);
        }

        Scanner in = new Scanner(inputStream, StandardCharsets.UTF_8);
        PrintWriter out = new PrintWriter(outputStream, true);

        out.println("--- CONNECTED ---");
        while (!out.checkError()) {
            try {
                out.println("Inspector is configured to search from " + fileTreeInspector.getRootPath() + ". Ready to perform request");
                out.println("Please specify search mask[string]:");

                String searchMask = in.nextLine();

                out.println("Please specify search depth[number]:");
                String depthLine = in.nextLine();
                int depth = Integer.parseInt(depthLine);

                out.println("--- SEARCH STARTED ---");
                Future<?> future = fileTreeInspector.runInspection(depth, searchMask, outputStream);
                future.get();
                out.println("--- SEARCH FINISHED ---");
            } catch (NumberFormatException numberFormatException) {
                out.println("Provided depth is not a valid number");
            } catch (ExecutionException e) {
                out.println("There was an error performing search: " + e);
            } catch (InterruptedException e) {
                out.println("Thread is being interrupted: " + e);
                Thread.currentThread().interrupt();
            } catch (RuntimeException runtimeException) {
                out.println("Error: " + runtimeException);
                System.err.println("Error while working with the client " + runtimeException);
            }
        }
        System.out.println("Connection closed");
    }

    @Override
    public void close() {
        super.close();
        fileTreeInspector.close();
    }
}
