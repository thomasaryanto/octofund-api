package com.thomasariyanto.octofund.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thomasariyanto.octofund.dao.BankAccountRepo;
import com.thomasariyanto.octofund.dao.BankRepo;
import com.thomasariyanto.octofund.dao.UserRepo;
import com.thomasariyanto.octofund.entity.Bank;
import com.thomasariyanto.octofund.entity.BankAccount;
import com.thomasariyanto.octofund.entity.User;

@RestController
@RequestMapping("/banks")
@CrossOrigin
public class BankController {
	
	@Autowired
	private BankRepo bankRepo;
	
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private BankAccountRepo bankAccountRepo;
	
	@GetMapping
	public Iterable<Bank> getBanks() {
		return bankRepo.findAll();
	}
	
	@GetMapping("/accounts")
	public Iterable<BankAccount> getBankAccounts() {
		return bankAccountRepo.findAll();
	}
	
	@GetMapping("/{id}")
	public Bank getBankById(@PathVariable int id) {
		return bankRepo.findById(id).get();
	}
	
	@GetMapping("/accounts/{id}")
	public BankAccount getBankAccountById(@PathVariable int id) {
		return bankAccountRepo.findById(id).get();
	}
	
	@GetMapping("/accounts/user/{userId}")
	public Page<BankAccount> getBankAccountByUserId(@PathVariable int userId, Pageable pageable) {
		return bankAccountRepo.findAllByUserId(userId, pageable);
	}
	
	@GetMapping("/accounts/user/{userId}/all")
	public Iterable<BankAccount> getAllBankAccountByUserId(@PathVariable int userId) {
		return bankAccountRepo.findAllByUserId(userId);
	}
	
	@PostMapping
	public Bank addBank(@Valid @RequestBody Bank bank) {
		bank.setId(0);
		return bankRepo.save(bank);
	}
	
	@PostMapping("/accounts")
	public BankAccount addBankAccount(@Valid @RequestBody BankAccount bankAccount) {
		Bank findBank = bankRepo.findById(bankAccount.getBank().getId()).get();
		User findUser = userRepo.findById(bankAccount.getUser().getId()).get();
		bankAccount.setId(0);
		bankAccount.setBank(findBank);
		bankAccount.setUser(findUser);
		return bankAccountRepo.save(bankAccount);
	}
	
	@PutMapping
	public Bank editBank(@Valid @RequestBody Bank bank) {
		return bankRepo.save(bank);
	}
	
	@PutMapping("/accounts")
	public BankAccount editBankAccount(@Valid @RequestBody BankAccount bankAccount) {
		BankAccount findBankAccount = bankAccountRepo.findById(bankAccount.getId()).get();
		User findUser = userRepo.findById(findBankAccount.getUser().getId()).get();
		Bank findBank = bankRepo.findById(bankAccount.getBank().getId()).get();	
		bankAccount.setUser(findUser);
		bankAccount.setBank(findBank);
		return bankAccountRepo.save(bankAccount);
	}
	
	//delete bank akan menghapus semua bank account yg terkait.
	@DeleteMapping("/{id}")
	public void deleteBank(@PathVariable int id) {
		bankRepo.deleteById(id);
	}
	
	@DeleteMapping("/accounts/{id}")
	public void deleteBankAccount(@PathVariable int id) {
		bankAccountRepo.deleteById(id);
	}
}
