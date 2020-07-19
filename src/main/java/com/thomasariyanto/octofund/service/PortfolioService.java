package com.thomasariyanto.octofund.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.thomasariyanto.octofund.entity.Portfolio;

public interface PortfolioService {

	public Iterable<Portfolio> getPortfolios();
	
	public Portfolio getPortfolioById(int id);
	
	public Page<Portfolio> getPortfolioByMemberId(int memberId, Pageable pageable);
	
	public List<Portfolio> getAllPortfolioByMemberId(int memberId);
}
