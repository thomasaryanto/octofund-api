package com.thomasariyanto.octofund.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.thomasariyanto.octofund.entity.MutualFund;
import com.thomasariyanto.octofund.entity.User;

public interface MutualFundRepo extends JpaRepository<MutualFund, Integer> {
	public Page<MutualFund> findAllByManagerId(int managerId, Pageable pageable);
}
