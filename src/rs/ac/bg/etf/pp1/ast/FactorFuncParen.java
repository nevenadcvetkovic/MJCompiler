// generated with ast extension for cup
// version 0.8
// 18/2/2020 17:9:3


package rs.ac.bg.etf.pp1.ast;

public class FactorFuncParen extends FactorFuncParens {

    private LFuncParen LFuncParen;
    private ActualPars ActualPars;

    public FactorFuncParen (LFuncParen LFuncParen, ActualPars ActualPars) {
        this.LFuncParen=LFuncParen;
        if(LFuncParen!=null) LFuncParen.setParent(this);
        this.ActualPars=ActualPars;
        if(ActualPars!=null) ActualPars.setParent(this);
    }

    public LFuncParen getLFuncParen() {
        return LFuncParen;
    }

    public void setLFuncParen(LFuncParen LFuncParen) {
        this.LFuncParen=LFuncParen;
    }

    public ActualPars getActualPars() {
        return ActualPars;
    }

    public void setActualPars(ActualPars ActualPars) {
        this.ActualPars=ActualPars;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(LFuncParen!=null) LFuncParen.accept(visitor);
        if(ActualPars!=null) ActualPars.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(LFuncParen!=null) LFuncParen.traverseTopDown(visitor);
        if(ActualPars!=null) ActualPars.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(LFuncParen!=null) LFuncParen.traverseBottomUp(visitor);
        if(ActualPars!=null) ActualPars.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("FactorFuncParen(\n");

        if(LFuncParen!=null)
            buffer.append(LFuncParen.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ActualPars!=null)
            buffer.append(ActualPars.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [FactorFuncParen]");
        return buffer.toString();
    }
}
