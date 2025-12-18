package com.kondratiev.multithreading.port;

import com.kondratiev.multithreading.entity.Dock;
import com.kondratiev.multithreading.entity.Ship;
import com.kondratiev.multithreading.exception.CustomException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Port {
  private static Port instance;
  private static final Lock lock = new ReentrantLock();

  private final List<Dock> docks;

  // --- warehouse ---
  private final AtomicInteger containers;
  private final int maxCapacity;
  private final ReentrantLock whLock = new ReentrantLock(true);
  private final Condition notFull = whLock.newCondition();
  private final Condition notEmpty = whLock.newCondition();

  private final ReentrantLock docksLock = new ReentrantLock(true);
  private final Condition dockAvailable = docksLock.newCondition();

  private Port(int docksCount, int maxCapacity, int initialContainers) {
    this.docks = new ArrayList<>();
    for (int i = 0; i < docksCount; i++) {
      docks.add(new Dock(i));
    }
    this.maxCapacity = maxCapacity;
    this.containers = new AtomicInteger(initialContainers);
  }

  public static Port getInstance(int docksCount, int maxCapacity, int initialContainers) {
    lock.lock();
    try {
      if (instance == null) {
        instance = new Port(docksCount, maxCapacity, initialContainers);
      }
      return instance;
    } finally {
      lock.unlock();
    }
  }

  public static Port getInstance() throws CustomException {
    lock.lock();
    try {
      if (instance == null) {
        throw new CustomException("Port is not initialized. Call getInstance(docksCount, maxCapacity, initialContainers) in main first.");
      }
      return instance;
    } finally {
      lock.unlock();
    }
  }

  public List<Dock> getDocks() {
    return Collections.unmodifiableList(docks);
  }

  public Dock acquireDock(Ship ship) throws InterruptedException {
    docksLock.lock();
    try {
      while (true) {
        for (Dock dock : docks) {
          if (dock.occupy(ship)) {
            return dock;
          }
        }
        dockAvailable.await();
      }
    } finally {
      docksLock.unlock();
    }
  }

  public void releaseDock(Dock dock) {
    docksLock.lock();
    try {
      dock.free();
      dockAvailable.signal();
    } finally {
      docksLock.unlock();
    }
  }

  public int unloadToPort(int amount) throws InterruptedException {
    if (amount <= 0) {
      return 0;
    }
    whLock.lock();
    try {
      while (containers.get() >= maxCapacity) {
        notFull.await();
      }
      int current = containers.get();
      int free = maxCapacity - current;
      int unload = Math.min(amount, free);

      containers.set(current + unload);
      notEmpty.signalAll();
      return unload;
    } finally {
      whLock.unlock();
    }
  }

  public int loadFromPort(int amount) throws InterruptedException {
    if (amount <= 0) {
      return 0;
    }
    whLock.lock();
    try {
      while (containers.get() <= 0) {
        notEmpty.await();
      }
      int current = containers.get();
      int load = Math.min(amount, current);

      containers.set(current - load);
      notFull.signalAll();
      return load;
    } finally {
      whLock.unlock();
    }
  }

  public int getCurrentContainers() {
    return containers.get();
  }

  public int getMaxCapacity() {
    return maxCapacity;
  }
}
