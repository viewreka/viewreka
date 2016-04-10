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
package org.beryx.viewreka.sql

import org.beryx.viewreka.sql.embedded.H2DB

class TestDB extends H2DB {

    static TestDB createInstance(String dbName, String user, String password, String statements) {
        TestDB testDB = new TestDB(dbName, user, password)
        testDB.withDefaultCreateAndDeleteStrategy()
        testDB.withCreationStatements(statements)
        testDB.deleteDB()
        testDB.createDB()
        testDB.executeSelect("select * from person") {rs -> println rs}
        testDB
    }

    private TestDB(String dbName, String user, String password) {
        super(createDbPath(dbName), user, password)
    }

    private static String createDbPath(String dbName) {
        File tmpDir = new File(System.properties['java.io.tmpdir'], 'viewreka-test')
        String baseDir = tmpDir.path.replaceAll('\\\\', '/')
        "${baseDir}/${dbName}"
    }

    @Override protected boolean shouldDeleteDBOnError(Exception e) { true }

    String getUrl() {
        getUrlForCreationOption(urlOptionIfExists)
    }
}
