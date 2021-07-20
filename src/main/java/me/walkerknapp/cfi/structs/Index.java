package me.walkerknapp.cfi.structs;

import com.dslplatform.json.*;
import me.walkerknapp.cfi.CFIQuery;
import me.walkerknapp.cfi.CMakeInstance;

import java.util.HashMap;
import java.util.List;

@CompiledJson
public class Index {
    @JsonAttribute
    public CMake cmake;

    @JsonAttribute
    public List<Object> objects;

    @JsonAttribute(converter = ReplyConverter.class)
    public Reply reply;

    @CompiledJson
    public static class CMake {
        @JsonAttribute(mandatory = true)
        public Version version;

        @JsonAttribute(mandatory = true)
        public Paths paths;

        @JsonAttribute(mandatory = true)
        public Generator generator;

        @CompiledJson
        public static class Version {
            @JsonAttribute(mandatory = true)
            public int major;

            @JsonAttribute(mandatory = true)
            public int minor;

            @JsonAttribute(mandatory = true)
            public int patch;

            @JsonAttribute(mandatory = true)
            public String suffix;

            @JsonAttribute(mandatory = true)
            public String string;

            @JsonAttribute(mandatory = true)
            public boolean isDirty;
        }

        @CompiledJson
        public static class Paths {
            @JsonAttribute(mandatory = true)
            public String cmake;

            @JsonAttribute(mandatory = true)
            public String ctest;

            @JsonAttribute(mandatory = true)
            public String cpack;
        }

        @CompiledJson
        public static class Generator {
            @JsonAttribute(mandatory = true)
            public boolean multiConfig;

            @JsonAttribute(mandatory = true)
            public String name;

            @JsonAttribute
            public String platform;
        }
    }

    @CompiledJson
    public static class Object {
        @JsonAttribute(mandatory = true)
        public String kind;

        @JsonAttribute(mandatory = true)
        public Version version;

        @JsonAttribute(mandatory = true)
        public String jsonFile;

        @CompiledJson
        public static class Version {
            @JsonAttribute(mandatory = true)
            public int major;

            @JsonAttribute(mandatory = true)
            public int minor;
        }
    }

    public static class Reply {
        public HashMap<String, HashMap<String, ReplyFileReference>> clientStatelessReplies = new HashMap<>();
        public HashMap<String, HashMap<String, String>> clientStatelessUnknowns = new HashMap<>();

        public HashMap<String, ReplyFileReference> sharedStatelessReplies = new HashMap<>();
        public HashMap<String, String> sharedStatelessUnknowns = new HashMap<>();

        @CompiledJson
        public static class ReplyFileReference {
            @JsonAttribute(mandatory = true)
            public String kind;

            @JsonAttribute(mandatory = true)
            public Version version;

            @JsonAttribute(mandatory = true)
            public String jsonFile;

            @CompiledJson
            public static class Version {
                @JsonAttribute(mandatory = true)
                public int major;

                @JsonAttribute(mandatory = true)
                public int minor;
            }
        }
    }

    @JsonConverter(target = Reply.class)
    public static class ReplyConverter {
        public static final JsonReader.ReadObject<Reply> JSON_READER = reader -> {
            if (reader.wasNull()) return null;
            if (reader.last() != '{') throw reader.newParseError("Expecting '{' for object start");

            CMakeInstance cMakeInstance = (CMakeInstance) reader.context;
            JsonReader.ReadObject<Reply.ReplyFileReference> replyFileReferenceReader = cMakeInstance.getDslJson()
                    .tryFindReader(Reply.ReplyFileReference.class);

            if (replyFileReferenceReader == null) {
                throw new ConfigurationException("Unable to find reader for ReplyFileReference.");
            }

            Reply reply = new Reply();

            while (reader.getNextToken() == '"') {
                String fieldName = reader.readSimpleString();

                reader.getNextToken(); // Pop ":" from reader
                reader.getNextToken(); // Pop "{" from reader

                // Fields will be <kind>-v<major>, client-<client>, or <unknown>.
                if (fieldName.startsWith("client-")) {
                    HashMap<String, Reply.ReplyFileReference> clientStatelessReplies =
                            reply.clientStatelessReplies.computeIfAbsent(fieldName.substring("client-".length()), k -> new HashMap<>());
                    HashMap<String, String> clientStatelessUnknowns =
                            reply.clientStatelessUnknowns.computeIfAbsent(fieldName.substring("client-".length()), k -> new HashMap<>());

                    while (reader.getNextToken() == '"') {
                        String clientFieldName = reader.readSimpleString();

                        reader.getNextToken(); // Pop ":" from reader
                        reader.getNextToken(); // Pop "{" from reader

                        // Fields will either be <kind>-v<major>, <unknown>, or query.json
                        if ("query.json".equals(clientFieldName)) {
                            throw new UnsupportedOperationException("query.json currently not supported in index files");
                        } else if (CFIQuery.VALID_QUERY_FILE_NAMES.contains(clientFieldName)) {
                            clientStatelessReplies.put(clientFieldName, replyFileReferenceReader.read(reader));
                        } else {
                            reader.getNextToken(); // Pop "\"" from reader
                            reader.fillName();
                            assert reader.wasLastName("error");
                            reader.getNextToken(); // Pop ":" from reader

                            String error = reader.readSimpleString();
                            clientStatelessUnknowns.put(clientFieldName, error);

                            reader.getNextToken(); // Pop "}" from reader
                        }

                        if (reader.getNextToken() == '}') { // Pop "," (or "}" for end of object)
                            break;
                        }
                    }
                } else if (CFIQuery.VALID_QUERY_FILE_NAMES.contains(fieldName)) {
                    reply.sharedStatelessReplies.put(fieldName, replyFileReferenceReader.read(reader));
                } else {
                    reader.getNextToken(); // Pop "\"" from reader
                    reader.fillName();
                    assert reader.wasLastName("error");
                    reader.getNextToken(); // Pop ":" from reader

                    String error = reader.readSimpleString();
                    reply.sharedStatelessUnknowns.put(fieldName, error);

                    reader.getNextToken(); // Pop "}" from reader
                }

                if (reader.getNextToken() == '}') { // Pop "," (or "}" for end of object)
                    break;
                }
            }

            return reply;
        };

        public static final JsonWriter.WriteObject<Reply> JSON_WRITER = (writer, value) -> {
            throw new UnsupportedOperationException("Cannot write CMake reply objects.");
        };
    }
}
