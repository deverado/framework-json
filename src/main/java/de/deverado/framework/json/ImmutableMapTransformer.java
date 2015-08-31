package de.deverado.framework.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.common.collect.ImmutableMap;

import java.io.IOException;
import java.util.Map;

/**
 * For deserialization - but doesn't pass on member types correctly.
 */
public class ImmutableMapTransformer extends
        StdDeserializer<ImmutableMap<?, ?>> implements TransformerPlugin,
        ResolvableDeserializer, ContextualDeserializer {

    public ImmutableMapTransformer() {
        super(ImmutableMap.class);
    }

    @Override
    public ImmutableMap<?, ?> deserialize(JsonParser jp,
            DeserializationContext ctxt) throws IOException,
            JsonProcessingException {

        Map<?, ?> map = (Map<?, ?>) ctxt.findRootValueDeserializer(
                ctxt.constructType(Map.class)).deserialize(jp, ctxt);
        return ImmutableMap.copyOf(map);
    }

    @Override
    public void resolve(DeserializationContext ctxt)
            throws JsonMappingException {

    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt,
            BeanProperty property) throws JsonMappingException {
        if (property == null) {
            return this;
        }
        return ctxt.findRootValueDeserializer(property.getType());
    }

    @Override
    public Class<?> getMixinAnnotation() {
        return ImmutableMapMixin.class;
    }

    @Override
    public Class<?> getMixinTarget() {
        return ImmutableMap.class;
    }

    @JsonDeserialize(using = ImmutableMapTransformer.class)
    public static class ImmutableMapMixin {

    }
}
