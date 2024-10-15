package gui.util;

/**
 * Excepción utilizada para indicar que ha habido un problema para cargar la
 * bandera del país especificado.
 */
public class IconLoadingException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IconLoadingException(String message) {
		super(message);
	}

	public IconLoadingException(Throwable cause) {
		super(cause);
	}

}
