package org.beryx.vbundle.derby
import groovy.util.logging.Slf4j
import org.beryx.viewreka.bundle.util.JdbcBundle
import org.beryx.viewreka.core.Version

@Slf4j
public class DerbyBundle extends JdbcBundle {
    @Override String getId() {'org.beryx.vbundle.derby-10.12.1.1'}
    @Override String getName() {'Derby data source'}
    @Override String getMainTemplateName() {'datasource (Derby)'}
    @Override Version getVersion() { new Version(1, 0, 0) }
    @Override String getDescription() {'The Derby JDBC driver derby-10.12.1.1.jar'}
    @Override String getSampleDataSourceName() {"dsDerby"}
    @Override String getDriverClassName() {'org.apache.derby.jdbc.EmbeddedDriver'}
    @Override String getSampleConnectionString() {"jdbc:derby:${System.properties['user.home']?.replaceAll('\\\\','/')}/datasources/mydb"}
}
