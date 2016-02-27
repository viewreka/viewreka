package org.beryx.dummy

import org.beryx.viewreka.bundle.api.CodeTemplate
import org.beryx.viewreka.bundle.util.AbstractFxBundle
import org.beryx.viewreka.core.Version

class FacebookBundle_1_7 extends AbstractFxBundle {
    @Override List<String> getCategories() { ['datasource'] }
    @Override String getId() { 'facebook' }
    @Override String getName() { 'Facebook Data' }
    @Override Version getVersion() { new Version(1, 7, 0) }
    @Override String getDescription() { 'Facebook Graph API Data Source' }
    @Override List<CodeTemplate> getTemplates() { [] }
}
