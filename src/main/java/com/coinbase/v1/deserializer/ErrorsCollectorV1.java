package com.coinbase.v1.deserializer;

import com.fasterxml.jackson.databind.util.StdConverter;
import java.util.List;
import java.util.ListIterator;

public class ErrorsCollectorV1 extends StdConverter<List<String>, String> {
    public String convert(List<String> errors) {
        StringBuilder sb = new StringBuilder();
        ListIterator<String> it = errors.listIterator();
        while (it.hasNext()) {
            sb.append((String) it.next());
            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
