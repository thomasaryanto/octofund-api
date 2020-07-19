package com.thomasariyanto.octofund.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thomasariyanto.octofund.entity.Portfolio;
import com.thomasariyanto.octofund.service.PortfolioService;

@RestController
@RequestMapping("/portfolios")
@CrossOrigin
public class PortfolioController {
	
	@Autowired 
	PortfolioService portfolioService;
	
	@GetMapping
	public Iterable<Portfolio> getPortfolios() {
		return portfolioService.getPortfolios();
	}
	
	@GetMapping("/{id}")
	public Portfolio getPortfolioById(@PathVariable int id) {
		return portfolioService.getPortfolioById(id);
	}
	
	@GetMapping("/member/{memberId}")
	public Page<Portfolio> getPortfolioByMemberId(@PathVariable int memberId, Pageable pageable) {
		return portfolioService.getPortfolioByMemberId(memberId, pageable);
	}
	
	@GetMapping("/member/{memberId}/all")
	public List<Portfolio> getAllPortfolioByMemberId(@PathVariable int memberId) {
		return portfolioService.getAllPortfolioByMemberId(memberId);
	}
}
