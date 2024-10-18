package io;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import domain.Athlete;
import domain.Athlete.Genre;
import domain.Medal;
import domain.Medal.Metal;

/**
 * Esta clase proporciona métodos de utilidad para leer los datos de atletas y
 * medallas desde ficheros CSV.
 */
public class CSVDataReader {

	/**
	 * Lee la información de atletas desde el fichero CSV indicado.
	 * 
	 * @param csvFile fichero CSV desde el que leer la información de atletas.
	 * @return lista de atletas cargados desde el fichero CSV.
	 */
	public static List<Athlete> loadAthletes(File csvFile) throws CSVDataReaderException {
		List<Athlete> athletes = new ArrayList<>(); // lista vacía de atletas

		// abrimos el fichero para leerlo línea a línea
		try (Scanner scanner = new Scanner(csvFile)) {
			int lineNum = 1; // número de línea

			// comprobamos que hay nuevas líneas para leer
			while (scanner.hasNextLine()) {
				String linea = scanner.nextLine(); // obtenemos la siguiente linea

				// no tenemos en cuenta la primera línea de cabecera
				if (lineNum > 1) {
					// partimos la línea en trozos por el caracter ','
					String[] columns = linea.split(",");

					try {
						// obtenemos cada parte conviertiendo la tipo de datos requerido
						int code = Integer.parseInt(columns[0]);
						String name = columns[2];

						Genre genre = Genre.valueOf(columns[5].toUpperCase());
						String country = columns[8];
						LocalDate birthdate = LocalDate.parse(columns[17]);

						// construimos una nueva instancia con los datos actuales
						athletes.add(new Athlete(code, name, genre, country, birthdate));
					} catch (DateTimeParseException | IllegalArgumentException e) {
						// si alguna línea tiene un error se ignora y se pasa a la siguiente
						System.out.println(String.format("Error procesando línea %d: %s", lineNum, e.getLocalizedMessage()));
					}
				}
				lineNum++; // linea procesada
			}
		} catch (FileNotFoundException e) {
			throw new CSVDataReaderException(
					String.format("Error abriendo el fichero '%s'", csvFile.getAbsolutePath(), e));
		}

		return athletes;
	}

	/**
	 * Lee la información de medallas desde el fichero CSV indicado.
	 * 
	 * @param csvFile  fichero CSV desde el que leer la información de medallas.
	 * @param athletes lista de atletas a los que asignar las medallas.
	 * @return lista de medallas cargadas desde el fichero CSV.
	 */
	public static List<Medal> loadMedals(File csvFile, List<Athlete> athletes) throws CSVDataReaderException {
		List<Medal> medals = new ArrayList<Medal>(); // lista vacía de medallas

		// abrimos el fichero para leerlo línea a línea
		try (Scanner scanner = new Scanner(csvFile)) {
			int lineNum = 1; // número de linea

			// comprobamos si hay líneas por leer
			while (scanner.hasNextLine()) {
				String linea = scanner.nextLine(); // obtenemos la siguiente linea

				// no tenemos en cuenta la primera línea de cabecera
				if (lineNum > 1) {
					// partimos la línea en trozos por el caracter ','
					String[] columns = linea.split(",");

					try {
						// obtenemos cada parte conviertiendo la tipo de datos requerido
						LocalDate date = LocalDate.parse(columns[0]);
						Metal metal = Metal.valueOf(columns[1].split("Medal")[0].trim());
						String discipline = columns[13];

						// busca el atleta que ha ganado la medalla en la lista de atletas
						// en base a su nombre utilizando el API de streams de Java
						String name = columns[3];
						Optional<Athlete> athlete = athletes.stream().filter(a -> a.getName().equals(name)).findFirst();
						// si se ha encontrado el atleta se construye la medalla, en caso contrario se
						// ignora esta medalla
						if (athlete.isPresent()) {
							Medal medal = new Medal(metal, date, athlete.get(), discipline);
							medals.add(medal);
						}
					} catch (IllegalArgumentException | DateTimeParseException e) {
						// si alguna línea tiene un error se ignora y se pasa a la siguiente
						System.out.println(String.format("Error procesando línea %d: %s", lineNum, e.getLocalizedMessage()));
					}
				}

				lineNum++; // incrementamos el contador de lineas procesadas
			}
		} catch (FileNotFoundException e) {
			throw new CSVDataReaderException(
					String.format("Error abriendo el fichero '%s'", csvFile.getAbsolutePath(), e));
		}

		return medals;
	}
}
