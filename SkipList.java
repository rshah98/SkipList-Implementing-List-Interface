import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

/**
 * Skip list implementation of the <tt>List</tt> interface.  Implements
 * only a subset of list operations, and permits all elements, including
 * <tt>null</tt>.  
 *
 * <p>The <tt>size</tt>, <tt>isEmpty</tt>, <tt>clear</tt> operations run in constant
 * time.  The <tt>add</tt>, <tt>remove</tt>, and <tt>get</tt> operations run in 
 * <i>expected logarithmic time</i>. 
 *
 * @author Andriy Pavlovych
 * @see     Collection
 * @see     List
 */
public class SkipList <E> implements List<E> {
	private int size = 0;
	private int listHeight = 1;
	private Node<E> head;
	private Random random;
	private final double p = 0.5;
	private final int MAX_LEVEL = 20; 

	private static class Node<E> {
		E item;
		Node<E> forward;
		Node<E> down;
		int distance;

		Node(E element) {
			this.item = element;
		}
	}

	public SkipList() {
		random = new Random();
		head = new Node <E> (null);
		head.distance = 1;
		head.forward = null;
		head.down = null;
	}

	@Override
	public boolean add(E e) {
		add (size, e);
		return true;
	}

	@Override
	public void add(int index, E element) {
		if (index < 0 || index > size){
			throw new IndexOutOfBoundsException("Index: "+index+", Size: "+size);
		}

		int lvl = randomLevel();

		//grow in height if necessary
		if (lvl > listHeight){ 
			for (int i = lvl; i > listHeight; i--){
				Node <E> node = new Node <> (null);
				node.down = head;
				node.forward = null;
				node.distance = size + 1;
				head = node;
			}
			listHeight = lvl;
		}

		int pos = 0; // pos = pos(x)
		int currentLevel = listHeight;
		Node <E> lastInserted = null;
		//		for (int i = listHeight; i >= 1; i--){
		for (Node <E> x = head; x != null; x = x.down, currentLevel --){
			while (x!= null && pos + x.distance <= index ){
				pos = pos + x.distance;
				x = x.forward;
			}
			if (currentLevel > lvl) 
				x.distance = x.distance + 1;
			else{
				Node <E> y = new Node <>(element);
				Node <E> z = x.forward; // insert y between x and z
				y.forward = z;
				x.forward = y;
				// new pos(z) = pos + old x->fDistance[i] + 1
				// new pos(y) = k + 1
				y.distance = pos + x.distance - index; // new y.distance = new pos(z) – new pos(y)
				x.distance = index + 1 - pos; // new x->fDistance = new pos(y) – new pos(x)
				if (lastInserted != null) lastInserted.down = y;				
				lastInserted = y;
			}
		}
		size ++;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear() {
		size = 0;
		listHeight = 1;

		head = new Node <E> (null);
		head.distance = 1;
		head.forward = null;
		head.down = null;
	}

	@Override
	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public E get(int index) {
		if (index < 0 || index >= size){
			throw new IndexOutOfBoundsException("Index: "+index+", Size: "+size);
		}

		int pos = -1;
		for (Node <E> x = head; x != null; x = x.down){
			while (pos + x.distance <= index ){
				pos = pos + x.distance;
				x = x.forward;
			}
			if (pos == index) return x.item;
		}
		return null; //this line should never be reached
	}

	@Override
	public int indexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterator<E> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int lastIndexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ListIterator<E> listIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public E remove(int index) {
		if (index < 0 || index >= size){
			throw new IndexOutOfBoundsException("Index: "+index+", Size: "+size);
		}

		int pos = -1;
		E deletedItem = null;
		for (Node <E> x = head; x != null; x = x.down){
			while (pos + x.distance < index ){//stop just before the element being removed 
				pos = pos + x.distance;
				x = x.forward;
			}
			if (pos + x.distance == index){ //the element exists at the current level, remove it
				deletedItem = x.forward.item;
				x.distance = x.distance + x.forward.distance - 1; 
				x.forward = x.forward.forward;
			}
			else x.distance--; //the element does not exist at this level, shrink the distance only.
		}
		size--;
		//now remove the empty top levels, if any were created
		if (head.down != null)//if there is more than one level
			for (Node <E> x = head; x != null; x = x.down){
				if (x.forward == null){
					head = x.down;
					listHeight--;
				}
			}		
		return deletedItem;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public E set(int index, E element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString(){
		if (size == 0)
			return "[]";

		Node<E> node = head;
		while(node.down != null) node = node.down; //go to the bottom level
		node = node.forward; //skip the head sentinel

		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (; node != null; node = node.forward){
			sb.append(node.item);
			if (node.forward == null)
				return sb.append(']').toString();
			sb.append(',').append(' ');
		}
		return null; //this line should never be reached
	}

	public String toString2(){
		StringBuffer sb = new StringBuffer();
		sb.append("toString() SkipList of height: " + listHeight + " Size: "+ size +"\n");
		for (Node<E> node = head; node != null; node = node.down){
			for (Node<E> node2 = node; node2 != null; node2 = node2.forward){
				sb.append("(" + node2.item + ")-" + node2.distance + "- ");
				for (int i = 2; i <= node2.distance; i++){
					sb.append("       ");
				}
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	private int randomLevel(){
		int lvl = 1;
		while (random.nextDouble() < p && lvl < MAX_LEVEL)
			lvl = lvl + 1;
		//return 5;
		return lvl;
	}

}
