package net.danygames2014.nyaview.mapping.entry;

import java.util.Objects;

public class ClassPath {
    // Package
    public final String pkg;

    // Class Name
    public final String name;

    public ClassPath(String pkg, String name) {
        this.pkg = pkg;
        this.name = name;
    }

    public static ClassPath fromName(String name) {
        if (name == null) {
            return new ClassPath("","");
        }

        String[] srcname = name.split("/");
        StringBuilder pkg = new StringBuilder(srcname.length - 1);
        for (int i = 0; i < srcname.length - 1; i++) {
            pkg.append(srcname[i]);

            if (i < (srcname.length - 2)) {
                pkg.append(".");
            }
        }
        return new ClassPath(pkg.toString(), srcname[srcname.length - 1]);
    }

    public String getFullPath() {
        return pkg.replace('.', '/') + "/" + name;
    }

    public final boolean equals(ClassPath other) {
        if(other.pkg.equals(this.pkg) && other.name.equals(this.name)) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(pkg);
        result = 31 * result + Objects.hashCode(name);
        return result;
    }

    @Override
    public String toString() {
        return "ClassPath{package=" + pkg + ", name=" + name + '}';
    }
}
