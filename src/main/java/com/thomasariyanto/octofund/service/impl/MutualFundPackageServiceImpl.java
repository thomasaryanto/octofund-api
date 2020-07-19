package com.thomasariyanto.octofund.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.thomasariyanto.octofund.dao.MutualFundPackageRepo;
import com.thomasariyanto.octofund.entity.MutualFundPackage;
import com.thomasariyanto.octofund.service.MutualFundPackageService;

@Service
public class MutualFundPackageServiceImpl implements MutualFundPackageService {
	@Autowired
	private MutualFundPackageRepo mutualFundPackageRepo;
	
	@Override
	public Page<MutualFundPackage> getMutualFundPackage(Pageable pageable) {
		return mutualFundPackageRepo.findAll(pageable);
	}
	
	@Override
	public MutualFundPackage getMutualFundPackageById(int id) {
		return mutualFundPackageRepo.findById(id).get();
	}
	
	@Override
	public Page<MutualFundPackage> getMutualFundPackageByManagerId(int managerId, Pageable pageable) {
		return mutualFundPackageRepo.findAllByManagerId(managerId, pageable);
	}
	
	@Override
	public MutualFundPackage addMutualFundPackage(MutualFundPackage mutualFundPackage) {
		if(mutualFundPackage.getProductOne() == null || mutualFundPackage.getProductTwo() == null || mutualFundPackage.getProductThree() == null) {
			throw new RuntimeException("Ketiga reksadana harus dipilih!");
		}
		if(mutualFundPackage.getPercentageOne() + mutualFundPackage.getPercentageTwo() + mutualFundPackage.getPercentageThree() != 100) {
			throw new RuntimeException("Jumlah persentase ketiga reksadana harus 100%!");
		}
		
		mutualFundPackage.setId(0);
		mutualFundPackage.setDate(new Date());
		return mutualFundPackageRepo.save(mutualFundPackage);
	}
	
	@Override
	public MutualFundPackage editMutualFundPackage(MutualFundPackage mutualFundPackage) {
		
		if(mutualFundPackage.getProductOne() == null || mutualFundPackage.getProductTwo() == null || mutualFundPackage.getProductThree() == null) {
			throw new RuntimeException("Ketiga reksadana harus dipilih!");
		}
		if(mutualFundPackage.getPercentageOne() + mutualFundPackage.getPercentageTwo() + mutualFundPackage.getPercentageThree() != 100) {
			throw new RuntimeException("Jumlah persentase ketiga reksadana harus 100%!");
		}
		
		MutualFundPackage findMutualFundPackage = mutualFundPackageRepo.findById(mutualFundPackage.getId()).get();
		mutualFundPackage.setDate(findMutualFundPackage.getDate());
		return mutualFundPackageRepo.save(mutualFundPackage);
	}
	
	@Override
	public void deleteMutualFundPackage(int id) {
		mutualFundPackageRepo.deleteById(id);
	}
}
