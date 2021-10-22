
package com.societecooperativegroupements.core.models.alkemics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "totalResults",
    "next_page",
    "data",
    "offset"
})
public class Alkemics {

    @JsonProperty("totalResults")
    private Integer totalResults;
    @JsonProperty("next_page")
    private String next_page;
    @JsonProperty("data")
    private List<AkDatum> data = null;
    @JsonProperty("offset")
    private Integer offset;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("totalResults")
    public Integer getTotalResults() {
        return totalResults;
    }

    @JsonProperty("totalResults")
    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }
    
    @JsonProperty("next_page")
    public String getNextPage() {
        return next_page;
    }

    @JsonProperty("next_page")
    public void setNextPage(String next_page) {
        this.next_page = next_page;
    }

    @JsonProperty("data")
    public List<AkDatum> getData() {
        return data != null ? new ArrayList<>(data) : null;
    }

    @JsonProperty("data")
    public void setData(List<AkDatum> data) {
        this.data = data != null ? new ArrayList<>(data) : null;
    }

    @JsonProperty("offset")
    public Integer getOffset() {
        return offset;
    }

    @JsonProperty("offset")
    public void setOffset(Integer offset) {
        this.offset = offset;
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
