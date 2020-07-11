package com.thomasariyanto.octofund.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thomasariyanto.octofund.entity.Portfolio;

public interface PortfolioRepo extends JpaRepository<Portfolio, Integer>{
	public Optional<Portfolio> findByMemberIdAndMutualFundId(int memberId, int mutualFundId);
}
