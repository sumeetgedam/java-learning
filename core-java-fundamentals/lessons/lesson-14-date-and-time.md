# Lesson 14: Dates and Time

## Questions

1. When would you use `LocalDate`?
2. Whe would you use `LocalDateTime`?
3. Why is `LocalDateTime` not enough for global events?
4. When would you use `ZonedDateTime`?
5. What is an `Instant`?
6. What is the difference between `Duration` and `Period`?
7. How do you format a date?
8. How do you parse a date from text?
9. Why should date-time objects generally not be mutated directly?
10. Why are timezones important in distributed systems?

## My summary

- `LocalDate` does not represent a moment in time because it has no time or timezone.
- Use `LocalTime` for time without a date or timezone
- Use `LocalDateTime` for a date and time without timezone information
  - should not e used with timezone or global ordering matters
- Use `ZonedDateTime` when timezone information matters.
  - International users
  - Distributed systems
  - Scheduled events
  - Daylight-saving changes
- Use `Instant` for a machine-oriented point on the global timeline.
  - Event timestamps
  - Audit records
  - Log timestamps
  - Database timestamp
  - Measuring elapsed time
- Use `Duration` for time-based amounts such as seconds, minutes, or hours
- Use `Period` for date-based amounts such as years, months, and days
- Use `DateTimeFormatter` to format dates and times


```text
`LocalDate` represents a date, `LocalTime` represents a time,
`LocalDateTime` combines them without a timezone,
`ZonedDateTime` includes timezone information, and
`Instant` represent a global point on the timeline 
```