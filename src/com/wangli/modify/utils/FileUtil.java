
package com.wangli.modify.utils;

import com.wangli.modify.bean.FileString;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class FileUtil {

    public static void writeStringsInFile(File file, String str) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(file, false);
            fw.write(str);
            fw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void appendXmlElement(String xmlPath, FileString fileString) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlPath);
            doc.normalize();
            Set<String> keySet = fileString.strNameMap.keySet();
            for (String name : keySet) {
                Element link = doc.createElement("string");
                link.setAttribute("name", name);
                String value = fileString.strNameMap.get(name);
                // 将value的前后双引号去掉
                value = value.substring(1, value.length() - 1);
                link.setTextContent(value);
                doc.getDocumentElement().appendChild(link);
            }

            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(xmlPath));
            transformer.transform(source, result);
        } catch (Exception e) {
        }
    }

    public static List<FileString> getFileStrings(List<File> fileList, String regex) {
        List<FileString> fileStringList = new ArrayList<FileString>();
        for (File file : fileList) {
            if (file != null) {
                try {
                    // 读取文件的内容，将所有的字符串添加到sb。
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    StringBuilder sb = new StringBuilder();
                    int len;
                    while ((len = br.read()) != -1) {
                        sb.append((char) len);
                    }
                    br.close();

                    // 检测sb里面的字符串，是否有符合规则的。将符合规则的字符串加到list
                    String str = sb.toString();
                    if (str != null && !"".equals(str.trim())) {
                        Pattern pattern = Pattern.compile(regex);
                        Matcher matcher = pattern.matcher(str);
                        List<String> strList = new ArrayList<String>();
                        while (matcher.find()) {
                            String value = matcher.group();
                            if(value!=null&&!"".equals(value.trim())&&containsChinese(value)){
                                strList.add(value);
                            }
                        }
                        if (strList.size() > 0 && matcher != null) {
                            FileString fileString = new FileString(file, str, strList);
                            fileStringList.add(fileString);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return fileStringList;
    }

    /**
     * 判断字符串中是否包含多字节
     * @param value
     * @return
     */
    private static boolean containsChinese(String value) {
        boolean result = false;
        if(value.getBytes().length==value.length()){
            result = false;
        }else{
            result = true;
        }
        return result;
    }

    public static List<File> getAllFiles(String...str) {
        List<File> fileList = new ArrayList<File>();
        for(String path:str){
            File dir = new File(path);
            insertFileToList(dir, fileList);  
        }
        return fileList;
    }

    private static void insertFileToList(File dir, List<File> fileList) {
        if (dir != null && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    if (file != null) {
                        if (file.isDirectory()) {
                            insertFileToList(file, fileList);
                        } else {
                            fileList.add(file);
                        }
                    }
                }
            }
        }
    }

    public static void appendFile(String filePath, String content, boolean append) {
        File file = new File(filePath);
        FileWriter fw = null;
        if (file == null || !file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            String str = readFile(filePath);
            if(!str.contains(content)){
                fw = new FileWriter(file, append);
                fw.write(content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String readFile(String filePath) {
        File file = new File(filePath);
        BufferedReader br;
        StringBuilder sb = null;
        String str = null;
        if (file != null && file.exists()) {
            try {
                br = new BufferedReader(new FileReader(file));
                sb = new StringBuilder();
                while ((str = br.readLine()) != null) {
                    sb.append(str);
                }
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 检测sb里面的字符串，是否有符合规则的。将符合规则的字符串加到list
            str = sb.toString().trim();
        }
        return str;
    }
    
    public static void deleteFile(String filePath){
        File file = new File(filePath);
        if(file!=null&&file.exists()){
            file.delete();
        }
    }
}
