package com.thomasariyanto.octofund.controller;


import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.thomasariyanto.octofund.entity.MutualFund;
import com.thomasariyanto.octofund.entity.MutualFundCategory;
import com.thomasariyanto.octofund.entity.MutualFundType;
import com.thomasariyanto.octofund.projection.TransactionStatistic;
import com.thomasariyanto.octofund.service.MutualFundService;

@RestController
@RequestMapping("/mutualfund")
@CrossOrigin
public class MutualFundController {
	
	@Autowired
	private MutualFundService mutualFundService;
	
	@GetMapping
	public Page<MutualFund> getMutualFunds(@RequestParam(value="page", defaultValue="0") int pageNo, 
			@RequestParam(value="size", defaultValue="2") int pageSize, 
			@RequestParam(value="sortKey", defaultValue="id") String sortKey, 
			@RequestParam(value="sortType", defaultValue="asc") String sortType,
			@RequestParam(value="filterKey", defaultValue="none") String filterKey,
			@RequestParam(value="filterValue", defaultValue="all") String filterValue,
			@RequestParam(value="keyword", defaultValue="") String keyword) {
		
		return mutualFundService.getMutualFunds(pageNo, pageSize, sortKey, sortType, filterKey, filterValue, keyword);
		
	}
	
	@GetMapping("/{id}")
	public MutualFund getMutualFundById(@PathVariable int id) {
		return mutualFundService.getMutualFundById(id);
	}
	
	@GetMapping("/count/{id}")
	public long getTransactionCount(@PathVariable int id) {
		return mutualFundService.getTransactionCount(id);
	}
	
	@GetMapping("/statistics")
	public List<TransactionStatistic> getMutualFundStatistics(@RequestParam(value="type", defaultValue="1") Integer type) {
		return mutualFundService.getMutualFundStatistics(type);
	}
	
	@GetMapping("/statistics/manager/{managerId}")
	public List<TransactionStatistic> getMutualFundStatisticsByManager(@PathVariable int managerId, @RequestParam(value="type", defaultValue="1") Integer type) {
		return mutualFundService.getMutualFundStatisticsByManager(type, managerId);
	}
	
	@GetMapping("/manager/{managerId}")
	public Page<MutualFund> getMutualFundByManagerId(@PathVariable int managerId, Pageable pageable) {
		return mutualFundService.getMutualFundByManagerId(managerId, pageable);
	}
	
	@GetMapping("/manager/{managerId}/all")
	public List<MutualFund> getAllMutualFundByManagerId(@PathVariable int managerId) {
		return mutualFundService.getAllMutualFundByManagerId(managerId);
	}
	
	@GetMapping("/category")
	public Iterable<MutualFundCategory> getMutualFundCategory() {
		return mutualFundService.getMutualFundCategory();
	}
	
	@GetMapping("/type")
	public Iterable<MutualFundType> getMutualFundType() {
		return mutualFundService.getMutualFundType();
	}
	
	@PostMapping
	public MutualFund addMutualFund(@Valid @RequestBody MutualFund mutualFund) {
		return mutualFundService.addMutualFund(mutualFund);
	}
	
	@PostMapping("/upload/")
	public Map<String, String> uploadDocument(@RequestParam("file") MultipartFile file) {
		return mutualFundService.uploadDocument(file);
	}
	
	@PostMapping("/upload/{id}/{type}")
	public MutualFund editDocument(@RequestParam("file") MultipartFile file, @PathVariable int id, @PathVariable String type) {
		return mutualFundService.editDocument(file, id, type);
	}
	
	@GetMapping("/document/{type}/{fileName:.+}")
	public ResponseEntity<Object> downloadFile(@PathVariable String type, @PathVariable String fileName){
		return mutualFundService.downloadFile(type, fileName);
	}
	
	@PutMapping
	public String editMutualFund(@RequestBody MutualFund mutualFund) {
		return mutualFundService.editMutualFund(mutualFund);
	}
	
	@DeleteMapping("/{id}")
	public void deleteMutualFund(@PathVariable int id) {
		mutualFundService.deleteMutualFund(id);
	}
}
