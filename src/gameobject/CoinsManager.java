package gameobject;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import util.Resource;

public class CoinsManager {

    private BufferedImage coin1;
    private BufferedImage coin2;
    private Random rand;

    private List<Token> coins;
    private MainCharacter mainCharacter;

    public CoinsManager(MainCharacter mainCharacter) {
        rand = new Random();
        coin1 = Resource.getResourceImage("data/coin1.png");
        coin2 = Resource.getResourceImage("data/coin1.png");
        coins = new ArrayList<Token>();
        this.mainCharacter = mainCharacter;
        coins.add(createCoins());
    }

    public void update() {
        for(Token g : coins) {
            g.update();
        }
        Token coin = coins.get(0);
        if(coin.isOutOfScreen()) {
            coins.clear();
            coins.add(createCoins());
        }
    }

    public void draw(Graphics g) {
        for(Token c : coins) {
            c.draw(g);
        }
    }

    private Token createCoins() {
        int type = rand.nextInt(3);
        if(type == 0) {
            return new Coin(mainCharacter, 750, coin1.getWidth()-10 , coin1.getHeight() -10, coin1);
        } else {
            return new Coin(mainCharacter, 700, coin2.getWidth()-10 , coin2.getHeight()-10, coin2);
        }
    }

    public boolean isCollision() {
        for(Token c : coins) {
            if (mainCharacter.getBound().intersects(c.getBound())) {
                return true;
            }
        }
        return false;
    }

    public void reset() {
        coins.clear();
        coins.add(createCoins());
    }

}
