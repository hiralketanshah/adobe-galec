
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
    "isConceptualizedBy"
})
public class IsLabeledBy {

    @JsonProperty("isConceptualizedBy")
    private IsConceptualizedBy isConceptualizedBy;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("isConceptualizedBy")
    public IsConceptualizedBy getIsConceptualizedBy() {
        return isConceptualizedBy;
    }

    @JsonProperty("isConceptualizedBy")
    public void setIsConceptualizedBy(IsConceptualizedBy isConceptualizedBy) {
        this.isConceptualizedBy = isConceptualizedBy;
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
