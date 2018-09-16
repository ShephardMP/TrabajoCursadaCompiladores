package resources;


public class AccionSemantica1 implements AccionSemantica {

	//definicion de accion semantica1
	/* >devolver ultimo caracter leido a la entrada
	 * >verificar que la longitud es menor a 25 caracteres
	 * 		>Si no cumple informar error (retornando token de error)
	 * >Buscar  en tabla de simbolos (buscar el String identificador, que debe ser el buffer completo
	 * 		>Si existe devolver <ID,PunTS>
	 * 		>Si no existe dar de alta y devolver <ID,PunTS>
	 * 
	 */
	public Token ejecutar(AnalizadorLexico AL, char nuevoChar) {
		Token devuelto = null;
		//String buffer=AL.buffer.substring(1); //esto estaba por si se debia eliminar el '_', pero en realidad no se agrega nunca al buffer
		AL.pos--; //volver un caracter de lectura hacia atras
		
		if(AL.buffer.length()>25) {
			System.out.println("Error: un identificador solo puede tener hasta 25 caracteres de longitud. L�nea: " + AL.nroLinea); 
			devuelto=new Token(-1,null); //token de error, ver de redefinir cualca
			
			
		}else {
			
			if(!AL.tablaSimbolos.containsKey(AL.buffer)) {
				
				AL.altaEnTablaSimbolos(AL.buffer, "ID", AL.buffer); //clave, tipo de token, lexema
	
			}
			devuelto= new Token(Tokens.ID,AL.buffer);
			
			
		}
		return devuelto;
	}

}
