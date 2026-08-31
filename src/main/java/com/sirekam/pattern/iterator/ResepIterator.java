package com.sirekam.pattern.iterator;

import com.sirekam.model.Resep;
import java.util.List;
import java.util.NoSuchElementException;

public class ResepIterator implements Iterator<Resep> {

    private List<Resep> items;
    private int position = 0;

    public ResepIterator(List<Resep> items) {
        this.items = items;
    }

    @Override
    public boolean hasNext() {
        return position < items.size();
    }

    @Override
    public Resep next() {
        if (!hasNext()) {
            throw new NoSuchElementException("Tidak ada elemen berikutnya");
        }
        return items.get(position++);
    }
}