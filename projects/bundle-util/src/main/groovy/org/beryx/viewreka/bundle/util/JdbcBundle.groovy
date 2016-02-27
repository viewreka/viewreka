package org.beryx.viewreka.bundle.util

import groovy.util.logging.Slf4j
import javafx.scene.control.PasswordField
import org.beryx.viewreka.bundle.api.CodeTemplate

import java.sql.Connection
import java.sql.Driver
import java.sql.DriverManager

@Slf4j
abstract class JdbcBundle extends AbstractFxBundle {
    abstract String getDriverClassName()
    abstract String getSampleConnectionString()
    abstract String getMainTemplateName()

    String getSampleDataSourceName() {"ds${id.substring(id.lastIndexOf('.')+1).capitalize()}"}
    String getTemplateDescription() {"${toString()} data source"}
    String getSampleUser() {'john'}
    String getSamplePassword() {'p@$$w0rd'}

    /**
     * Helper class used to avoid class loader problems.
     */
    public static class DriverDecorator implements Driver {
        @Delegate
        private final Driver delegate

        public DriverDecorator(Driver delegate) {
            this.delegate = delegate
        }
    }

    @Override
    List<String> getCategories() {
        ['datasource']
    }

    @Override
    List<CodeTemplate> getTemplates() {
        return [
                new ParameterizedTemplate.Builder(mainTemplateName, 'datasource', """
                datasource <dsName>(type: sql) {
                    driver = '$driverClassName'
                    connection = '<connection>'
                    user = '<user>'
                    password = '<password>'
                }""".stripIndent()
            )
            .withDescription("Declare $name")
            .withParameter("dsName", "data source name", "$sampleDataSourceName", false)
            .withParameter("connection", "JDBC connection string", "$sampleConnectionString", false)
            .withParameter("user", "user name", "$sampleUser", true)
            .withParameter(new SimpleParameter.Builder("password", "$samplePassword")
                    .withDescription("user password")
                    .withOptional(true)
                    .withControl(PasswordField.class, { field -> field.text}, { field, txt -> field.text = txt}).build()
            )
            .withValidator{values -> checkConnection(values)}
            .build()
        ]
    }

    String checkConnection(Map<String, String> values) {
        Driver driver = null
        try {
            log.debug("checkConnection() - class loader for {}: {}", Thread.currentThread(), Thread.currentThread().getContextClassLoader());
            def driverClass = Class.forName(driverClassName, true, Thread.currentThread().contextClassLoader)
            driver = new DriverDecorator(driverClass.getConstructor().newInstance())
            DriverManager.registerDriver(driver)
            Connection connection = DriverManager.getConnection(values.connection, values.user, values.password)
            return connection.isValid(1) ? null : "Invalid connection"
        } catch (Exception e) {
            log.debug('Failed to create connection', e)
            return e.toString()
        } finally {
            try {
                if(driver) DriverManager.deregisterDriver()
            } catch (Exception e) {
                log.debug('Failed to deregister driver', e)
            }
        }
        return null
    }

    @Override
    String toString() {
        "$name $version"
    }
}
