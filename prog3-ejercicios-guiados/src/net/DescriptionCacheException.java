package net;

/**
 * Excepción lanzada cuando se produce un error al acceder a la cache de
 * descripciones de disciplinas.
 */
public class DescriptionCacheException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DescriptionCacheException(String message) {
		super(message);
	}

	public DescriptionCacheException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public DescriptionCacheException(Throwable cause) {
		super(cause);
	}
}
