
package com.wangli.modify;

import com.wangli.modify.bean.FileString;
import com.wangli.modify.utils.FileUtil;
import com.wangli.modify.utils.Plan;
import com.wangli.modify.utils.StringUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BatchModifyStringsMainClass {

    //TODO:布局文件
    public static final String path = "E:\\workspace\\browser_6.1.5_international";// E:\\workspace\\demo
                                                            // E:\\workspace\\browser_6.1.5_international
    public static final String javaPath = path + "\\src";
    public static final String layoutXmlPath = path+"\\res\\layout";
    public static final String stringXmlPath = path + "\\res\\values\\strings.xml";
    public static final String regex ="\"[^\"]*\"";// "\"(.*)\""

    public static final int DEFAULT_PLAN = Plan.fourth.getNumber();// 当用户直接输入回车，默认选中第一个

    private static final String statusPath = "status.txt";
    private static final String SEPARATOR_FILEPATH = ";";
    private static boolean isReadStatus = false;
    private static List<String> filePathList = null;// 保存从文件中读取的内容，内容是之前读取过的文件的路径。

    public static void main(String[] args) {

        readStatus();

        // 获取所有的文件（不包括文件夹）
        List<File> allFiles = FileUtil.getAllFiles(javaPath,layoutXmlPath);

        // 获取所有匹配的文件中的字符串
        List<FileString> fileStringList = FileUtil.getFileStrings(allFiles, regex);

        if (fileStringList == null || fileStringList.size() == 0) {
            System.out.println("未找到匹配的字符串");
        }

        // 遍历文件，提示用户进行修改
        modifyStringsInFile(fileStringList, stringXmlPath);
    }

    private static void readStatus() {
        System.out.println("********是否需要从上次读取的文件开始读？(Y/N)********");
        String command = null;
        while (true) {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            try {
                command = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (command != null && !"".equals(command.trim())) {
                command = command.toLowerCase();
                if (command.trim().equals("y") || command.trim().equals("yes")) {
                    isReadStatus = true;
                    filePathList = new ArrayList<String>();
                    String str = FileUtil.readFile(statusPath);
                    if(str!=null&&!"".equals(str.trim())){
                        String[] strs = str.split(SEPARATOR_FILEPATH);
                        filePathList = Arrays.asList(strs);
                    }
                    break;
                } else if (command.trim().equals("n") || command.trim().equals("no")) {
                    isReadStatus = false;
                    FileUtil.deleteFile(statusPath);
                    break;
                } else {
                    System.out.println("输入的字符不符合要求，请重新输入");
                }
            } else {
                System.out.println("不能输入空字符，请重新输入");
            }
        }
    }

    private static void modifyStringsInFile(List<FileString> fileStringList, String stringXmlPath) {
        boolean isDeal = true;
        for (FileString fileString : fileStringList) {
            if (isReadStatus && filePathList != null) {
                if (filePathList.contains(fileString.file.getPath())) {
                    isDeal = false;
                } else {
                    isDeal = true;
                }
            } else {
                isDeal = true;
            }
            if (isDeal) {
                System.out.println("********" + fileString.file.getName() + "开始处理********");
                if (fileString.strList.size() > 0) {
                    for (int i = 0; i < fileString.strList.size(); i++) {
                        String str = fileString.strList.get(i);
                        System.out.println("********匹配到如下字符********");
                        System.out.println(str + "......filename......"
                                + fileString.file.getName());
                        System.out.println("提供三种方案，请选择数字并按回车：");
                        System.out.println(Plan.first.toString());
                        System.out.println(Plan.second.toString());
                        System.out.println(Plan.third.toString());
                        System.out.println(Plan.fourth.toString());
                        System.out.println(Plan.fifth.toString());
                        int num = getNum();
                        String strName = getStringName(num);

                        // 处理方案
                        if (num == Plan.fourth.getNumber()) {
                            System.out.println("忽略");
                        } else if (num == Plan.fifth.getNumber()) {
                            System.out.println("跳出当前文件");
                            break;
                        } else {
                            fileString.strNameMap.put(strName, str);
                            if (num == Plan.first.getNumber()) {
                                fileString.str = fileString.str.replaceAll(str,
                                        Plan.first.getReplace(strName));
                            } else if (num == Plan.second.getNumber()) {
                                fileString.str = fileString.str
                                        .replaceAll(str, Plan.second.getReplace(strName));
                            } else if (num == Plan.third.getNumber()) {
                                fileString.str = fileString.str
                                        .replaceAll(str, Plan.third.getReplace(strName));
                            }
                        }
                    }
                    writeStrInFile(fileString);
                    saveOperateStatus(fileString);
                    appendStrInStringsXml(fileString, stringXmlPath);
                }
                System.out.println("********" + fileString.file.getName() + "处理完成********");
            }
        }
        System.out.println("********所有字符串处理完成********");
    }

    /**
     * 记录当前读取到的文件
     * 
     * @param fileString
     */
    private static void saveOperateStatus(FileString fileString) {
        FileUtil.appendFile(statusPath, fileString.file.getPath().trim() + SEPARATOR_FILEPATH, true);
    }

    /**
     * 追加字符串到strings xml文件。
     * 
     * @param fileString
     * @param stringXmlPath
     */
    private static void appendStrInStringsXml(FileString fileString, String stringXmlPath) {
        FileUtil.appendXmlElement(stringXmlPath, fileString);
    }

    private static void writeStrInFile(FileString fileString) {
        FileUtil.writeStringsInFile(fileString.file, fileString.str);
    }

    private static String getStringName(int num) {
        String strName = null;
        if (num != Plan.fourth.getNumber() && num != Plan.fifth.getNumber()) {
            System.out.println("请输入字符串名称");
            while (true) {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                try {
                    strName = br.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (strName != null && !"".equals(strName.trim())) {
                    break;
                } else {
                    System.out.println("不能输入空字符，请重新输入");
                }
            }
        }
        return strName;
    }

    private static int getNum() {
        int num = 0;
        while (true) {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            try {
                String strIn = br.readLine();
                if (strIn != null && !"".equals(strIn.trim())) {
                    if (StringUtil.isNumeric(strIn)) {
                        num = Integer.valueOf(strIn);
                        if (isBelongPlan(num)) {
                            break;
                        } else {
                            System.out.println("您输入的数字不符合要求，请重新输入");
                        }
                    } else {
                        System.out.println("您输入的不是数字，请重新输入");
                    }
                } else {
                    num = DEFAULT_PLAN;
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return num;
    }

    private static boolean isBelongPlan(int num) {
        boolean result = false;
        for (Plan plan : Plan.values()) {
            if (plan.getNumber() == num) {
                result = true;
                break;
            }
        }
        return result;
    }
}
