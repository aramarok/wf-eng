package wf.server.controller;

import java.util.*;

public class Namespace {

	public String alias;
	public String uri;

	public static Namespace getNamespace(String nsStr) {
		StringTokenizer strtok = new StringTokenizer(nsStr, "=");
		Namespace ns = new Namespace();
		ns.alias = strtok.nextToken();
		ns.uri = strtok.nextToken();
		return ns;
	}
}
