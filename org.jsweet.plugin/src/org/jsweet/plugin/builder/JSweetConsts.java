package org.jsweet.plugin.builder;

import org.jsweet.JSweetConfig;

final class JSweetConsts {
	public static final String JSWEET_PROBLEM_MARKER_TYPE = "org.jsweet.plugin.jsweetProblem";

	public static final String[] DEFAULT_FAVORITES = {
			JSweetConfig.LANG_PACKAGE + "." + JSweetConfig.GLOBALS_CLASS_NAME + ".*",
			JSweetConfig.UTIL_CLASSNAME + ".*", JSweetConfig.DOM_PACKAGE + "." + JSweetConfig.GLOBALS_CLASS_NAME + ".*",
			JSweetConfig.LIBS_PACKAGE + ".jquery." + JSweetConfig.GLOBALS_CLASS_NAME + ".*",
			JSweetConfig.LIBS_PACKAGE + ".underscore." + JSweetConfig.GLOBALS_CLASS_NAME + ".*" };

	private JSweetConsts() {
	}

}
