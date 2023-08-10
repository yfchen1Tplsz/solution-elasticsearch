package com.cntaiping.solutionelasticsearchgis;

import com.cntaiping.domain.policy.entity.PolicyEntity;
import com.cntaiping.domain.policy.service.DocumentService;
import com.cntaiping.infrastructure.utils.FakeUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.IndexedObjectInformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@SpringBootTest
public class DocumentTests {
    @Autowired
    private DocumentService documentService;

    @Test
    void testFakeUtils(){
        System.out.println(FakeUtils.createFakePolicy().toString());
    }

    @Test
    void createPolicyByOperations(){
        PolicyEntity policyEntity = documentService.addPolicyByOperations(FakeUtils.createFakePolicy());
        System.out.println(policyEntity.toString());
    }
    @Test
    void createPolicyByRepository(){
        PolicyEntity policyEntity = documentService.addPolicyByRepository(FakeUtils.createFakePolicy());
        System.out.println(policyEntity.toString());
    }
    @Test
    void deletePolicyByOperations(){
        UUID id = documentService.deletePolicyByOperations(UUID.fromString("18dfdcf3-3f0a-4a2c-892e-28f05bf2e88e"));
        System.out.println(id);
    }

    @Test
    void deletePolicyByRepository(){
        UUID id = documentService.deletePolicyByOperations(UUID.fromString("70c95089-cf7f-49de-8daa-2797c7b3d0d9"));
        System.out.println(id);
    }

    // f3b07bc2-c47b-4841-8e3d-0a506fc70d22
    @Test
    void updatePolicyByOperations(){
        PolicyEntity policyEntity = FakeUtils.createFakePolicy();
        policyEntity.setId(UUID.fromString("f3b07bc2-c47b-4841-8e3d-0a506fc70d22"));
        documentService.updatePolicyByOperations(policyEntity);
        System.out.println(policyEntity.toString());
    }
    @Test
    void updatePolicyByRepository(){
        PolicyEntity policyEntity = FakeUtils.createFakePolicy();
        policyEntity.setId(UUID.fromString("f3b07bc2-c47b-4841-8e3d-0a506fc70d22"));
        documentService.updatePolicyByRepository(policyEntity);
        System.out.println(policyEntity.toString());
    }
    @Test
    void dynamicUpdatePolicyByOperations(){
        PolicyEntity policyEntity = new PolicyEntity();
        policyEntity.setId(UUID.fromString("f3b07bc2-c47b-4841-8e3d-0a506fc70d22"));
        policyEntity.setPolicyNo("P000000000000000");
        policyEntity.setRemark("这是一条很新的备注");
        PolicyEntity policyEntityReturn = documentService.dynamicUpdatePolicyByOperations(policyEntity);
        System.out.println(policyEntityReturn.toString());
    }

    @Test
    void dynamicUpdatePolicyByRepository(){
        PolicyEntity policyEntity = new PolicyEntity();
        policyEntity.setId(UUID.fromString("f3b07bc2-c47b-4841-8e3d-0a506fc70d22"));
        policyEntity.setPolicyNo("P000000000000001");
        policyEntity.setRemark("这是一条更新的备注捏，love form repository！");
        PolicyEntity policyEntityReturn = documentService.dynamicUpdatePolicyByRepository(policyEntity);
        System.out.println(policyEntityReturn.toString());
    }
    @Test
    void existsPolicyByRepository(){
        boolean exists = documentService.existsByIdByRepository(UUID.fromString("f3b07bc2-c47b-4841-8e3d-0a506fc70d22"));
        System.out.println(exists);
    }

    @Test
    void existsPolicyByOperations(){
        boolean exists = documentService.existsByIdByOperations(UUID.fromString("b3b07bc2-c47b-4841-8e3d-0a506fc70d22"));
        System.out.println(exists);
    }

    @Test
    void getPolicyByOperations(){
        PolicyEntity policyEntity = documentService.getPolicyByOperations(UUID.fromString("f3b07bc2-c47b-4841-8e3d-0a506fc70d22"));
        System.out.println(policyEntity.toString());
    }

    @Test
    void getPolicyByRepository(){
        PolicyEntity policyEntity = documentService.getPolicyByRepository(UUID.fromString("f3b07bc2-c47b-4841-8e3d-0a506fc70d22"));
        System.out.println(policyEntity.toString());
    }

    @Test
    void batchInsertPolicyByOperations(){
        List<PolicyEntity> policyEntities = FakeUtils.createFakePolicyList(20);
        List<IndexedObjectInformation> indexedObjectInformationList = documentService.batchInsertPolicyByOperations(policyEntities);
        System.out.println(Arrays.toString(indexedObjectInformationList.toArray()));
    }

    @Test
    void batchInsertPolicyByRepository(){
        List<PolicyEntity> policyEntities = FakeUtils.createFakePolicyList(80);
        List<PolicyEntity> policyEntitiesReturn = documentService.batchInsertPolicyByRepository(policyEntities);
        System.out.println(Arrays.toString(policyEntitiesReturn.toArray()));
    }

}
