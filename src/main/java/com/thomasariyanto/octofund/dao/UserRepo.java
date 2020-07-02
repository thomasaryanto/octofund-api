package com.thomasariyanto.octofund.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thomasariyanto.octofund.entity.User;

public interface UserRepo extends JpaRepository<User, Integer> {
	public Optional<User> findByEmail(String email);
	public boolean existsByEmail(String email);
}
