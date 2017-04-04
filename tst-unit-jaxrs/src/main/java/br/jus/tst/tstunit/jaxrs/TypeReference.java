package br.jus.tst.tstunit.jaxrs;

import java.lang.reflect.*;

/**
 * @see org.codehaus.jackson.type.TypeReference
 */
public abstract class TypeReference<T> implements Comparable<TypeReference<T>> {

    private final Type _type;

    protected TypeReference() {
        Type superClass = getClass().getGenericSuperclass();

        if (superClass instanceof Class<?>) { // sanity check, should never happen
            throw new IllegalArgumentException("Internal error: TypeReference constructed without actual type information");
        }

        _type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
    }
    
    public Type getType() {
        return _type;
    }

    /**
     * The only reason we define this method (and require implementation of <code>Comparable</code>) is to prevent constructing a reference without type
     * information.
     */
    @Override
    public int compareTo(TypeReference<T> o) {
        // just need an implementation, not a good one... hence:
        return 0;
    }
}
