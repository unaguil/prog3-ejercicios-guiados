package net;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Cache en ficheros de disco para las descripciones de disciplinas obtenidas de
 * internet.
 * 
 * La clase implementa el patrón singleton para que únicamente haya una
 * instancia de la cache en todo el programa.
 */
public class DescriptionCache {

	// estado de la descripción
	private enum Status {
		MISSING, PROCESSING, CACHED, ERROR
	}

	// directorio de la cache con los ficheros de disciplinas
	private static final String CACHE_DIR = "cache";

	// instancia única de la cache
	private static DescriptionCache instance = null;

	// mapa que contiene el estado de la cache
	// se utiliza un mapa concurrente para evitar problemas de concurrencia
	// cuando varios hilos modifican concurrentemente la estructura
	// por otro lado, el valor es un enum encapsulado en un AtomicReference
	// para poder modificarlo de forma atómica y evitar problemas de concurrencia
	// cuando se modifica el valor del estado de una descripción
	private Map<URL, AtomicReference<Status>> cacheMap = new ConcurrentHashMap<>();

	// referencia al hilo que se encarga de descargar las disciplinas en segundo
	// plano
	private Thread downloadThread = null;

	/**
	 * Constructor privado para evitar instanciaciones directas.
	 * 
	 */
	private DescriptionCache() {
		// se crea el directorio de cache si no existe
		File cacheDir = new File(CACHE_DIR);
		if (!cacheDir.exists()) {
			cacheDir.mkdir();
		} else {
			// se actualiza el mapa de estado de la cache
			// se listan todos los ficheros del directorio
			File[] files = cacheDir.listFiles();
			for (File file : files) {
				// nombre del fichero sin la extensión
				// se añade al mapa con el estado CACHED		
				String discipline = file.getName().replace(".bin", "");
				try {
					cacheMap.put(WebScraper.getURL(discipline), new AtomicReference<>(Status.CACHED));
				} catch (WebScraperException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Devuelve la instancia única de la cache.
	 * 
	 * @return instancia de la cache
	 */
	public static DescriptionCache getInstance() {
		if (instance == null) {
			instance = new DescriptionCache();
		}
		return instance;
	}
	
	/**
	 * Elimina todos los ficheros de cache de disciplinas.
	 */
	public void clearCache() {
		File cacheDir = new File(CACHE_DIR);
		if (cacheDir.exists()) {
			File[] files = cacheDir.listFiles();
			for (File file : files) {
				file.delete();
			}
		}
		cacheMap.clear();
	}

	/**
	 * Obtiene las URLs de las disciplinas que están gestionadas por la cache.
	 * 
	 * @return lista de las URLs disciplinas gestionadas por la cache
	 */
	public List<URL> getDisciplines() {
		return cacheMap.keySet().stream().collect(Collectors.toList());
	}

	/**
	 * Obtiene la lista de las URls de las disciplinas que están pendientes de descarga.
	 * 
	 * @return lista de las URLs disciplinas pendientes de descarga
	 */
	public List<URL> getPendingDisciplines() {
		return cacheMap.entrySet().stream().filter(entry -> entry.getValue().get() == Status.MISSING)
				.map(entry -> entry.getKey()).collect(Collectors.toList());
	}

	/**
	 * Añade URLs de disciplinas a la cache para ser descargadas en segundo plano.
	 * 
	 * @param urls de las disciplinas a gestionar por la cache
	 */
	public void addURLs(Set<URL> disciplines) {
		// se añaden las disciplinas al mapa de estado de la cache
		// solamene si no están ya en el mapa, en este caso el estado inicial
		// es MISSINGs
		for (URL discipline : disciplines) {
			cacheMap.putIfAbsent(discipline, new AtomicReference<>(Status.MISSING));
		}
	}

	/**
	 * Obtiene la descripción de una disciplina. Si la descripción no está en la
	 * cache se descarga de internet, si está en la cache se lee del fichero. Este
	 * método es asíncrono y devuelve la descripción a través de un consumer, que
	 * será notificado cuando la descripción esté disponible.
	 * 
	 * @param discipline url de la disciplina cuya descripción se quiere obtener
	 * @param onDescription consumer que recibe la descripción de la disciplina si ha terminado
	 *                 la descarga correctamente.
	 * @param onError    consumer que recibe un mensaje de error si no se puede obtener la
	 *                  descripción
	 * @param maxWait   tiempo máximo en milisegundos que se espera a que la descripción esté disponible
	 * 					antes de considerar que no se ha podido obtener
	 */
	public void getDescriptionAsync(URL discipline, Consumer<List<String>> onDescription,
			Consumer<DescriptionCacheException> onError, int maxWait) {
		// se obtiene el estado de la disciplina y si no existe se añade al mapa
		AtomicReference<Status> status = cacheMap.computeIfAbsent(discipline, k -> new AtomicReference<>(Status.MISSING));
		
		// se comprueba si la disciplina está en la cache
		if (status.get() == Status.CACHED) {
			try {
				System.out.println("Descripción cacheada. Leyendo de disco la descripción de la disciplina " + discipline);
				// si está en la cache se lee de disco
				List<String> description = deserializeDescription(discipline);
				// se notifica al consumer onDescription que la descripción está disponible
				onDescription.accept(description);
			} catch (IOException | ClassNotFoundException | URISyntaxException e) {
				// se notifica el error al consumer onError
				onError.accept(new DescriptionCacheException("Error al obtener la descripción de la disciplina " + discipline, e));
			}
		} else if (status.compareAndSet(Status.MISSING, Status.PROCESSING)) {
			// en el if anterior se utiliza compareAndSet para evitar problemas de
			// concurrencia y que vayos hilos se pongan a descargar la descripción a la vez. 
			// Solo uno de los hilos podrá cambiar el estado a PROCESSING y empezar a descargar la
			// descripción, el resto de hilos verán el estado en PROCESSING y esperarán a que el
			// hilo que está descargando la descripción termine y actualice el estado a CACHED
			System.out.println("Descargando la descripción de la disciplina " + discipline);
			new Thread(() -> {
				try {
					// obtenemos la descripción de la disciplina de internet
					List<String> description = downloadDescription(discipline);
					// se actualiza el estado de la disciplina a CACHED
					status.set(Status.CACHED);
					// se notifica al consumer onDescription que la descripción está disponible
					onDescription.accept(description);
				} catch (WebScraperException | IOException | URISyntaxException e) {
					// se actualiza el estado de la disciplina a ERROR
					status.set(Status.ERROR);
					// se notifica el error al consumer onError
					onError.accept(new DescriptionCacheException("Error al obtener la descripción de la disciplina " + discipline, e));
				} catch (InterruptedException e) {
					// se ha interrumpido la descarga
					// no se hace nada ya que el hilo no tiene ningún bucle
					// que deba ser interrumpido
				}
			}).start();
		} else if (status.get() == Status.PROCESSING) {
			// si el estado es PROCESSING significa que el hilo de descarga en segundo plano
			// está descargando la descripción, por lo que se va a esperar a que termine
			// comprobando periódicamente el estado de la disciplina y se notificará al
			// consumer cuando la descripción esté disponible. Se espera un máximo de 5 segundos
			// a que la descripción esté disponible, si no está disponible en ese tiempo se notifica
			// al consumer con un mensaje de error.
			System.out.println("Esperando a que termine la descarga de la descripción de la disciplina " + discipline);
			new Thread(() -> {
				final int SLEEP_TIME = 100;

				int totalTime = 0;
				while (status.get() == Status.PROCESSING && totalTime < maxWait) {
					try {
						Thread.sleep(SLEEP_TIME); // dormimos el hilo para esperar
					} catch (InterruptedException e) {
						// no vamos a hacer nada ya que el hilo no va a ser interrumpido
					}
				}

				// si el tiempo ha llegado a MAX_WAIT o se ha producido un error durante la
				// descarga se notifica al consumer con un mensaje de error en la descripción
				if (totalTime >= maxWait || status.get() == Status.ERROR) {
					// se notifica el error al consumer onError
					onError.accept(new DescriptionCacheException("Error al obtener la descripción de la disciplina " + discipline));
				}
				
				// se ha descargado la descripción correctamente, se lee de disco y se notifica al consumer 
				try {
					List<String> description = deserializeDescription(discipline);
					// se notifica al consumer onDescription que la descripción está disponible
					onDescription.accept(description);
				} catch (ClassNotFoundException | IOException | URISyntaxException e) {
					// se notifica el error al consumer onError
					onError.accept(new DescriptionCacheException("Error al obtener la descripción de la disciplina " + discipline, e));
				}
			}).start();
		}
	}

	// método que lleva a cabo la descarga de la descripción y la serialización
	protected List<String> downloadDescription(URL discipline) throws WebScraperException, IOException, URISyntaxException, InterruptedException {
		// obtener la descripción de la disciplina de internet
		List<String> description = WebScraper.getDescription(discipline);
		// serializar la descripción en un fichero
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(getCacheFile(discipline)))) {
			oos.writeObject(description);
		}
		return description;
	}

	/**
	 * Inicia la descarga de las disciplinas en segundo plano utilizando un hilo.
	 */
	public void startDownload() {
		// si el hilo ya está iniciado no se hace nada
		if (downloadThread != null) {
			return;
		}
		
		// se crea un nuevo hilo para la descarga
		downloadThread = new Thread(() -> {
			// en este caso vamos a utilizar la pregunta isInterrupted que no cambia el estado del flag
			// tras la consulta, ya que vamos a comprobarlo varias veces en el bucle
			while (!Thread.currentThread().isInterrupted()) {
				// obtener todas las disciplinas sin descargar a una lista
				// vamos a usar un stream pero se puede hacer con un bucle
				List<URL> pendingDisciplines = cacheMap.entrySet().stream()
						.filter(entry -> entry.getValue().get() == Status.MISSING).map(entry -> entry.getKey())
						.collect(Collectors.toList());
				
				// comprobamos si hay disciplinas cuya descripción no se ha descargado todavía
				if (pendingDisciplines.size() > 0) {	
					System.out.println("Disciplinas pendientes de descargar: " + pendingDisciplines.size());
					// iteramos sobre la disciplinas pendientes mientras haya disciplinas
					// y el hilo no haya sido interrumpido
					Iterator<URL> it = pendingDisciplines.iterator();
					while (it.hasNext() && !Thread.currentThread().isInterrupted()) {
						URL discipline = it.next(); // obtenemos la url de la disciplina
	
						// si la disciplina esta todavía en estado MISSING actualizamos el estado
						// a PROCESSING en una operación atómica/individisible y procedemos a descargar
						if (cacheMap.get(discipline).compareAndSet(Status.MISSING, Status.PROCESSING)) {
							try {
								downloadDescription(discipline); // descargamos la descripción
								// cambiamos el estado de la disciplina a CACHED
								cacheMap.get(discipline).set(Status.CACHED);
							} catch (WebScraperException | IOException | URISyntaxException e) {
								// se hay un error se actualiza el estado de la disciplina a ERROR
								cacheMap.get(discipline).set(Status.ERROR);
								// se muestra un mensaje por consola ya que no hay un consumer al que notificar
								System.out.println("Error al descargar la disciplina " + discipline + ". " + e.getMessage());
							} catch (InterruptedException e) {
								// se ha interrumpido la descarga con una llamada a Thread.interrupt
								// vamos a reestablecer el estado interrumpido del thread para las
								// siguientes comprobaciones
								Thread.currentThread().interrupt();
							}
						}
	
						if (!Thread.currentThread().isInterrupted()) {
							// vamos a dormir un tiempo aleatorio entre 1 y 10 segundos el hilo de descarga
							// para no saturar el servidor haciendo muchas peticiones seguidas
							// esto solamente se hace si el hilo no debe detenerse
							randomSleep(1000, 10000);
						}
					}
				}
				// vamos a esperar un tiempo entre 1 y 5 segundos antes de volver a comprobar si hay
				// descripciones pendientes de descargar
				if (!Thread.currentThread().isInterrupted()) {
					randomSleep(1000, 5000);
				}
			}
			
			System.out.println("Hilo de descarga terminado");
		});
		downloadThread.start();
	}
	
	// método para esperar un tiempo aleatorio durmiendo el hilo
	private void randomSleep(int min, int max) {
		long waitTime = (long) (Math.random() * (max - min) + min);
		System.out.format("Durmiendo el hilo de descarga durante %d ms...%n", waitTime);
		try {
			Thread.sleep(waitTime);
		} catch (InterruptedException e) {
			// vamos a reestablecer el estado interrumpido del thread para las
			// siguientes comprobaciones
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Detiene la descarga de las disciplinas en segundo plano.
	 */
	public void stopDownload() {
		if (downloadThread != null) {
			// interrumpimos el hilo
			downloadThread.interrupt();

			// esperamos a que el hilo termine
			try {
				downloadThread.join();
			} catch (InterruptedException e) {
				// si hay un error se muestra por consola
				System.err.println("Error al detener el hilo de descarga");
			}

			// indicamos que el hilo de descarga ha terminado
			downloadThread = null;
		}
	}

	// metodo que construye la ruta al fichero de cache de una disciplina
	// a partir de la URL de la disciplina y la ruta al directorio de cache
	private File getCacheFile(URL discipline) throws URISyntaxException {
		// se utiliza Paths para construir la ruta del fichero
		String[] parts = discipline.toString().split("/");
		String lastPart = parts[parts.length - 1];
		return Paths.get(CACHE_DIR, lastPart.concat(".bin")).toFile();
	}

	// método para leer la descripción de una disciplina de un fichero
	// utilizando la serialización nativa de Java
	@SuppressWarnings("unchecked")
	private List<String> deserializeDescription(URL discipline) throws IOException, ClassNotFoundException, URISyntaxException {
		// se deserializa la descripción del fichero usando un ObjectInputStream
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(getCacheFile(discipline)))) {
			return (List<String>) ois.readObject();
		}
	}

	// programa principal de prueba del hilo de descarga
	public static void main(String[] args) {
		// obtenemos la instancia de la cache que es única al ser singleton
		DescriptionCache cache = DescriptionCache.getInstance(); 		
		
		// limpiamos la cache
		cache.clearCache();

		// cada 1 segundo intentamos obtener la descripción de una disciplina
		// el proceso continua hasta que todas las descripciones estén disponibles
		try {
			// creamos una lista de disciplinas en formato slug
			Set<URL> disciplines = Set.of("Judo", "Basketball", "Wrestling", "Cycling Road", "Athletics").stream()
				.map(t -> {
					try {
						return WebScraper.getURL(t);
					} catch (WebScraperException e) {
						throw new RuntimeException(e);
					}
				})
				.collect(Collectors.toSet());
			
			// añadimos las disciplinas a la cache y que empiece a descargar en segundo plano	
			cache.addURLs(disciplines);
			cache.startDownload();
			
			Iterator<URL> it = disciplines.iterator();
			while (it.hasNext() && !cache.getPendingDisciplines().isEmpty()) {
				URL discipline = it.next();
				// se llama al método para obtener la descripción de la disciplina de forma asíncrona
				// el método puede terminar antes de que la descripción esté disponible, por lo que
				// se debe proporcionar un consumer que será notificado cuando la descripción esté disponible
				// así como otro consumer que será notificado si hay un error o si se ha superado el tiempo de espera
				cache.getDescriptionAsync(discipline, 
					description -> 	System.out.println("Descripción de la disciplina " + discipline + ": " + description),
					error -> System.out.println("Error al obtener la descripción de la disciplina " + discipline + ". " + error.getMessage()),
					5000
				);
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// solicitamos al hilo de descarga que se detenga
		cache.stopDownload();

		// mostramos las descripciones de las disciplinas pendientes
		System.out.println("Disciplinas pendientes de descarga:" + cache.getPendingDisciplines());
		
		System.out.println("Programa principal terminado");
	}
}
