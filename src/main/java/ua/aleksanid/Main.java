package ua.aleksanid;

import ua.aleksanid.server.FileInspectorTelnetServer;

import java.io.File;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Please specify the root path for the searches[string]:");
        String root = scanner.nextLine();
        File rootFile = new File(root);
        validateRoot(rootFile);

        System.out.println("Please specify the port for the server[number]:");
        String portLine = scanner.nextLine();
        int port = Integer.parseInt(portLine);
        validatePort(port);

        try (FileInspectorTelnetServer fileInspectorTelnetServer = new FileInspectorTelnetServer(rootFile)) {
            fileInspectorTelnetServer.run(port);
        }
    }

    private static void validatePort(int port) {
        if (port < 0 || port > 65535) {
            throw new IllegalArgumentException("Port must be a number between 0 and 65535");
        }
    }

    private static void validateRoot(File rootFile) {
        if (!rootFile.exists()) {
            throw new IllegalArgumentException(rootFile + " path does not exists");
        } else if (!rootFile.isDirectory()) {
            throw new IllegalArgumentException(rootFile + " path is not a directory");
        }
    }
}