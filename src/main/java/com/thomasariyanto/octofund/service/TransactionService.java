package com.thomasariyanto.octofund.service;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.thomasariyanto.octofund.entity.Transaction;


public interface TransactionService {
	
	public Iterable<Transaction> getTransactions();
	
	public Transaction getTransactionById(int id);
	
	public Page<Transaction> getTransactionByMemberId(int type, int memberId, int pageNo, int pageSize, String sortKey, String sortType);
	
	public Page<Transaction> getTransactionByManagerId(int type, int mutualFundId, int pageNo, int pageSize, String sortKey, String sortType);

	public Page<Transaction> getVerifyTransactionByManagerId(int managerId, Pageable pageable);
	
	public Transaction addBuyTransaction(Transaction transaction);
	
	public String addBuyPackageTransaction(MultipartFile file, String transactionString, int packageId) throws JsonMappingException, JsonProcessingException;
	
	public Transaction paymentBuyTransaction( int transactionId);
	
	public Transaction confirmBuyTransaction(MultipartFile file, int transactionId);
	
	public ResponseEntity<Object> getProof(String fileName);
	
	public String rejectBuyTransaction(Transaction transaction);
	
	public String acceptBuyTransaction(Transaction transaction) throws IOException;
	
	public ResponseEntity<Object> downloadFile(String fileName);
	
	public String addSellTransaction(Transaction transaction);
	
}
