package com.company.widen;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class NativeRobot implements NativeKeyListener {
    private String lineContext;
    private String startingPositionContext;
    private Robot robot;
    private List<String> breakPoints = Arrays.asList(" ", ",", "/", "(", ")", ".");
    private int startPosition = 0;
    private int endPosition = 0;
    private int initialPosition = 0;
    private int widenInvokeCount = 0;
    private boolean listenerShouldBeListening = true; //PROBABLY GOING TO HAVE PROBLEMS WITH PARALLEL THREADS

    NativeRobot() {
        setupRobot();
        registerNativeHook();
    }

    private synchronized void setContext() {
        removeListener();
        selectEnd();
        this.startingPositionContext = copySelection();
        right();
        selectHome();
        this.lineContext = copySelection();
        left();
        addListener();
    }

    private void addListener() {
        this.listenerShouldBeListening = true;
    }

    private void removeListener() {
        this.listenerShouldBeListening = false;
    }

    private void setupRobot() {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            System.out.println("Unable to create Robot");
            e.printStackTrace();
        }
        robot.setAutoDelay(0);
        robot.setAutoWaitForIdle(true);
        robot.delay(2000);
    }

    private synchronized void widen() {
        int lineLength = lineContext.length();
        initialPosition = lineLength - startingPositionContext.length();
        List<String> chars = Arrays.asList(lineContext.split(""));

        calculateEndPosition(chars);
        calculateStartPosition(chars);

        System.out.println("startPosition, endPosition = " + startPosition + ", " + endPosition);
        selectThisShit(startPosition, endPosition);
        widenInvokeCount++;
    }

    private void calculateEndPosition(List<String> chars) {
        int end = chars.size();
        int encounteredBreakpoints = 0;
        for (int i = initialPosition; i < chars.size(); i++) {
            String s = chars.get(i);

            if (widenInvokeCount == encounteredBreakpoints && breakPoints.contains(s)) {
                end = i;
                break;
            }

            if (breakPoints.contains(s)) encounteredBreakpoints++;
        }
        endPosition = end;
    }

    private void calculateStartPosition(List<String> chars) {
        for (int k = initialPosition; k >= 0; k--) {
            String s = chars.get(k);
            if (breakPoints.contains(s)) {
                startPosition = k + 1;
                break;
            }
        }
    }

    private void selectThisShit(int start, int end) {
        startOfLine();
        for (int i = 0; i < start; i++) right();
        robot.keyPress(KeyEvent.VK_SHIFT);
        for (int k = start; k < end; k++) right();
        robot.keyRelease(KeyEvent.VK_SHIFT);
    }

    private String copySelection() {
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_C);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.keyRelease(KeyEvent.VK_C);
        try {
            return (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException | IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void right() {
        robot.keyPress(KeyEvent.VK_RIGHT);
        robot.keyRelease(KeyEvent.VK_RIGHT);
    }

    private synchronized void selectEnd() {
        robot.keyPress(KeyEvent.VK_SHIFT);
        robot.keyPress(KeyEvent.VK_END);
        robot.keyRelease(KeyEvent.VK_SHIFT);
        robot.keyRelease(KeyEvent.VK_END);
    }

    private synchronized void selectHome() {
        robot.keyPress(KeyEvent.VK_SHIFT);
        robot.keyPress(KeyEvent.VK_HOME);
        robot.keyRelease(KeyEvent.VK_SHIFT);
        robot.keyRelease(KeyEvent.VK_HOME);
    }

    private synchronized void left() {
        robot.keyPress(KeyEvent.VK_LEFT);
        robot.keyRelease(KeyEvent.VK_LEFT);
    }

    private synchronized void startOfLine() {
        robot.keyPress(KeyEvent.VK_END);
        robot.keyRelease(KeyEvent.VK_END);
        robot.keyPress(KeyEvent.VK_HOME);
        robot.keyRelease(KeyEvent.VK_HOME);
    }

    /* NATIVE KEY EVENTS */

    public void nativeKeyPressed(NativeKeyEvent e) {
        System.out.println("listenerShouldBeListening = " + listenerShouldBeListening);
        if (this.listenerShouldBeListening) {
            System.out.println("e.getKeyCode() = " + e.getKeyCode());
            if (e.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
                try {
                    GlobalScreen.unregisterNativeHook();
                } catch (NativeHookException e1) {
                    System.out.println("Unable to unregister Native Hook");
                    e1.printStackTrace();
                }
            } else if (e.getKeyCode() == NativeKeyEvent.VC_F15 && noContext()) {
                setContext();
                widen();
            } else if (e.getKeyCode() == NativeKeyEvent.VC_F15) {
                widen();
            } else if (e.getKeyCode() == NativeKeyEvent.VC_F14){
                System.out.println("Releasing context...");
                releaseContext();
            }
        }
    }

    private boolean noContext() {
        return this.lineContext == null && this.startingPositionContext == null && this.widenInvokeCount == 0;
    }

    private void releaseContext() {
        this.lineContext = null;
        this.startingPositionContext = null;
        this.widenInvokeCount = 0;
    }

    public void nativeKeyReleased(NativeKeyEvent e) {
    }

    public void nativeKeyTyped(NativeKeyEvent e) {

    }

    public static void main(String[] args) {
        new NativeRobot();
    }

    private void registerNativeHook() {
        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(this);
        } catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());
        }
        addListener();
    }

}
