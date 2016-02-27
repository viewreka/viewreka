package org.beryx.dummy

import org.beryx.viewreka.bundle.api.CodeTemplate
import org.beryx.viewreka.bundle.util.AbstractFxBundle
import org.beryx.viewreka.core.Version

class RadarBundle extends AbstractFxBundle {
    @Override List<String> getCategories() { ['chart'] }
    @Override String getId() { 'radar' }
    @Override String getName() { 'Radar Chart' }
    @Override Version getVersion() { new Version(3, 0, 0, 'beta', true) }
    @Override String getDescription() { 'The radar chart' }
    @Override List<CodeTemplate> getTemplates() { [] }
}
