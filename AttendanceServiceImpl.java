package edu.java.eams.service.impl;

import edu.java.eams.domain.Attendance;
import edu.java.eams.mapper.AttendanceMapper;
import edu.java.eams.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceMapper attendanceMapper;

    @Override
    public List<Attendance> getAll() {
        return attendanceMapper.getAll();
    }

    @Override
    public Attendance getById(Long id) {
        return attendanceMapper.getById(id);
    }

    @Override
    public void insert(Attendance attendance) {
        attendanceMapper.insert(attendance);
    }

    @Override
    public void update(Attendance attendance) {
        attendanceMapper.update(attendance);
    }

    @Override
    public void delete(Long id) {
        attendanceMapper.delete(id);
    }

    @Override
    public List<Attendance> getByCondition(String employeeName, Long userId, int start, int size) {
        return attendanceMapper.getByCondition(employeeName, userId, start, size);
    }

    @Override
    public int countByCondition(String employeeName, Long userId) {
        return attendanceMapper.countByCondition(employeeName, userId);
    }

    @Override
    public boolean checkIn(Long userId, String userName) {
        Date today = new Date(System.currentTimeMillis());
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Attendance record = attendanceMapper.getByUserAndDate(userId, today);

        if (record == null) {
            attendanceMapper.insertCheckInRecord(userId, userName, today, now, 0);
            return true;
        }

        if (record.getCheckInTime() != null) {
            return false;
        }

        attendanceMapper.updateCheckInTime(record.getId(), now);
        return true;
    }

    @Override
    public boolean checkOut(Long userId, String userName) {
        Date today = new Date(System.currentTimeMillis());
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Attendance record = attendanceMapper.getByUserAndDate(userId, today);

        if (record == null) {
            attendanceMapper.insertCheckOutRecord(userId, userName, today, now, 0);
            return true;
        }

        if (record.getCheckOutTime() != null) {
            return false;
        }

        attendanceMapper.updateCheckOutTime(record.getId(), now);
        return true;
    }
}
