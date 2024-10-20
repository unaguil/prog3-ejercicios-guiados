package gui.main;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import domain.Athlete;
import domain.Athlete.Genre;
import domain.Medal;
import domain.Medal.Metal;
import gui.main.dialogs.NewAthleteDialog;
import gui.main.editors.DateTableCellEditor;
import gui.main.editors.MetalTableCellEditor;
import gui.main.form.AthleteFormPanel;
import gui.main.form.FormDataNotValid;
import gui.main.models.FilterListModel;
import gui.main.models.MedalsTableModel;
import gui.main.renderers.AthleteListCellRenderer;
import gui.main.renderers.DateTableCellRenderer;
import gui.main.renderers.MetalTableCellRenderer;
import net.DescriptionCache;
import net.WebScraper;
import net.WebScraperException;

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
					new Medal(Metal.SILVER, LocalDate.of(2024, 7, 29), sampleAthletes.get(0), "Judo"),
					new Medal(Metal.GOLD, LocalDate.of(2024, 7, 30), sampleAthletes.get(0), "Cycling road"))
			),
			sampleAthletes.get(1).getCode(), new ArrayList<>(Arrays.asList(
					new Medal(Metal.BRONZE, LocalDate.of(2024, 7, 29), sampleAthletes.get(1), "Hockey"),
					new Medal(Metal.GOLD, LocalDate.of(2024, 8, 2), sampleAthletes.get(1), "Wrestling"))
			),
			sampleAthletes.get(2).getCode(), new ArrayList<>(Arrays.asList(
					new Medal(Metal.SILVER, LocalDate.of(2024, 8, 5), sampleAthletes.get(2), "Tennis"))
			)
	);

	private AthleteListCellRenderer athleteListCellRenderer; // referencia al renderer de la lista de atletas
	private FilterListModel<Athlete> jListModelAthletes; // referencia al modelo de datos de la lista
	private JList<Athlete> jListAthletes; // referencia al JList de atletas
	private AthleteFormPanel formAthletes; // referencia al formulario (JPanel) de atletas
	private JButton removeAthletesButton; // referencia al botón de eliminar athletas

	private MedalsTableModel medalsTableModel; // referencia al modelo de datos de la tabla
	private JTable medalsJTable; // referencia a la tabla de medallas
	private JEditorPane contextualInfoEditorPane; // referencia al JEditorPane de información contextual
	
	private JPanel bottomPanel; // referencia al panel inferior de la ventana
	private JProgressBar progressBar; // referencia a la barra de progreso	
	private Thread exportThread; // referencia al hilo de exportación de disciplinas
	
	private DescriptionCache descriptionCache; // referencia a la cache de descripciones de disciplinas

	public MainWindow() {
		// creamos e iniciamos la cache de descripciones de disciplinas
		descriptionCache = DescriptionCache.getInstance();
		
		// vamos a obtener todas los nombres de disciplinas de los atletas
		// para ir descargando sus descripciones desde la página web en segundo plano
		Set<URL> allDisciplines = medalsPerAthlete.values().stream()
			.flatMap(List::stream)
            .map(m -> {
				try {
					return WebScraper.getURL(m.getDiscipline());
				} catch (WebScraperException e) {
					throw new RuntimeException(e);
				}
			})
            .collect(Collectors.toSet());
		
		descriptionCache.addURLs(allDisciplines);
		descriptionCache.startDownload();
		
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

		// panel general del tab de medallas que contiene la tabla de medallas
		// y un JTextArea para mostrar información contextual sobre la información
		// seleccionada por el usuario en la tabla
		JPanel medalsTabPanel = new JPanel(new GridLayout(2, 1));
		medalsTabPanel.add(createMedalPanel());
		
		// añadimos un JTextArea para mostrar información contextual con
		// formato HTML
		contextualInfoEditorPane = new JEditorPane();
		contextualInfoEditorPane.setEditable(false);
		contextualInfoEditorPane.setContentType("text/html");
		medalsTabPanel.add(new JScrollPane(contextualInfoEditorPane));
		
		jTabbedPane.addTab("Medallas", medalsTabPanel);
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
		
		// añadimos el panel con la barra de progreso y el botón cancelar
		// a la parte inferior de la ventana pero inicialmente es invisible
		bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
		bottomPanel.setVisible(false);
		
    	JLabel taskLabel = new JLabel("Exportando disciplinas...");
    	bottomPanel.add(taskLabel);
    	bottomPanel.add(Box.createHorizontalStrut(5));
    	
    	progressBar = new JProgressBar();
    	progressBar.setStringPainted(true);
  
    	bottomPanel.add(progressBar, BorderLayout.CENTER);
    	bottomPanel.add(Box.createHorizontalStrut(20));
    	
    	JButton cancelButton = new JButton("Cancelar");
    	bottomPanel.add(cancelButton, BorderLayout.EAST);
    	
    	// al pulsar el botón de cancelar se interrumpe el hilo
    	// si es que ha sido lanzado (es distinto de null)
        cancelButton.addActionListener(a -> {
			if (exportThread != null) {
				System.out.println("Interrumpiendo el hilo de descarga");
				exportThread.interrupt();
				exportThread = null;
			}
        });
        
        // añadimos el panel invisible a la ventana en la parte inferior
        add(bottomPanel, BorderLayout.SOUTH);
		
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
		
		// añadimos un listener de ratón para mostrar información contextual
		// sobre la celda seleccionada de la tabla
		medalsJTable.addMouseListener(new MouseAdapter() {
			@Override
            public void mouseClicked(MouseEvent e) {
                // obtenemos la fila y columna de la celda seleccionada
                int row = medalsJTable.rowAtPoint(e.getPoint());
                int col = medalsJTable.columnAtPoint(e.getPoint());
                
                // unicamente mostramos información contextual si la celda seleccionada
                // corresponde con la columna de disciplina de la tabla, en caso contrario
                // mostramos un texto por defecto
                if (col == 2) {
                	// obtenemos el nombre de la disciplina de la celda seleccionada
                	String discipline = medalsJTable.getValueAt(row, col).toString();
                	// obtenemos la URL de la disciplina
                	try {
	                	URL url = WebScraper.getURL(discipline);
	                	
	                	// usamos la cache de descripciones para intentar obtener la descripción
	                	descriptionCache.getDescriptionAsync(url,
	            			paragraphs -> {
	            				// si la descripción se ha podido obtener se llama este callback
	    						String info = "<html><h1>" + discipline + "</h1>";
	    						info += "<p>" + String.format("URL: <a href='%s'>%s</a>", url, url) + "</p>";
	    						for (String paragraph : paragraphs) {
	    							info += "<p>" + paragraph + "</p>";
	    						}
	    						info += "</html>";
	    						// actualizamos el JTextArea con la información contextual
		                    	contextualInfoEditorPane.setText(info);
		                    	// situar el cursos en la parte superior del JTextArea
		                    	contextualInfoEditorPane.setCaretPosition(0);
	            			},
	            			error -> {
	            				// si no se ha podido obtener la descripción se llama este callback
	            				String info = "<html><h1>" + discipline + "</h1>";
	            				info += "<p>" + String.format("URL: <a href='%s'>%s</a>", url, url) + "</p>";
	            				info += "<p>No se ha podido obtener la información</p>";
	            				info += "</html>";
	            				// actualizamos el JTextArea con la información contextual
	                        	contextualInfoEditorPane.setText(info);
	                        	// situar el cursos en la parte superior del JTextArea
	                        	contextualInfoEditorPane.setCaretPosition(0);
	            			},
	            			5000 // tiempo máximo de espera en milisegundos
	                	);
                	} catch (WebScraperException ex) {
                		System.out.println("Error descargando la información. " + ex.getMessage());
                	}
                }
            }
		});

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
		exportMenuItem.addActionListener(e -> {
            // mostramos un diálogo de selección de fichero
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("txt", "Ficheros de texto"));
            fileChooser.setDialogTitle("Exportar disciplinas");
            int result = fileChooser.showSaveDialog(MainWindow.this);
            if (result == JFileChooser.APPROVE_OPTION) {
            	
            	
                File exportFile = fileChooser.getSelectedFile();
                exportSelectedDisciplines(exportFile, progressBar);
            }
        });
		
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
			// antes de salir paramos el hilo de exportación si está en ejecución
			if (exportThread != null) {
				exportThread.interrupt();
				exportThread = null;
			}
			
			// detenemos también la descarga de descripciones de disciplinas
			descriptionCache.stopDownload();
			
			// el usuario está seguro que desea salir
			System.exit(0); // terminamos el programa
		}
	}
	
	// exporta las descripciones de las disciplinas en las que han participado
	// los atletas seleccionados en el JList de atletas a un fichero de texto
	// la tarea se lanza en un hilo para no bloquear la interfaz de usuario
	// el método devuelve la referencia al thread que se ha lanzado
	private void exportSelectedDisciplines(File exportFile, JProgressBar progressBar) {
		// obtenemos los atletas seleccionados en el JList
		List<Athlete> selectedAthletes = jListAthletes.getSelectedValuesList();
		
		// obtenemos el conjunto de todas las disciplinas a exportar
		// para poder establecer el máximo de la barra de progreso
		// vamos a utilizar un stream pero se puede usar un bucle
		Set<String> disciplines = selectedAthletes.stream()
				.filter(a -> medalsPerAthlete.containsKey(a.getCode()))
				.flatMap(a -> medalsPerAthlete.get(a.getCode()).stream())
				.map(Medal::getDiscipline).collect(Collectors.toSet());
		
		// establecemos el máximo de la barra de progreso
		progressBar.setMaximum(disciplines.size());
		
		// configuramos el hilo de ejecución para escribir las disciplinas
		exportThread = new Thread(() -> {
			System.out.println("Exportando disciplinas...");
			// abrimos el fichero en el que se van a exportar los datos
			try (FileWriter writer = new FileWriter(exportFile)) {
				// utilizamos un iterador para recorrer las disciplinas a exportar
				Iterator<String> iterator = disciplines.iterator();
				while (iterator.hasNext() && !Thread.interrupted()) {
					String discipline = iterator.next();
                    // escribimos la disciplina y su descripción en el fichero
					writer.write(discipline + "\n");
					
					try {
						// obtenemos la descripción de la disciplina de la página web
						// y escribimos todos sus párrafos en el fichero
						List<String> paragraphs = WebScraper.getDescription(WebScraper.getURL(discipline));
						for (String paragraph : paragraphs) {
                            writer.write(paragraph + "\n");
                        }
					}
					catch (WebScraperException e) {
                        // si hay problemas con la descarga se imprime el mensaje de error
                        writer.write("No se ha podido obtener la información\n");
                    } catch (InterruptedException e) {
                		// reestablecemos el flag interrupt() del hilo actual para que el bucle se detenga
                    	Thread.currentThread().interrupt();
                    }
					writer.write("\n");
						
					// actualizamos la barra de progreso
					// debemos utilizar SwingUtilities.invokeLater para actualizar la barra
					// de progreso ya que estamos en un hilo diferente al hilo de Swing
					SwingUtilities.invokeLater(() -> progressBar.setValue(progressBar.getValue() + 1));
				}
				
				// indicamos si ha sido el usuario el que han cancelado el hilo de exportación
				// lo sabemos porque todavía existen descripciones a exportar
				if (iterator.hasNext()) {
					System.out.println("El hilo de exportación ha sido detenido por el usuario");
				}
				
				// volvemos a hacer invisible el panel inferior
				SwingUtilities.invokeLater(() -> bottomPanel.setVisible(false));
			} catch (IOException e) {
				// mensaje de error si hay problemas con el fichero
				// se utiliza SwingUtilities.invokeLater para mostrar el mensaje debido a que
				// estamos en un hilo diferente al hilo de Swing
				SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Error al exportar las disciplinas", "Error", JOptionPane.ERROR_MESSAGE));
			}
			
			System.out.println("Terminada la exportación de disciplinas");
		});
		
		exportThread.start(); // iniciamos el thread
		
		// hacemos visible el panel inferior
		bottomPanel.setVisible(true);
	}
}