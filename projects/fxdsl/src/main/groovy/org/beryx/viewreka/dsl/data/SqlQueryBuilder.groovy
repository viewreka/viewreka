package org.beryx.viewreka.dsl.data

import static org.beryx.viewreka.core.Util.requireNonNull
import groovy.transform.ToString
import groovy.util.logging.Slf4j

import org.beryx.viewreka.model.Query
import org.beryx.viewreka.sql.SqlQuery
import org.beryx.viewreka.sql.SqlQueryImpl

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
