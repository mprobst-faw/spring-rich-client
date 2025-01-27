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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

/**
 * @author Peter De Bruycker
 */
public class PreferencesSettingsFactoryTests {

	@Test
	public void testSetPreferencesFactory() {
		PreferencesSettingsFactory settingsFactory = new PreferencesSettingsFactory();

		assertNull(settingsFactory.getPreferencesFactory(), "PreferencesFactory must be initially null");

		TransientPreferencesFactory prefsFactory = new TransientPreferencesFactory();
		settingsFactory.setPreferencesFactory(prefsFactory);
		assertEquals(prefsFactory, settingsFactory.getPreferencesFactory());
	}

	@Test
	public void testCreate() {
		PreferencesSettingsFactory settingsFactory = new PreferencesSettingsFactory();
		TransientPreferencesFactory prefsFactory = new TransientPreferencesFactory();
		settingsFactory.setPreferencesFactory(prefsFactory);

		try {
			// id has not yet been set
			settingsFactory.createSettings("test-settings");
			fail("Should throw IllegalStateException");
		} catch (IllegalStateException e) {
			// test passes
		}

		settingsFactory.setId("test-id");
		PreferencesSettings settings = (PreferencesSettings) settingsFactory.createSettings("internal");
		assertNotNull(settings);

		TransientPreferences prefs = (TransientPreferences) settings.getPreferences();
		assertNotNull(prefs);
		assertEquals("internal", prefs.name());
		assertEquals("/test-id/internal", prefs.absolutePath());
	}

	@Test
	public void testCreateWithPath() {
		PreferencesSettingsFactory settingsFactory = new PreferencesSettingsFactory();
		TransientPreferencesFactory prefsFactory = new TransientPreferencesFactory();
		settingsFactory.setPreferencesFactory(prefsFactory);

		settingsFactory.setId("application.1.0");
		PreferencesSettings settings = (PreferencesSettings) settingsFactory.createSettings("user");
		TransientPreferences prefs = (TransientPreferences) settings.getPreferences();
		assertEquals("/application/1/0/user", prefs.absolutePath());
	}

	@Test
	public void testSetId() {
		PreferencesSettingsFactory settingsFactory = new PreferencesSettingsFactory();
		settingsFactory.setId("id");
		assertEquals("id", settingsFactory.getId());
	}

}
