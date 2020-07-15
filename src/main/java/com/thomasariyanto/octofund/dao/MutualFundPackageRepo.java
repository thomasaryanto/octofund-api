package com.thomasariyanto.octofund.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.thomasariyanto.octofund.entity.MutualFundPackage;

public interface MutualFundPackageRepo extends JpaRepository<MutualFundPackage, Integer> {
	public Page<MutualFundPackage> findAllByManagerId(int managerId, Pageable pageable);

}
