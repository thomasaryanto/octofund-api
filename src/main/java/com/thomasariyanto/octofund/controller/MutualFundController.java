package com.thomasariyanto.octofund.controller;

import java.util.Calendar;
import java.util.Date;

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

import com.thomasariyanto.octofund.dao.ManagerRepo;
import com.thomasariyanto.octofund.dao.MutualFundRepo;
import com.thomasariyanto.octofund.dao.PriceHistoryRepo;
import com.thomasariyanto.octofund.entity.Bank;
import com.thomasariyanto.octofund.entity.Manager;
import com.thomasariyanto.octofund.entity.MutualFund;
import com.thomasariyanto.octofund.entity.PriceHistory;
import com.thomasariyanto.octofund.entity.User;

@RestController
@RequestMapping("/mutualfund")
@CrossOrigin
public class MutualFundController {
	
	@Autowired
	private MutualFundRepo mutualFundRepo;
	
	@Autowired
	private PriceHistoryRepo priceHistoryRepo;
	
	@Autowired
	private ManagerRepo managerRepo;
	
	@GetMapping
	public Iterable<MutualFund> getMutualFunds() {
		return mutualFundRepo.findAll();
	}
	
	@GetMapping("/{id}")
	public MutualFund getMutualFundById(@PathVariable int id) {
		return mutualFundRepo.findById(id).get();
	}
	
	@GetMapping("/manager/{managerId}")
	public Page<MutualFund> getMutualFundByManagerId(@PathVariable int managerId, Pageable pageable) {
		return mutualFundRepo.findAllByManagerId(managerId, pageable);
	}
	
	@PostMapping
	public MutualFund addMutualFund(@Valid @RequestBody MutualFund mutualFund) {
		Manager findManager = managerRepo.findById(mutualFund.getManager().getId()).get();
		mutualFund.setId(0);
		mutualFund.setManager(findManager);
		mutualFundRepo.save(mutualFund);
		
		Date date = new Date();
		Calendar dateYasterday = Calendar.getInstance();
		dateYasterday.setTime(date);
		dateYasterday.add(Calendar.DAY_OF_YEAR, -1);
		
		PriceHistory todayPrice = new PriceHistory();
		PriceHistory yasterdayPrice = new PriceHistory();
		todayPrice.setMutualFund(mutualFund);
		yasterdayPrice.setMutualFund(mutualFund);
		todayPrice.setPrice(mutualFund.getLastPrice());
		yasterdayPrice.setPrice(mutualFund.getLastPrice());
		todayPrice.setDate(date);
		yasterdayPrice.setDate(dateYasterday.getTime());
		
		priceHistoryRepo.save(todayPrice);
		priceHistoryRepo.save(yasterdayPrice);
		return mutualFundRepo.save(mutualFund);
	}
	
	@PutMapping
	public MutualFund editMutualFund(@RequestBody MutualFund mutualFund) {
		MutualFund findMutualFund = mutualFundRepo.findById(mutualFund.getId()).get();
		Manager findManager = managerRepo.findById(findMutualFund.getManager().getId()).get();
		mutualFund.setManager(findManager);
		return mutualFundRepo.save(mutualFund);
	}
	
	@DeleteMapping("/{id}")
	public void deleteMutualFund(@PathVariable int id) {
		mutualFundRepo.deleteById(id);
	}
}
