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
package org.jsweet.plugin.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Constant definitions for plug-in preferences
 */
public interface Preferences {

	String COMPILER_SOURCE_FOLDERS = "compiler.sourceFolders";

	String COMPILER_SOURCE_INCLUDE_FILTER = "compiler.sourceIncludeFilter";

	String COMPILER_SOURCE_EXCLUDE_FILTER = "compiler.sourceExcludeFilter";
	
	String COMPILER_TYPESCRIPT_FOLDER = "compiler.typescriptFolder";

	String COMPILER_JAVASCRIPT_FOLDER = "compiler.javascriptFolder";

	String COMPILER_CANDY_JS_FOLDER = "compiler.candyJsFolder";

	String COMPILER_BUNDLES_DIRECTORY = "compiler.bundlesDirectory";

	String COMPILER_BUNDLE = "compiler.bundle";

	String COMPILER_DEBUG_MODE = "compiler.debugMode";

	String COMPILER_MODULE_KIND = "compiler.moduleKind";

	String COMPILER_DEBUG_MODE_JAVA = "java";

	String COMPILER_DEBUG_MODE_TYPESCRIPT = "ts";

	static String getSourceFolders(IProject project) {
		IPreferenceStore projectPreferenceStore = new ProjectPreferenceStore(project);
		return projectPreferenceStore.getString(Preferences.COMPILER_SOURCE_FOLDERS);
	}

	static String getSourceIncludeFilter(IProject project) {
		IPreferenceStore projectPreferenceStore = new ProjectPreferenceStore(project);
		return projectPreferenceStore.getString(Preferences.COMPILER_SOURCE_INCLUDE_FILTER);
	}

	static String getSourceExcludeFilter(IProject project) {
		IPreferenceStore projectPreferenceStore = new ProjectPreferenceStore(project);
		return projectPreferenceStore.getString(Preferences.COMPILER_SOURCE_EXCLUDE_FILTER);
	}
	
	static String getTsOutputFolder(IProject project) {
		IPreferenceStore projectPreferenceStore = new ProjectPreferenceStore(project);
		return projectPreferenceStore.getString(Preferences.COMPILER_TYPESCRIPT_FOLDER);
	}

	static String getCandyJsOutputFolder(IProject project) {
		IPreferenceStore projectPreferenceStore = new ProjectPreferenceStore(project);
		return projectPreferenceStore.getString(Preferences.COMPILER_CANDY_JS_FOLDER);
	}

	static String getModuleKind(IProject project) {
		IPreferenceStore projectPreferenceStore = new ProjectPreferenceStore(project);
		return projectPreferenceStore.getString(Preferences.COMPILER_MODULE_KIND);
	}

	static String getJsOutputFolder(IProject project) {
		IPreferenceStore projectPreferenceStore = new ProjectPreferenceStore(project);
		return projectPreferenceStore.getString(Preferences.COMPILER_JAVASCRIPT_FOLDER);
	}

	static String getBundlesDirectory(IProject project) {
		IPreferenceStore projectPreferenceStore = new ProjectPreferenceStore(project);
		return projectPreferenceStore.getString(Preferences.COMPILER_BUNDLES_DIRECTORY);
	}

	static boolean getBundle(IProject project) {
		IPreferenceStore projectPreferenceStore = new ProjectPreferenceStore(project);
		return projectPreferenceStore.getBoolean(Preferences.COMPILER_BUNDLE);
	}

	static String getDebugMode(IProject project) {
		IPreferenceStore projectPreferenceStore = new ProjectPreferenceStore(project);
		return projectPreferenceStore.getString(Preferences.COMPILER_DEBUG_MODE);
	}

	static boolean isJavaDebugMode(IProject project) {
		return Preferences.COMPILER_DEBUG_MODE_JAVA.equals(Preferences.getDebugMode(project));
	}

	// static IEclipsePreferences getTypescriptPluginPreferences(IProject
	// project) {
	// IScopeContext projectScope = new ProjectScope(project);
	// return projectScope.getNode("com.palantir.typescript");
	// }

	// String TS_PLUGIN_SRC_FOLDER = "build.path.sourceFolder";
	// String TS_PLUGIN_OUTPUT_FOLDER = "compiler.outputDirOption";

}
