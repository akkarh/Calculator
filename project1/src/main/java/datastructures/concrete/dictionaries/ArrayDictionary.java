/* Team Members
 * Harshitha Akkaraju
 * Shaarika Kaul
 */

package datastructures.concrete.dictionaries;

import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;
import misc.exceptions.NotYetImplementedException;

/**
 * See IDictionary for more details on what this class should do
 */
public class ArrayDictionary<K, V> implements IDictionary<K, V> {
    // You may not change or rename this field: we will be inspecting
    // it using our private tests.
    private Pair<K, V>[] pairs;

    // You're encouraged to add extra fields (and helper methods) though!
    private int size; // number of pairs in this.pairs
    
    // Initializes a new empty dictionary with an initial capacity of 10 key-value pairs
    public ArrayDictionary() {
    	    this.pairs = makeArrayOfPairs(10);
    		this.size = 0;
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain Pair<K, V> objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private Pair<K, V>[] makeArrayOfPairs(int arraySize) {
        // It turns out that creating arrays of generic objects in Java
        // is complicated due to something known as 'type erasure'.
        //
        // We've given you this helper method to help simplify this part of
        // your assignment. Use this helper method as appropriate when
        // implementing the rest of this class.
        //
        // You are not required to understand how this method works, what
        // type erasure is, or how arrays and generics interact. Do not
        // modify this method in any way.
        return (Pair<K, V>[]) (new Pair[arraySize]);

    }
    
    /**
     * Returns the value corresponding to the given key.
     *
     * @throws NoSuchKeyException if the dictionary does not contain the given key.
     */
    @Override
    public V get(K key) {
    		int index = indexOf(key); // gets element index in this.pairs
    		if (index < 0) {
    			throw new NoSuchKeyException();
    		}
    		return this.pairs[index].value;
    }

    /**
     * Adds the key-value pair to the dictionary. If the key already exists in the dictionary,
     * replace its value with the given one.
     */
    @Override
    public void put(K key, V value) {
    		ensureCapacity();
    		if (this.size == 0 || indexOf(key) < 0) { // when key doesn't exist or this.size == 0
    			this.pairs[this.size] = new Pair<K, V>(key, value);
    			this.size++;
    		} else {
    			this.pairs[indexOf(key)].value = value; // replace existing value
    		}
    }
    
    /**
     * Remove the key-value pair corresponding to the given key from the dictionary.
     *
     * @throws NoSuchKeyException if the dictionary does not contain the given key.
     */
    @Override
    public V remove(K key) {
    		int index = indexOf(key); // gets element index in this.pairs
    		if (index < 0) {
    			throw new NoSuchKeyException();
    		}
    		V value = this.pairs[index].value;
    		for (int i = index; i < this.size - 1; i++) {
    			this.pairs[index] = this.pairs[i + 1]; // shift elements over to the left
    		}
    		this.size--;
    		return value;
    }

    /**
     * Returns 'true' if the dictionary contains the given key and 'false' otherwise.
     */
    @Override
    public boolean containsKey(K key) {
    		return indexOf(key) >= 0; // gets index of element in this.pairs
    }
    
    /**
     * Returns the number of key-value pairs stored in this dictionary.
     */
    @Override
    public int size() {
        return this.size;
    }
    
    /** Helper method
     * If pairs is full, ensureCapacity doubles the size of pairs so it can hold at least this.size elements.
     */
    private void ensureCapacity() {
    		if (this.size == this.pairs.length - 1) {
    			int newCapacity = 1;
    			while (this.size + 1 > newCapacity) {
    				newCapacity = (this.size + 1) * 2;
    			}
    			Pair<K, V> [] temp = makeArrayOfPairs(newCapacity);
    			// Copy over the elements into the new array of pairs
    			for (int i = 0; i < this.size; i++) {
    				temp[i] = this.pairs[i];
    			}
    			this.pairs = temp;
    		}
    }

    /** Helper method
     *  Returns the index of the element in this.pairs if key is in this.pairs and -1 otherwise
     */
    private int indexOf(K key) {
    		Pair<K, V> element;
    		for (int i = 0; i < this.size; i++) {
    			element = this.pairs[i];
    			if (element != null && (element.key == key || element.key.equals(key))) {
    				return i;
    			}
    		}
		return -1;
    }
    
    private static class Pair<K, V> {
        public K key;
        public V value;

        // You may add constructors and methods to this class as necessary.
        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return this.key + "=" + this.value;
        }
    }
}
