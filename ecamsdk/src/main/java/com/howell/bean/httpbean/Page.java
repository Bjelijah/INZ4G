package com.howell.bean.httpbean;

/**
 * Created by Administrator on 2017/4/7.
 */

public class Page {
    int pageIndex;
    int pageSize;
    int pageCount;
    int recordCount;
    int totalRecordCount;

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public int getTotalRecordCount() {
        return totalRecordCount;
    }

    public void setTotalRecordCount(int totalRecordCount) {
        this.totalRecordCount = totalRecordCount;
    }

    public Page(int pageIndex, int pageSize, int pageCount, int recordCount, int totalRecordCount) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.pageCount = pageCount;
        this.recordCount = recordCount;
        this.totalRecordCount = totalRecordCount;
    }

    public Page() {
    }

    @Override
    public String toString() {
        return "Page{" +
                "pageIndex=" + pageIndex +
                ", pageSize=" + pageSize +
                ", pageCount=" + pageCount +
                ", recordCount=" + recordCount +
                ", totalRecordCount=" + totalRecordCount +
                '}';
    }
}
