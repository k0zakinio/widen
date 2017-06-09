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
        new MrRobot();
    }
}


class MrRobot {
    private final String lineContext;
    private final String startingPositionContext;
    private Robot robot = new Robot();
    private int widenCount = 0;
    private int ignoredBreakpoints = 0;
    private List<String> breakPoints = Arrays.asList(" ", ",", "/", "(", ")", ".");
    private int startPosition = 0;
    private int endPosition = 0;
    private int initialPosition = 0;

    public static void main(String[] args) throws AWTException {
        new MrRobot();
    }

    MrRobot() throws AWTException {
        setupRobot();
        selectEnd();
        this.startingPositionContext = copySelection();

        right();
        selectHome();
        this.lineContext = copySelection();
        left();
        widen();

        System.exit(0);
    }

    private void setupRobot() {
        robot.setAutoDelay(1);
        robot.setAutoWaitForIdle(true);
        robot.delay(2000);
    }

    private void widen() {
        int lineLength = lineContext.length();
        initialPosition = lineLength - startingPositionContext.length();
        List<String> chars = Arrays.asList(lineContext.split(""));

        System.out.println("chars = " + chars);

        endPosition = calculateEndPosition(lineLength, chars);

        calculateStartPosition(chars);

        widenCount++;
        selectThisShit(startPosition, endPosition);
    }

    private int calculateEndPosition(int lineLength, List<String> chars) {
        int end = lineLength;
        for (int i = initialPosition; i < lineLength; i++) {
            String s = chars.get(i);
            if (breakPoints.contains(s) && shouldEndHere()) {
                end = i;
                ignoredBreakpoints++;
                break;
            }
        }
        return end;
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

    private boolean shouldEndHere() {
        return widenCount >= ignoredBreakpoints;
    }

    private void selectThisShit(int start, int end) {
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

}