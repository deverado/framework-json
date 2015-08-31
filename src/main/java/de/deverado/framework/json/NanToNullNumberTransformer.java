package de.deverado.framework.json;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Ensures that no values like Infinity, Nan and -Infinity appear in the json
 * output, as our jQuery client throws syntax exceptions for them.
 * <p>
 * CAREFUL, doesn't work for primitive types, so methods of to return e.g.
 * Integer, not int
 */
public class NanToNullNumberTransformer extends StdSerializer<Number> implements
        TransformerPlugin {

    private static final Logger log = LoggerFactory
            .getLogger(NanToNullNumberTransformer.class);

    public NanToNullNumberTransformer() {
        super(Number.class, false);
    }

    @Override
    public void serialize(Number object, JsonGenerator jgen,
                          SerializerProvider provider) throws IOException,
            JsonGenerationException {
        Number n = object;

        double doubleValue = n.doubleValue();
        if (Double.isNaN(doubleValue) || Double.isInfinite(doubleValue)) {
            jgen.writeNull();
        } else {
            if (n instanceof Double) {
                jgen.writeNumber((Double) n);
            } else if (n instanceof Float) {
                jgen.writeNumber((Float) n);
            } else if (n instanceof Integer) {
                jgen.writeNumber((Integer) n);
            } else if (n instanceof Long) {
                jgen.writeNumber((Long) n);
            } else if (n instanceof BigInteger) {
                jgen.writeNumber((BigInteger) n);
            } else if (n instanceof BigDecimal) {
                jgen.writeNumber((BigDecimal) n);
            } else {
                log.warn("Inexpected number type {}", n.getClass());
                jgen.writeNumber(String.valueOf(n));
            }
        }// bigdecimal has no inf/nan: else if (object instanceof BigDecimal)

    }

    @Override
    public Class<?> getMixinAnnotation() {
        return NanNullMixin.class;
    }

    @Override
    public Class<?> getMixinTarget() {
        return Number.class;
    }

    @JsonSerialize(using = NanToNullNumberTransformer.class)
    public static class NanNullMixin {

    }
}
