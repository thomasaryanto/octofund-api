package com.thomasariyanto.octofund.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thomasariyanto.octofund.entity.Transaction;

public interface TransactionRepo extends JpaRepository<Transaction, Integer> {

}
