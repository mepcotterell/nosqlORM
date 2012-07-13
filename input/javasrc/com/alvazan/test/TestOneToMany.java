package com.alvazan.test;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alvazan.orm.api.base.NoSqlEntityManager;
import com.alvazan.orm.api.base.NoSqlEntityManagerFactory;
import com.alvazan.test.db.Account;
import com.alvazan.test.db.Activity;
import com.alvazan.test.db.SomeEntity;

public class TestOneToMany {

	private static final String ACCOUNT_NAME = "declan";
	private static NoSqlEntityManagerFactory factory;
	private NoSqlEntityManager mgr;

	@BeforeClass
	public static void setup() {
		factory = FactorySingleton.createFactoryOnce();
	}
	
	@Before
	public void createEntityManager() {
		mgr = factory.createEntityManager();
	}
	@After
	public void clearDatabase() {
		NoSqlEntityManager other = factory.createEntityManager();
		other.clearDbAndIndexesIfInMemoryType();
	}

	@Test
	public void testOneToManyWithMap() {
		Activity act1 = new Activity();
		act1.setName("dean");
		act1.setNumTimes(3);
		mgr.put(act1);
		Activity act2 = new Activity();
		act2.setName("dean2");
		act2.setNumTimes(4);
		mgr.put(act2);
		
		SomeEntity entity = new SomeEntity();
		entity.setName("asdf");
		entity.putActivity(act1);
		entity.putActivity(act2);
		mgr.put(entity);
		
		mgr.flush();
		
		SomeEntity result = mgr.find(SomeEntity.class, entity.getId());
		
		Activity resAct1 = result.getActivity(act1.getName());
		Assert.assertEquals(act1.getNumTimes(), resAct1.getNumTimes());
		
		Activity resAct2 = result.getActivity(act2.getName());
		Assert.assertEquals(act2.getNumTimes(), resAct2.getNumTimes());
	}
	
	@Test
	public void testOneToManyWithList() {
		Account acc = new Account();
		acc.setName(ACCOUNT_NAME);
		acc.setUsers(5.0f);
		mgr.fillInWithKey(acc);
		
		Activity act1 = new Activity();
		act1.setAccount(acc);
		act1.setName("dean");
		act1.setNumTimes(3);
		
		mgr.put(act1);
		
		Activity act2 = new Activity();
		act2.setName("dean");
		act2.setNumTimes(4);
		
		mgr.put(act2);
		
		acc.addActivity(act1);
		acc.addActivity(act2);
		mgr.put(acc);
		
		mgr.flush();
		
		Account accountResult = mgr.find(Account.class, acc.getId());
		Assert.assertEquals(ACCOUNT_NAME, accountResult.getName());
		Assert.assertEquals(acc.getUsers(), accountResult.getUsers());
		List<Activity> activities = accountResult.getActivities();
		Assert.assertEquals(2, activities.size());
		
		//Now let's force proxy creation by getting one of the Activities
		Activity activity = activities.get(0);
		
		//This should NOT hit the database since the id is wrapped by the proxy and exists already
		String id = activity.getId();
		//since we added activity1 first, we better see that same activity be first in the list again...
		Assert.assertEquals(act1.getId(), id);
		
		//Now let's force a database lookup to have the activity filled in
		Assert.assertEquals("dean", activity.getName());
	}

}