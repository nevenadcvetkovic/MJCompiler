// generated with ast extension for cup
// version 0.8
// 18/2/2020 17:9:3


package rs.ac.bg.etf.pp1.ast;

public class ConstIdents extends ConstIdentList {

    private ConstIdentList ConstIdentList;
    private ConstIdent ConstIdent;

    public ConstIdents (ConstIdentList ConstIdentList, ConstIdent ConstIdent) {
        this.ConstIdentList=ConstIdentList;
        if(ConstIdentList!=null) ConstIdentList.setParent(this);
        this.ConstIdent=ConstIdent;
        if(ConstIdent!=null) ConstIdent.setParent(this);
    }

    public ConstIdentList getConstIdentList() {
        return ConstIdentList;
    }

    public void setConstIdentList(ConstIdentList ConstIdentList) {
        this.ConstIdentList=ConstIdentList;
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
        if(ConstIdentList!=null) ConstIdentList.accept(visitor);
        if(ConstIdent!=null) ConstIdent.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ConstIdentList!=null) ConstIdentList.traverseTopDown(visitor);
        if(ConstIdent!=null) ConstIdent.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ConstIdentList!=null) ConstIdentList.traverseBottomUp(visitor);
        if(ConstIdent!=null) ConstIdent.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ConstIdents(\n");

        if(ConstIdentList!=null)
            buffer.append(ConstIdentList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ConstIdent!=null)
            buffer.append(ConstIdent.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ConstIdents]");
        return buffer.toString();
    }
}
