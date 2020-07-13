package com.thomasariyanto.octofund.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thomasariyanto.octofund.dao.PortfolioRepo;
import com.thomasariyanto.octofund.entity.Portfolio;
import com.thomasariyanto.octofund.entity.Transaction;

@RestController
@RequestMapping("/portfolios")
@CrossOrigin
public class PorfolioController {
	@Autowired 
	PortfolioRepo portfolioRepo;
	
	@GetMapping
	public Iterable<Portfolio> getPortfolios() {
		return portfolioRepo.findAll();
	}
	
	@GetMapping("/{id}")
	public Portfolio getPortfolioById(@PathVariable int id) {
		return portfolioRepo.findById(id).get();
	}
	
	@GetMapping("/member/{memberId}")
	public Iterable<Portfolio> getPortfolioByMemberId(@PathVariable int memberId) {
		return portfolioRepo.findAllByMemberId(memberId);
	}
}
