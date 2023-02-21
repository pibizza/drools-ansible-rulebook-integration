package org.drools.ansible.rulebook.integration.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.drools.ansible.rulebook.integration.api.RuleConfigurationOptions;
import org.drools.ansible.rulebook.integration.api.domain.actions.Action;
import org.drools.ansible.rulebook.integration.api.domain.actions.MapAction;
import org.drools.ansible.rulebook.integration.api.domain.conditions.AstCondition;
import org.drools.ansible.rulebook.integration.api.domain.conditions.Condition;
import org.drools.ansible.rulebook.integration.api.domain.temporal.Throttle;
import org.drools.ansible.rulebook.integration.api.domain.temporal.TimeWindowDefinition;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Rule {
    private boolean enabled;

    final RuleGenerationContext ruleGenerationContext = new RuleGenerationContext();

	private String name;

	private Action action;

	private RuleConfigurationOptions options;

    public String getName() {
    	return name;
    }

    public void setName(String name) {
    	this.name = name;
    }

    public void setCondition(Condition condition) {
        ruleGenerationContext.setCondition(condition);
    }

    public Rule withOptions(RuleConfigurationOptions options) {
    	this.options = options;
        return this;
    }
    
    public RuleConfigurationOptions getOptions() {
    	return options;
    }

    public AstCondition withCondition() {
        AstCondition condition = new AstCondition(ruleGenerationContext);
        ruleGenerationContext.setCondition(condition);
        return condition;
    }
    
    public Action getAction() {
    	return action;
    }

    public void setAction(MapAction action) {
    	this.action = action;
    }

    public void setGenericAction(Action action) {
    	this.action = action;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setThrottle(Throttle throttle) {
        ruleGenerationContext.setTimeConstraint(throttle.asTimeConstraint());
    }

    public void setTimeout(String timeWindow) {
        ruleGenerationContext.setTimeConstraint(TimeWindowDefinition.parseTimeWindow(timeWindow));
    }

    public boolean hasTemporalConstraint() {
        return ruleGenerationContext.hasTemporalConstraint();
    }

    public boolean requiresAsyncExecution() {
        return ruleGenerationContext.requiresAsyncExecution();
    }

    @Override
    public String toString() {
        return "Rule{" +
                "name='" + ruleGenerationContext.getRuleName() + '\'' +
                ", condition='" + ruleGenerationContext.getCondition() + '\'' +
                ", action='" + ruleGenerationContext.getAction() + '\'' +
                '}';
    }
}
