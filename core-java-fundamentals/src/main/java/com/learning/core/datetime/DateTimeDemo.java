package com.learning.core.datetime;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class DateTimeDemo {

    public static void main(String[] args) {

        LocalDate today = LocalDate.now();
        LocalDate joiningDate = LocalDate.of(2024, 5, 20);
        System.out.println(today);
        System.out.println(joiningDate);

        LocalDate tomorrow = today.plusDays(1);
        LocalDate nextMonth = today.plusMonths(1);
        LocalDate previousYear = today.minusYears(1);

        System.out.println(today.getYear());
        System.out.println(today.getMonth());
        System.out.println(today.getDayOfWeek());

        LocalTime currentTime = LocalTime.now();
        LocalTime meetingTime = LocalTime.of(10, 30);
        System.out.println("currentTime = " + currentTime);
        System.out.println("meetingTime = " + meetingTime);

        LocalTime later = meetingTime.plusHours(2);
        LocalTime earlier = meetingTime.minusMinutes(15);
        System.out.println("later = " + later);
        System.out.println("earlier = " + earlier);

        LocalDateTime current = LocalDateTime.now();
        LocalDateTime appointment = LocalDateTime.of(
                2026, 9, 13, 10, 30
        );
        System.out.println("current = " + current);
        System.out.println("appointment = " + appointment);

        ZoneId newYork = ZoneId.of("America/New_York");
        ZonedDateTime newYorkTime = ZonedDateTime.now(newYork);

        System.out.println(newYorkTime);

        ZoneId london = ZoneId.of("Europe/London");
        ZonedDateTime londonTime = newYorkTime.withZoneSameInstant(london);
        System.out.println(londonTime);

        Instant now = Instant.now();
        System.out.println(now);

        Instant start = Instant.now();
        for (int i = 0; i < 10; i++) {
            continue;
        }
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        System.out.println("duration = " + duration.toMillis());

        Duration timeout = Duration.ofSeconds(30);
        System.out.println("timeout.toMillis() = " + timeout.toMillis());


        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2026, 9, 13);
        Period period = Period.between(startDate, endDate);
        System.out.println("period = " + period.getYears());
        System.out.println("period.getMonths() = " + period.getMonths());
        System.out.println("period.getDays() = " + period.getDays());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate date = LocalDate.of(2026, 9, 13);
        String formatted = date.format(formatter);
        System.out.println("formatted = " + formatted);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(
                "yyyy-MM-dd HH:mm:ss"
        );
        String result = LocalDateTime.now()
                .format(dateTimeFormatter);
        System.out.println("result = " + result);

        LocalDate parsedDate = LocalDate.parse("2026-09-13");
        System.out.println("parsedDate = " + parsedDate);

        LocalDate formattedParsedDate = LocalDate.parse("13-09-2026", formatter);
        System.out.println("formattedParsedDate = " + formattedParsedDate);




    }
}
