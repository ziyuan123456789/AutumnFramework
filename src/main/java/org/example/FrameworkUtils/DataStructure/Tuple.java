package org.example.FrameworkUtils.DataStructure;

/**
 * @author ziyuan
 * @since 2023.12
 */
public class Tuple<K, V> {

    public final K first;

    public final V second;

    public Tuple(K a, V b){
        first = a;
        second = b;
    }
    @Override
    public String toString(){
        return "(" + first + ", " + second + ")";
    }

}