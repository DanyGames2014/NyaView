package net.danygames2014.nyaview.mapping.entry;

import java.util.Objects;

public class ClassPath {
    // Package
    public String pkg;

    // Class Name
    public String name;

    public ClassPath(String pkg, String name) {
        this.pkg = pkg;
        this.name = name;
    }

    public static ClassPath fromName(String name){
        String[] srcname = name.split("/");
        StringBuilder pkg = new StringBuilder(srcname.length - 1);
        for (int i = 0; i < srcname.length - 1; i++) {
            pkg.append(srcname[i]);

            if (i < (srcname.length - 2)) {
                pkg.append(".");
            }
        }
        return new ClassPath(pkg.toString(), srcname[srcname.length-1]);
    }

    public String getFullPath() {
        return pkg.replace('.', '/') + "/" + name;
    }

    public String getPackage(){
        return pkg;
    }

    public String getClassName(){
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassPath classPath = (ClassPath) o;

        if (!Objects.equals(pkg, classPath.pkg)) return false;
        return Objects.equals(name, classPath.name);
    }

    @Override
    public int hashCode() {
        int result = pkg != null ? pkg.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClassPath{" +
                "pkg='" + pkg + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
