package com.thomasariyanto.octofund.projection;

public interface TransactionStatistic {
	int getId();
	String getName();
	int getCountTransaction();
	double getTotalUnit();
	long getTotalTransaction();
}
