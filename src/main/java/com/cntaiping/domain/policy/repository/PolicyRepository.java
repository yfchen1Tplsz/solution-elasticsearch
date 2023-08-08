package com.cntaiping.domain.policy.repository;

import com.cntaiping.domain.policy.entity.PolicyEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface PolicyRepository extends ElasticsearchRepository<PolicyEntity, UUID> { //实际测试下，加上repository定义后会进行自动的索引库创建
    List<PolicyEntity> findByProductName(String searchValue);

    List<PolicyEntity> findByProductNameIn(String searchValue);

    List<PolicyEntity> findByPolicyNo(String searchValue);


    List<PolicyEntity> findByPolicyAmountBetween(BigDecimal min, BigDecimal max);

    List<PolicyEntity> findByPolicyAmountLessThanAndPolicyAmountGearterThanEqual(BigDecimal min, BigDecimal max);
}
