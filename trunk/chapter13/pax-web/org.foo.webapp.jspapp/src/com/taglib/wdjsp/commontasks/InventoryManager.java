package com.taglib.wdjsp.commontasks;

import java.util.*;

public class InventoryManager {
  private static InventoryManager instance;
  private Map descriptions;
  private Map prices;
  
  public static InventoryManager instance() {
    if (instance == null) {
      instance = new InventoryManager();
    }
    return instance;
  }
  
  private InventoryManager() {
    // load up some hard coded descriptions
    descriptions = new HashMap();
    descriptions.put("T7535", "Light Cycle");
    descriptions.put("T9515", "Grid Bug Repellent");
    descriptions.put("T8875", "Digital Tank Grease");
    descriptions.put("T6684", "Input/Output Hat");
    // and some prices
    prices  = new HashMap();
    prices.put("T7535", new Double(1003.10));
    prices.put("T9515", new Double(20.12));
    prices.put("T8875", new Double(10.50));
    prices.put("T6684", new Double(19.95));
  }
  
  public double getPrice(String sku) {
    if (prices.containsKey(sku)) {
      return ((Double)prices.get(sku)).doubleValue();
    }
    else {
      return 0D;
    }
  }
  
  public String getDescription(String sku) {
    if (descriptions.containsKey(sku)) {
      return (String) descriptions.get(sku);
    }
    else {
      return "No Description Available";
    }
  }
}
