package com.example.woowa.restaurant.advertisement.converter;

import com.example.woowa.common.interfaces.EnumFindable;
import com.example.woowa.restaurant.advertisement.enums.RateType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class RateTypeConverter implements AttributeConverter<RateType, String> {

    @Override
    public String convertToDatabaseColumn(RateType attribute) {
        return attribute.getType();
    }

    @Override
    public RateType convertToEntityAttribute(String dbData) {
        return EnumFindable.find(dbData, RateType.values());
    }

}
