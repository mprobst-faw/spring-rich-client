/*
 * Copyright 2002-2006 the original author or authors.
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
package org.springframework.richclient.list;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.springframework.binding.value.ValueModel;
import org.springframework.richclient.core.Guarded;

/**
 * This class applies a guard to a {@link Guarded} object that enables the
 * guarded object bsaed on the contents of the selection model value. Concrete
 * subclasses must provide an implementation for {@link #shouldEnable(int[])}.
 *
 * @author Larry Streepy
 */
public abstract class AbstractListSelectionGuard implements PropertyChangeListener {

	private ValueModel selectionHolder;
	private Guarded guarded;

	/**
	 * Constructor.
	 * 
	 * @param selectionHolder ValueModel holding the list selection (value must be
	 *                        an array of int (<code>int[]</code).
	 * @param guarded         Object to guard
	 */
	public AbstractListSelectionGuard(ValueModel selectionHolder, Guarded guarded) {
		this.selectionHolder = selectionHolder;
		this.selectionHolder.addValueChangeListener(this);
		this.guarded = guarded;
		propertyChange(null);
	}

	/**
	 * Handle a change in the selectionHolder value.
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		int[] selected = (int[]) selectionHolder.getValue();
		guarded.setEnabled(shouldEnable(selected));
	}

	/**
	 * Get the guarded object.
	 * 
	 * @return guarded object
	 */
	public Guarded getGuarded() {
		return guarded;
	}

	/**
	 * Get the selection value holder. The value of this value model will be an int
	 * array (<code>int[]</code).
	 *
	 * @return selection value holder
	 */
	public ValueModel getSelectionHolder() {
		return selectionHolder;
	}

	/**
	 * Determine if the guarded object should be enabled based on the contents of
	 * the current selection model value.
	 * 
	 * @param selected The array of selected rows
	 * @return boolean true if the guarded object should be enabled
	 */
	protected abstract boolean shouldEnable(int[] selected);
}
