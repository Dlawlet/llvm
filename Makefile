all:
	jflex src/LexicalAnalyzer.flex
	jflex src/GrammarReader.flex
	javac -d bin -cp src/ src/*.java
	mkdir -p dist/
	jar cfe dist/Part2.jar WriteTree -C bin .
	jar cfe dist/ProcessGrammar.jar ProcessGrammar -C bin .
	# javadoc -private -html5 src/*.java -d doc/javadoc

clean:
	rm -f src/*.class
	rm -f src/*.java~
	rm -f bin/*.class
	rm -f dist/*.jar
	rm -f test-results/*.aux
	rm -f test-results/*.log

.PHONY: test
test:
	java -jar dist/ProcessGrammar.jar -pat more/Fortress_Grammar.txt
	java -jar dist/ProcessGrammar.jar -wat more/Action-table.txt more/Fortress_Grammar.txt
	# java -jar dist/Part2.jar -wt 07-ifThen.tex test/07-ifThen.fs
	# java -jar dist/Part2.jar -wt 07-voidIfThen.tex test/07-voidIfThen.fs
	# pdflatex 07-voidIfThen.tex