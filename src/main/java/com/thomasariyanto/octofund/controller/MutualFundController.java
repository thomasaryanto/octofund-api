package com.thomasariyanto.octofund.controller;

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

import javax.validation.Valid;

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
import org.springframework.util.StringUtils;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.thomasariyanto.octofund.dao.ManagerRepo;
import com.thomasariyanto.octofund.dao.MutualFundCategoryRepo;
import com.thomasariyanto.octofund.dao.MutualFundRepo;
import com.thomasariyanto.octofund.dao.MutualFundTypeRepo;
import com.thomasariyanto.octofund.dao.PriceHistoryRepo;
import com.thomasariyanto.octofund.entity.Manager;
import com.thomasariyanto.octofund.entity.MutualFund;
import com.thomasariyanto.octofund.entity.MutualFundCategory;
import com.thomasariyanto.octofund.entity.MutualFundType;
import com.thomasariyanto.octofund.entity.PriceHistory;
import com.thomasariyanto.octofund.projection.TransactionStatistic;
import com.thomasariyanto.octofund.util.UploadUtil;

@RestController
@RequestMapping("/mutualfund")
@CrossOrigin
public class MutualFundController {
	
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
	private UploadUtil fileUploader;
	
	@GetMapping
	public Page<MutualFund> getMutualFunds(@RequestParam(value="page", defaultValue="0") Integer pageNo, 
			@RequestParam(value="size", defaultValue="2") Integer pageSize, 
			@RequestParam(value="sortKey", defaultValue="id") String sortKey, 
			@RequestParam(value="sortType", defaultValue="asc") String sortType,
			@RequestParam(value="filterKey", defaultValue="none") String filterKey,
			@RequestParam(value="filterValue", defaultValue="all") String filterValue,
			@RequestParam(value="keyword", defaultValue="") String keyword) {
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
	
	@GetMapping("/{id}")
	public MutualFund getMutualFundById(@PathVariable int id) {
		return mutualFundRepo.findById(id).get();
	}
	
	@GetMapping("/statistics")
	public List<TransactionStatistic> getMutualFundStatistics(@RequestParam(value="type", defaultValue="1") Integer type) {
		return mutualFundRepo.getStatistics(type);
	}
	
	@GetMapping("/manager/{managerId}")
	public Page<MutualFund> getMutualFundByManagerId(@PathVariable int managerId, Pageable pageable) {
		return mutualFundRepo.findAllByManagerId(managerId, pageable);
	}
	
	@GetMapping("/manager/{managerId}/all")
	public List<MutualFund> getAllMutualFundByManagerId(@PathVariable int managerId) {
		return mutualFundRepo.findByManagerId(managerId);
	}
	
	@GetMapping("/category")
	public Iterable<MutualFundCategory> getMutualFundCategory() {
		return mutualFundCategoryRepo.findAll();
	}
	
	@GetMapping("/type")
	public Iterable<MutualFundType> getMutualFundType() {
		return mutualFundTypeRepo.findAll();
	}
	
	@PostMapping
	public MutualFund addMutualFund(@Valid @RequestBody MutualFund mutualFund) {
		String filePath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\pdf\\";
		
		Manager findManager = managerRepo.findById(mutualFund.getManager().getId()).get();
		MutualFundCategory findCategory = mutualFundCategoryRepo.findById(mutualFund.getMutualFundCategory().getId()).get();
		MutualFundType findType = mutualFundTypeRepo.findById(mutualFund.getMutualFundType().getId()).get();
		
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
		
		priceHistoryRepo.save(todayPrice);
		priceHistoryRepo.save(yasterdayPrice);
		return mutualFundRepo.save(mutualFund);
	}
	
	@PostMapping("/upload/")
	public Map<String, String> uploadIdentityPhoto(@RequestParam("file") MultipartFile file) {
		String uploadTempPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\pdf\\temp\\";
		HashMap<String, String> response = new HashMap<>();
		
		String fileName = fileUploader.uploadFile(file, uploadTempPath);

		response.put("fileName", fileName);
		return response;
	}
	
	@PostMapping("/upload/{id}/{type}")
	public MutualFund editDocument(@RequestParam("file") MultipartFile file, @PathVariable int id, @PathVariable String type) {
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
	
	@GetMapping("/document/{type}/{fileName:.+}")
	public ResponseEntity<Object> downloadFile(@PathVariable String type, @PathVariable String fileName){
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
	
	@PutMapping
	public MutualFund editMutualFund(@RequestBody MutualFund mutualFund) {
		MutualFund findMutualFund = mutualFundRepo.findById(mutualFund.getId()).get();
		Manager findManager = managerRepo.findById(findMutualFund.getManager().getId()).get();
		MutualFundCategory findCategory = mutualFundCategoryRepo.findById(mutualFund.getMutualFundCategory().getId()).get();
		MutualFundType findType = mutualFundTypeRepo.findById(mutualFund.getMutualFundType().getId()).get();
		
		mutualFund.setMutualFundCategory(findCategory);
		mutualFund.setMutualFundType(findType);
		mutualFund.setFactsheetFile(findMutualFund.getFactsheetFile());
		mutualFund.setProspectusFile(findMutualFund.getProspectusFile());
		mutualFund.setManager(findManager);
		return mutualFundRepo.save(mutualFund);
	}
	
	@PutMapping("updateprice")
	public PriceHistory updatePriceMutualFund(@RequestBody MutualFund mutualFund) {
		MutualFund findMutualFund = mutualFundRepo.findById(mutualFund.getId()).get();
		Calendar today = new GregorianCalendar();
		today.set(Calendar.HOUR_OF_DAY, 0); //anything 0 - 23
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
	    Date todayDate = today.getTime();
	    today.add(Calendar.DAY_OF_YEAR, 1);
	    Date tomorrowDate = today.getTime();
	    
	    findMutualFund.setLastUpdatePrice(new Date());
	    findMutualFund.setLastPrice(mutualFund.getLastPrice());
	    mutualFundRepo.save(findMutualFund);
	    
	    List<PriceHistory> checkPriceHistory = priceHistoryRepo.findAllByMutualFundIdAndDateBetween(mutualFund.getId(), todayDate, tomorrowDate);
	    if (checkPriceHistory.size() > 0) {
	    	PriceHistory findPriceHistory = priceHistoryRepo.findById(checkPriceHistory.get(0).getId()).get();
	    	findPriceHistory.setPrice(mutualFund.getLastPrice());
	    	return priceHistoryRepo.save(findPriceHistory);
	    }
	    else {
	    	PriceHistory priceHistory = new PriceHistory();
	    	priceHistory.setMutualFund(findMutualFund);
	    	priceHistory.setDate(new Date());
	    	priceHistory.setPrice(mutualFund.getLastPrice());
	    	return priceHistoryRepo.save(priceHistory);
	    }
	}
	
	@DeleteMapping("/{id}")
	public void deleteMutualFund(@PathVariable int id) {
		mutualFundRepo.deleteById(id);
	}
}
