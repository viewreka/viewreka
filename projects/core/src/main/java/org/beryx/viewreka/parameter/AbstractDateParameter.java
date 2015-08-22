package org.beryx.viewreka.parameter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import org.beryx.viewreka.core.ViewrekaException;

/**
 * The base class for parameters with date value types.
 * @param <T> the parameter value type
 */
public abstract class AbstractDateParameter<T extends Date> extends AbstractParameter<T> implements DateConfiguration<T> {
    private final Locale locale;
    private final String datePattern;
    private final SimpleDateFormat sdf;

    public abstract static class DateBuilder<TT extends Date, PP extends AbstractDateParameter<TT>> extends AbstractParameter.Builder<TT, PP>{
        private Locale locale = Locale.getDefault();
        private String datePattern = new SimpleDateFormat().toPattern();

        public DateBuilder(String name, Class<TT> type, ParameterGroup parameterGroup) {
            super(name, type, parameterGroup, createValueComparator());
        }

        public DateBuilder<TT, PP> locale(Locale val) { this.locale = val; return this; }
        public DateBuilder<TT, PP> datePattern(String val) { this.datePattern = val; return this; }


        private static final<TT extends Date> Comparator<TT> createValueComparator() {
            return (d1, d2) -> {
                if(d1 == null) return (d2 == null) ? 0 : -1;
                if(d2 == null) return 1;
                return Long.compare(d1.getTime(), d2.getTime());
            };
        }
    }

    protected AbstractDateParameter(DateBuilder<T, ? extends AbstractDateParameter<T>> builder) {
        super(builder);
        this.locale = builder.locale;
        this.datePattern = builder.datePattern;
        this.sdf = new SimpleDateFormat(datePattern, locale);
        this.sdf.setLenient(false);

    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public DateFormat getDateFormat() {
        return sdf;
    }

    @Override
    public String getDatePattern() {
        return datePattern;
    }

    public long toMilliseconds(String sVal) {
        try {
            return getDateFormat().parse(sVal).getTime();
        } catch(ParseException e) {
            throw new ViewrekaException("Parameter " + getName() + ": cannot parse '" + sVal + "' using the date format '" + getDatePattern() + "'");
        }
    }


    @Override
    public T toValue(String sVal) throws Exception {
        return fromMilliseconds(toMilliseconds(sVal));
    }

    @Override
    public String asString(T value) {
        if(value == null) return "null";
        return getDateFormat().format(value);
    }
}
