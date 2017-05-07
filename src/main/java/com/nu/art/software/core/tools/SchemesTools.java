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

import com.nu.art.software.core.utils.RegexAnalyzer;

public class SchemesTools {

	public static final RegexAnalyzer FTP_RegexAnalizer = new RegexAnalyzer("ftp://");

	public static final RegexAnalyzer HTTP_RegexAnalizer = new RegexAnalyzer("(?:http|https)://");

	public static final RegexAnalyzer LocalOrUNC_RegexAnalizer = new RegexAnalyzer("(?:([a-zA-Z]{1}:/)|(//\\d+.\\d+.\\d+" + "" +
			".\\d+/[a-zA-Z]{1}\\$/))");
}
