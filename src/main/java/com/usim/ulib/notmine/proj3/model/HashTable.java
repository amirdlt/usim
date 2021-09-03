package com.usim.ulib.notmine.proj3.model;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * HashTable implementation
 * @param <K> The type of keys
 * @param <V> The type of values
 */
public class HashTable<K, V> implements Map<K, V> {
    private Object[] values;
    private Object[] keys;
    private int numOfElements;

    public HashTable(int initialCapacity) {
        values = new Object[initialCapacity];
        keys = new Object[initialCapacity];
        numOfElements = 0;
    }

    public HashTable() {
        this(40);
    }

    public boolean isFull() {
        return numOfElements == values.length;
    }

    @Override
    public int size() {
        return numOfElements;
    }

    @Override
    public boolean isEmpty() {
        return numOfElements == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return Arrays.asList(keys).contains(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return Arrays.asList(values).contains(value);
    }

    @Override
    public V get(Object key) {
        var code = hashCode0(key);
        //noinspection unchecked
        return code == -1 ? null : (V) values[code];
    }

    @Override
    public V put(K key, V value) {
        if (numOfElements == values.length)
            enlarge();
        int code = hashCodeFromKey(key);
        keys[code] = key;
        values[code] = value;
        numOfElements++;
        return value;
    }

    private void enlarge() {
        var values = new Object[this.values.length * 2];
        System.arraycopy(this.values, 0, values, 0, this.values.length);
        this.values = values;

        var keys = new Object[this.keys.length * 2];
        System.arraycopy(this.keys, 0, keys, 0, this.keys.length);
        this.keys = keys;
    }

    @Override
    public V remove(Object key) {
        var code = hashCode0(key);
        if (code == -1)
            return null;
        keys[code] = null;
        var v = values[code];
        values[code] = null;
        numOfElements--;
        //noinspection unchecked
        return (V) v;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        System.err.println("WARNING:: This method is not implemented...");
    }

    @Override
    public void clear() {
        numOfElements = 0;
        Arrays.fill(keys, null);
        Arrays.fill(values, null);
    }

    @Override
    public Set<K> keySet() {
        //noinspection unchecked
        return Arrays.stream(keys).filter(Objects::nonNull).map(e -> (K) e).collect(Collectors.toSet());
    }

    @Override
    public Collection<V> values() {
        //noinspection unchecked
        return Arrays.stream(values).filter(Objects::nonNull).map(e -> (V) e).collect(Collectors.toList());
    }

    @Override
    public @NotNull Set<Entry<K, V>> entrySet() {
        var res = new HashSet<Entry<K, V>>();
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] == null)
                continue;
            //noinspection unchecked
            res.add(Map.entry((K) keys[i], (V) values[i]));
        }
        return res;
    }

    private int hashCodeFromKey(Object key) {
        if (containsKey(key))
            return hashCode0(key);
        var base = key.hashCode() % values.length;
        while (base < 0)
            base += values.length;
        if (values[base] == null)
            return base;
        int counter = 2;
        int index = (base + 1) % values.length;
        while (values[index] != null && index != base) {
            index = (base + counter * counter++) % values.length;
            if (values[index] == null)
                return index;
        }
        return -1;
    }

    private int hashCode0(Object key) {
        int i = key.hashCode() % values.length, h = 1;
        while (i < 0)
            i += values.length;
        while (keys[i] != null && h < values.length) {
            if (keys[i].equals(key))
                return i;
            i = (i + h * h++) % values.length;
        }
        return -1;
    }

    @Override
    public String toString() {
        if (isEmpty())
            return "[]";
        var sb = new StringBuilder("[");
        forEach((key, value) -> sb.append(key).append("=").append(value).append(", "));
        return sb.substring(0, sb.length() - 2) + "]";
    }
}
