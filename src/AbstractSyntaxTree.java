import java.util.List;
import java.util.ArrayList;

/** Base on Léo Exibard ParseTree
 * A skeleton class to represent parse trees.
 *  The arity is not fixed: a node can have 0, 1 or more children.
 *  Trees are represented in the following way:
 *                        Tree :== TreeLabel * List<Tree>
 *  In other words, trees are defined recursively:
 *  A tree is a root (with a label of type TreeLabel) and a list of trees children.
 *  Thus, a leave is simply a tree with no children (its list of children is empty).
 *  This class can also be seen as representing the Node of a tree,
 *  in which case a tree is simply represented as its root.
 */

public class AbstractSyntaxTree {
    private TreeLabel label;             // The label of the root of the tree
    private List<AbstractSyntaxTree> children; // Its children, which are trees themselves

    /** Creates a singleton tree with only a root labeled by lbl.
     * @param lbl The label of the root
     */
    public AbstractSyntaxTree(TreeLabel lbl) {
        this.label = lbl;
        this.children = new ArrayList<AbstractSyntaxTree>(); // This tree has no children
    }

    /** Creates a singleton tree with only a root labeled by the variable nonTerm.
     * @param nonTerm The label of the root, given as a NonTerminal
     */
    public AbstractSyntaxTree(NonTerminal nonTerm) {
        this.label = new TreeLabel(nonTerm);
        this.children = new ArrayList<AbstractSyntaxTree>(); // This tree has no children
    }

    /** Creates a singleton tree with only a root labeled by the token tok
     * @param tok The label of the root, given as a Token
     */
    public AbstractSyntaxTree(Token tok) {
        this.label = new TreeLabel(tok);
        this.children = new ArrayList<AbstractSyntaxTree>(); // This tree has no children
    }

    /** Creates a singleton tree with only a root labeled by the terminal term
     * @param term The label of the root, given as a Terminal
     */
    public AbstractSyntaxTree(Terminal term) {
        this.label = new TreeLabel(new Token(term));
        this.children = new ArrayList<AbstractSyntaxTree>(); // This tree has no children
    }

    /** Creates a tree with root labeled by lbl and children chdn.
     * @param lbl The label of the root
     * @param chdn Its children
     */
    public AbstractSyntaxTree(TreeLabel lbl, List<AbstractSyntaxTree> chdn) {
        this.label = lbl;
        this.children = chdn;
    }
    /** Creates a tree with root labeled by the variable nonTerm and children chdn.
     * @param nonTerm The label of the root, given as a NonTerminal
     * @param chdn Its children
     */
    public AbstractSyntaxTree(NonTerminal nonTerm, List<AbstractSyntaxTree> chdn) {
        this.label = new TreeLabel(nonTerm);
        this.children = chdn;
    }

    /** Creates a tree with root labeled by the token tok and children chdn.
     * @param tok The label of the root, given as a Token
     * @param chdn Its children
     */
    public AbstractSyntaxTree(Token tok, List<AbstractSyntaxTree> chdn) {
        this.label = new TreeLabel(tok);
        this.children = chdn;
    }

    /**
     * Populates the AST recursively by looping through each parse tree
     * in the provided list.
     *
     * @param parseTreeList list of parse trees
     */
    public void populateAST(List<ParseTree> parseTreeList) {
        for (ParseTree parseTree : parseTreeList) {
            this.populateAST(parseTree);
        }
    /**
     * Populates the AST by using the provided parse tree.
     *
     * @param parseTree parse tree on which the AST is created from.
     */
    public void populateAST(ParseTree parseTree) {
        // If the parse tree is a leaf, then we can create a new AST node
        // with the token as the label.
        if (parseTree.isLeaf()) {
            this.addChild(new AbstractSyntaxTree(parseTree.getToken()));
        } else {
            // If the parse tree is not a leaf, then we need to create a new AST node
            // with the non-terminal as the label.
            AbstractSyntaxTree ast = new AbstractSyntaxTree(parseTree.getNonTerminal());
            // Then we need to populate the AST recursively.
            ast.populateAST(parseTree.getChildren());
            // Finally, we add the AST node to the current AST.
            this.addChild(ast);
        }


    /** Writes the tree as LaTeX code
     */
    public String toLaTexTree() {
        StringBuilder treeTeX = new StringBuilder();
        treeTeX.append("[");
        if (label.isEpsilon()){
            treeTeX.append("{$\\varepsilon$}");
        }
        else {
            treeTeX.append("{"+label.toTeX()+"}");
        }
        treeTeX.append(" ");
        for (AbstractSyntaxTree child: children) {
            treeTeX.append(child.toLaTexTree());
        }
        treeTeX.append("]");
        return treeTeX.toString();

    }

    /** Writes the tree as TikZ code.
     *  TikZ is a language to specify drawings in LaTeX files.
     */
    public String toTikZ() {
        StringBuilder treeTikZ = new StringBuilder();
        treeTikZ.append("node {");
        treeTikZ.append(label.toTeX());
        treeTikZ.append("}\n");
        for (AbstractSyntaxTree child: children) {
            treeTikZ.append("child { ");
            treeTikZ.append(child.toTikZ());
            treeTikZ.append(" }\n");
        }
        return treeTikZ.toString();
    }

    /** Writes the tree as a TikZ picture.
     *  A TikZ picture embeds TikZ code so that LaTeX undertands it.
     */
    public String toTikZPicture() {
        return "\\begin{tikzpicture}[tree layout]\n\\" + toTikZ() + ";\n\\end{tikzpicture}";
    }

    /** Writes the tree as a forest picture.
     *  Returns the tree in forest enviroment using the latex code of the tree
     */
    public String toForestPicture() {
        return "\\begin{forest}for tree={rectangle,draw, l sep=20pt}" + toLaTexTree() + ";\n\\end{forest}";
    }

    /** Writes the tree as a LaTeX document which can be compiled using PDFLaTeX,
     */
    public String toLaTeX() {
        return "\\documentclass[border=5pt]{standalone}\n\n\\usepackage{tikz}\n\\usepackage{forest}\n\n\\begin{document}\n\n" + toForestPicture() + "\n\n\\end{document}\n%% Local Variables:\n%% TeX-engine: pdflatex\n%% End:";
    }

    /** Writes the tree as a LaTeX document which can be compiled (using the LuaLaTeX engine).
     *  Be careful that such code will not compile with PDFLaTeX,
     *  since the tree drawing algorithm is written in Lua.
     *  The code is not very readable as such, but you can have a look at the outputted file
     *  if you want to understand better.
     */
    public String toLaTeXwithLua() {
        return "\\RequirePackage{luatex85}\n\\documentclass{standalone}\n\n\\usepackage{tikz}\n\n\\usetikzlibrary{graphdrawing, graphdrawing.trees}\n\n\\begin{document}\n\n" + toTikZPicture() + "\n\n\\end{document}\n%% Local Variables:\n%% TeX-engine: luatex\n%% End:";
    }


}
