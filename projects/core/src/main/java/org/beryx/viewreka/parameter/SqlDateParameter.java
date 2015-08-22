package org.beryx.viewreka.parameter;

import java.sql.Date;

/**
 * A parameter with values of type java.sql.Date.
 */
public class SqlDateParameter extends AbstractDateParameter<Date> {

    public static class Builder extends DateBuilder<Date, SqlDateParameter> {
        public Builder(String name, ParameterGroup parameterGroup) {
            super(name, Date.class, parameterGroup);
        }

        @Override
        public SqlDateParameter build() {
            return new SqlDateParameter(this);
        }
    }

    private SqlDateParameter(Builder builder) {
        super(builder);
    }

    @Override
    public Class<Date> getValueClass() {
        return Date.class;
    }

    @Override
    public Date fromMilliseconds(long millis) {
        return new Date(millis);
    }
}
