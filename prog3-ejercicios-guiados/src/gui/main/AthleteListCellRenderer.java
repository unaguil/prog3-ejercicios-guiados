package gui.main;

import java.awt.Component;
import java.awt.Font;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;

import domain.Athlete;
import gui.util.CountryCodes;
import gui.util.IconLoader;
import gui.util.IconLoadingException;

/**
 * Renderer utilizado en el JList de atletas para cambiar el modo en el que se
 * renderiza cada celda.
 * 
 * Permite especificar un texto que se mostrará resaltado en cada celda.
 * Se utilizará un fondo amarillo para resaltar el texto.
 */
public class AthleteListCellRenderer extends DefaultListCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String text = ""; // text a resaltar en la celda		
	
	/**
	 * Establece el texto que se resaltará en el JLabel
	 * @param text texto a resaltar en el JLabel
	 */
	public void setHighLightedText(String text) {
		this.text = text;
	}

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		// obtenemos el componente JLabel creado por el renderer por defecto
		Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
		// sabemos que por defecto es un JLabel, así que podemos hacer un cast de manera segura
		JLabel jLabel = (JLabel) c;
		
		// quitamos la negrita de la fuente
		jLabel.setFont(jLabel.getFont().deriveFont(Font.PLAIN));
		
		// ahora establecemos el texto del JLabel al nombre del atleta recibdo en value
		// nuevamente podemos hacer un cast ya que sabemos que nuestro JList tiene Athletes
		Athlete athlete = (Athlete) value;
		
		// obtenemos el icono a partir del nombre del país del atleta
		try {
			String isoCode = CountryCodes.COUNTRY_ISO_CODES.get(athlete.getCountry());
			ImageIcon flagIcon = IconLoader.getIcon(String.format("/images/flags/%s.png", isoCode.toLowerCase()));
			jLabel.setIcon(flagIcon);
		} catch (IconLoadingException e) {
			// mostrar el error por consola
			System.err.println("No se ha podido encontrar la bandera el país: " + athlete.getCountry());
		}
		
		// tenemos en cuenta que el texto no sea vacio o sea espacios
		if (!text.isBlank()) {
			// buscamos dónde empieza y termina el texto a resaltar sin importar mayúsculas y minúsculas
			String lowerCaseText = text.toLowerCase();
			String lowerCaseName = athlete.getName().toLowerCase();
			int start = lowerCaseName.indexOf(lowerCaseText);
			int end = start + text.length();
			
			// construimos el texto resaltado con fondo amarillo
			String highlightedText = "<html>" + athlete.getName().substring(0, start) + 
				"<span style='background-color: yellow;'>" + 
				athlete.getName().substring(start, end) + "</span>" +
				athlete.getName().substring(end) + "</html>";
			
			// establecemos el texto resaltado en el JLabel
			jLabel.setText(highlightedText);
		} else {
			// si no hay texto a resaltar, establecemos el nombre del atleta sin cambios
            jLabel.setText(athlete.getName());
		}
		return jLabel;
	}

	
}
