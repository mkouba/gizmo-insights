package org.acme.gizmonster;

/**
 * A generated implementation of this interface will be registered as a CDI bean.
 */
public interface TransformService {

    /**
     * Applies all {@link Transformer}s found.
     * 
     * @param value
     * @return the transformed value
     */
    String transform(String value);

}
