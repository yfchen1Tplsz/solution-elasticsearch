package com.cntaiping.domain.policy.service;

import com.cntaiping.domain.policy.entity.PolicyEntity;
import com.cntaiping.domain.policy.mapper.PolicyMapper;
import com.cntaiping.domain.policy.repository.PolicyRepository;
import com.cntaiping.infrastructure.exception.ApplicationException;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexedObjectInformation;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * @author tplhk-yfchen1
 * @apiNote The following service is used to demonstrate how to operate an ES document with a specific ID using ElasticsearchOperations (specifically DocumentOperations).
 *      It also showcases how to utilize spring-data-elasticsearch to simplify coding.
 */
@Service
public class DocumentService {
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;
    @Autowired
    private PolicyRepository policyRepository;
    @Resource
    private PolicyMapper policyMapper;

    // 是否存在文档 - by DecumentOperations
    public boolean existsByIdByOperations(UUID id){
        return elasticsearchOperations.exists(id.toString(), PolicyEntity.class);
    }

    // 是否存在文档 - by SpringDataEs
    public boolean existsByIdByRepository(UUID id){
        return policyRepository.existsById(id);
    }

    //新增文档 - by DocumentOperations
    public PolicyEntity addPolicyByOperations(PolicyEntity policyEntity){
        return elasticsearchOperations.save(policyEntity);
    }

    //新增文档 - by SpringDataEs
    public PolicyEntity addPolicyByRepository(PolicyEntity policyEntity){
        return policyRepository.save(policyEntity);
    }

    // 全量修改文档 - by DocumentOperations
    public PolicyEntity updatePolicyByOperations(PolicyEntity policyEntity){
        if(!elasticsearchOperations.exists(policyEntity.getId().toString(), PolicyEntity.class)){
            throw new ApplicationException("不存在该id对应的保单实体");
        }
        return elasticsearchOperations.save(policyEntity);
    }

    // 全量修改文档 - by SpringDataEs
    public PolicyEntity updatePolicyByRepository(PolicyEntity policyEntity){
        if(!policyRepository.existsById(policyEntity.getId())){
            throw new ApplicationException("不存在该id对应的保单实体");
        }
        return policyRepository.save(policyEntity);
    }

    // 增量修改文档 - by DocumentOperations
    public PolicyEntity dynamicUpdatePolicyByOperations(PolicyEntity policyEntity){
        PolicyEntity policyDb = elasticsearchOperations.get(policyEntity.getId().toString(), PolicyEntity.class);
        if(policyDb == null){
            throw new ApplicationException("不存在该id对应的保单实体");
        }
        policyMapper.toPolicyEntity(policyEntity,policyDb);
        return elasticsearchOperations.save(policyDb);
    }

    // 增量修改文档 - by SpringDataEs
    public PolicyEntity dynamicUpdatePolicyByRepository(PolicyEntity policyEntity){
        PolicyEntity policyDb = policyRepository.findById(policyEntity.getId())
                .orElseThrow(()->new ApplicationException("不存在该id对应的保单实体"));
        policyMapper.toPolicyEntity(policyEntity,policyDb);
        return policyRepository.save(policyDb);
    }

    // 删除文档 - by DocumentOperations
    public UUID deletePolicyByOperations(UUID id){
        String policyIdStr = elasticsearchOperations.delete(id.toString(),PolicyEntity.class);
        if(!StringUtils.hasText(policyIdStr)){
            throw new ApplicationException("不存在该id对应的保单实体");
        }
        return UUID.fromString(policyIdStr);
    }

    // 删除文档 - by SpringDataEs
    public void deletePolicyByRepository(UUID id){
        policyRepository.deleteById(id);
    }

    // 获取文档 - by DocumentOperations
    public PolicyEntity getPolicyByOperations(UUID id){
        return elasticsearchOperations.get(id.toString(),PolicyEntity.class);
    }

    // 获取文档 - by SpringDataEs
    public PolicyEntity getPolicyByRepository(UUID id){
        return policyRepository.findById(id).orElseThrow(()->new ApplicationException("不存在该id对应的保单实体"));
    }

    // 批量导入文档 - byDocumentOperations
    public List<IndexedObjectInformation> batchInsertPolicyByOperations(List<PolicyEntity> policyEntities){
        return elasticsearchOperations.bulkIndex(policyEntities.stream().map(k -> new IndexQueryBuilder().withObject(k).build()).collect(Collectors.toList()),PolicyEntity.class);
    }

    public List<PolicyEntity> batchInsertPolicyByRepository(List<PolicyEntity> policyEntities){
        List<PolicyEntity> policies = new ArrayList<>();
        policyRepository.saveAll(policyEntities).forEach(k->policies.add(k));
        return policies;
    }
}
