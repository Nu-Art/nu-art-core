/*
 * The core of the core of all my projects!
 *
 * Copyright (C) 2017  Adam van der Kruk aka TacB0sS
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

package com.nu.art.software.core.repository;

import java.util.HashMap;

public final class Repository
		implements RepositoryDelegator {

	private final HashMap<RepositoryKey<?>, Object> map = new HashMap<>();

	public Repository(Repository repository) {
		embrace(repository);
	}

	public Repository() {}

	@Override
	public final <ItemType> void put(RepositoryKey<ItemType> key, ItemType value) {
		map.put(key, value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public final <ItemType> ItemType get(RepositoryKey<ItemType> key) {
		return (ItemType) map.get(key);
	}

	@Override
	@SuppressWarnings("unchecked")
	public final <ItemType> ItemType getOrThrowException(RepositoryKey<ItemType> key) {
		ItemType item = (ItemType) map.get(key);
		if (item == null) {
			throw new RepositoryItemNotFoundException(key);
		}
		return item;
	}

	public final void embrace(Repository repository) {
		this.map.putAll(repository.map);
	}

	public <ItemType> void remove(RepositoryKey<ItemType> key) {
		map.remove(key);
	}
}
