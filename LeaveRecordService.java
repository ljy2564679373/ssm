package edu.java.eams.service;
import edu.java.eams.domain.LeaveRecord;
import java.util.List;
public interface LeaveRecordService {
    List<LeaveRecord> getAll();
    LeaveRecord getById(Long id);
    void insert(LeaveRecord leaveRecord);
    void update(LeaveRecord leaveRecord);
    void delete(Long id);
    List<LeaveRecord> getByCondition(String employeeName, Long userId, int start, int size);
    int countByCondition(String employeeName, Long userId);
    int updateStatus(Long id,Integer status,Long approveUserId,java.util.Date approveTime,String approveContent);
} 