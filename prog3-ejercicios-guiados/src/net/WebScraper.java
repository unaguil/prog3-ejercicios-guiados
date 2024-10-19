package net;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Proporciona métodos de utilidad para descargar la descripción de una
 * disciplina de los Juegos Olimpicos la página web
 * https://olympics.com/en/paris-2024/sports/
 */
public class WebScraper {

	// URL base de la página web de los Juegos Olimpicos
	private static String WEB_URL = "https://olympics.com/en/paris-2024/sports";

	/**
	 * Obtiene la URL completa de la página de la disciplina a partir del nombre.
	 * El nombre debe estar en formato slug.
	 * 
	 * @param page nombre de la disciplina en formato slug
	 * @return devuelve la lista de párrafos que contienen la descripción de la
	 *         disciplina.
	 * @throws WebScraperException si existe algún problema al crear la URL 
	 */
	public static URL getURL(String discipline) throws WebScraperException {
		try {
			return new URI(String.format("%s/%s", WEB_URL, slugify(discipline))).toURL();
		} catch (MalformedURLException | URISyntaxException e) {
			throw new WebScraperException(e);
		}
	}

	// obtiene la versión slug del string pasado
	private static String slugify(String s) {
		return String.join("-", s.toLowerCase().split(" "));
	}

	/**
	 * Obtiene la descripción de una disciplina de los Juegos Olimpicos a partir
	 * de la URL de la página a descargar.
	 * 
	 * @param discipline url de la página donde se encuentra la descripción
	 * @return devuelve la lista de párrafos que contienen la descripción de la
	 *         disciplina.
	 * @throws WebScraperException si ocurre un error al descargar la descripción
	 * @throws InterruptedException se produce cuando la petición http ha sido interrumpida
	 * 			con una llamada el método Thread.interrupt
	 */
	public static List<String> getDescription(URL discipline) throws WebScraperException, InterruptedException {
		// creamos el cliente HTTP para hacer la petición a la web
		HttpClient client = HttpClient.newBuilder().version(Version.HTTP_1_1).build();

		System.out.println("Descargando la descripción desde " + discipline);
		try {
			HttpRequest request = HttpRequest.newBuilder(discipline.toURI()).build();

			// el método send se bloquea hasta que se recibe la respuesta, sin embargo,
			// puede ser interrumpido con Thread.interrupt, lo que produce el lanzamiento
			// de la excepción InterruptedException
			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
			String html = response.body().toString();

			// extraemos del HTML la etiqueta <div class="main"> y dentro de esta
			// las etiquetas <p> que contienen los párrafos con la descripción del deporte
			List<String> paragraphList = new ArrayList<>();

			Document document = Jsoup.parse(html);
			Elements paragraphs = document.select("div.main p");
			for (Element p : paragraphs) {
				paragraphList.add(p.text());
			}

			return paragraphList;
		} catch (URISyntaxException e) {
			throw new WebScraperException("URL incorrecta: " + discipline, e);
		} catch (IOException e) {
			throw new WebScraperException("Error al hacer la petición HTTP", e);
		}
	}

	// programa principal para pruebas
	public static void main(String[] args) throws WebScraperException, InterruptedException {
		System.out.println(getDescription(WebScraper.getURL("Judo")));
		System.out.println(getDescription(WebScraper.getURL("Basketball")));
		System.out.println(getDescription(WebScraper.getURL("Athletics")));
		System.out.println(getDescription(WebScraper.getURL("Cycling Road")));
		System.out.println(getDescription(WebScraper.getURL("Wrestling")));
	}
}
