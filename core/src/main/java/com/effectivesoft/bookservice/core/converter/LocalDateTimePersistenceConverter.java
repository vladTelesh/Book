package com.effectivesoft.bookservice.core.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Converter(autoApply = true)
public class LocalDateTimePersistenceConverter implements
        AttributeConverter<LocalDateTime, Timestamp> {

    @Override
    public Timestamp convertToDatabaseColumn(LocalDateTime attribute) {
        if(attribute != null){
            return Timestamp.valueOf(attribute);
        } else {
            return null;
        }
    }

    @Override
    public LocalDateTime convertToEntityAttribute(Timestamp dbData) {
        if(dbData != null) {
            return dbData.toLocalDateTime();
        } else {
            return null;
        }
    }
}
