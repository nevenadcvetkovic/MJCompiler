// generated with ast extension for cup
// version 0.8
// 18/2/2020 17:9:3


package rs.ac.bg.etf.pp1.ast;

public class SingleConst extends ConstIdentList {

    private ConstIdent ConstIdent;

    public SingleConst (ConstIdent ConstIdent) {
        this.ConstIdent=ConstIdent;
        if(ConstIdent!=null) ConstIdent.setParent(this);
    }

    public ConstIdent getConstIdent() {
        return ConstIdent;
    }

    public void setConstIdent(ConstIdent ConstIdent) {
        this.ConstIdent=ConstIdent;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ConstIdent!=null) ConstIdent.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ConstIdent!=null) ConstIdent.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ConstIdent!=null) ConstIdent.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("SingleConst(\n");

        if(ConstIdent!=null)
            buffer.append(ConstIdent.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [SingleConst]");
        return buffer.toString();
    }
}
