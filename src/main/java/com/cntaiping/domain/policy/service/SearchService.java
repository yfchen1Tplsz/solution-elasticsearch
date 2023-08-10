package com.cntaiping.domain.policy.service;

import co.elastic.clients.elasticsearch._types.DistanceUnit;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.TopLeftBottomRightGeoBounds;
import co.elastic.clients.json.JsonData;
import com.cntaiping.domain.policy.entity.PolicyEntity;
import com.cntaiping.domain.policy.mapper.PolicyMapper;
import com.cntaiping.domain.policy.repository.PolicyRepository;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.data.elasticsearch.core.script.Script;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author tplhk-yfchen1
 * @apiNote The following service will demonstrate how to use ElasticsearchOperations (specifically DocumentOperations) for searching documents.
 *  It will also illustrate how to leverage spring-data-elasticsearch to simplify coding.
 * @implNote  提示：下面的案例中springdata使用的repository返回值都是List<Entity>,实际上也可以是SearchHits<Entity>,为了方便展示数据使用了List以避免解析
 *
 * */


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

    // 多字段全文检索： multi_match by SpringDataEs 做不到的，因为是根据方法名
    public List<PolicyEntity> multiMatchSearchByRepository(List<String> fields, String searchValue) {
        return null;
    }

    // 精确值检索: term by SearchOperations
    public List<PolicyEntity> termSearchByOperations(String fieldStr, String searchValue){
        Query nativeQuery = NativeQuery.builder()    //仅仅对于string类型search，每个类型要用不同的query
                .withQuery(k->k
                        .term(t->t
                                .field(fieldStr)
                                .value(searchValue)
                        )
                )
                .build();

        Query criteriaQuery = new CriteriaQuery(     //可以用于不同类型得到查询
                new Criteria(fieldStr).is(searchValue)
        );
        SearchHits<PolicyEntity> searchHits = elasticsearchOperations.search(criteriaQuery, PolicyEntity.class);
        //解析：
        return searchHits.getSearchHits().stream().map(hit -> hit.getContent()).collect(Collectors.toList());

    }

    // 精确值检索: term by SpringDataEs
    public List<PolicyEntity> termSearchByRepository(String searchValue) {
        return policyRepository.findByPolicyStatus(searchValue);     //对于springdata而言根据方法名创建的dsl使用的是"query_string"，当搜索字段为text时是match搜索，但若字段为term则是精确值term查询
    }

    // 范围值检索: range by SearchOperations
    public List<PolicyEntity> rangeSearchByOperations(String fieldStr, Double min, Double max){
        Query nativeQuery = NativeQuery.builder()
               .withQuery(q->q
                       .range(r->r
                               .field(fieldStr)
                               .gt(JsonData.of(min))
                               .lte(JsonData.of(max))
                        )
                )
               .build();

        Query criteriaQuery = new CriteriaQuery(new Criteria(fieldStr).lessThanEqual(max).greaterThan(min));
        SearchHits<PolicyEntity> searchHits = elasticsearchOperations.search(criteriaQuery, PolicyEntity.class);
        //解析：
        return searchHits.getSearchHits().stream().map(hit -> hit.getContent()).collect(Collectors.toList());
    }

    // 范围值检索: range by SpringDataEs
    public List<PolicyEntity> rangeSearchByRepository(Double min, Double max) {
        //自定义两端
        policyRepository.findByPolicyAmountLessThanAndPolicyAmountGreaterThanEqual(min,max);  //左闭右开
        //时间可以用Before和After
        return policyRepository.findByPolicyAmountBetween(min, max);  //两边都含

    }

    //地理位置检索 geo_bounding_box by SearchOperation
    public List<PolicyEntity> geoBoundingBoxSearchByOperations(String fieldStr,Double topLeftLat,Double topLeftLon,Double bottomRightLat,Double bottomRightLon){
        TopLeftBottomRightGeoBounds geoBounds = TopLeftBottomRightGeoBounds.of(builder ->
                builder.topLeft(tf->tf
                                .latlon(ll->ll
                                        .lat(topLeftLat)
                                        .lon(topLeftLon)
                                )
                        )
                        .bottomRight(br->br
                                .latlon(ll->ll
                                        .lat(bottomRightLat)
                                        .lon(bottomRightLon)
                                )
                        )
        );
        Query nativeQuery = NativeQuery.builder()
              .withQuery(q->q
                      .geoBoundingBox(r->r
                              .field(fieldStr)
                              .boundingBox(b->b
                                      .tlbr(geoBounds)
                              )
                        )
                )
              .build();
        SearchHits<PolicyEntity> searchHits = elasticsearchOperations.search(nativeQuery, PolicyEntity.class);
        //解析：
        return searchHits.getSearchHits().stream().map(hit -> hit.getContent()).collect(Collectors.toList());
    }

//    // 地理位置检索 geo_bounding_box by SpringDataEs //暂时无法实现功能
//    public List<PolicyEntity> geoBoundingBoxSearchByRepository(Double topLeftLat,Double topLeftLon,Double bottomRightLat,Double bottomRightLon) {
//        GeoPoint gpLeftTop = new GeoPoint(topLeftLat, topLeftLon);
//        GeoPoint gpBottomRight =  new GeoPoint(bottomRightLat, bottomRightLon);
//        return policyRepository.findByLocationWithinLocation(gpLeftTop, gpBottomRight);
//    }

    // 地理距离检索 geo_distance by Operations
    public List<PolicyEntity> geoDistanceSearchByOperations(String fieldStr,Double lat,Double lon,String distance){
        Query query = NativeQuery.builder()
                .withQuery(q->q
                        .geoDistance(g->g
                                .field(fieldStr)
                                .location(l->l
                                        .latlon(ll->ll
                                                .lat(lat)
                                                .lon(lon)
                                        )
                                )
                                .distance(distance)

                        )
                )
                .build();
        SearchHits<PolicyEntity> searchHits = elasticsearchOperations.search(query, PolicyEntity.class);
        //解析
        return searchHits.getSearchHits().stream().map(hit -> hit.getContent()).collect(Collectors.toList());
    }

    // 地理距离检索 geo_distance by Repository
    //自定义方法名的方式暂时不支持geo_distance查询，如果仍然想用repository实现，可以用@query来做原生查询，下面是案例
    public List<PolicyEntity> geoDistanceSearchByRepository(Double lat,Double lon,String distance){
        return policyRepository.findByLocationDistance(lat,lon,distance);
    }

    // 相关性算分修正 functions by Operations,下面将演示如何使用operations创建一个模板，然后使用它，这有些类似pg存储过程
    public List<SearchHit<PolicyEntity>> functionsScoreByOperations(String productName,String policyStatus){
        elasticsearchOperations.putScript(
                Script.builder()
                        .withId("functionScore")
                        .withLanguage("mustache")
                        .withSource("""
                                {
                                     "query": {
                                       "function_score": {
                                         "query": {
                                           "match": {
                                             "productName": "{{productName}}"
                                           }
                                         },
                                         "functions": [
                                           {
                                             "filter": {
                                               "term": {
                                                 "policyStatus": "{{policyStatus}}"
                                               }
                                             },
                                             "weight": 10
                                           }
                                         ],
                                         "boost_mode": "multiply"
                                       }
                                     }
                                   }
                                """)
                        .build()
        );
        var query = SearchTemplateQuery.builder()
                .withId("functionScore")
                .withParams(
                        Map.of(
                                "productName", productName,
                                "policyStatus",policyStatus
                        )
                )
                .build();
        SearchHits<PolicyEntity> searchHits = elasticsearchOperations.search(query, PolicyEntity.class);
        //解析
        return searchHits.getSearchHits();
    }

    // 布尔复合查询 bool by Operations
    public List<SearchHit<PolicyEntity>> boolSearchByOperations(String fieldStr,String searchValue,String shouldField,String shouldValue1,String shouldValue2){
        Query query = NativeQuery.builder()
                .withQuery(q->q
                        .bool(b->b
                                .must(m -> m
                                        .match(t->t
                                                .field(fieldStr)
                                                .query(searchValue)
                                        )
                                )
                                .should(s->s
                                        .term(t->t
                                                .field(shouldField)
                                                .value(shouldValue1)
                                        )
                                )
                                .should(s->s
                                        .term(t->t
                                                .field(shouldField)
                                                .value(shouldValue2)
                                        )
                                )
                        )
                ).build();
        SearchHits<PolicyEntity> searchHits = elasticsearchOperations.search(query, PolicyEntity.class);
        //解析
        return searchHits.getSearchHits();
    }

    // 布尔复合查询 bool by Repository 只需要使用And Or Not即可，但是无法实现很复杂的嵌套逻辑以及不参与算分的filter与
    //略

    //对搜索结果进行排序 sort by Operations
    public List<SearchHit<PolicyEntity>> sortSearchByOperations(String termField,String termValue,String sortField,Double lat,Double lon){
        Query query = NativeQuery.builder()
               .withQuery(q->q
                       .term(m->m
                               .field(termField)
                               .value(termValue)
                       )
                )
                .withSort(s->s
                        .geoDistance(g->g
                                .field("location")
                                .location(l->l
                                        .latlon(ll->ll
                                                .lat(lat)
                                                .lon(lon)
                                        )
                                )
                                .unit(DistanceUnit.Kilometers)
                                .order(SortOrder.Asc)
                        )
                )
                .withSort(s->s
                        .field(f->f
                                .field(sortField)
                                .order(SortOrder.Desc)
                        )
                )
                .build();
        SearchHits<PolicyEntity> searchHits = elasticsearchOperations.search(query, PolicyEntity.class);
        //解析
        return searchHits.getSearchHits();
    }

    //对搜索结果进行排序 sort by Repository
    public List<SearchHit<PolicyEntity>> sortSearchByRepository(String policyStatus){
        SearchHits<PolicyEntity> searchHits = policyRepository.findByPolicyStatusOrderByPolicyAmountDesc(policyStatus);
        return searchHits.getSearchHits();
    }

    public List<SearchHit<PolicyEntity>> pageSearchByOperations(String termField,String termValue,Integer page,Integer pageSize){
//        Integer from = (page-1)*pageSize;
        Query query = NativeQuery.builder()
                .withQuery(q->q
                        .term(t->t
                                .field(termField)
                                .value(termValue)
                        )
                )
                .withSort(s->s
                        .field(f->f
                                .field("policyAmount")
                                .order(SortOrder.Desc)
                        )
                )
                .build().setPageable(Pageable.ofSize(pageSize).withPage(page));
        SearchHits<PolicyEntity> searchHits = elasticsearchOperations.search(query, PolicyEntity.class);
        //解析
        return searchHits.getSearchHits();
    }

    public List<PolicyEntity> pageSearchByRepository(String termValue,Integer page,Integer pageSize){
        Pageable pageable = Pageable.ofSize(pageSize).withPage(page);
        Page<PolicyEntity> pages = policyRepository.findByPolicyStatus(termValue,pageable);
        return pages.getContent();
    }

    public List<SearchHit<PolicyEntity>> highlightByRepository(String productName,String policyOwner){
        SearchHits<PolicyEntity> searchHits = policyRepository.findByProductNameOrPolicyOwnerName(productName, policyOwner);
        return searchHits.getSearchHits();
    }


}
