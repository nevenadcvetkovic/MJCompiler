// generated with ast extension for cup
// version 0.8
// 18/2/2020 17:9:3


package rs.ac.bg.etf.pp1.ast;

public class FactorVar extends Factor {

    private Designator Designator;
    private FactorFuncParens FactorFuncParens;

    public FactorVar (Designator Designator, FactorFuncParens FactorFuncParens) {
        this.Designator=Designator;
        if(Designator!=null) Designator.setParent(this);
        this.FactorFuncParens=FactorFuncParens;
        if(FactorFuncParens!=null) FactorFuncParens.setParent(this);
    }

    public Designator getDesignator() {
        return Designator;
    }

    public void setDesignator(Designator Designator) {
        this.Designator=Designator;
    }

    public FactorFuncParens getFactorFuncParens() {
        return FactorFuncParens;
    }

    public void setFactorFuncParens(FactorFuncParens FactorFuncParens) {
        this.FactorFuncParens=FactorFuncParens;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(Designator!=null) Designator.accept(visitor);
        if(FactorFuncParens!=null) FactorFuncParens.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Designator!=null) Designator.traverseTopDown(visitor);
        if(FactorFuncParens!=null) FactorFuncParens.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Designator!=null) Designator.traverseBottomUp(visitor);
        if(FactorFuncParens!=null) FactorFuncParens.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("FactorVar(\n");

        if(Designator!=null)
            buffer.append(Designator.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(FactorFuncParens!=null)
            buffer.append(FactorFuncParens.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [FactorVar]");
        return buffer.toString();
    }
}
