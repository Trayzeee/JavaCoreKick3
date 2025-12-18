package com.kondratiev.multithreading.state.impl;

import com.kondratiev.multithreading.entity.Dock;
import com.kondratiev.multithreading.entity.Ship;
import com.kondratiev.multithreading.exception.CustomException;
import com.kondratiev.multithreading.port.Port;
import com.kondratiev.multithreading.state.ShipState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ArrivingState implements ShipState {
  private static final Logger logger = LogManager.getLogger();

  @Override
  public ShipState handle(Ship ship) throws InterruptedException, CustomException {
    Port port = Port.getInstance();

    logger.info("Ship {} waiting for available dock...", ship.getShipId());
    Dock dock = port.acquireDock(ship);

    ship.setCurrentDock(dock);
    logger.info("Ship {} acquired dock {}", ship.getShipId(), dock.getDockId());

    return new DockedState();
  }
}
