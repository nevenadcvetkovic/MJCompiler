// generated with ast extension for cup
// version 0.8
// 18/2/2020 17:9:3


package rs.ac.bg.etf.pp1.ast;

public class FactorNew extends Factor {

    private Type Type;
    private NewFactorExpr NewFactorExpr;

    public FactorNew (Type Type, NewFactorExpr NewFactorExpr) {
        this.Type=Type;
        if(Type!=null) Type.setParent(this);
        this.NewFactorExpr=NewFactorExpr;
        if(NewFactorExpr!=null) NewFactorExpr.setParent(this);
    }

    public Type getType() {
        return Type;
    }

    public void setType(Type Type) {
        this.Type=Type;
    }

    public NewFactorExpr getNewFactorExpr() {
        return NewFactorExpr;
    }

    public void setNewFactorExpr(NewFactorExpr NewFactorExpr) {
        this.NewFactorExpr=NewFactorExpr;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(Type!=null) Type.accept(visitor);
        if(NewFactorExpr!=null) NewFactorExpr.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Type!=null) Type.traverseTopDown(visitor);
        if(NewFactorExpr!=null) NewFactorExpr.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Type!=null) Type.traverseBottomUp(visitor);
        if(NewFactorExpr!=null) NewFactorExpr.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("FactorNew(\n");

        if(Type!=null)
            buffer.append(Type.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(NewFactorExpr!=null)
            buffer.append(NewFactorExpr.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [FactorNew]");
        return buffer.toString();
    }
}
