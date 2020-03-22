// generated with ast extension for cup
// version 0.8
// 18/2/2020 17:9:3


package rs.ac.bg.etf.pp1.ast;

public class ForStm extends Statement {

    private ForDesignSt ForDesignSt;
    private ForCondition ForCondition;
    private ForDesignSt1 ForDesignSt1;
    private Statement Statement;

    public ForStm (ForDesignSt ForDesignSt, ForCondition ForCondition, ForDesignSt1 ForDesignSt1, Statement Statement) {
        this.ForDesignSt=ForDesignSt;
        if(ForDesignSt!=null) ForDesignSt.setParent(this);
        this.ForCondition=ForCondition;
        if(ForCondition!=null) ForCondition.setParent(this);
        this.ForDesignSt1=ForDesignSt1;
        if(ForDesignSt1!=null) ForDesignSt1.setParent(this);
        this.Statement=Statement;
        if(Statement!=null) Statement.setParent(this);
    }

    public ForDesignSt getForDesignSt() {
        return ForDesignSt;
    }

    public void setForDesignSt(ForDesignSt ForDesignSt) {
        this.ForDesignSt=ForDesignSt;
    }

    public ForCondition getForCondition() {
        return ForCondition;
    }

    public void setForCondition(ForCondition ForCondition) {
        this.ForCondition=ForCondition;
    }

    public ForDesignSt1 getForDesignSt1() {
        return ForDesignSt1;
    }

    public void setForDesignSt1(ForDesignSt1 ForDesignSt1) {
        this.ForDesignSt1=ForDesignSt1;
    }

    public Statement getStatement() {
        return Statement;
    }

    public void setStatement(Statement Statement) {
        this.Statement=Statement;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ForDesignSt!=null) ForDesignSt.accept(visitor);
        if(ForCondition!=null) ForCondition.accept(visitor);
        if(ForDesignSt1!=null) ForDesignSt1.accept(visitor);
        if(Statement!=null) Statement.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ForDesignSt!=null) ForDesignSt.traverseTopDown(visitor);
        if(ForCondition!=null) ForCondition.traverseTopDown(visitor);
        if(ForDesignSt1!=null) ForDesignSt1.traverseTopDown(visitor);
        if(Statement!=null) Statement.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ForDesignSt!=null) ForDesignSt.traverseBottomUp(visitor);
        if(ForCondition!=null) ForCondition.traverseBottomUp(visitor);
        if(ForDesignSt1!=null) ForDesignSt1.traverseBottomUp(visitor);
        if(Statement!=null) Statement.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ForStm(\n");

        if(ForDesignSt!=null)
            buffer.append(ForDesignSt.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ForCondition!=null)
            buffer.append(ForCondition.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ForDesignSt1!=null)
            buffer.append(ForDesignSt1.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Statement!=null)
            buffer.append(Statement.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ForStm]");
        return buffer.toString();
    }
}
