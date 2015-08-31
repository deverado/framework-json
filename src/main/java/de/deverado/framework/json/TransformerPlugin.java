package de.deverado.framework.json;

/**
 * Transformations are always applied during serialization.
 * 
 * @see SerializerPlugin for root-object-specific customization
 */
public interface TransformerPlugin {

	// TODO this should directly construct sth like flexjson.
	// something like the transformer in flexjson.
	// now it's also doing serializers.
	// Problem was that I didn't find where the HandlerInstantiator
	// was being constructed in Jackson! Seems to be hidden somewhere,
	// but had no time to check really - was without internet.
	Class<?> getMixinAnnotation();

	Class<?> getMixinTarget();
}
