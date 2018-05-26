package org.lislon;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class ListBackedByMapTest {

    private ListBackedByMap<Integer, String> list;

    @BeforeEach
    void setUp() {
        list = new ListBackedByMap<>(v -> v == null ? 0 : v.hashCode());
    }

    @Test
    public void testSizeAfterAddingToList() {
        list.add("test");
        assertEquals(1, list.size());
    }

    @Test
    public void testListElement() {
        list.add("a");
        assertEquals("a", list.get(0));
    }

    @Test
    public void testAdd_Duplicates() {
        list.add(0, "a");
        list.add(0, "a");
        assertEquals(1, list.size());
    }

    @Test
    public void testAddAll_Duplicates() {
        list.addAll(Arrays.asList("a", "a", "b"));
        assertEquals(2, list.size());
    }

    @Test
    public void testAddAllIndex_Duplicates() {
        list.addAll(0, Arrays.asList("a", "a"));
        assertEquals(1, list.size());
    }

    @Test
    @Disabled
    public void testModifyCollectionInForeach() {
        initializeListWith("a", "b");
        Assertions.assertThrows(ConcurrentModificationException.class, () -> {
            for (String item : list) {
                list.remove(item);
            }
        });
    }

    @Test
    public void testRemove() {
        list.addAll(Arrays.asList("a", "b"));
        list.remove(1);

        assertMapsEquals("a");
    }

    @Test
    public void testRemoveIf() {
        initializeListWith("a", "b", "c");
        list.removeIf(item -> item.equals("b"));

        assertMapsEquals("a", "c");
    }

    @Test
    public void testGet() {
        initializeListWith("a", "b", "c");
        assertEquals("a", list.get(0));
        assertEquals("b", list.get(1));
        assertEquals("c", list.get(2));
    }

    @Test
    public void testListIteratorForward() {
        initializeListWith("a", "b");
        ListIterator<String> iterator = list.listIterator();

        assertTrue(iterator.hasNext());
        assertEquals("a", iterator.next());

        assertTrue(iterator.hasNext());
        assertEquals("b", iterator.next());

        assertFalse(iterator.hasNext());

        Assertions.assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    public void testListIteratorBackward() {
        initializeListWith("a", "b");
        ListIterator<String> iterator = list.listIterator(2);

        assertTrue(iterator.hasPrevious());
        assertEquals("b", iterator.previous());

        assertTrue(iterator.hasPrevious());
        assertEquals("a", iterator.previous());

        assertFalse(iterator.hasPrevious());

        Assertions.assertThrows(NoSuchElementException.class, iterator::previous);
    }

    @Test
    public void testAddNullElement() {
        list.add(null);
        assertEquals(null, list.get(0));
    }

    @Test
    public void testRemoveNullElement() {
        list.add(null);
        list.remove(null);
        assertEquals(0, list.size());
    }

    @Test
    public void testSet() {
        initializeListWith("a", "b");
        list.set(0, "c");
        assertMapsEquals("c", "b");
    }

    private void initializeListWith(String... strings) {
        list.addAll(Arrays.asList(strings));
    }

    private void assertMapsEquals(String... strings) {
        assertEquals(Sets.newHashSet(strings), Sets.newHashSet(list.getBackedMap().values()));
    }

}