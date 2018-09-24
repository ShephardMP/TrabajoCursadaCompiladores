package resources;

public class AccionSemantica4 implements AccionSemantica {
	//esta accion semantica se define como:
	/*
	 * >verificar si cte entra en rango definido para i
	 * 		si no, pisar con maximo valor e informar warning
	 * >verificar si existe en tabla simbolo
	 * 	Si: retornar <cte, puntTS>
	 *  No: dar de alta en TS y retornar <cte,punts>
	 * @see resources.AccionSemantica#ejecutar(resources.AnalizadorLexico, char)
	 */
	private int minValor=-32768;
	private int maxValor=32768;
	public Token ejecutar(AnalizadorLexico AL, char nuevoChar) {
		
		String cte=AL.buffer; 
		int valorCte=new Integer(cte).intValue();
		
		String claveTS=cte+"_i";
		
	
		Token devuelto=null;
		
		if(valorCte< minValor || valorCte> maxValor ) { //chequeo de rangos
			valorCte=maxValor; //tecnica de reemplazo
			claveTS=valorCte+"_i"; //esto es tricky, si reemplace el valor, debo crear una nueva clave, asi evito usar claves que nunca voy a acceder.
			AL.agregarError("Warning: constante integer fuera de rango. Reemplazo en l�nea: " + AL.nroLinea);
		}
		
		if(!AL.TS.containsKey(claveTS)) {
			
			AL.altaEnTablaSimbolos(claveTS,"CTE_INTEGER",valorCte);
			
		}
			
		devuelto= new Token(Token.CTE_INTEGER,claveTS); 
		//si no existia, se creo antes, si existia devuelvo el TIPO DE TOKEN y su clave de acceso a la tabla de simbolos
		//Se incrementa para que se pueda hacer otra entrada a la TablaSimbolos con otra clave
		
		return devuelto;
		
		
		
		
		
	}

	
}
