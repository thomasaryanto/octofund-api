package com.thomasariyanto.octofund.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.thomasariyanto.octofund.entity.MutualFund;

public interface MutualFundRepo extends JpaRepository<MutualFund, Integer> {
	public Page<MutualFund> findAllByNameContaining(String name, Pageable pageable);
	public Page<MutualFund> findAllByNameContainingAndMutualFundCategoryId(String name, int categoryId, Pageable pageable);
	public Page<MutualFund> findAllByNameContainingAndLastPriceBetween(String name, double minPrice, double maxPrice, Pageable pageable);
	public List<MutualFund> findByManagerId(int managerId);
	public Page<MutualFund> findAllByManagerId(int managerId, Pageable pageable);
}
