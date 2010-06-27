package org.foo.condition.ask;

import javax.swing.*;

public class AskTheUser implements Runnable{ 
  private final String m_question; 
  private volatile boolean m_result; 
  
  public AskTheUser(String question) {                                   
    m_question = question; 
  } 
  public void run() { 
    m_result = (JOptionPane.YES_OPTION ==    
      JOptionPane.showConfirmDialog(null, m_question, "Security", JOptionPane.YES_NO_OPTION)); 
  } 
  public boolean ask() throws Exception {                                
    SwingUtilities.invokeAndWait(this); 
    return m_result; 
  } 
}
