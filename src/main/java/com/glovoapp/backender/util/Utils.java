package com.glovoapp.backender.util;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class Utils {

    public static<T> Stream<T> flatten(Collection<List<T>> values) {

        Stream<T> stream = values.stream()
                .flatMap(x -> x.stream());

        return stream;
    }
}
