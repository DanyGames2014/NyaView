package net.danygames2014.nyaview.descriptor;

import net.danygames2014.nyaview.mapping.entry.ClassPath;
import net.danygames2014.nyaview.mapping.entry.Method;

import java.util.ArrayList;
import java.util.List;

public class Descriptor {
    public List<String> args;
    public String returnType;

    public Descriptor() {
        this.args = new ArrayList<>();
        this.returnType = null;
    }

    @Override
    public String toString() {
        return "Descriptor{" +
                "args=" + args +
                ", returnType='" + returnType + '\'' +
                '}';
    }

    public static String niceString(Method method) {
        Descriptor descriptor = DescriptorParser.parseDescriptor(method.desc);

        StringBuilder sb = new StringBuilder();

        sb.append(ClassPath.fromName(descriptor.returnType).name).append(" ").append(ClassPath.fromName(method.name).name).append("(");
        for (int i = 0; i < descriptor.args.size(); i++) {
            sb.append(ClassPath.fromName(descriptor.args.get(i)).name);

            if (method.args.size() > i) {
                sb.append(" ").append(ClassPath.fromName(method.args.get(i)).name);
            }

            if (!(i == descriptor.args.size() - 1)) {
                sb.append(", ");
            }
        }
        sb.append(")");

        return sb.toString();
    }
}
