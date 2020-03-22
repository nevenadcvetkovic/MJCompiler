package rs.ac.bg.etf.pp1;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import rs.ac.bg.etf.pp1.ast.*;

import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.*;

public class SemanticPass extends VisitorAdaptor {

	boolean errorDetected = false;
	int printCallCount = 0, readCallCount = 0, varCount = 0, varArrayCount = 0, constCount = 0;
	Obj currentMethod = null;
	int returnFound = 0;
	int nVars;
	boolean functionCalled = false;
	boolean equalOperator = false, forLoop = false;
	int paramCount = 0;
	ArrayList<Struct> currentParamList = null;
	ArrayList<ArrayList<Struct>> currParamStack = new ArrayList<>();
	private Struct currType = null;
	private int nVarsFE=-1;

	public SemanticPass() {
		Struct struct = new Struct(Struct.Int);
		Tab.insert(Obj.Type, "bool", struct);

	}

	Logger log = Logger.getLogger(getClass());

	public void report_error(String message, SyntaxNode info) {
		errorDetected = true;
		StringBuilder msg = new StringBuilder(message);
		int line = (info == null) ? 0 : info.getLine();
		if (line != 0)
			msg.append(" na liniji ").append(line);
		log.error(msg.toString());
	}

	public void report_info(String message, SyntaxNode info) {
		StringBuilder msg = new StringBuilder(message);
		int line = (info == null) ? 0 : info.getLine();
		if (line != 0)
			msg.append(" na liniji ").append(line);
		log.info(msg.toString());
	}

	public void visit(Program program) {
		nVars = Tab.currentScope.getnVars();
		Tab.chainLocalSymbols(program.getProgName().obj);
		Tab.closeScope();
	}

	public void visit(ProgName progName) {
		progName.obj = Tab.insert(Obj.Prog, progName.getPName(), Tab.noType);
		Tab.openScope();
	}

	public void visit(FormalParamDecl formalParDecl) {
		if (Tab.currentScope().findSymbol(formalParDecl.getName()) != null) {
			report_error("Greska na liniji " + formalParDecl.getLine() + ": Parametar " + formalParDecl.getName()
					+ " je vec deklarisan!", null);

		} else {
			if(formalParDecl.getSquares() instanceof NoSquares) {
				report_info("Deklarisan parametar " + formalParDecl.getName(), formalParDecl);
				Obj parNode = Tab.insert(Obj.Var, formalParDecl.getName(), formalParDecl.getType().struct);
				parNode.setFpPos(++paramCount);
			}else {
				report_info("Deklarisan parametar " + formalParDecl.getName(), formalParDecl);
				Struct type = new Struct(Struct.Array, formalParDecl.getType().struct);
				Obj parNode = Tab.insert(Obj.Var, formalParDecl.getName(), type);
				parNode.setFpPos(++paramCount);
			}
		}

	}

	public void visit(VarIdent varIdent) {
		if (Tab.currentScope().findSymbol(varIdent.getVarName()) != null) {
			report_error("Greska na liniji " + varIdent.getLine() + ": Promenljiva " + varIdent.getVarName()
					+ " je vec deklarisana!", null);

		} else {
			if (varIdent.getVarSquares() instanceof NoVarSquares) {
				report_info("Deklarisana promenljiva " + varIdent.getVarName(), varIdent);
				Tab.insert(Obj.Var, varIdent.getVarName(), currType);
				varCount++;
			} else {
				report_info("Deklarisan niz " + varIdent.getVarName(), varIdent);
				Struct type = new Struct(Struct.Array, currType);
				Tab.insert(Obj.Var, varIdent.getVarName(), type);
				varArrayCount++;
			}

		}
	}

	// CONST
	public void visit(ConstIdent constDecl) {
		if (Tab.currentScope().findSymbol(constDecl.getConstName()) != null) {
			report_error("Greska na liniji " + constDecl.getLine() + ": Konstanta " + constDecl.getConstName()
					+ " je vec deklarisana!", null);

		} else if (!currType.compatibleWith(constDecl.getConstType().struct)) {
			report_error("Greska na liniji " + constDecl.getLine() + ": Greska u definiciji konstante  "
					+ constDecl.getConstName() + "! Tipovi nisu kompatibilni!", null);

		} else {
			int value = 0;
			if (constDecl.getConstType() instanceof NumConst) {
				value = ((NumConst) constDecl.getConstType()).getValue();
			} else if (constDecl.getConstType() instanceof CharConst) {
				value = ((CharConst) constDecl.getConstType()).getValue();
			} else if (constDecl.getConstType() instanceof BoolConst) {
				value = ((BoolConst) constDecl.getConstType()).getValue().equals("true") ? 1 : 0;
			}
			Obj con = Tab.insert(Obj.Con, constDecl.getConstName(), constDecl.getConstType().struct);
			con.setAdr(value);
			constDecl.struct = con.getType();
			report_info("Deklarisana konstanta " + constDecl.getConstName() + " i ima vrednost " + value, constDecl);
		}
	}

	public void visit(NumConst numDecl) {
		numDecl.struct = Tab.intType;
	}

	public void visit(CharConst charDecl) {
		charDecl.struct = Tab.charType;
	}

	public void visit(BoolConst boolDecl) {
		boolDecl.struct = Tab.find("bool").getType();
	}

	public void visit(SQExp sqExpr) {
		Struct struct = sqExpr.getExpr().struct;
		if (struct.getElemType() != null) {
			struct = struct.getElemType();
		}
		if (!struct.compatibleWith(Tab.find("int").getType())) {
			report_error("Neuspesan pristup nizu", sqExpr);
		} else {

		}
	}

	public void visit(Type type) {
		Obj typeNode = Tab.find(type.getTypeName());
		if (typeNode == Tab.noObj) {
			report_error("Nije pronadjen tip " + type.getTypeName() + " u tabeli simbola", null);
			type.struct = Tab.noType;
		} else {
			if (Obj.Type == typeNode.getKind()) {
				type.struct = typeNode.getType();
			} else {
				report_error("Greska: Ime " + type.getTypeName() + " ne predstavlja tip ", type);
				type.struct = Tab.noType;
			}
		}
		currType = type.struct;
	}

	public void visit(MethodDecl methodDecl) {
		if (returnFound == 0 && currentMethod.getType() != Tab.noType) {
			report_error("Semanticka greska na liniji " + methodDecl.getLine() + ": funcija " + currentMethod.getName()
					+ " nema return iskaz!", null);
		}

		Tab.chainLocalSymbols(currentMethod);
		Tab.closeScope();

		returnFound = 0;
		paramCount = 0;
		currentMethod = null;
	}

	public void visit(MethodTypeName methodTypeName) {
		currentMethod = Tab.insert(Obj.Meth, methodTypeName.getMethName(), methodTypeName.getReturnType().struct);

		methodTypeName.obj = currentMethod;
		Tab.openScope();
		report_info("Obradjuje se funkcija " + methodTypeName.getMethName(), methodTypeName);

	}

	public void visit(ReturnTypes retType) {
		retType.struct = retType.getType().struct;
	}

	public void visit(VoidType voidType) {
		voidType.struct = Tab.noType;
	}

	private boolean checkAssignable(Struct dstStruct, boolean dest, Struct srcStruct, boolean src, int line) {
		if (dest && src) {
			if (srcStruct.getElemType() != null && !srcStruct.getElemType().assignableTo(dstStruct.getElemType())) {
				report_error("Greska na liniji " + line + " : " + "nekompatibilni tipovi u dodeli vrednosti ", null);
				return false;
			}
		} else if (dest) {
			if (!srcStruct.assignableTo(dstStruct.getElemType())) {
				report_error("Greska na liniji " + line + " : " + "nekompatibilni tipovi u dodeli vrednosti ", null);
				return false;
			}
		} else if (src) {
			if (srcStruct.getElemType() != null && !srcStruct.getElemType().assignableTo(dstStruct)) {
				report_error("Greska na liniji " + line + " : " + "nekompatibilni tipovi u dodeli vrednosti ", null);
				return false;
			}

		} else if (!srcStruct.assignableTo(dstStruct)) {
			report_error("Greska na liniji " + line + " : " + "nekompatibilni tipovi u dodeli vrednosti ", null);
			return false;
		}

		return true;
	}

	public void visit(DesignEqExpr somthAssigned) {
		Struct srcStruct = somthAssigned.getExpr().struct;
		Struct dstStruct = somthAssigned.getDesignator().obj.getType();
		boolean src = srcStruct.getKind() == Struct.Array;
		boolean dest = dstStruct.getKind() == Struct.Array;

		checkAssignable(dstStruct, dest, srcStruct, src, somthAssigned.getLine());
	}

	public void visit(PrintStmt printStmt) {
		Struct argument = printStmt.getExpr().struct;
		if (argument.getElemType() != null) {
			argument = argument.getElemType();
		}
		Struct intArg = Tab.find("int").getType();
		Struct charArg = Tab.find("char").getType();
		Struct boolArg = Tab.find("bool").getType();
		if (!argument.compatibleWith(intArg) && !argument.compatibleWith(charArg)
				&& !argument.compatibleWith(boolArg)) {
			report_error("Greska na liniji " + printStmt.getLine() + ": Argument funkcije print nije int, char, bool!",
					null);
		} else
			printCallCount++;
	}

	public void visit(ReadDesign readDesign) {
		Obj argument = readDesign.getDesignator().obj;
		Struct intArg = Tab.find("int").getType();
		Struct charArg = Tab.find("char").getType();
		Struct boolArg = Tab.find("bool").getType();
		if (argument.getKind() != Obj.Var && argument.getType().getKind() != Struct.Array) {
			report_error("Greska na liniji " + readDesign.getLine()
					+ ": Argument funkcije read nije promenljiva ni element niza!", null);
		} else {
			Struct struct = argument.getType();
			if (struct.getElemType() != null) {
				struct = struct.getElemType();
			}
			if (!struct.compatibleWith(intArg) && !struct.compatibleWith(charArg) && !struct.compatibleWith(boolArg)) {
				report_error("Greska na liniji " + readDesign.getLine()
						+ ": Argument funkcije read nije tipa int, char, bool!", null);
			} else
				readCallCount++;
		}
	}

	// ideja je da u func ret val samo sacuvamo tip povratnog i ovde uporedimo
	public void visit(ReturnExpr returnExpr) {
		returnFound = 1;
		if (currentMethod != null) {
			Struct currMethType = currentMethod.getType();
			if (!currMethType.compatibleWith(returnExpr.getExpr().struct)) {
				report_error("Greska na liniji " + returnExpr.getLine() + " : "
						+ "tip izraza u return naredbi ne slaze se sa tipom povratne vrednosti funkcije "
						+ currentMethod.getName(), null);
			} else {
				report_info("Funkcija " + currentMethod.getName() + " uspesno definisana", returnExpr);
			}
		} else {// detektuje se vec u sintaksnoj
			report_error(
					"Greska na liniji " + returnExpr.getLine() + " : " + " Return naredba se nalazi van tela funcije!",
					null);
		}
	}

	public void visit(ReturnNoExpr returnExpr) {
		returnFound = 2;
		if (currentMethod != null) {
			Struct currMethType = currentMethod.getType();
			if (currMethType.getKind() != Struct.None) {
				report_error("Greska na liniji " + returnExpr.getLine() + " : "
						+ "tip izraza u return naredbi ne slaze se sa tipom povratne vrednosti funkcije "
						+ currentMethod.getName(), null);
			} else {
				report_info("Funkcija " + currentMethod.getName() + " uspesno definisana", returnExpr);
			}
		} else {
			report_error(
					"Greska na liniji " + returnExpr.getLine() + " : " + " Return naredba se nalazi van tela funcije!",
					null);
		}
	}

	public void visit(FactNumConst numDecl) {
		numDecl.struct = Tab.find("int").getType();
	}

	public void visit(FactCharConst charDecl) {
		charDecl.struct = Tab.find("char").getType();
	}

	public void visit(FactBoolConst boolDecl) {
		boolDecl.struct = Tab.find("bool").getType();

	}

	public void visit(FactorFuncParen func) {
		functionCalled = true;

	}

	private void processDefaultMeth(Obj method, String name, int line, SyntaxNode node) {
		ArrayList<Obj> localSymbols = new ArrayList<>(method.getLocalSymbols());
		if (currentParamList.size() == 0 || currentParamList.size() > 1) {
			report_error("Greska na liniji " + line
					+ " : Broj stvarnih parametara se razlikuje od broja parametara funkcije!", null);
			while (currentParamList.size() > 0)
				currentParamList.remove(0);
		} else if (!localSymbols.get(0).getType().compatibleWith(currentParamList.get(0))) {
			report_error("Greska na liniji " + line
					+ " : Tip stvarnog parametra se ne poklapa sa formalnim parametrom funkcije!", null);
			currentParamList.remove(0);
		} else {
			report_info("Poziv funkcije " + name, node);
			report_info(method.toString(), node);
			currentParamList.remove(0);
		}

	}

	private ArrayList<Obj> getLocalSymbols(Obj method) {
		if (currentMethod.equals(method)) {
			return new ArrayList<>(Tab.currentScope().getLocals().symbols());
		} else {
			return new ArrayList<>(method.getLocalSymbols());
		}
	}

	public void visit(FactorVar factVar) {
		factVar.struct = factVar.getDesignator().obj.getType();
		int currParam = 1;
		int i;
		boolean stop = false, work = true;
		String name = null;
		if (functionCalled && factVar.getDesignator() instanceof DesignatorName) {
			name = ((DesignatorName) factVar.getDesignator()).getName();
			functionCalled = false;
			Obj method = Tab.find(name);
			if (method.getKind() != Obj.Meth) {
				report_error("Greska na liniji " + factVar.getLine() + " : Tip " + name + " nije funkcija!", null);
			} else if (method.getKind() == Obj.Meth && method.getType() == Tab.noType) {
				report_error("Greska na liniji " + factVar.getLine() + " : Funkcija " + name
						+ " ima povratni tip void i ne moze se koristiti u izrazima!", null);

			} else if (name.equals("ord") || name.equals("chr") || name.equals("len")) {
				processDefaultMeth(method, name, factVar.getLine(), factVar);
			} else {
				ArrayList<Obj> localSymbols = getLocalSymbols(method);
				if (currentParamList.size() == 0) {
					for (i = 0; i < localSymbols.size() && !stop; i++) {
						if (localSymbols.get(i).getFpPos() == currParam) {
							stop = true;
						}
					}
					if (stop) {
						report_error(
								"Greska na liniji " + factVar.getLine()
										+ " : Broj stvarnih parametara se razlikuje od broja parametara funkcije!",
								null);
					} else
						report_info("Poziv funkcije " + name, factVar);
				} else {
					while (currentParamList.size() > 0 && work) {
						stop = false;
						Struct expr = currentParamList.get(0);

						for (i = 0; i < localSymbols.size() && !stop; i++) {
							if (localSymbols.get(i).getFpPos() == currParam) {
								stop = true;
								currParam++;
							}
						}
						if (i == localSymbols.size() && !stop) {
							report_error(
									"Greska na liniji " + factVar.getLine()
											+ " : Broj stvarnih parametara se razlikuje od broja parametara funkcije!",
									null);
							work = false;
						} else {
							i--;
							if (!localSymbols.get(i).getType().compatibleWith(expr)) {
								report_error("Greska na liniji " + factVar.getLine()
										+ " : Tip stvarnog parametra se ne poklapa sa formalnim parametrom funkcije!",
										null);
								work = false;
							} else {
								currentParamList.remove(0);
								report_info("Postavljen formalni parametar " + localSymbols.get(i).getName(), factVar);
							}
						}
					}
					if (currentParamList.size() == 0) {
						report_info("Poziv funkcije " + name, factVar);
						report_info(factVar.toString(""), factVar);
					}
					while (currentParamList.size() > 0)
						currentParamList.remove(0);
					currParamStack.remove(currParamStack.size() - 1);
					if (currParamStack.size() > 0)
						currentParamList = currParamStack.get(currParamStack.size() - 1);

				}
			}
		} else if (functionCalled && !(factVar.getDesignator() instanceof DesignatorName)) {
			report_error("Greska na liniji " + factVar.getLine() + " : Identifikator nije funkcija!", null);
			functionCalled = false;
		}

	}

	public void visit(FactorExpr factExpr) {
		factExpr.struct = factExpr.getExpr().struct;
	}

	public void visit(MulTerm mulTerm) {
		if (mulTerm.getMulOp() instanceof MulLeft) {
			Struct structF = mulTerm.getFactor().struct;
			if (structF.getElemType() != null) {
				structF = structF.getElemType();
			}
			Struct structT = mulTerm.getTerm().struct;
			if (structT.getElemType() != null) {
				structT = structT.getElemType();
			}

			if (!structF.compatibleWith(Tab.find("int").getType())
					|| !structT.compatibleWith(Tab.find("int").getType())) {
				report_error("Greska na liniji " + mulTerm.getLine() + " : Operand mora biti tipa int!", null);
			}
		} else {
			Struct srcStruct = mulTerm.getFactor().struct;
			Struct dstStruct = mulTerm.getTerm().struct;
			boolean src = srcStruct.getKind() == Struct.Array;
			boolean dest = dstStruct.getKind() == Struct.Array;
			checkAssignable(dstStruct, dest, srcStruct, src, mulTerm.getLine());
		}

		mulTerm.struct = mulTerm.getFactor().struct;
	}

	public void visit(TermFactor mulTerm) {
		mulTerm.struct = mulTerm.getFactor().struct;
	}

	public void visit(TermExpr termExpr) {
		termExpr.struct = termExpr.getTerm().struct;
	}

	public void visit(AddExpr addExpr) {
		if (addExpr.getAddOp() instanceof AddOp) {
			Struct structT = addExpr.getTerm().struct;
			Struct structE = addExpr.getExpr().struct;
			if (structT.getElemType() != null) {
				structT = structT.getElemType();
			}
			if (structE.getElemType() != null) {
				structE = structE.getElemType();
			}
			if (!structT.compatibleWith(Tab.find("int").getType())
					|| !structE.compatibleWith(Tab.find("int").getType())) {
				report_error("Greska na liniji " + addExpr.getLine() + " : Operand mora biti tipa int", null);
			}
		} else {
			Struct srcStruct = addExpr.getTerm().struct;
			Struct dstStruct = addExpr.getExpr().struct;
			boolean src = srcStruct.getKind() == Struct.Array;
			boolean dest = dstStruct.getKind() == Struct.Array;
			checkAssignable(dstStruct, dest, srcStruct, src, addExpr.getLine());
		}
		addExpr.struct = addExpr.getTerm().struct;
	}

	public void visit(MinusExpr expr) {
		expr.struct = expr.getTerm().struct;
		if (!expr.struct.compatibleWith(Tab.find("int").getType())) {
			report_error("Greska na liniji " + expr.getLine() + ": Minus nije ispred int!", null);
		}
	}

	public void visit(DesignatorClass designatorClass) {
		Designator design = designatorClass.getDesignator();
		while (!(design instanceof DesignatorName)) {
			design = ((DesignatorClass) design).getDesignator();
		}
		DesignatorName designName = (DesignatorName) design;
		Obj obj = Tab.find(designName.getName());
		if (obj == Tab.noObj) {
			report_error("Greska na liniji " + designatorClass.getLine() + " : Ime " + designName.getName()
					+ " nije deklarisano! ", null);
		} else if (obj.getType().getKind() != Struct.Array) {
			report_error("Greska na liniji " + designatorClass.getLine() + " : Ime " + designName.getName()
					+ " nije tipa niz!", null);
		} else {
			designatorClass.obj = design.obj;
			report_info("Pristup nizu ", designatorClass);
			report_info(designatorClass.toString(""), designatorClass);
		}
	}

	public void visit(DesignatorName designator) {
		Obj obj = Tab.find(designator.getName());
		if (obj == Tab.noObj) {
			report_error("Greska na liniji " + designator.getLine() + " : Ime " + designator.getName()
					+ " nije deklarisano! ", null);
			designator.obj = obj;
		} else {
			designator.obj = obj;
			if (obj.getFpPos() > 0)
				report_info("Koriscenje formalnog parametra ", designator);
			else if (obj.getKind() == Obj.Con)
				report_info("Koriscenje konstante ", designator);
			else if (obj.getKind() == Obj.Var)
				report_info("Koriscenje promenljive ", designator);
			report_info(designator.toString(""), designator);

			if (designator.getParent().getClass() == ForEach.class) {
				ForEach parrent = (ForEach) designator.getParent();
				if (obj.getType().getElemType() == null) {
					report_error("Greska na liniji " + designator.getLine() + " : Ime " + designator.getName()
							+ " nije tipa niz! ", null);
				} else {
					if(nVarsFE==-1)
						nVarsFE=Tab.currentScope().getnVars();
					Tab.openScope();
					parrent.getIdent().obj = Tab.insert(Obj.Var, parrent.getIdent().getName(),
							obj.getType().getElemType());
					parrent.getIdent().obj.setAdr(nVarsFE++);
				}
			}
		}
	}

	public void visit(ForEach forEach) {
		Tab.closeScope();		
		
	}

	public void visit(ConstFactor constFactor) {
		constFactor.struct = constFactor.getFactConstType().struct;
	}

	public void visit(FactorNew newFactor) {
		newFactor.struct = newFactor.getType().struct;

	}

	public void visit(NewFactorExp newFactorExp) {
		newFactorExp.struct = newFactorExp.getExpr().struct;
		if (!newFactorExp.struct.compatibleWith(Tab.find("int").getType())) {
			report_error("Greska na liniji " + newFactorExp.getLine() + " : broj elemenata niza mora biti int!", null);
		}
	}

	public void visit(NoNewFactorExpr noNewFactorExpr) {
		noNewFactorExpr.struct = Tab.noObj.getType();
	}

	public void visit(DesignFunc designFunc) {
		if (designFunc.getDesignator().obj.getKind() != Obj.Meth
				&& designFunc.getDesignator() instanceof DesignatorName) {
			report_error("Greska na liniji " + designFunc.getLine() + " : Identifikator "
					+ ((DesignatorName) designFunc.getDesignator()).getName() + " nije funkcija!", null);
		} else if (!(designFunc.getDesignator() instanceof DesignatorName)) {
			report_error("Greska na liniji " + designFunc.getLine() + " : Identifikator nije funkcija!", null);
		} else {
			int currParam = 1;
			int i;
			boolean stop = false, work = true;

			String name = ((DesignatorName) designFunc.getDesignator()).getName();
			Obj method = Tab.find(name);
			ArrayList<Obj> localSymbols = getLocalSymbols(method);
			if (name.equals("ord") || name.equals("chr") || name.equals("len")) {
				processDefaultMeth(method, name, designFunc.getLine(), designFunc);
			} else if (currentParamList.size() == 0) {
				for (i = 0; i < localSymbols.size() && !stop; i++) {
					if (localSymbols.get(i).getFpPos() == currParam) {
						stop = true;
					}
				}
				if (stop) {
					report_error("Greska na liniji " + designFunc.getLine()
							+ " : Broj stvarnih parametara se razlikuje od broja parametara funkcije!", null);
				} else
					report_info("Poziv funkcije " + ((DesignatorName) designFunc.getDesignator()).getName(),
							designFunc);
			} else {
				while (currentParamList.size() > 0 && work) {
					stop = false;
					Struct expr = currentParamList.get(0);

					for (i = 0; i < localSymbols.size() && !stop; i++) {
						if (localSymbols.get(i).getFpPos() == currParam) {
							stop = true;
							currParam++;
						}
					}
					if (i == localSymbols.size() && !stop) {
						report_error(
								"Greska na liniji " + designFunc.getLine()
										+ " : Broj stvarnih parametara se razlikuje od broja parametara funkcije!",
								null);
						work = false;
					} else {
						i--;
						if (!localSymbols.get(i).getType().compatibleWith(expr)) {
							report_error("Greska na liniji " + designFunc.getLine()
									+ " : Tip stvarnog parametra se ne poklapa sa formalnim parametrom funkcije!",
									null);
							work = false;
						} else {
							currentParamList.remove(0);
							report_info("Postavljen formalni parametar " + localSymbols.get(i).getName(), designFunc);
						}
					}
				}
				if (currentParamList.size() == 0) {
					report_info("Poziv funkcije " + ((DesignatorName) designFunc.getDesignator()).getName(),
							designFunc);
					report_info(designFunc.toString(""), designFunc);
				}
				while (currentParamList.size() > 0)
					currentParamList.remove(0);
				currParamStack.remove(currParamStack.size() - 1);
				if (currParamStack.size() > 0)
					currentParamList = currParamStack.get(currParamStack.size() - 1);
			}

		}
	}

	public void visit(DesignInc designInc) {// mora provera i da li je promenljiva ili elem niza
		Struct srcStruct = Tab.intType;
		Struct dstStruct = designInc.getDesignator().obj.getType();
		boolean src = srcStruct.getKind() == Struct.Array;
		boolean dest = dstStruct.getKind() == Struct.Array;
		checkAssignable(dstStruct, dest, srcStruct, src, designInc.getLine());
		Struct struct = designInc.getDesignator().obj.getType();
		if (struct.getElemType() != null) {
			struct = struct.getElemType();
		}
		if (struct.compatibleWith(Tab.find("int").getType())) {
			designInc.struct = struct;

		} else {
			report_error("Greska na liniji " + designInc.getLine() + " : Tip promenljive "
					+ ((DesignatorName) designInc.getDesignator()).getName() + " nije int!", null);
		}

	}

	public void visit(DesignDec designDec) {// mora provera i da li je promenljiva ili elem niza
		Struct srcStruct = Tab.intType;
		Struct dstStruct = designDec.getDesignator().obj.getType();
		boolean src = srcStruct.getKind() == Struct.Array;
		boolean dest = dstStruct.getKind() == Struct.Array;
		checkAssignable(dstStruct, dest, srcStruct, src, designDec.getLine());
		Struct struct = designDec.getDesignator().obj.getType();
		if (struct.getElemType() != null) {
			struct = struct.getElemType();
		}
		if (struct.compatibleWith(Tab.find("int").getType())) {
			designDec.struct = struct;

		} else {
			report_error("Greska na liniji " + designDec.getLine() + " : Tip promenljive "
					+ ((DesignatorName) designDec.getDesignator()).getName() + " nije int!", null);
		}

	}

	public void visit(RelExpression relExpression) {
		relExpression.struct = relExpression.getExpr().struct;
		if (relExpression.struct.compatibleWith(Tab.find("arr").getType()) && !equalOperator) {
			report_error(
					"Greska na liniji " + relExpression.getLine() + " : Nizovi se ne mogu porediti ovim operatorom!",
					null);
		}
		equalOperator = false;
	}

	public void visit(NoRelop noRelop) {
		noRelop.struct = Tab.noObj.getType();

	}

	// mozda je ono za niz u pitanju
	public void visit(CondFact condFact) {
		Struct struct = condFact.getExpr().struct;
		Obj boolObj = Tab.find("bool");
		if (struct.getElemType() != null) {
			struct = struct.getElemType();
		}

		if (condFact.getRelExpr().struct.compatibleWith(Tab.noObj.getType())) {
			if (!struct.compatibleWith(boolObj.getType()))
				report_error("Greska na liniji " + condFact.getLine() + " : Tip promenljive nije boolean!", null);
		} else if (!struct.compatibleWith(condFact.getRelExpr().struct)) {
			report_error("Greska na liniji " + condFact.getLine() + " : Tipovi nisu kompatibilni!", null);
		}

		condFact.obj = boolObj;

	}

	// nema potrebe da se proverava ovde, samo gore
	public void visit(SingleFact singleFact) {
		singleFact.obj = singleFact.getCondFact().obj;
	}

	public void visit(CondTerms condTerms) {
		condTerms.obj = condTerms.getCondFact().obj;
		if (condTerms.obj.getType().compatibleWith(Tab.noType)) {
			condTerms.obj = Tab.find("bool");
		}
	}

	public void visit(SingleCondition singleCondition) {
		singleCondition.obj = singleCondition.getCondTerm().obj;
	}

	public void visit(Conditions condition) {
		condition.obj = condition.getCondTerm().obj;

	}

	public void visit(ForStmtDesign forDesignSt) {
		forLoop = true;
	}

	public void visit(NoForDesign forDesignSt) {
		forLoop = true;
	}

	public void visit(ForStm forStm) {
		forLoop = false;
		report_info(forStm.toString(""), forStm);
	}

	public void visit(BreakSt breakSt) {
		if (!forLoop) {
			report_error("Greska na liniji " + breakSt.getLine() + " : Naredba break van for petlje!", null);
		}
	}

	public void visit(ContinueSt contSt) {
		if (!forLoop) {
			report_error("Greska na liniji " + contSt.getLine() + " : Naredba continue van for petlje!", null);
		}
	}

	public void visit(Equal equal) {
		equalOperator = true;
	}

	public void visit(NotEqual equal) {
		equalOperator = true;
	}

	public boolean passed() {
		return !errorDetected;
	}

	public void visit(ActualParams actualParams) {
		actualParams.struct = actualParams.getExpr().struct;
		Struct struct = actualParams.struct;
	/*	while (struct != null && struct.getElemType() != null) {
			struct = struct.getElemType();
		}*/
		currentParamList.add(struct);

	}

	public void visit(ActualParam actualParam) {
		actualParam.struct = actualParam.getExpr().struct;
		Struct struct = actualParam.struct;
		/*while (struct != null && struct.getElemType() != null) {
			struct = struct.getElemType();
		}*/
		currentParamList.add(struct);
	}

	public void visit(LFuncParen lFuncParen) {
		currParamStack.add(new ArrayList<>());
		currentParamList = currParamStack.get(currParamStack.size() - 1);

	}

}
