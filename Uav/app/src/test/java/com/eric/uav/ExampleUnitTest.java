package com.eric.uav;

import android.bluetooth.BluetoothDevice;

import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        Method[] methods = BluetoothDevice.class.getMethods();
        for (Method method : methods) {
            System.out.println(method.getName());
        }
    }
}