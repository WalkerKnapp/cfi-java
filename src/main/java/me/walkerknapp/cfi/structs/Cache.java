package me.walkerknapp.cfi.structs;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonAttribute;

import java.util.List;

@CompiledJson
public class Cache implements CFIObject {
    @JsonAttribute(mandatory = true)
    public List<Entry> entries;

    @CompiledJson
    public static class Entry {
        @JsonAttribute(mandatory = true)
        public String name;

        @JsonAttribute(mandatory = true)
        public String value;

        @JsonAttribute(mandatory = true)
        public String type;

        @JsonAttribute(mandatory = true)
        public List<Property> properties;

        @CompiledJson
        public static class Property {
            @JsonAttribute
            public String name;

            @JsonAttribute
            public String value;
        }
    }
}
