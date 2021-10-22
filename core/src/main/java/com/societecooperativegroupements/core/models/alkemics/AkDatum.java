
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
    "assetsLastRequests",
    "expressedIn",
    "unitType",
    "gtin",
    "updatedAt",
    "owner",
    "id",
    "supplierId",
    "createdAt",
    "uuid",
    "isClassifiedIn",
    "product_key",
    "barcodeScanText",
    "namePublicLong",
    "lastUserHistoryDate",
    "typePackaging",
    "brand",
    "isConsumerUnit",
    "description",
    "tags",
    "manufacturerSharingStatus",
    "lastRequest",
    "kind",
    "assets",
    "specializes",
    "targetProductStatus",
    "lifeCycle",
    "textileModel",
    "isDespatchUnit",
    "validation",
    "isLabeledBy"
})
public class AkDatum {

    @JsonProperty("assetsLastRequests")
    private AssetsLastRequests assetsLastRequests;
    @JsonProperty("expressedIn")
    private List<ExpressedIn> expressedIn = null;
    @JsonProperty("unitType")
    private String unitType;
    @JsonProperty("gtin")
    private String gtin;
    @JsonProperty("updatedAt")
    private String updatedAt;
    @JsonProperty("owner")
    private Owner owner;
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("supplierId")
    private String supplierId;
    @JsonProperty("createdAt")
    private String createdAt;
    @JsonProperty("uuid")
    private String uuid;
    @JsonProperty("isClassifiedIn")
    private Object isClassifiedIn;
    @JsonProperty("product_key")
    private ProductKey productKey;
    @JsonProperty("barcodeScanText")
    private String barcodeScanText;
    @JsonProperty("namePublicLong")
    private List<NamePublicLong> namePublicLong = null;
    @JsonProperty("lastUserHistoryDate")
    private String lastUserHistoryDate;
    @JsonProperty("typePackaging")
    private TypePackaging typePackaging;
    @JsonProperty("brand")
    private Brand brand;
    @JsonProperty("isDisplayUnit")
    private Boolean isDisplayUnit;
    @JsonProperty("isConsumerUnit")
    private Boolean isConsumerUnit;
    @JsonProperty("description")
    private List<Description> description = null;
    @JsonProperty("tags")
    private Tags tags;
    @JsonProperty("manufacturerSharingStatus")
    private Integer manufacturerSharingStatus;
    @JsonProperty("lastRequest")
    private Object lastRequest;
    @JsonProperty("kind")
    private Kind kind;
    @JsonProperty("assets")
    private Assets assets;
    @JsonProperty("specializes")
    private Specializes specializes;
    @JsonProperty("targetProductStatus")
    private Object targetProductStatus;
    @JsonProperty("lifeCycle")
    private Integer lifeCycle;
    @JsonProperty("textileModel")
    private Object textileModel;
    @JsonProperty("isDespatchUnit")
    private Boolean isDespatchUnit;
    @JsonProperty("validation")
    private Validation validation;
    @JsonProperty("isLabeledBy")
    private List<IsLabeledBy> isLabeledBy = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("assetsLastRequests")
    public AssetsLastRequests getAssetsLastRequests() {
        return assetsLastRequests;
    }

    @JsonProperty("assetsLastRequests")
    public void setAssetsLastRequests(AssetsLastRequests assetsLastRequests) {
        this.assetsLastRequests = assetsLastRequests;
    }

    @JsonProperty("expressedIn")
    public List<ExpressedIn> getExpressedIn() {
        return expressedIn != null ? new ArrayList<>(expressedIn) : null;
    }

    @JsonProperty("expressedIn")
    public void setExpressedIn(List<ExpressedIn> expressedIn) {
        this.expressedIn = expressedIn  != null ? new ArrayList<>(expressedIn) : null;
    }

    @JsonProperty("unitType")
    public String getUnitType() {
        return unitType;
    }

    @JsonProperty("unitType")
    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    @JsonProperty("gtin")
    public String getGtin() {
        return gtin;
    }

    @JsonProperty("gtin")
    public void setGtin(String gtin) {
        this.gtin = gtin;
    }

    @JsonProperty("updatedAt")
    public String getUpdatedAt() {
        return updatedAt;
    }

    @JsonProperty("updatedAt")
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @JsonProperty("owner")
    public Owner getOwner() {
        return owner;
    }

    @JsonProperty("owner")
    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }
    
   

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }
    @JsonProperty("supplierId")
    public String getSupplierId() {
        return supplierId;
    }
    @JsonProperty("supplierId")
    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }
    @JsonProperty("createdAt")
    public String getCreatedAt() {
        return createdAt;
    }

    @JsonProperty("createdAt")
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty("uuid")
    public String getUuid() {
        return uuid;
    }

    @JsonProperty("uuid")
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @JsonProperty("isClassifiedIn")
    public Object getIsClassifiedIn() {
        return isClassifiedIn;
    }

    @JsonProperty("isClassifiedIn")
    public void setIsClassifiedIn(Object isClassifiedIn) {
        this.isClassifiedIn = isClassifiedIn;
    }

    @JsonProperty("product_key")
    public ProductKey getProductKey() {
        return productKey;
    }

    @JsonProperty("product_key")
    public void setProductKey(ProductKey productKey) {
        this.productKey = productKey;
    }

    @JsonProperty("barcodeScanText")
    public String getBarcodeScanText() {
        return barcodeScanText;
    }

    @JsonProperty("barcodeScanText")
    public void setBarcodeScanText(String barcodeScanText) {
        this.barcodeScanText = barcodeScanText;
    }

    @JsonProperty("namePublicLong")
    public List<NamePublicLong> getNamePublicLong() {
        return namePublicLong != null ? new ArrayList<>(namePublicLong) : null;
    }

    @JsonProperty("namePublicLong")
    public void setNamePublicLong(List<NamePublicLong> namePublicLong) {
        this.namePublicLong = namePublicLong !=  null ? new ArrayList<>(namePublicLong) : null;
    }

    @JsonProperty("lastUserHistoryDate")
    public String getLastUserHistoryDate() {
        return lastUserHistoryDate;
    }

    @JsonProperty("lastUserHistoryDate")
    public void setLastUserHistoryDate(String lastUserHistoryDate) {
        this.lastUserHistoryDate = lastUserHistoryDate;
    }

    @JsonProperty("typePackaging")
    public TypePackaging getTypePackaging() {
        return typePackaging;
    }

    @JsonProperty("typePackaging")
    public void setTypePackaging(TypePackaging typePackaging) {
        this.typePackaging = typePackaging;
    }

    @JsonProperty("brand")
    public Brand getBrand() {
        return brand;
    }

    @JsonProperty("brand")
    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    @JsonProperty("isDisplayUnit")
    public Boolean getIsDisplayUnit() {
        return isDisplayUnit;
    }

    @JsonProperty("isDisplayUnit")
    public void setIsDisplayUnit(Boolean isDisplayUnit) {
        this.isDisplayUnit = isDisplayUnit;
    }
    @JsonProperty("isConsumerUnit")
    public Boolean getIsConsumerUnit() {
        return isConsumerUnit;
    }

    @JsonProperty("isConsumerUnit")
    public void setIsConsumerUnit(Boolean isConsumerUnit) {
        this.isConsumerUnit = isConsumerUnit;
    }

    @JsonProperty("description")
    public List<Description> getDescription() {
        return description != null ? new ArrayList<>(description) : null;
    }

    @JsonProperty("description")
    public void setDescription(List<Description> description) {
        this.description = description != null ? new ArrayList<>(description) : null;
    }

    @JsonProperty("tags")
    public Tags getTags() {
        return tags;
    }

    @JsonProperty("tags")
    public void setTags(Tags tags) {
        this.tags = tags;
    }

    @JsonProperty("manufacturerSharingStatus")
    public Integer getManufacturerSharingStatus() {
        return manufacturerSharingStatus;
    }

    @JsonProperty("manufacturerSharingStatus")
    public void setManufacturerSharingStatus(Integer manufacturerSharingStatus) {
        this.manufacturerSharingStatus = manufacturerSharingStatus;
    }

    @JsonProperty("lastRequest")
    public Object getLastRequest() {
        return lastRequest;
    }

    @JsonProperty("lastRequest")
    public void setLastRequest(Object lastRequest) {
        this.lastRequest = lastRequest;
    }

    @JsonProperty("kind")
    public Kind getKind() {
        return kind;
    }

    @JsonProperty("kind")
    public void setKind(Kind kind) {
        this.kind = kind;
    }

    @JsonProperty("assets")
    public Assets getAssets() {
        return assets;
    }

    @JsonProperty("assets")
    public void setAssets(Assets assets) {
        this.assets = assets;
    }

    @JsonProperty("specializes")
    public Specializes getSpecializes() {
        return specializes;
    }

    @JsonProperty("specializes")
    public void setSpecializes(Specializes specializes) {
        this.specializes = specializes;
    }

    @JsonProperty("targetProductStatus")
    public Object getTargetProductStatus() {
        return targetProductStatus;
    }

    @JsonProperty("targetProductStatus")
    public void setTargetProductStatus(Object targetProductStatus) {
        this.targetProductStatus = targetProductStatus;
    }

    @JsonProperty("lifeCycle")
    public Integer getLifeCycle() {
        return lifeCycle;
    }

    @JsonProperty("lifeCycle")
    public void setLifeCycle(Integer lifeCycle) {
        this.lifeCycle = lifeCycle;
    }

    @JsonProperty("textileModel")
    public Object getTextileModel() {
        return textileModel;
    }

    @JsonProperty("textileModel")
    public void setTextileModel(Object textileModel) {
        this.textileModel = textileModel;
    }

    @JsonProperty("isDespatchUnit")
    public Boolean getIsDespatchUnit() {
        return isDespatchUnit;
    }

    @JsonProperty("isDespatchUnit")
    public void setIsDespatchUnit(Boolean isDespatchUnit) {
        this.isDespatchUnit = isDespatchUnit;
    }

    @JsonProperty("validation")
    public Validation getValidation() {
        return validation;
    }

    @JsonProperty("validation")
    public void setValidation(Validation validation) {
        this.validation = validation;
    }

    @JsonProperty("isLabeledBy")
    public List<IsLabeledBy> getIsLabeledBy() {
        return isLabeledBy != null ? new ArrayList<>(isLabeledBy) : null;
    }

    @JsonProperty("isLabeledBy")
    public void setIsLabeledBy(List<IsLabeledBy> isLabeledBy) {
        this.isLabeledBy = isLabeledBy != null ? new ArrayList<>(isLabeledBy) : null;
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
