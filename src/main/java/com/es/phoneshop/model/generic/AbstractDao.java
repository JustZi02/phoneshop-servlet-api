package com.es.phoneshop.model;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class AbstractDao<T> {
    protected final List<T> items = new ArrayList<>();
    protected final ReadWriteLock lock = new ReentrantReadWriteLock();
    protected long maxId = 0L;

    protected abstract Long getId(T item);

    protected abstract void setId(T item, Long id);

    public T get(Long id) {
        lock.readLock().lock();
        try {
            return items.stream()
                    .filter(item -> id.equals(getId(item)))
                    .findAny()
                    .orElseThrow(() -> new NoSuchElementException("Item with id " + id + " not found"));
        } finally {
            lock.readLock().unlock();
        }
    }

    public void save(T item) {
        lock.writeLock().lock();
        try {
            Long id = getId(item);
            if (id != null) {
                items.removeIf(existing -> id.equals(getId(existing)));
                items.add(item);
            } else {
                setId(item, maxId++);
                items.add(item);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<T> getAll() {
        lock.readLock().lock();
        try {
            return items;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void delete(Long id) {
        lock.writeLock().lock();
        try {
            boolean removed = items.removeIf(item -> id.equals(getId(item)));
            if (!removed) {
                throw new NoSuchElementException("Item with id " + id + " not found");
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
}
