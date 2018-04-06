package com.nu.art.core.generics;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * Created by tacb0ss on 05/04/2018.
 */

public class GenericExtractor_Apache
	implements IGenericParamExtractor {

	private final Field rawField;
	private Field argsField;

	private Field resolvedTypesField;

	public GenericExtractor_Apache(Class<?> parametrizedType)
		throws NoSuchFieldException, ClassNotFoundException {
		argsField = parametrizedType.getDeclaredField("args");
		argsField.setAccessible(true);

		rawField = parametrizedType.getDeclaredField("rawTypeName");
		rawField.setAccessible(true);

		Class<?> listOfTypes = Class.forName("org.apache.harmony.luni.lang.reflect.ListOfTypes");
		resolvedTypesField = listOfTypes.getDeclaredField("resolvedTypes");
		resolvedTypesField.setAccessible(true);
	}

	@Override
	public Type[] getTypes(Type genericSuperclass)
		throws IllegalAccessException {
		return (Type[]) resolvedTypesField.get(argsField.get(genericSuperclass));
	}

	@Override
	public Type getRawType(Type genericInterface)
		throws IllegalAccessException, ClassNotFoundException {
		return Class.forName((String) rawField.get(genericInterface));
	}
}
