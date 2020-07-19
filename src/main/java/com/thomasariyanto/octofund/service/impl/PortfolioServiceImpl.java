package com.thomasariyanto.octofund.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.thomasariyanto.octofund.dao.PortfolioRepo;
import com.thomasariyanto.octofund.entity.Portfolio;
import com.thomasariyanto.octofund.service.PortfolioService;

@Service
public class PortfolioServiceImpl implements PortfolioService {
	@Autowired 
	PortfolioRepo portfolioRepo;
	
	@Override
	public Iterable<Portfolio> getPortfolios() {
		return portfolioRepo.findAll();
	}
	
	@Override
	public Portfolio getPortfolioById(int id) {
		return portfolioRepo.findById(id).get();
	}
	
	@Override
	public Page<Portfolio> getPortfolioByMemberId(int memberId, Pageable pageable) {
		return portfolioRepo.findAllByMemberId(memberId, pageable);
	}
	
	@Override
	public List<Portfolio> getAllPortfolioByMemberId(int memberId) {
		return portfolioRepo.findAllByMemberId(memberId);
	}
}
