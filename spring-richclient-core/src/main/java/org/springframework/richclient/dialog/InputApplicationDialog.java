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

import java.awt.Window;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import org.springframework.binding.form.ValidatingFormModel;
import org.springframework.richclient.core.Message;
import org.springframework.richclient.form.FormGuard;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.form.SimpleValidationResultsReporter;
import org.springframework.richclient.form.binding.swing.SwingBindingFactory;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.rules.closure.Closure;
import org.springframework.rules.constraint.Constraint;
import org.springframework.util.Assert;

/**
 * Simple input application dialog consisting of a label and a text field for
 * accepting input.
 *
 * @author Keith Donald
 */
public class InputApplicationDialog extends ApplicationDialog implements Messagable {

	private String inputLabelMessage = "dialog.input";

	private JComponent inputField;

	private Constraint inputConstraint;

	private Closure finishAction;

	private MessagePane reporter;

	private ValidatingFormModel formModel;

	public InputApplicationDialog(Object bean, String propertyName) {
		this(bean, propertyName, true);
	}

	public InputApplicationDialog(Object bean, String propertyName, boolean bufferChanges) {
		this(FormModelHelper.createFormModel(bean, bufferChanges), propertyName);
	}

	public InputApplicationDialog(ValidatingFormModel formModel, String propertyName) {
		this();
		this.formModel = formModel;
		setInputField(new SwingBindingFactory(formModel).createBinding(propertyName).getControl());
	}

	public InputApplicationDialog() {
		this(null, null, CloseAction.DISPOSE);
	}

	public InputApplicationDialog(String title, Window parent) {
		this(title, parent, CloseAction.DISPOSE);
	}

	public InputApplicationDialog(String title, Window parent, CloseAction closeAction) {
		super(title, parent, closeAction);
		setResizable(true);
	}

	public void setInputField(JComponent field) {
		Assert.notNull(field);
		this.inputField = field;
	}

	public void setInputLabelMessage(String inputLabel) {
		Assert.hasText(inputLabel, "The input label is required");
		this.inputLabelMessage = inputLabel;
	}

	public void setInputConstraint(Constraint constraint) {
		this.inputConstraint = constraint;
	}

	public void setFinishAction(Closure procedure) {
		this.finishAction = procedure;
	}

	protected MessagePane createMessagePane() {
		return new DefaultMessageAreaPane();
	}

	private MessagePane getMessagePane() {
		if (reporter == null) {
			reporter = createMessagePane();

			if (this.formModel != null) {
				new SimpleValidationResultsReporter(formModel.getValidationResults(), reporter);
				FormGuard formGuard = new FormGuard(formModel);
				formGuard.addGuarded(this, FormGuard.FORMERROR_GUARDED);
			}
		}
		return reporter;
	}

	@Override
	protected JComponent createDialogContentPane() {
		TableLayoutBuilder layoutBuilder = new TableLayoutBuilder();

		if (this.inputField == null) {
			this.inputField = getComponentFactory().createTextField();
		}
		// work around for bug in JFormattedTextField text field for selectAll
		if (inputField instanceof JFormattedTextField) {
			SelectAllBugFixer selectAllBugFixer = new SelectAllBugFixer();
			inputField.addFocusListener(selectAllBugFixer);
		}

		layoutBuilder.cell(createInputLabel(), TableLayoutBuilder.DEFAULT_LABEL_ATTRIBUTES);
		layoutBuilder.labelGapCol();
		layoutBuilder.cell(inputField);

		layoutBuilder.unrelatedGapRow();
		layoutBuilder.cell(getMessagePane().getControl());

		layoutBuilder.relatedGapRow();
		layoutBuilder.separator("");
		return layoutBuilder.getPanel();
	}

	protected JComponent createInputLabel() {
		return getComponentFactory().createLabelFor(inputLabelMessage, getInputField());
	}

	@Override
	protected boolean onFinish() {
		if (checkInputConstraint()) {
			onFinish(getInputValue());
			return true;
		}
		return false;
	}

	private boolean checkInputConstraint() {
		if (inputConstraint != null) {
			return inputConstraint.test(getInputValue());
		}

		return true;
	}

	private Object getInputValue() {
		if (inputField instanceof JFormattedTextField) {
			return ((JFormattedTextField) inputField).getValue();
		} else if (inputField instanceof JTextComponent) {
			return ((JTextComponent) inputField).getText();
		} else {
			throw new IllegalStateException("Input field type not supported");
		}
	}

	protected void onFinish(Object inputValue) {
		if (formModel != null) {
			formModel.commit();
		}
		if (finishAction != null) {
			finishAction.call(inputValue);
		}
	}

	public ValidatingFormModel getFormModel() {
		return formModel;
	}

	/**
	 * @return Returns the inputField.
	 */
	public JComponent getInputField() {
		return inputField;
	}

	private static class SelectAllBugFixer extends FocusAdapter {
		@Override
		public void focusGained(final FocusEvent evt) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					((JFormattedTextField) evt.getComponent()).selectAll();
				}
			});
		}
	}

	@Override
	public void setMessage(Message message) {
		getMessagePane().setMessage(message);
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		getMessagePane().addPropertyChangeListener(listener);
	}

	@Override
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		getMessagePane().addPropertyChangeListener(propertyName, listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		getMessagePane().removePropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		getMessagePane().removePropertyChangeListener(propertyName, listener);
	}
}