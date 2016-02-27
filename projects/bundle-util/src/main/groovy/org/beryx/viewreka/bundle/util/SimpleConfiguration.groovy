package org.beryx.viewreka.bundle.util

import org.beryx.viewreka.bundle.api.CodeTemplate
import org.beryx.viewreka.model.ProjectModel

class SimpleConfiguration implements CodeTemplate.Configuration {
    final ProjectModel<?> projectModel
    final CodeTemplate.Context context;

    /** Convenience constructor that sets an empty {@link SimpleContext} */
    SimpleConfiguration(ProjectModel<?> projectModel) {
        this(projectModel, new SimpleContext())
    }

    SimpleConfiguration(ProjectModel<?> projectModel, CodeTemplate.Context context) {
        this.projectModel = projectModel
        this.context = context
    }
}
