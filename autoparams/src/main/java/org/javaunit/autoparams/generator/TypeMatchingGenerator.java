package org.javaunit.autoparams.generator;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;

final class TypeMatchingGenerator implements ObjectGenerator {

    private final Function<Type, Boolean> predicate;
    private final Supplier<Object> factory;

    public TypeMatchingGenerator(Function<Type, Boolean> predicate, Supplier<Object> factory) {
        this.predicate = predicate;
        this.factory = factory;
    }

    public TypeMatchingGenerator(Supplier<Object> factory, Class<?>... candidates) {
        this(buildPredicateWithTypes(candidates), factory);
    }

    private static Function<Type, Boolean> buildPredicateWithTypes(Class<?>... candidates) {
        return type -> Arrays.stream(candidates).anyMatch(candidate -> match(type, candidate));
    }

    private static boolean match(Type type, Class<?> candidate) {
        return type.equals(candidate)
            || (type instanceof ParameterizedType && match((ParameterizedType) type, candidate));
    }

    private static boolean match(ParameterizedType type, Class<?> candidate) {
        return type.getRawType().equals(candidate);
    }

    @Override
    public ObjectContainer generate(ObjectQuery query, ObjectGenerationContext context) {
        Type type = query.getType();

        return predicate.apply(type)
            ? new ObjectContainer(factory.get())
            : ObjectContainer.EMPTY;
    }
}
