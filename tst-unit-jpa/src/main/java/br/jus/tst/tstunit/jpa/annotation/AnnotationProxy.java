package br.jus.tst.tstunit.jpa.annotation;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.Map.Entry;

/**
 * A concrete implementation of <code>Annotation</code> that pretends it is a "real" source code annotation. It's also an <code>InvocationHandler</code>.
 * <p>
 * When you create an <code>AnnotationProxy</code>, you must initialize it with an <code>AnnotationDescriptor</code>. The adapter checks that the provided
 * elements are the same elements defined in the annotation interface. However, it does <i>not</i> check that their values are the right type. If you omit an
 * element, the adapter will use the default value for that element from the annotation interface, if it exists. If no default exists, it will throw an
 * exception.
 * </p>
 *
 * @author Paolo Perrotta
 * @author Davide Marchignoli
 * @author Gunnar Morling
 * @see java.lang.annotation.Annotation
 */
public class AnnotationProxy implements Annotation, InvocationHandler, Serializable {

    private static final long serialVersionUID = 6907601010599429454L;

    private final Class<? extends Annotation> annotationType;
    private final Map<String, Object> values;
    private final int hashCode;

    public AnnotationProxy(AnnotationDescriptor<?> descriptor) {
        this.annotationType = descriptor.type();
        values = Collections.unmodifiableMap(getAnnotationValues(descriptor));
        this.hashCode = calculateHashCode();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (values.containsKey(method.getName())) {
            return values.get(method.getName());
        }
        return method.invoke(this, args);
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return annotationType;
    }

    /**
     * Performs an equality check as described in {@link Annotation#equals(Object)}.
     *
     * @param obj
     *            The object to compare
     *
     * @return Whether the given object is equal to this annotation proxy or not
     *
     * @see Annotation#equals(Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!annotationType.isInstance(obj)) {
            return false;
        }

        Annotation other = annotationType.cast(obj);

        // compare annotation member values
        for (Entry<String, Object> member : values.entrySet()) {

            Object value = member.getValue();
            Object otherValue = getAnnotationMemberValue(other, member.getKey());

            if (!areEqual(value, otherValue)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Calculates the hash code of this annotation proxy as described in {@link Annotation#hashCode()}.
     *
     * @return The hash code of this proxy.
     *
     * @see Annotation#hashCode()
     */
    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append('@').append(annotationType.getName()).append('(');
        for (String s : getRegisteredMethodsInAlphabeticalOrder()) {
            result.append(s).append('=').append(values.get(s)).append(", ");
        }
        // remove last separator:
        if (values.size() > 0) {
            result.delete(result.length() - 2, result.length());
            result.append(")");
        } else {
            result.delete(result.length() - 1, result.length());
        }

        return result.toString();
    }

    private Map<String, Object> getAnnotationValues(AnnotationDescriptor<?> descriptor) {
        Map<String, Object> result = new HashMap<>();
        int processedValuesFromDescriptor = 0;
        final Method[] declaredMethods = annotationType.getDeclaredMethods();
        for (Method m : declaredMethods) {
            if (descriptor.containsElement(m.getName())) {
                result.put(m.getName(), descriptor.valueOf(m.getName()));
                processedValuesFromDescriptor++;
            } else if (m.getDefaultValue() != null) {
                result.put(m.getName(), m.getDefaultValue());
            } else {
                throw new AnnotationException("No value provided for annotation");
            }
        }
        if (processedValuesFromDescriptor != descriptor.numberOfElements()) {

            Set<String> unknownParameters = descriptor.getElements().keySet();
            unknownParameters.removeAll(result.keySet());

            throw new AnnotationException("Trying to instantiate annotation with unknown parameters");
        }
        return result;
    }

    private int calculateHashCode() {
        int hashCode = 0;

        for (Entry<String, Object> member : values.entrySet()) {
            Object value = member.getValue();

            int nameHashCode = member.getKey().hashCode();

            int valueHashCode = !value.getClass().isArray() ? value.hashCode()
                    : value.getClass() == boolean[].class ? Arrays.hashCode((boolean[]) value)
                            : value.getClass() == byte[].class ? Arrays.hashCode((byte[]) value)
                                    : value.getClass() == char[].class ? Arrays.hashCode((char[]) value)
                                            : value.getClass() == double[].class ? Arrays.hashCode((double[]) value)
                                                    : value.getClass() == float[].class ? Arrays.hashCode((float[]) value)
                                                            : value.getClass() == int[].class ? Arrays.hashCode((int[]) value)
                                                                    : value.getClass() == long[].class ? Arrays.hashCode((long[]) value)
                                                                            : value.getClass() == short[].class ? Arrays.hashCode((short[]) value)
                                                                                    : Arrays.hashCode((Object[]) value);

            hashCode += 127 * nameHashCode ^ valueHashCode;
        }

        return hashCode;
    }

    private SortedSet<String> getRegisteredMethodsInAlphabeticalOrder() {
        SortedSet<String> result = new TreeSet<String>();
        result.addAll(values.keySet());
        return result;
    }

    private boolean areEqual(Object o1, Object o2) {
        return !o1.getClass().isArray() ? o1.equals(o2)
                : o1.getClass() == boolean[].class ? Arrays.equals((boolean[]) o1, (boolean[]) o2)
                        : o1.getClass() == byte[].class ? Arrays.equals((byte[]) o1, (byte[]) o2)
                                : o1.getClass() == char[].class ? Arrays.equals((char[]) o1, (char[]) o2)
                                        : o1.getClass() == double[].class ? Arrays.equals((double[]) o1, (double[]) o2)
                                                : o1.getClass() == float[].class ? Arrays.equals((float[]) o1, (float[]) o2)
                                                        : o1.getClass() == int[].class ? Arrays.equals((int[]) o1, (int[]) o2)
                                                                : o1.getClass() == long[].class ? Arrays.equals((long[]) o1, (long[]) o2)
                                                                        : o1.getClass() == short[].class ? Arrays.equals((short[]) o1, (short[]) o2)
                                                                                : Arrays.equals((Object[]) o1, (Object[]) o2);
    }

    private Object getAnnotationMemberValue(Annotation annotation, String name) {
        try {
            return annotation.annotationType().getDeclaredMethod(name).invoke(annotation);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            throw new AnnotationException("Unable to retrieve annotation parameter value", e);
        }
    }
}