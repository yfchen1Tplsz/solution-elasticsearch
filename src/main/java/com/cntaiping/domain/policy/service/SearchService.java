package com.cntaiping.domain.policy.service;

import co.elastic.clients.json.JsonData;
import com.cntaiping.domain.policy.entity.PolicyEntity;
import com.cntaiping.domain.policy.mapper.PolicyMapper;
import com.cntaiping.domain.policy.repository.PolicyRepository;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tplhk-yfchen1
 * @apiNote The following service will demonstrate how to use ElasticsearchOperations (specifically DocumentOperations) for searching documents.
 *  It will also illustrate how to leverage spring-data-elasticsearch to simplify coding.
 *
 */
@Service
public class SearchService {
/*
    as spring-doc says:
       --- " Query is an interface and Spring Data Elasticsearch provides three implementations: CriteriaQuery, StringQuery and NativeQuery."
*/

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;
    @Autowired
    private PolicyRepository policyRepository;
    @Resource
    private PolicyMapper policyMapper;

    // 查询所有文档: match_all by Operations
    public List<PolicyEntity> matchAllByOperations(){
        Query query = NativeQuery.builder()
                .withQuery(q->q
                        .matchAll(m->m)
                )
                .build();
        SearchHits<PolicyEntity> searchHits = elasticsearchOperations.search(query, PolicyEntity.class);
        //解析：
        return searchHits.getSearchHits().stream().map(hit -> hit.getContent()).collect(Collectors.toList());
    }

    // 查询所有文档: match_all by SpringDataEs
    public List<PolicyEntity> matchAllByRepository() {
        List<PolicyEntity> policies = new ArrayList<>();
        policyRepository.findAll().forEach(k->policies.add(k));
        return policies;
    }

    // 全文检索: match by SearchOperations-NativeQeury，写dsl和不写的都沉默的语法，走远点眯起眼一看这不就是原生dsl么
    public List<PolicyEntity> matchSearchByOperations(String fieldStr, String searchValue){
        Query query = NativeQuery.builder()
                .withQuery(q->q
                        .match(m->m
                                .field(fieldStr)
                                .query(searchValue)
                        )
                )
                .build();
        SearchHits<PolicyEntity> searchHits = elasticsearchOperations.search(query, PolicyEntity.class);
        //解析：
        return searchHits.getSearchHits().stream().map(hit -> hit.getContent()).collect(Collectors.toList());
    }

    // 全文检索: match by SpringDataEs,相当简单但是需要指定字段基本无法动态，因此灵活性不高
    public List<PolicyEntity> matchSearchByRepository(String searchValue) {
       return policyRepository.findByProductName(searchValue);
    }

    // 多字段全文检索： multi_match by Operations-NativeQuery
    public List<PolicyEntity> multiMatchSearchByOperations(List<String> fields, String searchValue){
        Query query = NativeQuery.builder()
                .withQuery(q->q
                        .multiMatch(m->m
                                .fields(fields)
                                .query(searchValue)
                        )
                )
                .build();
        SearchHits<PolicyEntity> searchHits = elasticsearchOperations.search(query, PolicyEntity.class);
        //解析：
        return searchHits.getSearchHits().stream().map(hit -> hit.getContent()).collect(Collectors.toList());
    }

    // 多字段全文检索： multi_match by SpringDataEs
    public List<PolicyEntity> multiMatchSearchByRepository(List<String> fields, String searchValue) {
        return policyRepository.findByProductNameIn(searchValue);
    }

    // 精确值检索: term by SearchOperations
    public List<PolicyEntity> termSearchByOperations(String fieldStr, String searchValue){
/*        Query nativeQuery = NativeQuery.builder()    //仅仅对于string类型search，每个类型要用不同的query
                .withQuery(k->k
                        .term(t->t
                                .field(fieldStr)
                                .value(searchValue)
                        )
                )
                .build();*/
        Query criteriaQuery = new CriteriaQuery(     //可以用于不同类型得到查询
                new Criteria(fieldStr).is(searchValue)
        );
        SearchHits<PolicyEntity> searchHits = elasticsearchOperations.search(criteriaQuery, PolicyEntity.class);
        //解析：
        return searchHits.getSearchHits().stream().map(hit -> hit.getContent()).collect(Collectors.toList());

    }

    // 精确值检索: term by SpringDataEs
    public List<PolicyEntity> termSearchByRepository(String searchValue) {
        return policyRepository.findByPolicyNo(searchValue);     //对于springdata而言根据方法名创建的dsl使用的是"query_string"，当搜索字段为text时是match搜索，但若字段为term则是精确值term查询
    }

    // 范围值检索: range by SearchOperations
    public List<PolicyEntity> rangeSearchByOperations(String fieldStr, BigDecimal min, BigDecimal max){
/*        Query nativeQuery = NativeQuery.builder()
               .withQuery(q->q
                       .range(r->r
                               .field(fieldStr)
                               .gt(JsonData.of(min))
                               .lte(JsonData.of(max))
                        )
                )
               .build();*/
        Query criteriaQuery = new CriteriaQuery(new Criteria(fieldStr).lessThanEqual(max).greaterThan(min));
        SearchHits<PolicyEntity> searchHits = elasticsearchOperations.search(criteriaQuery, PolicyEntity.class);
        //解析：
        return searchHits.getSearchHits().stream().map(hit -> hit.getContent()).collect(Collectors.toList());
    }

    // 范围值检索: range by SpringDataEs
    public List<PolicyEntity> rangeSearchByRepository(BigDecimal min, BigDecimal max) {
        //自定义两端
        policyRepository.findByPolicyAmountLessThanAndPolicyAmountGearterThanEqual(min,max);  //左闭右开
        //时间可以用Before和After
        return policyRepository.findByPolicyAmountBetween(min, max);  //两边都含

    }


    //地理位置检索 geo_bounding_box by SearchOperation
    //TODO geo

}
