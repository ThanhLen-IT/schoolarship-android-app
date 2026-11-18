package com.example.myapplication;

final class TextJoiner {
    static String join(String separator, Iterable<String> values) {
        StringBuilder out = new StringBuilder();
        boolean first = true;
        for (String value : values) {
            if (!first) out.append(separator);
            first = false;
            out.append(value);
        }
        return out.toString();
    }
}
