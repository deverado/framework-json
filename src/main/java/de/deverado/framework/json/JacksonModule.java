package de.deverado.framework.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class JacksonModule extends AbstractModule {

	@Override
	protected void configure() {

	}

	@Provides
	@Singleton
	public ObjectMapper provideMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.getSerializationConfig().with(
				SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
		return mapper;
	}

}
