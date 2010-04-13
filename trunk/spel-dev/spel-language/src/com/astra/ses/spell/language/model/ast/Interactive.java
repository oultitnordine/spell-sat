// Autogenerated AST node
package com.astra.ses.spell.language.model.ast;
import com.astra.ses.spell.language.model.SimpleNode;

public class Interactive extends modType {
    public stmtType[] body;

    public Interactive(stmtType[] body) {
        this.body = body;
    }

    public Interactive(stmtType[] body, SimpleNode parent) {
        this(body);
        this.beginLine = parent.beginLine;
        this.beginColumn = parent.beginColumn;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("Interactive[");
        sb.append("body=");
        sb.append(dumpThis(this.body));
        sb.append("]");
        return sb.toString();
    }

    public Object accept(VisitorIF visitor) throws Exception {
        return visitor.visitInteractive(this);
    }

    public void traverse(VisitorIF visitor) throws Exception {
        if (body != null) {
            for (int i = 0; i < body.length; i++) {
                if (body[i] != null)
                    body[i].accept(visitor);
            }
        }
    }

}
