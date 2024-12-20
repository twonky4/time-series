package de.viseit;

import java.math.BigDecimal;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@Getter
@RequiredArgsConstructor
public class TimeBoundedValue {
	private final TimeWindow timeWindow;
	private final BigDecimal value;
}
