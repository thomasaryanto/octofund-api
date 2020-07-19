package com.thomasariyanto.octofund.service.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.thomasariyanto.octofund.dao.BankAccountRepo;
import com.thomasariyanto.octofund.dao.BankRepo;
import com.thomasariyanto.octofund.dao.UserRepo;
import com.thomasariyanto.octofund.entity.Bank;
import com.thomasariyanto.octofund.entity.BankAccount;
import com.thomasariyanto.octofund.entity.User;
import com.thomasariyanto.octofund.service.BankService;
import com.thomasariyanto.octofund.util.UploadUtil;

@Service
public class BankServiceImpl implements BankService {

	@Autowired
	private BankRepo bankRepo;
	
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private BankAccountRepo bankAccountRepo;
	
	@Autowired
	private UploadUtil fileUploader;
	
	@Override
	public Iterable<Bank> getBanks() {
		return bankRepo.findAll();
	}
	
	@Override
	public Iterable<Bank> getBankPages(Pageable pageable) {
		return bankRepo.findAll(pageable);
	}
	
	@Override
	public Bank getBankById(int id) {
		return bankRepo.findById(id).get();
	}
	
	@Override
	public Iterable<BankAccount> getBankAccounts() {
		return bankAccountRepo.findAll();
	}
	
	@Override
	public BankAccount getBankAccountById(int id) {
		return bankAccountRepo.findById(id).get();
	}
	
	@Override
	public Page<BankAccount> getBankAccountByUserId(int userId, Pageable pageable) {
		return bankAccountRepo.findAllByUserId(userId, pageable);
	}
	
	@Override
	public Iterable<BankAccount> getAllBankAccountByUserId(int userId) {
		return bankAccountRepo.findAllByUserId(userId);
	}
	
	@Override
	public Bank addBank(Bank bank) {
		String filePath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\images\\";
		
		//proses file
		Path pathTempLogo = Paths.get(StringUtils.cleanPath(filePath + "\\temp\\") + bank.getLogo());
		Path pathLogo = Paths.get(StringUtils.cleanPath(filePath + "\\logo\\bank\\") + bank.getLogo());
		try {
			Files.move(pathTempLogo, pathLogo, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String logoUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("banks/logo/").path(bank.getLogo()).toUriString();
		
		bank.setLogo(logoUri);
		bank.setId(0);
		return bankRepo.save(bank);
	}
	
	@Override
	public BankAccount addBankAccount(BankAccount bankAccount) {
		Bank findBank = bankRepo.findById(bankAccount.getBank().getId()).get();
		User findUser = userRepo.findById(bankAccount.getUser().getId()).get();
		bankAccount.setId(0);
		bankAccount.setBank(findBank);
		bankAccount.setUser(findUser);
		return bankAccountRepo.save(bankAccount);
	}
	
	@Override
	public Bank editBank(Bank bank) {
		Bank findBank = bankRepo.findById(bank.getId()).get();
		bank.setLogo(findBank.getLogo());
		return bankRepo.save(bank);
	}
	
	@Override
	public BankAccount editBankAccount(BankAccount bankAccount) {
		BankAccount findBankAccount = bankAccountRepo.findById(bankAccount.getId()).get();
		User findUser = userRepo.findById(findBankAccount.getUser().getId()).get();
		Bank findBank = bankRepo.findById(bankAccount.getBank().getId()).get();	
		bankAccount.setUser(findUser);
		bankAccount.setBank(findBank);
		return bankAccountRepo.save(bankAccount);
	}
	
	@Override
	public Map<String, String> uploadBankLogo(MultipartFile file) {
		String uploadTempPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\images\\temp\\";
		HashMap<String, String> response = new HashMap<>();
		
		String fileName = fileUploader.uploadFile(file, uploadTempPath);

		response.put("fileName", fileName);
		return response;
	}
	
	@Override
	public Bank editBankLogo(MultipartFile file, int id) {
		Bank findBank = bankRepo.findById(id).get();
		String uploadPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\images\\logo\\bank\\";
		String fileName = fileUploader.uploadFile(file, uploadPath);
		String fileUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("banks/logo/").path(fileName).toUriString();
		System.out.println(fileUri);
		System.out.println(findBank.getName());
		findBank.setLogo(fileUri);
		bankRepo.save(findBank);
		return bankRepo.save(findBank);
	}
	
	@Override
	public ResponseEntity<Object> downloadFile(String fileName){
		String imagePath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\images\\logo\\bank\\";
		Path path = Paths.get(imagePath + fileName);
		Resource resource = null;
		
		try {
			resource = new UrlResource(path.toUri());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream")).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"").body(resource);
	}
	
	//delete bank akan menghapus semua bank account yg terkait.
	@Override
	public void deleteBank(int id) {
		List<Bank> findBanks = bankRepo.findAll();
		if(findBanks.size() <= 1) {
			throw new RuntimeException("Sistem wajib memiliki satu bank!");
		}
		bankRepo.deleteById(id);
	}
	
	@Override
	public void deleteBankAccount(int id) {
		BankAccount findBankAccount = bankAccountRepo.findById(id).get();
		List<BankAccount> findBankAccounts = bankAccountRepo.findAllByUserId(findBankAccount.getUser().getId());
		
		if(findBankAccounts.size() <= 1) {
			throw new RuntimeException("Kamu wajib memiliki satu no rekening!");
		}
		bankAccountRepo.deleteById(id);
	}
}
