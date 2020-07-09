
package com.societecooperativegroupements.core.models.alkemics;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "product_id",
    "organization_id",
    "source_type",
    "target_market_id",
    "source_id"
})
public class ProductKey {

    @JsonProperty("product_id")
    private Integer productId;
    @JsonProperty("organization_id")
    private Integer organizationId;
    @JsonProperty("source_type")
    private String sourceType;
    @JsonProperty("target_market_id")
    private Integer targetMarketId;
    @JsonProperty("source_id")
    private Integer sourceId;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("product_id")
    public Integer getProductId() {
        return productId;
    }

    @JsonProperty("product_id")
    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    @JsonProperty("organization_id")
    public Integer getOrganizationId() {
        return organizationId;
    }

    @JsonProperty("organization_id")
    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    @JsonProperty("source_type")
    public String getSourceType() {
        return sourceType;
    }

    @JsonProperty("source_type")
    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    @JsonProperty("target_market_id")
    public Integer getTargetMarketId() {
        return targetMarketId;
    }

    @JsonProperty("target_market_id")
    public void setTargetMarketId(Integer targetMarketId) {
        this.targetMarketId = targetMarketId;
    }

    @JsonProperty("source_id")
    public Integer getSourceId() {
        return sourceId;
    }

    @JsonProperty("source_id")
    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
