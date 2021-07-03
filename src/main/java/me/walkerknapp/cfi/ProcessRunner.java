package me.walkerknapp.cfi;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public interface ProcessRunner {
    CompletableFuture<Void> start(File workingDir, String... args) throws IOException;
}
