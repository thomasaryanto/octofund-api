package com.thomasariyanto.octofund.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thomasariyanto.octofund.entity.PriceHistory;

public interface PriceHistoryRepo extends JpaRepository<PriceHistory, Integer> {

}
