package com.kondratiev.multithreading.state.impl;

import com.kondratiev.multithreading.entity.Ship;
import com.kondratiev.multithreading.port.Port;
import com.kondratiev.multithreading.state.ShipState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UnloadingState implements ShipState {
  private static final Logger log = LogManager.getLogger();
  @Override
  public ShipState handle(Ship ship) throws InterruptedException {
    Port port = Port.getInstance(0, 0, 0);
    int shipContainers = ship.getCurrentContainers();

    if (shipContainers <= 0) {
      return new LoadingState();
    }

    int unloaded = port.unloadToPort(shipContainers);
    ship.setCurrentContainers(shipContainers - unloaded);
    log.info("Ship {} unloaded {} containers (left on ship: {})",
            ship.getShipId(), unloaded, ship.getCurrentContainers());

    if (ship.getCurrentContainers() > 0) {
      return this;
    }
    return new LoadingState();
  }
}
