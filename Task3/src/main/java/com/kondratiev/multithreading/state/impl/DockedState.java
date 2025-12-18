package com.kondratiev.multithreading.state.impl;

import com.kondratiev.multithreading.entity.Ship;
import com.kondratiev.multithreading.state.ShipState;

public class DockedState implements ShipState {
  @Override
  public ShipState handle(Ship ship) {
    return new UnloadingState();
  }
}
