/*
 * Copyright 2002-2004 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.springframework.richclient.settings.j2seprefs;

import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;

/**
 * J2SE <code>PreferencesFactory</code> implementation for creating
 * <code>TransientPreference</code> instances.
 *
 * @author Peter De Bruycker
 */
public class TransientPreferencesFactory implements PreferencesFactory {

	@Override
	public Preferences systemRoot() {
		return new TransientPreferences();
	}

	@Override
	public Preferences userRoot() {
		return new TransientPreferences();
	}

}
