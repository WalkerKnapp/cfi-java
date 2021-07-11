package me.walkerknapp.cfi.structs;

import com.dslplatform.json.*;

import java.util.List;

@CompiledJson
public class Directory {
    @JsonAttribute(mandatory = true)
    public Paths paths;

    @JsonAttribute(mandatory = true)
    public List<Installer> installers;

    @JsonAttribute(mandatory = true)
    public CodeModelBacktraceGraph backtraceGraph;

    @CompiledJson
    public static class Paths {
        @JsonAttribute(mandatory = true)
        public String source;

        @JsonAttribute(mandatory = true)
        public String build;
    }

    @CompiledJson
    public static class Installer {
        @JsonAttribute(mandatory = true)
        public String component;

        @JsonAttribute
        public String destination;

        @JsonAttribute
        public List<Path> paths;

        @JsonAttribute(mandatory = true)
        public String type;

        @JsonAttribute
        public boolean isExcludeFromAll;

        @JsonAttribute
        public boolean isForAllComponents;

        @JsonAttribute
        public boolean isOptional;

        @JsonAttribute
        public String targetId;

        @JsonAttribute
        public int targetIndex;

        @JsonAttribute
        public boolean targetIsImportLibrary;

        @JsonAttribute
        public String targetInstallNamelink;

        @JsonAttribute
        public String exportName;

        @JsonAttribute
        public List<ExportTarget> exportTargets;

        @JsonAttribute
        public String runtimeDependencySetName;

        @JsonAttribute
        public String runtimeDependencySetType;

        @JsonAttribute
        public String scriptFile;

        @JsonAttribute
        public int backtrace;

        /**
         * Either {@link Path#value} will be filled, or {@link Path#from} and {@link Path#to} will be filled.
         */
        @CompiledJson
        public static class Path {
            public String value;

            public String from;
            public String to;

            @JsonConverter(target=Path.class)
            public static class PathConverter {
                public static final JsonReader.ReadObject<Path> JSON_READER = reader -> {
                    if (reader.wasNull()) return null;

                    Path path = new Path();

                    if (reader.last() == '"') {
                        // Path only has a single value
                        path.value = reader.readString();
                    } else if (reader.last() == '{') {
                        // Path should have a "from" and "to" attribute
                        while (reader.getNextToken() == '"') {
                            String fieldName = reader.readString();

                            reader.getNextToken(); // Pop ":" from reader

                            if (fieldName.equals("from")) {
                                reader.getNextToken(); // Pop "\"" from reader

                                path.from = reader.readString();
                            } else if (fieldName.equals("to")) {
                                reader.getNextToken(); // Pop "\"" from reader

                                path.to = reader.readString();
                            } else {
                                throw new IllegalArgumentException("Invalid Directory.Installer.Path attribute: " + fieldName);
                            }

                            if (reader.getNextToken() == '}') { // Pop "," (or "}" for end of object)
                                break;
                            }
                        }
                    }

                    return path;
                };

                public static final JsonWriter.WriteObject<Path> JSON_WRITER = (writer, value) -> {
                    throw new UnsupportedOperationException("Cannot write Cmake directory objects.");
                };
            }
        }
    }

    @CompiledJson
    public static class ExportTarget {
        @JsonAttribute(mandatory = true)
        public String id;

        @JsonAttribute(mandatory = true)
        public int index;
    }
}
