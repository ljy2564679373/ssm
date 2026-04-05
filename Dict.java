package edu.java.eams.domain;

public class Dict {
    private Integer id;
    private String dictType;
    private String dictItemKey;
    private String dictItemValue;
    private Integer sortCode;
    private Integer status;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getDictType() { return dictType; }
    public void setDictType(String dictType) { this.dictType = dictType; }
    public String getDictItemKey() { return dictItemKey; }
    public void setDictItemKey(String dictItemKey) { this.dictItemKey = dictItemKey; }
    public String getDictItemValue() { return dictItemValue; }
    public void setDictItemValue(String dictItemValue) { this.dictItemValue = dictItemValue; }
    public Integer getSortCode() { return sortCode; }
    public void setSortCode(Integer sortCode) { this.sortCode = sortCode; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
} 