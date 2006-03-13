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
package org.springframework.rules.support;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.style.ToStringCreator;
import org.springframework.rules.Rules;
import org.springframework.rules.RulesSource;
import org.springframework.rules.constraint.ConstraintsAccessor;
import org.springframework.rules.constraint.property.PropertyConstraint;
import org.springframework.util.StringUtils;

/**
 * A default rules source implementation which is simply a in-memory registry
 * for bean validation rules backed by a map.
 * 
 * @author Keith Donald
 */
public class DefaultRulesSource extends ConstraintsAccessor implements RulesSource {
	protected final Log logger = LogFactory.getLog(getClass());

	private static final String DEFAULT_CONTEXT_ID = "default";

	private Map ruleContexts = new HashMap();

	/**
	 * Add or update the rules for a single bean class.
	 * 
	 * @param rules
	 *            The rules.
	 */
	public void addRules(Rules rules) {
		if (logger.isDebugEnabled()) {
			logger.debug("Adding rules -> " + rules);
		}
		addRules(DEFAULT_CONTEXT_ID, rules);
	}

	public void addRules(String contextId, Rules rules) {
		Map context = getOrCreateRuleContext(contextId);
		context.put(rules.getDomainObjectType(), rules);
	}

	private Map getOrCreateRuleContext(String contextId) {
		Map context = (Map)this.ruleContexts.get(contextId);
		if (context == null) {
			context = new HashMap();
			this.ruleContexts.put(contextId, context);
		}
		return context;
	}

	/**
	 * Set the list of rules retrievable by this source, where each item in the
	 * list is a <code>Rules</code> object which maintains validation rules
	 * for a bean class.
	 * 
	 * @param rules
	 *            The list of rules.
	 */
	public void setRules(List rules) {
		if (logger.isDebugEnabled()) {
			logger.debug("Configuring rules in source...");
		}
		getOrCreateRuleContext(DEFAULT_CONTEXT_ID).clear();
		for (Iterator i = rules.iterator(); i.hasNext();) {
			addRules((Rules)i.next());
		}
	}

	public Rules getRules(Class bean) {
		return (Rules)getRules(bean, DEFAULT_CONTEXT_ID);
	}

	public Rules getRules(Class bean, String contextId) {
		if (!StringUtils.hasText(contextId)) {
			contextId = DEFAULT_CONTEXT_ID;
		}
		return (Rules)getOrCreateRuleContext(contextId).get(bean);
	}

	public PropertyConstraint getPropertyConstraint(Class bean, String propertyName) {
		return getPropertyConstraint(bean, propertyName, DEFAULT_CONTEXT_ID);
	}

	public PropertyConstraint getPropertyConstraint(Class bean, String propertyName, String contextId) {
		if (logger.isDebugEnabled()) {
			logger.debug("Retrieving rules for bean '" + bean + "', context = " + contextId + ", property '" + propertyName
					+ "'");
		}
		Rules rules = getRules(bean, contextId);
		if (rules != null) {
			return rules.getPropertyConstraint(propertyName);
		}
		else {
			return null;
		}
	}

	public String toString() {
		return new ToStringCreator(this).append("rules", ruleContexts).toString();
	}

}