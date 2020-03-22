// generated with ast extension for cup
// version 0.8
// 18/2/2020 17:9:3


package rs.ac.bg.etf.pp1.ast;

public class ConstFactor extends Factor {

    private FactConstType FactConstType;

    public ConstFactor (FactConstType FactConstType) {
        this.FactConstType=FactConstType;
        if(FactConstType!=null) FactConstType.setParent(this);
    }

    public FactConstType getFactConstType() {
        return FactConstType;
    }

    public void setFactConstType(FactConstType FactConstType) {
        this.FactConstType=FactConstType;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(FactConstType!=null) FactConstType.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(FactConstType!=null) FactConstType.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(FactConstType!=null) FactConstType.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ConstFactor(\n");

        if(FactConstType!=null)
            buffer.append(FactConstType.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ConstFactor]");
        return buffer.toString();
    }
}
