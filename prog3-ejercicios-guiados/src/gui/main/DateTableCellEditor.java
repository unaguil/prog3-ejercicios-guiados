package gui.main;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.GregorianCalendar;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import org.jdatepicker.JDatePicker;

/**
 * Esta clase implementa un editor específico para la columna de fechas de la
 * tabla que utiliza JDatePicker (librería externa) para que se muestre un
 * calendario cuando el usuario quiere editar una celda de fecha.
 * 
 * La clase implementa un ActionListener para que el selector de fecha pueda
 * avisar de que el usuario ha seleccionado una fecha.
 */
class DateTableCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private JDatePicker datePicker;

	@Override
	public Object getCellEditorValue() {
		// este método debe devolver el valor seleccionado en el selector de fecha
		// que será una fecha en formato LocalDate		
		GregorianCalendar calendar = (GregorianCalendar) datePicker.getModel().getValue();
		return calendar.toZonedDateTime().toLocalDate();
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		// este método debe devolver el componente visual que se va a utilizar
		// cuando el usuario quiera editar la celda indicada
		// en este caso se devuelve un selector de fecha como componente de edición
		datePicker = new JDatePicker();
		
		// se establece la fecha actual en el selector antes de mostrarlo
		LocalDate currentDate = (LocalDate) value;
		datePicker.getModel().setDate(currentDate.getYear(), currentDate.getMonthValue() - 1, currentDate.getDayOfMonth());
		datePicker.getModel().setSelected(true);
		
		// se añade el ActionListener para que el selector de fecha pueda avisar
		// cuando el usuario ha terminado la edición
		datePicker.addActionListener(this);
		
		// se devuelve el selector de fecha para que se muestre en la celda
		return datePicker;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// este método se ejecuta cuando el usuario selecciona una fecha
		// se debe indicar que la edición ha terminado para lanzar
		// la actualización de la tabla y el modelo
		fireEditingStopped();
	}

}