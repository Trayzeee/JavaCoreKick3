package com.kondratiev.multithreading.state.impl;

import com.kondratiev.multithreading.entity.Dock;
import com.kondratiev.multithreading.entity.Ship;
import com.kondratiev.multithreading.exception.CustomException;
import com.kondratiev.multithreading.port.Port;
import com.kondratiev.multithreading.state.ShipState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DepartureState implements ShipState {
  private static final Logger logger = LogManager.getLogger();

  @Override
  public ShipState handle(Ship ship) throws CustomException {
    Dock dock = ship.getCurrentDock();
    if (dock != null) {
      Port port = Port.getInstance();
      port.releaseDock(dock);
      ship.setCurrentDock(null);
      logger.info("Ship {} departed and released dock {}", ship.getShipId(), dock.getDockId());
    }
    return null;
  }
}
