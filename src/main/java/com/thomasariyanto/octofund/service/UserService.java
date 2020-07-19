package com.thomasariyanto.octofund.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.thomasariyanto.octofund.entity.Manager;
import com.thomasariyanto.octofund.entity.Member;
import com.thomasariyanto.octofund.entity.User;
import com.thomasariyanto.octofund.projection.TransactionStatistic;

public interface UserService {
	public Iterable<User> getUsers();
	
	public User getUserById(int id);
	
	public Page<User> getUsersByRole(int roleId, Pageable pageable);

	public List<TransactionStatistic> getMemberStatistics(int type);
	
	public User registerAdmin(User user);

	public User registerManager(Manager manager);
	
	public User registerMember(Member member);
	
	public User loginUser(User user);
	
	public Map<String, String> uploadPhoto(MultipartFile file);
	
	public HashMap<String, String> uploadIdentityPhotoOCR(MultipartFile file);
	
	public ResponseEntity<Object> downloadFile(String type, String fileName);
	
	public User editManagerLogo(MultipartFile file, int id);
	
	public ResponseEntity<Object> downloadLogo(String fileName);
	
	public String verifyUser(String token);
	
	public String verifyUserResend(User user);
	
	public String sendForgotPassowrd(User user);
	
	public String getResetPassword(String token);
	
	public String changePassword(User user);
	
	public User editUser(User user);
	
	public User editUserAuth(User user, String oldPassword);
	
	public void deleteUser(int id);
	
	public Page<User> getKycUsers(Pageable pageable);
	
	public String acceptUser(User user);
	
	public String rejectUser(String msg, User user);
}
