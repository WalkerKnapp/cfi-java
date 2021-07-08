package me.walkerknapp.cfi.structs;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonAttribute;

import java.util.List;

@CompiledJson
public class Target {
    @JsonAttribute(mandatory = true)
    public String name;

    @JsonAttribute(mandatory = true)
    public String id;

    @JsonAttribute(mandatory = true)
    public String type;

    @JsonAttribute
    public int backtrace;

    @JsonAttribute
    public Folder folder;

    @JsonAttribute
    public Paths paths;

    @JsonAttribute
    public String nameOnDisk;

    @JsonAttribute
    public List<Artifacts> artifacts;

    @JsonAttribute
    public Install install;

    @JsonAttribute
    public Link link;

    @JsonAttribute
    public Archive archive;

    @JsonAttribute
    public List<Dependency> dependencies;

    @JsonAttribute(mandatory = true)
    public List<Sources> sources;

    @JsonAttribute
    public List<SourceGroup> sourceGroups;

    @JsonAttribute
    public List<CompileGroup> compileGroups;

    @JsonAttribute
    public CodeModelBacktraceGraph backtraceGraph;

    @CompiledJson
    public static class Folder {
        @JsonAttribute(mandatory = true)
        public String name;
    }

    @CompiledJson
    public static class Paths {
        @JsonAttribute(mandatory = true)
        public String source;

        @JsonAttribute(mandatory = true)
        public String build;
    }

    @CompiledJson
    public static class Artifacts {
        @JsonAttribute(mandatory = true)
        public String path;
    }

    @CompiledJson
    public static class Install {
        @JsonAttribute(mandatory = true)
        public Prefix prefix;

        @JsonAttribute(mandatory = true)
        public List<Destination> destinations;

        @CompiledJson
        public static class Prefix {
            @JsonAttribute(mandatory = true)
            public String path;
        }

        @CompiledJson
        public static class Destination {
            @JsonAttribute(mandatory = true)
            public String path;

            @JsonAttribute
            public int backtrace;
        }
    }

    @CompiledJson
    public static class Link {
        @JsonAttribute(mandatory = true)
        public String language;

        @JsonAttribute
        public List<CommandFragment> commandFragments;

        @JsonAttribute
        public boolean lto;

        @JsonAttribute
        public SysRoot sysroot;

        @CompiledJson
        public static class CommandFragment {
            @JsonAttribute(mandatory = true)
            public String fragment;

            @JsonAttribute(mandatory = true)
            public String role;
        }

        @CompiledJson
        public static class SysRoot {
            @JsonAttribute(mandatory = true)
            public String path;
        }
    }

    @CompiledJson
    public static class Archive {
        @JsonAttribute
        public List<Link.CommandFragment> commandFragments;

        @JsonAttribute
        public boolean lto;

        @CompiledJson
        public static class CommandFragment {
            @JsonAttribute(mandatory = true)
            public String fragment;

            @JsonAttribute(mandatory = true)
            public String role;
        }
    }

    @CompiledJson
    public static class Dependency {
        @JsonAttribute(mandatory = true)
        public String id;

        @JsonAttribute
        public int backtrace;
    }

    @CompiledJson
    public static class Sources {
        @JsonAttribute(mandatory = true)
        public String path;

        @JsonAttribute
        public int compileGroupIndex;

        @JsonAttribute
        public int sourceGroupIndex;

        @JsonAttribute
        public boolean isGenerated;

        @JsonAttribute
        public int backtrace;
    }

    @CompiledJson
    public static class SourceGroup {
        @JsonAttribute
        public String name;

        @JsonAttribute
        public List<Integer> sourceIndexes;
    }

    @CompiledJson
    public static class CompileGroup {
        @JsonAttribute
        public List<Integer> sourceIndexes;

        @JsonAttribute
        public String language;

        @JsonAttribute
        public LanguageStandard languageStandard;

        @JsonAttribute
        public List<CompileCommandFragment> compileCommandFragments;

        @JsonAttribute
        public List<Include> includes;

        @JsonAttribute
        public List<PrecompileHeader> precompileHeaders;

        @JsonAttribute
        public List<Define> defines;

        @JsonAttribute
        public SysRoot sysroot;

        @CompiledJson
        public static class LanguageStandard {
            @JsonAttribute
            public List<Integer> backtraces;

            @JsonAttribute(mandatory = true)
            public String standard;
        }

        @CompiledJson
        public static class CompileCommandFragment {
            @JsonAttribute(mandatory = true)
            public String fragment;
        }

        @CompiledJson
        public static class Include {
            @JsonAttribute(mandatory = true)
            public String path;

            @JsonAttribute
            public boolean isSystem;

            @JsonAttribute
            public int backtrace;
        }

        @CompiledJson
        public static class PrecompileHeader {
            @JsonAttribute(mandatory = true)
            public String header;

            @JsonAttribute
            public int backtrace;
        }

        @CompiledJson
        public static class Define {
            @JsonAttribute(mandatory = true)
            public String define;

            @JsonAttribute
            public int backtrace;
        }

        @CompiledJson
        public static class SysRoot {
            @JsonAttribute(mandatory = true)
            public String path;
        }
    }
}
