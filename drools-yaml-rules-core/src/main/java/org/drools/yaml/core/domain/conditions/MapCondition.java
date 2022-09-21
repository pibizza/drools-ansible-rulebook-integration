package org.drools.yaml.core.domain.conditions;

import java.util.Map;

import org.drools.model.Index;
import org.drools.model.PrototypeExpression;
import org.drools.model.view.CombinedExprViewItem;
import org.drools.model.view.ViewItem;
import org.drools.yaml.core.SessionGenerator;
import org.drools.yaml.core.rulesmodel.ParsedCondition;

import static org.drools.model.Index.ConstraintType.EXISTS_PROTOTYPE_FIELD;
import static org.drools.model.PrototypeExpression.fixedValue;
import static org.drools.model.PrototypeExpression.prototypeField;
import static org.drools.yaml.core.SessionGenerator.PROTOTYPE_NAME;
import static org.drools.yaml.core.domain.Binding.generateBinding;

public class MapCondition implements Condition {

    private Map<?,?> map;

    private String patternBinding;

    public MapCondition() { }

    public MapCondition(Map<?,?> map) {
        this.map = map;
    }

    public Map<?,?> getMap() {
        return map;
    }

    public void setMap(Map<?,?> map) {
        this.map = map;
    }

    public String getPatternBinding() {
        if (patternBinding == null) {
            patternBinding = generateBinding();
        }
        return patternBinding;
    }

    @Override
    public ViewItem toPattern(SessionGenerator.RuleContext ruleContext) {
        return condition2Pattern(ruleContext, this);
    }

    private static ViewItem condition2Pattern(SessionGenerator.RuleContext ruleContext, MapCondition condition) {
        assert(condition.getMap().size() == 1);
        Map.Entry entry = condition.getMap().entrySet().iterator().next();
        String expressionName = (String) entry.getKey();
        switch (expressionName) {
            case "OrExpression":
                return new CombinedExprViewItem(org.drools.model.Condition.Type.OR, new ViewItem[] {
                        scopingCondition2Pattern(ruleContext, new MapCondition((Map)((Map) entry.getValue()).get("lhs"))),
                        scopingCondition2Pattern(ruleContext, new MapCondition((Map)((Map) entry.getValue()).get("rhs")))

                });
            case "AndExpression":
                return new CombinedExprViewItem(org.drools.model.Condition.Type.AND, new ViewItem[] {
                        condition2Pattern(ruleContext, new MapCondition((Map)((Map) entry.getValue()).get("lhs"))),
                        condition2Pattern(ruleContext, new MapCondition((Map)((Map) entry.getValue()).get("rhs")))

                });
        }
        return singleCondition2Pattern(ruleContext, condition, entry);
    }

    private static ViewItem scopingCondition2Pattern(SessionGenerator.RuleContext ruleContext, MapCondition condition) {
        ruleContext.pushContext();
        ViewItem pattern = condition2Pattern(ruleContext, condition);
        ruleContext.popContext();
        return pattern;
    }

    private static ViewItem singleCondition2Pattern(SessionGenerator.RuleContext ruleContext, MapCondition condition, Map.Entry entry) {
        ParsedCondition parsedCondition = condition.parseSingle(entry);
        var pattern = ruleContext.getOrCreatePattern(condition.getPatternBinding(), PROTOTYPE_NAME);
        pattern.expr(parsedCondition.getLeft(), parsedCondition.getOperator(), parsedCondition.getRight());
        return pattern;
    }

    private static ParsedCondition parseSingle(Map.Entry entry) {
        String expressionName = (String) entry.getKey();
        Index.ConstraintType operator = decodeOperation(expressionName);

        Map<?,?> expression = (Map<?,?>) entry.getValue();
        PrototypeExpression left = map2Expr(expression.get("lhs"));
        PrototypeExpression right = operator == EXISTS_PROTOTYPE_FIELD ?
                fixedValue(expressionName.equals("IsDefinedExpression")) :
                map2Expr(expression.get("rhs"));

        return new ParsedCondition(left, operator, right);
    }

    private static PrototypeExpression map2Expr(Object expr) {
        if (expr instanceof String) {
            return prototypeField((String)expr);
        }

        Map<?,?> exprMap = (Map) expr;
        assert(exprMap.size() == 1);
        Map.Entry entry = exprMap.entrySet().iterator().next();
        String key = (String) entry.getKey();
        Object value = entry.getValue();

        switch (key) {
            case "Integer":
            case "String":
                return fixedValue(value);
        }

        return prototypeField(key + "." + value);
    }

    private static Index.ConstraintType decodeOperation(String expressionName) {
        switch (expressionName) {
            case "EqualsExpression":
                return Index.ConstraintType.EQUAL;
            case "GreaterThanExpression":
                return Index.ConstraintType.GREATER_THAN;
            case "GreaterThanOrEqualToExpression":
                return Index.ConstraintType.GREATER_OR_EQUAL;
            case "LessThanExpression":
                return Index.ConstraintType.LESS_THAN;
            case "LessThanOrEqualToExpression":
                return Index.ConstraintType.LESS_OR_EQUAL;
            case "IsDefinedExpression":
            case "IsNotDefinedExpression":
                return EXISTS_PROTOTYPE_FIELD;
        }
        return null;
    }

    @Override
    public String toString() {
        return "MapCondition{" +
                "map=" + map +
                '}';
    }
}