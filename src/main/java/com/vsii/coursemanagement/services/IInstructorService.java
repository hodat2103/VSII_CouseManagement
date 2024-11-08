package com.vsii.coursemanagement.services;

import com.vsii.coursemanagement.dtos.response.ResponseSuccess;
import com.vsii.coursemanagement.entities.Instructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IInstructorService {
    /**
     * phuong thuc nay de lay ra tat ca cac du lieu cua instructor
     * @return tra ve tat ca du lieu cua instructor
     */
    ResponseSuccess getAllInstructors();
}
