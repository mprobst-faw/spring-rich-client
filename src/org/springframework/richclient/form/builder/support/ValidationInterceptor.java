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
package org.springframework.richclient.form.builder.support;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.swing.JComponent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.binding.form.FormModel;
import org.springframework.binding.form.ValidationEvent;
import org.springframework.binding.form.ValidationListener;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.core.Guarded;
import org.springframework.richclient.dialog.MessageReceiver;
import org.springframework.richclient.form.builder.FormComponentInterceptor;
import org.springframework.richclient.forms.SimpleValidationResultsReporter;
import org.springframework.richclient.util.ListenerListHelper;
import org.springframework.rules.constraint.property.PropertyConstraint;
import org.springframework.rules.reporting.DefaultMessageTranslator;
import org.springframework.rules.reporting.PropertyResults;
import org.springframework.rules.reporting.Severity;
import org.springframework.rules.reporting.ValidationResults;
import org.springframework.util.Assert;

/**
 * @author oliverh
 */
public abstract class ValidationInterceptor implements FormComponentInterceptor {

    private SimplePropertyValidationResultsReporter propertyValidatonReporter;

    public void setFormModel(FormModel formModel) {
        propertyValidatonReporter = new SimplePropertyValidationResultsReporter(
                formModel);
    }

    public JComponent processLabel(String propertyName, JComponent label) {
        return label;
    }

    protected void registerErrorMessageReceiver(String propertyName,
            MessageReceiver messageReceiver) {
        propertyValidatonReporter.registerMessageReceiver(propertyName,
                messageReceiver);
    }

    protected void registerErrorGuarded(String propertyName, Guarded guarded) {
        propertyValidatonReporter.registerGuarded(propertyName, guarded);
    }

    private static class SimplePropertyValidationResultsReporter implements
            ValidationListener {
        private static final Log logger = LogFactory
                .getLog(SimpleValidationResultsReporter.class);

        private FormModel formModel;

        private Map propertyMessages = new HashMap();

        private Map propertyGuarded = new HashMap();

        private Map propertyMessage = new HashMap();

        public SimplePropertyValidationResultsReporter(FormModel formModel) {
            Assert.notNull(formModel);
            formModel.addValidationListener(this);
        }

        public void registerGuarded(String propertyName, Guarded guarded) {
            getGuards(propertyName).add(guarded);
            update(propertyName);
        }

        private ListenerListHelper getGuards(String propertyName) {
            ListenerListHelper guards = (ListenerListHelper)propertyGuarded
                    .get(propertyName);
            if (guards == null) {
                guards = new ListenerListHelper(Guarded.class);
                propertyGuarded.put(propertyName, guards);
            }
            return guards;
        }

        public void registerMessageReceiver(String propertyName,
                MessageReceiver messageReceiver) {
            getMessageReceivers(propertyName).add(messageReceiver);
            update(propertyName);
        }

        private ListenerListHelper getMessageReceivers(String propertyName) {
            ListenerListHelper messageReceivers = (ListenerListHelper)propertyMessage
                    .get(propertyName);
            if (messageReceivers == null) {
                messageReceivers = new ListenerListHelper(MessageReceiver.class);
                propertyMessage.put(propertyName, messageReceivers);
            }
            return messageReceivers;
        }

        public void constraintSatisfied(ValidationEvent event) {
            String propertyName = getPropertyNameFrom(event);
            if (propertyName != null) {
                remove(propertyName, event);
                update(propertyName);
            }
        }

        public void constraintViolated(ValidationEvent event) {
            String propertyName = getPropertyNameFrom(event);
            if (propertyName != null) {
                put(propertyName, event);
                update(propertyName);
            }
        }

        private String getPropertyNameFrom(ValidationEvent event) {
            return event.getConstraint() instanceof PropertyConstraint ? ((PropertyConstraint)event
                    .getConstraint()).getPropertyName()
                    : null;
        }

        private void remove(String propertyName, ValidationEvent event) {
            getMessages(propertyName).remove(event);
        }

        private Stack getMessages(String propertyName) {
            Stack messages = (Stack)propertyMessages.get(propertyName);
            if (messages == null) {
                messages = new Stack();
                propertyMessages.put(propertyName, messages);
            }
            return messages;
        }

        private void put(String propertyName, ValidationEvent event) {
            Stack messages = getMessages(propertyName);
            int index = messages.indexOf(event);
            if (index == -1) {
                messages.push(event);
            }
            else {
                messages.remove(index);
                messages.push(event);
            }
        }

        private void update(String propertyName) {
            Stack messages = getMessages(propertyName);

            Severity severity;
            String message;
            boolean enabled;

            if (messages.size() > 0) {
                ValidationEvent error = (ValidationEvent)messages.peek();
                severity = error.getResults().getSeverity();
                message = translate(error.getResults());
                enabled = false;
            }
            else {
                severity = Severity.INFO;
                message = "";
                enabled = true;
            }

            getMessageReceivers(propertyName).fire("setMessage", message,
                    severity);
            getGuards(propertyName)
                    .fire("setEnabled", Boolean.valueOf(enabled));
        }

        private String translate(ValidationResults results) {
            DefaultMessageTranslator messageTranslator = new DefaultMessageTranslator(
                    Application.services());
            return messageTranslator.getMessage((PropertyResults)results);
        }
    }
}