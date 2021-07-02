package me.walkerknapp.cfi;

import me.walkerknapp.cfi.structs.CFIObject;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a generated folder from an {@link CMakeProject}.
 */
public class CMakeInstance {
    private CMakeProject project;

    private Path targetPath;

    private <T extends CFIObject> CompletableFuture<T> requestObject(CFIQuery<T> query) {
        return null;
    }
}
