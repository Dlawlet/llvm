public class Token {
    public static final int UNDEFINED_POSITION = -1;
    public static final Object NO_VALUE = null;

    private final Terminal type;
    private final Object value;
    private final int line,column;

    public Token(Terminal unit,int line,int column,Object value){
        this.type	= unit;
        this.line	= line+1;
        this.column	= column;
        this.value	= value;
    }

    public Token(Terminal unit,int line,int column){
        this(unit,line,column,NO_VALUE);
    }

    public Token(Terminal unit,int line){
        this(unit,line,UNDEFINED_POSITION,NO_VALUE);
    }

    public Token(Terminal unit){
        this(unit,UNDEFINED_POSITION,UNDEFINED_POSITION,NO_VALUE);
    }

    public Token(Terminal unit,Object value){
        this(unit,UNDEFINED_POSITION,UNDEFINED_POSITION,value);
    }

    public boolean isEpsilon(){
        return (this.type != null && this.type.equals(Terminal.EPSILON));
    }

    public boolean isEndLine(){
        return (this.value != null && this.value.equals("\\n"));
    }

    public boolean isEOS(){
        return this.type.equals(Terminal.EOS);
    }

    public Terminal getType(){
        return this.type;
    }

    public Object getValue(){
        return this.value;
    }

    public int getLine(){
        return this.line;
    }

    public int getColumn(){
        return this.column;
    }

    @Override
    public int hashCode(){
        final String value	= this.value != null? this.value.toString() : "null";
        final String type		= this.type  != null? this.type.toString()  : "null";
        return new String(value+"_"+type).hashCode();
    }

    @Override
    public String toString(){
        final String value	= this.value != null? this.value.toString() : "null";
        final String type		= this.type  != null? this.type.toString()  : "null";
        return String.format("token: %-15slexical unit: %s", value, type);
    }

    public String toTeX(){
        if (this.isEpsilon()){
            return "$\\varepsilon$";
        } else if (this.isEOS()){
            return "$\\dashv$";
        } else if (this.isEndLine()){
                return "ENDLINE \\textbackslash n";
        } else {
            final String value = this.value != null? this.value.toString() : "";
            final String type  = this.type  != null? this.type.toString()  : "";
            return type+" \\texttt{"+value+"}";
        }
    }
}
