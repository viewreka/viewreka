package org.beryx.viewreka.parameter;

import java.util.Comparator;

/**
 * The base class of parameters with comparable value types.
 * @param <T> the parameter value type
 */
public abstract class AbstractComparableValueParameter<T extends Comparable<T>> extends AbstractParameter<T> {

    public abstract static class CompBuilder<TT extends Comparable<TT>, PP extends AbstractParameter<TT>> extends AbstractParameter.Builder<TT, PP>{
        public CompBuilder(String name, Class<TT> type, ParameterGroup parameterGroup) {
            super(name, type, parameterGroup, createValueComparator());
        }

        private static final<TT extends Comparable<TT>> Comparator<TT> createValueComparator() {
            return (val1, val2) -> {
                if(val1 == null) return (val2 == null) ? 0 : -1;
                if(val2 == null) return 1;
                return val1.compareTo(val2);
            };
        }
    }

    public AbstractComparableValueParameter(CompBuilder<T, ? extends AbstractParameter<T>> builder) {
        super(builder);
    }

}
