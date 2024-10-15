package gui.main;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;

import gui.util.CountryCodes;
import gui.util.IconLoader;
import gui.util.IconLoadingException;

/**
 * Renderer para el selector de países JComboBox
 */
public class CountryCellRenderer extends DefaultListCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		// referencia al componente creado por la implementación por defecto del renderer
		JLabel jLabel = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
		// el valor recibido para la celda es el país mostrado en el  JCombobox
		String country = (String) value;
		
		// intentamos obtener la bandera correspondiente al país
		try {
			String isoCode = CountryCodes.COUNTRY_ISO_CODES.get(country);
			ImageIcon flagIcon = IconLoader.getIcon(String.format("/images/flags/%s.png", isoCode.toLowerCase()));
			jLabel.setIcon(flagIcon);
		} catch (IconLoadingException e) {
			System.err.println("No se ha podido encontrar la bandera el país: " + country);
		}
		
		// devolvemos el componente a pintar en el JComboBox
		return jLabel; 
	}

}
