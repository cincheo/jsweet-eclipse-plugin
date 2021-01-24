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

import static org.jsweet.plugin.preferences.Preferences.DEFAULT_PROFILE_NAME;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.jsweet.plugin.JSweetPlugin;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = JSweetPlugin.getDefault().getPreferenceStore();
		store.setDefault(Preferences.profiles(), "default");
		store.setDefault(Preferences.tsOutputDir(DEFAULT_PROFILE_NAME), ".generated");
		store.setDefault(Preferences.jsOutputDir(DEFAULT_PROFILE_NAME), "js");
	}

}
