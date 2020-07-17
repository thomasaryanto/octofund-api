package com.thomasariyanto.octofund.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.thomasariyanto.octofund.entity.MutualFund;
import com.thomasariyanto.octofund.projection.TransactionStatistic;

public interface MutualFundRepo extends JpaRepository<MutualFund, Integer> {
	public Page<MutualFund> findAllByNameContaining(String name, Pageable pageable);
	public Page<MutualFund> findAllByNameContainingAndMutualFundCategoryId(String name, int categoryId, Pageable pageable);
	public Page<MutualFund> findAllByNameContainingAndLastPriceBetween(String name, double minPrice, double maxPrice, Pageable pageable);
	public List<MutualFund> findByManagerId(int managerId);
	public Page<MutualFund> findAllByManagerId(int managerId, Pageable pageable);
	
	@Query(value = "SELECT m.id, m.name, "
			+ "sum(t.total_unit) as totalUnit, "
			+ "count(t.id) as countTransaction, "
			+ "sum(t.total_price) as totalTransaction "
			+ "FROM mutual_fund m join transaction t on m.id = t.product_id "
			+ "where t.type = ?1 group by m.id order by totalTransaction DESC", nativeQuery = true)
	public List<TransactionStatistic> getStatistics(int typeId);
}
