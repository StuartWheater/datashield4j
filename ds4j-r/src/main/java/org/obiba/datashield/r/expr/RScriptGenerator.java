/*
 * Copyright (c) 2018 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.obiba.datashield.r.expr;


import org.obiba.datashield.core.DSEnvironment;
import org.obiba.datashield.core.DSMethod;

/**
 * Generates RScript from a DataSHIELD script.
 */
public class RScriptGenerator implements DataShieldGrammarVisitor {

  private final DSEnvironment environment;

  public RScriptGenerator(DSEnvironment environment) {
    this.environment = environment;
  }

  public String toScript(SimpleNode node) {
    StringBuilder sb = new StringBuilder();
    node.jjtAccept(this, sb);
    return sb.toString();
  }

  @Override
  public Object visit(ASTsymbol node, Object data) {
    StringBuilder sb = (StringBuilder) data;
    sb.append(node.value);
    return data;
  }

  @Override
  public Object visit(ASTstring node, Object data) {
    StringBuilder sb = (StringBuilder) data;
    sb.append(node.value);
    return data;
  }

  @Override
  public Object visit(ASTfuncCall node, Object data) {
    StringBuilder sb = (StringBuilder) data;
    sb.append(findMethod(node.value.toString()).invoke(environment.getMethodType())).append("( ");
    for (int i = 0; i < node.jjtGetNumChildren(); i++) {
      Node child = node.jjtGetChild(i);
      if (i > 0) sb.append(',');
      child.jjtAccept(this, sb);
    }
    sb.append(" )");
    return sb;
  }

  @Override
  public Object visit(ASTsubsetCall node, Object data) {
    StringBuilder sb = (StringBuilder) data;
    sb.append(node.value).append("[ ");
    for (int i = 0; i < node.jjtGetNumChildren(); i++) {
      Node child = node.jjtGetChild(i);
      if (i > 0) sb.append(',');
      child.jjtAccept(this, sb);
    }
    sb.append(" ]");
    return sb;
  }

  @Override
  public Object visit(ASTBinaryOp node, Object data) {
    StringBuilder sb = (StringBuilder) data;
    sb.append("base::'").append(node.value).append("'").append("( ");
    for (int i = 0; i < node.jjtGetNumChildren(); i++) {
      Node child = node.jjtGetChild(i);
      if (i > 0) sb.append(',');
      child.jjtAccept(this, sb);
    }
    sb.append(" )");
    return sb;
  }

  @Override
  public Object visit(ASTroot node, Object data) {
    StringBuilder sb = (StringBuilder) data;
    node.childrenAccept(this, data);
    return sb;
  }

  @Override
  public Object visit(SimpleNode node, Object data) {
    return node.childrenAccept(this, data);
  }

  private DSMethod findMethod(String name) {
    return environment.getMethod(name);
  }
}
