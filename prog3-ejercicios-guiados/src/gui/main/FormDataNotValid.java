package gui.main;

/**
 * Excepción utilizada para representar errores de datos en los
 * campos del formulario.
 */
public class FormDataNotValid extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FormDataNotValid(String message) {
		super(message);
	}
	
	public FormDataNotValid(String message, Throwable cause) {
		super(message, cause);
	}
}
