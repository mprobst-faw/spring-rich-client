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
package org.springframework.richclient.application.support;

import static org.junit.jupiter.api.Assertions.fail;

import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.PageListener;
import org.springframework.richclient.application.config.ApplicationLifecycleAdvisor;
import org.springframework.richclient.application.config.ApplicationWindowConfigurer;
import org.springframework.richclient.application.config.DefaultApplicationLifecycleAdvisor;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.test.SpringRichTestCase;

/**
 * Test cases for {@link DefaultApplicationWindow}.
 * 
 * @author Andy DePue
 */
public class DefaultApplicationWindowTests extends SpringRichTestCase {

	@Test
	public void testRegressionFailureToRemovePageListener() {
		PageListener pageListener = (PageListener) EasyMock.createNiceMock(PageListener.class);
		EasyMock.replay(pageListener);
		DefaultApplicationWindow daw = new DefaultApplicationWindow();
		daw.addPageListener(pageListener);

		try {
			daw.removePageListener(pageListener);
		} catch (IllegalArgumentException iae) {
			iae.printStackTrace();
			fail("DefaultApplicationWindow.removePageListener threw IllegalArgumentException when removing a valid pageListener: "
					+ iae);
		}
	}

	/**
	 * Mocks out various methods on the returned ApplicationLifecycleAdvisor as they
	 * are not needed for the current unit test(s) and will throw exceptions without
	 * further setup for the test. If more unit tests are added to this class in the
	 * future, then the returned ApplicationLifecycleAdvisor should be revisited to
	 * ensure it still meets the needs of this test case.
	 */
	@Override
	protected ApplicationLifecycleAdvisor createApplicationLifecycleAdvisor() {
		return new DefaultApplicationLifecycleAdvisor() {
			@Override
			public void onPreWindowOpen(ApplicationWindowConfigurer configurer) {
			}

			@Override
			public void onCommandsCreated(ApplicationWindow window) {
			}

			@Override
			public ApplicationWindowCommandManager createWindowCommandManager() {
				return null;
			}

			@Override
			public CommandGroup getMenuBarCommandGroup() {
				return null;
			}

			@Override
			public CommandGroup getToolBarCommandGroup() {
				return null;
			}
		};
	}
}
