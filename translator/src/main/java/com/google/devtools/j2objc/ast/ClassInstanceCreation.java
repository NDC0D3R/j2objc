/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.devtools.j2objc.ast;

import com.google.devtools.j2objc.jdt.BindingConverter;
import java.util.List;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import org.eclipse.jdt.core.dom.IMethodBinding;

/**
 * Node type for constructing a new instance. (e.g. "new Foo()")
 */
public class ClassInstanceCreation extends Expression {

  private ExecutableElement method = null;
  // Indicates that this expression leaves the created object with a retain
  // count of 1. (i.e. does not call autorelease)
  private boolean hasRetainedResult = false;
  private ChildLink<Expression> expression = ChildLink.create(Expression.class, this);
  // Used by anonymous classes where we have two outer scopes, one for the class and one for the
  // superclass.
  private ChildLink<Expression> superOuterArg = ChildLink.create(Expression.class, this);
  private ChildLink<Type> type = ChildLink.create(Type.class, this);
  private ChildList<Expression> arguments = ChildList.create(Expression.class, this);
  private ChildLink<AnonymousClassDeclaration> anonymousClassDeclaration =
      ChildLink.create(AnonymousClassDeclaration.class, this);

  public ClassInstanceCreation() {}

  public ClassInstanceCreation(ClassInstanceCreation other) {
    super(other);
    method = other.getExecutableElement();
    hasRetainedResult = other.hasRetainedResult();
    expression.copyFrom(other.getExpression());
    superOuterArg.copyFrom(other.getSuperOuterArg());
    type.copyFrom(other.getType());
    arguments.copyFrom(other.getArguments());
    anonymousClassDeclaration.copyFrom(other.getAnonymousClassDeclaration());
  }

  public ClassInstanceCreation(ExecutableElement method, Type type) {
    this.method = method;
    this.type.set(type);
  }

  public ClassInstanceCreation(IMethodBinding methodBinding, Type type) {
    method = BindingConverter.getExecutableElement(methodBinding);
    this.type.set(type);
  }

  public ClassInstanceCreation(IMethodBinding methodBinding) {
    method = BindingConverter.getExecutableElement(methodBinding);
    type.set(Type.newType(methodBinding.getDeclaringClass()));
  }

  @Override
  public Kind getKind() {
    return Kind.CLASS_INSTANCE_CREATION;
  }

  public IMethodBinding getMethodBinding() {
    return (IMethodBinding) BindingConverter.unwrapElement(method);
  }

  public void setMethodBinding(IMethodBinding methodBinding) {
    method = BindingConverter.getExecutableElement(methodBinding);
  }

  public ExecutableElement getExecutableElement() {
    return method;
  }

  public ClassInstanceCreation setExecutableElement(ExecutableElement element) {
    method = element;
    return this;
  }

  @Override
  public TypeMirror getTypeMirror() {
    return method != null
        ? method.getEnclosingElement().asType() : null;
  }

  public boolean hasRetainedResult() {
    return hasRetainedResult;
  }

  public void setHasRetainedResult(boolean hasRetainedResult) {
    this.hasRetainedResult = hasRetainedResult;
  }

  public Expression getExpression() {
    return expression.get();
  }

  public ClassInstanceCreation setExpression(Expression newExpression) {
    expression.set(newExpression);
    return this;
  }

  public Expression getSuperOuterArg() {
    return superOuterArg.get();
  }

  public ClassInstanceCreation setSuperOuterArg(Expression newSuperOuterArg) {
    superOuterArg.set(newSuperOuterArg);
    return this;
  }

  public Type getType() {
    return type.get();
  }

  public ClassInstanceCreation setType(Type newType) {
    type.set(newType);
    return this;
  }

  public ClassInstanceCreation addArgument(Expression arg) {
    arguments.add(arg);
    return this;
  }

  public ClassInstanceCreation addArgument(int index, Expression arg) {
    arguments.add(index, arg);
    return this;
  }

  public Expression getArgument(int index) {
    return arguments.get(index);
  }

  public List<Expression> getArguments() {
    return arguments;
  }

  public AnonymousClassDeclaration getAnonymousClassDeclaration() {
    return anonymousClassDeclaration.get();
  }

  public ClassInstanceCreation setAnonymousClassDeclaration(
      AnonymousClassDeclaration newAnonymousClassDeclaration) {
    anonymousClassDeclaration.set(newAnonymousClassDeclaration);
    return this;
  }

  @Override
  protected void acceptInner(TreeVisitor visitor) {
    if (visitor.visit(this)) {
      expression.accept(visitor);
      superOuterArg.accept(visitor);
      type.accept(visitor);
      arguments.accept(visitor);
      anonymousClassDeclaration.accept(visitor);
    }
    visitor.endVisit(this);
  }

  @Override
  public ClassInstanceCreation copy() {
    return new ClassInstanceCreation(this);
  }
}
