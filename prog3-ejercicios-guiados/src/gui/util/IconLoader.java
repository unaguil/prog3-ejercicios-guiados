package gui.util;

import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

/**
 * Clase de utilidad para cargar iconos a partir su nombre de fichero. La clase
 * cachea el icono para evitar cargarlo cada vez.
 */
public class IconLoader {

	private static Map<String, ImageIcon> loadedIcons = new HashMap<>(); // mapa para guardar los iconos cargados

	/**
	 * Obtiene el icono a partir de la ruta indicada
	 * 
	 * @param iconPath ruta al icono a cargar
	 * @return icono cargado
	 * @throws IconLoadingException si hay algún problema al cargar el icono
	 */
	public static ImageIcon getIcon(String piconPath) throws IconLoadingException {
		// comprobamos si el icono ya ha sido cargada y no es nulo
		if (loadedIcons.containsKey(piconPath)) {
			// si el icono es nulo es que ya se intentó cargar pero hubo algún error
			if (loadedIcons.get(piconPath) == null) {
				throw new IconLoadingException("Icono no encontrado");
			}

			// existe el icono, lo devolvemos
			return loadedIcons.get(piconPath);
		}

		// leemos el fichero desde la ruta indicada
		ImageIcon imageIcon = new ImageIcon(IconLoader.class.getResource(piconPath));
		loadedIcons.put(piconPath, imageIcon); // cacheamos el icono ya cargado
		return imageIcon;
	}
}
