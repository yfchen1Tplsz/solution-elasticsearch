package com.cntaiping.infrastructure.utils;

import com.cntaiping.domain.policy.entity.PolicyEntity;
import com.github.javafaker.Address;
import com.github.javafaker.Faker;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Component
public class FakeUtils {

    public static PolicyEntity createFakePolicy(){
        Faker faker = new Faker();
        PolicyEntity policyEntity = new PolicyEntity();
        policyEntity.setId(UUID.randomUUID());
        policyEntity.setPolicyNo("P" + faker.numerify("################"));
        policyEntity.setPolicyOwnerName(faker.name().fullName());
        policyEntity.setProductName(faker.commerce().productName());
        policyEntity.setPolicyStatus(getRandomPolicyStatus());
        policyEntity.setPolicyAmount(faker.random().nextDouble() * 1000);
        policyEntity.setPolicyPremium(faker.random().nextDouble() * 100);
        policyEntity.setExpireDate(LocalDate.now().plusDays(faker.random().nextInt(300)));
        policyEntity.setEffectiveDate(LocalDate.now().minusDays(faker.random().nextInt(30)));
        policyEntity.setRemark(faker.lorem().sentence());
        Address address = faker.address();
        policyEntity.setLocation(new GeoPoint(Double.parseDouble(address.latitude()), Double.parseDouble(address.longitude())));
        return policyEntity;
    }

    public static List<PolicyEntity> createFakePolicyList(int num){
        List<PolicyEntity> policies = new ArrayList<>();
        for (int i = 0; i < num; i++){
            policies.add(createFakePolicy());
        }
        return policies;
    }

    private static String getRandomPolicyStatus() {
        String[] policyStatuses = {"NB", "UW", "PRP", "STD", "SUB", "ISSUE", "POS"};
        Faker faker = new Faker();
        int randomIndex = faker.random().nextInt(policyStatuses.length);
        return policyStatuses[randomIndex];
    }
}
