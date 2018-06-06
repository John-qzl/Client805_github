package com.example.navigationdrawertest.model;

import org.litepal.crud.DataSupport;

/**
 * Created by john on 2018/6/4.
 */

public class Rows extends DataSupport{
    private String rowsid;
    private String taskid;
    private String rowsnumber;

    public String getRowsid() {
        return rowsid;
    }

    public void setRowsid(String rowsid) {
        this.rowsid = rowsid;
    }

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

    public String getRowsnumber() {
        return rowsnumber;
    }

    public void setRowsnumber(String rowsnumber) {
        this.rowsnumber = rowsnumber;
    }
}
