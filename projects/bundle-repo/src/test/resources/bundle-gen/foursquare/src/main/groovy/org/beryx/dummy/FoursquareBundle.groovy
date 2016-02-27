package org.beryx.dummy

import org.beryx.viewreka.bundle.api.CodeTemplate
import org.beryx.viewreka.bundle.util.AbstractFxBundle
import org.beryx.viewreka.core.Version

class FoursquareBundle extends AbstractFxBundle {
    @Override List<String> getCategories() { ['datasource'] }
    @Override String getId() { 'foursquare' }
    @Override String getName() { 'Foursquare Data' }
    @Override Version getVersion() { new Version(1, 1, 0) }
    @Override String getDescription() { 'Foursquare API Data Source' }
    @Override List<CodeTemplate> getTemplates() { [] }
}
