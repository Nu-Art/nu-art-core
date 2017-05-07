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

package com.nu.art.software.core.tools;

import com.nu.art.software.core.exceptions.runtime.BadImplementationException;

/**
 * Created by TacB0sS on 10-Sep 2016.
 */

public class MathTools {

	private MathTools() {
		throw new BadImplementationException("Do not instantiate this object");
	}

	public static float calcAverage(float[] values) {
		float sum = 0;
		for (float value : values) {
			sum += value;
		}
		return sum / values.length;
	}
}
