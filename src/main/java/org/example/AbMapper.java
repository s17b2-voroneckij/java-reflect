package org.example;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AbMapper {

    AbMapper INSTANCE = Mappers.getMapper(AbMapper.class );

    B AtoB(A a);
}