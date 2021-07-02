package me.walkerknapp.cfi;

import me.walkerknapp.cfi.structs.*;

public final class CFIQuery<T extends CFIObject> {
    public final CFIQuery<CodeModel> CODE_MODEL = new CFIQuery<>("codemodel", 2);
    public final CFIQuery<Cache> CACHE = new CFIQuery<>("cache", 2);
    public final CFIQuery<CMakeFiles> CMAKE_FILES = new CFIQuery<>("cmakeFiles", 1);
    public final CFIQuery<Toolchains> TOOLCHAINS = new CFIQuery<>("toolchains", 1);

    private final String objectKind;
    private final int version;

    private CFIQuery(String objectKind, int version) {
        this.objectKind = objectKind;
        this.version = version;
    }
}
