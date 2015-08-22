package org.beryx.viewreka.parameter;


/**
 * A parameter with values of type String.
 */
public class StringParameter extends AbstractComparableValueParameter<String> {

    public static class Builder extends AbstractComparableValueParameter.CompBuilder<String, StringParameter> {
        public Builder(String name, ParameterGroup parameterGroup) {
            super(name, String.class, parameterGroup);
        }

        @Override
        public StringParameter build() {
            return new StringParameter(this);
        }
    }

    private StringParameter(Builder builder) {
        super(builder);
    }

    @Override
    public Class<String> getValueClass() {
        return String.class;
    }

    @Override
    public String toValue(String sVal) throws Exception {
        return sVal;
    }

}
