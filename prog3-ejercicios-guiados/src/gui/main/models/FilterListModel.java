package gui.main.models;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import javax.swing.AbstractListModel;

/**
 * Clase genérica que se encarga de filtrar los elementos de una lista de
 * atletas de acuerdo a un criterio de búsqueda especificado con un
 * 
 * El modelo se va a implementar de manera genérica para que pueda ser utilizado
 * por cualquier tipo de objeto que se desee filtrar.
 */
public class FilterListModel<E> extends AbstractListModel<E> {

	private static final long serialVersionUID = 1L;

	private List<E> dataList; // referencia a la lista interna de datos
	
	private List<E> filteredList = new ArrayList<E>(); // lista de datos filtrados
	private Predicate<E> filter; // filtro a aplicar a la lista de datos

	public FilterListModel(List<E> dataList) {
		this.dataList = dataList;
		
		// el filtro inicial es true para mostrar todos los elementos
		setFilter(e -> true);
	}

	/**
	 * Método que se encarga de establecer el filtro a aplicar a la lista de datos.
	 * El filtro se establece como un Predicate que se encarga de determinar si un
	 * elemento cumple con el criterio de búsqueda especificado.
	 */
	public void setFilter(Predicate<E> filter) {
		this.filter = filter;
		updateFilteredList(); // aplicar el filtro a la lista de datos
	}

	// método interno que aplica el filtro a la lista
	// original para obtener la lista de datos filtrados
	private void updateFilteredList() {
		filteredList.clear(); // limpiar la lista de datos filtrados
		
		// filtrar la lista de datos original en base al criterio de búsqueda
		// especificado por el filtro y agregar los elementos que cumplen con
		// el criterio al listado de datos filtrados
		for (E e : dataList) {
			if (filter.test(e)) {
				filteredList.add(e);
			}
		}
		fireContentsChanged(this, 0, getSize() - 1); // notificar al JList del cambio producido
	}

	@Override
	public int getSize() {
		// número de elementos en la lista a mostrar
		return filteredList.size();
	}

	@Override
	public E getElementAt(int index) {
		// obtener el elemento en la posición index
		return filteredList.get(index);
	}

	/**
	 * Método que se encarga de agregar un elemento a la lista de datos y notificar
	 * al JList del cambio producido en los datos
	 * 
	 * @param element elemento a agregar
	 */
	public void addElement(E e) {
		dataList.add(e); // nuevo elemento a la lista
		updateFilteredList(); // aplicar el filtro a la lista de datos

		// notificar al JList del cambio producido
		fireIntervalAdded(this, dataList.size() - 1, dataList.size() - 1);
	}

	/**
	 * Método que se encarga de remover un elemento de la lista de datos y notificar
	 * al JList del cambio producido en los datos
	 * 
	 * @param index posición del elemento a remover
	 */
	public void remove(int i) {
		dataList.remove(i); // eliminar el elemento en la posición index
		updateFilteredList(); // aplicar el filtro a la lista de datos

		// notificar al JList del cambio producido
		fireIntervalRemoved(this, i, i);
	}

}
