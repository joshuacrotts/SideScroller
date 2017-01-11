package com.sidescrollerv2.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import com.sidescrollerv2.blocks.Block;

public class Game extends Canvas implements Runnable{

	private static final long serialVersionUID = 7451407222234826899L;
	//Variables relating to the Window 
	public static final short WIDTH = 1280;
	public static final short HEIGHT = 720;
	private Window w;
	
	//BufferStrategy/Graphics related objects
	
	//Etc Objects
	public static Player player;
	public static Level[] levels;
	public static Handler handler;
	public static Camera camera;
	
	//Level variables
	public static byte currentLevelInt = 0;
	
	//Objects/variables relating to the thread
	private Thread t;
	private boolean running = false;
	private short frames;
	private short updates;
	
	public Game(){
		handler = new Handler(this);
		camera = new Camera(0,0);
		this.w = new Window(WIDTH,HEIGHT,"Side Scroller V.2",this);
		player = new Player((short)90,(short)500);
		levels = new Level[1];
		
		this.addLevels();
		this.loadImageLevel(levels[currentLevelInt].getImage());
		this.addKeyListener(player);
		
		this.start();
	}
	
	public synchronized void start(){
		if(running) return;
		else{
			this.t = new Thread(this);
			this.t.start();
			this.running = true;
		}
	}
	
	public synchronized void stop(){
		if(!running) return;
		else{
			try{
				t.join();
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}
		running = false;
	}
	
	public void run(){
	      requestFocus();
	      
	      long lastTime = System.nanoTime();
	      final double ns = 1000000000.0 / 60.0;
	      double delta = 0;
	      long timer = System.currentTimeMillis();
	      this.frames = 0;
	      this.updates = 0;
	      
	      while (running) {
	         long now = System.nanoTime();
	         delta += (now - lastTime) / ns;
	         lastTime = now;
	         while (delta >= 1) {
	            tick();
	            delta--;
	            updates++;
	            render();
	            frames++;
	         }
	         if (System.currentTimeMillis() - timer > 1000) {
	            timer += 1000;
	            w.setTitle(" | " + updates + " ups, " + frames + " fps");
	            updates = 0;
	            frames = 0;
	         }
	      }
		stop();
	}
	
	private void tick(){
		levels[currentLevelInt].tick();
		handler.tick();
		camera.tick();
	}
	
	private void render() {
		BufferStrategy bs = this.getBufferStrategy();

		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}

		Graphics g = bs.getDrawGraphics();
		Graphics2D g2 = (Graphics2D) g;
		
		// DRAW HERE

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, WIDTH, HEIGHT);

		g2.translate(camera.getTranslationX(), camera.getTranslationY()); // begin of cam

		levels[currentLevelInt].render(g);
		handler.render(g);
		
		g2.translate(-camera.getTranslationX(), -camera.getTranslationY()); // end of camera

		// FPS drawer
		
		Font f = new Font("Arial", Font.BOLD, 14);
		g.setFont(f);
		g.setColor(Color.RED);
		g.drawString("Side Scroller Indev", 10, 20);

		// END DRAWING

		g.dispose();
		bs.show();
	}
	
	private void loadImageLevel(BufferedImage image) {
		int w = image.getWidth();
		int h = image.getHeight();

		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				int pixel = image.getRGB(x, y);

				int r = (pixel >> 16) & 0xff;
				int g = (pixel >> 8) & 0xff;
				int b = (pixel) & 0xff;

				if (r == 255 && g == 255 && b == 255) {
					handler.add(new Block((short) (x * 32), (short) (y * 32), "img/sprites/items/block1.png"));
				}
				if (r == 0 && g == 0 && b == 0) {
					Color c = Color.BLACK;
					image.setRGB(x, y, c.getRGB());
				}

				// if(r == 0 && g == 0 && b == 255){
				// handler.add(new Player(640, 624, ID.Player, handler, this,
				// levels));
				// }
			}
		}

	}
	
	private void addLevels() {
		//new Level(File, handler, width, height)
		levels[0] = new Level("img/backgrounds/level1.png", (short)3360, (short)704);
	}
	
	public static void main(String[] args){
		new Game();
		
	}
}