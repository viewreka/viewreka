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
