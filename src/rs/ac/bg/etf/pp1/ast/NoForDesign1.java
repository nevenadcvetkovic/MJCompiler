// generated with ast extension for cup
// version 0.8
// 18/2/2020 17:9:3


package rs.ac.bg.etf.pp1.ast;

public class NoForDesign1 extends ForDesignSt1 {

    public NoForDesign1 () {
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("NoForDesign1(\n");

        buffer.append(tab);
        buffer.append(") [NoForDesign1]");
        return buffer.toString();
    }
}
