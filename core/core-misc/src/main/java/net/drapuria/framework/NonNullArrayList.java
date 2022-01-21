/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework;

import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

@NoArgsConstructor
public class NonNullArrayList<E> extends ArrayList<E> {

    public NonNullArrayList(Collection<E> collection) {
        super(collection);

        for (E e : collection) {
            if (e == null) {
                throw new NullPointerException("The List shouldn't be added any null object!");
            }
        }
    }

    @Override
    public boolean add(E e) {
        if (e == null) {
            throw new NullPointerException("The List shouldn't be added any null object!");
        }
        return super.add(e);
    }

    @Override
    public void add(int i, E e) {
        if (e == null) {
            throw new NullPointerException("The List shouldn't be added any null object!");
        }
        super.add(i, e);
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        for (E e : collection) {
            if (e == null) {
                throw new NullPointerException("The List shouldn't be added any null object!");
            }
        }

        return super.addAll(collection);
    }

    @Override
    public boolean addAll(int i, Collection<? extends E> collection) {
        for (E e : collection) {
            if (e == null) {
                throw new NullPointerException("The List shouldn't be added any null object!");
            }
        }

        return super.addAll(i, collection);
    }
}
