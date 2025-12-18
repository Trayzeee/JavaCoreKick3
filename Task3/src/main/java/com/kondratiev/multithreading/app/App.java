package com.kondratiev.multithreading.app;

import com.kondratiev.multithreading.entity.Ship;
import com.kondratiev.multithreading.port.Port;
import com.kondratiev.multithreading.state.impl.ArrivingState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class App {

  private static final Logger logger = LogManager.getLogger();

  public static void main(String[] args) {
    final int docksCount = 6;
    final int portCapacity = 200;
    final int portInitial = 80;

    final int shipsCount = 25;
    final int poolThreads = 12;

    Port port = Port.getInstance(docksCount, portCapacity, portInitial);
    logger.info("Port initialized: docks={}, containers={}/{}",
            docksCount, port.getCurrentContainers(), port.getMaxCapacity());

    ExecutorService pool = Executors.newFixedThreadPool(poolThreads, new NamedThreadFactory("ship-"));
    List<Future<Boolean>> futures = new ArrayList<>();

    Random rnd = new Random();

    for (int i = 1; i <= shipsCount; i++) {
      int shipCapacity = 60 + rnd.nextInt(141);
      int initial = rnd.nextInt(shipCapacity + 1);

      Ship ship = new Ship(i, shipCapacity, initial, new ArrivingState());
      futures.add(pool.submit(ship));
    }

    int ok = 0;
    int fail = 0;

    for (Future<Boolean> f : futures) {
      try {
        Boolean res = f.get();
        if (res != null && res) ok++;
        else fail++;
      }
      catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        logger.warn("Main interrupted while waiting ships", e);
        break;
      }
      catch (ExecutionException e) {
        fail++;
        logger.error("Ship task failed: {}", e.getMessage(), e);
      }
    }

    pool.shutdown();
    try {
      if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
        logger.warn("Forcing shutdown...");
        pool.shutdownNow();
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      pool.shutdownNow();
    }

    logger.info("TEST FINISHED: success={}, failed={}, port containers={}/{}",
            ok, fail, port.getCurrentContainers(), port.getMaxCapacity());
  }

  private static final class NamedThreadFactory implements ThreadFactory {
    private final String prefix;
    private int idx = 1;

    private NamedThreadFactory(String prefix) {
      this.prefix = prefix;
    }

    @Override
    public Thread newThread(@NotNull Runnable r) {
      Thread t = new Thread(r);
      t.setName(prefix + idx);
      idx++;
      return t;
    }
  }
}

