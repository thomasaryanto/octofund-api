package com.thomasariyanto.octofund.controller;

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
import java.util.Map;

import javax.validation.Valid;

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

import com.thomasariyanto.octofund.dao.BankAccountRepo;
import com.thomasariyanto.octofund.dao.ManagerRepo;
import com.thomasariyanto.octofund.dao.MemberRepo;
import com.thomasariyanto.octofund.dao.RoleRepo;
import com.thomasariyanto.octofund.dao.UserRepo;
import com.thomasariyanto.octofund.entity.Manager;
import com.thomasariyanto.octofund.entity.Member;
import com.thomasariyanto.octofund.entity.User;
import com.thomasariyanto.octofund.util.EmailUtil;
import com.thomasariyanto.octofund.util.UploadUtil;

import net.bytebuddy.utility.RandomString;

@RestController
@RequestMapping("/users")
@CrossOrigin
public class UserController {
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
	private BankAccountRepo bankAccountRepo;
	
	@Autowired
	private EmailUtil emailUtil;
	
	@Autowired
	private UploadUtil fileUploader;
	
	@GetMapping
	public Iterable<User> getUsers() {
		return userRepo.findAll();
	}
	
	@GetMapping("/{id}")
	public User getUserById(@PathVariable int id) {
		return userRepo.findById(id).get();
	}
	
	@GetMapping("/role/{roleId}")
	public Page<User> getUsersByRole(@PathVariable int roleId, Pageable pageable) {
		return userRepo.findAllByRoleId(roleId, pageable);
	}
	
	@PostMapping("/staff")
	public User registerStaff(@Valid @RequestBody User user) {
		user.setId(0);
		userRepo.save(user);
		
		String encodedPassword = pwEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
		user.setRole(roleRepo.findById(2).get());
		user.setVerified(true);
		userRepo.save(user);
		
		return userRepo.findById(user.getId()).get();
	}
	
	@PostMapping("/manager")
	public User registerManager(@Valid @RequestBody Manager manager) {
		manager.setId(0);
		managerRepo.save(manager);
		
		String encodedPassword = pwEncoder.encode(manager.getUser().getPassword());
		manager.getUser().setPassword(encodedPassword);
		manager.getUser().setRole(roleRepo.findById(3).get());
		manager.getUser().setVerified(true);
		managerRepo.save(manager);
		
		return userRepo.findById(manager.getUser().getId()).get();
	}
	
	@PostMapping("/member")
	public User registerMember(@Valid @RequestBody Member member) {	
		String imagePath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\images\\";
		String signatureData = member.getSignature().replace("data:image/png;base64,", "");
		
		//disave dulu biar masuk validation
		member.setId(0);
		member.setSignature("");
		memberRepo.save(member);
		
		//proses foto
		Path pathTempIdentity = Paths.get(StringUtils.cleanPath(imagePath + "\\temp\\") + member.getIdentityPhoto());
		Path pathIdentity = Paths.get(StringUtils.cleanPath(imagePath + "\\identityPhoto\\") + member.getIdentityPhoto());
		try {
			Files.move(pathTempIdentity, pathIdentity, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Path pathTempSelfie = Paths.get(StringUtils.cleanPath(imagePath + "\\temp\\") + member.getSelfiePhoto());
		Path pathSelfie = Paths.get(StringUtils.cleanPath(imagePath + "\\selfiePhoto\\") + member.getSelfiePhoto());
		try {
			Files.move(pathTempSelfie, pathSelfie, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String identityPhotoUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("users/image/identityPhoto/").path(member.getIdentityPhoto()).toUriString();
		String selfiePhotoUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("users/image/selfiePhoto/").path(member.getSelfiePhoto()).toUriString();
		member.setIdentityPhoto(identityPhotoUri);
		member.setSelfiePhoto(selfiePhotoUri);
		
		//proses tanda tangan
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
		member.getUser().setRole(roleRepo.findById(4).get());
		member.setIdentityName(member.getUser().getName());
		memberRepo.save(member);
		
		this.emailUtil.sendEmail(member.getUser().getEmail(), "Verifikasi Akun Octofund", "Silahkan verifikasi akun kamu dengan klik link dibawah ini (berlaku 12 jam) : \n\n http://localhost:3000/verify/"+ token +"/");
		
		return userRepo.findById(member.getUser().getId()).get();
	}
	
	@PostMapping("/login")
	public User loginUser(@RequestBody User user) {
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
	
	@PostMapping("/upload/")
	public Map<String, String> uploadIdentityPhoto(@RequestParam("file") MultipartFile file) {
		String uploadTempPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\images\\temp\\";
		HashMap<String, String> response = new HashMap<>();
		
		String fileName = fileUploader.uploadFile(file, uploadTempPath);

		response.put("fileName", fileName);
		return response;
	}
	
//	@PostMapping("/upload/temp/")
//	public Map<String, String> uploadIdentityPhoto(@RequestParam("file") MultipartFile file) {
//		String uploadTempPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\images\\temp\\";
//		HashMap<String, String> response = new HashMap<>();
//		String fileExtension = file.getContentType().split("/")[1];
//		String newFileName = RandomString.make(20) + "." + fileExtension;
//
//		String fileName = StringUtils.cleanPath(newFileName);
//		
//		Path path = Paths.get(StringUtils.cleanPath(uploadTempPath) + fileName);
//		
//		try {
//			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
////		if (type == "identityPhoto" ) {
////			String ocr = this.cloudVisionTemplate.extractTextFromImage(this.resourceLoader.getResource("file:"+path));
////			System.out.println(ocr);
////			response.put("visionOcr", ocr);
////		}
//
//		response.put("fileName", fileName);
//		return response;
//	}
	
	@GetMapping("/image/{type}/{fileName:.+}")
	public ResponseEntity<Object> downloadFile(@PathVariable String type, @PathVariable String fileName){
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
	
//	@PostMapping("/upload/selfiePhoto")
//	public Map<String, String> uploadSelfiePhoto(@RequestParam("file") MultipartFile file) {
//		HashMap<String, String> response = new HashMap<>();
//		String fileExtension = file.getContentType().split("/")[1];
//		String newFileName = RandomString.make(20) + "." + fileExtension;
//
//		String fileName = StringUtils.cleanPath(newFileName);
//		
//		Path path = Paths.get(StringUtils.cleanPath(uploadTempPath) + fileName);
//		
//		try {
//			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		response.put("fileName", fileName);
//		return response;
//	}
//	
	@GetMapping("/verify/{token}")
	public String verifyUser(@PathVariable String token) {
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
			return "Email berhasil diverifikasi! Kamu sudah dapat login, namun masih harus menunggu verifikasi data diri maksimal 1 x 24 jam.";
		}
	}
	
	@PostMapping("/verify/resend")
	public String verifyUserResend(@RequestBody User user) {
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
	
	@PostMapping("/forgot")
	public String sendForgotPassowrd(@RequestBody User user) {
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
	
	@GetMapping("/reset/{token}")
	public String getResetPassword(@PathVariable String token) {
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
	
	@PostMapping("/reset/send")
	public String getResetPassword(@RequestBody User user) {
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
	@PutMapping
	public User editUser(@RequestBody User user) {
		User findUser = userRepo.findById(user.getId()).get();
		if (!user.getPassword().equalsIgnoreCase("")) {
			String encodedPassword = pwEncoder.encode(user.getPassword());
			user.setPassword(encodedPassword);
		} else {
			user.setPassword(findUser.getPassword());
		}
		user.setRole(findUser.getRole());
		user.setVerified(findUser.isVerified());
		user.setKyc(findUser.isKyc());
		user.setRejected(findUser.isRejected());
		return userRepo.save(user);
	}
	
	//delete user akan menghapus semua data yg terkait.
	@DeleteMapping("/{id}")
	public void deleteUser(@PathVariable int id) {
		userRepo.deleteById(id);
	}
	
	
	//kyc
	@GetMapping("kyc")
	public Page<User> getKycUsers(Pageable pageable) {
		return userRepo.findAllByRoleIdAndIsVerifiedAndIsRejectedAndIsKyc(4, true, false, false, pageable);
	}
	
	@PostMapping("kyc/accept")
	public String rejectUser(@RequestBody User user ) {
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
	
	@PostMapping("kyc/reject")
	public String rejectUser(@RequestParam String msg, @RequestBody User user ) {
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
