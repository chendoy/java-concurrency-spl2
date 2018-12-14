package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;

public class Pair<K,V> implements Serializable {

    private K key;
    private V value;

    public Pair(K key, V value)
    {
        this.key=key;
        this.value=value;
    }

    public K getKey() { return this.key; }
    public V getValue() { return this.value; }
}
