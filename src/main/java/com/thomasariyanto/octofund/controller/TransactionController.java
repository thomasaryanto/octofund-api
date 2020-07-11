package com.thomasariyanto.octofund.controller;

import java.util.Date;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thomasariyanto.octofund.dao.MemberRepo;
import com.thomasariyanto.octofund.dao.MutualFundRepo;
import com.thomasariyanto.octofund.dao.PortfolioRepo;
import com.thomasariyanto.octofund.dao.TransactionRepo;
import com.thomasariyanto.octofund.dao.TransactionStatusRepo;
import com.thomasariyanto.octofund.entity.Member;
import com.thomasariyanto.octofund.entity.MutualFund;
import com.thomasariyanto.octofund.entity.Portfolio;
import com.thomasariyanto.octofund.entity.Transaction;

@RestController
@RequestMapping("/transaction")
@CrossOrigin
public class TransactionController {
	
	@Autowired
	TransactionRepo transactionRepo;
	
	@Autowired
	TransactionStatusRepo transactionStatusRepo;
	
	@Autowired
	MemberRepo memberRepo;
	
	@Autowired 
	PortfolioRepo portfolioRepo;
	
	@Autowired
	MutualFundRepo mutualFundRepo;
	
	@GetMapping
	public Iterable<Transaction> getTransactions() {
		return transactionRepo.findAll();
	}
	
	@PostMapping("/buy")
	public Transaction addBuyTransaction(@Valid @RequestBody Transaction transaction) {
		Member findMember = memberRepo.findById(transaction.getMember().getId()).get();
		MutualFund findMutualFund = mutualFundRepo.findById(transaction.getMutualFund().getId()).get();
		Optional<Portfolio> checkPortfolio = portfolioRepo.findByMemberIdAndMutualFundId(transaction.getMember().getId(), transaction.getMutualFund().getId());
		
		transaction.setId(0);
		transaction.setType(1);
		transaction.setMember(findMember);
		transaction.setDate(new Date());
		
		if(checkPortfolio.toString() == "Optional.empty") {
			Portfolio portfolio = new Portfolio();
			portfolio.setMember(findMember);
			portfolio.setMutualFund(findMutualFund);
			portfolio.setTotalInvest(transaction.getTotalPrice());
			portfolio.setTotalUnit(transaction.getTotalPrice() / findMutualFund.getLastPrice());
			portfolioRepo.save(portfolio);
		}
		else {
			Portfolio portfolio = checkPortfolio.get();
			portfolio.setTotalInvest(portfolio.getTotalInvest() + transaction.getTotalPrice());
			portfolio.setTotalUnit(portfolio.getTotalUnit() + (transaction.getTotalPrice() / findMutualFund.getLastPrice()));
			portfolioRepo.save(portfolio);
		}

		return transactionRepo.save(transaction);
	}
	
	@PostMapping("/sell")
	public Transaction addTransaction(@Valid @RequestBody Transaction transaction) {
		Member findMember = memberRepo.findById(transaction.getMember().getId()).get();
		MutualFund findMutualFund = mutualFundRepo.findById(transaction.getMutualFund().getId()).get();
		Portfolio findPortfolio = portfolioRepo.findByMemberIdAndMutualFundId(transaction.getMember().getId(), transaction.getMutualFund().getId()).get();
		
		if(transaction.getTotalUnit() > findPortfolio.getTotalUnit()) {
			throw new RuntimeException("Jumlah penjualan tidak mencukupi!");
		}
		else if(transaction.getTotalUnit() == findPortfolio.getTotalUnit()) {
			portfolioRepo.delete(findPortfolio);
		}
		else {
			if (findPortfolio.getTotalInvest() - (transaction.getTotalUnit() * findMutualFund.getLastPrice()) < 0) {
				findPortfolio.setTotalInvest(0);
			}
			else {
				findPortfolio.setTotalInvest(findPortfolio.getTotalInvest() - (long)(transaction.getTotalUnit() * findMutualFund.getLastPrice()));
			}
			findPortfolio.setTotalUnit(findPortfolio.getTotalUnit() - transaction.getTotalUnit());
		}
		
		transaction.setId(0);
		transaction.setType(2);
		transaction.setMember(findMember);
		transaction.setDate(new Date());
		return transactionRepo.save(transaction);
	}
}
