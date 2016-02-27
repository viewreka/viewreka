package org.beryx.vbundle.mysql

import javafx.scene.Node
import org.beryx.viewreka.bundle.util.AbstractParameterizedTemplateSpec
import org.beryx.viewreka.bundle.util.ParameterizedTemplate
import spock.lang.Requires

import static org.testfx.api.FxAssert.verifyThat
import static org.testfx.matcher.base.NodeMatchers.hasText
import static org.hamcrest.core.StringContains.*

@Requires({sys.testfx == 'true'})
class MySqlBundleSpec extends AbstractParameterizedTemplateSpec {
    @Override
    ParameterizedTemplate createTemplate() {
        return new MySqlBundle().getTemplate("datasource (MySQL)")
    }


    def "should not use invalid configuration"() {
        given: "a connection string that includes an inexistent directory name"
        def invalidConnString = 'jdbc:mysql://org.example.db/mydb'
        
        when: "using the invalid connection string"
        fx.lookup('#prm-connection').queryFirst().text = invalidConnString

        and: "click the OK button"
        fx.clickOn '#butOk'

        then: "the value of the connection parameter is recognized as invalid"
        verifyThat '.header-panel > .label', hasText(containsString('CommunicationsException'))


        when: "choosing to use the invalid configuration"
        Set<Node> butNodes = fx.lookup(".button-bar > .container > .button").queryAll()
        Node butYes = getForPseudoClass(butNodes, "default")
        fx.clickOn butYes

        then: "the values have been correctly read"
        cfgPane.values == [
                dsName: "dsMySql",
                connection: invalidConnString,
                user: 'john',
                password: 'p@$$w0rd'
        ]
        and: "the merge operation returns the correct code fragment"
        template.mergeTemplate(cfgPane.values) == """
                datasource dsMySql(type: sql) {
                    driver = 'com.mysql.jdbc.Driver'
                    connection = '$invalidConnString'
                    user = 'john'
                    password = 'p@\$\$w0rd'
                }
                """.stripIndent()
    }
}
