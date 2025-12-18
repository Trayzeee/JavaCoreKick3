package com.kondratiev.multithreading.entity;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Dock {
  private final long dockId;
  private final Lock lock = new ReentrantLock();
  private Ship currentShip;

  public Dock(long dockId) {
    this.dockId = dockId;
  }

  public boolean occupy(Ship ship) {
    if (lock.tryLock()) {
      this.currentShip = ship;
      return true;
    }
    return false;
  }

  public void free() {
    this.currentShip = null;
    lock.unlock();
  }

  public long getDockId() {
    return dockId;
  }

  public Ship getCurrentShip() {
    return currentShip;
  }

  public void setCurrentShip(Ship currentShip) {
    this.currentShip = currentShip;
  }
}
