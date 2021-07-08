package me.walkerknapp.cfi;

import java.nio.file.Path;

/**
 * Represents a project that is buildable by CMake.
 */
public class CMakeProject {
    private final Path sourceDirectory;

    public CMakeProject(Path sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    public Path getSourceDirectory() {
        return this.sourceDirectory;
    }
}
