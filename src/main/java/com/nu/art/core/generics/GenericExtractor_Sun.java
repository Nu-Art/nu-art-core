package com.nu.art.core.generics;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * Created by tacb0ss on 05/04/2018.
 */

@SuppressWarnings("unchecked")
public class GenericExtractor_Sun
	implements IGenericParamExtractor {

	private final Field actualTypeField;
	private final Field rawField;

	GenericExtractor_Sun(Class<?> parametrizedType)
		throws NoSuchFieldException, ClassNotFoundException {

		actualTypeField = parametrizedType.getDeclaredField("actualTypeArguments");
		actualTypeField.setAccessible(true);

		rawField = parametrizedType.getDeclaredField("rawType");
		rawField.setAccessible(true);
	}

	@Override
	public Type[] getTypes(Type genericSuperclass)
		throws IllegalAccessException {
		return (Type[]) actualTypeField.get(genericSuperclass);
	}

	@Override
	public Type getRawType(Type genericInterface)
		throws IllegalAccessException {
		return (Type) rawField.get(genericInterface);
	}
}
