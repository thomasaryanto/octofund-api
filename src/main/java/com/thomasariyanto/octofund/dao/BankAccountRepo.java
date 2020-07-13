package com.thomasariyanto.octofund.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thomasariyanto.octofund.entity.BankAccount;

public interface BankAccountRepo extends JpaRepository<BankAccount, Integer> {
	public List<BankAccount> findAllByUserId(int userId);
}
