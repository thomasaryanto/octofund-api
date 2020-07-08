package com.thomasariyanto.octofund.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thomasariyanto.octofund.entity.MutualFund;

public interface MutualFundRepo extends JpaRepository<MutualFund, Integer> {

}
