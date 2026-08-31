package com.sirekam.controller;

import com.sirekam.dao.GenericDAO;
import java.util.List;

public abstract class GenericController<T> {

    protected GenericDAO<T> dao;

    public GenericController(GenericDAO<T> dao) {
        this.dao = dao;
    }

    public boolean simpan(T entity) throws Exception {
        return dao.save(entity);
    }

    public T cariById(int id) throws Exception {
        return dao.findById(id);
    }

    public List<T> cariSemua() throws Exception {
        return dao.findAll();
    }

    public boolean update(T entity) throws Exception {
        return dao.update(entity);
    }

    public boolean hapus(int id) throws Exception {
        return dao.delete(id);
    }

    public List<T> cari(String keyword) throws Exception {
        return dao.search(keyword);
    }
}