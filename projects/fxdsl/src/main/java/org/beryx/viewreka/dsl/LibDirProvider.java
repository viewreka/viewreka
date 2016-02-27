package org.beryx.viewreka.dsl;

import java.io.File;

public interface LibDirProvider {
    static File getLibDir(File baseDir) {
        return new File(baseDir, "lib");
    }
}
