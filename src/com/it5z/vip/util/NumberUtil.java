package com.it5z.vip.util;

/**
 * Created by Administrator on 2014/12/17.
 */
public class NumberUtil {
    private NumberUtil() {}

    public static int getPageCount(int count, int row) {
        return (count + row - 1) / row;
    }
}
