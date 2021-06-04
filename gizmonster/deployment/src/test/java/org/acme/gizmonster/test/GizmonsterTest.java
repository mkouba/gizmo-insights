package org.acme.gizmonster.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.inject.Inject;

import org.acme.gizmonster.TransformService;
import org.acme.gizmonster.Transformer;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;

public class GizmonsterTest {

    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class).addClass(FooTransformer.class));

    @Inject
    TransformService service;

    @Test
    public void testTransform() {
        assertEquals("foo", service.transform("whatever"));
    }

    public static class FooTransformer implements Transformer {

        @Override
        public String apply(String value) {
            return "foo";
        }

    }
}
