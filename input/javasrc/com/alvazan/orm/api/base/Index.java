package com.alvazan.orm.api.base;

public interface Index<T> {

	public Query<T> getNamedQuery(String name);
	
	public Query<T> getNamedQueryJoin(String name, JoinInfo... info);
	
}
