package com.seeyon.v3x.portal.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class DeepCopy {
	 public static List copyBySerialize(List src) throws IOException, ClassNotFoundException{
	        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
	        ObjectOutputStream out = new ObjectOutputStream(byteOut);
	        out.writeObject(src);
	    
	        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
	        ObjectInputStream in =new ObjectInputStream(byteIn);
	        List dest = (List)in.readObject();
	        return dest;
	 }
}
