package gui.main;

import java.awt.FlowLayout;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.Border;

/**
 * Formulario que permite visualizar o editar los datos de un atleta. Se
 * implementa a partir de un JPanel para que pueda ser reutilizable en varias
 * partes de la aplicación.
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

	private JFormattedTextField codeTextField;
	private JTextField nameTextField;
	private JComboBox<String> countryComboBox;
	private JFormattedTextField birthDateTextField;
	private JRadioButton maleRadioButton, femaleRadioButton;

	public AthleteFormPanel() {
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
		genrePanel.add(maleRadioButton);
		femaleRadioButton = new JRadioButton("Mujer");
		genrePanel.add(femaleRadioButton);

		// grupo para que los radio buttons sean exclusivos
		ButtonGroup genreGroup = new ButtonGroup();
		genreGroup.add(femaleRadioButton);
		genreGroup.add(maleRadioButton);

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

		// array de nombres de países para las pruebas
		String[] countries = new String[] { "Country 1", "Country 2", "Country 3", "Country 4" };

		// vamos a utilizar un JComboBox para el selector de países
		countryComboBox = new JComboBox<String>(countries);
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

	// programa principal para testear el panel en una ventana propia
	// esto permite que pueda ser desarrollado sin depender de otra UI
	public static void main(String[] args) {
		AthleteFormPanel testForm = new AthleteFormPanel();

		// creamos una ventana de prueba para visualizar el panel
		JFrame jFrame = new JFrame("Ventana de prueba");
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.add(testForm);

		jFrame.pack();
		jFrame.setVisible(true);
	}
}
