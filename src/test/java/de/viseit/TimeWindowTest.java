package de.viseit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.ZonedDateTime;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TimeWindowTest {
	@CsvSource(value = {
			"2024-01-01T00:00:00+01:00[Europe/Berlin],2025-01-01T00:00:00+01:00[Europe/Berlin],2024-06-01T00:00:00+01:00[Europe/Berlin],2025-06-01T00:00:00+01:00[Europe/Berlin],true",
			"2024-01-01T00:00:00+01:00[Europe/Berlin],2027-01-01T00:00:00+01:00[Europe/Berlin],2025-01-01T00:00:00+01:00[Europe/Berlin],2026-01-01T00:00:00+01:00[Europe/Berlin],true",
			"2024-01-01T00:00:00+01:00[Europe/Berlin],2025-01-01T00:00:00+01:00[Europe/Berlin],2026-01-01T00:00:00+01:00[Europe/Berlin],2027-01-01T00:00:00+01:00[Europe/Berlin],false",
			"2026-01-01T00:00:00+01:00[Europe/Berlin],2027-01-01T00:00:00+01:00[Europe/Berlin],2024-01-01T00:00:00+01:00[Europe/Berlin],2025-01-01T00:00:00+01:00[Europe/Berlin],false",
			"null,null,2024-06-01T00:00:00+01:00[Europe/Berlin],2025-06-01T00:00:00+01:00[Europe/Berlin],true",
			"2024-01-01T00:00:00+01:00[Europe/Berlin],2025-01-01T00:00:00+01:00[Europe/Berlin],null,null,true",
			"null,null,null,null,true",
			"null,2025-01-01T00:00:00+01:00[Europe/Berlin],2026-01-01T00:00:00+01:00[Europe/Berlin],2027-01-01T00:00:00+01:00[Europe/Berlin],false",
			"2024-01-01T00:00:00+01:00[Europe/Berlin],null,2026-01-01T00:00:00+01:00[Europe/Berlin],2027-01-01T00:00:00+01:00[Europe/Berlin],true",
			"2024-01-01T00:00:00+01:00[Europe/Berlin],2025-01-01T00:00:00+01:00[Europe/Berlin],null,2027-01-01T00:00:00+01:00[Europe/Berlin],true",
			"2024-01-01T00:00:00+01:00[Europe/Berlin],2025-01-01T00:00:00+01:00[Europe/Berlin],2026-01-01T00:00:00+01:00[Europe/Berlin],null,false",
			"null,2027-01-01T00:00:00+01:00[Europe/Berlin],2024-01-01T00:00:00+01:00[Europe/Berlin],2025-01-01T00:00:00+01:00[Europe/Berlin],true",
			"2026-01-01T00:00:00+01:00[Europe/Berlin],null,2024-01-01T00:00:00+01:00[Europe/Berlin],2025-01-01T00:00:00+01:00[Europe/Berlin],false",
			"2026-01-01T00:00:00+01:00[Europe/Berlin],2027-01-01T00:00:00+01:00[Europe/Berlin],null,2025-01-01T00:00:00+01:00[Europe/Berlin],false",
			"2026-01-01T00:00:00+01:00[Europe/Berlin],2027-01-01T00:00:00+01:00[Europe/Berlin],2024-01-01T00:00:00+01:00[Europe/Berlin],null,true" },
			nullValues = "null")
	@ParameterizedTest
	void isOverlapping(String start1, String end1, String start2, String end2, boolean value) {
		TimeWindow time1 = new TimeWindow(
				start1 != null ? ZonedDateTime.parse(start1) : null,
				end1 != null ? ZonedDateTime.parse(end1) : null);
		TimeWindow time2 = new TimeWindow(
				start2 != null ? ZonedDateTime.parse(start2) : null,
				end2 != null ? ZonedDateTime.parse(end2) : null);

		assertEquals(value, time1.isOverlapping(time2));
	}
}
