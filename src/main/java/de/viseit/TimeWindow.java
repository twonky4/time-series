package de.viseit;

import java.time.DateTimeException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

/**
 * Represents a temporal window defined by a start and end point in time.
 * <p>
 * A {@code TimeWindow} can represent a bounded or unbounded range of time.
 * The {@code start} and {@code end} attributes define the boundaries of the time window:
 * <ul>
 * <li>If {@code start} is {@code null}, the window is open-ended in the past.</li>
 * <li>If {@code end} is {@code null}, the window is open-ended in the future.</li>
 * <li>If both {@code start} and {@code end} are {@code null}, the window is considered
 * to cover all time (unbounded).</li>
 * </ul>
 * The {@code start} boundary is inclusive, and the {@code end} boundary is exclusive.
 * <p>
 * This class is immutable and thread-safe when its attributes are also immutable and thread-safe.
 */
@EqualsAndHashCode
@Getter
public class TimeWindow implements Comparable<TimeWindow> {
	private final ZonedDateTime start;
	private final ZonedDateTime end;

	public TimeWindow(ZonedDateTime start, ZonedDateTime end) {
		if (start != null && end != null && !end.isAfter(start)) {
			throw new DateTimeException("Start has to be before end");
		}

		this.start = start;
		this.end = end;
	}

	/**
	 * Only true if {@link #start} is after or equals {@code other} {@link #end}.
	 * <p>
	 * <img src="https://github.com/twonky4/time-series/blob/main/src/main/doc/TimeWindow.isAfter.png?raw=true">
	 */
	public boolean isAfter(@NonNull TimeWindow other) {
		return start != null && other.end != null && (start.isAfter(other.end) || start.isEqual(other.end));
	}

	/**
	 * Only true if {@link #end} is before or equals {@code other} {@link #start}.
	 * <p>
	 * <img src="https://github.com/twonky4/time-series/blob/main/src/main/doc/TimeWindow.isBefore.png?raw=true">
	 */
	public boolean isBefore(@NonNull TimeWindow other) {
		return end != null && other.start != null && (end.isBefore(other.start) || end.isEqual(other.start));
	}

	/**
	 * Only true if {@link #end} and {@link #start} are matching.
	 * <p>
	 * <img src="https://github.com/twonky4/time-series/blob/main/src/main/doc/TimeWindow.isEqual.png?raw=true">
	 */
	public boolean isEqual(@NonNull TimeWindow other) {
		return start == other.start && end == other.end;
	}

	/**
	 * Only true if the ranges are overlapping.
	 * <p>
	 * <img src=
	 * "https://github.com/twonky4/time-series/blob/main/src/main/doc/TimeWindow.isOverlapping.png?raw=true">
	 */
	public boolean isOverlapping(@NonNull TimeWindow other) {
		if ((start == null && end == null) || (other.start == null && other.end == null)) {
			return true;
		}

		ZonedDateTime thisStart = start != null ? start : other.start;
		ZonedDateTime thisEnd = end != null ? end : other.end;
		ZonedDateTime otherStart = other.start != null ? other.start : start;
		ZonedDateTime otherEnd = other.end != null ? other.end : end;

		return (thisStart == null || otherEnd == null || thisStart.isBefore(otherEnd)) &&
				(thisEnd == null || otherStart == null || thisEnd.isAfter(otherStart));
	}

	/**
	 * {@link #start} is more important than {@link #end}. Infinite is smaller than a defined value.
	 */
	@Override
	public int compareTo(@NonNull TimeWindow other) {
		if (start != null && other.start != null) {
			int startComparison = start.compareTo(other.start);
			if (startComparison != 0) {
				return startComparison;
			}
		} else if (start == null && other.start != null) {
			return -1;
		} else if (start != null) {
			return 1;
		}

		if (end != null && other.end != null) {
			return end.compareTo(other.end);
		} else if (end == null && other.end != null) {
			return 1;
		} else if (end != null) {
			return -1;
		}

		return 0;
	}

	/**
	 * Calculates the overlap between this TimeWindow and another TimeWindow.
	 */
	public Optional<TimeWindow> getOverlap(@NonNull TimeWindow other) {
		ZonedDateTime overlapStart;
		if (start == null || other.start == null) {
			overlapStart = start != null ? start : other.start;
		} else {
			overlapStart = start.isAfter(other.start) ? start : other.start;
		}
		ZonedDateTime overlapEnd;
		if (end == null || other.end == null) {
			overlapEnd = end != null ? end : other.end;
		} else {
			overlapEnd = end.isBefore(other.end) ? end : other.end;
		}
		if (overlapStart != null && overlapEnd != null && overlapStart.isAfter(overlapEnd)) {
			return Optional.empty();
		}

		return Optional.of(new TimeWindow(overlapStart, overlapEnd));
	}

	/**
	 * Splits this TimeWindow and another TimeWindow into distinct non-overlapping ranges.
	 */
	public List<TimeWindow> splitByOverlap(@NonNull TimeWindow other) {
		List<TimeWindow> result = new ArrayList<>();

		Optional<TimeWindow> overlap = getOverlap(other);
		if (overlap.isPresent()) {
			TimeWindow overlapWindow = overlap.get();

			if (start != null && (overlapWindow.getStart() == null || start.isBefore(overlapWindow.getStart()))) {
				result.add(new TimeWindow(start, overlapWindow.getStart()));
			}

			if (end != null && (overlapWindow.getEnd() == null || end.isAfter(overlapWindow.getEnd()))) {
				result.add(new TimeWindow(overlapWindow.getEnd(), end));
			}

			if (other.start != null && (overlapWindow.getStart() == null || other.start.isBefore(overlapWindow.getStart()))) {
				result.add(new TimeWindow(other.start, overlapWindow.getStart()));
			}

			if (other.end != null && (overlapWindow.getEnd() == null || other.end.isAfter(overlapWindow.getEnd()))) {
				result.add(new TimeWindow(overlapWindow.getEnd(), other.end));
			}
		} else {
			result.add(new TimeWindow(start, end));
			result.add(new TimeWindow(other.start, other.end));
		}

		return result;
	}
}
