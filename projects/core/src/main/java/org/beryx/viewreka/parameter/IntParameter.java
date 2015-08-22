package org.beryx.viewreka.parameter;


/**
 * A parameter with values of type Integer.
 */
public class IntParameter extends AbstractComparableValueParameter<Integer> {

    public static class Builder extends AbstractComparableValueParameter.CompBuilder<Integer, IntParameter> {
        public Builder(String name, ParameterGroup parameterGroup) {
            super(name, Integer.class, parameterGroup);
        }

        @Override
        public IntParameter build() {
            return new IntParameter(this);
        }
    }

    private IntParameter(Builder builder) {
        super(builder);
    }

    @Override
    public Class<Integer> getValueClass() {
        return Integer.class;
    }

    @Override
    public Integer toValue(String sVal) throws Exception {
        return Integer.decode(sVal);
    }
}
