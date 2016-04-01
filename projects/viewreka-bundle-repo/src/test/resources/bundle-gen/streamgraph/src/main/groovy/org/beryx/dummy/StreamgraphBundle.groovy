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
package org.beryx.dummy

import org.beryx.viewreka.bundle.api.CodeTemplate
import org.beryx.viewreka.bundle.util.AbstractFxBundle
import org.beryx.viewreka.core.Version

class StreamgraphBundle extends AbstractFxBundle {
    @Override List<String> getCategories() { ['chart', 'parameter'] }
    @Override String getId() { 'streamgraph' }
    @Override String getName() { 'Streamgraph chart and color parameter' }
    @Override Version getVersion() { new Version(5, 1, 0) }
    @Override String getDescription() { 'Streamgraph: a stacked curvilinear area graph displaced around a central axis' }
    @Override List<CodeTemplate> getTemplates() { [] }
}
