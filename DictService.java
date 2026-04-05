package edu.java.eams.service;

import edu.java.eams.domain.Dict;
import java.util.List;

public interface DictService {
    /**
     * 根据字典类型获取字典项
     */
    List<Dict> getDictByType(String dictType);
    
    /**
     * 获取所有字典项
     */
    List<Dict> getAllDict();
    
    /**
     * 根据字典类型和key获取value
     */
    String getDictValue(String dictType, String key);
} 