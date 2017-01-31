package com.seeyon.v3x.doc.util.compress;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 压缩工具，可以将文件（夹）压缩为zip格式
 */
public class CompressUtil {	
	public static File zip(String zipName, List<File> files) throws Exception{
		String zipFileName = zipName + ".zip";// 打包后文件名字
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
		zip(out, "", files);
		try{
			out.close();
		}catch(Exception e) {}
		return new File(zipFileName);
	}
	
	private static void zip(ZipOutputStream out, String base, List<File> files) throws Exception {
		for(File f:files) {
			if (f.isDirectory()) {
				File[] fl = f.listFiles();
				if(base.length() == 0)
					base = f.getName();
				out.putNextEntry(new ZipEntry(base + "/"));
				base = base.length() == 0 ? "" : base + "/";
				for (int i = 0; i < fl.length; i++) {
					List<File> file = new ArrayList<File>();
					file.add(fl[i]);
					zip(out, base + fl[i].getName(), file);
				}
			} else {
				out.putNextEntry(new ZipEntry(base.length() == 0 ? f.getName() : base));
				FileInputStream in = new FileInputStream(f);
				int b;
//				System.out.println("compressing base -- " + (base.length() == 0 ? f.getName() : base));
				while ((b = in.read()) != -1) {
					out.write(b);
				}
				try{
				in.close();
				}catch(Exception e) {}
			}
		}
	}

}
