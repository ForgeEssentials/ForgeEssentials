package com.ForgeEssentials.WorldControl;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Hashtable;

public class WCClassLoader extends ClassLoader {  
        private Hashtable classes = new Hashtable(); //used to cache already defined classes  
        
        public WCClassLoader() {  
            super(WCClassLoader.class.getClassLoader()); //calls the parent class loader's constructor  
        }  
      
        public Class loadClass(String name, InputStream input) throws ClassNotFoundException {
            try {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int data = input.read();

                while(data != -1){
                    buffer.write(data);
                    data = input.read();
                }

                input.close();

                byte[] classData = buffer.toByteArray();

                return defineClass(name,
                        classData, 0, classData.length);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

      
    }  