package org.beryx.vbundle.oracle

import javafx.scene.Node
import org.beryx.viewreka.bundle.util.AbstractParameterizedTemplateSpec
import org.beryx.viewreka.bundle.util.ParameterizedTemplate
import spock.lang.Requires

import static org.testfx.api.FxAssert.verifyThat
import static org.testfx.matcher.base.NodeMatchers.hasText
import static org.hamcrest.core.StringContains.*

@Requires({sys.testfx == 'true'})
class OracleBundleSpec extends AbstractParameterizedTemplateSpec {
    @Override
    ParameterizedTemplate createTemplate() {
        return new OracleBundle().getTemplate("datasource (Oracle)")
    }


    def "should not use invalid configuration"() {
        when: "Change the connection string to include the non-existing host org.example.db"
        fx.lookup('#prm-connection').queryFirst().text = 'jdbc:oracle:thin:@org.example.db:1521:orcl'

        and: "click the OK button"
        fx.clickOn '#butOk'

        then: "the value of the connection parameter is recognized as invalid"
        verifyThat '.header-panel > .label', hasText(containsString('SQLRecoverableException'))


        when: "choosing to use the invalid configuration"
        Set<Node> butNodes = fx.lookup(".button-bar > .container > .button").queryAll()
        Node butYes = getForPseudoClass(butNodes, "default")
        fx.clickOn butYes

        then: "the values have been correctly read"
        cfgPane.values == [
                dsName: "dsOracle",
                connection: "jdbc:oracle:thin:@org.example.db:1521:orcl",
                user: "scott",
                password: "tiger"
        ]
        and: "the merge operation returns the correct code fragment"
        template.mergeTemplate(cfgPane.values) == '''
                datasource dsOracle(type: sql) {
                    driver = 'oracle.jdbc.OracleDriver'
                    connection = 'jdbc:oracle:thin:@org.example.db:1521:orcl'
                    user = 'scott'
                    password = 'tiger'
                }
                '''.stripIndent()
    }
}
