package com.mobile.madassignment.util;

import java.util.HashMap;

/**
 * Created by lq on 09/06/2017.
 */

public class UserNameMap {
    public static HashMap<String, String> userId_nameMap = new HashMap<>();

    public static HashMap<String, String> getUserId_nameMap() {
        return userId_nameMap;
    }

    public static void setUserId_nameMap(HashMap<String, String> userId_nameMap) {
        UserNameMap.userId_nameMap = userId_nameMap;
    }
}
