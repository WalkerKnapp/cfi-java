package me.walkerknapp.cfi;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Executables {
    private static final Logger logger = LoggerFactory.getLogger(Executables.class);

    /**
     * Find a path to the intended CMake executable.
     *
     * In order, the following locations are tried:
     * - The "cmakeExecutable" java property.
     * - The "CMAKE_EXECUTABLE" environment variable.
     * - The current working directory.
     * - The system path.
     *
     * @return A path to a CMake executable.
     */
    public static Path cmake() throws IOException {
        String cmakeExecutable = System.getProperty("cmakeExecutable");
        if (isPathValid(cmakeExecutable, "cmakeExecutable property")) {
            return Paths.get(cmakeExecutable);
        }

        cmakeExecutable = System.getenv("CMAKE_EXECUTABLE");
        if (isPathValid(cmakeExecutable, "CMAKE_EXECUTABLE environment variable")) {
            return Paths.get(cmakeExecutable);
        }

        Path cmakeExecutablePath = Files.list(Paths.get("."))
                .filter(path -> {
                    String fileName = path.getFileName().toString().toLowerCase();
                    return "cmake".equals(fileName) || "cmake.exe".equals(fileName);
                }).filter(path -> isPathValid(path, "From Working Directory"))
                .findAny().orElse(null);
        if (cmakeExecutablePath != null) {
            return cmakeExecutablePath;
        }

        String systemPath = System.getenv("PATH");
        if (systemPath != null) {
            for (String systemPathEntry : StringUtils.split(systemPath, File.pathSeparatorChar)) {
                cmakeExecutablePath = Files.list(Paths.get(systemPathEntry))
                        .filter(path -> {
                            String fileName = path.getFileName().toString().toLowerCase();
                            return "cmake".equals(fileName) || "cmake.exe".equals(fileName);
                        }).filter(path -> isPathValid(path, "From System Path"))
                        .findAny().orElse(null);
                if (cmakeExecutablePath != null) {
                    return cmakeExecutablePath;
                }
            }
        }

        throw new IllegalStateException("Could not find a path to a cmake executable.");
    }

    private static boolean isPathValid(String path, String reason) {
        return path != null && isPathValid(Paths.get(path), reason);
    }

    private static boolean isPathValid(Path path, String reason) {
        if (path == null) {
            return false;
        }

        logger.debug("Searching for CMake at {} ({})", path.toAbsolutePath(), reason);

        if (!Files.exists(path)) {
            logger.error("Searched for CMake at {} ({}), but it does not exist.",
                    path.toAbsolutePath(), reason);
            return false;
        }

        if (!Files.isExecutable(path)) {
            logger.error("Searched for CMake at {} ({}), but it is not a regular executable file.",
                    path.toAbsolutePath(), reason);
            return false;
        }

        logger.debug("Found CMake at {} ({})", path.toAbsolutePath(), reason);

        return true;
    }
}
