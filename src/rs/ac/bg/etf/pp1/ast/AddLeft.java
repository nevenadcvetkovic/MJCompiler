// generated with ast extension for cup
// version 0.8
// 18/2/2020 17:9:3


package rs.ac.bg.etf.pp1.ast;

public class AddLeft extends AddOp {

    private AddOpLeft AddOpLeft;

    public AddLeft (AddOpLeft AddOpLeft) {
        this.AddOpLeft=AddOpLeft;
        if(AddOpLeft!=null) AddOpLeft.setParent(this);
    }

    public AddOpLeft getAddOpLeft() {
        return AddOpLeft;
    }

    public void setAddOpLeft(AddOpLeft AddOpLeft) {
        this.AddOpLeft=AddOpLeft;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(AddOpLeft!=null) AddOpLeft.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(AddOpLeft!=null) AddOpLeft.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(AddOpLeft!=null) AddOpLeft.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("AddLeft(\n");

        if(AddOpLeft!=null)
            buffer.append(AddOpLeft.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [AddLeft]");
        return buffer.toString();
    }
}
