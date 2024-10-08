package gui.main;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import domain.Athlete;

/**
 * Renderer utilizado en el JList de atletas para cambiar el modo en el que se
 * renderiza cada celda.
 * 
 */
public class AthleteListCellRenderer extends DefaultListCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		// obtenemos el componente JLabel creado por el renderer por defecto
		Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
		// sabemos que por defecto es un JLabel, así que podemos hacer un cast de manera segura
		JLabel jLabel = (JLabel) c;
		
		// ahora establecemos el texto del JLabel al nombre del atleta recibdo en value
		// nuevamente podemos hacer un cast ya que sabemos que nuestro JList tiene Athletes
		Athlete athlete = (Athlete) value;
		
		jLabel.setText(athlete.getName());
		
		return jLabel;
	}

	
}
