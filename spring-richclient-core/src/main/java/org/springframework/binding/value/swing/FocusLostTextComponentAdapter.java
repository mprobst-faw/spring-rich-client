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
package org.springframework.binding.value.swing;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.text.JTextComponent;

import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.AbstractValueModelAdapter;
import org.springframework.util.Assert;

public class FocusLostTextComponentAdapter extends AbstractValueModelAdapter implements FocusListener {
	private final JTextComponent control;

	public FocusLostTextComponentAdapter(JTextComponent component, ValueModel valueModel) {
		super(valueModel);
		Assert.notNull(component);
		this.control = component;
		this.control.addFocusListener(this);
		initalizeAdaptedValue();
	}

	@Override
	protected void valueModelValueChanged(Object value) {
		control.setText((String) value);
	}

	@Override
	public void focusLost(FocusEvent e) {
		adaptedValueChanged(control.getText());
	}

	@Override
	public void focusGained(FocusEvent e) {
	}
}