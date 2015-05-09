package org.beryx.viewreka.sql;

import static org.beryx.viewreka.core.Util.requireNonNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.beryx.viewreka.core.ViewrekaException;
import org.beryx.viewreka.parameter.Parameter;
import org.beryx.viewreka.parameter.ParameterGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlQueryImpl implements SqlQuery {
	private static final Logger log = LoggerFactory.getLogger(SqlQueryImpl.class);

	private static final String STRING1_REGEX = "\"([^\"]|\"\")*\"";
	private static final String STRING2_REGEX = "'([^']|'')*'";
	private static final String COMMENT_REGEX = "--[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";
	private static final String PARAMETER_REGEX = "(:[a-zA-Z_]\\w*)\\b";

	private static final String REGEX =
			   "(?<STRING1>" + STRING1_REGEX + ")"
			+ "|(?<STRING2>" + STRING2_REGEX + ")"
			+ "|(?<COMMENT>" + COMMENT_REGEX + ")"
			+ "|(?<PARAMETER>" + PARAMETER_REGEX + ")";

	private static final Pattern PATTERN = Pattern.compile(REGEX);


	private final String sql;
	private final List<String> parameterList = new ArrayList<>();
	private final Set<String> parameterNames = new TreeSet<>();


	public SqlQueryImpl(String query) {
		String tmpSql = query.trim();
		if(tmpSql.endsWith(";")) {
			tmpSql = tmpSql.substring(0, tmpSql.length() - 1);
		}
        boolean found = true;
        while(found) {
            Matcher matcher = PATTERN.matcher(tmpSql);
        	found = false;
            while(matcher.find()) {
        		String groupName = "PARAMETER";
				String prmGroup = matcher.group(groupName);
    			if(prmGroup != null) {
    				if(prmGroup.isEmpty()) throw new ViewrekaException("Internal error: empty parameter group in sql: " + tmpSql);
        			String parameterName = prmGroup.substring(1);
        			parameterList.add(parameterName);
        			parameterNames.add(parameterName);

        			int start = matcher.start(groupName);
        			int end = matcher.end(groupName);
        			tmpSql = tmpSql.substring(0, start) + "?" + tmpSql.substring(end);
        			found = true;
        			break;
        		}
            }
        }
		log.trace("Parsed sql: " + tmpSql);
		log.trace("Sql parameters: " + parameterList);
		this.sql = tmpSql;
	}

	@Override
	public Set<String> getParameterNames() {
		return parameterNames;
	}

	@Override
	public PreparedStatement getPreparedStatement(Connection connection, ParameterGroup parameterGroup) {
		try {
			PreparedStatement statement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE, ResultSet.HOLD_CURSORS_OVER_COMMIT);
			for(int i=0; i<parameterList.size(); i++) {
				String name = parameterList.get(i);
				Parameter<?> parameter = requireNonNull(parameterGroup.getParameter(name), "Query parameter '"+ name + "'");
				statement.setObject(i+1, parameter.getValue());
			}
			return statement;
		} catch (SQLException e) {
			throw new ViewrekaException("Cannot create prepared statement", e);
		}
	}

	@Override
	public String toString() {
		return parameterList + "\n" + sql;
	}
}
