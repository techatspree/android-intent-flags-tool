package de.akquinet.android.tools.intentflags.util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import android.content.Intent;


public class FlagUtil
{
    private static Map<String, Integer> flagMap =
            new HashMap<String, Integer>();

    static {
        flagMap.put("FLAG_ACTIVITY_BROUGHT_TO_FRONT",
                Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        flagMap.put("FLAG_ACTIVITY_CLEAR_TOP", Intent.FLAG_ACTIVITY_CLEAR_TOP);
        flagMap.put("FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET",
                Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        flagMap.put("FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS",
                Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        flagMap.put("FLAG_ACTIVITY_FORWARD_RESULT",
                Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        flagMap.put("FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY",
                Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
        flagMap.put("FLAG_ACTIVITY_MULTIPLE_TASK",
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        flagMap.put("FLAG_ACTIVITY_NEW_TASK", Intent.FLAG_ACTIVITY_NEW_TASK);
        // Intent.FLAG_ACTIVITY_NO_ANIMATION is Android 2.0
        flagMap.put("FLAG_ACTIVITY_NO_ANIMATION",
                Intent.FLAG_ACTIVITY_NO_ANIMATION);
        flagMap.put("FLAG_ACTIVITY_NO_HISTORY",
                Intent.FLAG_ACTIVITY_NO_HISTORY);
        flagMap.put("FLAG_ACTIVITY_NO_USER_ACTION",
                Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        flagMap.put("FLAG_ACTIVITY_PREVIOUS_IS_TOP",
                Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        flagMap.put("FLAG_ACTIVITY_REORDER_TO_FRONT",
                Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        flagMap.put("FLAG_ACTIVITY_RESET_TASK_IF_NEEDED",
                Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        flagMap.put("FLAG_ACTIVITY_SINGLE_TOP",
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
    }

    public static Integer getFlag(String flagName) {
        return flagMap.get(flagName);
    }

    public static String getFlagName(int flag) {
        for (Entry<String, Integer> entry : flagMap.entrySet()) {
            if (entry.getValue() == flag) {
                return entry.getKey();
            }
        }

        return null;
    }

    public static Set<Entry<String, Integer>> getFlags() {
        TreeSet<Entry<String, Integer>> result =
                new TreeSet<Entry<String, Integer>>(new EntryComparator());
        result.addAll(flagMap.entrySet());
        return result;
    }

    private static class EntryComparator implements
            Comparator<Entry<String, ?>>
    {
        @Override
        public int compare(Entry<String, ?> object1, Entry<String, ?> object2) {
            return object1.getKey().compareTo(object2.getKey());
        }
    }
}
