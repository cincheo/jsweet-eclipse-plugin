/*
 * Copyright 2015 CINCHEO SAS <renaud.pawlak@cincheo.fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jsweet.plugin;

import org.eclipse.core.runtime.Status;

public class Log {

	private Log() {
	}

	public static void info(String msg) {
		log(Status.INFO, msg, null);
	}

	public static void error(String msg, Throwable t) {
		log(Status.ERROR, msg, t);
	}

	public static void error(Throwable t) {
		log(Status.ERROR, t.getMessage(), t);
	}

	public static void error(String msg) {
		log(Status.ERROR, msg, null);
	}

	public static void warning(String msg, Throwable t) {
		log(Status.WARNING, msg, t);
	}

	public static void warning(Throwable t) {
		log(Status.WARNING, t.getMessage(), t);
	}

	public static void warning(String msg) {
		log(Status.WARNING, msg, null);
	}

	public static void log(int severity, String msg, Throwable t) {
		JSweetPlugin.getDefault().getLog().log(new Status(severity, JSweetPlugin.ID, Status.OK, msg, t));
	}
}
