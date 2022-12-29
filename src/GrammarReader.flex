import java.util.regex.PatternSyntaxException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

%%// Options of the scanner

%class GrammarReader // Name
%unicode               // Use unicode
%line                  // Use line counter (yyline variable)
%column                // Use character counter by line (yycolumn variable)
%type Grammar
%function getGrammar
%yylexthrow PatternSyntaxException

%{
    private NonTerminal currentVariable;
    private ArrayList<Symbol> currentRHS = new ArrayList<Symbol>();
    private Grammar grammar = new Grammar(NonTerminal.Program);

    public NonTerminal getNonTerminal(String nonTerm) {
        return NonTerminal.valueOf(nonTerm.substring(1, nonTerm.length() - 1));
    }

%}

%eofval{
	return grammar;
%eofval}

//Extended Regular Expressions

AlphaUpperCase = [A-Z]
AlphaLowerCase = [a-z]
Alpha          = {AlphaUpperCase}|{AlphaLowerCase}

RightArrow     = "-->"
Variable       = <{Alpha}+>
Terminal       = {AlphaUpperCase}+
Epsilon        = "epsilon"
LineFeed       = "\n"
CarriageReturn = "\r"
EndOfLine      = ({LineFeed}{CarriageReturn}?) | ({CarriageReturn}{LineFeed}?)
Space          = (\t | \f | " ")
Spaces         = {Space}+

//Declare exclusive states
%xstate RHS

%%// Identification of tokens

<YYINITIAL> {
    {Variable}         {currentVariable = getNonTerminal(yytext());}
    {RightArrow}       {yybegin(RHS);}
    {Spaces}           {}
    [^]                {throw new PatternSyntaxException("Unmatched token, out of symbols",yytext(),yyline);} // unmatched token gives an error
}

<RHS> {
    {Variable}         {currentRHS.add(new Symbol(getNonTerminal(yytext())));}
    {Terminal}         {currentRHS.add(new Symbol(Terminal.valueOf(yytext())));}
    {Epsilon}          {currentRHS.add(new Symbol(Terminal.EPSILON));}
    {Spaces}           {}
    {EndOfLine}        {grammar.addRule(currentVariable, Collections.unmodifiableList(currentRHS));
                        currentRHS = new ArrayList<Symbol>();
                        yybegin(YYINITIAL);}
    [^]                {throw new PatternSyntaxException("Unmatched token, out of symbols",yytext(),yyline);} // unmatched token gives an error
}
