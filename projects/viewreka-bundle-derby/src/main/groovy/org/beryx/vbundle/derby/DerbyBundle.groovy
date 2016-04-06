/*
 * Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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