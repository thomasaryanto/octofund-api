package com.thomasariyanto.octofund.service.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.thomasariyanto.octofund.dao.BankAccountRepo;
import com.thomasariyanto.octofund.dao.ManagerRepo;
import com.thomasariyanto.octofund.dao.MutualFundCategoryRepo;
import com.thomasariyanto.octofund.dao.MutualFundRepo;
import com.thomasariyanto.octofund.dao.MutualFundTypeRepo;
import com.thomasariyanto.octofund.dao.PortfolioRepo;
import com.thomasariyanto.octofund.dao.PriceHistoryRepo;
import com.thomasariyanto.octofund.dao.TransactionRepo;
import com.thomasariyanto.octofund.dao.TransactionStatusRepo;
import com.thomasariyanto.octofund.entity.BankAccount;
import com.thomasariyanto.octofund.entity.Manager;
import com.thomasariyanto.octofund.entity.MutualFund;
import com.thomasariyanto.octofund.entity.MutualFundCategory;
import com.thomasariyanto.octofund.entity.MutualFundType;
import com.thomasariyanto.octofund.entity.Portfolio;
import com.thomasariyanto.octofund.entity.PriceHistory;
import com.thomasariyanto.octofund.entity.Transaction;
import com.thomasariyanto.octofund.projection.TransactionStatistic;
import com.thomasariyanto.octofund.service.MutualFundService;
import com.thomasariyanto.octofund.util.UploadUtil;

@Service
public class MutualFundServiceImpl implements MutualFundService {

	@Autowired
	private MutualFundRepo mutualFundRepo;
	
	@Autowired
	private MutualFundCategoryRepo mutualFundCategoryRepo;
	
	@Autowired
	private MutualFundTypeRepo mutualFundTypeRepo;
	
	@Autowired
	private PriceHistoryRepo priceHistoryRepo;
	
	@Autowired
	private ManagerRepo managerRepo;
	
	@Autowired
	private PortfolioRepo portfolioRepo;
	
	@Autowired
	private TransactionRepo transactionRepo;
	
	@Autowired
	private TransactionStatusRepo transactionStatusRepo;
	
	@Autowired
	private BankAccountRepo bankAccountRepo;
	
	@Autowired
	private UploadUtil fileUploader;
	
	@Override
	public Page<MutualFund> getMutualFunds(int pageNo, int pageSize, String sortKey, String sortType, String filterKey, String filterValue, String keyword) {
		Sort sort = sortType.equalsIgnoreCase("asc") ? Sort.by(sortKey).ascending() : Sort.by(sortKey).descending();
		Pageable page = PageRequest.of(pageNo, pageSize, sort);
		
		if(filterKey.equals("category")) {
			Page<MutualFund> pagedResult = mutualFundRepo.findAllByNameContainingAndMutualFundCategoryId(keyword, Integer.parseInt(filterValue), page);
			return pagedResult;
		} 
		else if (filterKey.equals("price")) {
			String[] price = filterValue.split("-");
			Page<MutualFund> pagedResult = mutualFundRepo.findAllByNameContainingAndLastPriceBetween(keyword, Double.parseDouble(price[0]), Double.parseDouble(price[1]), page);
			return pagedResult;
		}
		else {
			Page<MutualFund> pagedResult = mutualFundRepo.findAllByNameContaining(keyword, page);
			return pagedResult;
		}
		
	}
	
	@Override
	public MutualFund getMutualFundById(int id) {
		return mutualFundRepo.findById(id).get();
	}
	
	@Override
	public List<TransactionStatistic> getMutualFundStatistics(int type) {
		return mutualFundRepo.getStatistics(type);
	}
	
	@Override
	public List<TransactionStatistic> getMutualFundStatisticsByManager(int type, int managerId) {
		return mutualFundRepo.getStatisticsManager(type, managerId);
	}
	
	@Override
	public long getTransactionCount(int mutualFundId) {
		return transactionRepo.countByMutualFundId(mutualFundId);
	}
	
	@Override
	public Page<MutualFund> getMutualFundByManagerId(int managerId, Pageable pageable) {
		return mutualFundRepo.findAllByManagerId(managerId, pageable);
	}
	
	@Override
	public List<MutualFund> getAllMutualFundByManagerId(int managerId) {
		return mutualFundRepo.findByManagerId(managerId);
	}
	
	@Override
	public Iterable<MutualFundCategory> getMutualFundCategory() {
		return mutualFundCategoryRepo.findAll();
	}
	
	@Override
	public Iterable<MutualFundType> getMutualFundType() {
		return mutualFundTypeRepo.findAll();
	}
	
	@Override
	public MutualFund addMutualFund(MutualFund mutualFund) {
		String filePath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\pdf\\";
		
		Manager findManager = managerRepo.findById(mutualFund.getManager().getId()).get();
		MutualFundCategory findCategory = mutualFundCategoryRepo.findById(mutualFund.getMutualFundCategory().getId()).get();
		MutualFundType findType = mutualFundTypeRepo.findById(mutualFund.getMutualFundType().getId()).get();
		
		if(mutualFund.getStock() > 0) {
			mutualFund.setLimited(true);
		}
		else {
			mutualFund.setLimited(false);
		}
		
		mutualFund.setId(0);
		mutualFund.setManager(findManager);
		mutualFund.setMutualFundCategory(findCategory);
		mutualFund.setMutualFundType(findType);
		mutualFundRepo.save(mutualFund);
		
		//proses file
		Path pathTempFactsheet = Paths.get(StringUtils.cleanPath(filePath + "\\temp\\") + mutualFund.getFactsheetFile());
		Path pathFactsheet = Paths.get(StringUtils.cleanPath(filePath + "\\factsheetFile\\") + mutualFund.getFactsheetFile());
		try {
			Files.move(pathTempFactsheet, pathFactsheet, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Path pathTempProspectus = Paths.get(StringUtils.cleanPath(filePath + "\\temp\\") + mutualFund.getProspectusFile());
		Path pathPorspectus = Paths.get(StringUtils.cleanPath(filePath + "\\prospectusFile\\") + mutualFund.getProspectusFile());
		try {
			Files.move(pathTempProspectus, pathPorspectus, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String factsheetFileUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("mutualfund/document/factsheetFile/").path(mutualFund.getFactsheetFile()).toUriString();
		String prospectusFileUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("mutualfund/document/prospectusFile/").path(mutualFund.getProspectusFile()).toUriString();
		mutualFund.setFactsheetFile(factsheetFileUri);
		mutualFund.setProspectusFile(prospectusFileUri);
		mutualFundRepo.save(mutualFund);
		
		//generate harga 30 hari kebelakang untuk demo chart
		Calendar today = new GregorianCalendar();
	    int count = -30;
	    for (int i = 0; i < 30; i++) {
	    	today.setTime(new Date());
	    	today.add(Calendar.DAY_OF_YEAR, count);
	    	
	    	Random random = new Random();
	    	PriceHistory priceHistory = new PriceHistory();
	    	priceHistory.setMutualFund(mutualFund);
	    	priceHistory.setDate(today.getTime());
	    	priceHistory.setPrice(random.nextInt(2000 - 1000) + 1000);
	    	priceHistoryRepo.save(priceHistory);
	    	count++;
    	}
	    
		
		Date date = new Date();
		Calendar dateYasterday = Calendar.getInstance();
		dateYasterday.setTime(date);
		dateYasterday.add(Calendar.DAY_OF_YEAR, -1);
		
		PriceHistory todayPrice = new PriceHistory();
		PriceHistory yasterdayPrice = new PriceHistory();
		todayPrice.setMutualFund(mutualFund);
		yasterdayPrice.setMutualFund(mutualFund);
		todayPrice.setPrice(mutualFund.getLastPrice());
		yasterdayPrice.setPrice(mutualFund.getLastPrice());
		todayPrice.setDate(date);
		yasterdayPrice.setDate(dateYasterday.getTime());
		

//		priceHistoryRepo.save(yasterdayPrice);
		priceHistoryRepo.save(todayPrice);
		return mutualFundRepo.save(mutualFund);
	}
	
	@Override
	public Map<String, String> uploadDocument(MultipartFile file) {
		String uploadTempPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\pdf\\temp\\";
		HashMap<String, String> response = new HashMap<>();
		
		String fileName = fileUploader.uploadFile(file, uploadTempPath);

		response.put("fileName", fileName);
		return response;
	}
	
	@Override
	public MutualFund editDocument(MultipartFile file, int id, String type) {
		MutualFund findMutualFund = mutualFundRepo.findById(id).get();
		String uploadPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\pdf\\"+type+"\\";
		String fileName = fileUploader.uploadFile(file, uploadPath);
		String fileUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("mutualfund/document/"+type+"/").path(fileName).toUriString();
		
		if(type.equalsIgnoreCase("factsheetFile")) {
			findMutualFund.setFactsheetFile(fileUri);
		}
		else {
			findMutualFund.setProspectusFile(fileUri);
		}
		return mutualFundRepo.save(findMutualFund);
	}
	
	@Override
	public ResponseEntity<Object> downloadFile(String type, String fileName){
		String imagePath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\pdf\\";
		Path path = Paths.get(imagePath +"\\"+type+"\\"+ fileName);
		Resource resource = null;
		
		try {
			resource = new UrlResource(path.toUri());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream")).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"").body(resource);
	}
	
	@Override
	public String editMutualFund(MutualFund mutualFund) {
		MutualFund findMutualFund = mutualFundRepo.findById(mutualFund.getId()).get();
		Manager findManager = managerRepo.findById(findMutualFund.getManager().getId()).get();
		MutualFundCategory findCategory = mutualFundCategoryRepo.findById(mutualFund.getMutualFundCategory().getId()).get();
		MutualFundType findType = mutualFundTypeRepo.findById(mutualFund.getMutualFundType().getId()).get();
		
		if(mutualFund.getStock() > 0) {
			mutualFund.setLimited(true);
		}
		else {
			mutualFund.setLimited(false);
		}
		
		mutualFund.setLastUpdatePrice(new Date());
		mutualFund.setMutualFundCategory(findCategory);
		mutualFund.setMutualFundType(findType);
		mutualFund.setFactsheetFile(findMutualFund.getFactsheetFile());
		mutualFund.setProspectusFile(findMutualFund.getProspectusFile());
		mutualFund.setManager(findManager);
		mutualFundRepo.save(mutualFund);
		
		Calendar today = new GregorianCalendar();
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
	    Date todayDate = today.getTime();
	    today.add(Calendar.DAY_OF_YEAR, 1);
	    Date tomorrowDate = today.getTime();
	    
		List<PriceHistory> checkPriceHistory = priceHistoryRepo.findAllByMutualFundIdAndDateBetween(mutualFund.getId(), todayDate, tomorrowDate);
	    if (checkPriceHistory.size() > 0) {
	    	PriceHistory findPriceHistory = priceHistoryRepo.findById(checkPriceHistory.get(0).getId()).get();
	    	findPriceHistory.setPrice(mutualFund.getLastPrice());
	    	priceHistoryRepo.save(findPriceHistory);
	    }
	    else {
	    	PriceHistory priceHistory = new PriceHistory();
	    	priceHistory.setMutualFund(findMutualFund);
	    	priceHistory.setDate(new Date());
	    	priceHistory.setPrice(mutualFund.getLastPrice());
	    	priceHistoryRepo.save(priceHistory);
	    }
	    
		return "Reksadana berhasil diedit!";
	}
	
	@Override
	public void deleteMutualFund(int id) {
		MutualFund findMutualFund = mutualFundRepo.findById(id).get();
		List<Portfolio> findPortfolios = portfolioRepo.findAllByMutualFundId(id);
		List<Transaction> findTransactions = transactionRepo.findAllByMutualFundIdAndTransactionStatusId(id, 4);
		
		if(findPortfolios.size() > 0) {
			for (Portfolio portfolio : findPortfolios) {
				List<BankAccount> findBankAccount = bankAccountRepo.findAllByUserId(portfolio.getMember().getId());
				Transaction transaction = new Transaction();
				
				transaction.setId(0);
				transaction.setMember(portfolio.getMember());
				transaction.setProductName(findMutualFund.getName());
				transaction.setManagerName(findMutualFund.getManager().getCompanyName());
				transaction.setBankName(findBankAccount.get(0).getBank().getShortName() +" - "+findBankAccount.get(0).getAccountNumber() + " - "+ findBankAccount.get(0).getHolderName());
				transaction.setTotalUnit(portfolio.getTotalUnit());
				transaction.setTotalPrice((long)(portfolio.getTotalUnit() * findMutualFund.getLastPrice()));
				transaction.setMutualFund(null);
				transaction.setType(2);
				transaction.setTransactionStatus(transactionStatusRepo.findById(4).get());
				transaction.setDate(new Date());
				transactionRepo.save(transaction);
				portfolioRepo.deleteById(portfolio.getId());
	        }
		}
		
		if(findTransactions.size() > 0) {
			for (Transaction transaction : findTransactions) {
				transaction.setMutualFund(null);
				transactionRepo.save(transaction);
			}
		}
		
		
		mutualFundRepo.deleteById(id);
	}
}
