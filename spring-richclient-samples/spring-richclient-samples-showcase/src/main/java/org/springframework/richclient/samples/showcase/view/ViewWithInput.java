/*
 * Copyright 2002-2008 the original author or authors.
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
package org.springframework.richclient.samples.showcase.view;

import java.awt.FlowLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.springframework.richclient.application.support.AbstractView;
import org.springframework.util.ObjectUtils;

/**
 * @author Peter De Bruycker
 */
public class ViewWithInput extends AbstractView {

	private JTextField inputField;

	@Override
	protected JComponent createControl() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING));

		JLabel label = new JLabel("The input: ");
		inputField = new JTextField(25);
		inputField.setEnabled(false);

		panel.add(label);
		panel.add(inputField);

		return panel;
	}

	@Override
	public void setInput(Object input) {
		inputField.setText(ObjectUtils.nullSafeToString(input));
	}

}
