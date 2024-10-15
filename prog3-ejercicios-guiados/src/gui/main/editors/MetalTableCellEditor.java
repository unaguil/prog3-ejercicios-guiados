package gui.main.editors;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import domain.Medal.Metal;

/**
 * Editor personalizado para la columna de tipo de metal de medalla de la tabla de medallas.
 * El editor utiliza un JComboBox para mostrar los tipos de metal disponibles.
 */
public class MetalTableCellEditor extends AbstractCellEditor implements TableCellEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JComboBox<Metal> comboBox; // referencia al selector de metales de medalla
	
	public MetalTableCellEditor() {
		comboBox = new JComboBox<Metal>(Metal.values());
	}

	@Override
	public Object getCellEditorValue() {
		// debe devolver el valor seleccionado en el JComboBox
		// que será uno de los valores del enum Metal
		return comboBox.getSelectedItem();
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		// devolvemos el JComboBox para que se muestre en la celda
		// cuando esta celda se edite por el usuario, pero primero
		// establecemos el selector al valor actual de la celda
		comboBox.setSelectedItem(value);
		return comboBox;
	}
}
