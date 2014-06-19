
package com.wangli.modify.utils;

public enum Plan {

    first {
        @Override
        public int getNumber() {
            return 1;
        }

        @Override
        public String getDes() {
            return "替换字符串为getString（R.string.xxx）";
        }

        @Override
        public String getReplace(String strName) {
            return "getString(R.string." + strName + ")";
        }
    },
    second {
        @Override
        public int getNumber() {
            return 2;
        }

        @Override
        public String getDes() {
            return "替换字符串为Global.mContext.getString（R.string.xxx）";
        }
        
        @Override
        public String getReplace(String strName) {
            return "Global.mContext.getString(R.string." + strName + ")";
        }
    },
    third {
        @Override
        public int getNumber() {
            return 3;
        }

        @Override
        public String getDes() {
            return "替换字符串为@string/xxx";
        }
        @Override
        public String getReplace(String strName) {
            return "\"@string/"+strName+"\"";
        }
    },
    fourth{
        @Override
        public int getNumber() {
            return 4;
        }

        @Override
        public String getDes() {
            return "忽略";
        }
    },
    fifth{
        @Override
        public int getNumber() {
            return 5;
        }

        @Override
        public String getDes() {
            return "跳出当前文件";
        } 
    };
    
    public int getNumber() {
        return 0;
    }

    public String getDes() {
        return "";
    }

    public String getReplace(String strName) {
        return strName;
    }

    @Override
    public String toString() {
        return getNumber() + ":" + getDes();
    }
}
