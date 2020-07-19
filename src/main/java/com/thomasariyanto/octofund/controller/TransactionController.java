package com.thomasariyanto.octofund.controller;

import java.io.IOException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.thomasariyanto.octofund.entity.Transaction;
import com.thomasariyanto.octofund.service.TransactionService;
@RestController
@RequestMapping("/transactions")
@CrossOrigin
public class TransactionController {
	
	@Autowired
	TransactionService transactionService;
	
	@GetMapping
	public Iterable<Transaction> getTransactions() {
		return transactionService.getTransactions();
	}
	
	@GetMapping("/{id}")
	public Transaction getTransactionById(@PathVariable int id) {
		return transactionService.getTransactionById(id);
	}
	
	@GetMapping("/member/{type}/{memberId}")
	public Page<Transaction> getTransactionByMemberId(@PathVariable int type, @PathVariable int memberId, @RequestParam(value="page", defaultValue="0") Integer pageNo, @RequestParam(value="size", defaultValue="2") Integer pageSize, @RequestParam(value="sortKey", defaultValue="id") String sortKey, @RequestParam(value="sortType", defaultValue="asc") String sortType) {
		return transactionService.getTransactionByMemberId(type, memberId, pageNo, pageSize, sortKey, sortType);
	}
	
	@GetMapping("/manager/history/{type}/{mutualFundId}")
	public Page<Transaction> getTransactionByManagerId(@PathVariable int type, @PathVariable int mutualFundId, @RequestParam(value="page", defaultValue="0") Integer pageNo, @RequestParam(value="size", defaultValue="2") Integer pageSize, @RequestParam(value="sortKey", defaultValue="id") String sortKey, @RequestParam(value="sortType", defaultValue="asc") String sortType) {
		return transactionService.getTransactionByManagerId(type, mutualFundId, pageNo, pageSize, sortKey, sortType);
	}
	
	@GetMapping("/manager/verify/{managerId}")
	public Page<Transaction> getVerifyTransactionByManagerId(@PathVariable int managerId, Pageable pageable) {
		return transactionService.getVerifyTransactionByManagerId(managerId, pageable);
	}
	
	@PostMapping("/buy")
	public Transaction addBuyTransaction(@Valid @RequestBody Transaction transaction) {
		return transactionService.addBuyTransaction(transaction);
	}
	
	@PostMapping("/buy/package/{packageId}")
	public String addBuyPackageTransaction(@RequestParam("file") MultipartFile file, @RequestParam("transactionData") String transactionString, @PathVariable int packageId) throws JsonMappingException, JsonProcessingException{
		return transactionService.addBuyPackageTransaction(file, transactionString, packageId);
	}
	
	@GetMapping("/buy/payment/{transactionId}")
	public Transaction paymentBuyTransaction(@PathVariable int transactionId) {
		return transactionService.paymentBuyTransaction(transactionId);
	}
	
	@PostMapping("/buy/confirm/{transactionId}")
	public Transaction confirmBuyTransaction(@RequestParam("file") MultipartFile file, @PathVariable int transactionId) {
		return transactionService.confirmBuyTransaction(file, transactionId);
	}
	
	@GetMapping("/paymentproof/{fileName:.+}")
	public ResponseEntity<Object> getProof(@PathVariable String fileName){
		return transactionService.getProof(fileName);
	}
	
	@PostMapping("/buy/reject")
	public String rejectBuyTransaction(@RequestBody Transaction transaction) {
		return transactionService.rejectBuyTransaction(transaction);
	}
	
	@PostMapping("/buy/accept")
	public String acceptBuyTransaction(@RequestBody Transaction transaction) throws IOException {
		return transactionService.acceptBuyTransaction(transaction);
	}
	
	@GetMapping("/invoice/{fileName:.+}")
	public ResponseEntity<Object> downloadFile(@PathVariable String fileName){
		return transactionService.downloadFile(fileName);
	}
	
	@PostMapping("/sell")
	public String addSellTransaction(@Valid @RequestBody Transaction transaction) {
		return transactionService.addSellTransaction(transaction);
	}
}
