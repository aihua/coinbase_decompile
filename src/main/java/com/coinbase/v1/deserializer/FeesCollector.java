package com.coinbase.v1.deserializer;

import com.fasterxml.jackson.databind.util.StdConverter;
import java.util.HashMap;
import java.util.List;
import org.joda.money.Money;

public class FeesCollector extends StdConverter<List<HashMap<String, Money>>, HashMap<String, Money>> {
    public HashMap<String, Money> convert(List<HashMap<String, Money>> value) {
        HashMap<String, Money> result = new HashMap();
        for (HashMap<String, Money> map : value) {
            result.putAll(map);
        }
        return result;
    }
}
