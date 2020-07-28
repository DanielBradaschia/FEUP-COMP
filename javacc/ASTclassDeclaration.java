public class ASTclassDeclaration extends SimpleNode {
  public ASTclassDeclaration(int id) {
    super(id);
  }

  public ASTclassDeclaration(JmmParser p, int id) {
    super(p, id);
  }

  public void applySemanticAnalysis(Table table) throws Exception {
    JmmParser.getInstance().getClassTable().setType(((SimpleNode) children[0]).name);

    if (children == null)
      return;

    for (int i = 0; i < children.length; i++) {
      ((SimpleNode) children[i]).applySemanticAnalysis(table);
    }
  }
}
