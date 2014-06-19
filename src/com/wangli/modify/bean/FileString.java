
package com.wangli.modify.bean;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileString {

    public FileString(File file, String str, List<String> strList) {
        this.file = file;
        this.str = str;
        this.strList = strList;
    }

    public File file;// 文件
    public String str;// 文件对应的字符串
    public List<String> strList = new ArrayList<String>();// 符合规则的字符串数组
    public HashMap<String,String> strNameMap = new HashMap<String,String>();// 符合规则的字符串数组对应的strings.xml中字符串名称数组。key为name，value为修改的字符串。

}
