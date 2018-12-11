package bgu.spl.mics.application.passiveObjects;

public class Pair<K,V> {

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
