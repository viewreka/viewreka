package org.beryx.viewreka.bundle.util
import javafx.scene.Node
import javafx.scene.control.PasswordField
import spock.lang.Requires

import static org.testfx.api.FxAssert.verifyThat
import static org.testfx.matcher.base.NodeMatchers.hasText

@Requires({sys.testfx == 'true'})
class ParameterizedTemplateSpec extends AbstractParameterizedTemplateSpec {
    private static final String PORT_RESTRICTION_ERR_MSG = 'Only connections on port 1522 are allowed'

    @Override
    ParameterizedTemplate createTemplate() {
        return new ParameterizedTemplate.Builder("Oracle data source", 'datasource', '''
                datasource dbWB(type : sql) {
                    driver = 'oracle.jdbc.OracleDriver'
                    connection = '<connection>'
                    user = '<user>'
                    password = '<password>'
                }'''.stripIndent()
        )
                .withParameter("connection", "JDBC connection string", "jdbc:oracle:thin:@localhost:1521:orcl", false)
                .withParameter("user", "user name", "scott", true)
                .withParameter(new SimpleParameter.Builder("password", "tiger")
                .withDescription("user password")
                .withOptional(true)
                .withControl(PasswordField.class, { field -> field.text}, { field, txt -> field.text = txt}).build()
        )
                .withValidator {values -> values.connection.contains(':1522:') ? null : PORT_RESTRICTION_ERR_MSG}
                .build()
    }

    def "should not use invalid configuration"() {
        when: "Click on the OK button without changing the sample parameter values"
        fx.clickOn '#butOk'

        then: "the value of the connection parameter is recognized as invalid"
        verifyThat '.header-panel > .label', hasText(PORT_RESTRICTION_ERR_MSG)


        when: "choosing to not use the invalid configuration"
        Set<Node> butNodes = fx.lookup(".button-bar > .container > .button").queryAll()
        Node butNo = getForPseudoClass(butNodes, "cancel")
        fx.clickOn butNo
        fx.clickOn '#butCancel'

        then: "all values have been cleared"
        cfgPane.values == [:]
        and: "the merge operation returns null"
        template.mergeTemplate(cfgPane.values) == null
    }

    def "should use invalid configuration if the user wants it"() {
        when: "Click on the OK button without changing the sample parameter values"
        fx.clickOn '#butOk'

        then: "the value of the connection parameter is recognized as invalid"
        verifyThat '.header-panel > .label', hasText(PORT_RESTRICTION_ERR_MSG)


        when: "choosing to use the invalid configuration"
        Set<Node> butNodes = fx.lookup(".button-bar > .container > .button").queryAll()
        Node butYes = getForPseudoClass(butNodes, "default")
        fx.clickOn butYes

        then: "the values have been correctly read"
        cfgPane.values == [
                connection: "jdbc:oracle:thin:@localhost:1521:orcl",
                user: "scott",
                password: "tiger"
        ]
        and: "the merge operation returns the correct code fragment"
        template.mergeTemplate(cfgPane.values) == '''
                datasource dbWB(type : sql) {
                    driver = 'oracle.jdbc.OracleDriver'
                    connection = 'jdbc:oracle:thin:@localhost:1521:orcl'
                    user = 'scott'
                    password = 'tiger'
                }
                '''.stripIndent()
    }


    def "should omit lines related to optional parameters with no value"() {
        when: "Change the port to 1522 in the connection string"
        fx.lookup('#prm-connection').queryFirst().text = 'jdbc:oracle:thin:@localhost:1522:orcl'
        and: "clear the user field"
        fx.lookup('#prm-user').queryFirst().clear()
        and: "clear the password field"
        fx.lookup('#prm-password').queryFirst().clear()
        and: "click the OK button"
        fx.clickOn '#butOk'

        then: "the values have been correctly read"
        cfgPane.values == [
                connection: "jdbc:oracle:thin:@localhost:1522:orcl",
                user: "",
                password: ""
        ]
        and: "the lines related to user and password do not appear in the generated code fragment"
        template.mergeTemplate(cfgPane.values) == '''
                datasource dbWB(type : sql) {
                    driver = 'oracle.jdbc.OracleDriver'
                    connection = 'jdbc:oracle:thin:@localhost:1522:orcl'
                }
                '''.stripIndent()
    }
}
