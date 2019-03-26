package com.example.navigationdrawertest.model;

import org.litepal.crud.DataSupport;

/**
 * Created by john on 2018/6/4.
 */

public class Rows extends DataSupport{
    private String rowsid; //rowsid
    private String taskid; //taskid
    private String rowsnumber; //rows中的行数
    private Long timeL;				//表单复制时的时间戳

    public Long getTimeL() {
        return timeL;
    }

    public void setTimeL(Long timeL) {
        this.timeL = timeL;
    }

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
