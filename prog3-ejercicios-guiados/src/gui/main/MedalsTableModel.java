package gui.main;

import java.util.Collections;
import java.time.LocalDate;
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
	 */
	public MedalsTableModel() {
		this.medals = Collections.emptyList();
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
		
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		// indica el tipo de cada columna de datos
		// puede ser usado por renderers o editors
		switch (columnIndex) {
			case 0: return Medal.Metal.class;
			case 1: return LocalDate.class;
			case 2: return String.class;
			default: return null;
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		// indica si una celda es editable o no
		// queremos que todas las celdas de la tabla sean editables
		return true;
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		// este método es llamado por el JTable cuando el usuario
		// modifica el valor de una celda, el modelo de datos debe
		// actualizar los datos internos en consecuencia
		Medal m = medals.get(rowIndex); // fila que se ha modificado
		switch (columnIndex) {
		case 0:
			m.setMetal((Medal.Metal) value);
			break;
		case 1:
			m.setMedalDate((LocalDate) value);
			break;
		case 2:
			m.setDiscipline((String) value);
			break;
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
	
	/**
	 * Añade una nueva medalla a la lista de medallas del modelo
	 * y notifica al JTable asociado para que se actualice
	 * @param medal	medalla a añadir
	 */
	public void addMedal(Medal medal) {
		medals.add(medal);
		
		// podemos indicar que se ha añadido una fila concreta
        fireTableRowsInserted(medals.size() - 1, medals.size() - 1);
    }
}
