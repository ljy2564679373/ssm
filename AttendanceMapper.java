package edu.java.eams.mapper;
import edu.java.eams.domain.Attendance;
import java.util.List;
public interface AttendanceMapper {
    List<Attendance> getAll();
    Attendance getById(Long id);
    void insert(Attendance attendance);
    void update(Attendance attendance);
    void delete(Long id);
    List<Attendance> getByCondition(@org.apache.ibatis.annotations.Param("employeeName") String employeeName,
                                   @org.apache.ibatis.annotations.Param("userId") Long userId,
                                   @org.apache.ibatis.annotations.Param("start") int start,
                                   @org.apache.ibatis.annotations.Param("size") int size);
    int countByCondition(@org.apache.ibatis.annotations.Param("employeeName") String employeeName,
                         @org.apache.ibatis.annotations.Param("userId") Long userId);
    Attendance getByUserAndDate(@org.apache.ibatis.annotations.Param("userId") Long userId,
                               @org.apache.ibatis.annotations.Param("attendanceDate") java.sql.Date attendanceDate);
    void insertCheckInRecord(@org.apache.ibatis.annotations.Param("userId") Long userId,
                             @org.apache.ibatis.annotations.Param("userName") String userName,
                             @org.apache.ibatis.annotations.Param("attendanceDate") java.sql.Date attendanceDate,
                             @org.apache.ibatis.annotations.Param("checkInTime") java.sql.Timestamp checkInTime,
                             @org.apache.ibatis.annotations.Param("status") Integer status);
    void insertCheckOutRecord(@org.apache.ibatis.annotations.Param("userId") Long userId,
                              @org.apache.ibatis.annotations.Param("userName") String userName,
                              @org.apache.ibatis.annotations.Param("attendanceDate") java.sql.Date attendanceDate,
                              @org.apache.ibatis.annotations.Param("checkOutTime") java.sql.Timestamp checkOutTime,
                              @org.apache.ibatis.annotations.Param("status") Integer status);
    void updateCheckInTime(@org.apache.ibatis.annotations.Param("id") Long id,
                           @org.apache.ibatis.annotations.Param("checkInTime") java.sql.Timestamp checkInTime);
    void updateCheckOutTime(@org.apache.ibatis.annotations.Param("id") Long id,
                            @org.apache.ibatis.annotations.Param("checkOutTime") java.sql.Timestamp checkOutTime);
    int deleteByUserId(@org.apache.ibatis.annotations.Param("userId") Long userId);
} 
