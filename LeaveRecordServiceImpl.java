package edu.java.eams.service.impl;
import edu.java.eams.domain.LeaveRecord;
import edu.java.eams.mapper.LeaveRecordMapper;
import edu.java.eams.service.LeaveRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class LeaveRecordServiceImpl implements LeaveRecordService {
    @Autowired
    private LeaveRecordMapper leaveRecordMapper;
    public List<LeaveRecord> getAll() { return leaveRecordMapper.getAll(); }
    public LeaveRecord getById(Long id) { return leaveRecordMapper.getById(id); }
    public void insert(LeaveRecord leaveRecord) { leaveRecordMapper.insert(leaveRecord); }
    public void update(LeaveRecord leaveRecord) { leaveRecordMapper.update(leaveRecord); }
    public void delete(Long id) { leaveRecordMapper.delete(id); }
    @Override
    public List<LeaveRecord> getByCondition(String employeeName, Long userId, int start, int size) {
        return leaveRecordMapper.getByCondition(employeeName,userId,start,size);
    }
    @Override
    public int countByCondition(String employeeName, Long userId) {
        return leaveRecordMapper.countByCondition(employeeName,userId);
    }
    public int updateStatus(Long id,Integer status,Long approveUserId,java.util.Date approveTime,String approveContent){
        return leaveRecordMapper.updateStatus(id,status,approveUserId,approveTime,approveContent);
    }
} 