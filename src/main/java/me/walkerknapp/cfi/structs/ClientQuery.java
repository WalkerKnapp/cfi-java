package me.walkerknapp.cfi.structs;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonAttribute;

import java.util.ArrayList;
import java.util.List;

@CompiledJson
public class ClientQuery {

    public ClientQuery() {
        this.requests = new ArrayList<>();
    }

    @JsonAttribute(mandatory = true)
    public List<Request> requests;

    @CompiledJson
    public static class Request {

        public Request() {
        }

        public Request(String kind, int major, int minor) {
            this.kind = kind;
            this.version = new Version();
            this.version.major = major;
            this.version.minor = minor;
        }

        @JsonAttribute(mandatory = true)
        public String kind;

        @JsonAttribute(mandatory = true)
        public Version version;

        @CompiledJson
        public static class Version {
            @JsonAttribute(mandatory = true)
            public int major;

            @JsonAttribute
            public int minor;
        }
    }
}
