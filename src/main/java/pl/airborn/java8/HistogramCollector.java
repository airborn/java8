package pl.airborn.java8;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class HistogramCollector<T> implements Collector<T, Map<T, Long>, Map<T, Long>> {

	@Override
	public Supplier<Map<T, Long>> supplier() {
		return HashMap::new;
	}

	@Override
	public BiConsumer<Map<T, Long>, T> accumulator() {
		return (acc, key) ->
				acc.compute(key,
						(key1, value) -> (value == null) ? 1 : ++value);
	}

	@Override
	public BinaryOperator<Map<T, Long>> combiner() {
		return (acc1, acc2) -> {
			acc2.forEach((key2, value2) ->
					acc1.merge(key2, value2, Long::sum));
			return acc1;
		};
	}

	@Override
	public Function<Map<T, Long>, Map<T, Long>> finisher() {
		return Function.identity();
	}

	@Override
	public Set<Characteristics> characteristics() {
		return EnumSet.allOf(Characteristics.class);
	}
}
