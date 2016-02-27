package org.beryx.vbundle.oracle
import groovy.util.logging.Slf4j
import org.beryx.viewreka.bundle.util.JdbcBundle
import org.beryx.viewreka.core.Version

@Slf4j
public class OracleBundle extends JdbcBundle {
    @Override String getId() {'org.beryx.vbundle.ojdbc-7'}
    @Override String getName() {'Oracle JDBC data source'}
    @Override String getMainTemplateName() {'datasource (Oracle)'}
    @Override Version getVersion() { new Version(1, 0, 0) }
    @Override String getDescription() {'The Oracle JDBC driver ojdbc7.jar'}
    @Override String getSampleDataSourceName() {'dsOracle'}
    @Override String getDriverClassName() {'oracle.jdbc.OracleDriver'}
    @Override String getSampleConnectionString() {'jdbc:oracle:thin:@localhost:1521:orcl'}
    @Override String getSampleUser() {'scott'}
    @Override String getSamplePassword() {'tiger'}
}
