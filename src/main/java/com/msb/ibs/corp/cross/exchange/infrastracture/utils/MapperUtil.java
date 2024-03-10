package com.msb.ibs.corp.cross.exchange.infrastracture.utils;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

public class MapperUtil {
    private static final ModelMapper modelMapper = new ModelMapper();


    static {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT).setSkipNullEnabled(true);
    }

    public static <S, T> T map(S source, Class<T> target) {
        return modelMapper.map(source, target);
    }
}
