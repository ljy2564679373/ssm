package edu.java.eams.mapper;

import edu.java.eams.domain.PostLevel;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PostLevelMapper {
    List<PostLevel> findAll();
    PostLevel findById(Long id);
    int insert(PostLevel postLevel);
    int update(PostLevel postLevel);
    int delete(Long id);
    List<PostLevel> findByCondition(@Param("name") String name, @Param("start") int start, @Param("size") int size);
    int countByCondition(@Param("name") String name);
} 