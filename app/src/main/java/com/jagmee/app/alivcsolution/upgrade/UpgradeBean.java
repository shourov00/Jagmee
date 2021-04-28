package com.jagmee.app.alivcsolution.upgrade;

public class UpgradeBean {
    private String versionName;//版本名称
    private int versionCode;//版本号
    private String describe;//描述
    private String url;//apk url

    public String getVersionName() { return versionName;}

    public void setVersionName(String versionName) { this.versionName = versionName;}

    public int getVersionCode() { return versionCode;}

    public void setVersionCode(int versionCode) { this.versionCode = versionCode;}

    public String getDescribe() { return describe;}

    public void setDescribe(String describe) { this.describe = describe;}

    public String getUrl() { return url;}

    public void setUrl(String url) { this.url = url;}
}
