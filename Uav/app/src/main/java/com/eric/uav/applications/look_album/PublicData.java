package com.eric.uav.applications.look_album;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PublicData {
    public static List<ConvertFile> files = new ArrayList<>();
    public static Map<Long, List<ConvertFile>> fileMap = new ConcurrentHashMap<>(); // 每天都对对应很多个文件，使用map将每天跟每天的文件对应起来
    public static List<Long> keys = new LinkedList<>();   // 所有时间节点的集合
}
