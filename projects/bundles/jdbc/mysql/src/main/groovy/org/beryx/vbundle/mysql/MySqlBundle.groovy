package org.beryx.vbundle.mysql
import groovy.util.logging.Slf4j
import org.beryx.viewreka.bundle.util.JdbcBundle
import org.beryx.viewreka.core.Version

@Slf4j
public class MySqlBundle extends JdbcBundle {
    @Override String getId() {'org.beryx.vbundle.mysql-5.1.38'}
    @Override String getName() {'MySQL data source'}
    @Override String getMainTemplateName() {'datasource (MySQL)'}
    @Override Version getVersion() { new Version(1, 0, 0) }
    @Override String getDescription() {'The MySQL JDBC driver mysql-connector-java-5.1.38.jar'}
    @Override String getSampleDataSourceName() {'dsMySql'}
    @Override String getDriverClassName() {'com.mysql.jdbc.Driver'}
    @Override String getSampleConnectionString() {"jdbc:mysql://localhost/mydb"}
}
