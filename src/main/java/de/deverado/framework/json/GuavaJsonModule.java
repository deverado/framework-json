package de.deverado.framework.json;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

/**
 * Provides json serializability for Guava immutable maps.
 * <ul>
 * <li> {@link TransformerPlugin} for ImmutableMap
 * </ul>
 */
public class GuavaJsonModule extends AbstractModule {

    @Override
    protected void configure() {
        Multibinder.newSetBinder(binder(), TransformerPlugin.class)
                .addBinding().to(ImmutableMapTransformer.class);
    }
}
