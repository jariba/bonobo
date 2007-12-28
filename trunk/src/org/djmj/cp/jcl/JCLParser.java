// $ANTLR 2.7.2: "JCLGrammar.g" -> "JCLParser.java"$

package org.djmj.cp.jcl;

import java.io.*;
import java.util.*;


import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.ANTLRException;
import antlr.LLkParser;
import antlr.Token;
import antlr.TokenStream;
import antlr.RecognitionException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.ParserSharedInputState;
import antlr.collections.impl.BitSet;

public class JCLParser extends antlr.LLkParser       implements JCLParserTokenTypes
 {

    public JCLParserContext context_;

    protected boolean isNumber(DataType dt)
    {
        if (!dt.isPrimitive())
            return false;
        
        return ((PrimitiveDataType)dt).isNumber();
    }

protected JCLParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
}

public JCLParser(TokenBuffer tokenBuf) {
  this(tokenBuf,3);
}

protected JCLParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
}

public JCLParser(TokenStream lexer) {
  this(lexer,3);
}

public JCLParser(ParserSharedInputState state) {
  super(state,3);
  tokenNames = _tokenNames;
}

	public final void program() throws RecognitionException, TokenStreamException {
		
		
		{
		_loop3:
		do {
			if ((_tokenSet_0.member(LA(1)))) {
				stmt();
			}
			else {
				break _loop3;
			}
			
		} while (true);
		}
	}
	
	public final void stmt() throws RecognitionException, TokenStreamException {
		
		
		
		Expr expr=null;
		
		{
		switch ( LA(1)) {
		case INCLUDE:
		{
			includeFile();
			break;
		}
		case CLASS:
		{
			classDefinition();
			break;
		}
		default:
			if ((LA(1)==IDENTIFIER) && (LA(2)==IDENTIFIER) && (LA(3)==LPAREN)) {
				methodDefinition();
			}
			else if ((_tokenSet_1.member(LA(1))) && (_tokenSet_2.member(LA(2))) && (_tokenSet_3.member(LA(3)))) {
				expr=subStmt();
				
				try {
				expr.eval(context_);
				}
				catch (Exception e) {
				e.printStackTrace();
				throw new SemanticException(e.toString());
				}
				
			}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
	}
	
	public final void includeFile() throws RecognitionException, TokenStreamException {
		
		Token  s1 = null;
		
		match(INCLUDE);
		s1 = LT(1);
		match(STRING_LITERAL);
		
		try {
		String filename=s1.getText().replace('\"',' ').trim();
		JCLLexer sublexer = new JCLLexer(new FileInputStream(filename));
		JCLParser parser = new JCLParser(sublexer);
		parser.context_ = context_;
		parser.program();
		}
		catch (Exception e) {
		String filename=s1.getText().replace('\"',' ').trim();
		context_.getOut().println("Failed including file "+filename+" : "+e);
		File f = new File(filename);
		context_.getOut().println("File is : "+f.getAbsolutePath());  
		context_.getOut().println("Default dir is : "+System.getProperty("user.dir"));
		}
		
	}
	
	public final void classDefinition() throws RecognitionException, TokenStreamException {
		
		Token  n = null;
		
		
		Map members=new TreeMap();
		ClassDataType superclass=null;
		List member=null;
		
		match(CLASS);
		n = LT(1);
		match(IDENTIFIER);
		{
		switch ( LA(1)) {
		case EXTENDS:
		{
			match(EXTENDS);
			superclass=class_data_type();
			break;
		}
		case LCURLY:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(LCURLY);
		{
		_loop10:
		do {
			if ((LA(1)==IDENTIFIER||LA(1)==CONSTRAINED)) {
				member=class_member();
				
				// TODO: Check for duplicate member names in superclass
				if (members.get(member.get(0)) != null)
				throw new SemanticException("Member "+member.get(0)+" is already defined");
				
				// TODO: if dt.isConstrained() 
				// then This class must inherit from CObject, throw exception otherwise
				members.put(member.get(0),  // name
				member.get(1)); // data type
				
			}
			else {
				break _loop10;
			}
			
		} while (true);
		}
		match(RCURLY);
		
		String name = n.getText();
		if (context_.getSymbolTable().getDataType(name)!=null)
		throw new SemanticException("Class "+name+" has already been defined");
		
		context_.getSymbolTable().addDataType(new ClassDataType(name,superclass,members));
		context_.getOut().println("Parsed class declaration : "+name); 
		
	}
	
	public final void methodDefinition() throws RecognitionException, TokenStreamException {
		
		Token  name = null;
		
		
		DataType returnDT=null;
		List parameters=null;
		List parameterNames=null;
		List parameterDTs=null;
		Expr bodyExpr=null;
		
		returnDT=data_type();
		name = LT(1);
		match(IDENTIFIER);
		match(LPAREN);
		parameters=parameterList();
		match(RPAREN);
		
		parameterNames = (List)parameters.get(0);
		parameterDTs = (List)parameters.get(1);
		JCLSymbolTable methodST = new JCLSymbolTable(context_.getSymbolTable());
		// Add return value as var
		Variable returnVar = new Variable(name.getText(),returnDT);
		methodST.addVariable(returnVar);
		// Add parameters as vars
		for (int i=0;i<parameterNames.size();i++) {
		methodST.addVariable(
		new Variable(
		(String)parameterNames.get(i),
		(DataType)parameterDTs.get(i)
		)
		);
		}
		
		context_.setSymbolTable(methodST);
		
		bodyExpr=stmtBlock();
		
		context_.setSymbolTable(methodST.getParent());
		// TODO: check for redefinition and deal with it appropriately
		// can't define methods with same name and different return type
		context_.getSymbolTable().addMethod(
		new UserMethod(
		name.getText(),
		parameterNames,
		parameterDTs,
		returnDT,
		bodyExpr
		)
		);
		
	}
	
	public final Expr  subStmt() throws RecognitionException, TokenStreamException {
		Expr expr;
		
		
		switch ( LA(1)) {
		case LITERAL_if:
		{
			expr=ifStmt();
			break;
		}
		case LITERAL_while:
		{
			expr=whileStmt();
			break;
		}
		case LCURLY:
		{
			expr=stmtBlock();
			break;
		}
		case CONSTRAIN:
		{
			expr=constraintBlock();
			break;
		}
		case QUESTION:
		{
			expr=print_stmt();
			break;
		}
		default:
			if ((LA(1)==IDENTIFIER) && (LA(2)==IDENTIFIER)) {
				expr=var_declaration();
			}
			else if ((LA(1)==IDENTIFIER) && (LA(2)==ASSIGN||LA(2)==DOT)) {
				expr=assignment();
			}
			else if ((LA(1)==IDENTIFIER) && (LA(2)==LPAREN)) {
				expr=methodCall();
			}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		return expr;
	}
	
	public final ClassDataType  class_data_type() throws RecognitionException, TokenStreamException {
		ClassDataType cdt;
		
		
		
		DataType dt=null;
		
		dt=data_type();
		
		if (dt.isPrimitive())
		throw new SemanticException("Class name expected, not primitive type");
		
		cdt = (ClassDataType)dt;
		
		return cdt;
	}
	
	public final List  class_member() throws RecognitionException, TokenStreamException {
		List memberInfo;
		
		Token  m = null;
		
		
		boolean isConstrained=false;
		DataType dt=null;
		memberInfo = new Vector();
		
		{
		switch ( LA(1)) {
		case CONSTRAINED:
		{
			match(CONSTRAINED);
			isConstrained=true;
			break;
		}
		case IDENTIFIER:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		dt=data_type();
		m = LT(1);
		match(IDENTIFIER);
		
		if (isConstrained) {
		// TODO: cache constrained types to save space and time
		dt = new ConstrainedDataType(dt);
		}
		
		memberInfo.add(m.getText());
		memberInfo.add(dt);
		
		return memberInfo;
	}
	
	public final DataType  data_type() throws RecognitionException, TokenStreamException {
		DataType dt;
		
		Token  t = null;
		
		t = LT(1);
		match(IDENTIFIER);
		
		dt = context_.getSymbolTable().getDataType(t.getText());
		if (dt == null)
		throw new SemanticException("Unrecognized type:"+t.getText());
		
		return dt;
	}
	
	public final List  parameterList() throws RecognitionException, TokenStreamException {
		List pinfo;
		
		Token  name = null;
		Token  name1 = null;
		
		
		pinfo = new Vector();
		List names = new Vector();
		List dts = new Vector();
		pinfo.add(names);
		pinfo.add(dts);
		DataType dt=null;
		
		{
		switch ( LA(1)) {
		case IDENTIFIER:
		{
			{
			dt=data_type();
			name = LT(1);
			match(IDENTIFIER);
			}
			names.add(name.getText());dts.add(dt);
			{
			_loop19:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					{
					dt=data_type();
					name1 = LT(1);
					match(IDENTIFIER);
					}
					names.add(name1.getText());dts.add(dt);
				}
				else {
					break _loop19;
				}
				
			} while (true);
			}
			break;
		}
		case RPAREN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		return pinfo;
	}
	
	public final Expr  stmtBlock() throws RecognitionException, TokenStreamException {
		Expr expr;
		
		
		
		Expr stmtExpr=null;
		List subStmts = new Vector();
		
		match(LCURLY);
		{
		_loop23:
		do {
			if ((_tokenSet_1.member(LA(1)))) {
				stmtExpr=subStmt();
				subStmts.add(stmtExpr);
			}
			else {
				break _loop23;
			}
			
		} while (true);
		}
		match(RCURLY);
		
		expr = new ExprStmtBlock(subStmts);
		
		return expr;
	}
	
	public final Expr  var_declaration() throws RecognitionException, TokenStreamException {
		Expr expr;
		
		Token  id = null;
		
		
		DataType dt=null; 
		
		dt=data_type();
		id = LT(1);
		match(IDENTIFIER);
		
		String vname = id.getText();
		Variable v = context_.getSymbolTable().getVariable(vname);
		if (v != null)
		throw new SemanticException("Variable "+vname+" has already been defined");
		
		expr = new ExprVariableDecl(vname,dt);    
		context_.getOut().println("Parsed var declaration : "+vname);
		
		return expr;
	}
	
	public final Expr  assignment() throws RecognitionException, TokenStreamException {
		Expr expr;
		
		
		
		Expr lhs=null,rhs=null;
		
		lhs=lval();
		match(ASSIGN);
		rhs=expression();
		
		if (!lhs.getDataType().isAssignableFrom(rhs.getDataType()))
		throw new SemanticException(
		"Can't assign "+rhs.getDataType().getName()+
		" to "+lhs.getDataType().getName()); 
		
		expr = new ExprAssignment(lhs,rhs);
		//context_.getOut().println("Parsed assignment"); 
		
		return expr;
	}
	
	public final Expr  ifStmt() throws RecognitionException, TokenStreamException {
		Expr expr;
		
		
		
		Expr condExpr=null,ifExpr=null,elseExpr=null; 
		
		match(LITERAL_if);
		match(LPAREN);
		condExpr=expression();
		match(RPAREN);
		ifExpr=subStmt();
		{
		if ((LA(1)==LITERAL_else) && (_tokenSet_1.member(LA(2))) && (_tokenSet_2.member(LA(3)))) {
			match(LITERAL_else);
			elseExpr=subStmt();
		}
		else if ((_tokenSet_4.member(LA(1))) && (_tokenSet_5.member(LA(2))) && (_tokenSet_6.member(LA(3)))) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		
		if (!isNumber(condExpr.getDataType())) 
		throw new SemanticException("Condition on if statement must evaluate to a number");
		
		expr = new ExprIf(condExpr,ifExpr,elseExpr);
		
		return expr;
	}
	
	public final Expr  whileStmt() throws RecognitionException, TokenStreamException {
		Expr expr;
		
		
		
		Expr condExpr=null,bodyExpr=null,elseExpr=null;    
		
		match(LITERAL_while);
		match(LPAREN);
		condExpr=expression();
		match(RPAREN);
		bodyExpr=subStmt();
		
		if (!isNumber(condExpr.getDataType())) 
		throw new SemanticException("Condition on while statement must evaluate to a number");
		
		expr = new ExprWhile(condExpr,bodyExpr);
		
		return expr;
	}
	
	public final Expr  constraintBlock() throws RecognitionException, TokenStreamException {
		Expr expr;
		
		
		
		Expr varExpr=null;
		List cstmts = new Vector();
		CStmt cstmt = null;
		
		match(CONSTRAIN);
		match(LPAREN);
		varExpr=varExpression();
		match(RPAREN);
		match(LCURLY);
		
		// TODO: check data type for var make sure is a CObject
		
		{
		_loop65:
		do {
			if ((_tokenSet_7.member(LA(1)))) {
				cstmt=constraintStmt();
				
				cstmts.add(cstmt);   
				
			}
			else {
				break _loop65;
			}
			
		} while (true);
		}
		match(RCURLY);
		
		expr = new ExprEnforceAnd(varExpr,cstmts);
		
		return expr;
	}
	
	public final Expr  print_stmt() throws RecognitionException, TokenStreamException {
		Expr expr;
		
		
		
		Expr printExpr=null;
		
		match(QUESTION);
		printExpr=expression();
		
		expr = new ExprPrint(printExpr);
		
		return expr;
	}
	
	public final Expr  methodCall() throws RecognitionException, TokenStreamException {
		Expr expr;
		
		Token  name = null;
		
		
		List args = new Vector();
		Expr argExpr=null;
		
		name = LT(1);
		match(IDENTIFIER);
		match(LPAREN);
		{
		switch ( LA(1)) {
		case STRING_LITERAL:
		case IDENTIFIER:
		case LPAREN:
		case NEW:
		case NUM_INT:
		case NUM_FLOAT:
		case NUM_LONG:
		case NUM_DOUBLE:
		{
			argExpr=expression();
			args.add(argExpr);
			{
			_loop103:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					argExpr=expression();
					args.add(argExpr);
				}
				else {
					break _loop103;
				}
				
			} while (true);
			}
			break;
		}
		case RPAREN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(RPAREN);
		
		List argDTs = new Vector();
		for (int i=0; i<args.size(); i++) {
		Expr arg = (Expr)args.get(i);
		argDTs.add(arg.getDataType());
		}
		JCLMethod m = context_.getSymbolTable().getBestMethodMatch(name.getText(),argDTs);
		if (m==null) {
		StringBuffer argTxt = new StringBuffer();
		argTxt.append("(");
		for (int i=0;i<argDTs.size();i++) {
		DataType dt = (DataType)argDTs.get(i);
		if (i>0)
		argTxt.append(",");
		argTxt.append(dt.getName());
		} 
		argTxt.append(")");
		throw new SemanticException("Couldn't find method definition match for "+
		name.getText() + argTxt);                                 
		}
		
		expr = new ExprMethodCall(name.getText(),args,m.getReturnDataType());
		
		return expr;
	}
	
	public final Expr  lval() throws RecognitionException, TokenStreamException {
		Expr expr;
		
		
		{
		if ((LA(1)==IDENTIFIER) && (LA(2)==ASSIGN)) {
			expr=varExpression();
		}
		else if ((LA(1)==IDENTIFIER) && (LA(2)==DOT)) {
			expr=objectFieldExpression();
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		return expr;
	}
	
	public final Expr  expression() throws RecognitionException, TokenStreamException {
		Expr expr;
		
		
		expr=logicalOrExpression();
		return expr;
	}
	
	public final Expr  varExpression() throws RecognitionException, TokenStreamException {
		Expr expr;
		
		Token  v = null;
		
		v = LT(1);
		match(IDENTIFIER);
		
		String vname = v.getText();
		Variable var = context_.getSymbolTable().getVariable(vname);
		if (var==null)
		throw new SemanticException("Undefined variable:"+vname);
		
		expr = new ExprVariableRef(vname,var.getDataType());
		
		return expr;
	}
	
	public final Expr  objectFieldExpression() throws RecognitionException, TokenStreamException {
		Expr expr;
		
		Token  member = null;
		
		
		Expr varExpr=null;
		DataType dt=null;
		List fieldNames = new Vector();
		
		varExpr=varExpression();
		dt = varExpr.getDataType();
		{
		int _cnt60=0;
		_loop60:
		do {
			if ((LA(1)==DOT)) {
				match(DOT);
				member = LT(1);
				match(IDENTIFIER);
				
				try {
				String memberName = member.getText();
				fieldNames.add(memberName);
				dt = dt.getMemberDataType(memberName);
				}
				catch (Exception e) {
				throw new SemanticException(e.getMessage());
				}
				
			}
			else {
				if ( _cnt60>=1 ) { break _loop60; } else {throw new NoViableAltException(LT(1), getFilename());}
			}
			
			_cnt60++;
		} while (true);
		}
		
		expr = new ExprObjectFieldRef(varExpr,fieldNames,dt);
		
		return expr;
	}
	
	public final Expr  logicalOrExpression() throws RecognitionException, TokenStreamException {
		Expr expr;
		
		Token  op = null;
		
		
		List children=null;
		Expr child=null;
		
		expr=logicalAndExpression();
		{
		_loop36:
		do {
			if ((LA(1)==LOR)) {
				op = LT(1);
				match(LOR);
				child=logicalAndExpression();
				
				if (children==null) {
				children = new Vector();
				children.add(expr);
				expr = new ExprLogical(op.getText(),children);
				}
				children.add(child);
				
			}
			else {
				break _loop36;
			}
			
		} while (true);
		}
		return expr;
	}
	
	public final Expr  logicalAndExpression() throws RecognitionException, TokenStreamException {
		Expr expr;
		
		Token  op = null;
		
		
		List children=null;
		Expr child=null;
		
		expr=equalityExpression();
		{
		_loop39:
		do {
			if ((LA(1)==LAND)) {
				op = LT(1);
				match(LAND);
				child=equalityExpression();
				
				if (children==null) {
				children = new Vector();
				children.add(expr);
				expr = new ExprLogical(op.getText(),children);
				}
				children.add(child);
				
			}
			else {
				break _loop39;
			}
			
		} while (true);
		}
		return expr;
	}
	
	public final Expr  equalityExpression() throws RecognitionException, TokenStreamException {
		Expr expr;
		
		Token  op1 = null;
		Token  op2 = null;
		
		
		Expr lhs=null,rhs=null;
		String operator=null;
		
		lhs=relationalExpression();
		{
		switch ( LA(1)) {
		case NOT_EQUAL:
		case EQUAL:
		{
			{
			switch ( LA(1)) {
			case NOT_EQUAL:
			{
				op1 = LT(1);
				match(NOT_EQUAL);
				operator=op1.getText();
				break;
			}
			case EQUAL:
			{
				op2 = LT(1);
				match(EQUAL);
				operator=op2.getText();
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			rhs=relationalExpression();
			break;
		}
		case EOF:
		case INCLUDE:
		case CLASS:
		case IDENTIFIER:
		case LCURLY:
		case RCURLY:
		case RPAREN:
		case COMMA:
		case LITERAL_if:
		case LITERAL_else:
		case LITERAL_while:
		case LOR:
		case LAND:
		case QUESTION:
		case CONSTRAIN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		
		if (rhs==null)
		expr = lhs;
		else {
		// TODO: check types
		expr = new ExprRelational(operator,lhs,rhs);
		}
		
		return expr;
	}
	
	public final Expr  relationalExpression() throws RecognitionException, TokenStreamException {
		Expr expr;
		
		Token  op1 = null;
		Token  op2 = null;
		Token  op3 = null;
		Token  op4 = null;
		
		
		Expr lhs=null,rhs=null;
		String operator=null;
		
		lhs=additiveExpression();
		{
		switch ( LA(1)) {
		case LT:
		case GT:
		case LE:
		case GE:
		{
			{
			switch ( LA(1)) {
			case LT:
			{
				op1 = LT(1);
				match(LT);
				operator=op1.getText();
				break;
			}
			case GT:
			{
				op2 = LT(1);
				match(GT);
				operator=op2.getText();
				break;
			}
			case LE:
			{
				op3 = LT(1);
				match(LE);
				operator=op3.getText();
				break;
			}
			case GE:
			{
				op4 = LT(1);
				match(GE);
				operator=op4.getText();
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			rhs=additiveExpression();
			break;
		}
		case EOF:
		case INCLUDE:
		case CLASS:
		case IDENTIFIER:
		case LCURLY:
		case RCURLY:
		case RPAREN:
		case COMMA:
		case LITERAL_if:
		case LITERAL_else:
		case LITERAL_while:
		case LOR:
		case LAND:
		case NOT_EQUAL:
		case EQUAL:
		case QUESTION:
		case CONSTRAIN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		
		if (rhs==null)
		expr = lhs;
		else {
		// TODO: check types
		expr = new ExprRelational(operator,lhs,rhs);
		}
		
		return expr;
	}
	
	public final Expr  additiveExpression() throws RecognitionException, TokenStreamException {
		Expr expr;
		
		Token  op1 = null;
		Token  op2 = null;
		
		
		Expr child=null;
		String operator=null;
		
		expr=multiplicativeExpression();
		{
		_loop49:
		do {
			if ((LA(1)==PLUS||LA(1)==MINUS)) {
				{
				switch ( LA(1)) {
				case PLUS:
				{
					op1 = LT(1);
					match(PLUS);
					operator=op1.getText();
					break;
				}
				case MINUS:
				{
					op2 = LT(1);
					match(MINUS);
					operator=op2.getText();
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				child=multiplicativeExpression();
				
				expr = new ExprArith(operator,expr,child);
				
			}
			else {
				break _loop49;
			}
			
		} while (true);
		}
		return expr;
	}
	
	public final Expr  multiplicativeExpression() throws RecognitionException, TokenStreamException {
		Expr expr;
		
		Token  op1 = null;
		Token  op2 = null;
		Token  op3 = null;
		
		
		Expr child=null;
		String operator=null;
		
		expr=primaryExpression();
		{
		_loop53:
		do {
			if (((LA(1) >= STAR && LA(1) <= MOD))) {
				{
				switch ( LA(1)) {
				case STAR:
				{
					op1 = LT(1);
					match(STAR);
					operator=op1.getText();
					break;
				}
				case DIV:
				{
					op2 = LT(1);
					match(DIV);
					operator=op2.getText();
					break;
				}
				case MOD:
				{
					op3 = LT(1);
					match(MOD);
					operator=op3.getText();
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				child=primaryExpression();
				
				expr = new ExprArith(operator,expr,child);
				
			}
			else {
				break _loop53;
			}
			
		} while (true);
		}
		return expr;
	}
	
	public final Expr  primaryExpression() throws RecognitionException, TokenStreamException {
		Expr expr;
		
		
		switch ( LA(1)) {
		case STRING_LITERAL:
		case IDENTIFIER:
		case NEW:
		case NUM_INT:
		case NUM_FLOAT:
		case NUM_LONG:
		case NUM_DOUBLE:
		{
			expr=simplePrimaryExpression();
			break;
		}
		case LPAREN:
		{
			match(LPAREN);
			expr=logicalOrExpression();
			match(RPAREN);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		return expr;
	}
	
	public final Expr  simplePrimaryExpression() throws RecognitionException, TokenStreamException {
		Expr expr;
		
		
		switch ( LA(1)) {
		case NEW:
		{
			expr=newExpression();
			break;
		}
		case STRING_LITERAL:
		case NUM_INT:
		case NUM_FLOAT:
		case NUM_LONG:
		case NUM_DOUBLE:
		{
			expr=constant();
			break;
		}
		default:
			if ((LA(1)==IDENTIFIER) && (LA(2)==DOT)) {
				expr=objectFieldExpression();
			}
			else if ((LA(1)==IDENTIFIER) && (LA(2)==LPAREN) && (_tokenSet_8.member(LA(3)))) {
				expr=methodCall();
			}
			else if ((LA(1)==IDENTIFIER) && (_tokenSet_9.member(LA(2))) && (_tokenSet_10.member(LA(3)))) {
				expr=varExpression();
			}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		return expr;
	}
	
	public final Expr  newExpression() throws RecognitionException, TokenStreamException {
		Expr expr;
		
		
		
		DataType dt=null;
		
		match(NEW);
		dt=class_data_type();
		
		expr = new ExprNewObject(dt);
		
		return expr;
	}
	
	public final Expr  constant() throws RecognitionException, TokenStreamException {
		Expr expr;
		
		Token  c1 = null;
		Token  c2 = null;
		Token  c3 = null;
		Token  c4 = null;
		Token  c5 = null;
		
		// TODO: reuse ExprPrimitiveConstants at least for common values like 0
		
		
		switch ( LA(1)) {
		case NUM_INT:
		{
			c1 = LT(1);
			match(NUM_INT);
			expr = new ExprPrimitiveConstant(context_.getSymbolTable().getDataType("int"),
			new Integer(c1.getText()));
			
			break;
		}
		case STRING_LITERAL:
		{
			c2 = LT(1);
			match(STRING_LITERAL);
			expr = new ExprPrimitiveConstant(context_.getSymbolTable().getDataType("string"),
			c2.getText().substring(1,c2.getText().length()-1));
			
			break;
		}
		case NUM_FLOAT:
		{
			c3 = LT(1);
			match(NUM_FLOAT);
			expr = new ExprPrimitiveConstant(context_.getSymbolTable().getDataType("float"),
			new Float(c3.getText()));
			
			break;
		}
		case NUM_LONG:
		{
			c4 = LT(1);
			match(NUM_LONG);
			expr = new ExprPrimitiveConstant(context_.getSymbolTable().getDataType("long"),
			new Long(c4.getText()));
			
			break;
		}
		case NUM_DOUBLE:
		{
			c5 = LT(1);
			match(NUM_DOUBLE);
			expr = new ExprPrimitiveConstant(context_.getSymbolTable().getDataType("double"),
			new Double(c5.getText()));
			
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		return expr;
	}
	
	public final CStmt  constraintStmt() throws RecognitionException, TokenStreamException {
		CStmt cstmt;
		
		Token  expl = null;
		
		{
		switch ( LA(1)) {
		case STRING_LITERAL:
		case IDENTIFIER:
		case LPAREN:
		case NEW:
		case NUM_INT:
		case NUM_FLOAT:
		case NUM_LONG:
		case NUM_DOUBLE:
		{
			cstmt=crelationalStmt();
			break;
		}
		case ENFORCE:
		{
			cstmt=cfunctionStmt();
			break;
		}
		case ENFORCE_IF:
		{
			cstmt=cenforceifStmt();
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		{
		switch ( LA(1)) {
		case COLON:
		{
			match(COLON);
			expl = LT(1);
			match(STRING_LITERAL);
			break;
		}
		case STRING_LITERAL:
		case IDENTIFIER:
		case RCURLY:
		case LPAREN:
		case NEW:
		case NUM_INT:
		case NUM_FLOAT:
		case NUM_LONG:
		case NUM_DOUBLE:
		case ENFORCE_IF:
		case ENFORCE:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		
		if (expl != null)
		cstmt.setViolationInfo(expl.getText());
		
		return cstmt;
	}
	
	public final CStmt  crelationalStmt() throws RecognitionException, TokenStreamException {
		CStmt cstmt;
		
		
		
		CExpr expr;
		
		expr=crelationalExpr();
		
		cstmt = new CStmtExpr(expr);
		
		return cstmt;
	}
	
	public final CStmt  cfunctionStmt() throws RecognitionException, TokenStreamException {
		CStmt cstmt;
		
		Token  name = null;
		
		
		List args = new Vector();
		CExpr argExpr=null;
		
		match(ENFORCE);
		name = LT(1);
		match(IDENTIFIER);
		match(LPAREN);
		{
		switch ( LA(1)) {
		case STRING_LITERAL:
		case IDENTIFIER:
		case LPAREN:
		case NEW:
		case NUM_INT:
		case NUM_FLOAT:
		case NUM_LONG:
		case NUM_DOUBLE:
		{
			argExpr=cadditiveExpr();
			args.add(argExpr);
			{
			_loop99:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					argExpr=cadditiveExpr();
					args.add(argExpr);
				}
				else {
					break _loop99;
				}
				
			} while (true);
			}
			break;
		}
		case RPAREN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(RPAREN);
		
		List argDTs = new Vector();
		for (int i=0; i<args.size(); i++) {
		CExpr arg = (CExpr)args.get(i);
		argDTs.add(arg.getDataType());
		}
		JCLConstraintFunction f = context_.getSymbolTable().getConstraintFunction(name.getText(),argDTs);
		if (f==null) {
		StringBuffer argTxt = new StringBuffer();
		argTxt.append("(");
		for (int i=0;i<argDTs.size();i++) {
		DataType dt = (DataType)argDTs.get(i);
		if (i>0)
		argTxt.append(",");
		argTxt.append(dt.getName());
		} 
		argTxt.append(")");
		throw new SemanticException("Couldn't find constraint function match for "+
		name.getText() + argTxt);                                 
		}
		
		cstmt = new CStmtFunction(f,args);
		
		return cstmt;
	}
	
	public final CStmt  cenforceifStmt() throws RecognitionException, TokenStreamException {
		CStmt cstmt;
		
		
		
		CStmt condStmt=null;
		List constraints = new Vector();    
		CStmt constraint=null;
		
		match(ENFORCE_IF);
		match(LPAREN);
		condStmt=cexprStmt();
		match(RPAREN);
		match(LCURLY);
		{
		_loop71:
		do {
			if ((_tokenSet_7.member(LA(1)))) {
				constraint=constraintStmt();
				constraints.add(constraint);
			}
			else {
				break _loop71;
			}
			
		} while (true);
		}
		match(RCURLY);
		
		cstmt = new CStmtEnforceIf(condStmt,constraints);
		
		return cstmt;
	}
	
	public final CStmt  cexprStmt() throws RecognitionException, TokenStreamException {
		CStmt cstmt;
		
		
		
		CExpr expr;
		
		expr=constrainedExpr();
		
		cstmt = new CStmtExpr(expr);
		
		return cstmt;
	}
	
	public final CExpr  constrainedExpr() throws RecognitionException, TokenStreamException {
		CExpr cexpr;
		
		
		{
		cexpr=clogicalOrExpr();
		}
		return cexpr;
	}
	
	public final CExpr  crelationalExpr() throws RecognitionException, TokenStreamException {
		CExpr cexpr;
		
		Token  op1 = null;
		Token  op2 = null;
		Token  op3 = null;
		Token  op4 = null;
		Token  op5 = null;
		Token  op6 = null;
		
		
		String operator=null;
		CExpr lhs=null,rhs=null;
		
		lhs=cadditiveExpr();
		{
		switch ( LA(1)) {
		case NOT_EQUAL:
		case EQUAL:
		case LT:
		case GT:
		case LE:
		case GE:
		{
			{
			switch ( LA(1)) {
			case NOT_EQUAL:
			{
				op1 = LT(1);
				match(NOT_EQUAL);
				operator=op1.getText();
				break;
			}
			case EQUAL:
			{
				op2 = LT(1);
				match(EQUAL);
				operator=op2.getText();
				break;
			}
			case LT:
			{
				op3 = LT(1);
				match(LT);
				operator=op3.getText();
				break;
			}
			case GT:
			{
				op4 = LT(1);
				match(GT);
				operator=op4.getText();
				break;
			}
			case LE:
			{
				op5 = LT(1);
				match(LE);
				operator=op5.getText();
				break;
			}
			case GE:
			{
				op6 = LT(1);
				match(GE);
				operator=op6.getText();
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			rhs=cadditiveExpr();
			break;
		}
		case STRING_LITERAL:
		case IDENTIFIER:
		case RCURLY:
		case LPAREN:
		case RPAREN:
		case LOR:
		case LAND:
		case NEW:
		case NUM_INT:
		case NUM_FLOAT:
		case NUM_LONG:
		case NUM_DOUBLE:
		case COLON:
		case ENFORCE_IF:
		case ENFORCE:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		
		if (rhs != null)
		cexpr = new CExprRelational(operator,lhs,rhs);
		else
		cexpr = lhs;
		
		return cexpr;
	}
	
	public final CExpr  clogicalOrExpr() throws RecognitionException, TokenStreamException {
		CExpr cexpr;
		
		Token  op = null;
		
		
		List children=null;
		CExpr child=null;
		
		cexpr=clogicalAndExpr();
		{
		_loop78:
		do {
			if ((LA(1)==LOR)) {
				op = LT(1);
				match(LOR);
				child=clogicalAndExpr();
				
				if (children==null) {
				children = new Vector();
				children.add(cexpr);
				cexpr = new CExprLogical(op.getText(),children);
				}
				children.add(child);
				
			}
			else {
				break _loop78;
			}
			
		} while (true);
		}
		return cexpr;
	}
	
	public final CExpr  clogicalAndExpr() throws RecognitionException, TokenStreamException {
		CExpr cexpr;
		
		Token  op = null;
		
		
		List children=null;
		CExpr child=null;
		
		cexpr=crelationalExpr();
		{
		_loop81:
		do {
			if ((LA(1)==LAND)) {
				op = LT(1);
				match(LAND);
				child=crelationalExpr();
				
				if (children==null) {
				children = new Vector();
				children.add(cexpr);
				cexpr = new CExprLogical(op.getText(),children);
				}
				children.add(child);
				
			}
			else {
				break _loop81;
			}
			
		} while (true);
		}
		return cexpr;
	}
	
	public final CExpr  cadditiveExpr() throws RecognitionException, TokenStreamException {
		CExpr cexpr;
		
		Token  op1 = null;
		Token  op2 = null;
		
		
		String operator=null;
		CExpr child = null;
		
		cexpr=cmultiplicativeExpr();
		{
		_loop88:
		do {
			if ((LA(1)==PLUS||LA(1)==MINUS)) {
				{
				switch ( LA(1)) {
				case PLUS:
				{
					op1 = LT(1);
					match(PLUS);
					operator=op1.getText();
					break;
				}
				case MINUS:
				{
					op2 = LT(1);
					match(MINUS);
					operator=op2.getText();
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				child=cmultiplicativeExpr();
				
				cexpr = new CExprArith(operator,cexpr,child);
				
			}
			else {
				break _loop88;
			}
			
		} while (true);
		}
		return cexpr;
	}
	
	public final CExpr  cmultiplicativeExpr() throws RecognitionException, TokenStreamException {
		CExpr cexpr;
		
		Token  op1 = null;
		Token  op2 = null;
		Token  op3 = null;
		
		
		String operator=null;
		CExpr child=null;
		
		cexpr=cprimaryExpr();
		{
		_loop92:
		do {
			if (((LA(1) >= STAR && LA(1) <= MOD))) {
				{
				switch ( LA(1)) {
				case STAR:
				{
					op1 = LT(1);
					match(STAR);
					operator=op1.getText();
					break;
				}
				case DIV:
				{
					op2 = LT(1);
					match(DIV);
					operator=op2.getText();
					break;
				}
				case MOD:
				{
					op3 = LT(1);
					match(MOD);
					operator=op3.getText();
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				child=cprimaryExpr();
				
				cexpr = new CExprArith(operator,cexpr,child);
				
			}
			else {
				break _loop92;
			}
			
		} while (true);
		}
		return cexpr;
	}
	
	public final CExpr  cprimaryExpr() throws RecognitionException, TokenStreamException {
		CExpr cexpr;
		
		
		{
		switch ( LA(1)) {
		case LPAREN:
		{
			match(LPAREN);
			cexpr=clogicalOrExpr();
			match(RPAREN);
			break;
		}
		case STRING_LITERAL:
		case IDENTIFIER:
		case NEW:
		case NUM_INT:
		case NUM_FLOAT:
		case NUM_LONG:
		case NUM_DOUBLE:
		{
			cexpr=csimplePrimaryExpr();
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		return cexpr;
	}
	
	public final CExpr  csimplePrimaryExpr() throws RecognitionException, TokenStreamException {
		CExpr cexpr;
		
		
		
		Expr expr=null;
		
		expr=simplePrimaryExpression();
		
		cexpr = new CExprWrapper(expr);
		
		return cexpr;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"\"include\"",
		"STRING_LITERAL",
		"\"class\"",
		"IDENTIFIER",
		"\"extends\"",
		"LCURLY",
		"RCURLY",
		"\"constrained\"",
		"LPAREN",
		"RPAREN",
		"COMMA",
		"ASSIGN",
		"\"if\"",
		"\"else\"",
		"\"while\"",
		"LOR",
		"LAND",
		"NOT_EQUAL",
		"EQUAL",
		"LT",
		"GT",
		"LE",
		"GE",
		"PLUS",
		"MINUS",
		"STAR",
		"DIV",
		"MOD",
		"\"new\"",
		"DOT",
		"NUM_INT",
		"NUM_FLOAT",
		"NUM_LONG",
		"NUM_DOUBLE",
		"QUESTION",
		"\"constrain\"",
		"COLON",
		"\"enforce_if\"",
		"\"enforce\"",
		"WS",
		"LBRACK",
		"RBRACK",
		"LNOT",
		"BNOT",
		"DIV_ASSIGN",
		"PLUS_ASSIGN",
		"INC",
		"MINUS_ASSIGN",
		"DEC",
		"STAR_ASSIGN",
		"MOD_ASSIGN",
		"SR",
		"SR_ASSIGN",
		"BSR",
		"BSR_ASSIGN",
		"SL",
		"SL_ASSIGN",
		"BXOR",
		"BXOR_ASSIGN",
		"BOR",
		"BOR_ASSIGN",
		"BAND",
		"BAND_ASSIGN",
		"SEMI",
		"EXPONENT",
		"FLOAT_SUFFIX",
		"HEX_DIGIT",
		"ESC",
		"SL_COMMENT",
		"ML_COMMENT"
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 824634049232L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 824634049152L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 1095217026720L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 1099511478002L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { 824634181330L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = { 1095217157874L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = { 1099511609330L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static final long[] mk_tokenSet_7() {
		long[] data = { 6859062775968L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	private static final long[] mk_tokenSet_8() {
		long[] data = { 261993017504L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());
	private static final long[] mk_tokenSet_9() {
		long[] data = { 8787503052530L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_9 = new BitSet(mk_tokenSet_9());
	private static final long[] mk_tokenSet_10() {
		long[] data = { 8796093019890L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_10 = new BitSet(mk_tokenSet_10());
	
	}
