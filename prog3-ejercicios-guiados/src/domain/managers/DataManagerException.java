package domain.managers;

/**
 * Excepción utilizada por los gestores de datos para indicar que se ha
 * producido algún problema
 */
public class DataManagerException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DataManagerException(String message) {
		super(message);
	}
	
	public DataManagerException(String message, Throwable t) {
		super(message, t);
	}
}
