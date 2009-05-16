package wf.server.controller;

import com.ibatis.sqlmap.client.SqlMapClient;

public abstract class IBatisWork {

	protected Object result;

	public Object getResult() {
		return result;
	}

	public abstract void execute(SqlMapClient sqlMap) throws Exception;

}
