package com.kondratiev.multithreading.entity;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.kondratiev.multithreading.exception.CustomException;
import com.kondratiev.multithreading.state.ShipState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Ship implements Callable<Boolean> {
  private static final Logger logger = LogManager.getLogger();
  private final long shipId;
  private final int shipCapacity;
  private int currentContainers;
  private ShipState state;
  private Dock currentDock;

  public Ship(long shipId, int shipCapacity, int initialContainers, ShipState initialState) {
    this.shipId = shipId;
    this.shipCapacity = shipCapacity;
    this.currentContainers = initialContainers;
    this.state = initialState;
    logger.debug("Ship {} created (capacity: {}, containers: {})",
            shipId, shipCapacity, initialContainers);
  }
  @Override
  public Boolean call() {
    logger.info("Ship {} started operations in thread: {}",
            shipId, Thread.currentThread().getName());
    try {
      while (state != null) {
        ShipState nextState = state.handle(this);
        if (nextState == null) {
          logger.info("Ship {} completed all operations", shipId);
          break;
        }
        state = nextState;
        TimeUnit.MILLISECONDS.sleep(500);
      }
    } catch (InterruptedException e) {
      logger.error("Ship {} encountered error: {}", shipId, e.getMessage(), e);
      return false;
    } catch (CustomException e) {
      e.printStackTrace();
    }
    return true;
  }
  public long getShipId() {
    return shipId;
  }
  public int getShipCapacity() {
    return shipCapacity;
  }

  public int getCurrentContainers() {
    return currentContainers;
  }

  public void setCurrentContainers(int count) {
    this.currentContainers = count;
  }

  public Dock getCurrentDock() {
    return currentDock;
  }

  public void setCurrentDock(Dock dock) {
    this.currentDock = dock;
  }
}
