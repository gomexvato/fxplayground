package com.components.submittable;


import java.math.BigDecimal;

class ConsumerValue {

    static class Options {
        public Integer times;
        public Integer times1E;
        public Integer divided;
        public Double min;
        public Double max;
        public Boolean negated;
    }

    // Value being submitted - PushToModel
    static <T> T set(T t, Options x) {
        T value = t;
        if(t instanceof Double) {
            if(x.min != null && (Double) value < x.min) {
                value = (T) x.min;
            } else if(x.max != null && (Double) value > x.max) {
                value = (T) x.max;
            }
        }
        if(t instanceof Integer) {
            if(x.min != null && (Integer) value < x.min) {
                value = (T) x.min;
            } else if(x.max != null && (Integer) value > x.max) {
                value = (T) x.max;
            }
        }
        if(x.times1E != null) value = times1E(value, x.times1E);
        if(x.times != null) value = times(value, x.times);
        if(x.divided != null) value = divided(value, x.divided);
        if(Boolean.TRUE.equals(x.negated)) value = negated(value);
        return value;
    }

    // Value being received (Opposite to set) - PushToUI
    static <T> T get(T t, Options o) {
        if(o.times1E != null) return times1E(t, -o.times1E);
        if(o.times != null) return divided(t, o.times);
        if(o.divided != null) return times(t, o.divided);
        if(Boolean.TRUE.equals(o.negated)) return negated(t);
        return t;
    }

    private static <T> T times1E(T t, int n) {
        if(t instanceof Double) {
            BigDecimal a = BigDecimal.valueOf((Double) t);
            BigDecimal r = a.movePointRight(n); // moves to the left when negative n
            return (T) (Double) r.doubleValue();
        }
        if(t instanceof Integer) {
            return (T) (Integer) ((Double) ((Integer)t * Math.pow(10, n))).intValue();
        }
        return t;
    }

    private static <T> T times(T t, int times) {
        if(t instanceof Double) {
            Double v = ((Double) t) * times;
            return (T) (Double) ((double) Math.round(v * times) / times); // Avoids floating point decimals
        }
        if(t instanceof Integer) { Integer v = ((Integer)t) * times; return (T) v;}
        return t;
    }

    private static <T> T divided(T t, int x) {
        if(t instanceof Double) {
            Double v = ((Double) t) / x;
            return (T) (Double) ((double) Math.round(v * x) / x); // Avoids floating-point decimals
        }
        if(t instanceof Integer) { Integer v = ((Integer)t) / x; return (T) v;}
        return t;
    }

    private static <T> T negated(T t) {
        if(t instanceof Double) {
            double value = (Double)t;
            return value == 0
                    ? (T) Double.valueOf(0)
                    : (T) (Double) (value * -1);
        } else if(t instanceof Integer) {
            return (T) (Integer) (((Integer) t) * -1);
        }
        return t;
    }
}
