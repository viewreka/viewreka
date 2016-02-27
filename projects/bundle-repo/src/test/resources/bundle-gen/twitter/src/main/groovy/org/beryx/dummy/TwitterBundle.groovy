package org.beryx.dummy

import org.beryx.viewreka.bundle.api.CodeTemplate
import org.beryx.viewreka.bundle.util.AbstractFxBundle
import org.beryx.viewreka.core.Version

class TwitterBundle extends AbstractFxBundle {
    @Override List<String> getCategories() { ['datasource'] }
    @Override String getId() { 'twitter' }
    @Override String getName() { 'Twitter Data' }
    @Override Version getVersion() { new Version(2, 1, 6) }
    @Override String getDescription() { 'Twitter API Data Source' }
    @Override List<CodeTemplate> getTemplates() { [] }
}
