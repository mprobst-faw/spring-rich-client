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
package org.springframework.binding.form.support;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import org.springframework.binding.value.ValueChangeDetector;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.AbstractValueModelWrapper;
import org.springframework.binding.value.support.DirtyTrackingValueModel;
import org.springframework.binding.value.support.ValueHolder;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.util.EventListenerListHelper;

/**
 * A value model wrapper that mediates between the (wrapped) data value model and the 
 * derived view value model. Allows for value change event delivery to be disabled.
 * 
 * @author Oliver Hutchison
 */
public class FormModelMediatingValueModel extends AbstractValueModelWrapper implements DirtyTrackingValueModel,
        PropertyChangeListener {

    private final EventListenerListHelper dirtyChangeListeners = new EventListenerListHelper(
            PropertyChangeListener.class);

    private boolean deliverValueChangeEvents = true;

    private final ValueHolder mediatedValueHolder;

    private Object originalValue;

    private boolean oldDirty;

    private final boolean trackDirty;

    private ValueChangeDetector valueChangeDetector;

    public FormModelMediatingValueModel(ValueModel propertyValueModel) {
        this(propertyValueModel, true);
    }

    public FormModelMediatingValueModel(ValueModel propertyValueModel, boolean trackDirty) {
        super(propertyValueModel);
        super.addValueChangeListener(this);
        this.originalValue = getValue();
        this.mediatedValueHolder = new ValueHolder(originalValue);
        this.trackDirty = trackDirty;
    }

    public void setValueSilently(Object value, PropertyChangeListener listenerToSkip) {
        super.setValueSilently(value, this);
        if (deliverValueChangeEvents) {
            mediatedValueHolder.setValueSilently(value, listenerToSkip);
            updateDirtyState();
        }        
    }

    // called by the wrapped value model
    public void propertyChange(PropertyChangeEvent evt) {
        originalValue = getValue();
        if (deliverValueChangeEvents) {            
            mediatedValueHolder.setValue(originalValue);
            updateDirtyState();
        }        
    }

    public void setDeliverValueChangeEvents(boolean deliverValueChangeEvents) {
        boolean oldDeliverValueChangeEvents = this.deliverValueChangeEvents;
        this.deliverValueChangeEvents = deliverValueChangeEvents;
        if (!oldDeliverValueChangeEvents && deliverValueChangeEvents) {
            mediatedValueHolder.setValue(getValue());
            updateDirtyState();            
        }
    }

    public boolean isDirty() {
        return trackDirty && getValueChangeDetector().hasValueChanged(originalValue, getValue());
    }

    public void clearDirty() {
        if (trackDirty) {
            originalValue = getValue();
            updateDirtyState();
        }
    }

    public void revertToOriginal() {
        if (trackDirty) {
            setValue(originalValue);
        }
    }

    protected void updateDirtyState() {
        boolean dirty = isDirty();
        if (oldDirty != dirty) {
            oldDirty = dirty;
            firePropertyChange(DIRTY_PROPERTY, !dirty, dirty);
        }
    }

    public void setValueChangeDetector(ValueChangeDetector valueChangeDetector) {
        this.valueChangeDetector = valueChangeDetector;
    }

    protected ValueChangeDetector getValueChangeDetector() {
        if (valueChangeDetector == null) {
            valueChangeDetector = Application.services().getValueChangeDetector();
        }
        return valueChangeDetector;
    }

    public void addValueChangeListener(PropertyChangeListener listener) {
        mediatedValueHolder.addValueChangeListener(listener);
    }

    public void removeValueChangeListener(PropertyChangeListener listener) {
        mediatedValueHolder.removeValueChangeListener(listener);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        throw new UnsupportedOperationException("not implemented");
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (DIRTY_PROPERTY.equals(propertyName)) {
            dirtyChangeListeners.add(listener);
        }
        else if (VALUE_PROPERTY.equals(propertyName)) {
            addValueChangeListener(listener);
        }
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        throw new UnsupportedOperationException("not implemented");
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (DIRTY_PROPERTY.equals(propertyName)) {
            dirtyChangeListeners.remove(listener);
        }
        else if (VALUE_PROPERTY.equals(propertyName)) {
            removeValueChangeListener(listener);
        }
    }

    protected final void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        if (DIRTY_PROPERTY.equals(propertyName)) {
            PropertyChangeEvent evt = new PropertyChangeEvent(this, propertyName, Boolean.valueOf(oldValue),
                    Boolean.valueOf(newValue));
            for (Iterator i = dirtyChangeListeners.iterator(); i.hasNext();) {
                ((PropertyChangeListener)i.next()).propertyChange(evt);
            }
        }
    }
}