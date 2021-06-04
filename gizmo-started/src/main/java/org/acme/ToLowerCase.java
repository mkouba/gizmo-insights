package org.acme;

import org.acme.gizmonster.Transformer;

public class ToLowerCase implements Transformer {

    @Override
    public String apply(String value) {
        return value.toLowerCase();
    }

}
