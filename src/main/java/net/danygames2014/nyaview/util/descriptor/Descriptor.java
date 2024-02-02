package net.danygames2014.nyaview.util.descriptor;

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
}
