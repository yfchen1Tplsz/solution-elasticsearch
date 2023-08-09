package com.cntaiping.solutionelasticsearchgis;

import com.cntaiping.domain.policy.service.IndexService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@Slf4j
@SpringBootTest
class IndexTests {

    @Autowired
    private IndexService indexService;

    /*
    无法测试，因为repository会自动创建索引，因此再次创建会抛出异常
    @Test
    void indexCreate() {
        indexService.createIndex();
    }
    */
    @Test
    void indexUpdate() {
        indexService.updateIndex();
    }

    @Test
    void indexDelete() {
        indexService.deleteIndex();
    }

    @Test
    void indexGet() {
        System.out.println((Arrays.toString(indexService.getIndex().toArray())));
        indexService.getIndex().stream().forEach(k-> System.out.println("name: "+k.getName()+"/n mappings:"+k.getMapping()));
    }

    @Test
    void indexExists() {
        System.out.println("Index exists:  "+ indexService.existsIndex());
    }

    @Test
    void indexFlush() {
        indexService.refreshIndex();
    }

}
