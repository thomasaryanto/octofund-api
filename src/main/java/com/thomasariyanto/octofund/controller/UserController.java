package com.thomasariyanto.octofund.controller;

import java.util.HashMap;
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
import com.thomasariyanto.octofund.entity.Manager;
import com.thomasariyanto.octofund.entity.Member;
import com.thomasariyanto.octofund.entity.User;
import com.thomasariyanto.octofund.projection.TransactionStatistic;
import com.thomasariyanto.octofund.service.UserService;

@RestController
@RequestMapping("/users")
@CrossOrigin
public class UserController {

	@Autowired
	private UserService userService;
	
	@GetMapping
	public Iterable<User> getUsers() {
		return userService.getUsers();
	}
	
	@GetMapping("/{id}")
	public User getUserById(@PathVariable int id) {
		return userService.getUserById(id);
	}
	
	@GetMapping("/role/{roleId}")
	public Page<User> getUsersByRole(@PathVariable int roleId, Pageable pageable) {
		return userService.getUsersByRole(roleId, pageable);
	}
	
	@GetMapping("/member/statistics")
	public List<TransactionStatistic> getMemberStatistics(@RequestParam(value="type", defaultValue="1") int type) {
		return userService.getMemberStatistics(type);
	}
	
	@PostMapping("/admin")
	public User registerAdmin(@Valid @RequestBody User user) {
		return userService.registerAdmin(user);
	}
	
	@PostMapping("/manager")
	public User registerManager(@Valid @RequestBody Manager manager) {
		return userService.registerManager(manager);
	}
	
	@PostMapping("/member")
	public User registerMember(@Valid @RequestBody Member member) {	
		return userService.registerMember(member);
	}
	
	@PostMapping("/login")
	public User loginUser(@RequestBody User user) {
		return userService.loginUser(user);
	}
	
	@PostMapping("/upload/")
	public Map<String, String> uploadPhoto(@RequestParam("file") MultipartFile file) {
		return userService.uploadPhoto(file);
	}
	
	@PostMapping("/upload/ktp")
	public HashMap<String, String> uploadIdentityPhotoOCR(@RequestParam("file") MultipartFile file) {
		return userService.uploadIdentityPhotoOCR(file);
	}	
	
	@GetMapping("/image/{type}/{fileName:.+}")
	public ResponseEntity<Object> downloadFile(@PathVariable String type, @PathVariable String fileName){
		return userService.downloadFile(type, fileName);
	}
	
	@PostMapping("/upload/manager/{id}")
	public User editManagerLogo(@RequestParam("file") MultipartFile file, @PathVariable int id) {
		return userService.editManagerLogo(file, id);
	}
	
	@GetMapping("/manager/logo/{fileName:.+}")
	public ResponseEntity<Object> downloadLogo(@PathVariable String fileName){
		return userService.downloadLogo(fileName);
	}
	
	@GetMapping("/verify/{token}")
	public String verifyUser(@PathVariable String token) {
		return userService.verifyUser(token);
	}
	
	@PostMapping("/verify/resend")
	public String verifyUserResend(@RequestBody User user) {
		return userService.verifyUserResend(user);
	}
	
	@PostMapping("/forgot")
	public String sendForgotPassowrd(@RequestBody User user) {
		return userService.sendForgotPassowrd(user);
	}
	
	@GetMapping("/reset/{token}")
	public String getResetPassword(@PathVariable String token) {
		return userService.getResetPassword(token);
	}
	
	@PostMapping("/reset/send")
	public String changePassword(@RequestBody User user) {
		return userService.changePassword(user);
	}
	
	//edit user harus ngirim password juga waaupun kosong.
	//edit user dari admin
	@PutMapping
	public User editUser(@RequestBody User user) {
		return userService.editUser(user);
	}
	
	//edit user sendiri
	@PutMapping("/edit")
	public User editUserAuth(@RequestBody User user, @RequestParam(value="oldPassword", defaultValue="") String oldPassword) {
		return userService.editUserAuth(user, oldPassword);
	}
	
	@GetMapping("kyc")
	public Page<User> getKycUsers(Pageable pageable) {
		return userService.getKycUsers(pageable);
	}
	
	@PostMapping("kyc/accept")
	public String acceptUser(@RequestBody User user ) {
		return userService.acceptUser(user);
	}
	
	@PostMapping("kyc/reject")
	public String rejectUser(@RequestParam String msg, @RequestBody User user ) {
		return userService.rejectUser(msg, user);
	}
	
	//delete user akan menghapus semua data yg terkait.
	@DeleteMapping("/{id}")
	public void deleteUser(@PathVariable int id) {
		userService.deleteUser(id);
	}
}
