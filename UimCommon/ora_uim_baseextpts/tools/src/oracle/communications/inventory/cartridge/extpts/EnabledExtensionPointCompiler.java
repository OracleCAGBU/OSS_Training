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

public class EnabledExtensionPointCompiler {

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
			int x = line.indexOf(',');
			if (x == -1) continue;
			String className = line.substring(0, x);
			String epname = line.substring(x + 1);
			String epnamefl = epname.replace('.', '_');
			int y = className.lastIndexOf('.');
			String classNickName = className.substring(y + 1);
			String name = classNickName + "__" + epnamefl;
			File outFile = new File(outDir, name + ".rstpscope");
			BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
			out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			out.newLine();
			out.write("<com:modelEntity xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:com=\"http://www.mslv.com/studio/core/model/common\" xmlns:inv=\"http://www.mslv.com/studio/inventory/model/specification\" xmlns=\"http://www.mslv.com/studio/inventory/model/specification\" xsi:type=\"inv:RuleTriggerPointScopeType\" name=\"" + name + "\">");
			out.newLine();
			out.write("  <inv:className>" + className + "</inv:className>");
			out.newLine();
			out.write("  <inv:ruleTriggerPoint>");
			out.newLine();
			out.write("    <com:entity>" + epnamefl + "</com:entity>");
			out.newLine();
			out.write("    <com:entityType>rstp</com:entityType>");
			out.newLine();
			out.write("    <com:relationship>com.mslv.studio.inventory.ruleset.point.scope.REL_POINT</com:relationship>");
			out.newLine();
			out.write("  </inv:ruleTriggerPoint>");
			out.newLine();
			out.write("</com:modelEntity>");
			out.newLine();
			out.close();
		}
		in.close();
	}
}
