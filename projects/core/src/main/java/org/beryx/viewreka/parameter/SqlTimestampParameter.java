package org.beryx.viewreka.parameter;

import java.sql.Timestamp;

public class SqlTimestampParameter extends AbstractDateParameter<Timestamp> {

	public static class Builder extends DateBuilder<Timestamp, SqlTimestampParameter> {
		public Builder(String name, ParameterGroup parameterGroup) {
			super(name, Timestamp.class, parameterGroup);
		}

		@Override
		public SqlTimestampParameter build() {
			return new SqlTimestampParameter(this);
		}
	}

	private SqlTimestampParameter(Builder builder) {
		super(builder);
	}

	@Override
	public Class<Timestamp> getValueClass() {
		return Timestamp.class;
	}

	@Override
	public Timestamp fromMilliseconds(long millis) {
		return new Timestamp(millis);
	}
}
