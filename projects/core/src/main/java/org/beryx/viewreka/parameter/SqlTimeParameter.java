package org.beryx.viewreka.parameter;

import java.sql.Time;

/**
 * A parameter with values of type java.sql.Time.
 */
public class SqlTimeParameter extends AbstractDateParameter<Time> {

    public static class Builder extends DateBuilder<Time, SqlTimeParameter> {
        public Builder(String name, ParameterGroup parameterGroup) {
            super(name, Time.class, parameterGroup);
        }

        @Override
        public SqlTimeParameter build() {
            return new SqlTimeParameter(this);
        }
    }

    private SqlTimeParameter(Builder builder) {
        super(builder);
    }

    @Override
    public Class<Time> getValueClass() {
        return Time.class;
    }

    @Override
    public Time fromMilliseconds(long millis) {
        return new Time(millis);
    }

}
