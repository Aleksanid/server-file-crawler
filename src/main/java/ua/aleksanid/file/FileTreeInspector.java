package ua.aleksanid.file;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;

public class FileTreeInspector implements AutoCloseable {
    private final ExecutorService fileInspectionThread;
    private final File root;

    public FileTreeInspector(File root) {
        this.root = root;
        this.fileInspectionThread = Executors.newSingleThreadExecutor();
    }


    public Future<?> runInspection(int depth, String searchMask, OutputStream resultsOutputStream) {

        String validationError = getValidationError(depth, searchMask);
        if (validationError != null) {
            throw new IllegalArgumentException(validationError);
        }

        return fileInspectionThread.submit(() -> inspect(depth, searchMask, resultsOutputStream));
    }

    private String getValidationError(int depth, String searchMask) {
        if (depth < 0) {
            return "Depth must be a positive number.";
        }
        if (searchMask.isEmpty()) {
            return "Search mask is empty";
        }
        return null;
    }

    private void inspect(int depth, String searchMask, OutputStream outputStream) {
        try (Stream<Path> fileWalk = Files.walk(Paths.get(root.toURI()), depth)) {

            fileWalk
                    .map(Path::toString)
                    .map(path -> path.substring(root.toString().length()))
                    .filter(path -> path.contains(searchMask))
                    .forEach(path -> {
                        try {
                            outputStream.write(path.getBytes());
                            outputStream.write(System.lineSeparator().getBytes());
                        } catch (IOException e) {
                            throw new UncheckedIOException("Error writing to output stream", e);
                        }
                    });

        } catch (IOException e) {
            throw new UncheckedIOException("File inspection faced problem with I/O system", e);
        }
    }

    public String getRootPath() {
        return root.getPath();
    }

    @Override
    public void close() {
        fileInspectionThread.shutdown();
    }

}
