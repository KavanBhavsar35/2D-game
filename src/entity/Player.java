package entity;

import main.KeyHandler;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import main.GamePanel;

public class Player extends Entity {
    
    // INITATION FOR INSTANCES
    KeyHandler keyHandler;
    
    public final int screenX;
    public final int screenY;
    public int standCounter = 0;

    // CONSTRUCTOR
    public Player(GamePanel gamePanel, KeyHandler keyHandler) {
        
        // INITIATION
        super(gamePanel);
        this.keyHandler = keyHandler;

        // PLAYER CENTER POSITION
        screenX = GamePanel.screenWidth / 2 - (GamePanel.tileSize / 2);
        screenY = GamePanel.screenHeight / 2 - (GamePanel.tileSize / 2);

        // COLLIDABLE AREA FOR PLAYER
        solidArea = new Rectangle();
        solidArea.x = 8;
        solidArea.y = 16;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        solidArea.width = 32;
        solidArea.height = 32;

        this.setDefaultValue();
        this.getPlayerImage();
    }

    // DEFAULT VALS
    public void setDefaultValue() {
        // worldX = GamePanel.tileSize * 23;
        // worldY = GamePanel.tileSize * 21;
        worldX = GamePanel.tileSize * 10;
        worldY = GamePanel.tileSize * 13;
        speed = 4;
        direction = "down";

        // PLAYER STATUS
        maxLife = 6;
        life = maxLife;
    }

    // LOAD IMAGES
    public void getPlayerImage() {

        up1 = setup("/player/boy_up_1.png");
        up2 = setup("/player/boy_up_2.png");
        down2 = setup("/player/boy_down_2.png");
        down1 = setup("/player/boy_down_1.png");
        left1 = setup("/player/boy_left_1.png");
        left2 = setup("/player/boy_left_2.png");
        right1 = setup("/player/boy_right_1.png");
        right2 = setup("/player/boy_right_2.png");
    }

    // UPDATE PLAYER IF CHANGED
    public void update() {
    	
    	if (keyHandler.upPressed == true || keyHandler.downPressed == true || 
    			keyHandler.leftPressed == true || keyHandler.rightPressed == true) {

	        if (keyHandler.upPressed == true)  direction = "up"; 
            else if (keyHandler.downPressed == true)  direction = "down"; 
            else if (keyHandler.leftPressed == true)  direction = "left"; 
            else if (keyHandler.rightPressed == true)  direction = "right"; 
            
            // CHECK TILE COLLISION
            collisionOn = false;
            gamePanel.collisionChecker.checkTile(this);

            // CHECK OBJECT COLLISION
            int objIndex = gamePanel.collisionChecker.checkObject(this, true);
            pickUpObject(objIndex);
            
            // CHECK NPC ? MONSTER COLLISION
            int npcIndex =  gamePanel.collisionChecker.checkEntity(this, gamePanel.npc);
            interactNPC(npcIndex);
            
            // CHECK MONSTER COLLISION
            int monsterIndex =  gamePanel.collisionChecker.checkEntity(this, gamePanel.monster);
            contactMonster(monsterIndex);
            
            // CHECKING EVENT
            gamePanel.eventHandler.checkEvent();
            gamePanel.keyHandler.enterPressed = false;


            // MOVE IF COLLISION IS OFF
            if (collisionOn == false) {
                switch (direction) {
                    case "up": worldY -= speed; break;
                    case "down": worldY += speed; break;
                    case "left": worldX -= speed; break;
                    case "right": worldX += speed; break;
                }
            }

            // SWAP BETWEEN DIFFERENT IMAGES
	        spriteCounter++;
	        if (spriteCounter > 12) {
	        	if (spriteNum == 1) spriteNum = 2; 
	        	else if (spriteNum == 2) spriteNum = 1; 
	        	spriteCounter = 0;
	        }
    	} else {
            standCounter++;
            System.out.println(standCounter);
            if (standCounter == 20) {
                spriteNum = 1;
                standCounter = 0;
            }
        }

        if (isInvincible) {
            invincibleCounter++;
            if (invincibleCounter > invincibleDuration) {
                isInvincible = false;
                invincibleCounter = 0;
            }
        }
    }

    private void contactMonster(int monsterIndex) {
        
        if (monsterIndex == 999) return;
        if (!isInvincible) {
            life -= 1;
            isInvincible = true;
        }

    }

    // PICK A OBJECT
    public void pickUpObject(int objIndex) {
        if (objIndex != 999) {
            
        }
    }

    // INTERACTION
    public void interactNPC(int i) {
        if (i != 999) {
            if (keyHandler.enterPressed) {
                    
                gamePanel.gameState = gamePanel.dialougeState;
                gamePanel.npc[i].speak();
            }
        }
    }

    // RENDER PLAYER
    public void draw(Graphics2D g2) {
        BufferedImage image = null;
        switch (direction) {
            case "up":
                if (spriteNum == 1) image = up1;
                if (spriteNum == 2) image = up2;
                break;
            case "down":
                if (spriteNum == 1) image = down1;
                if (spriteNum == 2) image = down2;
                break;
            case "left":
                if (spriteNum == 1) image = left1;
                if (spriteNum == 2) image = left2;
                break;
            case "right":
                if (spriteNum == 1) { image = right1; }
                if (spriteNum == 2) { image = right2; }
                break;
        }

        float alpha = 1f;
        if (isInvincible) {
            alpha =  0.3f;
        }
        if (invincibleCounter == 40 || invincibleCounter == 90 || invincibleCounter == 100) {
            alpha =  1f;
        } else if (invincibleCounter == 65 || invincibleCounter == 70) {
            alpha =  0.3f;
        }
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha)); 
        g2.drawImage(image, screenX, screenY, GamePanel.tileSize, GamePanel.tileSize, null);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }
}
