package com.sirekam.dao;

import java.util.List;

public interface GenericDAO<T> {
    boolean save(T entity) throws Exception;
    T findById(int id) throws Exception;
    List<T> findAll() throws Exception;
    boolean update(T entity) throws Exception;
    boolean delete(int id) throws Exception;
    List<T> search(String keyword) throws Exception;
}