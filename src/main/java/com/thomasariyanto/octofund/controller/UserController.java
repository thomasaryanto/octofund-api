package com.thomasariyanto.octofund.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.thomasariyanto.octofund.dao.ManagerRepo;
import com.thomasariyanto.octofund.dao.MemberRepo;
import com.thomasariyanto.octofund.dao.RoleRepo;
import com.thomasariyanto.octofund.dao.UserRepo;
import com.thomasariyanto.octofund.entity.Manager;
import com.thomasariyanto.octofund.entity.Member;
import com.thomasariyanto.octofund.entity.User;

@RestController
@RequestMapping("/users")
@CrossOrigin
public class UserController {
private PasswordEncoder pwEncoder = new BCryptPasswordEncoder();
	
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private RoleRepo roleRepo;
	
	@Autowired
	private ManagerRepo managerRepo;
	
	@Autowired
	private MemberRepo memberRepo;
	
	@GetMapping("/{id}")
	public User getUserById(@PathVariable int id) {
		return userRepo.findById(id).get();
	}
	
	@PostMapping("/staff")
	public User registerStaff(@Valid @RequestBody User user) {
		userRepo.save(user);
		
		String encodedPassword = pwEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
		user.setRole(roleRepo.findById(2).get());
		userRepo.save(user);
		
		return userRepo.findById(user.getId()).get();
	}
	
	@PostMapping("/manager")
	public User registerManager(@Valid @RequestBody Manager manager) {
		managerRepo.save(manager);
		
		String encodedPassword = pwEncoder.encode(manager.getUser().getPassword());
		manager.getUser().setPassword(encodedPassword);
		manager.getUser().setRole(roleRepo.findById(3).get());
		managerRepo.save(manager);
		
		return userRepo.findById(manager.getUser().getId()).get();
	}
	
	@PostMapping("/member")
	public User registerMember(@Valid @RequestBody Member member) {
		//disave dulu biar masuk validation
		memberRepo.save(member);

		//encrypt password dan set role
		String encodedPassword = pwEncoder.encode(member.getUser().getPassword());
		member.getUser().setPassword(encodedPassword);
		member.getUser().setRole(roleRepo.findById(4).get());
		memberRepo.save(member);
		
		return userRepo.findById(member.getUser().getId()).get();
		
	}
	
	@PostMapping("/login")
	public User loginUser(@RequestBody User user) {
		if(userRepo.existsByEmail(user.getEmail())) {
			User findUser = userRepo.findByEmail(user.getEmail()).get();
			
			if(pwEncoder.matches(user.getPassword(), findUser.getPassword())) {
				return findUser;
			}
			else {
				throw new RuntimeException("Password salah!");
			}
		}
		else {
			throw new RuntimeException("User tidak ditemukan!");
		}
	}
	
	@GetMapping
	public Iterable<User> getsers() {
		return userRepo.findAll();
	}
	
}
