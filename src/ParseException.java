import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class ParseException extends Exception {
    private Token token;
    private NonTerminal variable;
    private List<Terminal> alternatives;

    public ParseException(Token symbol){
        this.token = symbol;
        this.alternatives = new ArrayList<Terminal>();
    }

    public ParseException(Token symbol, NonTerminal var){
        this.token = symbol;
        this.variable = var;
        this.alternatives = new ArrayList<Terminal>();
    }

    public ParseException(Token symbol, List<Terminal> alts){
        this.token = symbol;
        this.alternatives = alts;
    }

    public ParseException(Token symbol, NonTerminal var, List<Terminal> alts){
        this.token = symbol;
        this.variable = var;
        this.alternatives = alts;
    }

    private String stringOfAlternatives(){
        StringBuilder altString = new StringBuilder();
        // We want a special condition for the last element
        if (alternatives.isEmpty()) {
            return "";
        } else {
            altString.append("expected ");
            boolean first = true;
            for (Terminal term: alternatives) {
                if (first) {
                    first = false;
                } else {
                    altString.append(", ");
                }
                altString.append(term);
            }
            altString.append(", but got ");
            return altString.toString();
        }
    }

    public String getMessage(){
        if (variable == null) {
            return String.format("Parsing Error at line %d and column %d: %s%s", token.getLine(), token.getColumn(), stringOfAlternatives(), token.getValue());
        } else {
            return String.format("Parsing Error at line %d and column %d trying to parse %s: %s%s", token.getLine(), token.getColumn(), variable.toString(), stringOfAlternatives(), token.getValue().toString());
        }
    }
}
