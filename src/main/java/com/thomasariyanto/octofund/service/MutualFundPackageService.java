package com.thomasariyanto.octofund.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.thomasariyanto.octofund.entity.MutualFundPackage;

public interface MutualFundPackageService {

	public Page<MutualFundPackage> getMutualFundPackage(Pageable pageable);
	
	public MutualFundPackage getMutualFundPackageById(int id);
	
	public Page<MutualFundPackage> getMutualFundPackageByManagerId(int managerId, Pageable pageable);
	
	public MutualFundPackage addMutualFundPackage(MutualFundPackage mutualFundPackage);
	
	public MutualFundPackage editMutualFundPackage(MutualFundPackage mutualFundPackage);
	
	public void deleteMutualFundPackage(int id);
}
