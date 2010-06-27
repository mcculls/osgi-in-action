package com.taglib.wdjsp.commontasks;

import java.text.*;
import java.io.*;

public class ShoppingCartItem implements Serializable {
  private String itemNumber;
  private int count;
  private NumberFormat currencyFormat;
  
  public ShoppingCartItem(String itemNumber) {
    this.itemNumber = itemNumber;
    this.count = 1;
    currencyFormat = NumberFormat.getCurrencyInstance();
  }
  
  public int getCount() {
    return count;
  }
  
  public String getItemNumber() {
    return itemNumber;
  }
  
  public String getDescription() {
    return InventoryManager.instance().getDescription(itemNumber);
  }
  
  public String getUnitPriceString() {
    double price = getUnitPrice();
    return currencyFormat.format(price);
  }
  
  public double getUnitPrice() {
    double price = InventoryManager.instance().getPrice(itemNumber);
    return price;
  }
  
  public String getExtendedPriceString() {
    double price = getExtendedPrice();
    return currencyFormat.format(price);
  }
  
  public double getExtendedPrice() {
    double price = InventoryManager.instance().getPrice(itemNumber);
    return price * count;
  }
  
  public void incrementCount(int delta) {
    count = count + delta;
  }
  
  public boolean equals(Object o) {
    if (! (o instanceof ShoppingCartItem)) {
      return false;
    }
    else {
      ShoppingCartItem item = (ShoppingCartItem)o;
      return item.getItemNumber().equals(this.itemNumber);
    }
  }
  
}
