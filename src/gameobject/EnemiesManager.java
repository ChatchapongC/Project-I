package gameobject;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import util.Resource;

public class EnemiesManager {
	
	private BufferedImage cactus1;
	private BufferedImage cactus2;
	private BufferedImage bullet;
	private AudioClip bulletSound;
	private Random rand;
	
	private List<Enemy> enemies;
	private MainCharacter mainCharacter;
	
	public EnemiesManager(MainCharacter mainCharacter) {
		rand = new Random();
		cactus1 = Resource.getResourceImage("data/cactus1.png");
		cactus2 = Resource.getResourceImage("data/cactus2.png");
		bullet = Resource.getResourceImage("data/Bullet.png");
		try {
			bulletSound = Applet.newAudioClip(new URL("file","","data/Gunsound.wav"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		enemies = new ArrayList<Enemy>();
		this.mainCharacter = mainCharacter;
		enemies.add(createEnemy());
	}
	
	public void update() {
		for(Enemy e : enemies) {
			e.update();
		}
		Enemy enemy = enemies.get(0);
		if(enemy.isOutOfScreen()) {
			mainCharacter.upScore();
			enemies.clear();
			enemies.add(createEnemy());
		}
	}
	
	public void draw(Graphics g) {
		for(Enemy e : enemies) {
			e.draw(g);
		}
	}
	
	private Enemy createEnemy() {
		// if (enemyType = getRandom)
		int type = rand.nextInt(3);
		if(type == 0) {
			return new Cactus(mainCharacter, 800, cactus1.getWidth() -10, cactus1.getHeight() -30, cactus1);
		}
		else if(type == 1) {
			bulletSound.play();
			return new Bullet(mainCharacter, 725, bullet.getWidth() -10, bullet.getHeight() -10, bullet);
		}
		else {
			return new Cactus(mainCharacter, 800, cactus2.getWidth() -10, cactus2.getHeight() -10, cactus2);
		}
	}
	
	public boolean isCollision() {
		for(Enemy e : enemies) {
			if (mainCharacter.getBound().intersects(e.getBound())) {
				return true;
			}
		}
		return false;
	}
	
	public void reset() {
		enemies.clear();
		enemies.add(createEnemy());
	}
	
}
