/* Generated By:JJTree: Do not edit this line. ASTClass.java Version 6.0 */
 /* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=true,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package cz.fit.cvut.czechjava.parser;

public class ASTClass extends SimpleNode {

    String name;
    String extending;

    public ASTClass(int id) {
        super(id);
    }

    public ASTClass(CZECHJavaParser p, int id) {
        super(p, id);
    }

    public String toString() {
        return super.toString() + "[" + name + " < " + extending + "]";
    }

    public String getName() {
        return name;
    }

    public String getExtending() {
        return extending;
    }

}
/* JavaCC - OriginalChecksum=5783ec5b767d370be22d89791b8fe701 (do not edit this line) */
