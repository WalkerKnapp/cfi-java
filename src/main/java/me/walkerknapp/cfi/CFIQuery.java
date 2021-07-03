package me.walkerknapp.cfi;

import me.walkerknapp.cfi.structs.*;

import java.util.Set;

public final class CFIQuery<T extends CFIObject> {
    public static final CFIQuery<CodeModel> CODE_MODEL = new CFIQuery<>("codemodel", 2);
    public static final CFIQuery<Cache> CACHE = new CFIQuery<>("cache", 2);
    public static final CFIQuery<CMakeFiles> CMAKE_FILES = new CFIQuery<>("cmakeFiles", 1);
    public static final CFIQuery<Toolchains> TOOLCHAINS = new CFIQuery<>("toolchains", 1);

    public static final Set<String> VALID_QUERY_FILE_NAMES = Set.of(CODE_MODEL.getQueryFileName(),
            CACHE.getQueryFileName(),
            CMAKE_FILES.getQueryFileName(),
            TOOLCHAINS.getQueryFileName());

    private final String objectKind;
    private final int version;

    private CFIQuery(String objectKind, int version) {
        this.objectKind = objectKind;
        this.version = version;
    }

    public String getQueryFileName() {
        return objectKind + "-v" + version;
    }
}
