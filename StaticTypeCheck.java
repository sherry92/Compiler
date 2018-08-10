import java.util.*;


public class StaticTypeCheck {

	public static TypeMap typing(Declarations d) {
		TypeMap map = new TypeMap();
		for (Declaration di : d){
			ArrayList<Type> arr = new ArrayList<>();
			arr.add(di.t);
			arr.add(di.inner_T);
			map.put(di.v, arr);
			
		}
		return map;
	}

	public static void check(boolean test, String msg) {
		if (test)
			return;
		System.err.println(msg);
		System.exit(1);
	}

	public static void V(Declarations d) {
		for (int i = 0; i < d.size() - 1; i++)
			for (int j = i + 1; j < d.size(); j++) {
				Declaration di = d.get(i);
				Declaration dj = d.get(j);
				check(!(di.v.equals(dj.v)), "duplicate declaration: " + dj.v);
			}
	}

	public static void V(Program p) {
		V(p.decpart);
		V(p.body, typing(p.decpart));
	}

	public static Type typeOf(Expression e, TypeMap tm) { 
		if (e instanceof Value)
			return ((Value) e).type;
		if (e instanceof Variable) {
			Variable v = (Variable) e; 
			check(tm.containsKey(v), "undefined variable: " + v);
			return (Type) tm.get(v).get(0);
		}
		if (e instanceof Binary) {
			Binary b = (Binary) e;
			if (b.op.ArithmeticOp())
				return (Type.INT);
			if (b.op.RelationalOp() || b.op.BooleanOp())
				return (Type.BOOL);
		}
		if (e instanceof Unary) {
			Unary u = (Unary) e;
			if (u.op.NotOp())
				return (Type.BOOL);
			else if (u.op.NegateOp())
				return typeOf(u.term, tm);
			else if (u.op.intOp())
				return (Type.INT);
			else if (u.op.charOp())
				return (Type.CHAR);
		}
		throw new IllegalArgumentException("should never reach here");
	}

	public static void V(Expression e, TypeMap tm) {
		if (e instanceof Value)
			return;
		if (e instanceof Variable) {
			Variable v = (Variable) e;
			check(tm.containsKey(v), "undeclared variable: " + v);
			return;
		}
		if (e instanceof Binary) {
			Binary b = (Binary) e;
			Type typ1 = typeOf(b.term1, tm);
			Type typ2 = typeOf(b.term2, tm);
			V(b.term1, tm);
			V(b.term2, tm);
			if (b.op.ArithmeticOp())
				check(typ1 == typ2 && (typ1 == Type.INT), "type error for " + b.op);
			else if (b.op.RelationalOp())
				check(typ1 == typ2, "type error for " + b.op);
			else if (b.op.BooleanOp())
				check(typ1 == Type.BOOL && typ2 == Type.BOOL, b.op + ": non-bool operand");
			else
				throw new IllegalArgumentException("should never reach here BinaryOp error");
			return;
		}
		if (e instanceof Unary) {
			Unary u = (Unary) e;
			Type type = typeOf(u.term, tm); // start here
			V(u.term, tm);
			if (u.op.NotOp()) {
				check((type == Type.BOOL), "type error for NotOp " + u.op);
			} else if (u.op.NegateOp()) {
				check((type == (Type.INT)), "type error for NegateOp " + u.op);
			} else {
				throw new IllegalArgumentException("should never reach here UnaryOp error");
			}
			return;
		}

		throw new IllegalArgumentException("should never reach here");
	}

	public static void V(Statement s, TypeMap tm) {
		if (s == null)
			throw new IllegalArgumentException("AST error: null statement");
		else if (s instanceof Skip)
			return;
		else if (s instanceof Assignment) {
			Assignment a = (Assignment) s;
			check(tm.containsKey(a.target), " undefined target in assignment: " + a.target);
			V(a.source, tm);
			Type ttype = (Type) tm.get(a.target).get(0); 
			Type srctype = typeOf(a.source, tm); 
			if (ttype != srctype) {
				if (ttype == Type.INT)
					check(srctype == Type.CHAR, "mixed mode assignment to " + a.target);
				else
					check(false, "mixed mode assignment to " + a.target);
			}
			return;
		} else if (s instanceof Conditional) {
			Conditional c = (Conditional) s;
			V(c.test, tm);
			Type testtype = typeOf(c.test, tm);
			if (testtype == Type.BOOL) {
				V(c.thenbranch, tm);
				V(c.elsebranch, tm);
				return;
			} else {
				check(false, "poorly typed if in Conditional: " + c.test);
			}
		} else if (s instanceof Loop) {
			Loop l = (Loop) s;
			V(l.test, tm);
			Type testtype = typeOf(l.test, tm);
			if (testtype == Type.BOOL) {
				V(l.body, tm);
			} else {
				check(false, "poorly typed test in while Loop in Conditional: " + l.test);
			}
		} else if (s instanceof Write) {
			Write w = (Write) s;
			check(tm.containsKey(w.e1), " undefined target in assignment: " + w.e1);
			Type ttype = (Type) tm.get(w.e1).get(0); 

			return;
		} else if (s instanceof Read) {
			Read r = (Read) s;
			check(tm.containsKey(r.target), " undefined target in assignment: " + r.target);
			Type ttype = (Type) tm.get(r.target).get(0); 

			return;
		}  else if (s instanceof Block) {
			Block b = (Block) s;
			for (Statement i : b.members) {
				V(i, tm);
			}
		} else if (s instanceof Sort){
			Sort t = (Sort) s;
			check(tm.containsKey(t.target), " undefined target in assignment: " + t.target);
		} else if(s instanceof Push_Front){
			Push_Front p = (Push_Front) s;
			check(tm.containsKey(p.target)," undefined target in assignment: " + p.target);
			V(p.e1, tm);
			Type ttype = (Type) tm.get(p.target).get(1); 
			Type srctype = typeOf(p.e1, tm); 
			if (ttype != srctype) {
				System.out.println("타입이 달라요");
				System.exit(1);
			}
			
		} else if(s instanceof Push_Back){
			Push_Back p = (Push_Back) s;
			check(tm.containsKey(p.target)," undefined target in assignment: " + p.target);
			V(p.e1, tm);
			Type ttype = (Type) tm.get(p.target).get(1); 
			Type srctype = typeOf(p.e1, tm); 
			if (ttype != srctype) {
				System.out.println("타입이 달라요");
				System.exit(1);
			}			
		} else if(s instanceof Pop_Front){
			Pop_Front p = (Pop_Front) s;
			check(tm.containsKey(p.target), " undefined target in assignment: " + p.target);
		} else if(s instanceof Pop_Back){
			Pop_Back p = (Pop_Back) s;
			check(tm.containsKey(p.target), " undefined target in assignment: " + p.target);
		}
		
		else {
			throw new IllegalArgumentException("should never reach here");
		}
	}

}
