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
package org.springframework.richclient.dialog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.richclient.core.DefaultMessage;
import org.springframework.richclient.core.Severity;

/**
 * @author Peter De Bruycker
 */
public class DefaultMessageAreaModelTests {

	private TestMessageAreaChangeListener ml1;

	private TestMessageAreaChangeListener ml2;

	@Test
	public void testAddAndRemoveMessageListener() {
		DefaultMessageAreaModel buffer = new DefaultMessageAreaModel();

		buffer.addPropertyChangeListener(ml1);
		buffer.setMessage(new DefaultMessage("Msg"));
		assertEquals(buffer, ml1.lastUpdated);

		ml1.lastUpdated = null;
		buffer.removePropertyChangeListener(ml1);
		buffer.setMessage(new DefaultMessage("Msg1"));
		assertEquals(null, ml1.lastUpdated);

		buffer.addPropertyChangeListener(Messagable.MESSAGE_PROPERTY, ml1);
		buffer.setMessage(new DefaultMessage("Msg"));
		assertEquals(buffer, ml1.lastUpdated);

		ml1.lastUpdated = null;
		buffer.removePropertyChangeListener(Messagable.MESSAGE_PROPERTY, ml1);
		buffer.setMessage(new DefaultMessage("Msg1"));
		assertEquals(null, ml1.lastUpdated);

		buffer.addPropertyChangeListener("Some Other Property", ml1);
		buffer.setMessage(new DefaultMessage("Msg"));
		assertEquals(null, ml1.lastUpdated);
	}

	@Test
	public void testSetMessage() {
		String msg = "Info message";

		DefaultMessageAreaModel buffer = new DefaultMessageAreaModel();
		buffer.addPropertyChangeListener(ml1);
		buffer.addPropertyChangeListener(ml2);

		buffer.setMessage(new DefaultMessage(msg));
		assertMessageAndSeveritySet(buffer, msg, Severity.INFO);

		// with delegate
		DefaultMessageAreaModel delegate = new DefaultMessageAreaModel();
		buffer = new DefaultMessageAreaModel(delegate);
		buffer.addPropertyChangeListener(ml1);
		buffer.addPropertyChangeListener(ml2);
		buffer.setMessage(new DefaultMessage(msg));
		assertMessageAndSeveritySet(buffer, msg, Severity.INFO);
	}

	private void assertMessageAndSeveritySet(DefaultMessageAreaModel buffer, String msg, Severity severity) {
		assertEquals(msg, buffer.getMessage().getMessage(), "message was not set");
		assertEquals(severity, buffer.getMessage().getSeverity(), "severity must be info");
		assertEquals(buffer.getDelegateFor(), ml1.lastUpdated, "listener not notified");
		assertEquals(buffer.getDelegateFor(), ml2.lastUpdated, "listener not notified");
	}

	@Test
	public void testSetErrorMessage() {
		String msg = "Error message";

		DefaultMessageAreaModel buffer = new DefaultMessageAreaModel();
		buffer.addPropertyChangeListener(ml1);
		buffer.addPropertyChangeListener(ml2);

		buffer.setMessage(new DefaultMessage(msg, Severity.ERROR));
		assertMessageAndSeveritySet(buffer, msg, Severity.ERROR);

		// with delegate
		DefaultMessageAreaModel delegate = new DefaultMessageAreaModel();
		buffer = new DefaultMessageAreaModel(delegate);
		buffer.addPropertyChangeListener(ml1);
		buffer.addPropertyChangeListener(ml2);

		buffer.setMessage(new DefaultMessage(msg, Severity.ERROR));
		assertMessageAndSeveritySet(buffer, msg, Severity.ERROR);
	}

	@Test
	public void testSetSameMessage() {
		String msg = "Test message";
		Severity severity = Severity.WARNING;

		DefaultMessageAreaModel buffer = new DefaultMessageAreaModel();
		buffer.addPropertyChangeListener(ml1);
		buffer.addPropertyChangeListener(ml2);

		buffer.setMessage(new DefaultMessage(msg, severity));

		assertMessageAndSeveritySet(buffer, msg, severity);
		ml1.lastUpdated = null;
		ml2.lastUpdated = null;

		// and again
		buffer.setMessage(new DefaultMessage(msg, severity));
		assertNull(ml1.lastUpdated);
		assertNull(ml2.lastUpdated);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@BeforeEach
	protected void setUp() throws Exception {
		ml1 = new TestMessageAreaChangeListener();
		ml2 = new TestMessageAreaChangeListener();
	}

	private static class TestMessageAreaChangeListener implements PropertyChangeListener {

		private Messagable lastUpdated;

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			this.lastUpdated = (Messagable) evt.getSource();

		}

	}
}