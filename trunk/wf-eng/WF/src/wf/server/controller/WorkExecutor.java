package wf.server.controller;

import wf.db.Persistence;

public class WorkExecutor {

	public void execute(IBatisWork work) throws Exception {
		work.execute(Persistence.getThreadSqlMapSession());
	}

}
