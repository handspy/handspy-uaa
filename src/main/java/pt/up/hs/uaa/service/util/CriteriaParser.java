package pt.up.hs.uaa.service.util;

import com.google.common.base.Joiner;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CriteriaParser {
    private static final Map<String, Operator> OPS;

    private static final Pattern SPEC_CRITERA_REGEX = Pattern.compile("^(\\w+?)(" + Joiner.on("|")
        .join(SearchOperation.SIMPLE_OPERATION_SET) + ")(\\p{Punct}?)(\\w+?)(\\p{Punct}?)$");

    private enum Operator {
        OR(1), AND(2);
        final int precedence;

        Operator(int p) {
            precedence = p;
        }
    }

    static {
        Map<String, Operator> tempMap = new HashMap<>();
        tempMap.put("AND", Operator.AND);
        tempMap.put("OR", Operator.OR);
        tempMap.put("or", Operator.OR);
        tempMap.put("and", Operator.AND);

        OPS = Collections.unmodifiableMap(tempMap);
    }

    private static boolean isHigerPrecedenceOperator(String currOp, String prevOp) {
        return (OPS.containsKey(prevOp) && OPS.get(prevOp).precedence >= OPS.get(currOp).precedence);
    }

    public Deque<?> parse(String searchParam) {

        Deque<Object> output = new LinkedList<>();
        Deque<String> stack = new LinkedList<>();

        Arrays.stream(searchParam.split("\\s+")).forEach(token -> {
            if (OPS.containsKey(token)) {
                while (!stack.isEmpty() && isHigerPrecedenceOperator(token, stack.peek()))
                    output.push(stack.pop()
                        .equalsIgnoreCase(SearchOperation.OR_OPERATOR) ? SearchOperation.OR_OPERATOR : SearchOperation.AND_OPERATOR);
                stack.push(token.equalsIgnoreCase(SearchOperation.OR_OPERATOR) ? SearchOperation.OR_OPERATOR : SearchOperation.AND_OPERATOR);
            } else if (token.equals(SearchOperation.LEFT_PARANTHESIS)) {
                stack.push(SearchOperation.LEFT_PARANTHESIS);
            } else if (token.equals(SearchOperation.RIGHT_PARANTHESIS)) {
                while (true) {
                    assert stack.peek() != null;
                    if (stack.peek().equals(SearchOperation.LEFT_PARANTHESIS)) break;
                    output.push(stack.pop());
                }
                stack.pop();
            } else {
                Matcher matcher = SPEC_CRITERA_REGEX.matcher(token);
                while (matcher.find()) {
                    output.push(new SpecSearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5)));
                }
            }
        });

        while (!stack.isEmpty())
            output.push(stack.pop());

        return output;
    }
}
