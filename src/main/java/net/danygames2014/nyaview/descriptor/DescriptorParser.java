package net.danygames2014.nyaview.descriptor;

public class DescriptorParser {
    public static Descriptor parseDescriptor(String toParse) {
        Descriptor descriptor = new Descriptor();
        boolean descriptorEnd = false;
        boolean nextIsArray = false;

        for (int i = 0; i < toParse.length(); i++) {
            switch (toParse.charAt(i)) {
                // Start of descriptor
                case '(':
                    break;

                // End of descriptor
                case ')':
                    descriptorEnd = true;
                    break;

                // Divider
                case ';':
                    break;

                // Array
                case '[':
                    nextIsArray = true;
                    break;

                // TYPES
                // int
                case 'I':
                    addToDescriptor(descriptor, descriptorEnd, "int", nextIsArray);
                    nextIsArray = false;
                    break;

                // boolean
                case 'Z':
                    addToDescriptor(descriptor, descriptorEnd, "boolean", nextIsArray);
                    nextIsArray = false;
                    break;

                // float
                case 'F':
                    addToDescriptor(descriptor, descriptorEnd, "float", nextIsArray);
                    nextIsArray = false;
                    break;

                // double
                case 'D':
                    addToDescriptor(descriptor, descriptorEnd, "double", nextIsArray);
                    nextIsArray = false;
                    break;

                // char
                case 'C':
                    addToDescriptor(descriptor, descriptorEnd, "char", nextIsArray);
                    nextIsArray = false;
                    break;

                // long
                case 'J':
                    addToDescriptor(descriptor, descriptorEnd, "long", nextIsArray);
                    nextIsArray = false;
                    break;

                // byte
                case 'B':
                    addToDescriptor(descriptor, descriptorEnd, "byte", nextIsArray);
                    nextIsArray = false;
                    break;

                case 'S':
                    addToDescriptor(descriptor, descriptorEnd, "short", nextIsArray);
                    nextIsArray = false;
                    break;

                // class
                case 'L':
                    StringBuilder sb = new StringBuilder();

                    while (i < toParse.length()) {
                        if (toParse.charAt(i + 1) != ';') {
                            i++;
                            sb.append(toParse.charAt(i));
                        } else {
                            break;
                        }
                    }

                    addToDescriptor(descriptor, descriptorEnd, sb.toString(), nextIsArray);
                    nextIsArray = false;
                    break;

                // void
                case 'V':
                    addToDescriptor(descriptor, descriptorEnd, "void");
                    break;

                // Unknown
                default:
//                    NyaView.LOGGER.warning("Found Unknown character in descriptor : " + toParse.charAt(i));
                    break;
            }
        }
        return descriptor;
    }

    public static void addToDescriptor(Descriptor descriptor, boolean descriptorEnd, String value, boolean isArray) {
        if (isArray) {
            addToDescriptor(descriptor, descriptorEnd, value + "[]");
        } else {
            addToDescriptor(descriptor, descriptorEnd, value);
        }
    }

    public static void addToDescriptor(Descriptor descriptor, boolean descriptorEnd, String value) {
        if (descriptorEnd) {
            descriptor.returnType = value;
        } else {
            descriptor.args.add(value);
        }
    }
}
