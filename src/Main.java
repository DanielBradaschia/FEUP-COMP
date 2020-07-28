public class Main {
	
	public static void main(String[] args) throws Exception {
	
		try {
            java.io.FileInputStream file = new java.io.FileInputStream(args[0]);
            JmmParser parser = new JmmParser(file);
            JmmParser.setInstance(parser);
            System.out.println(parser);
            SimpleNode root = parser.program();
            root.dump("");
            root.createTable(parser.getClassTable());
            parser.print();
            root.applySemanticAnalysis(parser.getClassTable());
            CodeParser codeGen = new CodeParser(root);
            codeGen.generate();
        } catch (Exception e) {
            System.err.println("\n\nTesting file: " + args[0]);
            e.printStackTrace();
            System.err.println("\n\n");
            throw new Exception(e);
        }
        
    }
}