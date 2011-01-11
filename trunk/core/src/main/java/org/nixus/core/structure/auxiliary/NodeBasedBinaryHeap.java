package org.nixus.core.structure.auxiliary;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.SortedSet;

import org.nixus.core.structure.nodes.AbstractNode;

/**
 * Specific binary priority queue based on the java implementation improved to have a O(log(N)) 
 * complexity on remove(Node) operations. 
 * */
public class NodeBasedBinaryHeap  implements java.io.Serializable {

	private static final long serialVersionUID = 6688137418086198209L;

    /**
     * Priority queue represented as a balanced binary heap: the two
     * children of queue[n] are queue[2*n+1] and queue[2*(n+1)].  The
     * priority queue is ordered by comparator, or by the elements'
     * natural ordering, if comparator is null: For each node n in the
     * heap and each descendant d of n, n <= d.  The element with the
     * lowest value is in queue[0], assuming the queue is nonempty.
     */
    private transient AbstractNode[] queue;

    private int[] nodeIndexesInQueue;
    
    /**
     * The number of elements in the priority queue.
     */
    private int size = 0;

    /**
     * The comparator, or null if priority queue uses elements'
     * natural ordering.
     */
    private final Comparator<? super AbstractNode> comparator;

    /**
     * The number of times this priority queue has been
     * <i>structurally modified</i>.  See AbstractList for gory details.
     */
    private transient int modCount = 0;

    /**
     * Creates a {@code PriorityQueue} containing the elements in the
     * specified collection.  If the specified collection is an instance of
     * a {@link SortedSet} or is another {@code PriorityQueue}, this
     * priority queue will be ordered according to the same ordering.
     * Otherwise, this priority queue will be ordered according to the
     * {@linkplain Comparable natural ordering} of its elements.
     *
     * @param  c the collection whose elements are to be placed
     *         into this priority queue
     * @throws ClassCastException if elements of the specified collection
     *         cannot be compared to one another according to the priority
     *         queue's ordering
     * @throws NullPointerException if the specified collection or any
     *         of its elements are null
     */
	public NodeBasedBinaryHeap(Collection<? extends AbstractNode> c, Comparator<AbstractNode> comparator) {
        initFromCollection(c);
        this.comparator = comparator;
        heapify();

    }

    /**
     * Initializes queue array with elements from the given Collection.
     *
     * @param c the collection
     */
    private void initFromCollection(Collection<? extends AbstractNode> c) {
    	AbstractNode[] a = new AbstractNode[c.size()];
    	a = c.toArray(a);
       
        queue = a;
        size = a.length;
        nodeIndexesInQueue = new int[size]; 
        for (int i = 0; i < a.length; i++) {
			nodeIndexesInQueue[a[i].getInsertionOrder()] = i;
		}
    }

    /**
     * Inserts the specified element into this priority queue.
     *
     * @return {@code true} (as specified by {@link Collection#add})
     * @throws ClassCastException if the specified element cannot be
     *         compared with elements currently in this priority queue
     *         according to the priority queue's ordering
     * @throws NullPointerException if the specified element is null
     */
    public boolean add(AbstractNode e) {
        return offer(e);
    }

    /**
     * Inserts the specified element into this priority queue.
     *
     * @return {@code true} (as specified by {@link Queue#offer})
     * @throws ClassCastException if the specified element cannot be
     *         compared with elements currently in this priority queue
     *         according to the priority queue's ordering
     * @throws NullPointerException if the specified element is null
     */
    public boolean offer(AbstractNode e) {
        if (e == null)
            throw new NullPointerException();
        modCount++;
        int i = size;
        size = i + 1;
        if (i == 0){
        	nodeIndexesInQueue[e.getInsertionOrder()] = 0;
            queue[0] = e;
        }
        else
            siftUp(i, e);
        return true;
    }

    public AbstractNode peek() {
        if (size == 0)
            return null;
        return queue[0];
    }

    private int indexOf(Object o) {
	if (o != null) {
            for (int i = 0; i < size; i++)
                if (o.equals(queue[i]))
                    return i;
        }
        return -1;
    }

    /**
     * Removes a single instance of the specified element from this queue,
     * if it is present.  More formally, removes an element {@code e} such
     * that {@code o.equals(e)}, if this queue contains one or more such
     * elements.  Returns {@code true} if and only if this queue contained
     * the specified element (or equivalently, if this queue changed as a
     * result of the call).
     *
     * @param o element to be removed from this queue, if present
     * @return {@code true} if this queue changed as a result of the call
     */
    public boolean remove(AbstractNode hnAbst) {
	int i = nodeIndexesInQueue[hnAbst.getInsertionOrder()];
	if (i == -1)
	    return false;
	else {
	    removeAt(i, hnAbst.getInsertionOrder());
	    return true;
	}
    }

    /**
     * Version of remove using reference equality, not equals.
     * Needed by iterator.remove.
     *
     * @param o element to be removed from this queue, if present
     * @return {@code true} if removed
     */
    boolean removeEq(Object o) {
	for (int i = 0; i < size; i++) {
	    if (o == queue[i]) {
                removeAt(i, queue[i].getInsertionOrder());
                return true;
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if this queue contains the specified element.
     * More formally, returns {@code true} if and only if this queue contains
     * at least one element {@code e} such that {@code o.equals(e)}.
     *
     * @param o object to be checked for containment in this queue
     * @return {@code true} if this queue contains the specified element
     */
    public boolean contains(Object o) {
	return indexOf(o) != -1;
    }

    /**
     * Returns an array containing all of the elements in this queue.
     * The elements are in no particular order.
     *
     * <p>The returned array will be "safe" in that no references to it are
     * maintained by this queue.  (In other words, this method must allocate
     * a new array).  The caller is thus free to modify the returned array.
     *
     * <p>This method acts as bridge between array-based and collection-based
     * APIs.
     *
     * @return an array containing all of the elements in this queue
     */
    public Object[] toArray() {
        return Arrays.copyOf(queue, size);
    }

    /**
     * Returns an array containing all of the elements in this queue; the
     * runtime type of the returned array is that of the specified array.
     * The returned array elements are in no particular order.
     * If the queue fits in the specified array, it is returned therein.
     * Otherwise, a new array is allocated with the runtime type of the
     * specified array and the size of this queue.
     *
     * <p>If the queue fits in the specified array with room to spare
     * (i.e., the array has more elements than the queue), the element in
     * the array immediately following the end of the collection is set to
     * {@code null}.
     *
     * <p>Like the {@link #toArray()} method, this method acts as bridge between
     * array-based and collection-based APIs.  Further, this method allows
     * precise control over the runtime type of the output array, and may,
     * under certain circumstances, be used to save allocation costs.
     *
     * <p>Suppose <tt>x</tt> is a queue known to contain only strings.
     * The following code can be used to dump the queue into a newly
     * allocated array of <tt>String</tt>:
     *
     * <pre>
     *     String[] y = x.toArray(new String[0]);</pre>
     *
     * Note that <tt>toArray(new Object[0])</tt> is identical in function to
     * <tt>toArray()</tt>.
     *
     * @param a the array into which the elements of the queue are to
     *          be stored, if it is big enough; otherwise, a new array of the
     *          same runtime type is allocated for this purpose.
     * @return an array containing all of the elements in this queue
     * @throws ArrayStoreException if the runtime type of the specified array
     *         is not a supertype of the runtime type of every element in
     *         this queue
     * @throws NullPointerException if the specified array is null
     */
    public <T> T[] toArray(T[] a) {
        if (a.length < size)
            // Make a new array of a's runtime type, but my contents:
            return (T[]) Arrays.copyOf(queue, size, a.getClass());
	System.arraycopy(queue, 0, a, 0, size);
        if (a.length > size)
            a[size] = null;
        return a;
    }

    /**
     * Returns an iterator over the elements in this queue. The iterator
     * does not return the elements in any particular order.
     *
     * @return an iterator over the elements in this queue
     */
    public Iterator<AbstractNode> iterator() {
        return new Itr();
    }

    private final class Itr implements Iterator<AbstractNode> {
        /**
         * Index (into queue array) of element to be returned by
         * subsequent call to next.
         */
        private int cursor = 0;

        /**
         * Index of element returned by most recent call to next,
         * unless that element came from the forgetMeNot list.
         * Set to -1 if element is deleted by a call to remove.
         */
        private int lastRet = -1;

        /**
         * A queue of elements that were moved from the unvisited portion of
         * the heap into the visited portion as a result of "unlucky" element
         * removals during the iteration.  (Unlucky element removals are those
         * that require a siftup instead of a siftdown.)  We must visit all of
         * the elements in this list to complete the iteration.  We do this
         * after we've completed the "normal" iteration.
         *
         * We expect that most iterations, even those involving removals,
         * will not need to store elements in this field.
         */
        private ArrayDeque<AbstractNode> forgetMeNot = null;

        /**
         * Element returned by the most recent call to next iff that
         * element was drawn from the forgetMeNot list.
         */
        private AbstractNode lastRetElt = null;

        /**
         * The modCount value that the iterator believes that the backing
         * Queue should have.  If this expectation is violated, the iterator
         * has detected concurrent modification.
         */
        private int expectedModCount = modCount;

        @Override
		public boolean hasNext() {
            return cursor < size ||
                (forgetMeNot != null && !forgetMeNot.isEmpty());
        }

        @Override
		public AbstractNode next() {
            if (expectedModCount != modCount)
                throw new ConcurrentModificationException();
            if (cursor < size)
                return queue[lastRet = cursor++];
            if (forgetMeNot != null) {
                lastRet = -1;
                lastRetElt = forgetMeNot.poll();
                if (lastRetElt != null)
                    return lastRetElt;
            }
            throw new NoSuchElementException();
        }

        @Override
		public void remove() {
            if (expectedModCount != modCount)
                throw new ConcurrentModificationException();
            if (lastRet != -1) {
            	AbstractNode moved = NodeBasedBinaryHeap.this.removeAt(lastRet, queue[lastRet].getInsertionOrder());
                lastRet = -1;
                if (moved == null)
                    cursor--;
                else {
                    if (forgetMeNot == null)
                        forgetMeNot = new ArrayDeque<AbstractNode>();
                    forgetMeNot.add(moved);
                }
            } else if (lastRetElt != null) {
                NodeBasedBinaryHeap.this.removeEq(lastRetElt);
                lastRetElt = null;
            } else {
                throw new IllegalStateException();
	    }
            expectedModCount = modCount;
        }
    }

    public int size() {
        return size;
    }

    /**
     * Removes all of the elements from this priority queue.
     * The queue will be empty after this call returns.
     */
    public void clear() {
        modCount++;
        for (int i = 0; i < size; i++){
        	nodeIndexesInQueue[queue[i].getInsertionOrder()] = -1;
            queue[i] = null;
        }
        size = 0;
    }

    public AbstractNode poll() {
        if (size == 0)
            return null;
        int s = --size;
        modCount++;
        AbstractNode result = queue[0];
        AbstractNode x = queue[s];
        nodeIndexesInQueue[result.getInsertionOrder()] = -1;
        queue[s] = null;
        if (s != 0)
            siftDown(0, x);
        return result;
    }

    /**
     * Removes the ith element from queue.
     *
     * Normally this method leaves the elements at up to i-1,
     * inclusive, untouched.  Under these circumstances, it returns
     * null.  Occasionally, in order to maintain the heap invariant,
     * it must swap a later element of the list with one earlier than
     * i.  Under these circumstances, this method returns the element
     * that was previously at the end of the list and is now at some
     * position before i. This fact is used by iterator.remove so as to
     * avoid missing traversing elements.
     * @param insertionOrder 
     */
    private AbstractNode removeAt(int i, int insertionOrder) {
        assert i >= 0 && i < size;
        modCount++;
        int s = --size;
        if (s == i) {// removed last element
        	nodeIndexesInQueue[insertionOrder] = -1;
            queue[i] = null;
        }
        else {
        	AbstractNode moved =  queue[s];
        	nodeIndexesInQueue[insertionOrder] = -1;
            queue[s] = null;
            siftDown(i, moved);
            if (queue[i] == moved) {
                siftUp(i, moved);
                if (queue[i] != moved)
                    return moved;
            }
        }
        return null;
    }

    /**
     * Inserts item x at position k, maintaining heap invariant by
     * promoting x up the tree until it is greater than or equal to
     * its parent, or is the root.
     *
     * To simplify and speed up coercions and comparisons. the
     * Comparable and Comparator versions are separated into different
     * methods that are otherwise identical. (Similarly for siftDown.)
     *
     * @param k the position to fill
     * @param x the item to insert
     */
    private void siftUp(int k, AbstractNode x) {
        if (comparator != null)
            siftUpUsingComparator(k, x);
        else
            siftUpComparable(k, x);
    }

    private void siftUpComparable(int k, AbstractNode x) {
        Comparable<? super AbstractNode> key = (Comparable<? super AbstractNode>) x;
        while (k > 0) {
            int parent = (k - 1) >>> 1;
            AbstractNode e = queue[parent];
            if (key.compareTo(e) >= 0)
                break;
            nodeIndexesInQueue[e.getInsertionOrder()] = k;
            queue[k] = e;
            k = parent;
        }
        nodeIndexesInQueue[((AbstractNode) key).getInsertionOrder()] = k;
        queue[k] = (AbstractNode) key;
    }

    private void siftUpUsingComparator(int k, AbstractNode x) {
        while (k > 0) {
            int parent = (k - 1) >>> 1;
        AbstractNode e = queue[parent];
            if (comparator.compare(x, e) >= 0)
                break;
            nodeIndexesInQueue[e.getInsertionOrder()] = k;
            queue[k] = e;
            k = parent;
        }
        nodeIndexesInQueue[x.getInsertionOrder()] = k;
        queue[k] = x;
    }

    /**
     * Inserts item x at position k, maintaining heap invariant by
     * demoting x down the tree repeatedly until it is less than or
     * equal to its children or is a leaf.
     *
     * @param k the position to fill
     * @param x the item to insert
     */
    private void siftDown(int k, AbstractNode x) {
        if (comparator != null)
            siftDownUsingComparator(k, x);
        else
            siftDownComparable(k, x);
    }

    @SuppressWarnings("unchecked")
	private void siftDownComparable(int k, AbstractNode x) {
        Comparable<? super AbstractNode> key = (Comparable<? super AbstractNode>)x;
        int half = size >>> 1;        // loop while a non-leaf
        while (k < half) {
            int child = (k << 1) + 1; // assume left child is least
            AbstractNode c = queue[child];
            int right = child + 1;
            if (right < size &&
                ((Comparable<? super AbstractNode>) c).compareTo(queue[right]) > 0)
                c = queue[child = right];
            if (key.compareTo(c) <= 0)
                break;
            nodeIndexesInQueue[c.getInsertionOrder()] = k;
            queue[k] = c;
            k = child;
        }
        nodeIndexesInQueue[((AbstractNode) key).getInsertionOrder()] = k;
        queue[k] = (AbstractNode) key;
    }

    private void siftDownUsingComparator(int k, AbstractNode x) {
        int half = size >>> 1;
        while (k < half) {
            int child = (k << 1) + 1;
            AbstractNode c = queue[child];
            int right = child + 1;
            if (right < size &&
                comparator.compare(c, queue[right]) > 0)
                c = queue[child = right];
            if (comparator.compare(x, c) <= 0)
                break;
            nodeIndexesInQueue[c.getInsertionOrder()] = k;
            queue[k] = c;
            k = child;
        }
        nodeIndexesInQueue[x.getInsertionOrder()] = k;
        queue[k] = x;
    }

    /**
     * Establishes the heap invariant (described above) in the entire tree,
     * assuming nothing about the order of the elements prior to the call.
     */
    private void heapify() {
        for (int i = (size >>> 1) - 1; i >= 0; i--)
            siftDown(i, queue[i]);
    }

    /**
     * Returns the comparator used to order the elements in this
     * queue, or {@code null} if this queue is sorted according to
     * the {@linkplain Comparable natural ordering} of its elements.
     *
     * @return the comparator used to order this queue, or
     *         {@code null} if this queue is sorted according to the
     *         natural ordering of its elements
     */
    public Comparator<? super AbstractNode> comparator() {
        return comparator;
    }

    /**
     * Saves the state of the instance to a stream (that
     * is, serializes it).
     *
     * @serialData The length of the array backing the instance is
     *             emitted (int), followed by all of its elements
     *             (each an {@code Object}) in the proper order.
     * @param s the stream
     */
    private void writeObject(java.io.ObjectOutputStream s)
        throws java.io.IOException{
        // Write out element count, and any hidden stuff
        s.defaultWriteObject();

        // Write out array length, for compatibility with 1.5 version
        s.writeInt(Math.max(2, size + 1));

        // Write out all elements in the "proper order".
        for (int i = 0; i < size; i++){
        	s.writeInt(queue[i].getInsertionOrder());
            s.writeObject(queue[i]);
        }
    }

    /**
     * Reconstitutes the {@code PriorityQueue} instance from a stream
     * (that is, deserializes it).
     *
     * @param s the stream
     */
    private void readObject(java.io.ObjectInputStream s)
        throws java.io.IOException, ClassNotFoundException {
        // Read in size, and any hidden stuff
        s.defaultReadObject();

        // Read in (and discard) array length
        s.readInt();

	queue = new AbstractNode[size];
	nodeIndexesInQueue = new int[size];

        // Read in all elements.
        for (int i = 0; i < size; i++){
        	int qIndex = s.readInt();
        	nodeIndexesInQueue[qIndex] = i;
            queue[i] = (AbstractNode) s.readObject();
        }

	// Elements are guaranteed to be in "proper order", but the
	// spec has never explained what that might be.
	heapify();
    }

	public boolean isEmpty() {
		return size == 0;
	}
}
