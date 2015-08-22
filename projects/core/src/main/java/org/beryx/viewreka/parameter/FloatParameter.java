package org.beryx.viewreka.parameter;

/**
 * A parameter with values of type Float.
 */
public class FloatParameter extends AbstractComparableValueParameter<Float> {

    public static class Builder extends AbstractComparableValueParameter.CompBuilder<Float, FloatParameter> {
        public Builder(String name, ParameterGroup parameterGroup) {
            super(name, Float.class, parameterGroup);
        }

        @Override
        public FloatParameter build() {
            return new FloatParameter(this);
        }
    }

    private FloatParameter(Builder builder) {
        super(builder);
    }

    @Override
    public Class<Float> getValueClass() {
        return Float.class;
    }

    @Override
    public Float toValue(String sVal) throws Exception {
        return Float.valueOf(sVal);
    }
}
