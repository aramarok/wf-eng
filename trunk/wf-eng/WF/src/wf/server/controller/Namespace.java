package wf.server.controller;

import java.util.StringTokenizer;

public class Namespace {

    public static Namespace getNamespace(String nsStr) {
	StringTokenizer strtok = new StringTokenizer(nsStr, "=");
	Namespace ns = new Namespace();
	ns.alias = strtok.nextToken();
	ns.uri = strtok.nextToken();
	return ns;
    }

    public String alias;

    public String uri;
}
