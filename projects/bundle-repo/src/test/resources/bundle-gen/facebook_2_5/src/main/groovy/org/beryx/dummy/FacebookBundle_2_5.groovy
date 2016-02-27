package org.beryx.dummy

import org.beryx.viewreka.bundle.api.CodeTemplate
import org.beryx.viewreka.bundle.util.AbstractFxBundle
import org.beryx.viewreka.core.Version

class FacebookBundle_2_5 extends AbstractFxBundle {
    @Override List<String> getCategories() { ['datasource'] }
    @Override String getId() { 'facebook' }
    @Override String getName() { 'Facebook Data' }
    @Override Version getVersion() { new Version(2, 5, 0) }
    @Override String getDescription() { 'Facebook Graph API Data Source' }
    @Override List<CodeTemplate> getTemplates() { [] }
}
