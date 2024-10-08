package gui.main;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

/**
 * Ventana principal de la aplicación.
 */
public class MainWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String[] sampleAthletes = new String[] { "Atleta 1", "Atleta 2", "Atleta 3", "Atleta 4", "Atleta 5",
			"Atleta 6", "Atleta 7", "Atleta 8", "Atleta 9", "Atleta 10", "Atleta 11", "Atleta 12", "Atleta 13",
			"Atleta 14", "Atleta 15", "Atleta 16", "Atleta 17", "Atleta 18", "Atleta 19", "Atleta 20", "Atleta 21",
			"Atleta 22", "Atleta 23", "Atleta 24", "Atleta 25", "Atleta 26", "Atleta 27", "Atleta 28", "Atleta 29",
			"Atleta 30" };

	private JList<String> jListAthletes; // referencia al JList de atletas

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

		// instanciamos y añadimos un JList en la parte WEST del BorderLayout
		// usamos un JScrollPane para permitir el scroll vertical
		jListAthletes = new JList<String>(sampleAthletes);
		jListAthletes.setFixedCellWidth(200); // anchura fija del JList
		
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
