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
package org.springframework.richclient.list;

import java.util.Observable;
import java.util.Observer;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;

import org.springframework.rules.constraint.Constraint;
import org.springframework.util.Assert;

/**
 * Decorates an existing {@link ListModel} by applying a constraint. The
 * constraint can implement {@link Observable} to notify a change of the filter
 * condition.
 *
 * @author Keith Donald
 * @author Mathias Broekelmann
 */
public class FilteredListModel extends AbstractFilteredListModel implements Observer {

	private static final long serialVersionUID = 1L;

	private Constraint constraint;

	private int[] indexes;

	private int filteredSize;

	/**
	 * Constructs a new instance
	 * 
	 * @param listModel  the list model to filter.
	 * @param constraint the constraint which is applied to the list model elements
	 * 
	 * @throws IllegalArgumentException if list model or constraint parameters where
	 *                                  null
	 */
	public FilteredListModel(ListModel listModel, Constraint constraint) {
		super(listModel);
		setConstraint(constraint);
	}

	@Override
	protected void fireContentsChanged(Object source, int index0, int index1) {
		reallocateIndexes();
		super.fireContentsChanged(source, index0, index1);
	}

	/**
	 * Defines the constraint which is applied to the list model elements
	 * 
	 * @param constraint the constraint to set
	 * 
	 * @throws IllegalArgumentException if constraint is null
	 */
	public final void setConstraint(Constraint constraint) {
		Assert.notNull(constraint);
		if (!constraint.equals(this.constraint)) {
			if (this.constraint instanceof Observable) {
				((Observable) constraint).deleteObserver(this);
			}
			this.constraint = constraint;
			if (constraint instanceof Observable) {
				((Observable) constraint).addObserver(this);
			}
			fireContentsChanged(this, -1, -1);
		}
	}

	/**
	 * @return the constraint
	 */
	public Constraint getConstraint() {
		return constraint;
	}

	/**
	 * Internally called to reallocate the indexes. This method should be called
	 * when the filtered model changes its element size
	 */
	protected void reallocateIndexes() {
		if (this.indexes == null || this.indexes.length != getFilteredModel().getSize()) {
			this.indexes = new int[getFilteredModel().getSize()];
		}
		applyConstraint();
	}

	/**
	 * If the constraint implements {@link Observable} this method is called and
	 * will apply the constraint to the list model elements
	 */
	@Override
	public void update(Observable changed, Object arg) {
		fireContentsChanged(this, -1, -1);
	}

	private void applyConstraint() {
		filteredSize = 0;
		ListModel filteredListModel = getFilteredModel();
		for (int i = 0, size = filteredListModel.getSize(); i < size; i++) {
			Object element = filteredListModel.getElementAt(i);
			if (constraint.test(element)) {
				indexes[filteredSize++] = i;
				onMatchingElement(element);
			}
		}
		postConstraintApplied();
	}

	/**
	 * Called to notify that an element has matched the filter constraint. This
	 * implementation does nothing.
	 * 
	 * @param element the element which was accepted by the filter
	 */
	protected void onMatchingElement(Object element) {

	}

	/**
	 * Called to notify that the constraint was applied to all elements. This
	 * implementation does nothing.
	 */
	protected void postConstraintApplied() {

	}

	/**
	 * Returns the size of the elements which passes the filter constraint.
	 */
	@Override
	public int getSize() {
		return filteredSize;
	}

	/**
	 * Returns the element index for a filtered index
	 * 
	 * @param filteredIndex the filtered index
	 * @return the unfiltered index of the filtered model
	 */
	@Override
	public int getElementIndex(int filteredIndex) {
		return indexes[filteredIndex];
	}

	@Override
	public void contentsChanged(ListDataEvent e) {
		reallocateIndexes();
		super.contentsChanged(e);
	}

	@Override
	public void intervalAdded(ListDataEvent e) {
		reallocateIndexes();
		super.intervalAdded(e);
	}

	@Override
	public void intervalRemoved(ListDataEvent e) {
		reallocateIndexes();
		super.intervalRemoved(e);
	}

}