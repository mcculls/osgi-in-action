package com.taglib.wdjsp.commontasks;

import java.util.*;
import java.text.*;
import java.io.*;

public class ShoppingCart implements Serializable {
  private List items;
  private NumberFormat currencyFormat;
  
  public ShoppingCart() {
    items = new ArrayList();
    currencyFormat = NumberFormat.getCurrencyInstance();
  }
  
  public void setAddItem(String itemNumber) {
    changeItemCount(itemNumber, 1);
  }
  
  public void setRemoveItem(String itemNumber) {
    changeItemCount(itemNumber, -1);
  }
  
  public ShoppingCartItem getItem(int i) {
    return (ShoppingCartItem)items.get(i);
  }
  
  public int getItemSize() {
    return items.size();
  }
  
  public String getTotalPrice() {
    Iterator i = items.iterator();
    double price = 0.00;
    while (i.hasNext()) {
      ShoppingCartItem item = (ShoppingCartItem)i.next();
      price += item.getExtendedPrice();
    }
    return currencyFormat.format(price);
  }
  
  private void changeItemCount(String itemNumber, int delta) {
    ShoppingCartItem item = new ShoppingCartItem(itemNumber);
    if (items.contains(item)) {
      // change the count for this item
      ShoppingCartItem existingItem;
      existingItem = (ShoppingCartItem)items.get(items.indexOf(item));
      existingItem.incrementCount(delta);
      if (existingItem.getCount() <= 0) {
        items.remove(existingItem);
      }
    }
    else {
      // new item, store it if positive change
      if (delta > 0) {
        items.add(item);
      }
    }
  }

}
