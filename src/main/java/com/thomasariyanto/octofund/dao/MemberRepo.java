package com.thomasariyanto.octofund.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.thomasariyanto.octofund.entity.Member;
import com.thomasariyanto.octofund.projection.TransactionStatistic;

public interface MemberRepo extends JpaRepository<Member, Integer> {
	@Query(value = "SELECT m.user_id as id, m.identity_name as name, "
			+ "sum(t.total_unit) as totalUnit, "
			+ "count(t.id) as countTransaction, "
			+ "sum(t.total_price) as totalTransaction FROM member m "
			+ "join transaction t on m.user_id = t.member_id "
			+ "where t.type = ?1 group by m.user_id order by totalTransaction DESC", nativeQuery = true)
	public List<TransactionStatistic> getStatistics(int typeId);
}
