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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeTools {

	public static final long Millies = 1;

	public static final long Second = Millies * 1000;

	public static final long Minute = Second * 60;

	public static final long Hour = Minute * 60;

	public static final long Day = Hour * 24;

	public static final long Week = Day * 7;

	public static final long Month = (long) (Week * 4.23);

	public static final long Year = Month * 12;

	public static final Date getDateFromString(SimpleDateFormat dateFormat, String date)
			throws ParseException {
		return dateFormat.parse(date);
	}

	public static final String getDurationAsString(String format, long duration) {
		if (duration < 0) {
			format = "-" + format;
			duration *= -1;
		}

		String toRet = format.toLowerCase();

		int days = (int) (duration / Day);
		duration -= days * Day;

		int hours = (int) (duration / Hour);
		duration -= hours * Hour;

		int minutes = (int) (duration / Minute);
		duration -= minutes * Minute;

		int seconds = (int) (duration / Second);
		duration -= seconds * Second;

		int milliseconds = (int) duration;

		toRet = toRet.replace("dd", (days < 10 ? "0" : "") + days);
		toRet = toRet.replace("hh", (hours < 10 ? "0" : "") + hours);
		toRet = toRet.replace("mm", (minutes < 10 ? "0" : "") + minutes);
		toRet = toRet.replace("ss", (seconds < 10 ? "0" : "") + seconds);
		toRet = toRet.replace("ms", (milliseconds < 10 ? "00" : milliseconds < 100 ? "0" : "") + milliseconds);
		return toRet;
	}
}
