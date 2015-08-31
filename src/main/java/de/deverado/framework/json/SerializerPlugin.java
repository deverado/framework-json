package de.deverado.framework.json;

import java.io.Writer;

/**
 * A plugin can be used to customize serialization for root objects with
 * {@link SerializerHelper}.
 * 
 * @see TransformerPlugin for always applied transformations
 */
public interface SerializerPlugin {

    void serialize(Object o, Writer writer);

    Class<?> getTargetClass();
}
