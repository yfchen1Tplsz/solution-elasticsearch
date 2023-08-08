package com.cntaiping.domain.policy.service;

import com.cntaiping.domain.policy.entity.PolicyEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexInformation;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author tplhk-yfchen1
 * @apiNote The service is used to demonstrate how to handle es-indexes by IndexOperations
 */
@Service
public class IndexService {
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    // 创建索引库
    public boolean createIndex(){
        //indexOperations should be obtained by ElasticsearchOperations::indexOps(clazz), clazz is Entity.getClass
        IndexOperations indexOperations = elasticsearchOperations.indexOps(PolicyEntity.class);
        return indexOperations.create(); //return true if the index was created
    }
    // 修改索引库
    public void updateIndex(){
        elasticsearchOperations.indexOps(PolicyEntity.class).putMapping();  //return true if the mapping could be stored
    }

    // 删除索引库
    public boolean deleteIndex(){
        return elasticsearchOperations.indexOps(PolicyEntity.class).delete(); //return true if the index was deleted
    }

    //查看索引库信息
    public List<IndexInformation> getIndex(){
        return elasticsearchOperations.indexOps(PolicyEntity.class).getInformation();
    }

    // 判断索引是否存在
    public boolean existsIndex(){
        return elasticsearchOperations.indexOps(PolicyEntity.class).exists(); //return true if the index exists
    }

    // 刷新索引库
    public void refreshIndex(){
        elasticsearchOperations.indexOps(PolicyEntity.class).refresh();
    }

}
