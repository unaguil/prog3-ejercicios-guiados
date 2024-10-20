package net;

/**
 * Excepción lanzada cuando se produce un error al acceder a la cache de
 * disciplinas.
 */
public class DisciplineCacheException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DisciplineCacheException(String message) {
		super(message);
	}

	public DisciplineCacheException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public DisciplineCacheException(Throwable cause) {
		super(cause);
	}
}
