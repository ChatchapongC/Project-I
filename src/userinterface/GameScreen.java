package userinterface;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JPanel;

import com.sun.org.apache.regexp.internal.RE;
import gameobject.EnemiesManager;
import gameobject.CoinsManager;
import gameobject.Land;
import gameobject.MainCharacter;
import util.Resource;

public class GameScreen extends JPanel implements Runnable, KeyListener {

	private static final int START_GAME_STATE = 0;
	private static final int GAME_PLAYING_STATE = 1;
	private static final int GAME_OVER_STATE = 2;
	
	private Land land;
	private MainCharacter mainCharacter;
	private EnemiesManager enemiesManager;
	private CoinsManager coinsManager;
	private Thread thread;

	private Font font;

	private AudioClip runSound;
	private AudioClip collectCoinSound;

	private boolean isKeyPressed;

	private int gameState = START_GAME_STATE;

	private BufferedImage startButtonImage;
	private BufferedImage howtoPlay;
	private BufferedImage gameOverButtonImage;
	private BufferedImage replayButtonImage;

	public GameScreen() {
		mainCharacter = new MainCharacter();
		land = new Land(GameWindow.SCREEN_WIDTH, mainCharacter);
		font = new Font("Comic Sans MS",Font.PLAIN, 11);
		mainCharacter.setSpeedX(6);
		startButtonImage = Resource.getResourceImage("data/Start_button.png");
		howtoPlay = Resource.getResourceImage(("data/Howto.png"));
		replayButtonImage = Resource.getResourceImage("data/Replay_button.png");
		gameOverButtonImage = Resource.getResourceImage("data/GameOver.png");
		enemiesManager = new EnemiesManager(mainCharacter);
		coinsManager = new CoinsManager(mainCharacter);
		try {
			runSound = Applet.newAudioClip(new URL("file","","data/Run.wav"));
			collectCoinSound = Applet.newAudioClip(new URL("file","","data/coinsound.wav"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public void startGame() {
		thread = new Thread(this);
		thread.start();
	}

	public void gameUpdate() {
		if (gameState == GAME_PLAYING_STATE) {
			land.update();
			mainCharacter.update();
			enemiesManager.update();
			if (enemiesManager.isCollision()) {
				mainCharacter.playDeadSound();
				gameState = GAME_OVER_STATE;
				mainCharacter.dead(true);
			}
			coinsManager.update();
			if(coinsManager.isCollision()) {
				collectCoinSound.play();
				mainCharacter.score+=10;
				coinsManager.reset();
			}
		}
	}

	public void paint(Graphics g) {
		g.setColor(Color.decode("#f7f7f7"));
		g.fillRect(0, 0, getWidth(), getHeight());

		switch (gameState) {
		case START_GAME_STATE:
			mainCharacter.draw(g);
			g.drawImage(startButtonImage, 90, 30, null);
			g.drawImage(howtoPlay,90,0,null);
			break;
		case GAME_PLAYING_STATE:
		case GAME_OVER_STATE:
			coinsManager.draw(g);
			land.draw(g);
			enemiesManager.draw(g);
			mainCharacter.draw(g);
			g.setColor(Color.DARK_GRAY);
			g.setFont(font);
			g.drawString("YOUR SCORE : " + mainCharacter.score, 475, 20);
			g.drawString("YOUR HIGHSCORE : " + mainCharacter.highScore,5,20);
			if (gameState == GAME_OVER_STATE) {
				g.drawImage(replayButtonImage, 306, 70, null);
				g.drawImage(gameOverButtonImage, 125, 5, null);

			}
			break;
		}
	}

	@Override
	public void run() {

		int fps = 100;
		long msPerFrame = 1000 * 1000000 / fps;
		long lastTime = 0;
		long elapsed;
		
		int msSleep;
		int nanoSleep;

		long endProcessGame;
		long lag = 0;

		while (true) {
			gameUpdate();
			repaint();
			endProcessGame = System.nanoTime();
			elapsed = (lastTime + msPerFrame - System.nanoTime());
			msSleep = (int) (elapsed / 1000000);
			nanoSleep = (int) (elapsed % 1000000);
			if (msSleep <= 0) {
				lastTime = System.nanoTime();
				continue;
			}
			try {
				Thread.sleep(msSleep, nanoSleep);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			lastTime = System.nanoTime();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (!isKeyPressed) {
			isKeyPressed = true;
			switch (gameState) {
			case START_GAME_STATE:
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					runSound.loop();
					gameState = GAME_PLAYING_STATE;
					break;
				}
				else if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
					System.exit(0);
				}
			case GAME_PLAYING_STATE:
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					mainCharacter.jump();
				} else if (e.getKeyCode() == KeyEvent.VK_S) {
					mainCharacter.down(true);
				}
				break;
			case GAME_OVER_STATE:
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					gameState = GAME_PLAYING_STATE;
					resetGame();
					break;
				}
				else if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
					System.exit(0);
					break;
				}
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		isKeyPressed = false;
		if (gameState == GAME_PLAYING_STATE) {
			if (e.getKeyCode() == KeyEvent.VK_S) {
				mainCharacter.down(false);
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	private void resetGame() {
		enemiesManager.reset();
		coinsManager.reset();
		mainCharacter.dead(false);
		mainCharacter.reset();
	}

}
