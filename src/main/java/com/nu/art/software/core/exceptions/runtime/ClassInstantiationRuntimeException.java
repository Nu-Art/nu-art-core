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

package com.nu.art.software.core.exceptions.runtime;

public final class ClassInstantiationRuntimeException
		extends MUST_NeverHappenedException {

	private static final long serialVersionUID = 7557607466508340486L;

	public ClassInstantiationRuntimeException(String reason) {
		super(reason);
	}

	public ClassInstantiationRuntimeException(String reason, Throwable t) {
		super(reason, t);
	}

	public ClassInstantiationRuntimeException(Class<?> classType, Throwable t) {
		super("Error instantiating class type '" + classType.getName() + "'", t);
	}
}
