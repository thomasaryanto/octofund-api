package com.thomasariyanto.octofund.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.thomasariyanto.octofund.entity.MutualFund;

public interface MutualFundRepo extends JpaRepository<MutualFund, Integer> {
	public List<MutualFund> findByManagerId(int managerId);
	public Page<MutualFund> findAllByManagerId(int managerId, Pageable pageable);
}
