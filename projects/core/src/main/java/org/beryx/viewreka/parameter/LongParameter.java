package org.beryx.viewreka.parameter;


/**
 * A parameter with values of type Long.
 */
public class LongParameter extends AbstractComparableValueParameter<Long> {

    public static class Builder extends AbstractComparableValueParameter.CompBuilder<Long, LongParameter> {
        public Builder(String name, ParameterGroup parameterGroup) {
            super(name, Long.class, parameterGroup);
        }

        @Override
        public LongParameter build() {
            return new LongParameter(this);
        }
    }

    private LongParameter(Builder builder) {
        super(builder);
    }

    @Override
    public Class<Long> getValueClass() {
        return Long.class;
    }


    @Override
    public Long toValue(String sVal) throws Exception {
        return Long.decode(sVal);
    }
}
