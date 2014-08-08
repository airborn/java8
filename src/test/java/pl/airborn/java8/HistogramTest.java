package pl.airborn.java8;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Map.Entry;
import static org.assertj.core.api.Assertions.assertThat;

public class HistogramTest {

	List<Integer> ints = Lists.newArrayList(3, 3, 5, 3, 3, 5, 2, 3, 3, 5, 3);
	Stream<Integer> integerStream = ints.stream();

	private void assertIsCorrectHistogram(Map<Integer, Long> histogram) {
		assertThat(histogram).containsEntry(2, 1l)
		                     .containsEntry(3, 7l)
		                     .containsEntry(5, 3l);
	}

	@Test
	public void shouldCalculateHistogram_preJava8() throws Exception {
		// given
		Map<Integer, Long> histogram = new HashMap<>();

		// when
		for (int key : ints) {
			Long value = histogram.get(key);
			value = (value == null) ? 1l : ++value;
			histogram.put(key, value);
		}

		// then
		assertIsCorrectHistogram(histogram);
	}

	@Test
	public void shouldCalculateHistogram_pseudoJava8() throws Exception {
		// given
		Map<Integer, Long> histogram = new HashMap<>();

		// when
		integerStream.forEach(key -> {
			Long value = histogram.get(key);
			value = (value == null) ? 1l : ++value;
			histogram.put(key, value);
		});

		// then
		assertIsCorrectHistogram(histogram);
	}

	@Test
	public void shouldCalculateHistogram_newMapApi_compute() throws Exception {
		// given
		Map<Integer, Long> histogram = new HashMap<>();

		// when
		integerStream.forEach(key ->
						histogram.compute(key,
								(key1, value) -> (value == null) ? 1 : ++value
						)
		);

		// then
		assertIsCorrectHistogram(histogram);
	}

	@Test
	public void shouldCalculateHistogram_newMapApi_merge() throws Exception {
		// given
		Map<Integer, Long> histogram = new HashMap<>();

		// when
		integerStream.forEach(key -> histogram.merge(key, 1l, Long::sum));

		// then
		assertIsCorrectHistogram(histogram);
	}

	@Test
	public void shouldCalculateHistogram_groupingAndTransforming() throws Exception {
		// when
		Map<Integer, List<Integer>> m = integerStream.collect(Collectors.groupingBy(Function.identity()));
		Map<Integer, Long> histogram = m.entrySet()
		                                .stream()
		                                .collect(Collectors.toMap(Entry::getKey, e -> (long) e.getValue().size()));

		// then
		assertIsCorrectHistogram(histogram);
	}

	@Test
	public void shouldCalculateHistogram_groupingAndMapping() throws Exception {
		// when
		Map<Integer, Long> histogram = integerStream.collect(
				Collectors.groupingBy(
						Function.identity(),
						Collectors.mapping(i -> 1l, Collectors.summingLong(l -> l))
				)
		);

		// then
		assertIsCorrectHistogram(histogram);
	}

	@Test
	public void shouldCalculateHistogram_customCollector() throws Exception {
		// given
		HistogramCollector<Integer> collector = new HistogramCollector<>();

		// when
		Map<Integer, Long> histogram = integerStream.collect(collector);

		// then
		assertIsCorrectHistogram(histogram);
	}

	@Test
	public void shouldCalculateHistogram_groupingAndCounting() throws Exception {
		// when
		Map<Integer, Long> histogram = integerStream.collect(
				Collectors.groupingBy(
						Function.identity(),
						Collectors.counting()
				)
		);

		// then
		assertIsCorrectHistogram(histogram);
	}
}