package com.cntaiping.domain.policy.mapper;

import com.cntaiping.domain.policy.entity.PolicyEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface PolicyMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toPolicyEntity(PolicyEntity policyEntity,@MappingTarget PolicyEntity policyDb);
}
