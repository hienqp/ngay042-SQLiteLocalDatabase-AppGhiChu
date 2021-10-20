package com.hienqp.appghichu;

public class CongViec {
    private int mIdCV;
    private String mTenCV;

    public CongViec(int mIdCV, String mtenCV) {
        this.mIdCV = mIdCV;
        this.mTenCV = mtenCV;
    }

    public int getmIdCV() {
        return mIdCV;
    }

    public void setmIdCV(int mIdCV) {
        this.mIdCV = mIdCV;
    }

    public String getmTenCV() {
        return mTenCV;
    }

    public void setmTenCV(String mtenCV) {
        this.mTenCV = mtenCV;
    }
}
