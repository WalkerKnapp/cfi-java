package me.walkerknapp.cfi;

import java.nio.file.Path;

/**
 * Represents a project that is buildable by CMake.
 */
public class CMakeProject {
    private Path sourceDirectory;

    public Path getSourceDirectory() {
        return this.sourceDirectory;
    }
}
