package com.company.widen;

import java.awt.*;
import java.awt.peer.RobotPeer;

public class MyOwnRobotIGNORE implements RobotPeer {

    private final GraphicsDevice defaultScreenDevice;

    MyOwnRobotIGNORE() {
        defaultScreenDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        Toolkit toolkit = Toolkit.getDefaultToolkit();

    }

    @Override
    public void mouseMove(int x, int y) {}

    @Override
    public void mousePress(int buttons) {}

    @Override
    public void mouseRelease(int buttons) {}

    @Override
    public void mouseWheel(int wheelAmt) {}

    @Override
    public void keyPress(int keycode) {

    }

    @Override
    public void keyRelease(int keycode) {

    }

    @Override
    public int getRGBPixel(int x, int y) {
        return 0;
    }

    @Override
    public int[] getRGBPixels(Rectangle bounds) {
        return new int[0];
    }

    @Override
    public void dispose() {

    }
}
