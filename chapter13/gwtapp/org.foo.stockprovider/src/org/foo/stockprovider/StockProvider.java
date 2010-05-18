package org.foo.stockprovider;

import java.util.Map;

public interface StockProvider {
  Map<String, Double> getStocks(String[] symbols);
}
