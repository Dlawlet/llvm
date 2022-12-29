import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * Project Part 2: Parser
 *
 * @author Marie Van Den Bogaard, Léo Exibard, Gilles Geeraerts, edited by Sarah Winter
 * 
 */

public class ProcessGrammar{
    /**
     *
     * Takes a grammar txt file and computes first, follow sets, action table
     *
     * @param args  The argument(s) given to the program
     * @throws IOException java.io.IOException if an I/O-Error occurs
     * @throws FileNotFoundException java.io.FileNotFoundException if the specified file does not exist
     *
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, SecurityException, Exception{
        // Display the usage when no arguments are given
        if(args.length == 0){
            System.out.println("Usage:  java -jar ProcessGrammar.jar [OPTIONS] [FILES]\n"
                               + "\tOPTIONS:\n"
                               + "\t -pat (print-action-table): prints action table on screen\n"
                               + "\t -wat (write-action-table) filename.txt: writes action table to filename.txt\n"
                               + "\tFILES:\n"
                               + "\tA .txt file containing a grammar\n"
                               );
            System.exit(0);
        } else {
            boolean printActionTable = false;
            boolean writeActionTable = false;
            FileReader grammarSource = null;
            try {
                grammarSource = new FileReader(args[args.length-1]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            BufferedWriter bwActionTable = null;
            FileWriter fwActionTable = null;
            StringBuilder output = new StringBuilder();

            for (int i = 0 ; i < args.length; i++) {
                if (args[i].equals("-pat")) {
                    printActionTable = true;
                } else if (args[i].equals("-wat")) {
                    writeActionTable = true;
                    try {
                        fwActionTable = new FileWriter(args[i+1]);
                        bwActionTable = new BufferedWriter(fwActionTable);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (bwActionTable != null || printActionTable) {
                final GrammarReader grammarReader = new GrammarReader(grammarSource);
                Grammar grammar = grammarReader.getGrammar();
                output.append("********** The Grammar **********\n");
                System.out.print("test" + output);
                output.append(grammar);
                grammar.setFirst();
                output.append("\n********** First **********\n" + grammar.stringFirst());
                grammar.setFollow();
                output.append("\n********** Follow **********\n" + grammar.stringFollow());
                try {
                    grammar.setActionTable();
                    output.append("\n********** Action table **********\n");
                    output.append(grammar.stringActionTable());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                if (bwActionTable != null) {
                    try {
                        bwActionTable.write(output.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (bwActionTable != null)
                                bwActionTable.close();
                            if (fwActionTable != null)
                                fwActionTable.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                if (printActionTable) {
                    System.out.println(output);
                }
            }
        }
    }
}
