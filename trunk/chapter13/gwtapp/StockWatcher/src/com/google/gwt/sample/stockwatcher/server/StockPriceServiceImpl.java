package com.google.gwt.sample.stockwatcher.server;

import java.util.Collections;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.foo.stockprovider.StockProvider;
import org.osgi.framework.BundleContext;

import com.google.gwt.sample.stockwatcher.client.DelistedException;
import com.google.gwt.sample.stockwatcher.client.ServiceUnavailableException;
import com.google.gwt.sample.stockwatcher.client.StockPrice;
import com.google.gwt.sample.stockwatcher.client.StockPriceService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.osgi.util.tracker.ServiceTracker;

@SuppressWarnings("serial")
public class StockPriceServiceImpl extends RemoteServiceServlet implements StockPriceService {

  private ServiceTracker tracker;

  @Override
  public void init() throws ServletException {
    BundleContext ctx = (BundleContext) getServletContext().getAttribute(OSGiConstants.OSGI_BUNDLE_CONTEXT_ATTRIBUTE);
    tracker = new ServiceTracker(ctx, StockProvider.class.getName(), null);
    tracker.open();
  }

  @Override
  public void destroy() {
    tracker.close();
    tracker = null;
  }

  public StockPrice[] getPrices(String[] symbols) throws DelistedException, ServiceUnavailableException {
    StockPrice[] prices = null;
    
    StockProvider provider = (StockProvider) tracker.getService();
    if ( provider != null ) {
      prices = readPrices(provider, symbols);
    }
    
    if ( prices == null ) {
      throw new ServiceUnavailableException(); 
    }
    
    return prices;
  }

  private StockPrice[] readPrices(StockProvider provider, String[] symbols) throws DelistedException {
    StockPrice[] prices = new StockPrice[symbols.length];
    Map<String, Double> stocks = provider.getStocks(symbols);
    
    for (int i=0; i<symbols.length; i++) {
      Double newPrice = stocks.get( symbols[i] );
      Map<String, Double> last = updateStockPrices(stocks);
      if ( newPrice == null ) {
        throw new DelistedException(symbols[i]);
      }
      else {
        Double oldPrice = last.get( symbols[i] );
        prices[i] = updatePrice(symbols[i], oldPrice, newPrice);
      }
    }
    
    return prices;
  }

  @SuppressWarnings("unchecked")
  private Map<String, Double> updateStockPrices(Map<String, Double> stocks) {
    final String key = StockPrice.class.getName();
    final HttpServletRequest req = getThreadLocalRequest();
    final HttpSession session = req.getSession();
    
    Map<String, Double> last = (Map<String, Double>) session.getAttribute(key);
    
    if ( last == null ) {
//      System.out.println( "Created stocks history" );
      last = Collections.emptyMap();
    }
    else {
//      System.out.println( "Found stocks history " + last );
    }
    
//    System.out.println( "Set stocks history " + stocks );
    session.setAttribute(key, stocks);
    
    return last;
  }

  private StockPrice updatePrice(String symbol, Double oldPrice, Double newPrice) {
    double change = oldPrice == null ? 0 : (newPrice - oldPrice);
    return new StockPrice(symbol, newPrice, change);
  }
}
