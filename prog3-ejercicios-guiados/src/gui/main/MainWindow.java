package gui.main;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import domain.Athlete;
import domain.Athlete.Genre;

/**
 * Ventana principal de la aplicación.
 */
public class MainWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<Athlete> sampleAthletes = List.of(
		new Athlete(1111111, "Apellido, Nombre 1", Genre.FEMALE, "Country 1", LocalDate.of(1990, 12, 15)),
		new Athlete(2222222, "Apellido, Nombre 2", Genre.FEMALE, "Country 2", LocalDate.of(1995, 5, 20)),
		new Athlete(3333333, "Apellido, Nombre 3", Genre.MALE, "Country 1", LocalDate.of(1993, 1, 30)),
		new Athlete(4444444, "Apellido, Nombre 4", Genre.MALE, "Country 3", LocalDate.of(1994, 3, 29)),
		new Athlete(5555555, "Apellido, Nombre 5", Genre.FEMALE, "Country 4", LocalDate.of(1998, 7, 9))
	);

	private JList<Athlete> jListAthletes; // referencia al JList de atletas

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
		DefaultListModel<Athlete> jListModelAthletes = new DefaultListModel<Athlete>();
		jListModelAthletes.addAll(sampleAthletes);

		// instanciamos y añadimos un JList en la parte WEST del BorderLayout
		// usamos un JScrollPane para permitir el scroll vertical	
		jListAthletes = new JList<Athlete>(jListModelAthletes);
		jListAthletes.setFixedCellWidth(200); // anchura fija del JList
		jListAthletes.setCellRenderer(new AthleteListCellRenderer());

		JScrollPane scrollJListAthletes = new JScrollPane(jListAthletes);
		add(scrollJListAthletes, BorderLayout.WEST); // añadimos el scroll a la ventana

		// añadimos un JTabbedPane con dos tabs a la zona central del BorderLayout
		JTabbedPane jTabbedPane = new JTabbedPane();
		jTabbedPane.addTab("Datos", new JPanel());
		jTabbedPane.addTab("Medallas", new JPanel());
		add(jTabbedPane, BorderLayout.CENTER);

		setVisible(true); // hacemos visible la ventana
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
