package edu.java.eams.comm;

public class PageParameter {
    private int pageNum = 1; // 当前页码
    private int pageSize = 10; // 每页条数

    public int getPageNum() {
        return pageNum;
    }
    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }
    public int getPageSize() {
        return pageSize;
    }
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
} 