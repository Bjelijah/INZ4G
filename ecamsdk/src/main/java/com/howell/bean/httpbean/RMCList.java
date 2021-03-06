package com.howell.bean.httpbean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/4/25.
 */

public class RMCList {
    Page page;
    ArrayList<RMC> RMCs;

    @Override
    public String toString() {
        return "RMCList{" +
                "page=" + page +
                ", RMCs=" + RMCs +
                '}';
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public ArrayList<RMC> getRMCs() {
        return RMCs;
    }

    public void setRMCs(ArrayList<RMC> RMCs) {
        this.RMCs = RMCs;
    }

    public RMCList() {

    }

    public RMCList(Page page, ArrayList<RMC> RMCs) {

        this.page = page;
        this.RMCs = RMCs;
    }
}
