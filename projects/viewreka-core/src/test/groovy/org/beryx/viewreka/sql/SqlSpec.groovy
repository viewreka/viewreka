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

import org.beryx.viewreka.parameter.IntParameter
import org.beryx.viewreka.parameter.ParameterGroup
import org.beryx.viewreka.parameter.StringParameter
import spock.lang.Shared
import spock.lang.Specification

class SqlSpec extends Specification {
    static String statements = '''
            CREATE TABLE person (
                 id INTEGER NOT NULL,
                 firstname VARCHAR(100),
                 lastname VARCHAR(100),
                 age INTEGER
            );
            /
            insert into person(id, firstname, lastname, age) values(1, 'John', 'Smith', 33);
            /
            insert into person(id, firstname, lastname, age) values(2, 'Jane', 'Smith', 27);
            /
            insert into person(id, firstname, lastname, age) values(3, 'Mary', 'Smith', 3);
            /
            insert into person(id, firstname, lastname, age) values(4, 'Jack', 'Jackson', 48);
            /
            insert into person(id, firstname, lastname, age) values(5, 'Lisa', 'Johnson', 38);
            /
        '''

    @Shared TestDB db = new TestDB("${getClass().simpleName}-DB", '', '', statements)

    def "retrieve values from a data source using a non-parameterized query"() {
        given:
        SqlDataSourceImpl dataSource = new SqlDataSourceImpl('dsource', db.url, db.user, db.password)
        def group = new ParameterGroup()
        def query = new SqlQueryImpl("select firstname, age from person")
        def ds = dataSource.getDatasetProvider("ds1", query, group).dataset

        when:
        def name = ds.getValue(0, 'firstname', String)
        def age = ds.getObject(0, 2)

        then:
        name == 'John'
        age == 33
    }

    def "retrieve values from a data source using a parameterized query"() {
        given:
        SqlDataSourceImpl dataSource = new SqlDataSourceImpl('dsource', db.url, db.user, db.password)
        def query = new SqlQueryImpl('''
                select firstname, lastname, age from person
                where firstname like :prmFirstname
                and lastname like :prmLastname
                and age between :prmMinAge and :prmMaxAge
                order by id
        ''')
        def group = new ParameterGroup()


        def prmFirstname = new StringParameter.Builder('prmFirstname', group).build()
        group.addParameter(prmFirstname)
        def prmLastname = new StringParameter.Builder('prmLastname', group).build()
        group.addParameter(prmLastname)
        def prmMinAge = new IntParameter.Builder('prmMinAge', group).build()
        group.addParameter(prmMinAge)
        def prmMaxAge = new IntParameter.Builder('prmMaxAge', group).build()
        group.addParameter(prmMaxAge)

        prmFirstname.setValue(pFirstname)
        prmLastname.setValue(pLastname)
        prmMinAge.setValue(pMinAge)
        prmMaxAge.setValue(pMaxAge)

        def ds = dataSource.getDatasetProvider("ds1", query, group).dataset

        when:
        def fname1 = ds.getValue(row, 'firstname', String)
        def fname2 = ds.getValue(row, 1, String)
        def fname3 = ds.getObject(row, 'firstname')
        def fname4 = ds.getObject(row, 1)

        def lname1 = ds.getValue(row, 'lastname', String)
        def lname2 = ds.getValue(row, 2, String)
        def lname3 = ds.getObject(row, 'lastname')
        def lname4 = ds.getObject(row, 2)

        def age1 = ds.getValue(row, 'age', Integer)
        def age2 = ds.getValue(row, 3, Integer)
        def age3 = ds.getObject(row, 'age')
        def age4 = ds.getObject(row, 3)

        then:
        ds.rowCount == rowCount

        fname1 == fname
        fname2 == fname
        fname3 == fname
        fname4 == fname

        lname1 == lname
        lname2 == lname
        lname3 == lname
        lname4 == lname

        age1 == age
        age2 == age
        age3 == age
        age4 == age

        where:
        pFirstname | pLastname | pMinAge | pMaxAge || rowCount | row | fname  | lname     | age
        'J%'       | 'Smith'   | 1       | 99      || 2        | 1   | 'Jane' | 'Smith'   | 27
        'J%'       | '%'       | 40      | 50      || 1        | 0   | 'Jack' | 'Jackson' | 48
        '%'        | 'J%'      | 10      | 50      || 2        | 1   | 'Lisa' | 'Johnson' | 38
        '%'        | '%'       | 20      | 40      || 3        | 0   | 'John' | 'Smith'   | 33

    }
}
