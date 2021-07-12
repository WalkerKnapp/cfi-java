package me.walkerknapp.cfi.structs;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonAttribute;

import java.util.List;

@CompiledJson
public class CMakeFiles implements CFIObject {
    @JsonAttribute(mandatory = true)
    public Paths paths;

    @JsonAttribute(mandatory = true)
    public List<Input> inputs;

    @CompiledJson
    public static class Paths {
        @JsonAttribute(mandatory = true)
        public String source;

        @JsonAttribute(mandatory = true)
        public String build;
    }

    @CompiledJson
    public static class Input {
        @JsonAttribute(mandatory = true)
        public String path;

        @JsonAttribute
        public boolean isGenerated;

        @JsonAttribute
        public boolean isExternal;

        @JsonAttribute
        public boolean isCMake;
    }
}
