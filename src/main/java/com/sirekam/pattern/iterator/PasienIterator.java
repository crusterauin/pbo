package com.sirekam.pattern.iterator;

import com.sirekam.model.Pasien;
import java.util.List;
import java.util.NoSuchElementException;

public class PasienIterator implements Iterator<Pasien> {

    private List<Pasien> items;
    private int position = 0;

    public PasienIterator(List<Pasien> items) {
        this.items = items;
    }

    @Override
    public boolean hasNext() {
        return position < items.size();
    }

    @Override
    public Pasien next() {
        if (!hasNext()) {
            throw new NoSuchElementException("Tidak ada elemen berikutnya");
        }
        return items.get(position++);
    }
}