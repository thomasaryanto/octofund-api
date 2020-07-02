package com.thomasariyanto.octofund.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thomasariyanto.octofund.entity.Role;

public interface RoleRepo extends JpaRepository<Role, Integer> {

}
