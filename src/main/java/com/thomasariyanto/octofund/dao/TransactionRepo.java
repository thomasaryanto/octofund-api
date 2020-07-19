package com.thomasariyanto.octofund.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.thomasariyanto.octofund.entity.Transaction;

public interface TransactionRepo extends JpaRepository<Transaction, Integer> {
	public Page<Transaction> findAllByTypeAndMemberId(int type, int memberId, Pageable pageable);
	public Page<Transaction> findAllByTypeAndMutualFundId(int type, int mutualFundId, Pageable pageable);
	public Transaction findByTypeAndId(int type, int transactionId);
	public List<Transaction> findAllByMutualFundIdAndTransactionStatusId(int mutualFundId, int transactionStatusId);
	public List<Transaction> findAllByMemberIdAndTransactionStatusId(int memberId, int transactionStatusId);
	public Page<Transaction> findAllByTransactionStatusIdAndMutualFundManagerId(int statusId, int managerId, Pageable pageable);
}
