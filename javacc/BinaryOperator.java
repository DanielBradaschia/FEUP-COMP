public class BinaryOperator extends SimpleNode {
    public BinaryOperator(int id) {
        super(id);
    }

    public BinaryOperator(JmmParser p, int id) {
        super(p, id);
    }

    public void applySemanticAnalysis(Table table) throws Exception {
        SimpleNode lhs = ((SimpleNode) children[0]);
        SimpleNode rhs = ((SimpleNode) children[1]);

        checkSide(lhs, table);
        checkSide(rhs, table);
    }

    public void checkSide(SimpleNode side, Table table) throws Exception {
        if (side.name != null) {
            if (side instanceof ASTADDSUB || side instanceof ASTMULDIV) {
                side.applySemanticAnalysis(table);
                return;
            }

            Symbol s = table.getSymbol(side.name);
            
            if (s != null) {
                if (!s.isInitialized()) {
                    throw new Exception("Variable " + side.name + " not initialized on line " + side.getLine());
                }
                if (!s.getType().equals("int")) {
                    throw new Exception("Incompatible type: " + side.name + " not of type int on line " + side.getLine());
                }
                return;
            }

            try { 
                Integer.parseInt(side.name); 
            } catch(NumberFormatException | NullPointerException e) { 
                throw new Exception("Incompatible type: " + side.name + " not of type int on line " + side.getLine());
            }

            return;
        }

        if (side instanceof ASTarray) {
            side.applySemanticAnalysis(table);
            return;
        }

        if (side instanceof ASTDOT) {
            side.applySemanticAnalysis(table);
            SimpleNode var = (SimpleNode) side.children[0];
            SimpleNode rhs = (SimpleNode) side.children[1];

            Symbol s = table.getSymbol(var.name);
            if (rhs instanceof ASTfunctionCall && s != null) {
                if (!s.getType().equals(JmmParser.getInstance().getClassTable().getType())) return;
                String fName = ((ASTfunctionCall) rhs).getMethodName();
                if (JmmParser.getInstance().containsMethod(fName)) {
                    if (JmmParser.getInstance().getMethod(fName).getType().equals("int")) return;
                    throw new Exception("Incompatible type: " + fName + " return type not of type int on line " + side.getLine());
                }
                throw new Exception("Unknown method on line " + side.getLine());
            }

            if (rhs instanceof ASTfunctionCall && var instanceof ASTNEW) {
                if (!JmmParser.getInstance().getClassTable().getType().equals(((SimpleNode) var.children[0]).name)) return;
                String fName = ((ASTfunctionCall) rhs).getMethodName();
                if (JmmParser.getInstance().containsMethod(fName)) {
                    if (JmmParser.getInstance().getMethod(fName).getType().equals("int")) return;
                    throw new Exception("Incompatible type: " + fName + " return type not of type int on line " + side.getLine());
                }
                throw new Exception("Unknown method on line " + side.getLine());
            }
            return;
        }

        throw new Exception("Found " + side.toString() + " and was expecting *, /, +, -, or integer value on line " + side.getLine());
    }
}