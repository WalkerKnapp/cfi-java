package me.walkerknapp.cfi.structs;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonAttribute;

import java.util.List;

@CompiledJson
public class CodeModelBacktraceGraph {
    @JsonAttribute(mandatory = true)
    public List<Node> nodes;

    @JsonAttribute(mandatory = true)
    public List<String> commands;

    @JsonAttribute(mandatory = true)
    public List<String> files;

    @CompiledJson
    public static class Node {
        @JsonAttribute(mandatory = true)
        public int file;

        @JsonAttribute
        public int line;

        @JsonAttribute
        public int command;

        @JsonAttribute
        public int parent;
    }
}
