/*
 * Copyright 2002-2004 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.richclient.form.binding.swing;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.binding.support.CustomBinding;

/**
 * @author Mauro Ransolin
 */
public class LabelBinding extends CustomBinding {

	private final JLabel label;

	public LabelBinding(JLabel label, FormModel formModel, String formPropertyPath) {
		super(formModel, formPropertyPath, String.class);
		this.label = label;
	}

	@Override
	protected JComponent doBindControl() {
		label.setText((String) getValueModel().getValue());
		return label;
	}

	@Override
	protected void readOnlyChanged() {
		label.setEnabled(isEnabled() && !isReadOnly());
	}

	@Override
	protected void enabledChanged() {
		label.setEnabled(isEnabled() && !isReadOnly());
	}

	@Override
	protected void valueModelChanged(Object newValue) {
		label.setText((String) newValue);
	}
}