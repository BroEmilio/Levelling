package tests;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class InsertElevetionsAndCalculateLevelling {
	Robot bot;
	
	public InsertElevetionsAndCalculateLevelling() {
		try {
			Thread.sleep(2000);
			bot = new Robot();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	
	public void run() throws Exception {
		doubleClickInPlace(1000, 260); // first cell of elevation
		enterElevationEndsWith(KeyEvent.VK_1);
		bot.keyPress(KeyEvent.VK_DOWN);
		enterElevationEndsWith(KeyEvent.VK_2);
		enterElevationEndsWith(KeyEvent.VK_3);
		enterElevationEndsWith(KeyEvent.VK_4);
		enterElevationEndsWith(KeyEvent.VK_5);
		enterElevationEndsWith(KeyEvent.VK_6);
		enterElevationEndsWith(KeyEvent.VK_7);
		bot.keyPress(KeyEvent.VK_UP);
		Thread.sleep(200);
		bot.keyPress(KeyEvent.VK_UP);
		Thread.sleep(200);
		bot.keyPress(KeyEvent.VK_UP);
		Thread.sleep(200);
		bot.keyPress(KeyEvent.VK_UP);
		Thread.sleep(200);
		bot.keyPress(KeyEvent.VK_F5); // set as last benchmark
		Thread.sleep(1200);
		bot.keyPress(KeyEvent.VK_ENTER);
		Thread.sleep(700);
		oneClickInPlace(500, 100); // calculate leveling
		Thread.sleep(1200);
		bot.keyPress(KeyEvent.VK_ENTER);
		Thread.sleep(400);
		doubleClickInPlace(850, 100);
		Thread.sleep(1200); // calculate second sights
		bot.keyPress(KeyEvent.VK_ENTER);
	}
	
	void doubleClickInPlace(int x, int y) {
		bot.mouseMove(x, y);
		bot.mousePress(InputEvent.BUTTON1_MASK);
		bot.mouseRelease(InputEvent.BUTTON1_MASK);
		bot.mousePress(InputEvent.BUTTON1_MASK);
		bot.mouseRelease(InputEvent.BUTTON1_MASK);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	void oneClickInPlace(int x, int y) {
		bot.mouseMove(x, y);
		bot.mousePress(InputEvent.BUTTON1_MASK);
		bot.mouseRelease(InputEvent.BUTTON1_MASK);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	void enterElevationEndsWith(int lastDigitOfElevation) {
		bot.keyPress(KeyEvent.VK_1);
		bot.keyPress(KeyEvent.VK_2);
		if(lastDigitOfElevation==KeyEvent.VK_2)
			bot.keyRelease(KeyEvent.VK_2);
		bot.keyPress(lastDigitOfElevation);
		bot.keyPress(KeyEvent.VK_ENTER);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	

}
