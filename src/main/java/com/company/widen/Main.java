package com.company.widen;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) throws AWTException {
        new MisterRobot();
    }
}


class MisterRobot {
    private final String lineContext;
    private final String startingPositionContext;
    private Robot robot = new Robot();
//    private List<String> breakPoints = Arrays.asList(" ", ",", "/", "(", ")", ".");
    private List<String> breakPoints = Arrays.asList("/");
    private int startPosition = 0;
    private int endPosition = 0;
    private int initialPosition = 0;
    private int widenInvokeCount = 0;

    public static void main(String[] args) throws AWTException {
        new MisterRobot();
    }

    MisterRobot() throws AWTException {
        setupRobot();
        selectEnd();
        this.startingPositionContext = copySelection();

        right();
        selectHome();
        this.lineContext = copySelection();
        left();
        widen();
        widen();

        System.exit(0);
    }

    private void setupRobot() {
        robot.setAutoDelay(0);
        robot.setAutoWaitForIdle(true);
        robot.delay(2000);
    }

    private void widen() {
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

            if(widenInvokeCount == encounteredBreakpoints && breakPoints.contains(s)) {
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

    private void selectEnd() {
        robot.keyPress(KeyEvent.VK_SHIFT);
        robot.keyPress(KeyEvent.VK_END);
        robot.keyRelease(KeyEvent.VK_SHIFT);
        robot.keyRelease(KeyEvent.VK_END);
    }

    private void selectHome() {
        robot.keyPress(KeyEvent.VK_SHIFT);
        robot.keyPress(KeyEvent.VK_HOME);
        robot.keyRelease(KeyEvent.VK_SHIFT);
        robot.keyRelease(KeyEvent.VK_HOME);
    }

    private void left() {
        robot.keyPress(KeyEvent.VK_LEFT);
        robot.keyRelease(KeyEvent.VK_LEFT);
    }

    private void startOfLine() {
        robot.keyPress(KeyEvent.VK_END);
        robot.keyRelease(KeyEvent.VK_END);
        robot.keyPress(KeyEvent.VK_HOME);
        robot.keyRelease(KeyEvent.VK_HOME);
    }

}