package com.thomasariyanto.octofund.dao;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.thomasariyanto.octofund.entity.User;

public interface UserRepo extends JpaRepository<User, Integer> {
	public Optional<User> findByEmail(String email);
	public Optional<User> findByToken(String token);
	public Page<User> findAllByIsVerifiedAndIsRejectedAndIsKyc(boolean isVerified, boolean isRejected, boolean isKyc, Pageable pageable);
	public boolean existsByEmail(String email);
	public boolean existsByToken(String token);
}
