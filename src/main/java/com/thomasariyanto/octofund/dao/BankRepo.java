package com.thomasariyanto.octofund.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thomasariyanto.octofund.entity.Bank;

public interface BankRepo extends JpaRepository<Bank, Integer> {

}
