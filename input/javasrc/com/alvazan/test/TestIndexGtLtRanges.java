package com.alvazan.test;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alvazan.orm.api.base.Index;
import com.alvazan.orm.api.base.NoSqlEntityManager;
import com.alvazan.orm.api.base.NoSqlEntityManagerFactory;
import com.alvazan.test.db.Activity;

public class TestIndexGtLtRanges {

	private static Index<Activity> index;

	private static NoSqlEntityManagerFactory factory;
	private NoSqlEntityManager mgr;

	@BeforeClass
	public static void setup() {
		factory = FactorySingleton.createFactoryOnce();
	}
	
	@Before
	public void createEntityManager() {
		mgr = factory.createEntityManager();
		index = setupRecords();
	}
	@After
	public void clearDatabase() {
		NoSqlEntityManager other = factory.createEntityManager();
		other.clearDbAndIndexesIfInMemoryType();
	}

	@Test
	public void testBasicString() {
		//TODO: test some ranges out here
	}

	private Index<Activity> setupRecords() {
		Activity act = new Activity();
		act.setName("hello");
		act.setMyFloat(5.65f);
		act.setUniqueColumn("notunique");
		act.setNumTimes(5);
		act.setIsCool(true);
		mgr.put(act);
		
		Index<Activity> index = mgr.getIndex(Activity.class, "/activity/byaccount/account1");
		index.addToIndex(act);
		
		mgr.flush();
		return index;
	}

}