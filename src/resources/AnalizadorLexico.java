package resources;
import java.util.*; //hash, list

public class AnalizadorLexico {
	public int pos = 0;
	public int nroLinea = 1; //se comienza a leer el archivo desde la linea 1 
	public String buffer;
	public int punteroTablaSimbolos = 0; //este se debe incrementar por cada entrada hecha en la tabla de simbolos
	static final int FINAL = 100;
	static final int ERROR = -1;
	static final AccionSemantica1 AS1 = new AccionSemantica1();
	static final AccionSemantica2 AS2 = new AccionSemantica2();
	static final AccionSemantica3 AS3 = new AccionSemantica3();
	static final AccionSemantica4 AS4 = new AccionSemantica4();
	static final AccionSemantica5 AS5 = new AccionSemantica5();
	static final AccionSemantica6 AS6 = new AccionSemantica6();
	static final AccionSemantica7 AS7 = new AccionSemantica7();
	static final AccionSemantica8 AS8 = new AccionSemantica8();
	static final AccionSemantica9 AS9 = new AccionSemantica9();
	Set<String> palabrasReservadas;
	String fuente;
	private List<String> erroresLexicos = new ArrayList<String>();
	Hashtable<String, Atributos> TS;


	//no descartar definir una interfaz llamada Symbol
	//List Object va a estar conformada por... <tipoToken, Lexema> <String, valor concreto (Character, Integer)>
	public Hashtable<String,Integer> mapeoTipoTokens; //por ejemplo, ID 50, CTE 60. IF 20. Son distintos a los que hay en equivalencia. Repensar




	static final short[][] matrizTransicionEstados = {
		/* 0*/	{ERROR,     10,      3,     10,     10,     10,      1,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,      6,      7,      8,      9,     12,     11,      0,      0,      0},
		/* 1*/	{    2,      2,      2,      2,      2,      2,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR},
		/* 2*/	{    2,      2,      2,      2,      2,      2,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL},
		/* 3*/	{ERROR,  ERROR,      3,  ERROR,  ERROR,  ERROR,      4,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR},
		/* 4*/	{ERROR,  ERROR,  ERROR,  FINAL,  ERROR,      5,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR},
		/* 5*/	{ERROR,  ERROR,  ERROR,  ERROR,  FINAL,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR},
		/* 6*/	{ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  FINAL,  ERROR,  ERROR,  FINAL,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR},
		/* 7*/	{FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL},
		/* 8*/	{FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL},
		/* 9*/	{ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  FINAL,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR,  ERROR},
		/*10*/	{FINAL,     10,  FINAL,     10,     10,     10,     10,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL,  FINAL},
		/*11*/	{   11,     11,     11,     11,     11,     11,     11,     11,     11,     11,     11,     11,     11,     11,     11,     11,     11,     11,     11,     11,     11,     11,     11,      0,     11,     11,     11},
		/*12*/	{   12,     12,     12,     12,     12,     12,     12,     12,     12,     12,     12,     12,     12,     12,     12,     12,     12,     12,     12,     12,     12,     12,  FINAL,     12,     12,     12,  ERROR}
		/*        letra   minus digito       i       l       u       _       +	     -	     *	     /	     {	     }	     (	     )	     ;	     ,	     =	     :	      <	     >	     !	     '	     #	 blanco	    tab	    \n
		 */	};

	static final AccionSemantica[][] matrizAccionesSemanticas = {
			/* 0*/	{ null,    AS3,    AS3,    AS3,    AS3,    AS3,   null,    AS7,    AS7,    AS7,    AS7,    AS7,    AS7,    AS7,    AS7,    AS7,    AS7,    AS7,    AS3,    AS3,    AS3,    AS3,   null,   null,   null,   null,    AS9},
			/* 1*/	{  AS3,    AS3,    AS3,    AS3,    AS3,    AS3,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null},
			/* 2*/	{  AS3,    AS3,    AS3,    AS3,    AS3,    AS3,    AS1,    AS1,    AS1,    AS1,    AS1,    AS1,    AS1,    AS1,    AS1,    AS1,    AS1,    AS1,    AS1,    AS1,    AS1,    AS1,    AS1,    AS1,    AS1,    AS1,    AS1},
			/* 3*/	{ null,   null,    AS3,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null},
			/* 4*/	{ null,   null,   null,    AS4,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null},
			/* 5*/	{ null,   null,   null,   null,    AS5,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null},
			/* 6*/	{ null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,    AS7,   null,   null,   null,   null,   null,   null,   null,   null,   null},
			/* 7*/	{  AS8,    AS8,    AS8,    AS8,    AS8,    AS8,    AS8,    AS8,    AS8,    AS8,    AS8,    AS8,    AS8,    AS8,    AS8,    AS8,    AS8,    AS7,    AS8,    AS8,    AS8,    AS8,    AS8,    AS8,    AS8,    AS8,    AS8},
			/* 8*/	{  AS8,    AS8,    AS8,    AS8,    AS8,    AS8,    AS8,    AS8,    AS8,    AS8,    AS8,    AS8,    AS8,    AS8,    AS8,    AS8,    AS8,    AS7,    AS8,    AS8,    AS8,    AS8,    AS8,    AS8,    AS8,    AS8,    AS8},
			/* 9*/	{ null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,    AS7,   null,   null,   null,   null,   null,   null,   null,   null,   null},
			/*10*/	{  AS6,    AS3,    AS6,    AS3,    AS3,    AS3,    AS3,    AS6,    AS6,    AS6,    AS6,    AS6,    AS6,    AS6,    AS6,    AS6,    AS6,    AS6,    AS6,    AS6,    AS6,    AS6,    AS6,    AS6,    AS6,    AS6,    AS6},
			/*11*/	{ null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,   null,    AS9},
			/*12*/	{  AS3,    AS3,    AS3,    AS3,    AS3,    AS3,    AS3,    AS3,    AS3,    AS3,    AS3,    AS3,    AS3,    AS3,    AS3,    AS3,    AS3,    AS3,    AS3,    AS3,    AS3,    AS3,    AS2,    AS3,    AS3,    AS3,   null}
			/*        letra   minus digito       i       l       u       _       +	     -	     *	     /	     {	     }	     (	     )	     ;	     ,	     =	     :	      <	     >	     !	     '	     #	 blanco	    tab	    \n
			 */
	};



	public AnalizadorLexico(String programa, Hashtable<String, Atributos> tablaSimbolos){
		fuente = programa;
		TS = tablaSimbolos;

		pos = 0;
		mapeoTipoTokens= new Hashtable<>();
		palabrasReservadas = new HashSet<>();

		palabrasReservadas.add("if");
		palabrasReservadas.add("else");
		palabrasReservadas.add("end_if");
		palabrasReservadas.add("print");
		palabrasReservadas.add("integer");
		palabrasReservadas.add("uslinteger");
		palabrasReservadas.add("while");
		palabrasReservadas.add("void");
		palabrasReservadas.add("fun");
		palabrasReservadas.add("return");

		mapeoTipoTokens.put("if",(int) Token.IF);
		mapeoTipoTokens.put("else",(int) Token.ELSE);
		mapeoTipoTokens.put("end_if",(int) Token.END_IF);
		mapeoTipoTokens.put("print",(int) Token.PRINT);
		mapeoTipoTokens.put("integer",(int) Token.INTEGER);
		mapeoTipoTokens.put("uslinteger",(int) Token.USLINTEGER);
		mapeoTipoTokens.put("while",(int) Token.WHILE);
		mapeoTipoTokens.put("void",(int) Token.VOID);
		mapeoTipoTokens.put("fun",(int) Token.FUN);
		mapeoTipoTokens.put("return",(int) Token.RETURN);

		mapeoTipoTokens.put(":=",(int) Token.ASIGN);
		mapeoTipoTokens.put("!=",(int) Token.COMP_DISTINTO);
		mapeoTipoTokens.put(">=",(int) Token.COMP_MAYOR_IGUAL);
		mapeoTipoTokens.put("<=",(int) Token.COMP_MENOR_IGUAL);

	}



	public Token getToken(){

		char c;
		buffer = ""; //se resetea el buffer cada vez que se va a leer un token
		Token token=null;
		int estado = 0;
		int estadoAnterior = -1; //para errores
		while (estado != ERROR){
				if (pos >= fuente.length()) {
					return new Token(0, "FIN_PROGRAMA", this.nroLinea);	//fin del programa
				}
				/*poniendo este chequeo aca evita que salten excepciones y permite
										 * que devuelva el token de error, indicando que el token que aparecio
										 * no es valido
				*/

			c = fuente.charAt(pos++); // se lee el proximo caracter y se aumenta pos

			int columna = equivalencia(c); //se obtiene la columna de la matriz a partir del caracter leido
			if(matrizAccionesSemanticas[estado][columna]!=null)
				token = matrizAccionesSemanticas[estado][columna].ejecutar(this, c); //se ejecuta la AS

			estadoAnterior = estado;
			estado = matrizTransicionEstados[estado][columna]; //y se pasa al proximo estado


			if (estado == FINAL)
				return (token==null)?(new Token(Token.YYERRCODE, "ERROR", this.nroLinea)):token;
		}


		// si llega a este punto, se llegó a una situación de error por un caracter
		if (estadoAnterior != 0) // un error en el estado 0 es un caso especial, se tiene que saltear el caracter leido
			pos--; //se vuelve atras para releer el caracter que dio originó el error
		this.agregarError(estadoAnterior);

		return new Token(Token.YYERRCODE, "ERROR", this.nroLinea);
	}

	private int equivalencia(char c){
		if (c == 'i')
			return 3;
		if (c == 'l')
			return 4;
		if (c == 'u')
			return 5;
		if ((int)c >= 97 && (int)c <= 122) //letras minusculas
			return 1;
		if ((int)c >= 65 && (int)c <= 90) //otras letras
			return 0;
		if ((int)c >= 48 && (int)c <= 57) //digitos
			return 2;
		if (c == '_')
			return 6;
		if (c == '+')
			return 7;
		if (c == '-')
			return 8;
		if (c == '*')
			return 9;
		if (c == '/')
			return 10;
		if (c == '{')
			return 11;
		if (c == '}')
			return 12;
		if (c == '(')
			return 13;
		if (c == ')')
			return 14;
		if (c == ';')
			return 15;
		if (c == ',')
			return 16;
		if (c == '=')
			return 17;
		if (c == ':')
			return 18;
		if (c == '<')
			return 19;
		if (c == '>')
			return 20;
		if (c == '!')
			return 21;
		if (c == '\'')
			return 22;
		if (c == '#')
			return 23;
		if ((int)c == 32) //espacio en blanco
			return 24;
		if ((int)c == 9)  //tab
			return 25;
		if ((int)c == 10) //salto de linea,\n. Si el programa tiene \r\n, se debe reemplazar con String.replaceAll
			return 26;

		return ERROR;
	}

	public boolean existePalabraReservada(String palabra){
		return palabrasReservadas.contains(palabra);
	}

	public void altaEnTablaSimbolos(String clave, Atributos atts) {
		//metodo pensado para dar de alta en la tabla de simbolos. La cantidad de atributos es variable, de esta manera
		//no es necesario cambiar en cada archivo como se da de alta.

		this.TS.put(clave, atts);
	}

	public void inicializarBuffer(){
		buffer = "";
	}

	public int ASCIIToken(char c) {
		return (int) c;
	}

	public boolean finDePrograma(){
		return pos >= fuente.length();
	}

	public String erroresLexicos(){
		String salida = "";
		for (String s: erroresLexicos)
			salida += s + "\n";
		return salida;
	}

	public void agregarError(int estado){
		switch(estado){
			case 0:
				erroresLexicos.add("Error: letra mayúscula no esperada. Linea: " + nroLinea);
				break;
			case 1:
				erroresLexicos.add("Error: carácter inválido en identificador. Se espera letra o digito. Linea: " + nroLinea);
				break;
			/*
			 * case 2: ESTADO SIN ERRORES POSIBLES
				erroresLexicos.add();*/
			case 3:
				erroresLexicos.add("Error: carácter inválido en constante. Se espera digito o sufijo luego de '" + buffer + "'. Linea: " + nroLinea);
				break;
			case 4:
				erroresLexicos.add("Error: carácter inválido en sufijo de constante. Se espera sufijo '_i' o '_ul'. Linea: " + nroLinea);
				break;
			case 5:
				erroresLexicos.add("Error: carácter inválido en sufijo de constante. Se espera sufijo '_i' o '_ul'. Linea: " + nroLinea);
				break;
			case 6:
				erroresLexicos.add("Error: se espera '=' luego de ':' para una asignación. Linea: " + nroLinea);
				break;
			/*
			 * case 7: ESTADO SIN ERRORES POSIBLES
				erroresLexicos.add("");
			   case 8: ESTADO SIN ERRORES POSIBLES
				erroresLexicos.add(""); */
			case 9:
				erroresLexicos.add("Error: se espera '=' luego de '!' para el comparador NOT EQUAL. Linea: " + nroLinea);
				break;
			/*case 10: ESTADO SIN ERRORES POSIBLES
				erroresLexicos.add("");
			  case 11: ESTADO SIN ERRORES POSIBLES
				erroresLexicos.add("");*/
			case 12:
				erroresLexicos.add("Error: las cadenas de caracteres no pueden contener saltos de linea. Linea: " + nroLinea);
				break;
		}

	}
	public void agregarError(String error){
		this.erroresLexicos.add(error);
	}
}
