package com.cntaiping.solutionelasticsearchgis;

import com.cntaiping.domain.policy.entity.PolicyEntity;
import com.cntaiping.domain.policy.repository.PolicyRepository;
import com.cntaiping.domain.policy.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.SearchHit;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@SpringBootTest
public class SearchTests {
    @Autowired
    private SearchService searchService;
    @Autowired
    private PolicyRepository policyRepository;

    @Test
    public void matchAllByOperations(){   //默认分页10
        List<PolicyEntity> policyEntities = searchService.matchAllByOperations();
        policyEntities.stream().forEach(System.out::println);
    }

    @Test
    public void matchAllByRepository(){   //没有默认分页
        List<PolicyEntity> policyEntities = searchService.matchAllByRepository();
        policyEntities.stream().forEach(System.out::println);
    }

    @Test
    public void matchSearchByOperations(){
        List<PolicyEntity> policyEntities = searchService.matchSearchByOperations("productName", "Awesome");
        policyEntities.stream().forEach(System.out::println);
    }

    @Test
    public void matchSearchByRepository(){
        List<PolicyEntity> policyEntities = searchService.matchSearchByRepository( "Awesome");
        policyEntities.stream().forEach(System.out::println);
    }

    @Test
    public void multiMatchSearchByOperations(){
        List<String> fields = new ArrayList<>();
        fields.add("productName");
        fields.add("policyOwnerName");
        List<PolicyEntity> policyEntities = searchService.multiMatchSearchByOperations(fields,"Mr");
        policyEntities.stream().forEach(System.out::println);
    }


    @Test
    public void termSearchByOperations(){
        List<PolicyEntity> policyEntities = searchService.termSearchByOperations("policyStatus", "NB");
        policyEntities.stream().forEach(System.out::println);
    }

    @Test
    public void termSearchByRepository(){
        List<PolicyEntity> policyEntities = searchService.termSearchByRepository("NB");
        policyEntities.stream().forEach(System.out::println);
    }

    @Test
    public void rangeSearchByOperations(){
        List<PolicyEntity> policyEntities = searchService.rangeSearchByOperations("policyPremium", 7d, 70d);
        policyEntities.stream().forEach(System.out::println);
    }

    @Test
    public void rangeSearchByRepository(){
        List<PolicyEntity> policyEntities = searchService.rangeSearchByRepository(7d, 70d);
        policyEntities.stream().forEach(System.out::println);
    }

    @Test
    public void geoBoundingBoxSearchByOperations(){
        List<PolicyEntity> policyEntities = searchService.geoBoundingBoxSearchByOperations("location",30d,100d,-30d,170d);
        policyEntities.stream().forEach(System.out::println);
    }

/*    @Test   //暂时无法实现
    public void geoBoundingBoxSearchByRepository(){
        List<PolicyEntity> policyEntities = searchService.geoBoundingBoxSearchByRepository(30d,100d,-30d,170d);
        policyEntities.stream().forEach(System.out::println);
    }*/

    @Test
    public void geoDistanceSearchByOperations(){
        List<PolicyEntity> policyEntities = searchService.geoDistanceSearchByOperations("location",74d,76d,"3000km");
        policyEntities.stream().forEach(System.out::println);
    }
    @Test
    public void geoDistanceSearchByReposiotory(){
        List<PolicyEntity> policyEntities = searchService.geoDistanceSearchByRepository(74d,76d,"3000km");
        policyEntities.stream().forEach(System.out::println);
    }

    @Test
    public void functionsScoreByOperations(){
        List<SearchHit<PolicyEntity>> policyEntities = searchService.functionsScoreByOperations("Awesome","NB");
        policyEntities.stream().forEach(System.out::println);
    }

    @Test
    public void sortSearchByOperations(){
        List<SearchHit<PolicyEntity>> policyEntities = searchService.sortSearchByOperations("policyStatus","NB","policyAmount",-15.9,134.34);
        policyEntities.stream().forEach(System.out::println);
    }
    @Test
    public void sortSearchByRepository(){
        List<SearchHit<PolicyEntity>> policyEntities = searchService.sortSearchByRepository("NB");
        policyEntities.stream().forEach(System.out::println);
    }
    @Test
    public void pageSearchByOperations(){
        List<SearchHit<PolicyEntity>> policyEntities = searchService.pageSearchByOperations("policyStatus","NB",0,10);
        policyEntities.stream().forEach(System.out::println);
    }
    @Test
    public void pageSearchByRepository(){
        List<PolicyEntity> policyEntities = searchService.pageSearchByRepository("NB",0,3);
        policyEntities.stream().forEach(System.out::println);
    }

    @Test
    public void highlightByRepository(){
        List<SearchHit<PolicyEntity>> policyEntities = searchService.highlightByRepository("Heavy","Mr");
        policyEntities.stream().forEach(System.out::println);
    }


}
