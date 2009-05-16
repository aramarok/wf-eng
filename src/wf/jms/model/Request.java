package wf.jms.model;

import java.io.Serializable;

import wf.client.auth.User;

public abstract class Request implements Serializable {

	public String replyName;
	public User user;

	public Response service() {
		throw new RuntimeException(
				"The method is supposed to be overriden or Handler "
						+ "for the request[" + this + "] should exist");
	};

}
