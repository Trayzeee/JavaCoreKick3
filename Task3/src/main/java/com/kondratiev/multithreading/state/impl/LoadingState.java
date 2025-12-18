package com.kondratiev.multithreading.state.impl;

import com.kondratiev.multithreading.entity.Ship;
import com.kondratiev.multithreading.port.Port;
import com.kondratiev.multithreading.state.ShipState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoadingState implements ShipState {
  private static final Logger log = LogManager.getLogger();
  @Override
  public ShipState handle(Ship ship) throws InterruptedException {

    Port port = Port.getInstance(0, 0, 0);
    int capacity = ship.getShipCapacity();
    int current = ship.getCurrentContainers();
    int need = capacity - current;
    if (need <= 0) {
      return new DepartureState();
    }
    int loaded = port.loadFromPort(need);
    if (loaded == 0) {
      return new DepartureState();
    }
    ship.setCurrentContainers(current + loaded);
    log.info("Ship {} loaded {} containers (on ship: {})",
            ship.getShipId(), loaded, ship.getCurrentContainers());

    return new DepartureState();
  }
}
