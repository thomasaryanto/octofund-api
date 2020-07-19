package com.thomasariyanto.octofund.service;

import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import com.thomasariyanto.octofund.entity.MutualFund;
import com.thomasariyanto.octofund.entity.MutualFundCategory;
import com.thomasariyanto.octofund.entity.MutualFundType;
import com.thomasariyanto.octofund.projection.TransactionStatistic;

public interface MutualFundService {

	public Page<MutualFund> getMutualFunds(int pageNo, int pageSize,String sortKey, String sortType, String filterKey, String filterValue, String keyword);
	
	public MutualFund getMutualFundById(int id);
	
	public List<TransactionStatistic> getMutualFundStatistics(int type);
	
	public Page<MutualFund> getMutualFundByManagerId(int managerId, Pageable pageable);
	
	public List<MutualFund> getAllMutualFundByManagerId(int managerId);
	
	public Iterable<MutualFundCategory> getMutualFundCategory();
	
	public Iterable<MutualFundType> getMutualFundType();
	
	public MutualFund addMutualFund(MutualFund mutualFund);
	
	public Map<String, String> uploadDocument(MultipartFile file);
	
	public MutualFund editDocument(MultipartFile file,int id, String type);
	
	public ResponseEntity<Object> downloadFile(String type, String fileName);
	
	public String editMutualFund(MutualFund mutualFund);
	
	public void deleteMutualFund(int id);
	
}
