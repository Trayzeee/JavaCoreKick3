package com.kondratiev.multithreading.state;

import com.kondratiev.multithreading.entity.Ship;
import com.kondratiev.multithreading.exception.CustomException;

public interface ShipState {
  ShipState handle(Ship ship) throws InterruptedException, CustomException;
}
