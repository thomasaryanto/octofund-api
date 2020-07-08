package com.thomasariyanto.octofund.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thomasariyanto.octofund.dao.MutualFundRepo;
import com.thomasariyanto.octofund.entity.MutualFund;

@RestController
@RequestMapping("/mutualfund")
@CrossOrigin
public class MutualFundController {
	
	@Autowired
	private MutualFundRepo mutualFundRepo;
	
	@GetMapping
	public Iterable<MutualFund> getMutualFunds() {
		return mutualFundRepo.findAll();
	}
}
