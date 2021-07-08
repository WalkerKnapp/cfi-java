package me.walkerknapp.cfi;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class DefaultProcessRunner implements ProcessRunner {
    @Override
    public CompletableFuture<Void> start(File workingDir, String... args) throws IOException {
        ProcessBuilder pb = new ProcessBuilder();
        pb.directory(workingDir);
        pb.command(args);
        pb.inheritIO();

        Process process = pb.start();

        return process.onExit().thenApply(p -> null);
    }
}
