package org.foo.dosgi.hooks;

import java.util.Collection;

import org.apache.felix.sigil.common.osgi.ExprVisitor;
import org.apache.felix.sigil.common.osgi.LDAPExpr;
import org.apache.felix.sigil.common.osgi.LDAPParser;
import org.apache.felix.sigil.common.osgi.SimpleTerm;
import org.foo.dosgi.helper.LogUtil;
import org.foo.dosgi.helper.RegistryWatcher;
import org.osgi.framework.Constants;
import org.osgi.framework.hooks.service.ListenerHook;

public class ImportedServiceListenerHook implements ListenerHook {

  private final RegistryWatcher watcher;

  public ImportedServiceListenerHook(RegistryWatcher watcher) {
    this.watcher = watcher;
  }

  public void added(Collection listeners) {
    for (final ListenerInfo info : (Collection<ListenerInfo>) listeners) {
      if (!info.isRemoved()) {
        LogUtil.info("Adding listener " + info);
        LDAPExpr expr = LDAPParser.parseExpression(info.getFilter());
        expr.visit(new ExprVisitor() {
          public void visitExpr(LDAPExpr expr) {
            if (expr instanceof SimpleTerm) {
              SimpleTerm term = (SimpleTerm) expr;
              if (term.getName().equals(Constants.OBJECTCLASS)) {
                watcher.addWatch(term.getRval(), info.getFilter());
              }
            }
          }
        });
      }
    }
  }

  public void removed(Collection listeners) {
    for (final ListenerInfo info : (Collection<ListenerInfo>) listeners) {
      LDAPExpr expr = LDAPParser.parseExpression(info.getFilter());
      expr.visit(new ExprVisitor() {
        public void visitExpr(LDAPExpr expr) {
          if (expr instanceof SimpleTerm) {
            SimpleTerm term = (SimpleTerm) expr;
            if (term.getName().equals(Constants.OBJECTCLASS)) {
              watcher.removeWatch(term.getRval(), info.getFilter());
            }
          }
        }
      });
    }
  }
}
