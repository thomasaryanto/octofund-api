package com.thomasariyanto.octofund.controller;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.thomasariyanto.octofund.entity.Bank;
import com.thomasariyanto.octofund.entity.BankAccount;
import com.thomasariyanto.octofund.service.BankService;

@RestController
@RequestMapping("/banks")
@CrossOrigin
public class BankController {
	
	@Autowired
	private BankService bankService;
	
	@GetMapping
	public Iterable<Bank> getBanks() {
		return bankService.getBanks();
	}
	
	@GetMapping("/paging")
	public Iterable<Bank> getBankPages(Pageable pageable) {
		return bankService.getBankPages(pageable);
	}
	
	@GetMapping("/{id}")
	public Bank getBankById(@PathVariable int id) {
		return bankService.getBankById(id);
	}
	
	@GetMapping("/accounts")
	public Iterable<BankAccount> getBankAccounts() {
		return bankService.getBankAccounts();
	}
	
	@GetMapping("/accounts/{id}")
	public BankAccount getBankAccountById(@PathVariable int id) {
		return bankService.getBankAccountById(id);
	}
	
	@GetMapping("/accounts/user/{userId}")
	public Page<BankAccount> getBankAccountByUserId(@PathVariable int userId, Pageable pageable) {
		return bankService.getBankAccountByUserId(userId, pageable);
	}
	
	@GetMapping("/accounts/user/{userId}/all")
	public Iterable<BankAccount> getAllBankAccountByUserId(@PathVariable int userId) {
		return bankService.getAllBankAccountByUserId(userId);
	}
	
	@PostMapping
	public Bank addBank(@Valid @RequestBody Bank bank) {
		return bankService.addBank(bank);
	}
	
	@PostMapping("/accounts")
	public BankAccount addBankAccount(@Valid @RequestBody BankAccount bankAccount) {
		return bankService.addBankAccount(bankAccount);
	}
	
	@PutMapping
	public Bank editBank(@Valid @RequestBody Bank bank) {
		return bankService.editBank(bank);
	}
	
	@PutMapping("/accounts")
	public BankAccount editBankAccount(@Valid @RequestBody BankAccount bankAccount) {
		return bankService.editBankAccount(bankAccount);
	}
	
	@PostMapping("/upload/")
	public Map<String, String> uploadBankLogo(@RequestParam("file") MultipartFile file) {
		return bankService.uploadBankLogo(file);
	}
	
	@PostMapping("/upload/{id}")
	public Bank editBankLogo(@RequestParam("file") MultipartFile file, @PathVariable int id) {
		return bankService.editBankLogo(file, id);
	}
	
	@GetMapping("/logo/{fileName:.+}")
	public ResponseEntity<Object> downloadFile(@PathVariable String fileName){
		return bankService.downloadFile(fileName);
	}
	
	//delete bank akan menghapus semua bank account yg terkait.
	@DeleteMapping("/{id}")
	public void deleteBank(@PathVariable int id) {
		bankService.deleteBank(id);
	}
	
	@DeleteMapping("/accounts/{id}")
	public void deleteBankAccount(@PathVariable int id) {
		bankService.deleteBankAccount(id);
	}
}
