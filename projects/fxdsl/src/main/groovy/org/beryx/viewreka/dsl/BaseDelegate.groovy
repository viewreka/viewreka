package org.beryx.viewreka.dsl

import org.beryx.viewreka.core.ViewrekaException;

/**
 * Helper class serving as base class for most closure delegates used for implementing the Viewreka DSL.
 */
class BaseDelegate {
    static final ON_MISSING_THROW = {name -> throw new ViewrekaException("Missing property: $name")}
    static final ON_MISSING_NULL = null
    static final ON_MISSING_NAME = {name -> name}

    final Map injectedProps = [:]
    final Closure missingStrategy

    public BaseDelegate(Closure missingStrategy = ON_MISSING_THROW) {
        this.missingStrategy = missingStrategy
    }

    Object propertyMissing(String name) {
        return injectedProps.containsKey(name) ? injectedProps[name] : missingStrategy?.call(name)
    }

    Object injectProperty(String name, Object value) {
        injectedProps[name] = value
    }

    void injectProperties(Map<String, ?> properties) {
        injectedProps << properties
    }

    void injectProperties(Collection<String> properties) {
        properties.each { prop -> injectedProps[prop] = prop}
    }
}
