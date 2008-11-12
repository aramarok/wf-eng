
package wf.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import wf.exceptions.XflowException;
import wf.model.DirectedGraph;
import wf.xml.XflowXMLParser;


public class Validation {

	public static void main(String[] args) {
		try {
			BufferedReader fin =
				new BufferedReader(new FileReader("d:\\eclipse\\workspace\\" +					"XFlow\\src\\xflow\\test\\testRule.xml"));
			String xml = "";
			String s = "";
			while ((s = fin.readLine()) != null) {
				s+="\n";
				xml += s;
			}
			System.out.println(xml);
			try {
				DirectedGraph g = XflowXMLParser.parse(xml);
				g.validate();
			} catch (XflowException e) {
				e.printStackTrace();
			} 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
