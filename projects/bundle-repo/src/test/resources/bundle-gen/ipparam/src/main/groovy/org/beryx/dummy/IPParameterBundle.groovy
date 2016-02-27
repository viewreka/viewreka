package org.beryx.dummy

import org.beryx.viewreka.bundle.api.CodeTemplate
import org.beryx.viewreka.bundle.util.AbstractFxBundle
import org.beryx.viewreka.core.Version

class IPParameterBundle extends AbstractFxBundle {
    @Override List<String> getCategories() { ['parameter'] }
    @Override String getId() { 'ip-param' }
    @Override String getName() { 'IP parameter' }
    @Override Version getVersion() { new Version(1, 0, 0) }
    @Override String getDescription() { 'IPv4 and IPv6 parameters' }
    @Override List<CodeTemplate> getTemplates() { [] }
}
