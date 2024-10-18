package io;

/**
 * Excepción utilizada para representar errores al leer datos desde un fichero
 * CSV.
 */
public class CSVDataReaderException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CSVDataReaderException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public CSVDataReaderException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}
}
