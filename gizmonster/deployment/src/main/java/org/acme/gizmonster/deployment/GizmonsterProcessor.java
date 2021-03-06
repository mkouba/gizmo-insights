package org.acme.gizmonster.deployment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import org.acme.gizmonster.Transformer;
import org.acme.gizmonster.TransformService;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.objectweb.asm.Opcodes;

import io.quarkus.arc.deployment.GeneratedBeanBuildItem;
import io.quarkus.arc.deployment.GeneratedBeanGizmoAdaptor;
import io.quarkus.arc.processor.MethodDescriptors;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.gizmo.AssignableResultHandle;
import io.quarkus.gizmo.BranchResult;
import io.quarkus.gizmo.BytecodeCreator;
import io.quarkus.gizmo.ClassCreator;
import io.quarkus.gizmo.FieldCreator;
import io.quarkus.gizmo.MethodCreator;
import io.quarkus.gizmo.MethodDescriptor;
import io.quarkus.gizmo.ResultHandle;
import io.quarkus.gizmo.WhileLoop;

class GizmonsterProcessor {

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem("gizmonster");
    }

    @BuildStep
    void generateTransformService(
            BuildProducer<GeneratedBeanBuildItem> generatedBeanClasses,
            CombinedIndexBuildItem index) {

        // Create a ClassOutput instance
        GeneratedBeanGizmoAdaptor gizmoAdaptor = new GeneratedBeanGizmoAdaptor(
                generatedBeanClasses);

        // Collect all transformers
        Set<String> transformerClasses = index.getIndex()
                .getAllKnownImplementors(
                        DotName.createSimple(Transformer.class.getName()))
                .stream()
                .map(ClassInfo::toString)
                .collect(Collectors.toSet());

        // Generate the TransformerService implementation class
        // use the try-with-resources to call ClassCreator.close() automatically
        try (ClassCreator classCreator = ClassCreator.builder()
                .className("org.acme.gizmonster.GeneratedTransformService")
                .interfaces(TransformService.class)
                .classOutput(gizmoAdaptor)
                .build()) {

            //================================================================================
            // Add annotation - make it a bean
            classCreator.addAnnotation(ApplicationScoped.class);

            //================================================================================
            // Add a field - private final List transformers
            FieldCreator transformersField = classCreator
                    .getFieldCreator("transformers", List.class)
                    .setModifiers(Opcodes.ACC_FINAL | Opcodes.ACC_PRIVATE);

            //================================================================================
            // Add a method -implement the no-args constructor
            MethodCreator constructor = classCreator.getMethodCreator("<init>",
                    "V");
            // Invoke super()
            constructor.invokeSpecialMethod(
                    MethodDescriptors.OBJECT_CONSTRUCTOR,
                    constructor.getThis());
            // ArrayList list = new ArrayList()
            ResultHandle list = constructor.newInstance(
                    MethodDescriptor.ofConstructor(ArrayList.class));
            for (String transformerClass : transformerClasses) {
                ResultHandle transformerInstance = constructor.newInstance(
                        MethodDescriptor.ofConstructor(transformerClass));
                // list.add(transformerInstance)
                constructor.invokeInterfaceMethod(
                        LIST_ADD,
                        list, transformerInstance);
            }
            // this.transformers = list
            constructor.writeInstanceField(
                    transformersField.getFieldDescriptor(),
                    constructor.getThis(), list);
            // Do not forget to add a return statement
            constructor.returnValue(null);

            //================================================================================
            // Add a method -implement the transform() method
            MethodCreator transformMethod = classCreator
                    .getMethodCreator("transform", String.class, String.class);
            ResultHandle param = transformMethod.getMethodParam(0);

            // Test null and blank value
            BranchResult testIsNull = transformMethod.ifNull(param);
            BytecodeCreator isNull = testIsNull.trueBranch();
            isNull.returnValue(isNull.load("Null not allowed!"));

            BranchResult testIsBlank = transformMethod.ifTrue(transformMethod
                    .invokeVirtualMethod(STRING_IS_BLANK, param));
            BytecodeCreator isBlank = testIsBlank.trueBranch();
            isBlank.returnValue(isBlank.load("Empty string not allowed!"));

            // Now let's apply all transformers
            AssignableResultHandle result = transformMethod
                    .createVariable(String.class);
            transformMethod.assign(result, param);
            ResultHandle transformers = transformMethod.readInstanceField(
                    transformersField.getFieldDescriptor(),
                    transformMethod.getThis());
            ResultHandle iterator = transformMethod
                    .invokeInterfaceMethod(LIST_ITERATOR, transformers);
            // while(iterator.hasNext())
            WhileLoop loop = transformMethod.whileLoop(bc -> bc.ifTrue(
                    bc.invokeInterfaceMethod(ITERATOR_HAS_NEXT,
                            iterator)));
            BytecodeCreator block = loop.block();
            // Transformer transformer = iterator.next()
            ResultHandle transformer = block
                    .invokeInterfaceMethod(ITERATOR_NEXT, iterator);
            // result = transformer.apply(result)
            block.assign(result, block.invokeInterfaceMethod(
                    TRANSFORMER_APPLY,
                    transformer, result));
            transformMethod.returnValue(result);
        }
    }

    private static final MethodDescriptor TRANSFORMER_APPLY = MethodDescriptor
            .ofMethod(Transformer.class, "apply",
                    String.class, String.class);
    private static final MethodDescriptor ITERATOR_NEXT = MethodDescriptor
            .ofMethod(
                    Iterator.class, "next", Object.class);
    private static final MethodDescriptor ITERATOR_HAS_NEXT = MethodDescriptor
            .ofMethod(Iterator.class, "hasNext", boolean.class);
    private static final MethodDescriptor LIST_ITERATOR = MethodDescriptor
            .ofMethod(List.class,
                    "iterator", Iterator.class);
    private static final MethodDescriptor STRING_IS_BLANK = MethodDescriptor
            .ofMethod(String.class,
                    "isBlank", boolean.class);
    private static final MethodDescriptor LIST_ADD = MethodDescriptor.ofMethod(
            List.class, "add",
            boolean.class, Object.class);
}
