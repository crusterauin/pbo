package com.sirekam.pattern.iterator;

import com.sirekam.model.Kunjungan;
import java.util.List;
import java.util.NoSuchElementException;

public class KunjunganIterator implements Iterator<Kunjungan> {

    private List<Kunjungan> items;
    private int position = 0;

    public KunjunganIterator(List<Kunjungan> items) {
        this.items = items;
    }

    @Override
    public boolean hasNext() {
        return position < items.size();
    }

    @Override
    public Kunjungan next() {
        if (!hasNext()) {
            throw new NoSuchElementException("Tidak ada elemen berikutnya");
        }
        return items.get(position++);
    }
}