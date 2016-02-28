/*
 * Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
