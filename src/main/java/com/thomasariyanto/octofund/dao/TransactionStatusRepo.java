package com.thomasariyanto.octofund.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thomasariyanto.octofund.entity.TransactionStatus;

public interface TransactionStatusRepo extends JpaRepository<TransactionStatus, Integer> {

}
