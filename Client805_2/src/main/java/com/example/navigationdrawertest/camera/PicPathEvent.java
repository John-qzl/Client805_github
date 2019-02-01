package com.example.navigationdrawertest.camera;

import java.util.ArrayList;

/**
 * Created by qiaozhili on 2019/1/30 9:36.
 */
public class PicPathEvent {
    ArrayList<String> pathList;

    public PicPathEvent(ArrayList<String> data)
    {
        pathList = new ArrayList<String>();
        if (data!=null && data.size()>0) {
            pathList.addAll(data);
        }
    }

    public ArrayList<String> getPathList(){
        return pathList;
    }
}
