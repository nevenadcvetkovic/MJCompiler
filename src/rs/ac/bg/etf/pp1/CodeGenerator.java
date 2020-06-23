package rs.ac.bg.etf.pp1;

import java.util.ArrayList;
import java.util.HashMap;

import rs.ac.bg.etf.pp1.CounterVisitor.FormParamCounter;
import rs.ac.bg.etf.pp1.CounterVisitor.VarCounter;
import rs.ac.bg.etf.pp1.ast.Add;
import rs.ac.bg.etf.pp1.ast.AddAss;
import rs.ac.bg.etf.pp1.ast.AddEq;
import rs.ac.bg.etf.pp1.ast.AddExpr;
import rs.ac.bg.etf.pp1.ast.AddLeft;
import rs.ac.bg.etf.pp1.ast.AddRight;
import rs.ac.bg.etf.pp1.ast.BreakSt;
import rs.ac.bg.etf.pp1.ast.CondFact;
import rs.ac.bg.etf.pp1.ast.Conditions;
import rs.ac.bg.etf.pp1.ast.ContinueSt;
import rs.ac.bg.etf.pp1.ast.DesignDec;
import rs.ac.bg.etf.pp1.ast.DesignEqExpr;
import rs.ac.bg.etf.pp1.ast.DesignFunc;
import rs.ac.bg.etf.pp1.ast.DesignInc;
import rs.ac.bg.etf.pp1.ast.Designator;
import rs.ac.bg.etf.pp1.ast.DesignatorClass;
import rs.ac.bg.etf.pp1.ast.DesignatorName;
import rs.ac.bg.etf.pp1.ast.Div;
import rs.ac.bg.etf.pp1.ast.DivEq;
import rs.ac.bg.etf.pp1.ast.Else;
import rs.ac.bg.etf.pp1.ast.Equal;
import rs.ac.bg.etf.pp1.ast.Expr;
import rs.ac.bg.etf.pp1.ast.FactBoolConst;
import rs.ac.bg.etf.pp1.ast.FactCharConst;
import rs.ac.bg.etf.pp1.ast.FactNumConst;
import rs.ac.bg.etf.pp1.ast.FactorNew;
import rs.ac.bg.etf.pp1.ast.FactorVar;
import rs.ac.bg.etf.pp1.ast.ForCond;
import rs.ac.bg.etf.pp1.ast.ForEach;
import rs.ac.bg.etf.pp1.ast.ForStm;
import rs.ac.bg.etf.pp1.ast.ForStmtDesign;
import rs.ac.bg.etf.pp1.ast.ForStmtDesign1;
import rs.ac.bg.etf.pp1.ast.FormalParamDecl;
import rs.ac.bg.etf.pp1.ast.Greater;
import rs.ac.bg.etf.pp1.ast.GreaterEq;
import rs.ac.bg.etf.pp1.ast.Ident;
import rs.ac.bg.etf.pp1.ast.IfStatementMatch;
import rs.ac.bg.etf.pp1.ast.IfStatementNotMatch;
import rs.ac.bg.etf.pp1.ast.Less;
import rs.ac.bg.etf.pp1.ast.LessEq;
import rs.ac.bg.etf.pp1.ast.MethodDecl;
import rs.ac.bg.etf.pp1.ast.MethodTypeName;
import rs.ac.bg.etf.pp1.ast.MinusExpr;
import rs.ac.bg.etf.pp1.ast.Mod;
import rs.ac.bg.etf.pp1.ast.ModEq;
import rs.ac.bg.etf.pp1.ast.Mul;
import rs.ac.bg.etf.pp1.ast.MulAss;
import rs.ac.bg.etf.pp1.ast.MulEq;
import rs.ac.bg.etf.pp1.ast.MulLeft;
import rs.ac.bg.etf.pp1.ast.MulRight;
import rs.ac.bg.etf.pp1.ast.MulTerm;
import rs.ac.bg.etf.pp1.ast.NoForCond;
import rs.ac.bg.etf.pp1.ast.NoForDesign;
import rs.ac.bg.etf.pp1.ast.NoForDesign1;
import rs.ac.bg.etf.pp1.ast.NoFuncParens;
import rs.ac.bg.etf.pp1.ast.NotEqual;
import rs.ac.bg.etf.pp1.ast.PrintNum;
import rs.ac.bg.etf.pp1.ast.PrintStmt;
import rs.ac.bg.etf.pp1.ast.ReadDesign;
import rs.ac.bg.etf.pp1.ast.RelExpression;
import rs.ac.bg.etf.pp1.ast.ReturnExpr;
import rs.ac.bg.etf.pp1.ast.ReturnNoExpr;
import rs.ac.bg.etf.pp1.ast.Rparen;
import rs.ac.bg.etf.pp1.ast.SingleCondition;
import rs.ac.bg.etf.pp1.ast.Sub;
import rs.ac.bg.etf.pp1.ast.SubEq;
import rs.ac.bg.etf.pp1.ast.SyntaxNode;
import rs.ac.bg.etf.pp1.ast.Term;
import rs.ac.bg.etf.pp1.ast.TermExpr;
import rs.ac.bg.etf.pp1.ast.TermFactor;
import rs.ac.bg.etf.pp1.ast.VarIdent;
import rs.ac.bg.etf.pp1.ast.VisitorAdaptor;
import rs.ac.bg.etf.pp1.ast.VoidType;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class CodeGenerator extends VisitorAdaptor {

	private int varCount;

	private int paramCnt;

	private int mainPc = -2;

	public int getMainPc() {
		return mainPc;
	}

	private ArrayList<Integer> condFactsToBakpatch = new ArrayList<>();
	private ArrayList<Integer> condCondsElseToBakpatch = new ArrayList<>();
	private ArrayList<Integer> condCondsToBakpatch = new ArrayList<>();

	private ArrayList<Integer> forCondBeginAddr = new ArrayList<>();
	private ArrayList<Integer> forDesignBeginAddr = new ArrayList<>();

	private ArrayList<Integer> forCondStmtToBakpatch = new ArrayList<>();
	private ArrayList<ArrayList<Integer>> forCondFactsToBakpatch = new ArrayList<>();
	private ArrayList<Boolean> doesForHaveACond = new ArrayList<>();

	private ArrayList<ArrayList<Integer>> breakAdrToBakpatch = new ArrayList<>();

	private ArrayList<Integer> foreachBegin = new ArrayList<>();
	private ArrayList<Integer> foreachCondToBakpatch = new ArrayList<>();
	private HashMap<String, Obj> feIterArray = new HashMap<>();

	private boolean start = false;
	private ArrayList<String> designs = null;
	private ArrayList<Integer> operations = null;
	private ArrayList<Obj> objs = null;

	private ArrayList<Boolean> startList = new ArrayList<>();
	private ArrayList<ArrayList<String>> designsList = new ArrayList<>();
	private ArrayList<ArrayList<Integer>> operationsList = new ArrayList<>();
	private ArrayList<ArrayList<Obj>> objsList = new ArrayList<>();

	@Override
	public void visit(MethodTypeName MethodTypeName) {
		if ("main".equalsIgnoreCase(MethodTypeName.getMethName())) {
			if (MethodTypeName.getReturnType() instanceof VoidType)
				mainPc = Code.pc;
			else
				mainPc = -1;

		}
		MethodTypeName.obj.setAdr(Code.pc);

		// Collect arguments and local variables.
		SyntaxNode methodNode = MethodTypeName.getParent();
		VarCounter varCnt = new VarCounter();
		methodNode.traverseTopDown(varCnt);
		FormParamCounter fpCnt = new FormParamCounter();
		methodNode.traverseTopDown(fpCnt);

		// Generate the entry.
		MethodTypeName.obj.setLevel(fpCnt.getCount());
		Code.put(Code.enter);
		Code.put(fpCnt.getCount());
		Code.put(varCnt.getCount() + fpCnt.getCount());

		// init for the expressions
		startList.add(new Boolean(false));
		start = false;

		designsList.add(new ArrayList<>(30));
		designs = designsList.get(designsList.size() - 1);

		operationsList.add(new ArrayList<>());
		operations = operationsList.get(operationsList.size() - 1);

		objsList.add(new ArrayList<>());
		objs = objsList.get(objsList.size() - 1);

	}

	@Override
	public void visit(VarIdent varIdent) {
		varCount++;
	}

	@Override
	public void visit(FormalParamDecl FormalParam) {
		paramCnt++;
	}

	@Override
	public void visit(MethodDecl MethodDecl) {
		Code.put(Code.exit);
		Code.put(Code.return_);
		int size = startList.size();
		if (size > 0) {
			startList.remove(size - 1);
			designsList.remove(size - 1);
			operationsList.remove(size - 1);
			objsList.remove(size - 1);
		}

		if (size - 1 > 0) {
			size -= 2;
			start = startList.get(size);
			designs = designsList.get(size);
			operations = operationsList.get(size);
			objs = objsList.get(size);
		}
	}

	@Override
	public void visit(ReturnExpr ReturnExpr) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}

	@Override
	public void visit(ReturnNoExpr ReturnNoExpr) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}

	@Override
	public void visit(FactNumConst fact) {
		Code.load(new Obj(Obj.Con, "$num", fact.struct, fact.getValue(), 0));

	}

	@Override
	public void visit(FactCharConst fact) {
		Code.load(new Obj(Obj.Con, "$num", fact.struct, fact.getValue(), 0));

	}

	@Override
	public void visit(FactBoolConst fact) {
		Code.load(new Obj(Obj.Con, "$bool", fact.struct, fact.getValue().equals("true") ? 1 : 0, 0));

	}

	private String getDesignName(Designator designator) {
		if (designator instanceof DesignatorName)
			return ((DesignatorName) designator).getName();
		Designator design = designator;
		while (!(design instanceof DesignatorName)) {
			design = ((DesignatorClass) design).getDesignator();
		}
		return ((DesignatorName) design).getName();
	}

	@Override
	public void visit(DesignEqExpr designExpr) {
		if (operations.size() > 0) {
			for (int i = objs.size() - 1; i >= 0; i--) {
				Code.put(operations.remove(i * 2 + 1));
				Code.put(operations.remove(i * 2));
				Code.store(objs.remove(i));

			}
			start = false;

		}
		operations.clear();
		objs.clear();
		designs.clear();
		start = false;
		if (designExpr.getDesignator() instanceof DesignatorClass) {
			setArrDesignObj(designExpr.getDesignator());
		} else if (feIterArray.containsKey(((DesignatorName) designExpr.getDesignator()).getName())) {
			// is an iterator
			String name = ((DesignatorName) designExpr.getDesignator()).getName();
			Obj design = feIterArray.get(name);
			Obj arrayElem = new Obj(Obj.Elem, name, designExpr.getDesignator().obj.getType(), design.getAdr(),
					design.getLevel());
			designExpr.getDesignator().obj = arrayElem;
		}
		if (designExpr.getAssignOp() instanceof AddAss) {
			if (((AddAss) designExpr.getAssignOp()).getAddOpRight() instanceof AddEq)
				Code.put(Code.add);
			else
				Code.put(Code.sub);
		}
		if (designExpr.getAssignOp() instanceof MulAss) {
			MulAss mulAss = (MulAss) designExpr.getAssignOp();
			if (mulAss.getMulOpRight() instanceof MulEq)
				Code.put(Code.mul);
			else if (mulAss.getMulOpRight() instanceof DivEq)
				Code.put(Code.div);
			else
				Code.put(Code.rem);
		}
		Code.store(designExpr.getDesignator().obj);

	}

	private void setArrDesignObj(Designator designator) {
		Struct struct = designator.obj.getType().getElemType();
		if (struct == null)
			struct = designator.obj.getType();
		while (struct != null && struct.getElemType() != null) {
			struct = struct.getElemType();
		}
		Obj arrayElem = new Obj(Obj.Elem, getDesignName(designator), struct, designator.obj.getAdr(),
				designator.obj.getLevel());
		designator.obj = arrayElem;
	}

	@Override
	public void visit(FactorVar factorVar) {
		if (factorVar.getFactorFuncParens() instanceof NoFuncParens) {
			if (factorVar.getDesignator() instanceof DesignatorClass) {
				setArrDesignObj(factorVar.getDesignator());
			} else if (feIterArray.containsKey(((DesignatorName) factorVar.getDesignator()).getName())) {
				// is an iterator
				String name = ((DesignatorName) factorVar.getDesignator()).getName();
				Obj design = feIterArray.get(name);
				Code.load(design);
				Code.load(factorVar.getDesignator().obj);
				Obj arrayElem = new Obj(Obj.Elem, name, factorVar.getDesignator().obj.getType(), design.getAdr(),
						design.getLevel());
				factorVar.getDesignator().obj = arrayElem;
			}

			Designator designator = factorVar.getDesignator();
			if ((factorVar.getDesignator() instanceof DesignatorClass
					|| feIterArray.containsKey(((DesignatorName) factorVar.getDesignator()).getName()))//
					&& (factorVar.getParent() instanceof TermFactor//
							&& (factorVar.getParent().getParent() instanceof MulTerm//
									&& ((MulTerm) factorVar.getParent().getParent()).getMulOp() instanceof MulRight//
									&& ((MulTerm) factorVar.getParent().getParent()).getTerm() == factorVar.getParent()//
									|| factorVar.getParent().getParent() instanceof TermExpr//
											&& factorVar.getParent().getParent().getParent() instanceof AddExpr//
											&& ((AddExpr) factorVar.getParent().getParent().getParent())//
													.getAddOp() instanceof AddRight//
											&& ((AddExpr) factorVar.getParent().getParent().getParent())
													.getExpr() == factorVar.getParent().getParent()
									|| factorVar.getParent().getParent() instanceof AddExpr//
											&& factorVar.getParent().getParent().getParent() instanceof AddExpr//
											&& ((AddExpr) factorVar.getParent().getParent().getParent())//
													.getAddOp() instanceof AddRight)//
							// && ((AddExpr) factorVar.getParent().getParent().getParent())
							// .getExpr()==factorVar.getParent().getParent()
							|| factorVar.getParent() instanceof MulTerm//
									&& factorVar.getParent().getParent() instanceof MulTerm//
									&& ((MulTerm) factorVar.getParent()).getMulOp() instanceof MulRight
									&& (((MulTerm) factorVar.getParent()).getTerm() instanceof TermFactor
											&& ((TermFactor) ((MulTerm) factorVar.getParent()).getTerm())
													.getFactor() == factorVar) // moze da se desi da treba da se doda
																				// jos ||
							|| factorVar.getParent() instanceof MulTerm//
									&& factorVar.getParent().getParent() instanceof TermExpr//
									&& factorVar.getParent().getParent().getParent() instanceof AddExpr//
									&& ((AddExpr) factorVar.getParent().getParent().getParent())
											.getAddOp() instanceof AddRight
									&& ((AddExpr) factorVar.getParent().getParent().getParent()).getExpr() == factorVar
											.getParent().getParent())) {//
				Code.put(Code.dup2);
				Code.put(Code.aload);
			} else
				Code.load(designator.obj);
		} else {// postoje zagrade -> poziv metode
			String name = ((DesignatorName) factorVar.getDesignator()).getName();
			if (!name.equals("ord") && !name.equals("len") && !name.equals("chr")) {
				Obj funcObj = factorVar.getDesignator().obj;
				int offset = funcObj.getAdr() - Code.pc;
				Code.put(Code.call);

				Code.put2(offset);
			} else if (name.equals("len")) {
				Code.put(Code.arraylength);
			}

		}
	}

	@Override
	public void visit(DesignFunc designFunc) {

		String name = ((DesignatorName) designFunc.getDesignator()).getName();
		if (!name.equals("ord") && !name.equals("len") && !name.equals("chr")) {
			Obj funcObj = designFunc.getDesignator().obj;
			int offset = funcObj.getAdr() - Code.pc;
			Code.put(Code.call);

			Code.put2(offset);

			if (funcObj.getType() != Tab.noType) {
				Code.put(Code.pop);
			}
		} else if (name.equals("len")) {
			Code.put(Code.arraylength);
			Code.put(Code.pop);
		} else {
			Code.put(Code.pop);
		}
	}

	private void printWord(Struct struct, String str, int value) {
		Code.load(new Obj(Obj.Con, "$num", struct, str.charAt(0), 0));
		Code.loadConst(value);
		Code.put(Code.bprint);
		for (int i = 1; i < str.length(); i++) {
			Code.load(new Obj(Obj.Con, "$num", struct, str.charAt(i), 0));
			Code.loadConst(1);
			Code.put(Code.bprint);
		}

	}

	@Override
	public void visit(PrintStmt PrintStmt) {
		Struct struct = PrintStmt.getExpr().struct;
		while (struct != null && struct.getElemType() != null) {
			struct = struct.getElemType();
		}
		if (struct == Tab.intType) {
			if (PrintStmt.getPrintNumConst() instanceof PrintNum)
				Code.loadConst(((PrintNum) PrintStmt.getPrintNumConst()).getN1());
			else
				Code.loadConst(5);
			Code.put(Code.print);
		} else if (struct == Tab.charType) {
			if (PrintStmt.getPrintNumConst() instanceof PrintNum)
				Code.loadConst(((PrintNum) PrintStmt.getPrintNumConst()).getN1());
			else
				Code.loadConst(1);
			Code.put(Code.bprint);
		} else {
			int value = 1;
			if (PrintStmt.getPrintNumConst() instanceof PrintNum)
				value = ((PrintNum) PrintStmt.getPrintNumConst()).getN1();

			Code.loadConst(1);
			Code.putFalseJump(Code.eq, 0);
			int backpatch = Code.pc - 2;
			printWord(struct, "true", value);
			Code.loadConst(1);
			Code.loadConst(1);
			Code.putFalseJump(Code.ne, 0);
			int backpatchTrue = Code.pc - 2;
			Code.fixup(backpatch);
			printWord(struct, "false", value);
			Code.fixup(backpatchTrue);

		}
	}

	@Override
	public void visit(ReadDesign readDesign) {
		Struct struct = readDesign.getDesignator().obj.getType();
		while (struct != null && struct.getElemType() != null) {
			struct = struct.getElemType();
		}

		if (struct == Tab.intType) {
			Code.put(Code.read);
		} else {
			Code.put(Code.bread);
		}
		if (readDesign.getDesignator() instanceof DesignatorClass) {
			setArrDesignObj(readDesign.getDesignator());
		} else if (feIterArray.containsKey(((DesignatorName) readDesign.getDesignator()).getName())) {
			// is an iterator
			String name = ((DesignatorName) readDesign.getDesignator()).getName();
			Obj design = feIterArray.get(name);
			Obj arrayElem = new Obj(Obj.Elem, name, readDesign.getDesignator().obj.getType(), design.getAdr(),
					design.getLevel());
			readDesign.getDesignator().obj = arrayElem;
		}

		Code.store(readDesign.getDesignator().obj);

	}

	@Override
	public void visit(MinusExpr optMinusEx) {
		Code.put(Code.neg);
		// Code.put(optM)
	}

	// postavi 0 u ident
	@Override
	public void visit(Ident ident) {
		Code.loadConst(0);
		Code.store(ident.obj);
	}

	@Override
	public void visit(Rparen rparen) {
		foreachBegin.add(Code.pc);
		ForEach parrent = (ForEach) rparen.getParent();
		Code.load(parrent.getIdent().obj);
		Code.load(parrent.getDesignator().obj);
		Code.put(Code.arraylength);
		// na steku pocetni ovaj i duzina niza
		Code.putFalseJump(Code.lt, 0);
		foreachCondToBakpatch.add(Code.pc - 2);

		// add the iter array connection
		feIterArray.put(parrent.getIdent().getName(), parrent.getDesignator().obj);

	}

	@Override
	public void visit(ForEach forEach) {
		Code.load(forEach.getIdent().obj);
		Code.loadConst(1);
		Code.put(Code.add);
		Code.store(forEach.getIdent().obj);
		Code.putJump(foreachBegin.remove(foreachBegin.size() - 1));
		Code.fixup(foreachCondToBakpatch.remove(foreachCondToBakpatch.size() - 1));

		// remove the iter arr of this foreach
		feIterArray.remove(forEach.getIdent().getName());
	}

	@Override
	public void visit(DesignatorName designator) {
		SyntaxNode parrent = designator.getParent();
		if (DesignFunc.class != parrent.getClass() && FactorVar.class != parrent.getClass()
				&& ReadDesign.class != parrent.getClass()) {
			if (DesignEqExpr.class == parrent.getClass()) {
				start = true;// ovde problem vidi sto load nema
				DesignEqExpr expr = (DesignEqExpr) parrent;
				if (expr.getAssignOp() instanceof AddAss || expr.getAssignOp() instanceof MulAss) {
					if (feIterArray.containsKey(designator.getName())) {
						Code.load(feIterArray.get(designator.getName()));
						Code.load(designator.obj);
						Code.put(Code.dup2);
						Code.put(Code.aload);
					} else
						Code.load(designator.obj);
				} else if (feIterArray.containsKey(designator.getName())) {
					Code.load(feIterArray.get(designator.getName()));
					Code.load(designator.obj);
				}
			} else {
				if (feIterArray.containsKey(designator.getName())) {
					Code.load(feIterArray.get(designator.getName()));
					// load ispod ce da stavi odgovarajuci indeks
				}
				if (designator.getParent().getClass() != ForEach.class)
					Code.load(designator.obj);
			}
		} else if (parrent.getClass() == ReadDesign.class && feIterArray.containsKey(designator.getName())) {
			Code.load(feIterArray.get(designator.getName()));
			Code.load(designator.obj);
		}
		if (start && DesignEqExpr.class != parrent.getClass()) {
			designs.add(designator.getName());
		}
		if (DesignatorClass.class == parrent.getClass() && DesignEqExpr.class == parrent.getParent().getClass()) {
			start = true;
		}
		//ako slucajno budu stavili return a+=b proveri taj slucaj i dodaj i top
	}

	@Override
	public void visit(DesignatorClass designClass) {
		if (designClass.getParent().getClass() == DesignEqExpr.class) {
			DesignEqExpr expr = (DesignEqExpr) designClass.getParent();
			if (expr.getAssignOp() instanceof AddAss || expr.getAssignOp() instanceof MulAss) {
				Code.put(Code.dup2);
				Code.put(Code.aload);
			}

		}
	}

	@Override
	public void visit(FactorNew newArray) {
		Code.put(Code.newarray);
		if (newArray.getType().struct == Tab.charType) {
			Code.put(0);
		} else {
			Code.put(1);
		}
	}

	@Override
	public void visit(DesignInc designInc) {
		if (designInc.getDesignator() instanceof DesignatorClass) {
			setArrDesignObj(designInc.getDesignator());
			Code.put(Code.dup2);
			Code.put(Code.aload);
		} else if (feIterArray.containsKey(((DesignatorName) designInc.getDesignator()).getName())) {
			// is an iterator
			String name = ((DesignatorName) designInc.getDesignator()).getName();
			Obj design = feIterArray.get(name);
			Obj arrayElem = new Obj(Obj.Elem, name, designInc.getDesignator().obj.getType(), design.getAdr(),
					design.getLevel());
			designInc.getDesignator().obj = arrayElem;
			Code.put(Code.dup2);
			Code.put(Code.aload);
		}

		Code.loadConst(1);
		Code.put(Code.add);
		Code.store(designInc.getDesignator().obj);
	}

	@Override
	public void visit(DesignDec designDec) {
		if (designDec.getDesignator() instanceof DesignatorClass) {
			setArrDesignObj(designDec.getDesignator());
			Code.put(Code.dup2);
			Code.put(Code.aload);
		} else if (feIterArray.containsKey(((DesignatorName) designDec.getDesignator()).getName())) {
			// is an iterator
			String name = ((DesignatorName) designDec.getDesignator()).getName();
			Obj design = feIterArray.get(name);
			Obj arrayElem = new Obj(Obj.Elem, name, designDec.getDesignator().obj.getType(), design.getAdr(),
					design.getLevel());
			designDec.getDesignator().obj = arrayElem;
			Code.put(Code.dup2);
			Code.put(Code.aload);
		}
		Code.loadConst(1);
		Code.put(Code.sub);
		Code.store(designDec.getDesignator().obj);
	}

	// dodaj proveru da li treba pop
	// vidi sto baguje
	@Override
	public void visit(AddExpr addop) {

		if (addop.getAddOp() instanceof AddLeft) {
			AddLeft addLeft = (AddLeft) addop.getAddOp();
			if (addLeft.getAddOpLeft() instanceof Add)
				Code.put(Code.add);
			if (addLeft.getAddOpLeft() instanceof Sub)
				Code.put(Code.sub);
		} else {
			int i = -1;
			AddRight addRight = (AddRight) addop.getAddOp();

			FactorVar factVar = null;

			if (addop.getExpr() instanceof TermExpr) {
				TermExpr termExpr = (TermExpr) addop.getExpr();
				if (termExpr.getTerm() instanceof TermFactor) {
					TermFactor termFactor = (TermFactor) termExpr.getTerm();
					if (termFactor.getFactor() instanceof FactorVar) {
						factVar = (FactorVar) termFactor.getFactor();
					}
				} else if (termExpr.getTerm() instanceof MulTerm) {
					MulTerm mulTerm = (MulTerm) termExpr.getTerm();
					if (mulTerm.getFactor() instanceof FactorVar) {
						factVar = (FactorVar) mulTerm.getFactor();
					}

				}
			} else {
				AddExpr addEx = (AddExpr) addop.getExpr();
				if (addEx.getTerm() instanceof TermFactor) {
					TermFactor termFactor = (TermFactor) addEx.getTerm();
					if (termFactor.getFactor() instanceof FactorVar) {
						factVar = (FactorVar) termFactor.getFactor();
					}
				}
			}

			if (factVar.getDesignator() instanceof DesignatorClass) {
				setArrDesignObj(factVar.getDesignator());

				i = designs.indexOf(factVar.getDesignator().obj.getName());
				designs.set(i, null);
				while (operations.size() < i * 2 + 2) {
					operations.add(0);
					operations.add(0);
					objs.add(null);
				}
				operations.set(i * 2, Code.dup_x2);

			} else if (feIterArray.containsKey(((DesignatorName) factVar.getDesignator()).getName())) {
				// is an iterator
				String name = ((DesignatorName) factVar.getDesignator()).getName();
				Obj design = feIterArray.get(name);
				Obj arrayElem = new Obj(Obj.Elem, name, factVar.getDesignator().obj.getType(), design.getAdr(),
						design.getLevel());
				factVar.getDesignator().obj = arrayElem;

				i = designs.indexOf(factVar.getDesignator().obj.getName());
				designs.set(i, null);
				while (operations.size() < i * 2 + 2) {
					operations.add(0);
					operations.add(0);
					objs.add(null);
				}
				operations.set(i * 2, Code.dup_x2);
			} else {
				i = designs.indexOf(factVar.getDesignator().obj.getName());
				designs.set(i, null);
				while (operations.size() < i * 2 + 2) {
					operations.add(0);
					operations.add(0);
					objs.add(null);
				}
				operations.set(i * 2, Code.dup);
			}
			if (addRight.getAddOpRight() instanceof AddEq)
				operations.set(i * 2 + 1, Code.add);
			if (addRight.getAddOpRight() instanceof SubEq)
				operations.set(i * 2 + 1, Code.sub);
			objs.set(i, factVar.getDesignator().obj);

		}

	}

	@Override
	public void visit(MulTerm mulop) {
		if (mulop.getMulOp() instanceof MulLeft) {
			MulLeft mulLeft = (MulLeft) mulop.getMulOp();
			if (mulLeft.getMulOpLeft() instanceof Mul) {
				Code.put(Code.mul);
			}
			if (mulLeft.getMulOpLeft() instanceof Div) {
				Code.put(Code.div);
			}
			if (mulLeft.getMulOpLeft() instanceof Mod) {
				Code.put(Code.rem);
			}
		} else {
			int i = -1;
			MulRight mulRight = (MulRight) mulop.getMulOp();

			FactorVar factVar = null;

			if (mulop.getTerm() instanceof TermFactor) {
				TermFactor fact = (TermFactor) mulop.getTerm();
				if (fact.getFactor() instanceof FactorVar) {
					factVar = (FactorVar) fact.getFactor();
				}
			} else {
				MulTerm term = (MulTerm) mulop.getTerm();
				if (term.getFactor() instanceof FactorVar)
					factVar = (FactorVar) term.getFactor();
			}
			if (factVar.getDesignator() instanceof DesignatorClass) {
				setArrDesignObj(factVar.getDesignator());

				i = designs.indexOf(factVar.getDesignator().obj.getName());
				designs.set(i, null);
				while (operations.size() < i * 2 + 2) {
					operations.add(0);
					operations.add(0);
					objs.add(null);
				}
				operations.set(i * 2, Code.dup_x2);

			} else if (feIterArray.containsKey(((DesignatorName) factVar.getDesignator()).getName())) {
				// is an iterator
				String name = ((DesignatorName) factVar.getDesignator()).getName();
				Obj design = feIterArray.get(name);
				Obj arrayElem = new Obj(Obj.Elem, name, factVar.getDesignator().obj.getType(), design.getAdr(),
						design.getLevel());
				factVar.getDesignator().obj = arrayElem;

				i = designs.indexOf(factVar.getDesignator().obj.getName());
				designs.set(i, null);
				while (operations.size() < i * 2 + 2) {
					operations.add(0);
					operations.add(0);
					objs.add(null);
				}
				operations.set(i * 2, Code.dup_x2);
			} else {
				i = designs.indexOf(factVar.getDesignator().obj.getName());
				designs.set(i, null);
				while (operations.size() < i * 2 + 2) {
					operations.add(0);
					operations.add(0);
					objs.add(null);
				}
				operations.set(i * 2, Code.dup);
			}

			if (mulRight.getMulOpRight() instanceof MulEq) {
				operations.set(i * 2 + 1, Code.mul);
			}
			if (mulRight.getMulOpRight() instanceof DivEq) {
				operations.set(i * 2 + 1, Code.div);
			}
			if (mulRight.getMulOpRight() instanceof ModEq) {
				operations.set(i * 2 + 1, Code.div);
			}

			objs.set(i, factVar.getDesignator().obj);
		}

	}

	@Override
	public void visit(CondFact condFact) {

		if (condFact.getRelExpr() instanceof RelExpression) {
			RelExpression relExpr = (RelExpression) condFact.getRelExpr();
			if (relExpr.getRelOp() instanceof Equal) {
				Code.putFalseJump(Code.eq, 0);
			}
			if (relExpr.getRelOp() instanceof NotEqual) {
				Code.putFalseJump(Code.ne, 0);
			}
			if (relExpr.getRelOp() instanceof Greater) {
				Code.putFalseJump(Code.gt, 0);
			}
			if (relExpr.getRelOp() instanceof GreaterEq) {
				Code.putFalseJump(Code.ge, 0);
			}
			if (relExpr.getRelOp() instanceof Less) {
				Code.putFalseJump(Code.lt, 0);
			}
			if (relExpr.getRelOp() instanceof LessEq) {
				Code.putFalseJump(Code.le, 0);
			}
			condFactsToBakpatch.add(Code.pc - 2);
		} else {// nije izraz, samo bool konstanta koja je vec na steku
			Code.loadConst(1);// ako je to sto je na steku 1 bice true
			Code.putFalseJump(Code.eq, 0);
			condFactsToBakpatch.add(Code.pc - 2);

		}

	}

	@Override
	public void visit(SingleCondition condition) {
		if (condition.getParent() instanceof Conditions) {
			Code.loadConst(1);
			Code.loadConst(1);
			Code.putFalseJump(Code.ne, 0);
			condCondsToBakpatch.add(Code.pc - 2);

			while (condFactsToBakpatch.size() > 0)
				Code.fixup(condFactsToBakpatch.remove(0));
		}
	}

	@Override
	public void visit(Conditions condition) {
		if (condition.getParent() instanceof IfStatementNotMatch || condition.getParent() instanceof IfStatementMatch) {
			// kraj Conditions je pocetak if
			while (condCondsToBakpatch.size() > 0)
				Code.fixup(condCondsToBakpatch.remove(0));
		} else {
			Code.loadConst(1);
			Code.loadConst(1);
			Code.putFalseJump(Code.ne, 0);
			condCondsToBakpatch.add(Code.pc - 2);

			while (condFactsToBakpatch.size() > 0)
				Code.fixup(condFactsToBakpatch.remove(0));

		}
	}

	@Override
	public void visit(IfStatementNotMatch ifStmt) {
		// dosli na kraj ifa tj nije ispunjen uslov
		while (condFactsToBakpatch.size() > 0)
			Code.fixup(condFactsToBakpatch.remove(0));

	}

	@Override
	public void visit(Else elseStmt) {
		Code.loadConst(1);
		Code.loadConst(1);
		Code.putFalseJump(Code.ne, 0);
		condCondsElseToBakpatch.add(Code.pc - 2);
		while (condFactsToBakpatch.size() > 0)
			Code.fixup(condFactsToBakpatch.remove(0));
	}

	@Override
	public void visit(IfStatementMatch ifStm) {
		while (condCondsElseToBakpatch.size() > 0)
			Code.fixup(condCondsElseToBakpatch.remove(0));
	}

	@Override
	public void visit(ForStmtDesign forStmt) {
		forCondBeginAddr.add(Code.pc);
		breakAdrToBakpatch.add(new ArrayList<>());
	}

	@Override
	public void visit(NoForDesign forStmt) {
		forCondBeginAddr.add(Code.pc);
		breakAdrToBakpatch.add(new ArrayList<>());
	}

	@Override
	public void visit(ForCond forCond) {
		doesForHaveACond.add(true);
		forCondFactsToBakpatch.add(condFactsToBakpatch);
		condFactsToBakpatch = new ArrayList<>();
		Code.loadConst(1);
		Code.loadConst(1);
		Code.putFalseJump(Code.ne, 0);
		forCondStmtToBakpatch.add(Code.pc - 2);
		forDesignBeginAddr.add(Code.pc);
	}

	@Override
	public void visit(NoForCond forCond) {
		doesForHaveACond.add(false);
		Code.loadConst(1);
		Code.loadConst(1);
		Code.putFalseJump(Code.ne, 0);
		forCondStmtToBakpatch.add(Code.pc - 2);
		forDesignBeginAddr.add(Code.pc);
	}

	@Override
	public void visit(ForStmtDesign1 forStmt) {
		Code.putJump(forCondBeginAddr.remove(forCondBeginAddr.size() - 1));
		Code.fixup(forCondStmtToBakpatch.remove(forCondStmtToBakpatch.size() - 1));
	}

	@Override
	public void visit(NoForDesign1 forStmt) {
		Code.putJump(forCondBeginAddr.remove(forCondBeginAddr.size() - 1));
		Code.fixup(forCondStmtToBakpatch.remove(forCondStmtToBakpatch.size() - 1));

	}

	@Override
	public void visit(ForStm forStmt) {
		// statement end
		Code.putJump(forDesignBeginAddr.remove(forDesignBeginAddr.size() - 1));

		// if cond not correct jump to the end of the most nested for
		if (doesForHaveACond.remove(doesForHaveACond.size() - 1) && forCondFactsToBakpatch.size() > 0) {
			ArrayList<Integer> condFactsToBakpatch = forCondFactsToBakpatch.remove(forCondFactsToBakpatch.size() - 1);
			while (condFactsToBakpatch.size() > 0)
				Code.fixup(condFactsToBakpatch.remove(0));
		}
		ArrayList<Integer> breakToBakpatch = breakAdrToBakpatch.remove(breakAdrToBakpatch.size() - 1);
		while (breakToBakpatch.size() > 0) {
			Code.fixup(breakToBakpatch.remove(0));
		}

	}

	@Override
	public void visit(ContinueSt continueSt) {
		Code.putJump(forDesignBeginAddr.get(forDesignBeginAddr.size() - 1));
	}

	@Override
	public void visit(BreakSt breakSt) {
		Code.loadConst(1);
		Code.loadConst(1);
		Code.putFalseJump(Code.ne, 0);
		breakAdrToBakpatch.get(breakAdrToBakpatch.size() - 1).add(Code.pc - 2);
	}

}