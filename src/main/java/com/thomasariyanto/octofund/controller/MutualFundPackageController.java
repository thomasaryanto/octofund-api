package com.thomasariyanto.octofund.controller;


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

import com.thomasariyanto.octofund.entity.MutualFundPackage;
import com.thomasariyanto.octofund.service.MutualFundPackageService;

@RestController
@RequestMapping("/packages")
@CrossOrigin
public class MutualFundPackageController {
	
	@Autowired
	private MutualFundPackageService mutualFundPackageService;

	
	@GetMapping
	public Page<MutualFundPackage> getMutualFundPackage(Pageable pageable) {
		return mutualFundPackageService.getMutualFundPackage(pageable);
	}
	
	@GetMapping("/{id}")
	public MutualFundPackage getMutualFundPackageById(@PathVariable int id) {
		return mutualFundPackageService.getMutualFundPackageById(id);
	}
	
	@GetMapping("/manager/{managerId}")
	public Page<MutualFundPackage> getMutualFundPackageByManagerId(@PathVariable int managerId, Pageable pageable) {
		return mutualFundPackageService.getMutualFundPackageByManagerId(managerId, pageable);
	}
	
	@PostMapping
	public MutualFundPackage addMutualFundPackage(@Valid @RequestBody MutualFundPackage mutualFundPackage) {
		return mutualFundPackageService.addMutualFundPackage(mutualFundPackage);
	}
	
	@PutMapping
	public MutualFundPackage editMutualFundPackage(@Valid @RequestBody MutualFundPackage mutualFundPackage) {
		return mutualFundPackageService.editMutualFundPackage(mutualFundPackage);
	}
	
	@DeleteMapping("/{id}")
	public void deleteMutualFundPackage(@PathVariable int id) {
		mutualFundPackageService.deleteMutualFundPackage(id);
	}
}
