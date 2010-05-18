package org.foo.stockprovider.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.foo.stockprovider.StockProvider;

public class StockProviderImpl implements StockProvider {

  private static final double MAX_PRICE = 100.0; // $100.00
  
  Random rnd = new Random();

  public Map<String, Double> getStocks(String[] symbols) {
    HashMap<String, Double> stocks = new HashMap<String, Double>();
    
    for (int i=0; i<symbols.length; i++) {
      if (!symbols[i].equals("ERR")) {
        double price = rnd.nextDouble() * MAX_PRICE;
        stocks.put(symbols[i], price);
      }
    }

    return stocks;
  }

}
