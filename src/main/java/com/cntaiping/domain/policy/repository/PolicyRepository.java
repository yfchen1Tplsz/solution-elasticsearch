package com.cntaiping.domain.policy.repository;

import com.cntaiping.domain.policy.entity.PolicyEntity;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface PolicyRepository extends ElasticsearchRepository<PolicyEntity, UUID> { //实际测试下，加上repository定义后会进行自动的索引库创建
    List<PolicyEntity> findByProductName(String searchValue);

    List<PolicyEntity> findByProductNameIn(String searchValue);

    List<PolicyEntity> findByPolicyStatus(String searchValue);

    List<PolicyEntity> findByPolicyAmountLessThanAndPolicyAmountGreaterThanEqual(Double min, Double max);

    List<PolicyEntity> findByPolicyAmountBetween(Double min, Double max);

//    List<PolicyEntity> findByLocationWithinLocation(GeoPoint gpLeftTop, GeoPoint gpBottomRight);

   @Query("{\"geo_distance\":{\"distance\": \"?2\",\"location\":{\"lat\": ?0,\"lon\": ?1}}}")
    List<PolicyEntity> findByLocationDistance(Double lat, Double lon, String distance);

//    SearchHits<PolicyEntity> findByPolicyOwnerNameAndPolicyNoWithSearchTemplate(String policyOwnerName, String policyNo);
}
