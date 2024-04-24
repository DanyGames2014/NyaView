package net.danygames2014.nyaview.util.descriptor;

import net.danygames2014.nyaview.mapping.entry.Method;

import java.util.ArrayList;
import java.util.List;

public class Descriptor {
    public List<String> args;
    public String returnType;

    public Descriptor() {
        this.args = new ArrayList<>();
        this.returnType = returnType = null;
    }

    @Override
    public String toString() {
        return "Descriptor{" +
                "args=" + args +
                ", returnType='" + returnType + '\'' +
                '}';
    }

    public static String niceString(Method method) {
        if (method.desc == null) {
            System.out.println("AAAA");
        }
        Descriptor descriptor = DescriptorParser.parseDescriptor(method.desc);

        StringBuilder sb = new StringBuilder();

        sb.append(descriptor.returnType).append(" ").append(method.name).append("(");
        for (int i = 0; i < descriptor.args.size(); i++) {
            sb.append(descriptor.args.get(i));

            if (method.args.size() > i) {
                sb.append(" ").append(method.args.get(i));
            }

            if (!(i == descriptor.args.size() - 1)) {
                sb.append(", ");
            }
        }
        sb.append(")");

        return sb.toString();
    }
}
