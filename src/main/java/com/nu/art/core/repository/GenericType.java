/*
 * The core of the core of all my projects!
 *
 * Copyright (C) 2018  Adam van der Kruk aka TacB0sS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nu.art.core.repository;

import java.util.Vector;

public abstract class GenericType<ItemType> {

	private static Vector<GenericType<?>> AvailableTypes;

	@SuppressWarnings("rawtypes")
	public static final GenericType[] getAvailableTypes() {
		return AvailableTypes.toArray(new GenericType[AvailableTypes.size()]);
	}

	/**
	 * The default name the repository would be named.
	 */
	protected final String name;

	/**
	 * Defines a default item for this Type.
	 *
	 * @param name The name of the type
	 */
	public GenericType(String name) {
		if (name == null) {
			throw new NullPointerException("name == null");
		}
		if (GenericType.AvailableTypes == null) {
			GenericType.AvailableTypes = new Vector<>();
		}

		GenericType.AvailableTypes.add(this);
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof GenericType<?> && name.equals(((GenericType<?>) obj).name);
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		return name;
	}
}
