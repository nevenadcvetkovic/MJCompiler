// generated with ast extension for cup
// version 0.8
// 18/2/2020 17:9:3


package rs.ac.bg.etf.pp1.ast;

public class DesignatorClass extends Designator {

    private Designator Designator;
    private DesExpIdent DesExpIdent;

    public DesignatorClass (Designator Designator, DesExpIdent DesExpIdent) {
        this.Designator=Designator;
        if(Designator!=null) Designator.setParent(this);
        this.DesExpIdent=DesExpIdent;
        if(DesExpIdent!=null) DesExpIdent.setParent(this);
    }

    public Designator getDesignator() {
        return Designator;
    }

    public void setDesignator(Designator Designator) {
        this.Designator=Designator;
    }

    public DesExpIdent getDesExpIdent() {
        return DesExpIdent;
    }

    public void setDesExpIdent(DesExpIdent DesExpIdent) {
        this.DesExpIdent=DesExpIdent;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(Designator!=null) Designator.accept(visitor);
        if(DesExpIdent!=null) DesExpIdent.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Designator!=null) Designator.traverseTopDown(visitor);
        if(DesExpIdent!=null) DesExpIdent.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Designator!=null) Designator.traverseBottomUp(visitor);
        if(DesExpIdent!=null) DesExpIdent.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("DesignatorClass(\n");

        if(Designator!=null)
            buffer.append(Designator.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(DesExpIdent!=null)
            buffer.append(DesExpIdent.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [DesignatorClass]");
        return buffer.toString();
    }
}
