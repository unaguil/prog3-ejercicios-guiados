package gui.main;

import java.awt.BorderLayout;
import java.awt.Window;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import domain.Athlete;

/**
 * Dialogo que permite registrar un nuevo atleta.
 */
public class NewAthleteDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int result = JOptionPane.CANCEL_OPTION; // resultado del diálogo
	
	private AthleteFormPanel formPanel; // panel del formulario
	
	
	/**
	 * Constructor que recibe la ventana que ha creado el diálogo
	 * @param countries lista de países para el selector del formulario
	 */
	public NewAthleteDialog(List<String> countries) {
		// establecemos el diálogo en modo modal
		setModal(true);
		
		// queremos que la ventana de diálogo termine pero que
		// no se cierre toda la aplicación
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Nuevo atleta");
		
		formPanel = new AthleteFormPanel(countries);
		add(formPanel, BorderLayout.CENTER); // al centro del diálogo
		
		//creamos los botones del panel inferior
		JPanel bottomPanel = new JPanel();
		
		// cierra el diálogo y establece 
		JButton saveButton = new JButton("Guardar");
		bottomPanel.add(saveButton);
		
		JButton cancelButton = new JButton("Cancelar");
		bottomPanel.add(cancelButton);
		
		// establecemos el comportamiento para los botones
		saveButton.addActionListener(event -> {
			// validamos el formulario intentando crear un nuevo
			// atleta y si hay algún error se lo mostramos al usuario			
			try {
				// si todo es correcto establecemos el resultado OK
				// para la operación de guardado y terminamos el diálogo
				formPanel.getAthlete();			
				result = JOptionPane.OK_OPTION;
				dispose(); // esta operación termina la ventana de diálogo
			} catch (FormDataNotValid e) {
				// mostramos un cuadro de diálogo si hay algún error
				// en el formulario
				JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
		
		// el botón de cancelar únicamente cierra el diálogo
		cancelButton.addActionListener(e -> dispose() );
		
		// añadimos los botones a la parte inferior del diálogo
		add(bottomPanel, BorderLayout.SOUTH);
		
		// establece el tamaño del diálogo a su contenido
		pack();
	}

	/**
	 * Muestra el diálogo con el formulario del nuevo atleta.
	 * @return el resultado de la operación.
	 */
	public int showDialog(Window parent) {
		setLocationRelativeTo(parent);
		setVisible(true);
		return result;
	}
	
	/**
	 * Obtiene el nuevo atleta creado a partir de los datos 
	 * del formulario.
	 * @throws FormDataNotValid si existe algún error en el formulario
	 * @return el athleta creado a partir de los datos del formulario
	 */
	public Athlete getAhtlete() throws FormDataNotValid {
		return formPanel.getAthlete();
	}
}
