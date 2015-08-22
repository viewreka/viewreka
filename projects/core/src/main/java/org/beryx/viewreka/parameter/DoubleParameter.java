package org.beryx.viewreka.parameter;

/**
 * A parameter with values of type Double.
 */
public class DoubleParameter extends AbstractComparableValueParameter<Double> {

    public static class Builder extends AbstractComparableValueParameter.CompBuilder<Double, DoubleParameter> {
        public Builder(String name, ParameterGroup parameterGroup) {
            super(name, Double.class, parameterGroup);
        }

        @Override
        public DoubleParameter build() {
            return new DoubleParameter(this);
        }
    }

    private DoubleParameter(Builder builder) {
        super(builder);
    }

    @Override
    public Class<Double> getValueClass() {
        return Double.class;
    }

    @Override
    public Double toValue(String sVal) throws Exception {
        return Double.valueOf(sVal);
    }
}
