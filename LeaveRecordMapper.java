package edu.java.eams.mapper;
import edu.java.eams.domain.LeaveRecord;
import java.util.List;
public interface LeaveRecordMapper {
    List<LeaveRecord> getAll();
    LeaveRecord getById(Long id);
    void insert(LeaveRecord leaveRecord);
    void update(LeaveRecord leaveRecord);
    void delete(Long id);
    List<LeaveRecord> getByCondition(@org.apache.ibatis.annotations.Param("employeeName") String employeeName, @org.apache.ibatis.annotations.Param("userId") Long userId, @org.apache.ibatis.annotations.Param("start") int start, @org.apache.ibatis.annotations.Param("size") int size);
    int countByCondition(@org.apache.ibatis.annotations.Param("employeeName") String employeeName, @org.apache.ibatis.annotations.Param("userId") Long userId);
    int updateStatus(@org.apache.ibatis.annotations.Param("id") Long id,
                     @org.apache.ibatis.annotations.Param("status") Integer status,
                     @org.apache.ibatis.annotations.Param("approveUserId") Long approveUserId,
                     @org.apache.ibatis.annotations.Param("approveTime") java.util.Date approveTime,
                     @org.apache.ibatis.annotations.Param("approveContent") String approveContent);
    int deleteByUserId(@org.apache.ibatis.annotations.Param("userId") Long userId);
    int clearApproveUser(@org.apache.ibatis.annotations.Param("userId") Long userId);
} 