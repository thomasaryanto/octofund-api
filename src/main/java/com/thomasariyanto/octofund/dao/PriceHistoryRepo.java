package com.thomasariyanto.octofund.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thomasariyanto.octofund.entity.PriceHistory;

public interface PriceHistoryRepo extends JpaRepository<PriceHistory, Integer> {
	public PriceHistory findByMutualFundIdAndDate(int productId, Date date);
	public List<PriceHistory> findAllByMutualFundId(int productId);
	public List<PriceHistory> findAllByMutualFundIdAndDateBetween(int productId, Date startDate, Date endDate);
}
