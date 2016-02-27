package org.beryx.vbundle.derby

import javafx.scene.Node
import org.beryx.viewreka.bundle.util.AbstractParameterizedTemplateSpec
import org.beryx.viewreka.bundle.util.ParameterizedTemplate
import spock.lang.Requires

import static org.testfx.api.FxAssert.verifyThat
import static org.testfx.matcher.base.NodeMatchers.hasText
import static org.hamcrest.text.StringContainsInOrder.*

@Requires({sys.testfx == 'true'})
class DerbyBundleSpec extends AbstractParameterizedTemplateSpec {
    @Override
    ParameterizedTemplate createTemplate() {
        return new DerbyBundle().getTemplate("datasource (Derby)")
    }


    def "should not use invalid configuration"() {
        given: "a connection string that includes an inexistent directory name"
        def invalidConnString = 'jdbc:derby:__inexistent_-_dir__/mydb'

        when: "using the invalid connection string"
        fx.lookup('#prm-connection').queryFirst().text = invalidConnString

        and: "click the OK button"
        fx.clickOn '#butOk'

        then: "the value of the connection parameter is recognized as invalid"
        verifyThat '.header-panel > .label', hasText(stringContainsInOrder(['Database', 'not found.']))


        when: "choosing to use the invalid configuration"
        Set<Node> butNodes = fx.lookup(".button-bar > .container > .button").queryAll()
        Node butYes = getForPseudoClass(butNodes, "default")
        fx.clickOn butYes

        then: "the values have been correctly read"
        cfgPane.values == [
                dsName: 'dsDerby',
                connection: invalidConnString,
                user: 'john',
                password: 'p@$$w0rd'
        ]
        and: "the merge operation returns the correct code fragment"
        template.mergeTemplate(cfgPane.values) == """
                datasource dsDerby(type: sql) {
                    driver = 'org.apache.derby.jdbc.EmbeddedDriver'
                    connection = '$invalidConnString'
                    user = 'john'
                    password = 'p@\$\$w0rd'
                }
                """.stripIndent()
    }
}
