package com.thomasariyanto.octofund.controller;

import java.util.Date;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thomasariyanto.octofund.dao.MutualFundPackageRepo;
import com.thomasariyanto.octofund.entity.MutualFundPackage;

@RestController
@RequestMapping("/packages")
@CrossOrigin
public class MutualFundPackageController {

	@Autowired
	private MutualFundPackageRepo mutualFundPackageRepo;
	
	@GetMapping
	public Page<MutualFundPackage> getMutualFundPackage(Pageable pageable) {
		return mutualFundPackageRepo.findAll(pageable);
	}
	
	@GetMapping("/{id}")
	public MutualFundPackage getMutualFundPackageById(@PathVariable int id) {
		return mutualFundPackageRepo.findById(id).get();
	}
	
	@GetMapping("/manager/{managerId}")
	public Page<MutualFundPackage> getMutualFundPackageByManagerId(@PathVariable int managerId, Pageable pageable) {
		return mutualFundPackageRepo.findAllByManagerId(managerId, pageable);
	}
	
	@PostMapping
	public MutualFundPackage addMutualFundPackage(@Valid @RequestBody MutualFundPackage mutualFundPackage) {
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
	
	@PutMapping
	public MutualFundPackage editMutualFundPackage(@Valid @RequestBody MutualFundPackage mutualFundPackage) {
		
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
	
	@DeleteMapping("/{id}")
	public void deleteMutualFundPackage(@PathVariable int id) {
		mutualFundPackageRepo.deleteById(id);
	}
}
