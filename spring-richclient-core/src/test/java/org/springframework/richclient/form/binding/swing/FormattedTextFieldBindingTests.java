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
package org.springframework.richclient.form.binding.swing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.NumberFormat;

import javax.swing.JFormattedTextField;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.edt.GuiTask;
import org.junit.jupiter.api.Test;
import org.springframework.binding.form.FieldMetadata;
import org.springframework.binding.support.TestBean;

public class FormattedTextFieldBindingTests extends BindingAbstractTests {

	private static final Long initialValue = new Long(99);

	private JFormattedTextField ftc;

	private FormattedTextFieldBinding b;

	@Override
	protected TestBean createTestBean() {
		TestBean testBean = super.createTestBean();
		testBean.setNumberProperty(initialValue);
		return testBean;
	}

	@Override
	protected String setUpBinding() {
		GuiActionRunner.execute(new GuiTask() {
			@Override
			protected void executeInEDT() throws Throwable {
				b = new FormattedTextFieldBinding(new JFormattedTextField(NumberFormat.getInstance()), fm,
						"numberProperty", null);
				ftc = (JFormattedTextField) b.getControl();
			}
		});
		return "numberProperty";
	}

	@Test
	public void testInitialValue() {
		assertEquals(initialValue, vm.getValue());
		assertEquals(initialValue, ftc.getValue());
	}

	@Override
	@Test
	public void testComponentTracksEnabledChanges() {
		GuiActionRunner.execute(new GuiTask() {
			@Override
			protected void executeInEDT() throws Throwable {
				assertEquals(true, ftc.isEnabled());
				fm.setEnabled(false);
				assertEquals(false, ftc.isEnabled());
				fm.setEnabled(true);
				assertEquals(true, ftc.isEnabled());
			}
		});
	}

	@Override
	@Test
	public void testComponentTracksReadOnlyChanges() {
		GuiActionRunner.execute(new GuiTask() {
			@Override
			protected void executeInEDT() throws Throwable {
				FieldMetadata state = fm.getFieldMetadata("numberProperty");
				assertEquals(true, ftc.isEditable());
				state.setReadOnly(true);
				assertEquals(false, ftc.isEditable());
				state.setReadOnly(false);
				assertEquals(true, ftc.isEditable());
			}
		});
	}

	@Override
	@Test
	public void testComponentUpdatesValueModel() {
		Long one = new Long(1);
		Long two = new Long(2);
		ftc.setValue(one);
		assertTrue(vm.getValue() instanceof Long);
		assertTrue(one.compareTo((Long) vm.getValue()) == 0);
		ftc.setValue(null);
		assertEquals(null, vm.getValue());
		ftc.setValue(two);
		assertEquals(two, vm.getValue());
		ftc.setValue(null);
		assertEquals(null, vm.getValue());
	}

	@Override
	@Test
	public void testValueModelUpdatesComponent() {
		Long one = new Long(1);
		Long two = new Long(2);
		vm.setValue(one);
		assertEquals(one, ftc.getValue());
		vm.setValue(null);
		assertEquals(null, ftc.getValue());
		vm.setValue(two);
		assertEquals(two, ftc.getValue());
		vm.setValue(null);
		assertEquals(null, ftc.getValue());
	}
}
