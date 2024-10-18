package domain.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import domain.Athlete;

/**
 * Clase que proporciona métodos para gestionar los atletas registrados en la
 * aplicación.
 */
public class AthleteManager {

	/**
	 * Código utilizado para indicar que un atleta no tiene un id válido y por lo
	 * tanto todavía no está registrado.
	 */
	public static final int NOT_VALID_ID = 1;

	private Map<Integer, Athlete> athleteMap; // mapa de atletas registrados
	private int lastId; // último id registrado

	public AthleteManager() {
		// inicialmente no hay atletas registrados
		this.athleteMap = new HashMap<Integer, Athlete>();
	}

	/**
	 * Elimina todos los atletas registrados.
	 */
	public void removeAll() {
		athleteMap.clear();
	}

	/**
	 * Obtiene la lista de todos los atletas registrados.
	 * 
	 * @return lista de atletas registrados
	 */
	public List<Athlete> getAll() {
		List<Athlete> athletes = new ArrayList<>();
		for (Entry<Integer, Athlete> e : athleteMap.entrySet()) {
			athletes.add(e.getValue());
		}
		return athletes;
	}

	/**
	 * Registra un nuevo atleta en el gestor de datos y genera un nuevo id para
	 * identificarlo.
	 * 
	 * @param athlete nuevo atleta a registrar
	 * @return atleta registrado en el gestor de datos
	 * @throws DataManagerException se produce si el atleta ya estaba registrado
	 */
	public Athlete add(Athlete athlete) throws DataManagerException {
		// comprobamos si el atleta ya estaba registrado
		if (athlete.getCode() != NOT_VALID_ID && athleteMap.containsKey(athlete.getCode())) {
			throw new DataManagerException(
					String.format("El atleta '%d' ya se encontraba registrado", athlete.getCode()));
		}

		// si el atleta todavía no estaba registrado, generamos un nuevo código
		// y lo registramos en el mapa de atletas
		// construimos un nuevo objeto a partir de los datos originales y lo devolvemos
		Athlete newAthlete = new Athlete(lastId++, athlete.getName(), athlete.getGenre(), athlete.getCountry(),
				athlete.getBirthdate());
		athleteMap.put(newAthlete.getCode(), newAthlete);
		return athlete;
	}

	/**
	 * Actualiza los datos del atleta registrado. Si el atleta no estaba registrado
	 * (no se encuentra su código), se lanza una excepción para indicarlo.
	 * 
	 * @param updated atleta cuyos datos se deben actualizar
	 * @throws DataManagerException si el atleta no estaba registrado
	 */
	public void update(Athlete updated) throws DataManagerException {
		checkAlreadyRegistered(updated.getCode()); // comprobar si estaba registrado
		athleteMap.put(updated.getCode(), updated); // se actualiza el atleta
	}

	/**
	 * Elimina el atleta registrado. Si el atleta no estaba registrado (no se
	 * encuentra su id), se lanza una excepción para indicarlo
	 * 
	 * @param id identificador del atleta a eliminar
	 * @throws DataManagerException si el atleta no estaba registrado
	 */
	public void remove(int id) throws DataManagerException {
		checkAlreadyRegistered(id); // comprobar si estaba registrado
		athleteMap.remove(id); // se elimina el atleta del mapa
	}

	// método que comprueba si atleta estaba anteriormente registrado
	// en caso que no lo estuviera, lanza una excepción indicándolo
	private void checkAlreadyRegistered(int id) throws DataManagerException {
		// si el atleta no estaba registrado, lanzamos una excepción
		if (!athleteMap.containsKey(id)) {
			throw new DataManagerException(String.format("El atleta '%d' no estaba registrado", id));
		}
	}
}
