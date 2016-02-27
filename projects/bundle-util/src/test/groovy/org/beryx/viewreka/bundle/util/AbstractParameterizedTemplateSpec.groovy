package org.beryx.viewreka.bundle.util
import javafx.scene.Node
import org.beryx.viewreka.fxcommons.FxSpec

abstract class AbstractParameterizedTemplateSpec extends FxSpec {
    ParameterizedTemplate template
    ParameterConfigPane cfgPane

    abstract ParameterizedTemplate createTemplate();

    def setup() {
        setupStage { stage ->
            template = createTemplate()
            cfgPane = new ParameterConfigPane(stage, template)
            return cfgPane
        }
    }

    protected Node getForPseudoClass(Set<Node> nodes, String pseudoClass) {
        nodes.findAll {node -> node.pseudoClassStates.any {cls -> pseudoClass == cls.pseudoClassName}}.find()
    }
}
