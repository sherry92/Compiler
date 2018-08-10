import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.LinkedList;

public class codegenerator {
	private int ifLabelN = 1;
	private int loopLabelN = 1;
	private String ucodeStr = "";

	codegenerator(VariableMap vMap, Program out) {
		CodeStartMain(out.decpart);
		CodeDeclaration(vMap, out.decpart);
		CodeBody(vMap, out.body);
		CodeEndMain();
	}

	public void CodeStartMain(Declarations ds) {
		String fileName = "Ucode";
		int size = ds.size();
	}

	public void CodeEndMain() {
		String fileName = "Ucode";
		ucodeStr += "exit       nop\n" + "           end";
	}

	public void CodeDeclaration(VariableMap m, Declarations ds) {
		String fileName = "Ucode";
		String key;
		int count = 0;
		LinkedList<Integer> a = new<Integer> LinkedList();
		for (Declaration d : ds) {
			key = d.v.toString();
			if (key.indexOf('[') >= 0) {
				a.add(1);
			} else
				a.add(0);
		}
		int i = 0;
		int offest = 0, wordleng = 0, index = 0;
		int j=0;
		int end;
		while (a.get(j)==1){
			if(j+1==a.size()) break;
			else j++;
		}
		for (Declaration d : ds) {
			key = d.v.toString();
			if (a.get(i) == 0) {
				ucodeStr += "           sym" + " " + m.get(key).getSegment() + " " + m.get(key).getOffSet() + " "
						+ m.get(key).getWordLength() + "\n";
				offest = m.get(key).getOffSet();
				wordleng = m.get(key).getWordLength();
				index = i;
			} else {
				if (i + 1 == a.size()) {
					if(a.get(0)==1){
						if(i==j){
						ucodeStr += "           sym" + " " + m.get(key).getSegment() + " " + (offest + wordleng+1) + " "
								+ (i - index+1) + "\n";}
						else ucodeStr += "           sym" + " " + m.get(key).getSegment() + " " + (offest + wordleng) + " "
								+ (i - index) + "\n";
					}
					else{ucodeStr += "           sym" + " " + m.get(key).getSegment() + " " + (offest + wordleng) + " "
							+ (i - index) + "\n";}
				} else if (a.get(i + 1) == 0) {
					if(a.get(0)==1){
						if(i==j-1){
							ucodeStr += "           sym" + " " + m.get(key).getSegment() + " " + (offest + wordleng+1) + " "
									+ (i - index+1) + "\n";}
						else ucodeStr += "           sym" + " " + m.get(key).getSegment() + " " + (offest + wordleng) + " "
									+ (i - index) + "\n";
					}
					else{
					ucodeStr += "           sym" + " " + m.get(key).getSegment() + " " + (offest + wordleng) + " "
							+ (i - index) + "\n";
					}
				}
			}
			count = count + m.get(key).getWordLength();
			i++;
		}
		ucodeStr += "           bgn " + count + "\n";
	}

	public void CodeBody(VariableMap m, Block b) {
		for (Statement s : b.members) {
			if (s instanceof Skip)
				;
			if (s instanceof Assignment)
				CodeAssignment(m, (Assignment) s);
			if (s instanceof Conditional)
				CodeConditional(m, (Conditional) s);
			if (s instanceof Read)
				CodeRead(m, (Read) s);
			if (s instanceof Rand)
				CodeRand(m, (Rand) s);
			if (s instanceof Write)
				CodeWrite(m, (Write) s);
			if (s instanceof Loop)
				CodeLoop(m, (Loop) s);
			if (s instanceof Block)
				CodeBlock(m, (Block) s);
		}
	}

	public void CodeStatement(VariableMap m, Statement s) {
		if (s instanceof Skip)
			;
		if (s instanceof Assignment)
			CodeAssignment(m, (Assignment) s);
		if (s instanceof Conditional)
			CodeConditional(m, (Conditional) s);
		if (s instanceof Read)
			CodeRead(m, (Read) s);
		if (s instanceof Rand)
			CodeRand(m, (Rand) s);
		if (s instanceof Write)
			CodeWrite(m, (Write) s);
		if (s instanceof Loop)
			CodeLoop(m, (Loop) s);
		if (s instanceof Block)
			CodeBlock(m, (Block) s);
	}

	public void CodeAssignment(VariableMap m, Assignment a) {
		String fileName = "Ucode";
		String key = a.target.toString();
		CodeExpression(m, a.source);
		ucodeStr += "           str" + " " + m.get(key).getSegment() + " " + m.get(key).getOffSet() + "\n";
	}

	public void CodeRead(VariableMap m, Read r) {
		String fileName = "Ucode";
		String key = r.target.toString();
		ucodeStr += "           ldp" + "\n";
		ucodeStr += "           lda" + " " + m.get(key).getSegment() + " " + m.get(key).getOffSet() + "\n";
		ucodeStr += "           call read" + "\n";
	}

	public void CodeWrite(VariableMap m, Write r) {
		String fileName = "Ucode";
		String key = r.target.toString();
		ucodeStr += "           ldp" + "\n";
		ucodeStr += "           lod" + " " + m.get(key).getSegment() + " " + m.get(key).getOffSet() + "\n";
		ucodeStr += "           call write" + "\n";
	}

	public void CodeRand(VariableMap m, Rand r) {
		String fileName = "Ucode";
		String key = r.target.toString();
		int x = (int) (Math.random() * 100000000);
		ucodeStr += "           ldc " + x + "\n";
		ucodeStr += "           str" + " " + m.get(key).getSegment() + " " + m.get(key).getOffSet() + "\n";
	}

	public void CodeConditional(VariableMap m, Conditional c) {
		Expression test = c.test;
		Statement thenBranch = c.thenbranch;
		Statement elseBranch = c.elsebranch;
		String asdf = "abcdefghijklmnopqrstuvwxyz";
		String labelName = "Ifco" + asdf.charAt(ifLabelN - 1);
		ifLabelN++;
		String fileName = "Ucode";

		CodeExpression(m, test);
		ucodeStr += "           fjp" + " " + labelName + "\n";
		CodeStatement(m, thenBranch);
		ucodeStr += labelName + "      nop" + "\n";
		CodeStatement(m, elseBranch);
	}

	public void CodeLoop(VariableMap m, Loop l) {
		Expression test = l.test;
		Statement body = l.body;
		String asdf = "abcdefghijklmnopqrstuvwxyz";
		String labelName = "Loop" + asdf.charAt(loopLabelN - 1);
		String exitLabelName = "LoEX" + asdf.charAt(loopLabelN - 1);
		loopLabelN++;
		String fileName = "Ucode";

		CodeExpression(m, test);
		ucodeStr += "           fjp" + " " + exitLabelName + "\n";
		ucodeStr += labelName + "   " + "   nop" + "\n";
		CodeStatement(m, body);
		CodeExpression(m, test);
		ucodeStr += "           tjp" + " " + labelName + "\n";
		ucodeStr += exitLabelName + "      nop" + "\n";
	}

	public void CodeBlock(VariableMap m, Block b) {
		for (Statement s : b.members) {
			if (s instanceof Skip)
				;
			if (s instanceof Assignment)
				CodeAssignment(m, (Assignment) s);
			if (s instanceof Conditional)
				CodeConditional(m, (Conditional) s);
			if (s instanceof Read)
				CodeRead(m, (Read) s);
			if (s instanceof Rand)
				CodeRand(m, (Rand) s);
			if (s instanceof Write)
				CodeWrite(m, (Write) s);
			if (s instanceof Loop)
				CodeLoop(m, (Loop) s);
			if (s instanceof Block)
				CodeBlock(m, (Block) s);
		}
	}

	public void CodeExpression(VariableMap m, Expression e) {
		if (e instanceof Value)
			CodeValue(m, (Value) e);
		if (e instanceof Variable)
			CodeVariable(m, (Variable) e);
		if (e instanceof Binary)
			CodeBinary(m, (Binary) e);
		if (e instanceof Unary)
			CodeUnary(m, (Unary) e);

	}

	public void CodeValue(VariableMap m, Value v) {
		if (v instanceof BoolValue)
			CodeBoolValue(m, (BoolValue) v);
		if (v instanceof IntValue)
			CodeIntValue(m, (IntValue) v);
		if (v instanceof CharValue)
			CodeCharValue(m, (CharValue) v);

	}

	public void CodeVariable(VariableMap m, Variable v) {
		String fileName = "Ucode";

		String key = v.toString();
		ucodeStr += "           lod" + " " + m.get(key).getSegment() + " " + m.get(key).getOffSet() + "\n";

	}

	public void CodeBinary(VariableMap m, Binary b) {

		String strOp = b.op.toString();
		CodeExpression(m, b.term1);
		CodeExpression(m, b.term2);

		String fileName = "Ucode";

		switch (strOp) {
		case "&&":
			ucodeStr += "           and" + "\n";
			break;

		case "||":
			ucodeStr += "           or" + "\n";
			break;

		case "INT<":
		case "CHAR<":
		case "BOOL<":
			ucodeStr += "           gt" + "\n";
			break;

		case "INT<=":
		case "CHAR<=":
		case "BOOL<=":
			ucodeStr += "           ge" + "\n";
			break;

		case "INT==":
		case "CHAR==":
		case "BOOL==":
			ucodeStr += "           eq" + "\n";
			break;

		case "INT!=":
		case "CHAR!=":
		case "BOOL!=":
			ucodeStr += "           ne" + "\n";
			break;

		case "INT>":
		case "CHAR>":
		case "BOOL>":
			ucodeStr += "           lt" + "\n";
			break;

		case "INT>=":
		case "CHAR>=":
		case "BOOL>=":
			ucodeStr += "           le" + "\n";
			break;

		case "INT+":
			ucodeStr += "           add" + "\n";
			break;

		case "INT-":
			ucodeStr += "           sub" + "\n";
			break;

		case "INT*":
			ucodeStr += "           mult" + "\n";
			break;

		case "INT/":
			ucodeStr += "           div" + "\n";
			break;

		case "INT%":
			ucodeStr += "           mod" + "\n";
			break;

		default:
			break;
		}

	}

	public void CodeUnary(VariableMap m, Unary u) {

		String opString = u.op.toString();
		CodeExpression(m, u.term);
		if (opString == "!") {
			String fileName = "Ucode";

			ucodeStr += "           notop" + "\n";
		} else if (opString == "-") {
			String fileName = "Ucode";

			ucodeStr += "           neg" + "\n";
		} else {// 에러
		}
	}

	public void CodeBoolValue(VariableMap m, BoolValue b) {
		if (b.boolValue()) {
			String fileName = "Ucode";
			ucodeStr += "           ldc" + " 1" + "\n";
		} else {
			String fileName = "Ucode";
			ucodeStr += "           ldc" + " 0" + "\n";
		}
	}

	public void CodeIntValue(VariableMap m, IntValue i) {
		String fileName = "Ucode";
		ucodeStr += "           ldc" + " " + i + "\n";
	}

	public void CodeCharValue(VariableMap m, CharValue c) {
		String fileName = "Ucode";
		ucodeStr += "           ldc" + " " + (int) c.charValue() + "\n";
	}

	public String getCode() {
		return ucodeStr;
	}

	public void flush() {
		ucodeStr = "";
	}
	public static void main(String args[]) {
		System.out.println("ucode 생성");
		Parser parser = new Parser(new Lexer("1.txt"));
		Program prog = parser.program();
		TypeMap map = StaticTypeCheck.typing(prog.decpart);
		StaticTypeCheck.V(prog);
		Program out = TypeTransformer.T(prog, map);
		VtoLMapping m = new VtoLMapping();
		VariableMap vMap = m.typing(out.decpart);
		codegenerator G = new codegenerator(vMap, out);
		String retStr = G.getCode();
		G.flush();
		System.out.println(retStr);
	}
}
