// Autogenerated AST node
package com.astra.ses.spell.language.model.ast;
import com.astra.ses.spell.language.model.SimpleNode;

public class FunctionDef extends stmtType {
    public NameTokType name;
    public argumentsType args;
    public stmtType[] body;
    public decoratorsType[] decs;
    public exprType returns;

    public FunctionDef(NameTokType name, argumentsType args, stmtType[]
    body, decoratorsType[] decs, exprType returns) {
        this.name = name;
        this.args = args;
        this.body = body;
        this.decs = decs;
        this.returns = returns;
    }

    public FunctionDef(NameTokType name, argumentsType args, stmtType[]
    body, decoratorsType[] decs, exprType returns, SimpleNode parent) {
        this(name, args, body, decs, returns);
        this.beginLine = parent.beginLine;
        this.beginColumn = parent.beginColumn;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("FunctionDef[");
        sb.append("name=");
        sb.append(dumpThis(this.name));
        sb.append(", ");
        sb.append("args=");
        sb.append(dumpThis(this.args));
        sb.append(", ");
        sb.append("body=");
        sb.append(dumpThis(this.body));
        sb.append(", ");
        sb.append("decs=");
        sb.append(dumpThis(this.decs));
        sb.append(", ");
        sb.append("returns=");
        sb.append(dumpThis(this.returns));
        sb.append("]");
        return sb.toString();
    }

    public Object accept(VisitorIF visitor) throws Exception {
        return visitor.visitFunctionDef(this);
    }

    public void traverse(VisitorIF visitor) throws Exception {
        if (name != null)
            name.accept(visitor);
        if (args != null)
            args.accept(visitor);
        if (body != null) {
            for (int i = 0; i < body.length; i++) {
                if (body[i] != null)
                    body[i].accept(visitor);
            }
        }
        if (decs != null) {
            for (int i = 0; i < decs.length; i++) {
                if (decs[i] != null)
                    decs[i].accept(visitor);
            }
        }
        if (returns != null)
            returns.accept(visitor);
    }

}
