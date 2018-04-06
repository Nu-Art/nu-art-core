package com.nu.art.core.generics;

import com.nu.art.core.exceptions.runtime.MUST_NeverHappenedException;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;

public class GenericParamExtractor {

	public static final GenericParamExtractor _GenericParamExtractor = new GenericParamExtractor();

	IGenericParamExtractor extractor;

	private GenericParamExtractor() {
		try {
			extractor = new GenericExtractor_Libcore(Class.forName("libcore.reflect.ParameterizedTypeImpl"));
			return;
		} catch (Throwable ignore) {}

		try {
			extractor = new GenericExtractor_Sun(Class.forName("sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl"));
			return;
		} catch (Throwable ignore) {}

		try {
			extractor = new GenericExtractor_Apache(Class.forName("org.apache.harmony.luni.lang.reflect.ImplForType"));
			return;
		} catch (Throwable ignore) {}

		throw new MUST_NeverHappenedException("Error extracting processor generic parameter fields for runtime use");
	}

	private Class<?> getListOfTypes()
		throws ClassNotFoundException {
		try {
			return Class.forName("sun.reflect.generics.reflectiveObjects.ListOfTypes");
		} catch (Throwable ignore) {}

		try {
			return Class.forName("org.apache.harmony.luni.lang.reflect.ListOfTypes");
		} catch (Throwable e) {
			throw new MUST_NeverHappenedException("Error extracting processor generic parameter fields for runtime use", e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T, K> Class<K> extractGenericType(Class<T> type, T instance, int index) {
		try {
			Class<?> aClass = instance.getClass();
			HashMap<String, Type> genericParamsTypes = new HashMap<>();
			do {
				TypeVariable<? extends Class<?>>[] superTypes = aClass.getSuperclass().getTypeParameters();
				Type genericSuperclass = aClass.getGenericSuperclass();
				if (genericSuperclass != Object.class) {
					Type[] e = extractor.getTypes(genericSuperclass);

					for (int i = 0; i < superTypes.length; i++) {
						if (!(e[i] instanceof TypeVariable)) {
							genericParamsTypes.put(superTypes[i].getName(), e[i]);
							continue;
						}

						TypeVariable typeVariable = (TypeVariable) e[i];
						Type remove = genericParamsTypes.remove(typeVariable.getName());
						genericParamsTypes.put(superTypes[i].getName(), remove);
					}
				}

				if (type.isInterface()) {
					Type[] genericInterfaces = aClass.getGenericInterfaces();
					for (Type genericInterface : genericInterfaces) {
						if (extractor.getRawType(genericInterface) != type)
							continue;

						Type[] e1 = extractor.getTypes(genericInterface);
						Type type1 = e1[index];
						if (type1 instanceof Class)
							return (Class<K>) type1;

						return (Class<K>) genericParamsTypes.get(((TypeVariable) type1).getName());
					}
				}

				aClass = aClass.getSuperclass();
			} while (aClass != type && aClass != Object.class);

			return (Class<K>) genericParamsTypes.get(type.getTypeParameters()[index].getName());
		} catch (Throwable e) {
			throw new MUST_NeverHappenedException("Error extracting processor generic parameter from processor type: " + instance.getClass(), e);
		}
	}
}
