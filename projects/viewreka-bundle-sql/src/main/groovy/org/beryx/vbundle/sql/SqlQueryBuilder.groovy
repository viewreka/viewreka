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
package org.beryx.vbundle.sql

import org.beryx.viewreka.dsl.data.QueryBuilder

import static org.beryx.viewreka.core.Util.requireNonNull
import groovy.transform.ToString
import groovy.util.logging.Slf4j

import org.beryx.viewreka.model.Query
import org.beryx.viewreka.sql.SqlQuery
import org.beryx.viewreka.sql.SqlQueryImpl

/**
 * A {@link QueryBuilder} implementation for creating {@link SqlQuery} instances.
 */
@Slf4j
class SqlQueryBuilder implements QueryBuilder {

    @ToString(includePackage=false)
    static class QueryDelegate {
        String query

        String file(String path) {
            String absPath = new File(path).absolutePath
            log.debug "Query file path: $absPath"
            return new File(absPath).text
        }

        String url(String spec) {
            return spec.toURL().text
        }
    }

    @Override
    public SqlQuery build(String name, Closure closure) {
        def queryDelegate = new QueryDelegate()
        closure.delegate = queryDelegate
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()

        String queryText = requireNonNull(queryDelegate.query, "query")
        SqlQuery query = new SqlQueryImpl(queryText)
        return query;
    }
}
