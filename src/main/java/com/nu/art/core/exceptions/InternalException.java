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

package com.nu.art.core.exceptions;

import java.util.Vector;

public class InternalException
		extends Exception {

	private static final long serialVersionUID = -7107592057924006287L;

	public static final Vector<InternalException> sessionExceptions = new Vector<>();

	public InternalException(String reason) {
		super(reason);
		InternalException.sessionExceptions.add(this);
	}

	public InternalException(String reason, Throwable e) {
		super(reason, e);
		InternalException.sessionExceptions.add(this);
	}
}
