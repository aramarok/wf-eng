package wf.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import wf.exceptions.ExceptieWF;
import wf.model.DirectedGraph;
import wf.xml.DefinitionParser;

public class Validation {

	public static void main(String[] args) {
		try {
			BufferedReader fin = new BufferedReader(new FileReader(
					"testRule.xml"));
			String xml = "";
			String s = "";
			while ((s = fin.readLine()) != null) {
				s += "\n";
				xml += s;
			}
			System.out.println(xml);
			try {
				DirectedGraph g = DefinitionParser.parse(xml);
				g.validate();
			} catch (ExceptieWF e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
