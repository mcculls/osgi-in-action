package org.foo.condition.ask;

import java.util.*;
import java.security.*;
import org.osgi.framework.*;
import org.osgi.service.condpermadmin.*;

public class AskUserCondition implements Condition { 
  private final Bundle m_bundle; 
  private final String m_question; 
  private final boolean m_not; 
  private volatile boolean m_result = false;
  private volatile boolean m_isMutable = true;

  public AskUserCondition(Bundle bundle, ConditionInfo info) {           
    m_bundle = bundle; 
    m_question = info.getArgs()[0].replace( 
      "$symbolic-name", bundle.getSymbolicName()); 
    m_not = (info.getArgs().length == 2 && "!".equals(info.getArgs()[1])); 
  } 
  public static Condition getCondition(Bundle bundle, ConditionInfo info) { 
    return new AskUserCondition(bundle, info);                                 
  } 
  public boolean isMutable() {                                           
    return m_isMutable; 
  } 
  public boolean isPostponed() {                                        
    return true; 
  } 
  public boolean isSatisfied() {     
    return m_result;                                     
  }
  public boolean isSatisfied(Condition[] conditions, Dictionary context) { 
    if (!m_isMutable) { 
      return m_result;
    }
    Boolean result = ((Boolean) AccessController.doPrivileged(                 
        new PrivilegedAction() { 
          public Object run() { 
            AskTheUser question = new AskTheUser(m_question);           
            try { 
              return question.ask() ? Boolean.TRUE : Boolean.FALSE;       
            } catch (Exception e) {
              return Boolean.FALSE;                                        
          } 
        } 
      })); 
    m_isMutable = false; 
    if (m_not) {
      return (m_result = !result.booleanValue());           
    } 
    else { 
      return (m_result = result.booleanValue());             
    } 
  }
} 

