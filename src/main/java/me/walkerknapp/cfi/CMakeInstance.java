package me.walkerknapp.cfi;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.runtime.Settings;
import me.walkerknapp.cfi.structs.CFIObject;
import me.walkerknapp.cfi.structs.ClientQuery;
import me.walkerknapp.cfi.structs.Index;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.AccessDeniedException;
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

        this.clientName = "cfijava" + UUID.randomUUID().toString().replace("-", "");
    }

    public CMakeInstance(CMakeProject project, Path targetPath) {
        this(project, targetPath, new DefaultProcessRunner());
    }

    public CompletableFuture<Void> generate() throws IOException {
        return this.processRunner.start(targetPath.toFile(),
                Executables.cmake().toAbsolutePath().toString(),
                "--no-warn-unused-cli", project.getSourceDirectory().toAbsolutePath().toString());
    }

    public <T extends CFIObject> CompletableFuture<T> queueRequest(CFIQuery<T> query) {
        return CompletableFuture
                .supplyAsync(() -> {
                    try {
                        Path queryPath = getApiQueryPath();
                        Path requestFile = queryPath.resolve(query.getQueryFileName());
                        Files.createFile(requestFile);

                        Path replyPath = getApiReplyPath();
                        Path previousIndex = Files.list(replyPath)
                                .filter(p -> p.getFileName().toString().startsWith("index-"))
                                .findAny()
                                .orElse(null);

                        // Spin wait for the previous index file to be gone
                        while (previousIndex != null && Files.exists(previousIndex)) {
                            Thread.onSpinWait();
                        }

                        // Spin wait for a new index file
                        Path newIndex;
                        do {
                            newIndex = Files.list(replyPath)
                                    .filter(p -> p.getFileName().toString().startsWith("index-"))
                                    .findAny()
                                    .orElse(null);
                        } while (newIndex == null);

                        // Read the index file
                        Index indexFile;

                        try (InputStream indexIs = Files.newInputStream(newIndex)) {
                            indexFile = dslJson.deserialize(Index.class, indexIs);
                        }

                        var replies = indexFile.reply.clientStatelessReplies.get(this.clientName);
                        if (replies == null) {
                            throw new IllegalStateException("CMake didn't create a response for our client (" + this.clientName + ").");
                        }
                        var queryReply = replies.get(query.getQueryFileName());
                        if (queryReply == null) {
                            throw new IllegalStateException("CMake didn't respond to the query " + query.getQueryFileName() + " from client " + this.clientName);
                        }

                        // Read the object we queried for
                        T cfiObject;
                        try (InputStream queryObjectIs = Files.newInputStream(replyPath.resolve(queryReply.jsonFile))) {
                            cfiObject = dslJson.deserialize(query.getObjClass(), queryObjectIs);
                        }

                        // Clean up our request file
                        Files.delete(requestFile);

                        return cfiObject;
                    } catch (IOException e) {
                        throw new CompletionException(e);
                    }
                }, executorService);
    }

    public <T extends CFIObject> CompletableFuture<T> immediatelyRequestObject(CFIQuery<T> query) {
        return CompletableFuture
                .supplyAsync(() -> {
                    try {
                        Path queryPath = getApiQueryPath();
                        Path requestFile = queryPath.resolve(query.getQueryFileName());
                        Files.createFile(requestFile);

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

                        var replies = indexFile.reply.clientStatelessReplies.get(this.clientName);
                        if (replies == null) {
                            throw new IllegalStateException("CMake didn't create a response for our client (" + this.clientName + ").");
                        }
                        var queryReply = replies.get(query.getQueryFileName());
                        if (queryReply == null) {
                            throw new IllegalStateException("CMake didn't respond to the query " + query.getQueryFileName() + " from client " + this.clientName);
                        }

                        // Read the object we queried for
                        T cfiObject;
                        try (InputStream queryObjectIs = Files.newInputStream(replyPath.resolve(queryReply.jsonFile))) {
                            cfiObject = dslJson.deserialize(query.getObjClass(), queryObjectIs);
                        }

                        // Clean up our request file
                        Files.delete(requestFile);

                        return cfiObject;
                    } catch (IOException e) {
                        throw new CompletionException(e);
                    }
                }, executorService);
    }

    public <T> CompletableFuture<T> readReplyObject(Class<T> replyClass, String jsonFile) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Path replyObjectPath = getApiReplyPath().resolve(jsonFile);

                try (InputStream is = Files.newInputStream(replyObjectPath)) {
                    return dslJson.deserialize(replyClass, is);
                }
            } catch (IOException e) {
                throw new CompletionException(e);
            }
        }, executorService);
    }

    private Path getApiQueryPath() throws IOException {
        Path queryPath = targetPath.resolve(".cmake").resolve("api").resolve("v1").resolve("query").resolve("client-" + this.clientName);
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
