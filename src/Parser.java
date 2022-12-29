import java.util.List;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class Parser{
    private LexicalAnalyzer scanner;
    private Token current;

    public Parser(FileReader source) throws IOException{
        this.scanner = new LexicalAnalyzer(source);
        this.current = scanner.nextToken();
    }

    private void consume() throws IOException{
        current = scanner.nextToken();
    }

    private ParseTree match(Terminal token) throws IOException, ParseException{
        if(!current.getType().equals(token)){
            // There is a parsing error
            throw new ParseException(current, Arrays.asList(token));
        }
        else {
            Token cur = current;
            consume();
            return new ParseTree(new TreeLabel(cur));
        }
    }
    public ParseTree parse() throws IOException, ParseException{
        // Program is the initial symbol of the grammar
        return program();
    }

    private ParseTree program() throws IOException, ParseException{
        // Program     --> BEGIN PROGNAME Code END
        return new ParseTree(NonTerminal.Program, Arrays.asList(
				match(Terminal.BEGIN),
				match(Terminal.PROGNAME),
				code(), 
				match(Terminal.END),
				match(Terminal.EOS)));
    }

    private ParseTree code() throws IOException, ParseException{
        switch(current.getType()) {
            // <Code>         --> EPSILON 
        case END:
        case ELSE:
                return new ParseTree(NonTerminal.Code, Arrays.asList(new ParseTree(Terminal.EPSILON)));
            // <Code>  -->  Instruction COMMA Code
            case VARNAME:
            case IF:
            case WHILE:
            case PRINT:
            case READ:
                return new ParseTree(NonTerminal.Code, Arrays.asList(
				instruction(),
				match(Terminal.COMMA),
				code()));
            default:
                throw new ParseException(current);
        }
    }


    private ParseTree instruction() throws IOException, ParseException{
        switch(current.getType()) {
            // <Instruction>  --> <Assign> 
        case VARNAME:
            return new ParseTree(NonTerminal.Instruction, Arrays.asList(assign()));
            // <Instruction>  --> <If> 
        case IF:
            return new ParseTree(NonTerminal.Instruction, Arrays.asList(ifI()));
            // <Instruction>  --> <While> 
        case WHILE:
            return new ParseTree(NonTerminal.Instruction, Arrays.asList(whileI()));
            // <Instruction>  --> <Print> 
        case PRINT:
            return new ParseTree(NonTerminal.Instruction, Arrays.asList(print()));
            // <Instruction>  --> <Read> 
        case READ:
            return new ParseTree(NonTerminal.Instruction, Arrays.asList(read()));
        default:
            throw new ParseException(current);
        }
    }

    private ParseTree assign() throws IOException, ParseException{
        // <Assign>       --> VARNAME ASSIGN <ExprArith> 
        return new ParseTree(
                             NonTerminal.Assign,
                             Arrays.asList(
                                           match(Terminal.VARNAME),
                                           match(Terminal.ASSIGN),
                                           exprArith()
                                           ));
    }

    private ParseTree exprArith() throws IOException, ParseException{
        // <ExprArith>    --> <ProdExpr> <ExprTail>
        return new ParseTree(NonTerminal.ExprArith,
                             Arrays.asList(
                                           prodExpr(),
                                           exprTail()
                                           ));
    }

    private ParseTree prodExpr() throws IOException, ParseException{
        // <ProdExpr>     --> <ExprAtom> <ProdTail> 
        return new ParseTree(NonTerminal.ProdExpr,
                             Arrays.asList(
                                           exprAtom(),
                                           prodTail()
                                           ));
    }

    private ParseTree exprAtom() throws IOException, ParseException{
        List<Terminal> altExprAtom = Arrays.asList(Terminal.MINUS, Terminal.LPAREN, Terminal.VARNAME, Terminal.NUMBER);
        switch(current.getType()) {
            // <ExprAtom>     --> MINUS <ExprAtom>
        case MINUS:
            return new ParseTree(NonTerminal.ExprAtom,
                                 Arrays.asList(
                                               match(Terminal.MINUS),
                                               exprAtom()
                                               ));
            // <ExprAtom>     --> VARNAME
        case VARNAME:
            return new ParseTree(NonTerminal.ExprAtom,
                                 Arrays.asList(match(Terminal.VARNAME)));
            // <ExprAtom>     --> NUMBER
        case NUMBER:
            return new ParseTree(NonTerminal.ExprAtom,
                                 Arrays.asList(match(Terminal.NUMBER)));
            // <ExprAtom>     --> LPAREN <ExprArith> RPAREN
        case LPAREN:
            return new ParseTree(NonTerminal.ExprAtom,
                                 Arrays.asList(
                                               match(Terminal.LPAREN),
                                               exprArith(),
                                               match(Terminal.RPAREN)
                                               ));
        default:
            throw new ParseException(current, altExprAtom);
        }
    }

    private ParseTree exprTail() throws IOException, ParseException{
        switch(current.getType()) {
            // <ExprTail>     --> <AddOp> <ProdExpr> <ExprTail>
            case PLUS:
            case MINUS:
                return new ParseTree(NonTerminal.ExprTail,
                                     Arrays.asList(
                                                   addOp(),
                                                   prodExpr(),
                                                   exprTail()
                                                   ));
                // <ExprTail> --> EPSILON
            default:
                return new ParseTree(NonTerminal.ExprTail, Arrays.asList(new ParseTree(Terminal.EPSILON)));
        }
    }

    private ParseTree prodTail() throws IOException, ParseException{
        switch(current.getType()) {
            // <ProdTail>     --> <MultOp> <ExprAtom> <ProdTail>
        case TIMES:
        case DIVIDE:
            return new ParseTree(NonTerminal.ProdTail,
                                 Arrays.asList(
                                               multOp(),
                                               exprAtom(),
                                               prodTail()
                                               ));
            // <ProdTail> --> EPSILON
        default:
            return new ParseTree(NonTerminal.ProdTail, Arrays.asList(new ParseTree(Terminal.EPSILON)));
        }
    }

    private ParseTree addOp() throws IOException, ParseException{
        switch(current.getType()) {
            // <AddOp>        --> PLUS
        case PLUS:
            return new ParseTree(NonTerminal.AddOp, Arrays.asList(match(Terminal.PLUS)));
            // <AddOp>        --> MINUS
        case MINUS:
            return new ParseTree(NonTerminal.AddOp, Arrays.asList(match(Terminal.MINUS)));
        default:
            throw new ParseException(current);
        }
    }

    private ParseTree multOp() throws IOException, ParseException{
        switch(current.getType()) {
             // <MultOp>       --> TIMES
        case TIMES:
            return new ParseTree(NonTerminal.MultOp, Arrays.asList(match(Terminal.TIMES)));
             // <MultOp>       --> DIVIDE
        case DIVIDE:
            return new ParseTree(NonTerminal.MultOp, Arrays.asList(match(Terminal.DIVIDE)));
        default:
            throw new ParseException(current);
        }
    }

    private ParseTree cond() throws IOException, ParseException{
        // <Cond>         --> ExprArith Comp ExprArith
        return new ParseTree(NonTerminal.Cond,
                             Arrays.asList(
                                           exprArith(),
                                           comp(),
                                           exprArith()
                                           ));
    }


    private ParseTree ifI() throws IOException, ParseException{
        // Cannot be simply named "if"
        // <If> --> IF LPAREN Cond RPAREN THEN Code MaybeElse END
        return new ParseTree(NonTerminal.If,
                             Arrays.asList(
						match(Terminal.IF),
						match(Terminal.LPAREN),
                        cond(),
						match(Terminal.RPAREN),
                        match(Terminal.THEN),
                        code(),
                        maybeElse(),
                        match(Terminal.END)));
    }

    private ParseTree maybeElse() throws IOException, ParseException{
        switch(current.getType()) {
            // <MaybeElse>    --> ELSE Code
        case ELSE:
            return new ParseTree(NonTerminal.MaybeElse,
                                 Arrays.asList(
                                               match(Terminal.ELSE),
                                               code()
                                               ));
            // <MaybeElse> --> EPSILON
        default:
			return new ParseTree(NonTerminal.Code, Arrays.asList(new ParseTree(Terminal.EPSILON)));
        }
    }


    private ParseTree comp() throws IOException, ParseException{
        List<Terminal> altComp = Arrays.asList(Terminal.EQUAL, Terminal.GREATER, Terminal.SMALLER);
        switch(current.getType()) {
            // Comp --> EQUAL
        case EQUAL:
            return new ParseTree(NonTerminal.Comp, Arrays.asList(match(Terminal.EQUAL)));
            // Comp --> GREATER
        case GREATER:
            return new ParseTree(NonTerminal.Comp, Arrays.asList(match(Terminal.GREATER)));
            // Comp --> SMALLER
        case SMALLER:
            return new ParseTree(NonTerminal.Comp, Arrays.asList(match(Terminal.SMALLER)));
        default:
            throw new ParseException(current, NonTerminal.Comp, altComp);
        }
    }

    private ParseTree whileI() throws IOException, ParseException{
        // <While>        --> WHILE LPAREN Cond RPAREN DO Code END
            return new ParseTree(NonTerminal.While,
                                 Arrays.asList(
							match(Terminal.WHILE),
							match(Terminal.LPAREN),
                            cond(),
                            match(Terminal.RPAREN),
                            match(Terminal.DO),
                            code(),
                            match(Terminal.END)
                                               ));
    }


    private ParseTree print() throws IOException, ParseException{
        // <Print>        --> PRINT LPAREN VARNAME RPAREN
        return new ParseTree(NonTerminal.Print,
                             Arrays.asList(
                                          match(Terminal.PRINT),
                                          match(Terminal.LPAREN),
                                          match(Terminal.VARNAME),
                                          match(Terminal.RPAREN)
                                          ));
    }

    private ParseTree read() throws IOException, ParseException{
        // <Read>         --> READ LPAREN VARNAME RPAREN
        return new ParseTree(NonTerminal.Read,
                             Arrays.asList(
                                           match(Terminal.READ),
                                           match(Terminal.LPAREN),
                                           match(Terminal.VARNAME),
                                           match(Terminal.RPAREN)
                                           ));
    }
}



