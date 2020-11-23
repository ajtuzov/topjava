package ru.javawebinar.topjava.web.converters;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;

import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalDate;

public class StringToLocalDateConverter implements Converter<String, LocalDate> {
    @Override
    public LocalDate convert(String source) {
        return parseLocalDate(source);
    }
}
