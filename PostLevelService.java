package edu.java.eams.service;

import edu.java.eams.domain.PostLevel;
import java.util.List;

public interface PostLevelService {
    List<PostLevel> getAllPostLevels();
    PostLevel getPostLevelById(Long id);
    boolean addPostLevel(PostLevel postLevel);
    boolean updatePostLevel(PostLevel postLevel);
    boolean deletePostLevel(Long id);
    List<PostLevel> getPostLevelsByCondition(String name, int start, int size);
    int countPostLevelsByCondition(String name);
} 