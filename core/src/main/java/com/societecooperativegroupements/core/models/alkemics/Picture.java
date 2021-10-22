
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
    "product_key_id",
    "sequenceNumber",
    "productFace",
    "height",
    "uniformResourceIdentifierOrigin",
    "backgroundScore",
    "updatedAt",
    "createdAt",
    "angleOther",
    "gdsnFileName",
    "typeOfInformation",
    "ratio",
    "angleHorizontal",
    "width",
    "fileType",
    "resolutionY",
    "resolutionX",
    "scope",
    "contentType",
    "format",
    "webOptimizedType",
    "crc32",
    "fileEffectiveStartDateTime",
    "definition",
    "fileEffectiveEndDateTime",
    "url",
    "ipUsageRights",
    "exportables",
    "isPackshot",
    "angleVertical",
    "resolution"
})
public class Picture {

    @JsonProperty("product_key_id")
    private Integer productKeyId;
    @JsonProperty("sequenceNumber")
    private Integer sequenceNumber;
    @JsonProperty("productFace")
    private Integer productFace;
    @JsonProperty("height")
    private Integer height;
    @JsonProperty("uniformResourceIdentifierOrigin")
    private Object uniformResourceIdentifierOrigin;
    @JsonProperty("backgroundScore")
    private Integer backgroundScore;
    @JsonProperty("updatedAt")
    private String updatedAt;
    @JsonProperty("createdAt")
    private String createdAt;
    @JsonProperty("angleOther")
    private Object angleOther;
    @JsonProperty("gdsnFileName")
    private String gdsnFileName;
    @JsonProperty("typeOfInformation")
    private Object typeOfInformation;
    @JsonProperty("ratio")
    private String ratio;
    @JsonProperty("angleHorizontal")
    private Integer angleHorizontal;
    @JsonProperty("width")
    private Integer width;
    @JsonProperty("fileType")
    private Integer fileType;
    @JsonProperty("resolutionY")
    private Integer resolutionY;
    @JsonProperty("resolutionX")
    private Integer resolutionX;
    @JsonProperty("scope")
    private String scope;
    @JsonProperty("contentType")
    private Integer contentType;
    @JsonProperty("format")
    private String format;
    @JsonProperty("webOptimizedType")
    private Object webOptimizedType;
    @JsonProperty("crc32")
    private String crc32;
    @JsonProperty("fileEffectiveStartDateTime")
    private String fileEffectiveStartDateTime;
    @JsonProperty("definition")
    private String definition;
    @JsonProperty("fileEffectiveEndDateTime")
    private String fileEffectiveEndDateTime;
    @JsonProperty("url")
    private String url;
    @JsonProperty("ipUsageRights")
    private Object ipUsageRights;
    @JsonProperty("exportables")
    private List<Exportable> exportables = null;
    @JsonProperty("isPackshot")
    private Boolean isPackshot;
    @JsonProperty("angleVertical")
    private Integer angleVertical;
    @JsonProperty("resolution")
    private String resolution;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("product_key_id")
    public Integer getProductKeyId() {
        return productKeyId;
    }

    @JsonProperty("product_key_id")
    public void setProductKeyId(Integer productKeyId) {
        this.productKeyId = productKeyId;
    }

    @JsonProperty("sequenceNumber")
    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    @JsonProperty("sequenceNumber")
    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    @JsonProperty("productFace")
    public Integer getProductFace() {
        return productFace;
    }

    @JsonProperty("productFace")
    public void setProductFace(Integer productFace) {
        this.productFace = productFace;
    }

    @JsonProperty("height")
    public Integer getHeight() {
        return height;
    }

    @JsonProperty("height")
    public void setHeight(Integer height) {
        this.height = height;
    }

    @JsonProperty("uniformResourceIdentifierOrigin")
    public Object getUniformResourceIdentifierOrigin() {
        return uniformResourceIdentifierOrigin;
    }

    @JsonProperty("uniformResourceIdentifierOrigin")
    public void setUniformResourceIdentifierOrigin(Object uniformResourceIdentifierOrigin) {
        this.uniformResourceIdentifierOrigin = uniformResourceIdentifierOrigin;
    }

    @JsonProperty("backgroundScore")
    public Integer getBackgroundScore() {
        return backgroundScore;
    }

    @JsonProperty("backgroundScore")
    public void setBackgroundScore(Integer backgroundScore) {
        this.backgroundScore = backgroundScore;
    }

    @JsonProperty("updatedAt")
    public String getUpdatedAt() {
        return updatedAt;
    }

    @JsonProperty("updatedAt")
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @JsonProperty("createdAt")
    public String getCreatedAt() {
        return createdAt;
    }

    @JsonProperty("createdAt")
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty("angleOther")
    public Object getAngleOther() {
        return angleOther;
    }

    @JsonProperty("angleOther")
    public void setAngleOther(Object angleOther) {
        this.angleOther = angleOther;
    }

    @JsonProperty("gdsnFileName")
    public String getGdsnFileName() {
        return gdsnFileName;
    }

    @JsonProperty("gdsnFileName")
    public void setGdsnFileName(String gdsnFileName) {
        this.gdsnFileName = gdsnFileName;
    }

    @JsonProperty("typeOfInformation")
    public Object getTypeOfInformation() {
        return typeOfInformation;
    }

    @JsonProperty("typeOfInformation")
    public void setTypeOfInformation(Object typeOfInformation) {
        this.typeOfInformation = typeOfInformation;
    }

    @JsonProperty("ratio")
    public String getRatio() {
        return ratio;
    }

    @JsonProperty("ratio")
    public void setRatio(String ratio) {
        this.ratio = ratio;
    }

    @JsonProperty("angleHorizontal")
    public Integer getAngleHorizontal() {
        return angleHorizontal;
    }

    @JsonProperty("angleHorizontal")
    public void setAngleHorizontal(Integer angleHorizontal) {
        this.angleHorizontal = angleHorizontal;
    }

    @JsonProperty("width")
    public Integer getWidth() {
        return width;
    }

    @JsonProperty("width")
    public void setWidth(Integer width) {
        this.width = width;
    }

    @JsonProperty("fileType")
    public Integer getFileType() {
        return fileType;
    }

    @JsonProperty("fileType")
    public void setFileType(Integer fileType) {
        this.fileType = fileType;
    }

    @JsonProperty("resolutionY")
    public Integer getResolutionY() {
        return resolutionY;
    }

    @JsonProperty("resolutionY")
    public void setResolutionY(Integer resolutionY) {
        this.resolutionY = resolutionY;
    }

    @JsonProperty("resolutionX")
    public Integer getResolutionX() {
        return resolutionX;
    }

    @JsonProperty("resolutionX")
    public void setResolutionX(Integer resolutionX) {
        this.resolutionX = resolutionX;
    }

    @JsonProperty("scope")
    public String getScope() {
        return scope;
    }

    @JsonProperty("scope")
    public void setScope(String scope) {
        this.scope = scope;
    }

    @JsonProperty("contentType")
    public Integer getContentType() {
        return contentType;
    }

    @JsonProperty("contentType")
    public void setContentType(Integer contentType) {
        this.contentType = contentType;
    }

    @JsonProperty("format")
    public String getFormat() {
        return format;
    }

    @JsonProperty("format")
    public void setFormat(String format) {
        this.format = format;
    }

    @JsonProperty("webOptimizedType")
    public Object getWebOptimizedType() {
        return webOptimizedType;
    }

    @JsonProperty("webOptimizedType")
    public void setWebOptimizedType(Object webOptimizedType) {
        this.webOptimizedType = webOptimizedType;
    }

    @JsonProperty("crc32")
    public String getCrc32() {
        return crc32;
    }

    @JsonProperty("crc32")
    public void setCrc32(String crc32) {
        this.crc32 = crc32;
    }

    @JsonProperty("fileEffectiveStartDateTime")
    public String getFileEffectiveStartDateTime() {
        return fileEffectiveStartDateTime;
    }

    @JsonProperty("fileEffectiveStartDateTime")
    public void setFileEffectiveStartDateTime(String fileEffectiveStartDateTime) {
        this.fileEffectiveStartDateTime = fileEffectiveStartDateTime;
    }

    @JsonProperty("definition")
    public String getDefinition() {
        return definition;
    }

    @JsonProperty("definition")
    public void setDefinition(String definition) {
        this.definition = definition;
    }

    @JsonProperty("fileEffectiveEndDateTime")
    public String getFileEffectiveEndDateTime() {
        return fileEffectiveEndDateTime;
    }

    @JsonProperty("fileEffectiveEndDateTime")
    public void setFileEffectiveEndDateTime(String fileEffectiveEndDateTime) {
        this.fileEffectiveEndDateTime = fileEffectiveEndDateTime;
    }

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty("ipUsageRights")
    public Object getIpUsageRights() {
        return ipUsageRights;
    }

    @JsonProperty("ipUsageRights")
    public void setIpUsageRights(Object ipUsageRights) {
        this.ipUsageRights = ipUsageRights;
    }

    @JsonProperty("exportables")
    public List<Exportable> getExportables() {
        return exportables != null ? new ArrayList<>(exportables) : null;
    }

    @JsonProperty("exportables")
    public void setExportables(List<Exportable> exportables) {
        this.exportables = exportables != null ? new ArrayList<>(exportables) : null;
    }

    @JsonProperty("isPackshot")
    public Boolean getIsPackshot() {
        return isPackshot;
    }

    @JsonProperty("isPackshot")
    public void setIsPackshot(Boolean isPackshot) {
        this.isPackshot = isPackshot;
    }

    @JsonProperty("angleVertical")
    public Integer getAngleVertical() {
        return angleVertical;
    }

    @JsonProperty("angleVertical")
    public void setAngleVertical(Integer angleVertical) {
        this.angleVertical = angleVertical;
    }

    @JsonProperty("resolution")
    public String getResolution() {
        return resolution;
    }

    @JsonProperty("resolution")
    public void setResolution(String resolution) {
        this.resolution = resolution;
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
