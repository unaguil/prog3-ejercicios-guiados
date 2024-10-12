package gui.main;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import domain.Athlete;
import domain.Athlete.Genre;
import domain.Medal;
import domain.Medal.Metal;

/**
 * Ventana principal de la aplicación.
 */
public class MainWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<Athlete> sampleAthletes = new ArrayList<>(Arrays.asList(
			new Athlete(1111111, "Apellido, Nombre 1", Genre.FEMALE, "Country 1", LocalDate.of(1990, 12, 15)),
			new Athlete(2222222, "Apellido, Nombre 2", Genre.FEMALE, "Country 2", LocalDate.of(1995, 5, 20)),
			new Athlete(3333333, "Apellido, Nombre 3", Genre.MALE, "Country 1", LocalDate.of(1993, 1, 30)),
			new Athlete(4444444, "Apellido, Nombre 4", Genre.MALE, "Country 3", LocalDate.of(1994, 3, 29)),
			new Athlete(5555555, "Apellido, Nombre 5", Genre.FEMALE, "Country 4", LocalDate.of(1998, 7, 9))
	));

	private List<String> countries = List.of("Country 1", "Country 2", "Country 3", "Country 4");

	private Map<Integer, List<Medal>> medalsPerAthlete = Map.of(
			sampleAthletes.get(0).getCode(), new ArrayList<>(Arrays.asList(
					new Medal(Metal.SILVER, LocalDate.of(2024, 7, 29), sampleAthletes.get(0), "Discipline 1"),
					new Medal(Metal.GOLD, LocalDate.of(2024, 7, 30), sampleAthletes.get(0), "Discipline 2"))
			),
			sampleAthletes.get(1).getCode(), new ArrayList<>(Arrays.asList(
					new Medal(Metal.BRONZE, LocalDate.of(2024, 7, 29), sampleAthletes.get(1), "Discipline 1"),
					new Medal(Metal.GOLD, LocalDate.of(2024, 8, 2), sampleAthletes.get(1), "Discipline 3"))
			),
			sampleAthletes.get(2).getCode(), new ArrayList<>(Arrays.asList(
					new Medal(Metal.SILVER, LocalDate.of(2024, 8, 5), sampleAthletes.get(2), "Discipline 4"))
			)
	);

	private AthleteListCellRenderer athleteListCellRenderer; // referencia al renderer de la lista de atletas
	private FilterListModel<Athlete> jListModelAthletes; // referencia al modelo de datos de la lista
	private JList<Athlete> jListAthletes; // referencia al JList de atletas
	private AthleteFormPanel formAthletes; // referencia al formulario (JPanel) de atletas
	private JButton removeAthletesButton; // referencia al botón de eliminar athletas

	private MedalsTableModel medalsTableModel; // referencia al modelo de datos de la tabla
	private JTable medalsJTable; // referencia a la tabla de medallas

	public MainWindow() {
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // comportamiento al cerrar
		setTitle("JJ.OO. Paris 2024"); // título de la ventana
		setSize(640, 480); // tamaño inicial
		setLocationRelativeTo(null); // situa la ventana en el centro de la pantalla

		// vamos a añadir un escuchador de eventos de la ventana
		// para conocer cuándo el usuario quiere cerrarla y abrir
		// un mensaje de confirmación en su lugar
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				// este método es llamado cuando el usuario intenta cerrar la ventana
				confirmWindowClosing(); // mostramos el dialogo de confirmación
			}

		});

		// creamos el menu de la ventana
		createWindowMenu();

		// creamos un modelo de datos para instancias Athlete que son las que maneja
		// la aplicación, así nos evitamos convertir de Athlete a String y viceversa
		// vamos a utilizar un modelo de datos propio al que se le puede especificar
		// un filtro cuando sea necesario
		jListModelAthletes = new FilterListModel<>(sampleAthletes);
		
		// instanciamos y añadimos un JList en la parte WEST del BorderLayout
		// usamos un JScrollPane para permitir el scroll vertical
		jListAthletes = new JList<Athlete>(jListModelAthletes);
		jListAthletes.setFixedCellWidth(200); // anchura fija del JList
		
		athleteListCellRenderer = new AthleteListCellRenderer();
		jListAthletes.setCellRenderer(athleteListCellRenderer);

		// registramos un escuchador en la lista para actualizar el panel de la derecha
		// con el atleta seleccionado en cada momento
		// podemos usar una expresión lambda ya que la interfaz ListSelectionListener
		// es funcional
		jListAthletes.addListSelectionListener(e -> {
			// solamente vamos a procesar el último evento de la selección en el JList
			// esto evita procesar dos veces cada selección de items
			if (!e.getValueIsAdjusting()) {
				// comprobamos que haya algún atleta seleccionado
				if (jListAthletes.getSelectedIndices().length > 0) {
					// obtenemos el atleta seleccionado del JList
					Athlete selectedAthlete = jListAthletes.getSelectedValue();
					// lo mostramos en el formulario de la derecha
					formAthletes.setAthlete(selectedAthlete);
	
					// establecemos también los datos a mostrar en la tabla de medallas
					// obteniendo la lista de medallas del atleta seleccionado del mapa
					// si el atleta no tiene medallas usamos una lista vacía
					List<Medal> medals = medalsPerAthlete.getOrDefault(selectedAthlete.getCode(), Collections.emptyList());
					medalsTableModel.updateMedals(medals);
				} else {
					// en caso contrario limpiamos el formulario de atletas
					formAthletes.setAthlete(null);
				}
			}
		});

		// vamos a registrar otro escuchador de selección para habilitar
		// deshabilitar el botón de eliminar atleta
		jListAthletes.addListSelectionListener(e -> {
			// procesamos el último evento de la cadena
			if (!e.getValueIsAdjusting()) {
				// si hay algún atleta seleccionado habilitamos el bóton
				// o lo deshabilitamos en caso contrario
				removeAthletesButton.setEnabled(jListAthletes.getSelectedIndices().length > 0);
			}
		});

		JScrollPane scrollJListAthletes = new JScrollPane(jListAthletes);

		// panel izquierdo de la ventana que contiene el scrollpane de
		// la lista y un panel para botones en la parte inferior
		// con el botón de eliminación de atletas seleccionados
		// también contiene un campo de texto para filtrar la lista
		JPanel leftPanel = new JPanel(new BorderLayout());
		
		// creamos un campo para filtrar la lista de atletas
		JTextField filterTextField = new JTextField("");
		leftPanel.add(filterTextField, BorderLayout.NORTH);
		
		// instancia de un Predicate que se encarga de determinar si un atleta
		// cumple con el criterio de búsqueda especificado en el campo de texto
		// si el nombre del atleta contiene el texto del campo de filtro
		Predicate<Athlete> filter = new Predicate<Athlete>() {
			
			@Override
			public boolean test(Athlete t) {
				return t.getName().toLowerCase().contains(filterTextField.getText().toLowerCase());
			}
		};
			
		// escuchador de eventos para actualizar el filtro del modelo de datos
		filterTextField.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				jListModelAthletes.setFilter(filter); // aplicar el nuevo filtro
				// establecemos el texto a resaltar en el renderer de la lista
				athleteListCellRenderer.setHighLightedText(filterTextField.getText());
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				jListModelAthletes.setFilter(filter); // aplicar el nuevo filtro
				// establecemos el texto a resaltar en el renderer de la lista
				athleteListCellRenderer.setHighLightedText(filterTextField.getText());
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				// no hacemos nada en este caso
			}
		});
		
		leftPanel.add(scrollJListAthletes, BorderLayout.CENTER);

		JPanel buttonsPanel = new JPanel();
		JButton newAthleteButton = new JButton("Añadir");
		buttonsPanel.add(newAthleteButton);
		newAthleteButton.addActionListener(e -> showNewAthleteDialog());

		removeAthletesButton = new JButton("Eliminar");
		removeAthletesButton.setEnabled(false); // botón deshabilitado por defecto
		buttonsPanel.add(removeAthletesButton);
		leftPanel.add(buttonsPanel, BorderLayout.SOUTH);
		removeAthletesButton.addActionListener(e -> showRemoveAthletesDialog());

		add(leftPanel, BorderLayout.WEST); // añadimos el scroll a la ventana

		// añadimos un JTabbedPane con dos tabs a la zona central del BorderLayout
		JTabbedPane jTabbedPane = new JTabbedPane();

		// añadimos el formulario de atletas al panel de la derecha en "Datos"
		formAthletes = new AthleteFormPanel(countries);
		formAthletes.setEditable(false); // formulario en modo no editable
		jTabbedPane.addTab("Datos", formAthletes);

		jTabbedPane.addTab("Medallas", createMedalPanel());
		add(jTabbedPane, BorderLayout.CENTER);

		// añadimos un evento de teclado a la lista de atletas
		jListAthletes.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				// si el usuario ha pulsado la tecla SUPR
				if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					// se borran los atletas si hay seleccionados
					if (jListAthletes.getSelectedIndices().length > 0) {
						showRemoveAthletesDialog();
					}
				}

				// si el usuario pulsa la tecla CTRL + M
				if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_M) {
					// se comprueba que algún atleta seleccionado
					if (jListAthletes.getSelectedIndices().length > 0) {
						// se cambia al panel de medallas
						jTabbedPane.setSelectedIndex(1);
						
						// se crea una nueva medalla para el atleta seleccionado
						// y se añade al modelo de datos de la tabla
						Athlete selectedAthlete = jListAthletes.getSelectedValue();
						Medal newMedal = new Medal(Metal.BRONZE, LocalDate.now(), selectedAthlete, "Nueva disciplina");
						medalsTableModel.addMedal(newMedal);
						
						// seleccionamos en la tabla la nueva medalla
						// para que el usuario pueda editarla
						int row = medalsTableModel.getRowCount() - 1;
						medalsJTable.setRowSelectionInterval(row, row);
						medalsJTable.editCellAt(row, 0);
					}
				}
				
				// si el usuario pulsa la tecla CTRL + A
				if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_A) {
					// muestra el diálogo de para añadir un nuevo atleta
					showNewAthleteDialog();
				}
				
				// si el usuario pulsa la tecla ESC
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    // se deselecciona cualquier atleta seleccionado
					jListAthletes.clearSelection();
				}
			}
		});

		setVisible(true); // hacemos visible la ventana
	}

	// crea el panel que contiene el JTable de medallas
	private JComponent createMedalPanel() {
		// creamos el modelo de datos de la tabla
		medalsTableModel = new MedalsTableModel();
		// creamos la tabla de medallas y le asignamos el modelo de datos
		medalsJTable = new JTable(medalsTableModel);

		// vamos a establecer el tamaño de las columnas de tipo y fecha para ajustar
		// mejor la tabla
		medalsJTable.getColumnModel().getColumn(0).setMaxWidth(60);
		medalsJTable.getColumnModel().getColumn(1).setMaxWidth(80);

		// configuramos el renderer de la columna de metal de la medalla
		medalsJTable.getColumnModel().getColumn(0).setCellRenderer(new MetalTableCellRenderer());

		// este renderer se lo vamos a añadir a toda la tabla, pero se va filtrar por
		// tipo de dato
		// de tal manera que únicamente se aplique a las columnas cuyo tipo es LocalDate
		// de acuerdo
		// al método getColumnClass del modelo de datos
		// vamos a usar el formato localizado de fecha "2 ago 2024"
		medalsJTable.setDefaultRenderer(LocalDate.class,
				new DateTableCellRenderer(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));

		// establecemos el editor para la primera columna de la tabla (tipo de metal)
		// que es un JComboBox con los valores del enum Metal
		medalsJTable.getColumnModel().getColumn(0).setCellEditor(new MetalTableCellEditor());

		// establecemos el editor para la segunda columna de la tabla (fecha)
		medalsJTable.getColumnModel().getColumn(1).setCellEditor(new DateTableCellEditor());

		// añadimos la tabla a un panel de scroll y lo devolvemos
		return new JScrollPane(medalsJTable);
	}

	// método para crear el menú de la ventana
	private void createWindowMenu() {
		// creamos la barra principal y asignamos a la ventana
		JMenuBar jMenuBar = new JMenuBar();
		setJMenuBar(jMenuBar);

		// creamos el menú "Fichero"
		JMenu fileMenu = new JMenu("Fichero");
		fileMenu.setMnemonic(KeyEvent.VK_F);

		// creamos los items del menu
		JMenuItem newAhtleteMenuItem = new JMenuItem("Nuevo atleta...");
		newAhtleteMenuItem.setMnemonic(KeyEvent.VK_N);
		// abrimos el cuadro de diálogo de nuevo atleta y
		// procesamos el resultado de la operación
		newAhtleteMenuItem.addActionListener(e -> showNewAthleteDialog());

		fileMenu.add(newAhtleteMenuItem);

		fileMenu.addSeparator();

		JMenuItem importMenuItem = new JMenuItem("Importar...");
		importMenuItem.setMnemonic(KeyEvent.VK_I);
		fileMenu.add(importMenuItem);

		JMenuItem exportMenuItem = new JMenuItem("Exportar...");
		exportMenuItem.setMnemonic(KeyEvent.VK_E);
		fileMenu.add(exportMenuItem);

		fileMenu.addSeparator();

		JMenuItem exitMenuItem = new JMenuItem("Salir");
		exitMenuItem.setMnemonic(KeyEvent.VK_S);
		exitMenuItem.addActionListener(e -> confirmWindowClosing());
		fileMenu.add(exitMenuItem);

		jMenuBar.add(fileMenu);
	}

	// muestra y procesa el diálogo para añadir un nuevo atleta
	private void showNewAthleteDialog() {
		NewAthleteDialog newAthleteDialog = new NewAthleteDialog(countries);
		int result = newAthleteDialog.showDialog(this);
		if (result == JOptionPane.OK_OPTION) {
			// si el usuario ha pulsado la opción guardar
			// obtenemos el nuevo atleta y lo añadimos a la lista de atletas
			try {
				jListModelAthletes.addElement(newAthleteDialog.getAhtlete());
			} catch (FormDataNotValid e) {
				// no hacemos nada porque sabemos que el atleta debe
				// ser válido en este punto
			}
		}
	}

	// muestra y procesa un diálogo de borrado de atletas seleccionados
	private void showRemoveAthletesDialog() {
		int[] selectedIndices = jListAthletes.getSelectedIndices();
		// primero mostramos un diálogo de confirmación al usuario
		int result = JOptionPane.showConfirmDialog(this,
				String.format("¿Está seguro de querer eliminar %d atletas seleccionados", selectedIndices.length),
				"Eliminar atletas", JOptionPane.YES_NO_OPTION);
		if (result == JOptionPane.YES_OPTION) {
			// si la respuesta del usuario es afirmativa
			// obtenemos la lista de índices seleccionados en el JList de atletas
			// y los borramos de su modelo de datos
			for (int i = selectedIndices.length - 1; i >= 0; i--) {
				jListModelAthletes.remove(selectedIndices[i]);
			}
		}
	}

	// método que muestra al usuario un diálogo de
	// confirmación antes de cerrar la ventana
	private void confirmWindowClosing() {
		int result = JOptionPane.showConfirmDialog(MainWindow.this, "¿Seguro que desea salir?", "Salir",
				JOptionPane.YES_NO_OPTION);

		if (result == JOptionPane.YES_OPTION) {
			// el usuario está seguro que desea salir
			System.exit(0); // terminamos el programa
		}
	}
}
