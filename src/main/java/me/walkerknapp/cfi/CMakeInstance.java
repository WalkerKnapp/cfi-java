package me.walkerknapp.cfi;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.runtime.Settings;
import me.walkerknapp.cfi.structs.CFIObject;
import me.walkerknapp.cfi.structs.Index;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Represents a generated folder from an {@link CMakeProject}.
 */
public class CMakeInstance {
    private final ProcessRunner processRunner;
    private final ExecutorService executorService;
    private final DslJson<CMakeInstance> dslJson;

    private final CMakeProject project;
    private final Path targetPath;

    private final String clientName;

    public CMakeInstance(CMakeProject project, Path targetPath, ProcessRunner processRunner) {
        this.project = project;
        this.targetPath = targetPath;

        this.processRunner = processRunner;
        this.executorService = Executors.newFixedThreadPool(Integer.MAX_VALUE);
        this.dslJson = new DslJson<>(Settings.<CMakeInstance>withRuntime().
                allowArrayFormat(true).includeServiceLoader().withContext(this));

        this.clientName = "cfijava" + UUID.randomUUID().toString().replace('-', '.');
    }

    public CMakeInstance(CMakeProject project, Path targetPath) {
        this(project, targetPath, new DefaultProcessRunner());
    }

    public CompletableFuture<Void> generate() throws IOException {
        return this.processRunner.start(targetPath.toFile(),
                Executables.cmake().toAbsolutePath().toString(),
                "--no-warn-unused-cli", project.getSourceDirectory().toAbsolutePath().toString());
    }

    public <T extends CFIObject> CompletableFuture<T> requestObject(CFIQuery<T> query) {
        return CompletableFuture
                .supplyAsync(() -> {
                    try {
                        Path queryPath = getApiQueryPath();
                        Files.createFile(queryPath);

                        Path replyPath = getApiReplyPath();
                        Path previousIndex = Files.list(replyPath)
                                .filter(p -> p.getFileName().toString().startsWith("index-"))
                                .findAny()
                                .orElse(null);

                        CompletableFuture<Void> generationFuture = generate();

                        // Spin wait for the previous index file to be gone
                        while (previousIndex != null && Files.exists(previousIndex) && !generationFuture.isDone()) {
                            Thread.onSpinWait();
                        }

                        if (previousIndex != null && Files.exists(previousIndex) && generationFuture.isDone()) {
                            throw new IllegalStateException("Cmake finished without removing the old API index file.");
                        }

                        // Spin wait for a new index file
                        Path newIndex;
                        do {
                            newIndex = Files.list(replyPath)
                                    .filter(p -> p.getFileName().toString().startsWith("index-"))
                                    .findAny()
                                    .orElse(null);
                        } while (newIndex == null && !generationFuture.isDone());

                        if (newIndex == null && generationFuture.isDone()) {
                            throw new IllegalStateException("Cmake finished without generating a new API index file.");
                        }

                        // Read the index file
                        Index indexFile;

                        assert newIndex != null;
                        try (InputStream indexIs = Files.newInputStream(newIndex)) {
                            indexFile = dslJson.deserialize(Index.class, indexIs);
                        }

                        // TODO: Read off this input file
                        return null;


                    } catch (IOException e) {
                        throw new CompletionException(e);
                    }
                }, executorService);
    }

    private Path getApiQueryPath() throws IOException {
        Path queryPath = targetPath.resolve(".cmake").resolve("api").resolve("v1").resolve("query").resolve(this.clientName);
        Files.createDirectories(queryPath);
        return queryPath;
    }

    private Path getApiReplyPath() throws IOException {
        Path replyPath = targetPath.resolve(".cmake").resolve("api").resolve("v1").resolve("reply");
        Files.createDirectories(replyPath);
        return replyPath;
    }

    public String getClientName() {
        return this.clientName;
    }

    public DslJson<CMakeInstance> getDslJson() {
        return dslJson;
    }
}
