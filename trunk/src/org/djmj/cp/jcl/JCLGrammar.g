header {
package org.djmj.cp.jcl;

import java.io.*;
import java.util.*;

}

class JCLParser extends Parser;
options {
    //buildAST = true;	// uses CommonAST by default
    k=3;
    defaultErrorHandler=false;
}
{
    public JCLParserContext context_;

    protected boolean isNumber(DataType dt)
    {
        if (!dt.isPrimitive())
            return false;
        
        return ((PrimitiveDataType)dt).isNumber();
    }
}

program : (stmt)*
;

stmt : 
{
    Expr expr=null;
}
( 
    // For some reason, ambiguity is claimed between methodDefinition and subStmt
    // TODO: why the warning?, still don't get it, with k=3 we can 
    // see IDENTIFIER IDENTIFIER LPAREN and decide whether it's a methodDefinition
    // unambigously. The parser does the right thing anyway
    includeFile
    | classDefinition
    | methodDefinition
    | expr=subStmt
        {
            try {
                expr.eval(context_);
            }
            catch (Exception e) {
                e.printStackTrace();
                throw new SemanticException(e.toString());
            }
        }
)
;

includeFile : 
    INCLUDE s1:STRING_LITERAL
{
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
;

classDefinition : 
{
    Map members=new TreeMap();
    ClassDataType superclass=null;
    List member=null;
}
CLASS n:IDENTIFIER (EXTENDS superclass=class_data_type)? LCURLY
    (member=class_member 
     { 
         // TODO: Check for duplicate member names in superclass
         if (members.get(member.get(0)) != null)
             throw new SemanticException("Member "+member.get(0)+" is already defined");

         // TODO: if dt.isConstrained() 
         // then This class must inherit from CObject, throw exception otherwise
         members.put(member.get(0),  // name
                     member.get(1)); // data type
     }
    )*
RCURLY 
{
    String name = n.getText();
    if (context_.getSymbolTable().getDataType(name)!=null)
        throw new SemanticException("Class "+name+" has already been defined");

    context_.getSymbolTable().addDataType(new ClassDataType(name,superclass,members));
    context_.getOut().println("Parsed class declaration : "+name); 
}
;

class_member returns [List memberInfo] : 
{
    boolean isConstrained=false;
    DataType dt=null;
    memberInfo = new Vector();
}
(CONSTRAINED {isConstrained=true;})? dt=data_type m:IDENTIFIER
{
    if (isConstrained) {
        // TODO: cache constrained types to save space and time
        dt = new ConstrainedDataType(dt);
    }

    memberInfo.add(m.getText());
    memberInfo.add(dt);
}
;

methodDefinition :
{
    DataType returnDT=null;
    List parameters=null;
    List parameterNames=null;
    List parameterDTs=null;
    Expr bodyExpr=null;
}
    returnDT=data_type name:IDENTIFIER LPAREN! parameters=parameterList RPAREN!
{
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
}
    bodyExpr=stmtBlock
{
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
;

parameterList returns [List pinfo] :
{
    pinfo = new Vector();
    List names = new Vector();
    List dts = new Vector();
    pinfo.add(names);
    pinfo.add(dts);
    DataType dt=null;
}
    ((dt=data_type name:IDENTIFIER) 
     { names.add(name.getText());dts.add(dt); }
         (COMMA (dt=data_type name1:IDENTIFIER) 
                { names.add(name1.getText());dts.add(dt); }
         )*
    )?    
;

subStmt returns [Expr expr] :
    expr=var_declaration    |
    expr=assignment         |
    expr=ifStmt             |
    expr=whileStmt          |
    expr=stmtBlock          |
    expr=constraintBlock    |
    expr=print_stmt         |
    expr=methodCall 
;


// TODO: deal with var scope created by the block
stmtBlock returns [Expr expr] :
{
    Expr stmtExpr=null;
    List subStmts = new Vector();
}
    LCURLY
        (stmtExpr=subStmt { subStmts.add(stmtExpr); })*
    RCURLY
{
    expr = new ExprStmtBlock(subStmts);
}
;

data_type returns [DataType dt] : 
    t:IDENTIFIER
{
    dt = context_.getSymbolTable().getDataType(t.getText());
    if (dt == null)
        throw new SemanticException("Unrecognized type:"+t.getText());
}
;

class_data_type returns [ClassDataType cdt] :
{
    DataType dt=null;
}
    dt=data_type
{
    if (dt.isPrimitive())
        throw new SemanticException("Class name expected, not primitive type");

    cdt = (ClassDataType)dt;
}
;

var_declaration returns [Expr expr] : 
{   
    DataType dt=null; 
}    
dt=data_type id:IDENTIFIER 
{
    String vname = id.getText();
    Variable v = context_.getSymbolTable().getVariable(vname);
    if (v != null)
        throw new SemanticException("Variable "+vname+" has already been defined");

    expr = new ExprVariableDecl(vname,dt);    
    context_.getOut().println("Parsed var declaration : "+vname);
}
;

assignment returns [Expr expr] : 
{    
    Expr lhs=null,rhs=null;
}
lhs=lval ASSIGN rhs=expression 
{
    if (!lhs.getDataType().isAssignableFrom(rhs.getDataType()))
        throw new SemanticException(
            "Can't assign "+rhs.getDataType().getName()+
            " to "+lhs.getDataType().getName()); 
                      
    expr = new ExprAssignment(lhs,rhs);
    //context_.getOut().println("Parsed assignment"); 
}
;

lval returns [Expr expr] :
(
    expr=varExpression
    | expr=objectFieldExpression
)
;

// If-else statement
ifStmt returns [Expr expr] : 
{
    Expr condExpr=null,ifExpr=null,elseExpr=null; 
}
    "if"^ LPAREN! condExpr=expression RPAREN! ifExpr=subStmt
		(
			// CONFLICT: the old "dangling-else" problem...
			//           ANTLR generates proper code matching
			//			 as soon as possible.  Hush warning.
			options {
				warnWhenFollowAmbig = false;
			}
		:
			"else"! elseExpr=subStmt
		)?
{
    if (!isNumber(condExpr.getDataType())) 
        throw new SemanticException("Condition on if statement must evaluate to a number");
    
    expr = new ExprIf(condExpr,ifExpr,elseExpr);
}
;

whileStmt returns [Expr expr] :
{
    Expr condExpr=null,bodyExpr=null,elseExpr=null;    
}
    "while"^ LPAREN! condExpr=expression RPAREN! bodyExpr=subStmt
{
    if (!isNumber(condExpr.getDataType())) 
        throw new SemanticException("Condition on while statement must evaluate to a number");
    
    expr = new ExprWhile(condExpr,bodyExpr);
}
;

// expressions
// Note that most of these expressions follow the pattern
//   thisLevelExpression :
//       nextHigherPrecedenceExpression
//           (OPERATOR nextHigherPrecedenceExpression)*
// which is a standard recursive definition for a parsing an expression.
//    lowest  (7)  ||
//            (6)  &&
//            (5)  == !=
//            (4)  < <= > >=
//            (3)  +(binary) -(binary)
//            (2)  * / %
//            (1)  !  (method call) new  (explicit parenthesis)
//
// Note that the above precedence levels map to the rules below...
// Once you have a precedence chart, writing the appropriate rules as below
//   is usually very straightfoward


expression returns [Expr expr] : 
    expr=logicalOrExpression
;

// logical or (||)  
logicalOrExpression returns [Expr expr] :	
{
    List children=null;
    Expr child=null;
}
    expr=logicalAndExpression 
        (op:LOR^ child=logicalAndExpression 
         {
             if (children==null) {
                 children = new Vector();
                 children.add(expr);
                 expr = new ExprLogical(op.getText(),children);
             }
             children.add(child);
         }
        )*
;


// logical and (&&)  
logicalAndExpression returns [Expr expr] :
{
    List children=null;
    Expr child=null;
}
    expr=equalityExpression 
        (op:LAND^ child=equalityExpression
         {
             if (children==null) {
                 children = new Vector();
                 children.add(expr);
                 expr = new ExprLogical(op.getText(),children);
             }
             children.add(child);
         }
        )*
;


// equality/inequality (==/!=) 
equalityExpression returns [Expr expr] :	
{
    Expr lhs=null,rhs=null;
    String operator=null;
}
    lhs=relationalExpression 
        ((op1:NOT_EQUAL^ { operator=op1.getText(); } | 
          op2:EQUAL^     { operator=op2.getText(); }) 
          rhs=relationalExpression)?
{
    if (rhs==null)
        expr = lhs;
    else {
        // TODO: check types
        expr = new ExprRelational(operator,lhs,rhs);
    }
}
;


// boolean relational expressions 
relationalExpression returns [Expr expr] :	
{
    Expr lhs=null,rhs=null;
    String operator=null;
}
    lhs=additiveExpression
		    ((op1:LT^ { operator=op1.getText(); } |
                      op2:GT^ { operator=op2.getText(); } |
		      op3:LE^ { operator=op3.getText(); } |
		      op4:GE^ { operator=op4.getText(); } 
		     )
		     rhs=additiveExpression
		    )?
{
    if (rhs==null)
        expr = lhs;
    else {
        // TODO: check types
        expr = new ExprRelational(operator,lhs,rhs);
    }
}
;


// binary addition/subtraction 
additiveExpression returns [Expr expr] :	
{
    Expr child=null;
    String operator=null;
}
    expr=multiplicativeExpression 
        ((op1:PLUS^ { operator=op1.getText(); } | 
          op2:MINUS^{ operator=op2.getText(); }
         ) 
         child=multiplicativeExpression
         {
             expr = new ExprArith(operator,expr,child);
         }
        )*
;


// multiplication/division/modulo (level 2)
multiplicativeExpression returns [Expr expr] :
{
    Expr child=null;
    String operator=null;
}
    expr=primaryExpression 
        ((op1:STAR^ { operator=op1.getText(); } | 
          op2:DIV^  { operator=op2.getText(); } | 
          op3:MOD^  { operator=op3.getText(); }
         ) 
         child=primaryExpression
         {
             expr = new ExprArith(operator,expr,child);
         }
        )*
;

// the basic element of an expression
primaryExpression returns [Expr expr] :	
    expr=simplePrimaryExpression
    | LPAREN! expr=logicalOrExpression RPAREN!
;

simplePrimaryExpression returns [Expr expr] :
    expr=newExpression
    | expr=objectFieldExpression
    | expr=methodCall
    | expr=varExpression
    | expr=constant
;
  
newExpression returns [Expr expr] : 
{
    DataType dt=null;
}
NEW dt=class_data_type
{
    expr = new ExprNewObject(dt);
}
;

varExpression returns [Expr expr] : v:IDENTIFIER
{
    String vname = v.getText();
    Variable var = context_.getSymbolTable().getVariable(vname);
    if (var==null)
        throw new SemanticException("Undefined variable:"+vname);

    expr = new ExprVariableRef(vname,var.getDataType());
}
;

objectFieldExpression returns [Expr expr] : 
{
    Expr varExpr=null;
    DataType dt=null;
    List fieldNames = new Vector();
}
varExpr=varExpression { dt = varExpr.getDataType(); }
(DOT^ member:IDENTIFIER
{
    try {
        String memberName = member.getText();
        fieldNames.add(memberName);
        dt = dt.getMemberDataType(memberName);
    }
    catch (Exception e) {
        throw new SemanticException(e.getMessage());
    }
}
)+
{
    expr = new ExprObjectFieldRef(varExpr,fieldNames,dt);
}
;

constant returns [Expr expr]
{
    // TODO: reuse ExprPrimitiveConstants at least for common values like 0
}
	:	c1:NUM_INT         
                { expr = new ExprPrimitiveConstant(context_.getSymbolTable().getDataType("int"),
                                               new Integer(c1.getText()));
                }
	|	c2:STRING_LITERAL
                { expr = new ExprPrimitiveConstant(context_.getSymbolTable().getDataType("string"),
                                               c2.getText().substring(1,c2.getText().length()-1));
                }
	|	c3:NUM_FLOAT
                { expr = new ExprPrimitiveConstant(context_.getSymbolTable().getDataType("float"),
                                               new Float(c3.getText()));
                }
	|	c4:NUM_LONG
                { expr = new ExprPrimitiveConstant(context_.getSymbolTable().getDataType("long"),
                                               new Long(c4.getText()));
                }
	|	c5:NUM_DOUBLE
                { expr = new ExprPrimitiveConstant(context_.getSymbolTable().getDataType("double"),
                                               new Double(c5.getText()));
                }
;


print_stmt returns [Expr expr] : 
{
    Expr printExpr=null;
}
QUESTION printExpr=expression
{
    expr = new ExprPrint(printExpr);
}
;


constraintBlock returns [Expr expr] : 
{
    Expr varExpr=null;
    List cstmts = new Vector();
    CStmt cstmt = null;
}
CONSTRAIN LPAREN varExpr=varExpression RPAREN LCURLY
{
    // TODO: check data type for var make sure is a CObject
}
(cstmt=constraintStmt
{
    cstmts.add(cstmt);   
}
)*
RCURLY
{
    expr = new ExprEnforceAnd(varExpr,cstmts);
}
;

constraintStmt returns [CStmt cstmt] : 
    ( cstmt=crelationalStmt
      | cstmt=cfunctionStmt
      | cstmt=cenforceifStmt )
    (COLON expl:STRING_LITERAL)?
{
    if (expl != null)
        cstmt.setViolationInfo(expl.getText());
}
;

cenforceifStmt returns [CStmt cstmt] :
{
    CStmt condStmt=null;
    List constraints = new Vector();    
    CStmt constraint=null;
}
    ENFORCE_IF  LPAREN condStmt=cexprStmt RPAREN LCURLY
    (
        constraint = constraintStmt
        { constraints.add(constraint); }
    )*
    RCURLY
{
    cstmt = new CStmtEnforceIf(condStmt,constraints);
}
;

cexprStmt returns [CStmt cstmt] :
{ 
    CExpr expr;
}
    expr=constrainedExpr 
{
    cstmt = new CStmtExpr(expr);
}
; 

crelationalStmt returns [CStmt cstmt] :
{ 
    CExpr expr;
}
    expr=crelationalExpr 
{
    cstmt = new CStmtExpr(expr);
}
; 

constrainedExpr returns [CExpr cexpr] :
    ( cexpr = clogicalOrExpr
    )
;

// logical or (||)  
clogicalOrExpr returns [CExpr cexpr] :	
{
    List children=null;
    CExpr child=null;
}
    cexpr=clogicalAndExpr
        (op:LOR^ child=clogicalAndExpr
         {
             if (children==null) {
                 children = new Vector();
                 children.add(cexpr);
                 cexpr = new CExprLogical(op.getText(),children);
             }
             children.add(child);
         }
        )*
;

// logical and (&&)  
clogicalAndExpr returns [CExpr cexpr] :
{
    List children=null;
    CExpr child=null;
}
    cexpr=crelationalExpr
        (op:LAND^ child=crelationalExpr
         {
             if (children==null) {
                 children = new Vector();
                 children.add(cexpr);
                 cexpr = new CExprLogical(op.getText(),children);
             }
             children.add(child);
         }
        )*
;

crelationalExpr returns [CExpr cexpr] :
{ 
    String operator=null;
    CExpr lhs=null,rhs=null;
}
    lhs=cadditiveExpr 
      (
        (
          op1:NOT_EQUAL^ { operator=op1.getText(); } | 
          op2:EQUAL^     { operator=op2.getText(); } | 
          op3:LT^        { operator=op3.getText(); } |
          op4:GT^        { operator=op4.getText(); } |
          op5:LE^        { operator=op5.getText(); } |
          op6:GE^        { operator=op6.getText(); } 
         )
         rhs=cadditiveExpr
      )?
{
    if (rhs != null)
        cexpr = new CExprRelational(operator,lhs,rhs);
    else
        cexpr = lhs;
}
; 

cadditiveExpr returns [CExpr cexpr] :
{
    String operator=null;
    CExpr child = null;
}
    cexpr=cmultiplicativeExpr 
        ((op1:PLUS^ { operator=op1.getText(); } | 
          op2:MINUS^{ operator=op2.getText(); }
         ) 
         child=cmultiplicativeExpr
         {
             cexpr = new CExprArith(operator,cexpr,child);
         }
        )*
;

cmultiplicativeExpr returns [CExpr cexpr] :
{
    String operator=null;
    CExpr child=null;
}
    cexpr=cprimaryExpr 
        ((op1:STAR^ { operator=op1.getText(); } | 
          op2:DIV^  { operator=op2.getText(); } | 
          op3:MOD^  { operator=op3.getText(); }
         ) 
         child=cprimaryExpr
         {
             cexpr = new CExprArith(operator,cexpr,child);
         }
        )*
;

cprimaryExpr returns [CExpr cexpr] :
  (
    LPAREN! cexpr=clogicalOrExpr RPAREN! |
    cexpr=csimplePrimaryExpr 
  )
;

csimplePrimaryExpr returns [CExpr cexpr] :
{
    Expr expr=null;
}
    expr=simplePrimaryExpression
    {
        cexpr = new CExprWrapper(expr);
    }
;

cfunctionStmt returns [CStmt cstmt] :
{
    List args = new Vector();
    CExpr argExpr=null;
}
   ENFORCE
   name:IDENTIFIER LPAREN 
    (argExpr=cadditiveExpr { args.add(argExpr); }
     (COMMA! argExpr=cadditiveExpr { args.add(argExpr); })* )? 
   RPAREN
{
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
}
;


methodCall returns [Expr expr] : 
{
    List args = new Vector();
    Expr argExpr=null;
}
   name:IDENTIFIER LPAREN 
    (argExpr=expression { args.add(argExpr); }
     (COMMA! argExpr=expression { args.add(argExpr); })* )? 
   RPAREN
{
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
}
;


class JCLLexer extends Lexer;
options { 
    k=5;
    //caseSensitive=false; 
    testLiterals=false;   // don't automatically test for literals
}
tokens {
    CLASS   = "class";
    CONSTRAIN = "constrain";
    CONSTRAINED = "constrained";   
    ENFORCE = "enforce";   
    ENFORCE_IF = "enforce_if";   
    EXTENDS = "extends";
    INCLUDE = "include";
    NEW     = "new";
}

IDENTIFIER 
	options {testLiterals=true;}
	:	('a'..'z'|'A'..'Z'|'_'|'$') ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'$')*
;

// Whitespace -- ignored
WS	:	(	' '
		|	'\t'
		|	'\f'
			// handle newlines
		|	(	options {generateAmbigWarnings=false;}
			:	"\r\n"  // Evil DOS
			|	'\r'    // Macintosh
			|	'\n'    // Unix (the right way)
			)
			{ newline(); }
		)+
		{ _ttype = Token.SKIP; }
;


// OPERATORS
QUESTION		:	'?'		;
LPAREN			:	'('		;
RPAREN			:	')'		;
LBRACK			:	'['		;
RBRACK			:	']'		;
LCURLY			:	'{'		;
RCURLY			:	'}'		;
COLON			:	':'		;
COMMA			:	','		;
//DOT			:	'.'		;
ASSIGN			:	'='		;
EQUAL			:	"=="	;
LNOT			:	'!'		;
BNOT			:	'~'		;
NOT_EQUAL		:	"!="	;
DIV			:	'/'		;
DIV_ASSIGN		:	"/="	;
PLUS			:	'+'		;
PLUS_ASSIGN		:	"+="	;
INC			:	"++"	;
MINUS			:	'-'		;
MINUS_ASSIGN	:	"-="	;
DEC			:	"--"	;
STAR			:	'*'		;
STAR_ASSIGN		:	"*="	;
MOD			:	'%'		;
MOD_ASSIGN		:	"%="	;
SR			:	">>"	;
SR_ASSIGN		:	">>="	;
BSR			:	">>>"	;
BSR_ASSIGN		:	">>>="	;
GE			:	">="	;
GT			:	">"		;
SL			:	"<<"	;
SL_ASSIGN		:	"<<="	;
LE			:	"<="	;
LT			:	'<'		;
BXOR			:	'^'		;
BXOR_ASSIGN		:	"^="	;
BOR			:	'|'		;
BOR_ASSIGN		:	"|="	;
LOR			:	"||"	;
BAND			:	'&'		;
BAND_ASSIGN		:	"&="	;
LAND			:	"&&"	;
SEMI			:	';'		;


//DOT: '.';

// a numeric literal
NUM_INT
	{boolean isDecimal=false; Token t=null;}
    :   '.' {_ttype = DOT;}
            (	('0'..'9')+ (EXPONENT)? (f1:FLOAT_SUFFIX {t=f1;})?
                {
				if (t != null && t.getText().toUpperCase().indexOf('F')>=0) {
                	_ttype = NUM_FLOAT;
				}
				else {
                	_ttype = NUM_DOUBLE; // assume double
				}
				}
            )?

	|	(	'0' {isDecimal = true;} // special case for just '0'
			(	('x'|'X')
				(											// hex
					// the 'e'|'E' and float suffix stuff look
					// like hex digits, hence the (...)+ doesn't
					// know when to stop: ambig.  ANTLR resolves
					// it correctly by matching immediately.  It
					// is therefor ok to hush warning.
					options {
						warnWhenFollowAmbig=false;
					}
				:	HEX_DIGIT
				)+
			|	('0'..'7')+									// octal
			)?
		|	('1'..'9') ('0'..'9')*  {isDecimal=true;}		// non-zero decimal
		)
		(	('l'|'L') { _ttype = NUM_LONG; }

		// only check to see if it's a float if looks like decimal so far
		|	{isDecimal}?
            (   '.' ('0'..'9')* (EXPONENT)? (f2:FLOAT_SUFFIX {t=f2;})?
            |   EXPONENT (f3:FLOAT_SUFFIX {t=f3;})?
            |   f4:FLOAT_SUFFIX {t=f4;}
            )
            {
			if (t != null && t.getText().toUpperCase() .indexOf('F') >= 0) {
                _ttype = NUM_FLOAT;
			}
            else {
	           	_ttype = NUM_DOUBLE; // assume double
			}
			}
        )?
	;


// a couple protected methods to assist in matching floating point numbers
protected
EXPONENT
	:	('e'|'E') ('+'|'-')? ('0'..'9')+
	;


protected
FLOAT_SUFFIX
	:	'f'|'F'|'d'|'D'
	;


// hexadecimal digit (again, note it's protected!)
protected
HEX_DIGIT
	:	('0'..'9'|'A'..'F'|'a'..'f')
	;

// string literals
STRING_LITERAL
	:	'"' (ESC|~('"'|'\\')|'#')* '"'
	;


// escape sequence -- note that this is protected; it can only be called
//   from another lexer rule -- it will not ever directly return a token to
//   the parser
// There are various ambiguities hushed in this rule.  The optional
// '0'...'9' digit matches should be matched here rather than letting
// them go back to STRING_LITERAL to be matched.  ANTLR does the
// right thing by matching immediately; hence, it's ok to shut off
// the FOLLOW ambig warnings.
protected
ESC
	:	'\\'
		(	'n'
		|	'r'
		|	't'
		|	'b'
		|	'f'
		|	'"'
		|	'\''
		|	'\\'
		|	('u')+ HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
		|	'0'..'3'
			(
				options {
					warnWhenFollowAmbig = false;
				}
			:	'0'..'7'
				(
					options {
						warnWhenFollowAmbig = false;
					}
				:	'0'..'7'
				)?
			)?
		|	'4'..'7'
			(
				options {
					warnWhenFollowAmbig = false;
				}
			:	'0'..'7'
			)?
		)              
	;

// Single-line comments
SL_COMMENT
	:	"//"
		(~('\n'|'\r'))* ('\n'|'\r'('\n')?)
		{$setType(Token.SKIP); newline();}
	;

// multiple-line comments
ML_COMMENT
	:	"/*"
		(	/*	'\r' '\n' can be matched in one alternative or by matching
				'\r' in one iteration and '\n' in another.  I am trying to
				handle any flavor of newline that comes in, but the language
				that allows both "\r\n" and "\r" and "\n" to all be valid
				newline is ambiguous.  Consequently, the resulting grammar
				must be ambiguous.  I'm shutting this warning off.
			 */
			options {
				generateAmbigWarnings=false;
			}
		:
			{ LA(2)!='/' }? '*'
		|	'\r' '\n'		{newline();}
		|	'\r'			{newline();}
		|	'\n'			{newline();}
		|	~('*'|'\n'|'\r')
		)*
		"*/"
		{$setType(Token.SKIP);}
	;

