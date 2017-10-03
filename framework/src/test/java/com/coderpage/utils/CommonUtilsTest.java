package com.coderpage.utils;

import com.coderpage.base.utils.CommonUtils;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

/**
 * @author lc. 2017-09-22 22:30
 * @since 0.5.0
 */

public class CommonUtilsTest {

    @Test
    public void testJoinCollectionElements() {
        ArrayList<String> strings = new ArrayList<>();
        String result;

        result = CommonUtils.collectionJoinElements(strings, ",");
        Assert.assertEquals("", result);

        strings.add("A");
        result = CommonUtils.collectionJoinElements(strings, ",");
        Assert.assertEquals("A", result);

        strings.add("B");
        result = CommonUtils.collectionJoinElements(strings, ",");
        Assert.assertEquals("A,B", result);
    }
}
