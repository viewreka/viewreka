package org.beryx.viewreka.dsl.transform

import groovy.util.logging.Slf4j;

import java.util.Map;

import org.beryx.viewreka.core.ViewrekaException;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.MethodCall;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.NamedArgumentListExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;

@Slf4j
public class KeywordVisitor extends CodeVisitorSupport {
	// indexed by keyword name; the inner map (the value) is indexed by alias
	final Map<String, Map<String, Class<? extends AliasHandler<?>>>> aliasMaps = [:]
	final Map<String, Boolean> stringConvert = [:]

	KeywordVisitor(Keyword... keywords) {
		keywords.each { Keyword k -> aliasMaps[k.name] = (k.aliasHandlers ?: [:])}
	}

	@Override
	public void visitMethodCallExpression(MethodCallExpression call) {
		log.trace "\n\n${'-'*60}\nmethodAsString: ${call.methodAsString} \n\ttext: ${call.text} \n\tmethodText: ${call.method.text}"

		Map<String, Class<? extends AliasHandler<?>>> aliasHandlers = aliasMaps[call.methodAsString]
		if(aliasHandlers != null) {
			log.trace "call arguments: ${call.arguments}"
			if(call.arguments instanceof ArgumentListExpression) {
				ArgumentListExpression argList = call.arguments
				List<Expression> expressions = argList.getExpressions()
				if(expressions?.size() == 1 && expressions[0] instanceof MethodCall) {
					MethodCall mCall = expressions[0]
					if(mCall.arguments instanceof ArgumentListExpression) {
						ArgumentListExpression mCallArgs = mCall.arguments
						mCallArgs.expressions.add(0, new ConstantExpression(mCall.methodAsString));
						call.arguments = mCallArgs

						List<GroovyCodeVisitor> aliasCodeVisitors = []
						mCallArgs.expressions.each {expr ->
							log.trace "expr: $expr"
							if(expr instanceof MapExpression) {
								MapExpression mapExpr = expr
								applySubstitution(mapExpr, aliasHandlers, aliasCodeVisitors)
							}
							if(expr instanceof ClosureExpression) {
								aliasCodeVisitors.each {
									aliasCodeVisitor -> expr.visit(aliasCodeVisitor)
								}
							}
						}
					} else if(mCall.arguments instanceof TupleExpression) {
						List<GroovyCodeVisitor> aliasCodeVisitors = []
						TupleExpression tupleExpr = mCall.arguments
						tupleExpr.expressions.each { tupleItem ->
							if(tupleItem instanceof NamedArgumentListExpression) {
								List<MapEntryExpression> mapEntryExprList = []
								MapExpression mapExpr = new MapExpression(mapEntryExprList)
								NamedArgumentListExpression nalExpr = tupleItem
								nalExpr.mapEntryExpressions.each { mapEntryExpr -> mapEntryExprList << mapEntryExpr}

								List<Expression> exprList = []
								ArgumentListExpression mCallArgs = new ArgumentListExpression(exprList)
								mCallArgs.addExpression(new ConstantExpression(mCall.methodAsString))
								mCallArgs.addExpression(mapExpr)
								call.arguments = mCallArgs

								applySubstitution(nalExpr, aliasHandlers, aliasCodeVisitors)
							}
						}
					}
				}
			} else if(call.arguments instanceof TupleExpression) {
				List<GroovyCodeVisitor> aliasCodeVisitors = []
				TupleExpression tupleExpr = call.arguments
				tupleExpr.expressions.each { tupleItem ->
					if(tupleItem instanceof NamedArgumentListExpression) {
						NamedArgumentListExpression nalExpr = tupleItem
						applySubstitution(nalExpr, aliasHandlers, aliasCodeVisitors)
					}
				}
			}
		}

		super.visitMethodCallExpression(call);
	}

	private void applySubstitution(MapExpression mapExpr, Map<String, Class<? extends AliasHandler<?>>> aliasHandlers, List<GroovyCodeVisitor> aliasCodeVisitors) {
		mapExpr.mapEntryExpressions.each { entryExpr ->
			log.trace "\t--- entryExpr: $entryExpr"
			if(entryExpr.keyExpression instanceof ConstantExpression) {
				ConstantExpression keyExpr = entryExpr.keyExpression
				log.trace "\t\t--- key: $keyExpr.value"
				Class<? extends AliasHandler<?>> handlerClass = aliasHandlers[keyExpr.value]
				String exprName = null
				if(entryExpr.valueExpression instanceof VariableExpression) {
					exprName = ((VariableExpression)entryExpr.valueExpression).name
				} else if(entryExpr.valueExpression instanceof ClassExpression) {
					exprName = ((ClassExpression)entryExpr.valueExpression).text
				}
				if(exprName) {
					if(handlerClass == ToStringHandler) {
							log.trace "\t\t\t--- Applying ToStringHandler"
							ConstantExpression constExpr = new ConstantExpression(exprName)
						log.trace "\t\t\t\t--- Substituted with $constExpr"
						entryExpr.valueExpression = constExpr
					} else if(handlerClass) {
						log.trace "\t\t\t--- Searching substitute for exprName: $exprName"

						Class aliasedClass = null
						GroovyCodeVisitor aliasCodeVisitor = null
						Class<? extends AliasHandler<?>> lastHandlerClass = null
						try {
							log.trace "\t\t\t--- Loading service: $handlerClass.name"
							ServiceLoader svcLoader = ServiceLoader.load(handlerClass)
							log.trace "\t\t\t--- ServiceLoader ok"
							svcLoader.iterator().each { AliasHandler<?> h ->
								log.trace "\t\t\t\t--- Evaluating service $h"
								if(h.aliases.contains(exprName)) {
									if(aliasedClass) {
										log.warn("Multiple classes associated with alias $exprName: "
											+ "${lastHandlerClass?.name} / ${aliasedClass.name} and ${((Object)h).class.name} / ${h.aliasClass?.name}")
									}
									lastHandlerClass = ((Object)h).class
									aliasedClass = h.aliasClass
									aliasCodeVisitor = h.codeVisitor
								}
							}
						} catch (ServiceConfigurationError e) {
							e.printStackTrace()
							throw new ViewrekaException("Invalid ${handlerClass.name} configuration", e)
						}

						if(!aliasedClass) throw new ViewrekaException("No ${handlerClass.name} implementation associated with alias '$exprName'")

						if(aliasCodeVisitor) {
							log.trace "Adding aliasCodeVisitor $aliasCodeVisitor"
							aliasCodeVisitors << aliasCodeVisitor
						}

						ClassExpression classExpr = new ClassExpression(new ClassNode(aliasedClass))
						log.trace "\t\t\t\t--- Substituted with $classExpr"
						entryExpr.valueExpression = classExpr
					}
				}
			}
		}
	}
}

