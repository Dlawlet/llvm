import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.lang.StringBuilder;

public class Grammar {
    private NonTerminal startSymbol;
    private Map<NonTerminal,Set<List<Symbol>>> derivationRules;
    private Map<NonTerminal,Set<Terminal>> first;
    private Map<NonTerminal,Set<Terminal>> follow;
    private Map<Pair<NonTerminal,Terminal>,List<Symbol>> actionTable;

    public Grammar(NonTerminal startSymb) {
        startSymbol = startSymbol;
        derivationRules = new LinkedHashMap<NonTerminal,Set<List<Symbol>>>();
        first = new LinkedHashMap<NonTerminal,Set<Terminal>>();
        follow = new LinkedHashMap<NonTerminal,Set<Terminal>>();
        actionTable = new LinkedHashMap<Pair<NonTerminal,Terminal>, List<Symbol>>();
    }

    public void addRule(NonTerminal lhs, List<Symbol> rhs) {
        if (derivationRules.containsKey(lhs)) {
            derivationRules.get(lhs).add(rhs);
            } else {
            HashSet<List<Symbol>> lhsRules = new HashSet<List<Symbol>>();
            lhsRules.add(rhs);
            derivationRules.put(lhs, lhsRules);
        }
    }

    public Set<List<Symbol>> getRules(NonTerminal nonTerm) {
        return derivationRules.get(nonTerm);
    }

    public Map<NonTerminal,Set<Terminal>> getFirst() {
        return first;
    }

    public Map<NonTerminal,Set<Terminal>> getFollow() {
        return follow;
    }

    public Map<Pair<NonTerminal,Terminal>,List<Symbol>> getActionTable() {
        return actionTable;
    }


    public void setFirst() {
        for (NonTerminal nonTerm: NonTerminal.values()) {
            first.put(nonTerm, EnumSet.noneOf(Terminal.class));
        }
        boolean stable;
        do {
            stable = true;
            for (NonTerminal nonTerm: NonTerminal.values()) {
                for (List<Symbol> rule: getRules(nonTerm)) {
                    Set<Terminal> firstOfRule = first(rule);
                    if (first.get(nonTerm).addAll(firstOfRule)) {
                        stable = false;
                    }
                }
            }
        } while (!stable);
    }

    public Set<Terminal> first(List<Symbol> partialRule) {
        Set<Terminal> firstOfRule = EnumSet.noneOf(Terminal.class);
        for (Symbol symb: partialRule) {
            if (symb.isTerminal()) {
                firstOfRule.add(symb.getTerminal());
                if (!symb.isEpsilon()) {
                    break;
                }
            } else {
                firstOfRule.addAll(first.get(symb.getNonTerminal()));
                if (!first.get(symb.getNonTerminal()).contains(Terminal.EPSILON)) {
                    break;
                }
            }
        }
        return firstOfRule;
    }

    public void setFollow () {
        for (NonTerminal nonTerm: NonTerminal.values()) {
            follow.put(nonTerm, EnumSet.noneOf(Terminal.class));
        }
        follow.put(startSymbol, EnumSet.of(Terminal.EPSILON));
        boolean stable;
        do {
            stable = true;
            for (NonTerminal nonTerm: NonTerminal.values()) {
                for (List<Symbol> rule: getRules(nonTerm)) {
                    for (int i = 0; i < rule.size() ; i++) { // We need to use index to get sublist
                        Symbol symb = rule.get(i);
                        if (symb.isNonTerminal()) {
                            Set<Terminal> firstOfRule = first(rule.subList(i+1, rule.size()));
                            if (follow.get(symb.getNonTerminal()).addAll(firstOfRule)) {
                                stable = false;
                            };
                            if (firstOfRule.contains(Terminal.EPSILON) || i == rule.size()-1) {
                                if (follow.get(symb.getNonTerminal()).addAll(follow.get(nonTerm))) {
                                    stable = false;
                                }
                            }
                        }
                    }
                }
            }
        } while (!stable);
    }

    public void addAction(NonTerminal nonTerm, Terminal term, List<Symbol> rule) throws Exception {
        if (actionTable.containsKey(new Pair<NonTerminal, Terminal>(nonTerm, term))) {
            // throw new Exception("The grammar is not LL(1): tried to add rule " + rule + " to (" + nonTerm + ", " + term + ") but already had rule " + actionTable.get(new Pair<NonTerminal, Terminal>(nonTerm, term)) + "\n");
            System.out.println("The grammar is not LL(1): tried to add rule " + rule + " to (" + nonTerm + ", " + term + ") but already had rule " + actionTable.get(new Pair<NonTerminal, Terminal>(nonTerm, term)) + "\n");
        } else {
            actionTable.put(new Pair<NonTerminal, Terminal>(nonTerm, term), rule);
        }
    }

    public void setActionTable() throws Exception {
        for (NonTerminal nonTerm: NonTerminal.values()) {
            for (List<Symbol> rule: getRules(nonTerm)) {
                for (Terminal term: first(rule)) {
                    if (term.equals(Terminal.EPSILON)) {
                        for (Terminal folTerm: follow.get(nonTerm)) {
                            addAction(nonTerm, folTerm, rule);
                        }
                    } else {
                        addAction(nonTerm, term, rule);
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        boolean first;
        for (NonTerminal variable: derivationRules.keySet()) {
            string.append(String.format("%1$-12s--> ", variable));
            first = true;
            for (List<Symbol> rule: derivationRules.get(variable)) {
                if (first) {
                    first = false;
                } else {
                    string.append("            --> ");
                }
                for (Symbol symb: rule) {
                    string.append(symb);
                    string.append(" ");
                }
                string.append("\n");
            }
        }
        return string.toString();
    }

    public String stringFirst() {
        StringBuilder string = new StringBuilder();
        boolean firstIt;
        for (NonTerminal nonTerm: first.keySet()) {
            string.append(String.format("%1$-12s| ", nonTerm));
            firstIt = true;
            for (Terminal term: first.get(nonTerm)) {
                if (firstIt) {
                    firstIt = false;
                } else {
                    string.append(", ");
                }
                string.append(term);
            }
            string.append("\n");
        }
        return string.toString();
    }

    public String stringFollow() {
        StringBuilder string = new StringBuilder();
        boolean firstIt;
        for (NonTerminal nonTerm: follow.keySet()) {
            string.append(String.format("%1$-12s| ", nonTerm));
            firstIt = true;
            for (Terminal term: follow.get(nonTerm)) {
                if (firstIt) {
                    firstIt = false;
                } else {
                    string.append(", ");
                }
                string.append(term);
            }
            string.append("\n");
        }
        return string.toString();
    }

    public String stringActionTable() {
        StringBuilder string = new StringBuilder();
        boolean firstIt;
        for (NonTerminal nonTerm: NonTerminal.values()) {
            for (Terminal term: Terminal.values()) {
                if (actionTable.containsKey(new Pair<>(nonTerm, term))) {
                    string.append(String.format("[%1$-11s, ", nonTerm));
                    string.append(String.format("%1$-9s] : ", term));
                    String ruleString = actionTable.get(new Pair<>(nonTerm, term)).toString();
                    string.append(ruleString.substring(1,ruleString.length()-1).replace(",",""));
                    string.append("\n");
                }
            }
        }
        return string.toString();
    }
}
