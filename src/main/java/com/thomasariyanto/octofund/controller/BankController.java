package com.thomasariyanto.octofund.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thomasariyanto.octofund.dao.BankRepo;
import com.thomasariyanto.octofund.entity.Bank;

@RestController
@RequestMapping("/banks")
@CrossOrigin
public class BankController {
	
	@Autowired
	private BankRepo bankRepo;
	
	@GetMapping
	public Iterable<Bank> getBanks() {
		return bankRepo.findAll();
	}
	
	@GetMapping("/{id}")
	public Bank getBankById(@PathVariable int id) {
		return bankRepo.findById(id).get();
	}
	
	@PostMapping
	public Bank addBank(@Valid @RequestBody Bank bank) {
		bank.setId(0);
		return bankRepo.save(bank);
	}
	
	@PutMapping
	public Bank editBank(@Valid @RequestBody Bank bank) {
		return bankRepo.save(bank);
	}
	
	@DeleteMapping("/{id}")
	public void deleteBank(@PathVariable int id) {
		bankRepo.deleteById(id);
	}
}
