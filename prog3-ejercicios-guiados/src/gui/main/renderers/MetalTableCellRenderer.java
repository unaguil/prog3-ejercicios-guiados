package gui.main.renderers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import domain.Medal;
import domain.Medal.Metal;

/**
 * Esta clase implementa un renderer básico para que el tipo de medalla se pinte
 * en colores distintos según sea ORO, PLATA o BRONCE. Este renderer se asignará
 * a las celdas de la primera columna. Además, pone la letra en negrita para que
 * se lea mejor.
 * 
 * El renderer extiende del renderer por defecto de una tabla para configurar
 * únicamente el mínimo necesario del JLabel que se usa en cada celda. 
 */
public class MetalTableCellRenderer extends DefaultTableCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Map<Medal.Metal, Color> metalColors = Map.of(
		Metal.GOLD, new Color(255, 215, 0),
		Metal.SILVER, new Color(165, 169, 180),
		Metal.BRONZE, new Color(205, 127, 50)
	);

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		// aprovechamos la implementación existente en la clase antecesora
		// para obtener el componente JLabel visualizado como dentro de cada
		// celda con la configuración por defecto
		JLabel jLabel = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		// en value se recibe el contenido de la celda, que por diseño de
		// nuestro código sabemos que es un enum Metal
		Metal metal = (Metal) value;
		
		// establecemos el color del texto del JLabel de acuerdo
		// al metal de la medalla recibida
		jLabel.setForeground(metalColors.get(metal));
		jLabel.setFont(jLabel.getFont().deriveFont(Font.BOLD));
		
		return jLabel;
	}

}
