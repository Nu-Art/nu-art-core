package com.nu.art.core.generics;

import java.lang.reflect.Type;

public interface IGenericParamExtractor {

	Type[] getTypes(Type genericSuperclass)
		throws IllegalAccessException;

	Type getRawType(Type genericInterface)
		throws IllegalAccessException, ClassNotFoundException;
}
