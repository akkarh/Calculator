/* Team Members
 * Harshitha Akkaraju
 * Shaarika Kaul
 */

package datastructures.concrete;

import datastructures.interfaces.IList;
import misc.exceptions.EmptyContainerException;
import misc.exceptions.NotYetImplementedException;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Note: For more info on the expected behavior of your methods, see
 * the source code for IList.
 */
public class DoubleLinkedList<T> implements IList<T> {
    // You may not rename these fields or change their types.
    // We will be inspecting these in our private tests.
    // You also may not add any additional fields.
    private Node<T> front;
    private Node<T> back;
    private int size;

    public DoubleLinkedList() {
        this.front = null;
        this.back = null;
        this.size = 0;
    }
    
    /**
     * Adds the given item to the *end* of this IList.
     */
    @Override
    public void add(T item) {
    		if (this.size == 0) {
    			this.front = new Node<T>(item);
    			this.back = this.front;
    		} else {
    			this.back.next = new Node<T>(this.back, item, null);
    			this.back = this.back.next;
    		}
    		this.size++;
    }
    
    /**
     * Removes and returns the item from the *end* of this IList.
     *
     * @throws EmptyContainerException if the container is empty and there is no element to remove.
     */
    @Override
    public T remove() {
	    	if (this.size == 0) {
	    		throw new EmptyContainerException();
	    	}
	    	Node<T> temp = this.back;
	    	this.back = this.back.prev;
	    	this.size--;
	    	return temp.data;
    }
    
    /**
     * Returns the item located at the given index.
     *
     * @throws IndexOutOfBoundsException if the index < 0 or index >= this.size()
     */
    @Override
    public T get(int index) {
    		if (index < 0 || index >= this.size) {
    			throw new IndexOutOfBoundsException();
    		}
    		Node<T> curr;
    		if (index == 0) { // first element
    			curr = this.front;
    		} else if (index == this.size - 1) { // last element
    			curr = this.back;
    		} else {
    			curr = getNode(index); // get node at 'index'
    		}
    		return curr.data;
    }
    
    /**
     * Overwrites the element located at the given index with the new item.
     *
     * @throws IndexOutOfBoundsException if the index < 0 or index >= this.size()
     */
    @Override
    public void set(int index, T item) {
    		if (index < 0 || index >= this.size()) {
			throw new IndexOutOfBoundsException();
		}
    		if ((index == 0 && this.size == 1)) { // set at front
    			this.size--;
    			add(item);
    		} else if (index == 0 && this.size > 1) { // set at front variation
    			Node<T> temp = new Node<T>(null, item, this.front.next);
    			this.front.next.prev = temp;
    			this.front = temp;
    		} else if (index == this.size - 1) {
    			remove(); // remove the last node
    			add(item); // add item to the back
    		} else {
    			Node<T> curr = getNode(index); // get node at 'index'
    			Node<T> left = curr.prev;
        		Node<T> right = curr.next;
        		Node<T> temp = new Node<T>(left, item, right);
        		connectNode(left, right, temp); // connect 'temp' to this.pairs
    		}
    }
    
    /**
     * Inserts the given item at the given index. If there already exists an element
     * at that index, shift over that element and any subsequent elements one index
     * higher.
     *
     * @throws IndexOutOfBoundsException if the index < 0 or index >= this.size() + 1
     */
    @Override
    public void insert(int index, T item) {
	    	if (index < 0 || index >= this.size + 1) {
	    		throw new IndexOutOfBoundsException();
	    	}
	    	if (this.size == 0) { // insert to empty list
	    		add(item);
	    	} else if (index == 0) { // insert at front
	    		Node<T> temp = new Node<T>(null, item, this.front);
	    		this.front.prev = temp;
	    		this.front = temp;
	    		this.size++;
	    	} else if (index == this.size) { // insert at back == add to back
	    		add(item);
	    	} else { // insert in the middle
	    		Node<T> curr = getNode(index); // get node at 'index'
	    		Node<T> left = curr.prev;
	    		Node<T> right = curr;
	    		Node<T> temp = new Node<T>(left, item, right);
	    		connectNode(left, right, temp); // connect 'temp' to this.pairs
	    		this.size++;
	    	}
    }
    
    /**
     * Deletes the item at the given index. If there are any elements located at a higher
     * index, shift them all down by one.
     *
     * @throws IndexOutOfBoundsException if the index < 0 or index >= this.size()
     */
    @Override
    public T delete(int index) {
    		if (index < 0 || index >= this.size) {
			throw new IndexOutOfBoundsException();
		}
    		T value; // data to be returned
    		if (index == 0) {
    			value = this.front.data;
    			this.front = this.front.next;
    			this.size--;
    		} else if (index == this.size - 1) {
    			value = this.back.data;
    			remove(); // delete the last node
    		} else {
    			Node<T> curr = getNode(index); // get node at 'index'
    			value = curr.data;
    			Node<T> left = curr.prev;
    			Node<T> right = curr.next;
    			// connect left to right omitting curr
    			left.next = right;
    			right.prev = left;
    			this.size--;
    		}
    		return value;
    }

    /**
     * Returns the index corresponding to the first occurrence of the given item
     * in the list.
     *
     * If the item does not exist in the list, return -1.
     */
    @Override
    public int indexOf(T item) {
		Node<T> curr = this.front;
    		for (int i = 0; i < this.size; i++) {
    			if (curr.data == item || curr.data.equals(item)) { // '==' for when item == null
    				return i;
    			}
    			curr = curr.next;
    		}
    		return -1; // if doesn't exist
    }

    /**
     * Returns the number of elements in the container.
     */
    @Override
    public int size() {
    		return this.size;
    }

    /**
     * Returns 'true' if this container contains the given element, and 'false' otherwise.
     */
    @Override
    public boolean contains(T other) {
        return indexOf(other) >= 0; // indexOf(other) returns -1 when 'other' isn't in this.pairs
    }
    
    /** Helper Method
     *  Returns node at given index
     */
    private Node<T> getNode(int index) {
    		Node<T> curr;
    		if (index <= this.size / 2) { // index is closer to front
    			curr = this.front;
    			for (int i = 0; i < index; i++) {
        			curr = curr.next;
        		}
    		} else { // index is closer to back 
    			curr = this.back;
    			for (int i = this.size - 1; i > index; i--) {
    				curr = curr.prev;
    			}
    		}
    		return curr;
    }
    
    /** Helper Method
     *  Connects 'temp' to the given prev and next nodes
     */
    private void connectNode(Node<T> left, Node<T> right, Node<T> temp) {
    		left.next = temp;
		right.prev = temp;
    }
    
    @Override
    public Iterator<T> iterator() {
        // Note: we have provided a part of the implementation of
        // an iterator for you. You should complete the methods stubs
        // in the DoubleLinkedListIterator inner class at the bottom
        // of this file. You do not need to change this method.
        return new DoubleLinkedListIterator<>(this.front);
    }
    
    private static class Node<E> {
        // You may not change the fields in this node or add any new fields.
        public final E data;
        public Node<E> prev;
        public Node<E> next;

        public Node(Node<E> prev, E data, Node<E> next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }

        public Node(E data) {
            this(null, data, null);
        }

        // Feel free to add additional constructors or methods to this class.
    }

    private static class DoubleLinkedListIterator<T> implements Iterator<T> {
        // You should not need to change this field, or add any new fields.
        private Node<T> current;

        public DoubleLinkedListIterator(Node<T> current) {
            // You do not need to make any changes to this constructor.
            this.current = current;
        }

        /**
         * Returns 'true' if the iterator still has elements to look at;
         * returns 'false' otherwise.
         */
        public boolean hasNext() {
            return this.current != null;
        }

        /**
         * Returns the next item in the iteration and internally updates the
         * iterator to advance one element forward.
         *
         * @throws NoSuchElementException if we have reached the end of the iteration and
         *         there are no more elements to look at.
         */
        public T next() {
        		if (current == null) {
        			throw new NoSuchElementException();
        		}
        		T value = current.data;
        		this.current = this.current.next;
        		return value;
        }
    }
}
