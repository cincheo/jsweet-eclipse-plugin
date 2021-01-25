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

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Constant definitions for plug-in preferences
 */
public class Preferences {

	public static final String DEFAULT_PROFILE_NAME = "default";

	private static final String COMPILER_PROFILES = "compiler.profiles";

	private static final String COMPILER_SOURCE_FOLDERS = "compiler.sourceFolders";

	private static final String COMPILER_SOURCE_INCLUDE_FILTER = "compiler.sourceIncludeFilter";

	private static final String COMPILER_SOURCE_EXCLUDE_FILTER = "compiler.sourceExcludeFilter";

	private static final String COMPILER_TYPESCRIPT_FOLDER = "compiler.typescriptFolder";

	private static final String COMPILER_JAVASCRIPT_FOLDER = "compiler.javascriptFolder";

	private static final String COMPILER_CANDY_JS_FOLDER = "compiler.candyJsFolder";

	private static final String COMPILER_BUNDLES_DIRECTORY = "compiler.bundlesDirectory";

	private static final String COMPILER_BUNDLE = "compiler.bundle";

	private static final String COMPILER_DECLARATION_DIRECTORY = "compiler.declarationDirectory";

	private static final String COMPILER_DECLARATION = "compiler.declaration";

	private static final String COMPILER_NO_JS = "compiler.nojs";

	private static final String COMPILER_DEBUG_MODE = "compiler.debugMode";

	private static final String COMPILER_MODULE_KIND = "compiler.moduleKind";

	private static final String COMPILER_ECMA_TARGET_VERSION = "compiler.target";

	public static String getProfilePrefix(String profile) {
		return StringUtils.isBlank(profile) || DEFAULT_PROFILE_NAME.equals(profile) ? "" : profile + ".";
	}

	public static String profiles() {
		return Preferences.COMPILER_PROFILES;
	}

	public static String getProfiles(IProject project) {
		IPreferenceStore projectPreferenceStore = new ProjectPreferenceStore(project);
		return projectPreferenceStore.getString(Preferences.COMPILER_PROFILES);
	}

	public static String[] parseProfiles(IProject project) {
		IPreferenceStore projectPreferenceStore = new ProjectPreferenceStore(project);
		String profiles = projectPreferenceStore.getString(Preferences.COMPILER_PROFILES);
		return (profiles == null ? "" : profiles).split(";");
	}

	public static String srcDirs(final String profile) {
		return getProfilePrefix(profile) + Preferences.COMPILER_SOURCE_FOLDERS;
	}

	public static String getSourceFolders(IProject project, String profile) {
		IPreferenceStore projectPreferenceStore = new ProjectPreferenceStore(project);
		return projectPreferenceStore.getString(getProfilePrefix(profile) + Preferences.COMPILER_SOURCE_FOLDERS);
	}

	public static String srcIncludeFilter(final String profile) {
		return getProfilePrefix(profile) + Preferences.COMPILER_SOURCE_INCLUDE_FILTER;
	}

	public static String getSourceIncludeFilter(IProject project, String profile) {
		IPreferenceStore projectPreferenceStore = new ProjectPreferenceStore(project);
		return projectPreferenceStore.getString(getProfilePrefix(profile) + Preferences.COMPILER_SOURCE_INCLUDE_FILTER);
	}

	public static String srcExcludeFilter(final String profile) {
		return getProfilePrefix(profile) + Preferences.COMPILER_SOURCE_EXCLUDE_FILTER;
	}

	public static String getSourceExcludeFilter(IProject project, String profile) {
		IPreferenceStore projectPreferenceStore = new ProjectPreferenceStore(project);
		return projectPreferenceStore.getString(getProfilePrefix(profile) + Preferences.COMPILER_SOURCE_EXCLUDE_FILTER);
	}

	public static String tsOutputDir(final String profile) {
		return getProfilePrefix(profile) + Preferences.COMPILER_TYPESCRIPT_FOLDER;
	}

	public static String getTsOutputFolder(IProject project, String profile) {
		IPreferenceStore projectPreferenceStore = new ProjectPreferenceStore(project);
		return projectPreferenceStore.getString(getProfilePrefix(profile) + Preferences.COMPILER_TYPESCRIPT_FOLDER);
	}

	public static String candyJsOutputDir(final String profile) {
		return getProfilePrefix(profile) + Preferences.COMPILER_CANDY_JS_FOLDER;
	}

	public static String getCandyJsOutputFolder(IProject project, String profile) {
		IPreferenceStore projectPreferenceStore = new ProjectPreferenceStore(project);
		return projectPreferenceStore.getString(getProfilePrefix(profile) + Preferences.COMPILER_CANDY_JS_FOLDER);
	}

	public static String moduleKind(String profile) {
		return getProfilePrefix(profile) + Preferences.COMPILER_MODULE_KIND;
	}

	public static String getModuleKind(IProject project, String profile) {
		IPreferenceStore projectPreferenceStore = new ProjectPreferenceStore(project);
		return projectPreferenceStore.getString(getProfilePrefix(profile) + Preferences.COMPILER_MODULE_KIND);
	}
	
	public static String ecmaTargetVersion(String profile) {
		return getProfilePrefix(profile) + Preferences.COMPILER_ECMA_TARGET_VERSION;
	}

	public static String getEcmaTargetVersion(IProject project, String profile) {
		IPreferenceStore projectPreferenceStore = new ProjectPreferenceStore(project);
		return projectPreferenceStore.getString(getProfilePrefix(profile) + Preferences.COMPILER_ECMA_TARGET_VERSION);
	}

	public static String jsOutputDir(final String profile) {
		return getProfilePrefix(profile) + Preferences.COMPILER_JAVASCRIPT_FOLDER;
	}

	public static String getJsOutputFolder(IProject project, String profile) {
		IPreferenceStore projectPreferenceStore = new ProjectPreferenceStore(project);
		return projectPreferenceStore.getString(getProfilePrefix(profile) + Preferences.COMPILER_JAVASCRIPT_FOLDER);
	}

	public static String bundlesDir(final String profile) {
		return getProfilePrefix(profile) + Preferences.COMPILER_BUNDLES_DIRECTORY;
	}

	public static String getBundlesDirectory(IProject project, String profile) {
		IPreferenceStore projectPreferenceStore = new ProjectPreferenceStore(project);
		return projectPreferenceStore.getString(getProfilePrefix(profile) + Preferences.COMPILER_BUNDLES_DIRECTORY);
	}

	public static String bundle(String profile) {
		return getProfilePrefix(profile) + Preferences.COMPILER_BUNDLE;
	}

	public static boolean getBundle(IProject project, String profile) {
		IPreferenceStore projectPreferenceStore = new ProjectPreferenceStore(project);
		return projectPreferenceStore.getBoolean(getProfilePrefix(profile) + Preferences.COMPILER_BUNDLE);
	}

	public static String declaration(String profile) {
		return getProfilePrefix(profile) + Preferences.COMPILER_DECLARATION;
	}

	public static boolean getDeclaration(IProject project, String profile) {
		IPreferenceStore projectPreferenceStore = new ProjectPreferenceStore(project);
		return projectPreferenceStore.getBoolean(getProfilePrefix(profile) + Preferences.COMPILER_DECLARATION);
	}

	public static String declarationDir(final String profile) {
		return getProfilePrefix(profile) + Preferences.COMPILER_DECLARATION_DIRECTORY;
	}

	public static String getDeclarationDirectory(IProject project, String profile) {
		IPreferenceStore projectPreferenceStore = new ProjectPreferenceStore(project);
		return projectPreferenceStore.getString(getProfilePrefix(profile) + Preferences.COMPILER_DECLARATION_DIRECTORY);
	}

	public static String debugMode(String profile) {
		return getProfilePrefix(profile) + Preferences.COMPILER_DEBUG_MODE;
	}

	public static boolean isDebugMode(IProject project, String profile) {
		IPreferenceStore projectPreferenceStore = new ProjectPreferenceStore(project);
		return projectPreferenceStore.getBoolean(getProfilePrefix(profile) + Preferences.COMPILER_DEBUG_MODE);
	}

	public static String noJs(String profile) {
		return getProfilePrefix(profile) + Preferences.COMPILER_NO_JS;
	}

	public static boolean getNoJs(IProject project, String profile) {
		IPreferenceStore projectPreferenceStore = new ProjectPreferenceStore(project);
		return projectPreferenceStore.getBoolean(getProfilePrefix(profile) + Preferences.COMPILER_NO_JS);
	}

}
