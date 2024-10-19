package net;

/**
 * Excepción que se lanza cuando se produce un error al hacer scraping de la web
 * de datos sobre disciplinas de los Juegos Olimpicos de Paris 2024.
 */
public class WebScraperException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WebScraperException(String message, Throwable cause) {
		super(message, cause);
	}

	public WebScraperException(String message) {
		super(message);
	}

	public WebScraperException(Throwable cause) {
		super(cause);
	}
}
