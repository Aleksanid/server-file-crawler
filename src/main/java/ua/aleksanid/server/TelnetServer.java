package ua.aleksanid.server;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class TelnetServer implements AutoCloseable {

    private final ExecutorService handlersExecutor;

    protected TelnetServer() {
        this.handlersExecutor = Executors.newCachedThreadPool();
    }

    public void run(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("--- SERVER STARTED on port: " + port + " ---");
            while (true) {
                serveConnection(serverSocket);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Error while starting server", e);
        }
    }

    private void serveConnection(ServerSocket serverSocket) {
        try {
            Socket incoming = serverSocket.accept();
            handlersExecutor.submit(() -> handleRequest(incoming));
        } catch (IOException e) {
            throw new UncheckedIOException("Error while accepting traffic", e);
        }
    }

    public abstract void handleRequest(Socket incoming);

    @Override
    public void close() {
        handlersExecutor.shutdown();
    }
}
