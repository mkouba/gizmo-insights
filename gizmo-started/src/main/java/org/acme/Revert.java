package org.acme;

import org.acme.gizmonster.Transformer;

public class Revert implements Transformer {

    @Override
    public String apply(String value) {
        return new StringBuilder(value).reverse().toString();
    }

}
