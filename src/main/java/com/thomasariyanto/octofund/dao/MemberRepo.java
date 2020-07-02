package com.thomasariyanto.octofund.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thomasariyanto.octofund.entity.Member;

public interface MemberRepo extends JpaRepository<Member, Integer> {

}
