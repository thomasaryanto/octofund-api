package com.thomasariyanto.octofund.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.thomasariyanto.octofund.entity.BankAccount;

public interface BankAccountRepo extends JpaRepository<BankAccount, Integer> {
	public Page<BankAccount> findAllByUserId(int userId, Pageable pageable);
	public List<BankAccount> findAllByUserId(int userId);
}
