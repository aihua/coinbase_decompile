package com.coinbase;

import com.coinbase.v1.deserializer.CurrencyUnitDeserializer;
import com.coinbase.v1.deserializer.MoneyDeserializer;
import com.coinbase.v1.serializer.CurrencyUnitSerializer;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

public class ObjectMapperProvider {
    final ObjectMapper defaultObjectMapper = createDefaultMapper();

    public ObjectMapper getContext(Class<?> cls) {
        return this.defaultObjectMapper;
    }

    public static ObjectMapper createDefaultMapper() {
        ObjectMapper result = new ObjectMapper();
        result.setSerializationInclusion(Include.NON_NULL);
        result.configure(SerializationFeature.INDENT_OUTPUT, false);
        result.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        result.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Money.class, new MoneyDeserializer());
        module.addDeserializer(CurrencyUnit.class, new CurrencyUnitDeserializer());
        module.addSerializer(CurrencyUnit.class, new CurrencyUnitSerializer());
        result.registerModule(module);
        result.registerModule(new JodaModule());
        return result;
    }
}
