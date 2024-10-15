package gui.main.renderers;

import java.awt.Component;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Esta clase implementa un renderer de fecha que puede ser utilizado para
 * cambiar el formato de visualización de la fecha en una tabla. Solamente es
 * compatible con celdas que visualicen datos de tipo LocalDate. Además, el
 * texto de la fecha se alinea a la derecha de la celda.
 * 
 * El renderer extiende del renderer por defecto de una tabla para configurar
 * únicamente el mínimo necesario del JLabel que se usa en cada celda.
 */
public class DateTableCellRenderer extends DefaultTableCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private DateTimeFormatter dateTimeFormatter;
	
	/** Constructor que recibe el formateador de fecha a utilizar por el
	 * renderer
	 * @param dateTimeFormatter formateador de fecha utilizado por el renderer
	 * para el campo fecha
	 */
	public DateTableCellRenderer(DateTimeFormatter dateTimeFormatter) {
		this.dateTimeFormatter = dateTimeFormatter;		
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {

		// aprovechamos la implementación existente en la clase antecesora
		// para obtener el componente JLabel visualizado como dentro de cada
		// celda con la configuración por defecto
		JLabel jLabel = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		// establecemos el texto del JLabel al resultado de
		// formatear la fecha con el formateador configurado para
		// este renderer obtenido de value, que sabemos que es un
		// LocalDate porque este renderer no debe ser usado con otros tipos
		// de datos
		jLabel.setText(dateTimeFormatter.format((LocalDate) value));
		
		// texto a la derecha en el JLabel
		jLabel.setHorizontalAlignment(JLabel.RIGHT);
		
		// devolvemos el label para que se pinte en la celda que está siendo
		// renderizada en el JTable 
		return jLabel;
	}
}
