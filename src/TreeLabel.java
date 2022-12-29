public class TreeLabel {
    private NonTerminal nonTerminal;
    private Token token;

    public TreeLabel(NonTerminal nonTerm) {
        nonTerminal = nonTerm;
        token = null;
    }

    public TreeLabel(Token tok) {
        nonTerminal = null;
        token = tok;
    }

    public boolean isToken() {
        return (token != null && nonTerminal == null);
    }

    public boolean isNonTerminal() {
        return (nonTerminal != null && token == null);
    }

    public boolean isEpsilon() {
        return (nonTerminal == null && token.isEpsilon());
    }

    @Override
    public String toString() {
        if (isToken()) {
            return token.toString();
        } else if (isNonTerminal()) {
            return nonTerminal.toString();
        } else {
            return "InvalidTreeLeaf";
        }
    }

    public String toTeX() {
        if (isToken()) {
            return token.toTeX();
        } else if (isNonTerminal()) {
            return nonTerminal.toString();
        } else {
            return "InvalidTreeLeaf";
        }
    }
}
