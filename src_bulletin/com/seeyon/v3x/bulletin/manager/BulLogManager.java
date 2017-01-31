package com.seeyon.v3x.bulletin.manager;

import java.util.List;

import com.seeyon.v3x.bulletin.domain.BulLog;

public interface BulLogManager {
	public void record(BulLog log);
	public List<BulLog> findAll();
	public List<BulLog> findByExample(BulLog log);
}
