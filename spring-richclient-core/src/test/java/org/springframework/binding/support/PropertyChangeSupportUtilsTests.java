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
package org.springframework.binding.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.junit.jupiter.api.Test;
import org.springframework.binding.value.PropertyChangePublisher;
import org.springframework.binding.value.support.AbstractPropertyChangePublisher;

/**
 * Tests class {@link PropertyChangeSupportUtils}.
 * 
 * @author Oliver Hutchison
 */
public class PropertyChangeSupportUtilsTests {

	/**
	 * Checks that #supportsBoundProperties detects observable classes.
	 */
	@Test
	public void testDetectObservableClasses() {
		Class[] observableClasses = new Class[] { PropertyChangePublisherImpl.class, StandardJavaBeanImpl.class };
		for (int i = 0; i < observableClasses.length; i++) {
			Class beanClass = observableClasses[i];
			assertTrue(PropertyChangeSupportUtils.supportsBoundProperties(beanClass),
					"Could not detect that the class supports bound properties.");
		}
	}

	/**
	 * Checks that #supportsBoundProperties rejects unobservable classes.
	 */
	@Test
	public void testRejectUnobservableClasses() {
		Class[] unobservableClasses = new Class[] { Object.class, int.class };
		for (int i = 0; i < unobservableClasses.length; i++) {
			Class beanClass = unobservableClasses[i];
			assertFalse(PropertyChangeSupportUtils.supportsBoundProperties(beanClass),
					"Failed to reject a class that supports no bound properties.");
		}
	}

	@Test
	public void testAddRemovePropertyChangeListener() {
		final PropertyChangeListener listener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
			}
		};

		PropertyChangePublisherImpl bean1 = new PropertyChangePublisherImpl();
		PropertyChangeSupportUtils.addPropertyChangeListener(bean1, "propertName", listener);
		PropertyChangeListener[] listeners = bean1.getPropertyChangeListeners("propertName");
		assertEquals(1, listeners.length);
		assertSame(listener, listeners[0]);
		PropertyChangeSupportUtils.removePropertyChangeListener(bean1, "propertName", listener);
		listeners = bean1.getPropertyChangeListeners("propertName");
		assertEquals(0, listeners.length);

		StandardJavaBeanImpl bean2 = new StandardJavaBeanImpl();
		PropertyChangeSupportUtils.addPropertyChangeListener(bean2, "propertName", listener);
		listeners = bean2.getPropertyChangeListeners("propertName");
		assertEquals(1, listeners.length);
		assertSame(listener, listeners[0]);
		PropertyChangeSupportUtils.removePropertyChangeListener(bean2, "propertName", listener);
		listeners = bean2.getPropertyChangeListeners("propertName");
		assertEquals(0, listeners.length);
	}

	private class PropertyChangePublisherImpl extends AbstractPropertyChangePublisher
			implements PropertyChangePublisher {
	}

	private class StandardJavaBeanImpl extends PropertyChangeSupport {
		public StandardJavaBeanImpl() {
			super("whatever");
		}
	}
}
