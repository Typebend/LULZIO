package org.supersrsFP.lulzio;

import java.util.NoSuchElementException;
import java.util.Objects;

final public class IOLinkedArrayQueue {

    private Object[] internal = new Object[13];
    private int nesting = 0;
    private int size = 0;

    public final boolean isEmpty(){
        return size == 0;
    }

    @SuppressWarnings("unchecked")
    public final Object pop(){
        Object a;
        if (size <= 0) throw new NoSuchElementException();
        else if((--size) == 0 && nesting > 0) {
            internal = (Object []) internal[0];
            a = internal[12];
            internal[12] = null;
            size = 12;
            nesting--;
        } else {
            a = internal[size];
            internal[size] = null;
        }
        return a;
    }

    public final void push(Object a){
        Objects.requireNonNull(a);
        if(size == 13){
            internal = new Object[]{internal, a, null, null, null, null, null, null, null, null, null, null, null};
            size = 2;
            nesting++;
        } else {
            internal[size++] = a;
        }
    }

}
