package gui.main;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import domain.Medal;

/**
 * Modelo de datos personalizado para visualizar información de medallas
 * a partir de un List<Medal>.
 * 
 * Implementamos los distintos métodos qué configuran la información que
 * se muestra en la tabla que haga uso de este modelo. Cada método del
 * modelo responde a una serie de preguntas hace el JTable.
 */
public class MedalsTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String[] columnNames = new String[] { "Metal", "Fecha", "Disciplina" };
	private List<Medal> medals; // referencia a la lista de medallas
	
	/**
	 * Constructor que recibe los datos del modelo
	 * @param medals lista de medallas con los datos mostrar en
	 * la tabla
	 */
	public MedalsTableModel(List<Medal> medals) {
		this.medals = medals;
	}

	@Override
	public int getRowCount() {
		// número de filas a mostrar en la tabla
		return medals.size();
	}

	@Override
	public int getColumnCount() {
		// número de columnas a mostrar en la tabla
		return columnNames.length;
	}

	@Override
	public String getColumnName(int column) {
		// devolvemos el nombre de la columna indicada
		return columnNames[column];
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// devolvemos el valor a mostrar en cada celda a
		// partir de los datos que tenemos
		Medal m = medals.get(rowIndex);
		switch (columnIndex) {
			case 0: return m.getMetal();
			case 1: return m.getMedalDate();
			case 2: return m.getDiscipline();
			default: return null;
		}
	}

	
	/**
	 * Actualiza la lista de medallas del modelo de datos y
	 * notifica al JTable asociado para que se actualice
	 */
	public void updateMedals(List<Medal> medals) {
		this.medals = medals;

		// notificamos el cambio de datos para que
		// la tabla se repinte
		fireTableDataChanged();
	}
}
