package de.deverado.framework.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Charsets;
import com.google.common.io.Closeables;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import de.deverado.framework.core.ParsingUtil;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Singleton
public class SerializerHelper {

    public final ObjectMapper DEFAULT_MAPPER;

    private final boolean prettyPrint;

    private final Set<TransformerPlugin> transformerPlugins;

    private final HashMap<Class<?>, SerializerPlugin> serializerPluginMap;

    private final static class InjectionHelper {
        @Named("serializerhelper.prettyprint")
        @Inject(optional = true)
        String prettyPrintStr = null;

        @Inject(optional = true)
        Set<SerializerPlugin> serializerPlugins;

        @Inject(optional = true)
        Set<TransformerPlugin> transformerPlugins;
    }

    @Inject
    public SerializerHelper(InjectionHelper injHelper) {
        prettyPrint = ParsingUtil.parseAsBoolean(injHelper.prettyPrintStr);
        if (injHelper.transformerPlugins != null) {
            this.transformerPlugins = injHelper.transformerPlugins;
        } else {
            this.transformerPlugins = Collections.emptySet();
        }
        this.serializerPluginMap = initSerializerPlugins(injHelper.serializerPlugins);

        ObjectMapper mapper = createSerializer();
        // FIXME root name wrapping doesn't work - maybe drop
        // check online resources first
        mapper.getSerializationConfig().withRootName("data");
        DEFAULT_MAPPER = mapper;
    }

    private HashMap<Class<?>, SerializerPlugin> initSerializerPlugins(
            Set<SerializerPlugin> serializerPlugins) {
        HashMap<Class<?>, SerializerPlugin> retval = new HashMap<Class<?>, SerializerPlugin>();
        if (serializerPlugins != null) {
            for (SerializerPlugin p : serializerPlugins) {
                retval.put(p.getTargetClass(), p);
            }
        }
        return retval;
    }

    private ObjectMapper initMapper(ObjectMapper objectMapper) {
        if (prettyPrint) {
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        }
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        for (TransformerPlugin p : transformerPlugins) {
            objectMapper.addMixInAnnotations(p.getMixinTarget(),
                    p.getMixinAnnotation());
        }
        return objectMapper;
    }

    public String serialize(Object o) {
        StringWriter writer = new StringWriter();
        serialize(o, writer);
        return writer.toString();
    }

    public void serialize(Object o, OutputStream os) {
        serialize(o, new OutputStreamWriter(os, Charsets.UTF_8));
    }

    public void serialize(Object o, Writer w) {
        if (o == null)
            return;
        Class<?> currClass = o.getClass();
        SerializerPlugin p;
        while (null == (p = getPluginForClassOrInterface(currClass))) {
            currClass = currClass.getSuperclass();
            if (currClass == null)
                break;
        }
        if (p != null) {
            p.serialize(o, w);
        } else {
            try {
                getShallowSerializer().writeValue(w, o);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private SerializerPlugin getPluginForClassOrInterface(
            Class<?> investigatedClass) {
        if (investigatedClass == null)
            return null;
        SerializerPlugin directHit = this.serializerPluginMap
                .get(investigatedClass);
        if (directHit != null) {
            return directHit;
        }
        for (Class<?> anIf : investigatedClass.getInterfaces()) {
            SerializerPlugin p = serializerPluginMap.get(anIf);
            if (p != null) {
                return p;
            }
        }
        return null;
    }

    /**
     * Get a shared serializer that wraps content in a object with a single data
     * attribute.
     * 
     * @return
     */
    public ObjectMapper getShallowSerializer() {
        return DEFAULT_MAPPER;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> readMap(String in) throws Exception {
        return getShallowSerializer().readValue(in, Map.class);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> readMap(InputStream in) throws Exception {
        boolean swallow = true;
        Map<String, Object> readValue;
        try {
            readValue = getShallowSerializer().readValue(in, Map.class);
            swallow = false;
        } finally {
            Closeables.close(in, swallow);
        }
        return readValue;
    }

    /**
     * Get a serializer with all transformation plugins without classname
     * printing.
     * 
     * @return
     */
    public ObjectMapper createSerializer() {
        return initMapper(new ObjectMapper());
    }

}
