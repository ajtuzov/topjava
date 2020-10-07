package ru.javawebinar.topjava.dao;

import java.util.List;

public interface Storage<E> {
    int put(E element);

    List<E> getAll();

    E getById(int id);

    void delete(int id);
}
