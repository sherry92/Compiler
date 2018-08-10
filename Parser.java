import java.util.*;

public class Parser {
    // Recursive descent parser that inputs a C++Lite program and 
    // generates its abstract syntax.  Each method corresponds to
    // a concrete syntax grammar rule, which appears as a comment
    // at the beginning of the method.
  
    Token token;          // current token from the input stream
    Lexer lexer;
  
    public Parser(Lexer ts) { // Open the C++Lite source program
        lexer = ts;                          // as a token stream, and
        token = lexer.next();            // retrieve its first Token
//        System.out.println("Toke : " + token);
    }
  
    private String match (TokenType t) { // * return the string of a token if it matches with t *
        String value = token.value();
        if (token.type().equals(t)){
            token = lexer.next();
//            System.out.println("Toke : " + token);
        }
        else
            error(t);
        return value;
    }
  
    private void error(TokenType tok) {
        System.err.println("Syntax error: expecting: " + tok 
                           + "; saw: " + token);
        System.exit(1);
    }
  
    private void error(String tok) {
        System.err.println("Syntax error: expecting: " + tok 
                           + "; saw: " + token);
        System.exit(1);
    }
  
    public Program program() {
        // Program --> void main ( ) '{' Declarations Statements '}'
        TokenType[ ] header = {TokenType.Int, TokenType.Main,
                          TokenType.LeftParen, TokenType.RightParen};
        // Program의 시작은 int main ()
        for (int i=0; i<header.length; i++)   // bypass "int main ( )"
            match(header[i]);
        match(TokenType.LeftBrace);
        // student exercise
        // { Declarations, Statements } 순서대로 Token이 오는가를 확인
        Declarations d = declarations();
        Block b = startStatements();
        match(TokenType.RightBrace);
        return new Program(d, b);  // student exercise
    }

    private Declarations declarations () {
        // Declarations --> { Declaration }
    	Declarations d = new Declarations();
    	// 다음 토큰이 Type이라면 declaration node 생성
    	while(isType()) declaration(d);
        return d;  // student exercise
    }
  
    private void declaration (Declarations ds) {
        // Declaration  --> Type Identifier { , Identifier } ;
        // student exercise
    	Variable v;
    	Declaration d;
    	if(token.type().equals(TokenType.Stack)||token.type().equals(TokenType.Queue)
    			|| token.type().equals(TokenType.List)
    			|| token.type().equals(TokenType.Deque)){
    		if(token.type().equals(TokenType.Stack)){
    			token = lexer.next();
    			v = new Variable(match(TokenType.Identifier));
    			match(TokenType.LeftParen);
    			Type in = type();
    			match(TokenType.RightParen);
    			Type t = Type.STACK;
        		d = new Declaration(v, t, in);
    			ds.add(d);
            	while(isComma()){
        			token = lexer.next();
        			v = new Variable(match(TokenType.Identifier));
        			match(TokenType.LeftParen);
        			in = type();
        			match(TokenType.RightParen);
        			t = Type.STACK;
            		d = new Declaration(v, t, in);
        			ds.add(d);
            	}
    		}
    		else if(token.type().equals(TokenType.Queue)){
    			token = lexer.next();
    			v = new Variable(match(TokenType.Identifier));
    			match(TokenType.LeftParen);
    			Type in = type();
    			match(TokenType.RightParen);
    			Type t = Type.QUEUE;
        		d = new Declaration(v, t, in);
    			ds.add(d);
            	while(isComma()){
        			token = lexer.next();
        			v = new Variable(match(TokenType.Identifier));
        			match(TokenType.LeftParen);
        			in = type();
        			match(TokenType.RightParen);
        			t = Type.QUEUE;
            		d = new Declaration(v, t, in);
        			ds.add(d);
            	}

    		}
    		else if(token.type().equals(TokenType.List)){
    			token = lexer.next();
    			v = new Variable(match(TokenType.Identifier));
    			match(TokenType.LeftParen);
    			Type in = type();
    			match(TokenType.RightParen);
    			Type t = Type.LIST;
        		d = new Declaration(v, t, in);
    			ds.add(d);
            	while(isComma()){
        			token = lexer.next();
        			v = new Variable(match(TokenType.Identifier));
        			match(TokenType.LeftParen);
        			in = type();
        			match(TokenType.RightParen);
        			t = Type.LIST;
            		d = new Declaration(v, t, in);
        			ds.add(d);
            	}
    		}
    		else if(token.type().equals(TokenType.Deque)){
    			token = lexer.next();
    			v = new Variable(match(TokenType.Identifier));
    			match(TokenType.LeftParen);
    			Type in = type();
    			match(TokenType.RightParen);
    			Type t = Type.DEQUE;
        		d = new Declaration(v, t, in);
    			ds.add(d);
            	while(isComma()){
        			token = lexer.next();
        			v = new Variable(match(TokenType.Identifier));
        			match(TokenType.LeftParen);
        			in = type();
        			match(TokenType.RightParen);
        			t = Type.DEQUE;
            		d = new Declaration(v, t, in);
        			ds.add(d);
            	}
    		}
    	}
    	else{
        	Type t = type();
        	
        	v = new Variable(match(TokenType.Identifier));
        	d = new Declaration(v, t);
        	// Declarations 노드에 새로운 노드 d를 추가
        	ds.add(d);
        	
        	// ,로 연결되는 선언부가 있다면 계속해서 추가
        	while(isComma()){
        		token = lexer.next();
        		v = new Variable(match(TokenType.Identifier));
        		d = new Declaration(v, t);
        		ds.add(d);
//                System.out.println("Toke : " + token);
        	}
        	// 선언부 끝에 semicolon 확인    		
    	}
    	match(TokenType.Semicolon); 
    }
  
    private Type type () {
        // Type  -->  int | bool | float | char 
        Type t = null;
        // student exercise
        if(token.type().equals(TokenType.Int)) t = Type.INT;
        else if(token.type().equals(TokenType.Float)) t = Type.FLOAT;
        else if(token.type().equals(TokenType.Char)) t = Type.CHAR;
        else if(token.type().equals(TokenType.Bool)) t = Type.BOOL;
        else error("Type Error");
        token = lexer.next();
 //       System.out.println("Toke : " + token);
        return t;          
    }
  
    private Statement statement() {
        // Statement --> ; | Block | Assignment | IfStatement | WhileStatement
        Statement s = null;
        Variable v;
        Type t;
        // student exercise
        // 다음으로 오는 토큰 종류에 따라 Statement 생성
        if(token.type().equals(TokenType.Semicolon)) {
        	s = new Skip();
        	token = lexer.next();
        }
        else if(token.type().equals(TokenType.LeftBrace)) s = statements();
        else if(token.type().equals(TokenType.Identifier))s = assignment();
        else if(token.type().equals(TokenType.If)) s = ifStatement();
        else if(token.type().equals(TokenType.While)) s = whileStatement();
        else if(token.type().equals(TokenType.Push_Front)) s = Push_Front_Statement();
        else if(token.type().equals(TokenType.Push_Back)) s = Push_Back_Statement();
        else if(token.type().equals(TokenType.Pop_Front)) s = Pop_Front_Statement();
        else if(token.type().equals(TokenType.Pop_Back)) s = Pop_Back_Statement();
        else if(token.type().equals(TokenType.Read)) s = Read_Statement();
        else if(token.type().equals(TokenType.Write)) s = Write_Statement();
        else if(token.type().equals(TokenType.Sort)) s = Sort_Statement();
        else error("Statement Error");
        return s;
    }
    
    private Sort Sort_Statement(){
    	System.out.println(token.value());
    	token = lexer.next();
    	match(TokenType.LeftParen);
    	Variable v = new Variable(match(TokenType.Identifier));
    	match(TokenType.RightParen);
    	match(TokenType.Semicolon);
    	return new Sort(v);
    }
    
    private Read Read_Statement(){
    	token = lexer.next();
    	match(TokenType.LeftParen);
    	Variable v = new Variable(match(TokenType.Identifier));
    	match(TokenType.RightParen);
    	match(TokenType.Semicolon);
    	return new Read(v);
    }
    
    private Write Write_Statement(){
    	token = lexer.next();
    	match(TokenType.LeftParen);
    	Expression e;
    	e = expression();
    	match(TokenType.RightParen);
    	match(TokenType.Semicolon);
    	return new Write(e);
    }
    
    
    private Push_Front Push_Front_Statement(){
    	Expression e;
    	token = lexer.next();
    	match(TokenType.LeftParen);
    	Variable v = new Variable(match(TokenType.Identifier));
    	match(TokenType.Comma);
    	e = expression();
    	match(TokenType.RightParen);
    	match(TokenType.Semicolon);
    	return new Push_Front(v, e);
    }
    
    private Push_Back Push_Back_Statement(){
    	Expression e;
    	token = lexer.next();
    	match(TokenType.LeftParen);
    	Variable v = new Variable(match(TokenType.Identifier));
    	match(TokenType.Comma);
    	e = expression();
    	match(TokenType.RightParen);
    	match(TokenType.Semicolon);
    	return new Push_Back(v, e);
    }
    
    private Pop_Front Pop_Front_Statement(){
    	token = lexer.next();
    	match(TokenType.LeftParen);
    	Variable v = new Variable(match(TokenType.Identifier));
    	match(TokenType.RightParen);
    	match(TokenType.Semicolon);
    	return new Pop_Front(v);
    }
    
    private Pop_Back Pop_Back_Statement(){
    	token = lexer.next();
    	match(TokenType.LeftParen);
    	Variable v = new Variable(match(TokenType.Identifier));
    	match(TokenType.RightParen);
    	match(TokenType.Semicolon);
    	return new Pop_Back(v);
    }    
    private Block statements () {
        // Block --> '{' Statements '}'
        // student exercise
    	Block b = new Block();
        Statement s;
        match(TokenType.LeftBrace);
        // Block안에 새로운 statement가 있다면 안으로 계속 추가
        while(isStatement()){
        	s = statement();
        	b.members.add(s);
        }
        // Block 종료 확인
        match(TokenType.RightBrace);
        return b;
    }
    
    private Block startStatements(){
    	// Program 생성자가 LeftBrace와 RightBrace를 확인하므로 
    	// 시작하는 Block의 경우는 '{' 와 '}'의 존재여부를 검사하지 않음
    	Block b = new Block();
    	Statement s;
    	while(isStatement()){
    		s = statement();
    		b.members.add(s);
    	}
    	return b;
    }
  
    private Assignment assignment () {
        // Assignment --> Identifier = Expression ;
    	Expression e;
    	Variable v;
    	
    	// Assignment 정의에 따라 Token이 오는지 검사하고 expression node를 생성
    	v = new Variable(match(TokenType.Identifier));
    	match(TokenType.Assign);
    	e = expression();
    	match(TokenType.Semicolon);
        return new Assignment(v, e);  // student exercise
    }
  
    private Conditional ifStatement () {
        // IfStatement --> if ( Expression ) Statement [ else Statement ]
    	Conditional c;
    	Statement s;
    	Expression e;
    	
    	// IfStatement의 정의에 따라 Token이 오는지 검사하고 expression node를 생성
    	match(TokenType.If);
    	match(TokenType.LeftParen);
    	e = expression();
    	match(TokenType.RightParen);
    	s = statement();
    	// 만약 elese 파트가 있다면 추가적인 작업 수행
    	if(token.type().equals(TokenType.Else)){
        	token = lexer.next();
    		Statement el = statement();
    		c = new Conditional(e, s, el);
    	}else{
    		c = new Conditional(e, s);
    	}
        return c;  // student exercise
    }
  
    private Loop whileStatement () {
        // WhileStatement --> while ( Expression ) Statement
    	Statement s;
    	Expression e;
    	
    	// WhileStatement의 정의에 따라 Token이 오는지 검사
    	match(TokenType.While);
    	match(TokenType.LeftParen);
    	e = expression();
    	match(TokenType.RightParen);
    	s = statement();
    	
        return new Loop(e, s);  // student exercise
    }

    private Expression expression () {
        // Expression --> Conjunction { || Conjunction }
    	Expression c = conjunction();
    	while(token.type().equals(TokenType.Or)){
    		Operator op = new Operator(match(token.type()));
    		Expression e = expression();
    		c = new Binary(op, c, e);
    	}
        return c;  // student exercise
    }
  
    private Expression conjunction () {
        // Conjunction --> Equality { && Equality }
    	Expression eq = equality();
    	while(token.type().equals(TokenType.And)){
    		Operator op = new Operator(match(token.type()));
    		Expression c = conjunction();
    		eq = new Binary(op, eq, c);
    	}
        return eq;  // student exercise
    }
  
    private Expression equality () {
        // Equality --> Relation [ EquOp Relation ]
    	Expression re = relation();
    	while(isEqualityOp()){
    		Operator op = new Operator(match(token.type()));
    		Expression re2 = relation();
    		re = new Binary(op, re, re2);
    	}
        return re;  // student exercise
    }

    private Expression relation (){
        // Relation --> Addition [RelOp Addition]
    	Expression a = addition();
    	while(isRelationalOp()){
    		Operator op = new Operator(match(token.type()));
    		Expression a2 = addition();
    		a = new Binary(op, a, a2);
    	}
        return a;  // student exercise
    }
  
    private Expression addition () {
        // Addition --> Term { AddOp Term }
        Expression e = term();
        while (isAddOp()) {
            Operator op = new Operator(match(token.type()));
            Expression term2 = term();
            e = new Binary(op, e, term2);
        }
        return e;
    }
  
    private Expression term () {
        // Term --> Factor { MultiplyOp Factor }
        Expression e = factor();
        while (isMultiplyOp()) {
            Operator op = new Operator(match(token.type()));
            Expression term2 = factor();
            e = new Binary(op, e, term2);
        }
        return e;
    }
  
    private Expression factor() {
        // Factor --> [ UnaryOp ] Primary 
        if (isUnaryOp()) {
            Operator op = new Operator(match(token.type()));
            Expression term = primary();
            return new Unary(op, term);
        }
        else return primary();
    }
  
    private Expression primary () {
        // Primary --> Identifier | Literal | ( Expression )
        //             | Type ( Expression )
        Expression e = null;
        if (token.type().equals(TokenType.Identifier)) {
            e = new Variable(match(TokenType.Identifier));
        } else if (isLiteral()) {
            e = literal();
        } else if (token.type().equals(TokenType.LeftParen)) {
            token = lexer.next();
 //           System.out.println("Toke : " + token);
            e = expression();       
            match(TokenType.RightParen);
        } else if (isType( )) {
            Operator op = new Operator(match(token.type()));
            match(TokenType.LeftParen);
            Expression term = expression();
            match(TokenType.RightParen);
            e = new Unary(op, term);
        } else error("Identifier | Literal | ( | Type");
        return e;
    }

    private Value literal( ) {
    	Value v = null;
    	String str = token.value();
    	if(token.type().equals(TokenType.IntLiteral)){
    		v = new IntValue(Integer.parseInt(str));
    		token = lexer.next();
    	}
    	else if(token.type().equals(TokenType.FloatLiteral)){
    		v = new FloatValue(Float.parseFloat(str));
    		token = lexer.next();
    	}
    	else if(token.type().equals(TokenType.CharLiteral)){
    		v = new CharValue(str.charAt(0));
    		token = lexer.next();
    	}
    	else if(token.type().equals(TokenType.True)){
    		v = new BoolValue(true);
    		token = lexer.next();
    	}
    	else if(token.type().equals(TokenType.False)){
    		v = new BoolValue(false);
    		token = lexer.next();
    	} else error("ilegal literal");
//        System.out.println("Toke : " + token);

        return v;  // student exercise
    }
    
    private boolean isAddOp( ) {
        return token.type().equals(TokenType.Plus) ||
               token.type().equals(TokenType.Minus);
    }
    
    private boolean isMultiplyOp( ) {
        return token.type().equals(TokenType.Multiply) ||
               token.type().equals(TokenType.Divide);
    }
    
    private boolean isUnaryOp( ) {
        return token.type().equals(TokenType.Not) ||
               token.type().equals(TokenType.Minus);
    }
    
    private boolean isEqualityOp( ) {
        return token.type().equals(TokenType.Equals) ||
            token.type().equals(TokenType.NotEqual);
    }
    
    private boolean isRelationalOp( ) {
        return token.type().equals(TokenType.Less) ||
               token.type().equals(TokenType.LessEqual) || 
               token.type().equals(TokenType.Greater) ||
               token.type().equals(TokenType.GreaterEqual);
    }
    
    private boolean isType( ) {
        return token.type().equals(TokenType.Int)
            || token.type().equals(TokenType.Bool) 
            || token.type().equals(TokenType.Float)
            || token.type().equals(TokenType.Char)
            || token.type().equals(TokenType.Stack)
            || token.type().equals(TokenType.Queue)
            || token.type().equals(TokenType.List)
            || token.type().equals(TokenType.Deque);
    }
    
    private boolean isLiteral( ) {
        return token.type().equals(TokenType.IntLiteral) ||
            isBooleanLiteral() ||
            token.type().equals(TokenType.FloatLiteral) ||
            token.type().equals(TokenType.CharLiteral);
    }
    
    private boolean isBooleanLiteral( ) {
        return token.type().equals(TokenType.True) ||
            token.type().equals(TokenType.False);
    }
    
    private boolean isComma(){
    	return token.type().equals(TokenType.Comma);
    }
    
    private boolean isSemicolon(){
    	return token.type().equals(TokenType.Semicolon);
    }
    
    private boolean isLeftBrace(){
    	return token.type().equals(TokenType.LeftBrace);
    }
    
    private boolean isRightBrace(){
    	return token.type().equals(TokenType.RightBrace);
    }
     
    private boolean isStatement(){
    	return isSemicolon() || isLeftBrace() ||
    			token.type().equals(TokenType.If) ||
    			token.type().equals(TokenType.While) ||
    			token.type().equals(TokenType.Identifier) ||
    			token.type().equals(TokenType.Push_Front) ||
    			token.type().equals(TokenType.Push_Back) ||
    			token.type().equals(TokenType.Pop_Back) ||
    			token.type().equals(TokenType.Pop_Front) ||
    			token.type().equals(TokenType.Sort) ||
    			token.type().equals(TokenType.Read) ||
    			token.type().equals(TokenType.Write);
    }
    
    public static void main(String args[]) {
        Parser parser  = new Parser(new Lexer(args[0]));
        Program prog = parser.program();
        
//        prog.display(0);           // display abstract syntax tree
    } //main

} // Parser
