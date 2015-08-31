package de.deverado.framework.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Provider;

import java.io.Writer;

public abstract class AbstractJacksonSerializer implements SerializerPlugin {

	private ObjectMapper mapper = null;
	private final Provider<SerializerHelper> shProv;

	@Inject
	public AbstractJacksonSerializer(Provider<SerializerHelper> helper) {
		this.shProv = helper;
	}

	/**
	 * Used in lazy init - might get called more than once because no
	 * synchronization used!
	 * 
	 * @param serializer you configure
	 */
	protected abstract void configure(ObjectMapper serializer);

	@Override
	public void serialize(Object o, Writer writer) {
		lazyCreation();
		try {
			mapper.writeValue(writer, o);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void lazyCreation() {
		if (mapper == null) {
			ObjectMapper serializer = shProv.get().createSerializer();
			configure(serializer);
			this.mapper = serializer;
		}
	}
}
