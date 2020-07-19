package com.thomasariyanto.octofund.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.vision.CloudVisionTemplate;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.thomasariyanto.octofund.dao.BankAccountRepo;
import com.thomasariyanto.octofund.dao.ManagerRepo;
import com.thomasariyanto.octofund.dao.MemberRepo;
import com.thomasariyanto.octofund.dao.MutualFundRepo;
import com.thomasariyanto.octofund.dao.PortfolioRepo;
import com.thomasariyanto.octofund.dao.RoleRepo;
import com.thomasariyanto.octofund.dao.TransactionRepo;
import com.thomasariyanto.octofund.dao.TransactionStatusRepo;
import com.thomasariyanto.octofund.dao.UserRepo;
import com.thomasariyanto.octofund.entity.BankAccount;
import com.thomasariyanto.octofund.entity.Manager;
import com.thomasariyanto.octofund.entity.Member;
import com.thomasariyanto.octofund.entity.MutualFund;
import com.thomasariyanto.octofund.entity.Portfolio;
import com.thomasariyanto.octofund.entity.Transaction;
import com.thomasariyanto.octofund.entity.User;
import com.thomasariyanto.octofund.projection.TransactionStatistic;
import com.thomasariyanto.octofund.service.UserService;
import com.thomasariyanto.octofund.util.EmailUtil;
import com.thomasariyanto.octofund.util.OCRUtil;
import com.thomasariyanto.octofund.util.UploadUtil;

import net.bytebuddy.utility.RandomString;

@Service
public class UserServiceImpl implements UserService {
	
private PasswordEncoder pwEncoder = new BCryptPasswordEncoder();
	
	@Autowired
	private ResourceLoader resourceLoader;

	@Autowired
	private CloudVisionTemplate cloudVisionTemplate;
	
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private RoleRepo roleRepo;
	
	@Autowired
	private ManagerRepo managerRepo;
	
	@Autowired
	private MemberRepo memberRepo;
	
	@Autowired
	private MutualFundRepo mutualFundRepo;
	
	@Autowired
	private PortfolioRepo portfolioRepo;
	
	@Autowired
	private TransactionRepo transactionRepo;
	
	@Autowired
	private TransactionStatusRepo transactionStatusRepo;
	
	@Autowired
	private BankAccountRepo bankAccountRepo;
	
	@Autowired
	private EmailUtil emailUtil;
	
	@Autowired
	private OCRUtil ocrUtil;
	
	@Autowired
	private UploadUtil fileUploader;
	
	@Override
	public Iterable<User> getUsers() {
		return userRepo.findAll();
	}
	
	@Override
	public User getUserById(int id) {
		return userRepo.findById(id).get();
	}
	
	@Override
	public Page<User> getUsersByRole(int roleId, Pageable pageable) {
		return userRepo.findAllByRoleId(roleId, pageable);
	}
	
	@Override
	public List<TransactionStatistic> getMemberStatistics(int type) {
		return memberRepo.getStatistics(type);
	}
	
	@Override
	public User registerAdmin(User user) {
		user.setId(0);
		userRepo.save(user);
		
		this.emailUtil.sendEmail(user.getEmail(), "Akun Administrator Octofund", "Akun adminsitrator octofund berhasil dibuat menggunakan email ini dengan password: "+user.getPassword());
		
		String encodedPassword = pwEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
		user.setRole(roleRepo.findById(1).get());
		user.setVerified(true);
		userRepo.save(user);
		
		return userRepo.findById(user.getId()).get();
	}
	
	@Override
	public User registerManager(Manager manager) {
		String filePath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\images\\";
		
		//proses file
		Path pathTempLogo = Paths.get(StringUtils.cleanPath(filePath + "\\temp\\") + manager.getLogo());
		Path pathLogo = Paths.get(StringUtils.cleanPath(filePath + "\\logo\\manager\\") + manager.getLogo());
		try {
			Files.move(pathTempLogo, pathLogo, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String logoUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("users/manager/logo/").path(manager.getLogo()).toUriString();
		
		manager.setLogo(logoUri);
		manager.setId(0);
		managerRepo.save(manager);
		
		this.emailUtil.sendEmail(manager.getUser().getEmail(), "Akun Manajer Investasi Octofund", "Akun manajer investasi "+manager.getCompanyName()+" di octofund berhasil dibuat menggunakan email ini dengan password: "+manager.getUser().getPassword());
		
		String encodedPassword = pwEncoder.encode(manager.getUser().getPassword());
		manager.getUser().setPassword(encodedPassword);
		manager.getUser().setRole(roleRepo.findById(2).get());
		manager.getUser().setVerified(true);
		managerRepo.save(manager);
		
		return userRepo.findById(manager.getUser().getId()).get();
	}
	
	@Override
	public User registerMember(Member member) {	
		String imagePath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\images\\";
		String signatureData = member.getSignature().replace("data:image/png;base64,", "");
		
		//disave dulu biar masuk validation
		member.setId(0);
		member.setSignature("");
		memberRepo.save(member);
		
		//proses foto
		System.out.println(member.getIdentityPhoto());
		
//		Path pathTempIdentity = Paths.get(StringUtils.cleanPath(imagePath + "\\temp\\") + member.getIdentityPhoto());
//		Path pathIdentity = Paths.get(StringUtils.cleanPath(imagePath + "\\identityPhoto\\") + member.getIdentityPhoto());
//		try {
//			Files.move(pathTempIdentity, pathIdentity, StandardCopyOption.REPLACE_EXISTING);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		Path pathTempSelfie = Paths.get(StringUtils.cleanPath(imagePath + "\\temp\\") + member.getSelfiePhoto());
		Path pathSelfie = Paths.get(StringUtils.cleanPath(imagePath + "\\selfiePhoto\\") + member.getSelfiePhoto());
		try {
			Files.move(pathTempSelfie, pathSelfie, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String identityPhotoUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("users/image/temp/").path(member.getIdentityPhoto()).toUriString();
		String selfiePhotoUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("users/image/selfiePhoto/").path(member.getSelfiePhoto()).toUriString();
		member.setIdentityPhoto(identityPhotoUri);
		member.setSelfiePhoto(selfiePhotoUri);
		
		//proses tanda  tangan
		String signatureName = RandomString.make(30)+".png";
		String signatureClean = StringUtils.cleanPath(signatureName); 
		Path signaturePath = Paths.get(StringUtils.cleanPath(imagePath + "\\signature\\") + signatureClean);
		InputStream signatureDecode = new ByteArrayInputStream(Base64.decodeBase64(signatureData.getBytes()));
		
		try {
			Files.copy(signatureDecode, signaturePath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String signatureUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("users/image/signature/").path(signatureName).toUriString();
		member.setSignature(signatureUri);
			
		//encrypt password dan set role
		String encodedPassword = pwEncoder.encode(member.getUser().getPassword());
		String token = RandomString.make(30);
		Date date = new Date();
		Calendar tokenExpired = Calendar.getInstance();
		tokenExpired.setTime(date);
		tokenExpired.add(Calendar.HOUR, 12);
		
		member.getUser().setPassword(encodedPassword);
		member.getUser().setToken(token);
		member.getUser().setTokenExpired(tokenExpired.getTime());
		member.getUser().setRole(roleRepo.findById(3).get());
		member.setIdentityName(member.getUser().getName());
		memberRepo.save(member);
		
		this.emailUtil.sendEmail(member.getUser().getEmail(), "Verifikasi Akun Octofund", "Silahkan verifikasi akun kamu dengan klik link dibawah ini (berlaku 12 jam) : \n\n http://localhost:3000/verify/"+ token +"/");
		
		return userRepo.findById(member.getUser().getId()).get();
	}
	
	@Override
	public User loginUser(User user) {
		if(userRepo.existsByEmail(user.getEmail())) {
			User findUser = userRepo.findByEmail(user.getEmail()).get();
			
			if(pwEncoder.matches(user.getPassword(), findUser.getPassword())) {
				if(findUser.isVerified()) {
					return findUser;
				}
				else {
					throw new RuntimeException("Silahkan verifikasi alamat email ("+findUser.getEmail()+") terlebih dahulu!");
				}
			}
			else {
				throw new RuntimeException("Password pengguna salah!");
			}
		}
		else {
			throw new RuntimeException("Pengguna tidak ditemukan!");
		}
	}
	
	@Override
	public Map<String, String> uploadPhoto(MultipartFile file) {
		String uploadTempPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\images\\temp\\";
		HashMap<String, String> response = new HashMap<>();
		
		String fileName = fileUploader.uploadFile(file, uploadTempPath);

		response.put("fileName", fileName);
		return response;
	}
	
	@Override
	public HashMap<String, String> uploadIdentityPhotoOCR(MultipartFile file) {
		String uploadTempPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\images\\temp\\";
		HashMap<String, String> response = new HashMap<>();
		
		String fileName = fileUploader.uploadFile(file, uploadTempPath);
		response.put("fileName", fileName);
		
		String ocrResult = this.cloudVisionTemplate.extractTextFromImage(this.resourceLoader.getResource("file:"+uploadTempPath+fileName));
		
		List<String> cleanOcrResult = ocrUtil.cleanResult(ocrResult);
		
		int detectionCount = 0;
		
		if(cleanOcrResult.size() > 5) {
			int ktpType = ocrUtil.checkKTPType(cleanOcrResult.get(5));
			int namePosition = ktpType == 1 ? 3 : 4;
			int addressPosition = ktpType == 1 ? 6 : 7;
			
			for (String s : cleanOcrResult) {
				String[] detectedField = ocrUtil.checkKTPData(s);
				if(!detectedField[0].equalsIgnoreCase("unknown")) {
					response.put(detectedField[0], detectedField[1]);
					detectionCount++;
				}
			}
			
			response.put("nik", cleanOcrResult.get(2).replaceAll("[^0-9]", ""));
			response.put("name", cleanOcrResult.get(namePosition));
			response.put("address", cleanOcrResult.get(addressPosition));
		}
		
		response.put("detectionCount", String.valueOf(detectionCount));
		
		return response;
	}	
	
	@Override
	public ResponseEntity<Object> downloadFile(String type, String fileName){
		String imagePath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\images\\";
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
	public User editManagerLogo(MultipartFile file, int id) {
		User findUser = userRepo.findById(id).get();
		String uploadPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\images\\logo\\manager\\";
		String fileName = fileUploader.uploadFile(file, uploadPath);
		String fileUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("users/manager/logo/").path(fileName).toUriString();
		findUser.getManager().setLogo(fileUri);
		return userRepo.save(findUser);
	}
	
	@Override
	public ResponseEntity<Object> downloadLogo(String fileName){
		String imagePath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\images\\logo\\manager\\";
		Path path = Paths.get(imagePath + fileName);
		Resource resource = null;
		
		try {
			resource = new UrlResource(path.toUri());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream")).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"").body(resource);
	}
	
	@Override
	public String verifyUser(String token) {
		Date date = new Date();
		User findUser = userRepo.findByToken(token).get();
		
		if (findUser.isVerified()) {
			throw new RuntimeException("User "+ findUser.getEmail() +" sudah diverifikasi!");
		}
		else if (date.after(findUser.getTokenExpired())) {
			throw new RuntimeException("Token verifikasi sudah kadaluarsa!");
		}
		else {
			findUser.setVerified(true);
			findUser.setToken(null);
			findUser.setTokenExpired(null);
			userRepo.save(findUser);		
			return "Email berhasil diverifikasi! Kamu sudah dapat login, namun masih harus menunggu verifikasi data diri untuk melakukan transaksi.";
		}
	}
	
	@Override
	public String verifyUserResend(User user) {
		User findUser;
		if(user.getEmail() != null) {
			findUser = userRepo.findByEmail(user.getEmail()).get();
		} else {
			findUser = userRepo.findByToken(user.getToken()).get();
		}
		
		Date date = new Date();
		String newToken = RandomString.make(30);
		Calendar tokenExpired = Calendar.getInstance();
		tokenExpired.setTime(date);
		tokenExpired.add(Calendar.HOUR, 12);
		
		if (findUser.isVerified()) {
			throw new RuntimeException("User "+ findUser.getEmail() +" sudah diverifikasi!");
		}
		else {
			findUser.setToken(newToken);
			findUser.setTokenExpired(tokenExpired.getTime());
			userRepo.save(findUser);		
			
			this.emailUtil.sendEmail(findUser.getEmail(), "Verifikasi Akun Octofund", "Silahkan verifikasi ulang akun kamu dengan klik link dibawah ini (berlaku 12 jam) : \n\n http://localhost:3000/verify/"+ newToken +"/");
			
			return "Verifikasi berhasil dikirim ulang ke email "+ findUser.getEmail();
		}
	}
	
	@Override
	public String sendForgotPassowrd(User user) {
		Date date = new Date();
		String token = RandomString.make(30);
		Calendar tokenExpired = Calendar.getInstance();
		tokenExpired.setTime(date);
		tokenExpired.add(Calendar.HOUR, 2);
		
		User findUser = userRepo.findByEmail(user.getEmail()).get();
		
		if (!findUser.isVerified()) {
			throw new RuntimeException("User "+ findUser.getEmail() +" belum diverifikasi!");
		}
		else {
			findUser.setToken(token);
			findUser.setTokenExpired(tokenExpired.getTime());
			userRepo.save(findUser);		
			
			this.emailUtil.sendEmail(findUser.getEmail(), "Reset Passowrd Akun Octofund", "Silahkan reset password akun kamu dengan klik link dibawah ini (berlaku 2 jam) : \n\n http://localhost:3000/reset/"+ token +"/");
			
			return "Instruksi reset password berhasil dikirim ulang ke email "+ findUser.getEmail();
		}
	}
	
	@Override
	public String getResetPassword(String token) {
		Date date = new Date();
		User findUser = userRepo.findByToken(token).get();
		
		if (!findUser.isVerified()) {
			throw new RuntimeException("User "+ findUser.getEmail() +" belum diverifikasi!");
		}
		else if (date.after(findUser.getTokenExpired())) {
			throw new RuntimeException("Token reset password sudah kadaluarsa!");
		}
		else {
			return findUser.getEmail();
		}
	}
	
	@Override
	public String changePassword(User user) {
		User findUser = userRepo.findByToken(user.getToken()).get();
		Date date = new Date();
		String encodedPassword = pwEncoder.encode(user.getPassword());
		
		if (!findUser.isVerified()) {
			throw new RuntimeException("User "+ findUser.getEmail() +" belum diverifikasi!");
		}
		else if (date.after(findUser.getTokenExpired())) {
			throw new RuntimeException("Token reset password sudah kadaluarsa!");
		}
		else {
			findUser.setPassword(encodedPassword);
			findUser.setToken(null);
			findUser.setTokenExpired(null);
			userRepo.save(findUser);
			return "Password berhasil di ubah!";
		}
	}
	
	//edit user harus ngirim password juga waaupun kosong.
	//edit user dari admin
	@Override
	public User editUser(User user) {
		User findUser = userRepo.findById(user.getId()).get();
		if (!user.getPassword().equalsIgnoreCase("")) {
			String encodedPassword = pwEncoder.encode(user.getPassword());
			user.setPassword(encodedPassword);
		} else {
			user.setPassword(findUser.getPassword());
		}
		
		if(user.getMember() != null) {
			user.getMember().setIdentityName(user.getName());
		}
		
		if(user.getManager() != null) {
			user.getManager().setLogo(findUser.getManager().getLogo());
		}
		
		user.setRole(findUser.getRole());
		user.setVerified(findUser.isVerified());
		user.setKyc(findUser.isKyc());
		user.setRejected(findUser.isRejected());
		return userRepo.save(user);
	}
	
	//edit user sendiri
	@Override
	public User editUserAuth(User user, String oldPassword) {
		User findUser = userRepo.findById(user.getId()).get();
		if (!user.getPassword().equalsIgnoreCase("")) {
			if(pwEncoder.matches(oldPassword, findUser.getPassword())) {
				String encodedPassword = pwEncoder.encode(user.getPassword());
				user.setPassword(encodedPassword);
			} else {
				throw new RuntimeException("Password lama salah!");
			}
			
		} else {
			user.setPassword(findUser.getPassword());
		}
		
		if(user.getMember() != null) {
			user.getMember().setIdentityName(user.getName());
			if(user.isRejected()) {
				user.setRejected(false);
			}
		}
		
		if(user.getManager() != null) {
			user.getManager().setLogo(findUser.getManager().getLogo());
		}
		
		user.setRole(findUser.getRole());
		user.setVerified(findUser.isVerified());
		user.setKyc(findUser.isKyc());
		return userRepo.save(user);
	}
	
	@Override
	public void deleteUser(int id) {
		User findUser = userRepo.findById(id).get();
		
		if(findUser.getRole().getId() == 2) {
			List<MutualFund> findMutualFunds = mutualFundRepo.findByManagerId(findUser.getId());
			
			System.out.println(findMutualFunds.size());
			
			if(findMutualFunds.size() > 0) {
				for (MutualFund mutualFund : findMutualFunds) {
					
					MutualFund findMutualFund = mutualFundRepo.findById(mutualFund.getId()).get();
					List<Portfolio> findPortfolios = portfolioRepo.findAllByMutualFundId(mutualFund.getId());
					List<Transaction> findTransactions = transactionRepo.findAllByMutualFundIdAndTransactionStatusId(mutualFund.getId(), 4);
					
					System.out.println(findPortfolios.size());
					System.out.println(findTransactions.size());
					
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
				        }
					}
					
					if(findTransactions.size() > 0) {
						for (Transaction transaction : findTransactions) {
							transaction.setMutualFund(null);
							transactionRepo.save(transaction);
						}
					}
					
					mutualFundRepo.deleteById(mutualFund.getId());
				}
			}
		}
		
		if(findUser.getRole().getId() == 3) {
			List<Transaction> findTransactions = transactionRepo.findAllByMemberIdAndTransactionStatusId(findUser.getId(), 4);
			
			if(findTransactions.size() > 0) {
				for (Transaction transaction : findTransactions) {
					transaction.setMutualFund(null);
					transactionRepo.save(transaction);
				}
			}
		}
		userRepo.deleteById(id);
	}
	
	
	@Override
	public Page<User> getKycUsers(Pageable pageable) {
		return userRepo.findAllByRoleIdAndIsVerifiedAndIsRejectedAndIsKyc(3, true, false, false, pageable);
	}
	
	@Override
	public String acceptUser(User user) {
		User findUser = userRepo.findById(user.getId()).get();
		if(findUser.isKyc()) {
			throw new RuntimeException("Nasabah ini sudah terverifikasi KYC!");
		}
		else if(!findUser.isVerified()) {
			throw new RuntimeException("Nasabah ini belum melakukan verifikasi email!");
		}
		else {
			findUser.setKyc(true);
			findUser.getMember().setSid(user.getMember().getSid());
			findUser.getMember().setIfua(user.getMember().getIfua());
			userRepo.save(findUser);
			this.emailUtil.sendEmail(findUser.getEmail(), "Verifikasi data diri diterima", "Verifikasi data diri kamu berhasil! Sekarang kamu sudah bisa mulai berinvestasi di OctoFund!");
			return "KYC nasabah berhasil diterima!";
		}
	}
	
	@Override
	public String rejectUser(String msg, User user ) {
		User findUser = userRepo.findById(user.getId()).get();
		if(findUser.isKyc()) {
			throw new RuntimeException("Nasabah ini sudah terverifikasi KYC!");
		}
		else if(!findUser.isVerified()) {
			throw new RuntimeException("Nasabah ini belum melakukan verifikasi email!");
		}
		else {
			findUser.setRejected(true);
			userRepo.save(findUser);
			this.emailUtil.sendEmail(findUser.getEmail(), "Verifikasi data diri ditolak", "Maaf verifikasi data diri anda ditolak dikarenakan hal berikut ini: \n\n" + msg);
			return "KYC nasabah berhasil ditolak!";
		}
	}
}
