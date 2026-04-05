package edu.java.eams.service;
import edu.java.eams.domain.Attendance;
import java.util.List;
public interface AttendanceService {
    List<Attendance> getAll();
    Attendance getById(Long id);
    void insert(Attendance attendance);
    void update(Attendance attendance);
    void delete(Long id);
    List<Attendance> getByCondition(String employeeName, Long userId, int start, int size);
    int countByCondition(String employeeName, Long userId);
    boolean checkIn(Long userId, String userName);
    boolean checkOut(Long userId, String userName);
} 