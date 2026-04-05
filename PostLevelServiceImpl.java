package edu.java.eams.service;

import edu.java.eams.domain.PostLevel;
import edu.java.eams.mapper.PostLevelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PostLevelServiceImpl implements PostLevelService {
    @Autowired
    private PostLevelMapper postLevelMapper;

    @Override
    public List<PostLevel> getAllPostLevels() {
        return postLevelMapper.findAll();
    }

    @Override
    public PostLevel getPostLevelById(Long id) {
        return postLevelMapper.findById(id);
    }

    @Override
    public boolean addPostLevel(PostLevel postLevel) {
        return postLevelMapper.insert(postLevel) > 0;
    }

    @Override
    public boolean updatePostLevel(PostLevel postLevel) {
        return postLevelMapper.update(postLevel) > 0;
    }

    @Override
    public boolean deletePostLevel(Long id) {
        return postLevelMapper.delete(id) > 0;
    }

    @Override
    public List<PostLevel> getPostLevelsByCondition(String name, int start, int size) {
        return postLevelMapper.findByCondition(name, start, size);
    }

    @Override
    public int countPostLevelsByCondition(String name) {
        return postLevelMapper.countByCondition(name);
    }
} 