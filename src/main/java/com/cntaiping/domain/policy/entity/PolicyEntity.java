package com.cntaiping.domain.policy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Document(indexName = "gis-policy")
@Component
public class PolicyEntity {
    @Id
    private UUID id;
    @Field(type = FieldType.Keyword)
    private String policyNo;
    @Field(type = FieldType.Keyword)
    private String policyStatus;
    @Field(type = FieldType.Text,analyzer = "standard")  //暂时没有安装ik分词器和py分词器
    private String productName;
    @Field(type = FieldType.Text,analyzer = "standard")
    private String policyOwnerName;
    @Field(type = FieldType.Double)
    private Double policyAmount;
    @Field(type = FieldType.Double)
    private Double policyPremium;
    @Field(type = FieldType.Date,format = {},pattern = "dd/MM/yyyy")
    private LocalDate expireDate;
    @Field(type = FieldType.Date,format = {},pattern = "dd/MM/yyyy")
    private LocalDate effectiveDate;
    @Field(type = FieldType.Text,analyzer = "standard")
    private String remark;
    @GeoPointField
    private GeoPoint location;

}
