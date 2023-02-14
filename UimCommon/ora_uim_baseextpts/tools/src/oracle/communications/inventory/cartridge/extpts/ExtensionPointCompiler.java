/*
* Copyright (c) 2007, 2013, Oracle and/or its affiliates. All rights reserved. 
* Oracle Corporation and/or its affiliates. Other names may be trademarks of their respective owners.
*/

package oracle.communications.inventory.cartridge.extpts;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ExtensionPointCompiler {

	public static void main(String[] args) throws IOException
	{
		File epsFile = new File(args[0]);
		File outDir = new File(args[1]);
		BufferedReader in = new BufferedReader(new FileReader(epsFile));
		String line;
		while ((line = in.readLine()) != null)
		{
			line = line.trim();
			if (line.equals("")) continue;
			int x = line.indexOf('#');
			if (x == -1) continue;
			int y = line.lastIndexOf('#');
			if (y == x) continue;
			String name = line.substring(0, x);
			String namefl = name.replace('.', '_');
			String desc = line.substring(x + 1, y);
			String sign = line.substring(y + 1);
			File outFile = new File(outDir, namefl + ".rstp");
			BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
			out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			out.newLine();
			out.write("<com:modelEntity xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:com=\"http://www.mslv.com/studio/core/model/common\" xmlns:inv=\"http://www.mslv.com/studio/inventory/model/specification\" xmlns=\"http://www.mslv.com/studio/inventory/model/specification\" xsi:type=\"inv:RuleTriggerPointType\" name=\"" + namefl + "\">");
			out.newLine();
			out.write("  <inv:signature>" + sign + "</inv:signature>");
			out.newLine();
			out.write("  <inv:pointName>" + name + "</inv:pointName>");
			out.newLine();
			out.write("  <inv:description>" + desc + "</inv:description>");
			out.newLine();
			out.write("</com:modelEntity>");
			out.newLine();
			out.close();
		}
		in.close();
	}
}
