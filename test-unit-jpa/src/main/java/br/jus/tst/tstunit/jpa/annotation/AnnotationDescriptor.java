package br.jus.tst.tstunit.jpa.annotation;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * Encapsulates the data you need to create an annotation. In particular, it stores the type of an <code>Annotation</code> instance and the values of its
 * elements. The "elements" we're talking about are the annotation attributes, not its targets (the term "element" is used ambiguously in Java's annotations
 * documentation).
 *
 * @author Paolo Perrotta
 * @author Davide Marchignoli
 * @author Hardy Ferentschik
 * @author Gunnar Morling
 */
public class AnnotationDescriptor<T extends Annotation> {

    private final Class<T> type;

    private final Map<String, Object> elements = new HashMap<String, Object>();

    /**
     * Returns a new descriptor for the given annotation type.
     *
     * @param <S>
     *            The type of the annotation.
     * @param annotationType
     *            The annotation's class.
     *
     * @return A new descriptor for the given annotation type.
     */
    public static <S extends Annotation> AnnotationDescriptor<S> getInstance(Class<S> annotationType) {
        return new AnnotationDescriptor<S>(annotationType);
    }

    /**
     * Returns a new descriptor for the given annotation type.
     *
     * @param <S>
     *            The type of the annotation.
     * @param annotationType
     *            The annotation's class.
     * @param elements
     *            A map with attribute values for the annotation to be created.
     *
     * @return A new descriptor for the given annotation type.
     */
    public static <S extends Annotation> AnnotationDescriptor<S> getInstance(Class<S> annotationType, Map<String, Object> elements) {
        return new AnnotationDescriptor<S>(annotationType, elements);
    }

    public AnnotationDescriptor(Class<T> annotationType) {
        this.type = annotationType;
    }

    public AnnotationDescriptor(Class<T> annotationType, Map<String, Object> elements) {
        this.type = annotationType;
        for (Map.Entry<String, Object> entry : elements.entrySet()) {
            this.elements.put(entry.getKey(), entry.getValue());
        }
    }

    public void setValue(String elementName, Object value) {
        elements.put(elementName, value);
    }

    public Object valueOf(String elementName) {
        return elements.get(elementName);
    }

    public boolean containsElement(String elementName) {
        return elements.containsKey(elementName);
    }

    public int numberOfElements() {
        return elements.size();
    }

    /**
     * Returns a map with the elements contained in this descriptor keyed by name. This map is a copy of the internally used map, so it can safely be modified
     * without altering this descriptor.
     *
     * @return A map with this descriptor's elements.
     */
    public Map<String, Object> getElements() {
        return new HashMap<String, Object>(elements);
    }

    public Class<T> type() {
        return type;
    }
}
