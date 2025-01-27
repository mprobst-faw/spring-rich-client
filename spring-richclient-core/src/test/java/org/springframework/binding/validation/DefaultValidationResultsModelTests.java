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
package org.springframework.binding.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.binding.support.TestPropertyChangeListener;
import org.springframework.binding.validation.support.DefaultValidationMessage;
import org.springframework.binding.validation.support.DefaultValidationResults;
import org.springframework.binding.validation.support.DefaultValidationResultsModel;
import org.springframework.richclient.core.Severity;

/**
 * Tests for @link DefaultValidationResultsModel
 * 
 * @author Oliver Hutchison
 */
public class DefaultValidationResultsModelTests {
	private DefaultValidationResultsModel vrm;

	private TestPropertyChangeListener warnListener;

	private TestPropertyChangeListener infoListener;

	private TestPropertyChangeListener errorsListener;

	private TestValidationListener listener;

	private TestValidationListener field1Listener;

	private TestValidationListener nullListener;

	@BeforeEach
	public void setUp() {
		vrm = new DefaultValidationResultsModel();
		errorsListener = new TestPropertyChangeListener(ValidationResultsModel.HAS_ERRORS_PROPERTY);
		vrm.addPropertyChangeListener(ValidationResultsModel.HAS_ERRORS_PROPERTY, errorsListener);
		warnListener = new TestPropertyChangeListener(ValidationResultsModel.HAS_WARNINGS_PROPERTY);
		vrm.addPropertyChangeListener(ValidationResultsModel.HAS_WARNINGS_PROPERTY, warnListener);
		infoListener = new TestPropertyChangeListener(ValidationResultsModel.HAS_INFO_PROPERTY);
		vrm.addPropertyChangeListener(ValidationResultsModel.HAS_INFO_PROPERTY, infoListener);
		listener = new TestValidationListener();
		vrm.addValidationListener(listener);
		field1Listener = new TestValidationListener();
		vrm.addValidationListener("field1", field1Listener);
		nullListener = new TestValidationListener();
		vrm.addValidationListener(ValidationMessage.GLOBAL_PROPERTY, nullListener);
	}

	@Test
	public void testUpdatesFirePropertyChangeEvents() {
		vrm.updateValidationResults(getResults("field1", Severity.INFO));
		assertEquals(1, infoListener.eventCount());
		assertEquals(Boolean.FALSE, infoListener.lastEvent().getOldValue());
		assertEquals(Boolean.TRUE, infoListener.lastEvent().getNewValue());
		vrm.updateValidationResults(getResults("field1", Severity.INFO));
		assertEquals(1, infoListener.eventCount());

		vrm.updateValidationResults(getResults("field1", Severity.WARNING));
		assertEquals(1, warnListener.eventCount());
		assertEquals(Boolean.FALSE, warnListener.lastEvent().getOldValue());
		assertEquals(Boolean.TRUE, warnListener.lastEvent().getNewValue());
		assertEquals(2, infoListener.eventCount());
		assertEquals(Boolean.TRUE, infoListener.lastEvent().getOldValue());
		assertEquals(Boolean.FALSE, infoListener.lastEvent().getNewValue());
		vrm.updateValidationResults(getResults("field1", Severity.WARNING));
		assertEquals(1, warnListener.eventCount());

		vrm.updateValidationResults(getResults("field1", Severity.ERROR));
		assertEquals(1, errorsListener.eventCount());
		assertEquals(Boolean.FALSE, errorsListener.lastEvent().getOldValue());
		assertEquals(Boolean.TRUE, errorsListener.lastEvent().getNewValue());
		assertEquals(2, warnListener.eventCount());
		assertEquals(Boolean.TRUE, warnListener.lastEvent().getOldValue());
		assertEquals(Boolean.FALSE, warnListener.lastEvent().getNewValue());
		vrm.updateValidationResults(getResults("field1", Severity.ERROR));
		assertEquals(1, errorsListener.eventCount());

		vrm.clearAllValidationResults();
		assertEquals(2, infoListener.eventCount());
		assertEquals(Boolean.TRUE, infoListener.lastEvent().getOldValue());
		assertEquals(Boolean.FALSE, infoListener.lastEvent().getNewValue());
		assertEquals(2, warnListener.eventCount());
		assertEquals(Boolean.TRUE, warnListener.lastEvent().getOldValue());
		assertEquals(Boolean.FALSE, warnListener.lastEvent().getNewValue());
		assertEquals(2, errorsListener.eventCount());
		assertEquals(Boolean.TRUE, errorsListener.lastEvent().getOldValue());
		assertEquals(Boolean.FALSE, errorsListener.lastEvent().getNewValue());
	}

	@Test
	public void testEventsHaveCorectSource() {
		vrm.updateValidationResults(getResults("field1", Severity.ERROR));
		assertEquals(vrm, errorsListener.lastEvent().getSource());
		assertEquals(vrm, listener.lastResults());

		ValidationResultsModel delegateFor = new DefaultValidationResultsModel();
		vrm = new DefaultValidationResultsModel(delegateFor);
		vrm.addValidationListener(listener);
		vrm.addPropertyChangeListener(ValidationResultsModel.HAS_ERRORS_PROPERTY, errorsListener);
		vrm.updateValidationResults(getResults("field1", Severity.ERROR));
		assertEquals(delegateFor, errorsListener.lastEvent().getSource());
		assertEquals(delegateFor, listener.lastResults());
	}

	@Test
	public void testUpdatesFireValidationEvents() {
		vrm.updateValidationResults(getResults("field1", Severity.INFO));
		assertEquals(1, listener.eventCount());
		assertEquals(1, field1Listener.eventCount());
		assertEquals(0, nullListener.eventCount());
		assertEquals(vrm, listener.lastResults());
		assertEquals(vrm, field1Listener.lastResults());
		assertEquals(null, nullListener.lastResults());

		vrm.updateValidationResults(
				getResults("field1", Severity.INFO, ValidationMessage.GLOBAL_PROPERTY, Severity.ERROR));
		assertEquals(2, listener.eventCount());
		assertEquals(2, field1Listener.eventCount());
		assertEquals(1, nullListener.eventCount());
		assertEquals(vrm, nullListener.lastResults());

		vrm.clearAllValidationResults();
		assertEquals(3, listener.eventCount());
		assertEquals(3, field1Listener.eventCount());
		assertEquals(2, nullListener.eventCount());

		vrm.clearAllValidationResults();
		assertEquals(3, listener.eventCount());
		assertEquals(3, field1Listener.eventCount());
		assertEquals(2, nullListener.eventCount());

		vrm.updateValidationResults(getResults(ValidationMessage.GLOBAL_PROPERTY, Severity.INFO));
		assertEquals(4, listener.eventCount());
		assertEquals(3, field1Listener.eventCount());
		assertEquals(3, nullListener.eventCount());
	}

	/**
	 * Simply check if {@link DefaultValidationResultsModel} counts its messages
	 * correctly.
	 */
	@Test
	public void testMessageCount() {
		DefaultValidationResultsModel resultsModel = new DefaultValidationResultsModel();
		resultsModel.addMessage(new DefaultValidationMessage("property1", Severity.ERROR, "message1"));
		resultsModel.addMessage(new DefaultValidationMessage("property1", Severity.INFO, "message2"));
		resultsModel.addMessage(new DefaultValidationMessage("property2", Severity.ERROR, "message3"));
		assertEquals(3, resultsModel.getMessageCount(), "Number of messages should be 3");
		assertEquals(2, resultsModel.getMessageCount("property1"),
				"Number of messages registered for property1 should be 2");
		assertEquals(1, resultsModel.getMessageCount(Severity.INFO), "Number of messages flagged as INFO should be 1");
	}

	/**
	 * Check if adding a child triggers the parent to fire appropriate events.
	 */
	@Test
	public void testAddChildEvents() {
		DefaultValidationResultsModel childModel = new DefaultValidationResultsModel();

		childModel.addMessage(new DefaultValidationMessage("childProperty1", Severity.ERROR, "childErrorMessage1"));
		childModel.addMessage(new DefaultValidationMessage("childProperty1", Severity.WARNING, "childWarningMessage1"));
		childModel.addMessage(new DefaultValidationMessage("childProperty1", Severity.INFO, "childInfoMessage1"));
		vrm.add(childModel);
		assertEquals(1, listener.eventCount(), "ParentModel adds child with Error.");
		assertEquals(1, errorsListener.eventCount(), "ChildModel has ErrorMessage.");
		assertEquals(1, warnListener.eventCount(), "ChildModel has WarningMessage.");
		assertEquals(1, infoListener.eventCount(), "ChildModel has InfoMessage.");
		assertEquals(Boolean.TRUE, errorsListener.lastEvent().getNewValue(), "ChildModel has ErrorMessage.");
		assertEquals(Boolean.TRUE, warnListener.lastEvent().getNewValue(), "ChildModel has WarningMessage.");
		assertEquals(Boolean.TRUE, infoListener.lastEvent().getNewValue(), "ChildModel has InfoMessage.");
	}

	/**
	 * Check if adding a child triggers the parent to fire appropriate events.
	 */
	@Test
	public void testChildEvents() {
		DefaultValidationResultsModel childModel = new DefaultValidationResultsModel();

		vrm.add(childModel);
		childModel.addMessage(new DefaultValidationMessage("childProperty1", Severity.ERROR, "childErrorMessage1"));
		childModel.addMessage(new DefaultValidationMessage("childProperty1", Severity.WARNING, "childWarningMessage1"));
		childModel.addMessage(new DefaultValidationMessage("childProperty1", Severity.INFO, "childInfoMessage1"));
		assertEquals(3, listener.eventCount(), "Child added errorMessage, warningMessage and InfoMessage.");
		assertEquals(1, errorsListener.eventCount(), "ChildModel added ErrorMessage.");
		assertEquals(1, warnListener.eventCount(), "ChildModel added WarningMessage.");
		assertEquals(1, infoListener.eventCount(), "ChildModel added InfoMessage.");
		assertEquals(Boolean.TRUE, errorsListener.lastEvent().getNewValue(), "ChildModel added ErrorMessage.");
		assertEquals(Boolean.TRUE, warnListener.lastEvent().getNewValue(), "ChildModel added WarningMessage.");
		assertEquals(Boolean.TRUE, infoListener.lastEvent().getNewValue(), "ChildModel added InfoMessage.");
	}

	/**
	 * Check if adding a child triggers the parent to fire appropriate events.
	 */
	@Test
	public void testRemoveChildEvents() {
		DefaultValidationResultsModel childModel = new DefaultValidationResultsModel();

		childModel.addMessage(new DefaultValidationMessage("childProperty1", Severity.ERROR, "childErrorMessage1"));
		childModel.addMessage(new DefaultValidationMessage("childProperty1", Severity.WARNING, "childWarningMessage1"));
		childModel.addMessage(new DefaultValidationMessage("childProperty1", Severity.INFO, "childInfoMessage1"));
		vrm.add(childModel);
		vrm.remove(childModel);
		assertEquals(2, listener.eventCount(), "Child removed, revalidate.");
		assertEquals(2, errorsListener.eventCount(), "Child removed, revalidate ErrorMessages.");
		assertEquals(2, warnListener.eventCount(), "Child removed, revalidate WarningMessages.");
		assertEquals(2, infoListener.eventCount(), "Child removed, revalidate InfoMessages.");
		assertEquals(Boolean.FALSE, errorsListener.lastEvent().getNewValue(),
				"Child removed, revalidate ErrorMessages.");
		assertEquals(Boolean.FALSE, warnListener.lastEvent().getNewValue(),
				"Child removed, revalidate WarningMessages.");
		assertEquals(Boolean.FALSE, infoListener.lastEvent().getNewValue(), "Child removed, revalidate InfoMessages.");
	}

	private ValidationResults getResults(String field, Severity severity) {
		DefaultValidationResults vr = new DefaultValidationResults();
		vr.addMessage(field, severity, "");
		return vr;
	}

	private ValidationResults getResults(String field1, Severity severity1, String field2, Severity severity2) {
		DefaultValidationResults vr = new DefaultValidationResults();
		vr.addMessage(field1, severity1, "");
		vr.addMessage(field2, severity2, "");
		return vr;
	}

	public static class TestValidationListener implements ValidationListener {

		private ValidationResults lastResults;

		private int eventCount = 0;

		@Override
		public void validationResultsChanged(ValidationResults results) {
			lastResults = results;
			eventCount++;
		}

		public int eventCount() {
			return eventCount;
		}

		public ValidationResults lastResults() {
			return lastResults;
		}
	}
}
