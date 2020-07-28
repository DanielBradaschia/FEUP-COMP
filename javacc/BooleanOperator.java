public class BooleanOperator extends SimpleNode {
    public BooleanOperator(int id) {
        super(id);
    }

    public BooleanOperator(JmmParser p, int id) {
        super(p, id);
    }

    public void applySemanticAnalysis(Table table) throws Exception {
        SimpleNode lhs = ((SimpleNode) children[0]);
        SimpleNode rhs = ((SimpleNode) children[1]);

        checkSide(lhs, table);
        checkSide(rhs, table);
    }

    public void checkSide(SimpleNode side, Table table) throws Exception { //methods?
        if(table == null) return;
        if (side.name != null) {
            Symbol s = table.getSymbol(side.name);
            if (s != null) {
                if (!s.isInitialized()) {
                    throw new Exception("Variable " + side.name + " not initialized on line " + side.getLine());
                    
                }
                if (!s.getType().equals("boolean")) {
                    throw new Exception("Incompatible type: " + side.name + " not of type boolean on line " + side.getLine());
                    
                }
            }
            return;
        } 

        if (side instanceof ASTTRUE || side instanceof ASTFALSE) return; 

        if (side instanceof ASTMINOR || side instanceof ASTCOMMERCIALE || side instanceof ASTEXCLAMATION) {
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
                    if (JmmParser.getInstance().getMethod(fName).getType().equals("boolean")) return;
                    throw new Exception("Incompatible type: " + fName + " return type not of type boolean on line " + side.getLine());
                    
                }
                throw new Exception("Unknown method on line " + side.getLine());
                
            }

            if (rhs instanceof ASTfunctionCall && var instanceof ASTNEW) {
                if (!JmmParser.getInstance().getClassTable().getType().equals(((SimpleNode) var.children[0]).name)) return;
                String fName = ((ASTfunctionCall) rhs).getMethodName();
                if (JmmParser.getInstance().containsMethod(fName)) {
                    if (JmmParser.getInstance().getMethod(fName).getType().equals("boolean")) return;
                    throw new Exception("Incompatible type: " + fName + " return type not of type boolean on line " + side.getLine());
                    
                }
                throw new Exception("Unknown method on line " + side.getLine());
                
            }
            return;
        }

        throw new Exception("Found " + side.toString() + " and was expecting <, && or boolean value on line " + side.getLine());
        
    }
}