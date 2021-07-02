package me.walkerknapp.cfi.structs;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonAttribute;

import java.util.List;

@CompiledJson
public class CodeModel implements CFIObject {

    @JsonAttribute(mandatory = true)
    public Paths paths;

    @JsonAttribute(mandatory = true)
    public List<Configuration> configurations;

    @CompiledJson
    public static class Paths {
        @JsonAttribute(mandatory = true)
        public String source;

        @JsonAttribute(mandatory = true)
        public String build;
    }

    @CompiledJson
    public static class Configuration {
        @JsonAttribute(mandatory = true)
        public String name;

        @JsonAttribute(mandatory = true)
        public List<Directory> directories;

        @JsonAttribute(mandatory = true)
        public List<Project> projects;

        @JsonAttribute(mandatory = true)
        public List<Target> targets;

        public static class Directory {
            @JsonAttribute(mandatory = true)
            public String source;

            @JsonAttribute(mandatory = true)
            public String build;

            @JsonAttribute
            public int parentIndex;

            @JsonAttribute
            public List<Integer> childIndexes;

            @JsonAttribute(mandatory = true)
            public int projectIndex;

            @JsonAttribute
            public int targetIndexes;

            @JsonAttribute
            public MinimumCMakeVersion minimumCMakeVersion;

            @JsonAttribute
            public boolean hasInstallRule;

            @JsonAttribute
            public String jsonFile;

            @CompiledJson
            public static class MinimumCMakeVersion {
                @JsonAttribute(mandatory = true)
                public String string;
            }
        }

        @CompiledJson
        public static class Project {
            @JsonAttribute(mandatory = true)
            public String name;

            @JsonAttribute
            public int parentIndex;

            @JsonAttribute
            public List<Integer> childIndexes;

            @JsonAttribute(mandatory = true)
            public List<Integer> directoryIndexes;

            @JsonAttribute
            public List<Integer> targetIndexes;
        }

        @CompiledJson
        public static class Target {
            @JsonAttribute(mandatory = true)
            public String name;

            @JsonAttribute(mandatory = true)
            public String id;

            @JsonAttribute(mandatory = true)
            public int directoryIndex;

            @JsonAttribute(mandatory = true)
            public int projectIndex;

            @JsonAttribute
            public String jsonFile;
        }
    }
}
