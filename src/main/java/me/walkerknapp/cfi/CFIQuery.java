package me.walkerknapp.cfi;

import me.walkerknapp.cfi.structs.*;

import java.util.Set;

public final class CFIQuery<T extends CFIObject> {
    public static final CFIQuery<CodeModel> CODE_MODEL = new CFIQuery<>("codemodel", 2, 3, CodeModel.class);
    public static final CFIQuery<Cache> CACHE = new CFIQuery<>("cache", 2, 0, Cache.class);
    public static final CFIQuery<CMakeFiles> CMAKE_FILES = new CFIQuery<>("cmakeFiles", 1, 0, CMakeFiles.class);
    public static final CFIQuery<Toolchains> TOOLCHAINS = new CFIQuery<>("toolchains", 1, 0, Toolchains.class);

    public static final Set<String> VALID_QUERY_FILE_NAMES = Set.of(CODE_MODEL.getQueryFileName(),
            CACHE.getQueryFileName(),
            CMAKE_FILES.getQueryFileName(),
            TOOLCHAINS.getQueryFileName());

    private final String objectKind;
    private final int version;
    private final int minor;
    private final Class<T> objClass;

    private CFIQuery(String objectKind, int version, int minor, Class<T> objClass) {
        this.objectKind = objectKind;
        this.version = version;
        this.minor = minor;
        this.objClass = objClass;
    }

    public String getQueryFileName() {
        return objectKind + "-v" + version;
    }

    public Class<T> getObjClass() {
        return objClass;
    }

    public ClientQuery.Request createClientRequest() {
        return new ClientQuery.Request(objectKind, version, minor);
    }
}
