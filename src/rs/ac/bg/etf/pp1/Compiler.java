package rs.ac.bg.etf.pp1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import java_cup.runtime.Symbol;
import rs.ac.bg.etf.pp1.ast.Program;
import rs.ac.bg.etf.pp1.ast.SyntaxNode;
import rs.ac.bg.etf.pp1.util.Log4JUtils;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;

public class Compiler {
	static {
		DOMConfigurator.configure(Log4JUtils.instance().findLoggerConfigFile());
		Log4JUtils.instance().prepareLogFile(Logger.getRootLogger());
	}

	public static void main(String[] args) throws Exception {
		Logger log = Logger.getLogger(Compiler.class);
		if (args.length < 2) {
			log.error("Not enough arguments supplied! Usage: MJParser <source-file> <obj-file> ");
			return;
		}

		File sourceCode = new File(args[0]);
		if (!sourceCode.exists()) {
			log.error("Source file [" + sourceCode.getAbsolutePath() + "] not found!");
			return;
		}

		log.info("Compiling source file: " + sourceCode.getAbsolutePath());

		try (BufferedReader br = new BufferedReader(new FileReader(sourceCode))) {
			Yylex lexer = new Yylex(br);
			MJParser p = new MJParser(lexer);
			Symbol s = p.parse(); // pocetak parsiranja
			SyntaxNode prog = (SyntaxNode) (s.value);

			Program progg = (Program) prog;

			Tab.init(); // Universe scope
			log.info(progg.toString(""));
			log.info("===================================");
			SemanticPass semanticCheck = new SemanticPass();
			prog.traverseBottomUp(semanticCheck);

			log.info("Print calls = " + semanticCheck.printCallCount);
			log.info("Read calls = " + semanticCheck.readCallCount);
			log.info("Broj promenljivih = " + semanticCheck.varCount);
			log.info("Broj nizova = " + semanticCheck.varArrayCount);
			log.info("Broj konstanti = " + semanticCheck.constCount);
			Tab.dump();

			if (!p.errorDetected && semanticCheck.passed()) {
				File objFile = new File(args[1]);
				log.info("Generating bytecode file: " + objFile.getAbsolutePath());
				if (objFile.exists())
					objFile.delete();

				// Code generation...
				CodeGenerator codeGenerator = new CodeGenerator();
				prog.traverseBottomUp(codeGenerator);
				Code.dataSize = semanticCheck.nVars;
				Code.mainPc = codeGenerator.getMainPc();
				if (codeGenerator.getMainPc() == -1) {
					log.error("Main metoda mora biti void tipa!");
					log.error("Parsiranje NIJE uspesno zavrseno!");
				} else if (codeGenerator.getMainPc() == -2) {
					log.error("Main metoda ne postoji!");
					log.error("Parsiranje NIJE uspesno zavrseno!");
				} else {
					Code.write(new FileOutputStream(objFile));
					log.info("Parsiranje uspesno zavrseno!");
				}
			} else {
				log.error("Parsiranje NIJE uspesno zavrseno!");
			}
		}
	}
}
