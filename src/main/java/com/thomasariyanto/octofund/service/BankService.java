package com.thomasariyanto.octofund.service;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.thomasariyanto.octofund.entity.Bank;
import com.thomasariyanto.octofund.entity.BankAccount;

public interface BankService {

	public Iterable<Bank> getBanks();
	
	public Iterable<Bank> getBankPages(Pageable pageable);
	
	public Bank getBankById(int id);
	
	public Iterable<BankAccount> getBankAccounts();
	
	public BankAccount getBankAccountById(int id);
	
	public Page<BankAccount> getBankAccountByUserId(int userId, Pageable pageable);
	
	public Iterable<BankAccount> getAllBankAccountByUserId(int userId);
	
	public Bank addBank(Bank bank);
	
	public BankAccount addBankAccount(BankAccount bankAccount);
	
	public Bank editBank(Bank bank);
	
	public BankAccount editBankAccount(BankAccount bankAccount);
	
	public Map<String, String> uploadBankLogo(MultipartFile file);
	
	public Bank editBankLogo(MultipartFile file, int id);
	
	public ResponseEntity<Object> downloadFile(String fileName);
	
	public void deleteBank(int id);
	
	public void deleteBankAccount(int id);
}
