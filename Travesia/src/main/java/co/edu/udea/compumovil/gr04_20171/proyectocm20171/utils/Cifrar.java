package co.edu.udea.compumovil.gr04_20171.proyectocm20171.utils;


import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * Define metodos de utilidad para la aplicacion
 * @author Jonnatan Rios - jrios328@gmail.com
 *
 */

public class Cifrar {
	
	/**
	 * cifra la cadena de caracteres ingresada como parametro
	 * @author Jonnatan Rios - jrios328@gmail.com
	 *
	 */
	public static String cifrar(String pws){
		String sha1;
		sha1 = new String(Hex.encodeHex(DigestUtils.sha1(pws)));
		return sha1;
	}
}
