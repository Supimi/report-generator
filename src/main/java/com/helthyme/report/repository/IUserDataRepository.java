package com.helthyme.report.repository;

import com.helthyme.report.model.UserData;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IUserDataRepository {
    List<UserData> findAll();
}
