package org.beryx.viewreka.dsl

import org.beryx.viewreka.fxui.FxProject
import org.beryx.viewreka.fxui.FxViewImpl
import org.beryx.viewreka.settings.ViewSettings

/**
 * Helper class used by the {@link ViewrekaScript} to create a view.
 */
class ViewBuilder {
    final FxProject project

    public ViewBuilder(FxProject project) {
        this.project = project
    }

    public FxViewImpl build(String viewId, Closure viewClosure) {
        def delegate = new ViewDelegate(viewId, project)
        viewClosure.delegate = delegate
        viewClosure.resolveStrategy = Closure.DELEGATE_FIRST
        viewClosure.call()

        ViewSettings viewSettings = project.projectSettingsManager.settings.getOrCreateViewSetting(viewId)
        FxViewImpl view = new FxViewImpl(delegate.name, delegate.description, viewSettings)
        return view
    }

}
