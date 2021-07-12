package me.walkerknapp.cfi.structs;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonAttribute;

import java.util.List;

@CompiledJson
public class Toolchains implements CFIObject {
    @JsonAttribute(mandatory = true)
    public List<Toolchain> toolchains;

    @CompiledJson
    public static class Toolchain {
        @JsonAttribute(mandatory = true)
        public String language;

        @JsonAttribute(mandatory = true)
        public Compiler compiler;

        @CompiledJson
        public static class Compiler {
            @JsonAttribute
            public String path;

            @JsonAttribute
            public String id;

            @JsonAttribute
            public String version;

            @JsonAttribute
            public String target;

            @JsonAttribute
            public Implicit implicit;

            @CompiledJson
            public static class Implicit {
                @JsonAttribute
                public List<String> includeDirectories;

                @JsonAttribute
                public List<String> linkDirectories;

                @JsonAttribute
                public List<String> linkFrameworkDirectories;

                @JsonAttribute
                public List<String> linkLibraries;
            }
        }

        @JsonAttribute
        public List<String> sourceFileExtensions;
    }
}
