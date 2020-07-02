package com.thomasariyanto.octofund.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thomasariyanto.octofund.entity.Manager;

public interface ManagerRepo extends JpaRepository<Manager, Integer> {

}
