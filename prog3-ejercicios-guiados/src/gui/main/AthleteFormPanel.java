package gui.main;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.Border;

import domain.Athlete;
import domain.Athlete.Genre;

/**
 * Formulario que permite visualizar o editar los datos de un atleta. Se
 * implementa a partir de un JPanel para que pueda ser reutilizable en varias
 * partes de la aplicación.
 * 
 * Esta clase proporciona un método al programador para establecer qué instancia
 * de Athleta se va a mostrar en el panel, otro método para obtener los datos
 * introducidos en los JTexFields y un método para establecer si el panel está
 * en modo edición o modo visualización (no editable).
 * 
 * Como trabajamos con orientación a objetos podemos extender y crear
 * componentes personalizados para posteriormente reutilizarlos además de
 * encapsular funcionalidad.
 */
public class AthleteFormPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String DATE_FORMAT = "dd/MM/yyyy"; // formato de fecha
	private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

	private boolean editable = true; // indica si los campos del formulario son editables

	private JFormattedTextField codeTextField;
	private JTextField nameTextField;
	private JComboBox<String> countryComboBox;
	private JFormattedTextField birthDateTextField;
	private JRadioButton maleRadioButton, femaleRadioButton;
	private ButtonGroup genreButtonGroup;

	public AthleteFormPanel(List<String> countries) {
		// vamos a cambiar el layout de este panel a un BoxLayout
		// para distribuir los componentes verticalemente
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		// campos de datos de código consistente en un JLabel y un JTextField
		// para el código del atleta actual alineados a la izquierda en su panel.
		// el texto dentro del JTextField para el código debe estar alineado a la der.
		JLabel codeJLabel = new JLabel("Código");
		codeTextField = new JFormattedTextField(new DecimalFormat("#######"));
		codeTextField.setColumns(5);
		codeTextField.setHorizontalAlignment(JTextField.RIGHT);

		JPanel codePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		codePanel.add(codeJLabel);
		codePanel.add(codeTextField);

		// campo de datos del nombre consistente en un JLabel y un JTextField
		// situados en su propio panel y alineados a la izquierda en este y
		// separados 10 píxeles en horizontal
		JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel nameLabel = new JLabel("Nombre");
		nameTextField = new JTextField(20);
		namePanel.add(nameLabel);
		namePanel.add(nameTextField);

		// el género vamos a seleccionarlo con radio buttons
		JPanel genrePanel = new JPanel();
		// creamos los radio buttons y los añadimos al panel anterior
		maleRadioButton = new JRadioButton("Hombre");
		maleRadioButton.setActionCommand(Genre.MALE.toString()); // código interno
		genrePanel.add(maleRadioButton);
		
		femaleRadioButton = new JRadioButton("Mujer");
		femaleRadioButton.setActionCommand(Genre.FEMALE.toString()); // código interno
		genrePanel.add(femaleRadioButton);

		// grupo para que los radio buttons sean exclusivos
		genreButtonGroup = new ButtonGroup();
		genreButtonGroup.add(femaleRadioButton);
		genreButtonGroup.add(maleRadioButton);
		
		// campo de datos de fecha de nacimiento similar al anterior
		JPanel birthDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel birthDateLabel = new JLabel("Nacimiento");

		DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		birthDateTextField = new JFormattedTextField(dateFormat);
		birthDateTextField.setColumns(6);
		birthDateTextField.setHorizontalAlignment(JTextField.RIGHT);
		birthDatePanel.add(birthDateLabel);
		birthDatePanel.add(birthDateTextField);
		
		// campo de datos del país similar al anterior
		JPanel countryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel countryLabel = new JLabel("País");

		// vamos a utilizar un JComboBox para el selector de países
		countryComboBox = new JComboBox<String>(countries.toArray(String[]::new));
		countryPanel.add(countryLabel);
		countryPanel.add(countryComboBox);

		// añadimos los componentes al panel
		add(codePanel);
		add(namePanel);
		add(genrePanel);
		add(birthDatePanel);
		add(countryPanel);

		// vamos a añadir un borde al panel para resaltar toda la zona de datos
		Border formBorder = BorderFactory.createTitledBorder("Atleta");
		setBorder(formBorder);
	}

	/**
	 * Establece el atleta cuyos datos se visualizan en el formulario.
	 * Si el atleta es null, el formulario se queda en blanco
	 * 
	 * @param athlete atleta cuyos datos se visualizan en el formulario
	 */
	public void setAthlete(Athlete athlete) {
		if (athlete == null) {
			clear();
		} else {
			// rellenamos los campos con los datos del atleta
			codeTextField.setText(Integer.toString(athlete.getCode()));
			nameTextField.setText(athlete.getName());
	
			// convertimos la fecha al formato especificado para visualizar en el form
			birthDateTextField.setText(athlete.getBirthdate().format(dateFormatter));
	
			// seleccionamos el radio button según el género del atleta
			switch (athlete.getGenre()) {
			case MALE:
				maleRadioButton.setSelected(true);
				break;
	
			case FEMALE:
				femaleRadioButton.setSelected(true);
				break;
			}
	
			// seleccionamos el país del desplegable según el del atleta
			countryComboBox.setSelectedItem(athlete.getCountry());
		}
	}
	
	/**
	 * Limpia todos los campos del formulario y
	 * selecciona el primer país del JComboBox
	 */
	public void clear() {
		codeTextField.setText("");
		nameTextField.setText("");
		birthDateTextField.setText("");
		maleRadioButton.setSelected(false);
		femaleRadioButton.setSelected(false);
		countryComboBox.setSelectedIndex(0);
	}

	/**
	 * Obtiene un nuevo atleta a partir de los datos del formulario.
	 * 
	 * @return un nuevo atleta creado a partir de los datos del formulario
	 */
	public Athlete getAthlete() throws FormDataNotValid {
		// comprobamos si el campo esta vacío o con espacios
		if (codeTextField.getText().isBlank())
			throw new FormDataNotValid("El código no puede ser vacío");
		
		// comprobamos si el campo esta vacío o con espacios
		if (nameTextField.getText().isBlank())
			throw new FormDataNotValid("El nombre no puede estar vacío");
		
		// alguna de las opciones debe estar seleccionada
		if (!maleRadioButton.isSelected() && !femaleRadioButton.isSelected())
			throw new FormDataNotValid("Se debe seleccionar un género");
		
		// comprobamos si el campo esta vacío o con espacios
		if (birthDateTextField.getText().isBlank())
			throw new FormDataNotValid("La fecha no puede ser vacía");
			
		// intentamos construir el objeto con los datos del formulario
		// si hay algún error lanzamos la excepción con el mensaje adecuado
		try {
			return new Athlete(
				Integer.parseInt(codeTextField.getText()),
				nameTextField.getText(),
				Genre.valueOf(genreButtonGroup.getSelection().getActionCommand()),
				countryComboBox.getItemAt(countryComboBox.getSelectedIndex()),
				LocalDate.parse(birthDateTextField.getText(), dateFormatter)
			);
		} catch (NumberFormatException e) {
			throw new FormDataNotValid("Se esperaba un código numérico", e);
		} catch (DateTimeException e) {
			throw new FormDataNotValid("La fecha no tiene el formato esperado", e);
		}
	}

	/**
	 * Establece el modo en el que se encuentra el formulario.
	 * 
	 * @param editable true si el formulario debe ser editable, false si no
	 */
	public void setEditable(boolean editable) {
		this.editable = editable;
		
		// establecemos los componentes editables al estado indicado
		codeTextField.setEditable(editable);
		nameTextField.setEditable(editable);
		birthDateTextField.setEditable(editable);		
		maleRadioButton.setEnabled(editable);
		femaleRadioButton.setEnabled(editable);
		countryComboBox.setEnabled(editable);
	}

	/**
	 * Indica si los campos del panel están habilitados para modificar los datos.
	 * 
	 * @return true si los campos son editables, false en caso contrario
	 */
	public boolean isEditable() {
		return editable;
	}

	// programa principal para testear el panel en una ventana propia
	// esto permite que pueda ser desarrollado sin depender de otra UI
	public static void main(String[] args) {
		// datos de prueba para el panel
		Athlete sampleAthlete = new Athlete(1111111, "Apellido, Nombre 1", Genre.FEMALE, "Country 3",
				LocalDate.of(1990, 12, 15));
		List<String> countries = List.of("Country 1", "Country 2", "Country 3", "Country 4");

		// creamos el panel de prueba y establecemos los datos de ejemplo a visualizar
		AthleteFormPanel testForm = new AthleteFormPanel(countries);
		testForm.setAthlete(sampleAthlete);

		// podemos hacer que el panel no sea editable para probar

		// creamos una ventana de prueba para visualizar el panel
		JFrame jFrame = new JFrame("Ventana de prueba");
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.add(testForm);

		// vamos a añadir un botón en la parte inferior para probar la validación
		// de datos y otro para hablitar/deshabilitar el formulario
		JPanel southPanel = new JPanel();

		// botón para la lanzar la validación del formulario
		JButton validateButton = new JButton("Obtener datos");
		southPanel.add(validateButton);
		validateButton.addActionListener(event -> {
			try {
				// intentamos obtener el athleta con los datos del formulario
				System.out.println("Datos introducidos: " + testForm.getAthlete());
			} catch (FormDataNotValid e) {
				// escribimos por consola los posibles errores 
				System.out.println("Datos del formulario no validos. Motivo: " + e.getMessage());
			}
		});

		// botón que habilita/deshabilita los campos del formulario
		JButton toggleButton = new JButton("Habilitar/deshabilitar");
		southPanel.add(toggleButton);
		toggleButton.addActionListener(e -> testForm.setEditable(!testForm.isEditable()));

		// panel con botones de prueba al sur del BorderLayout
		jFrame.add(southPanel, BorderLayout.SOUTH);

		jFrame.pack();
		jFrame.setVisible(true);
	}
}
