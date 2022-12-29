public class Symbol {
    private Terminal terminal;
    private NonTerminal nonTerminal;

    public Symbol(Terminal term) {
        terminal = term;
        nonTerminal = null;
    }

    public Symbol(NonTerminal nonTerm) {
        nonTerminal = nonTerm;
        terminal = null;
    }

    public Terminal getTerminal() {
        return terminal;
    }

    public NonTerminal getNonTerminal() {
        return nonTerminal;
    }

    public boolean isTerminal() {
        return (terminal != null && nonTerminal == null);
    }

    public boolean isNonTerminal() {
        return (nonTerminal != null && terminal == null);
    }

    public boolean isEpsilon() {
        return (nonTerminal == null && terminal.equals(Terminal.EPSILON));
    }

    @Override
    public String toString() {
        if (isTerminal()) {
                return terminal.toString();
        } else if (isNonTerminal()) {
            return nonTerminal.toString();
        } else {
            return "InvalidSymbol";
        }
    }
}
