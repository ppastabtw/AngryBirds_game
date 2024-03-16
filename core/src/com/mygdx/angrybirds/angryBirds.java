/*
File Name: angryBirds.java
Author: Kevin Yan

This is where most of the main game is run. World gets generated, levels are loaded, physics with launching birds are
calculated, interface and pictures are loaded, inputs received and managed, and more are all managed in this class.
The game runs using 2 main functions after it is first initialized; render() and update(). All the game's calculations
are made in render() and then displayed on screen in update().

The world is run using Box2D and rendered with Libgdx.

 */

package com.mygdx.angrybirds;

//import

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.angrybirds.utils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mygdx.angrybirds.utils.Constants.*;

public class angryBirds implements Screen {
	final Drop game;
	InputProcessor IP; // class for listening to inputs and processing them(only used for mx, my)
	ContactListener CL = new ListenerClass();// listener class to detect/resolve world collisions
	FileManager FM = new FileManager(); // class to help read/write txt files
	private ShapeRenderer sr; // class made for drawing lines(or shapes)

	private Box2DDebugRenderer b2dr; // draws the sides of all box2D objects
	private World world; // the world
	private float mapWidth = 1000;
	private float mapHeight = 800;

	private final float SCALE = 2.0f; // scale of the world(i shouldnt have done this, but its what the tutorial said to..)

	private OrthographicCamera camera; // your eyes :)
	private float cameraScale; // scale of how large everything should be rendered relative to camera's 'position'
	private int camerax, cameray; // camera's x/y values
	private int cameradeltax = 0; // used for moving the camera 'dx' distance
	private boolean cameraFollowPlayer; // to tell the camera to follow bird flying through the air
	private boolean startCameraFollowPlayer; // to tell the camera to start follow bird
	private Body platform; // the ground
	private ArrayList<Body> redBirds = new ArrayList<Body>(); // list of all red bird bodies
	private ArrayList<Body> blueBirds = new ArrayList<Body>(); // list of all blue bird bodies
	private ArrayList<Body> yellowBirds = new ArrayList<Body>(); // list of all yellow bird bodies
	private ArrayList<Body> bigBirds = new ArrayList<Body>(); // list of all big bird bodies
	private ArrayList[] AllBirds = {redBirds, blueBirds, yellowBirds, bigBirds}; // superlist of bird lists
	private ArrayList<Body> birdOrder = new ArrayList<Body>(); // list of the order in which birds were used
	private ArrayList<Integer> birdSpawnOrder = new ArrayList<Integer>();// list of the order in which birds will be used
	private int birdSpawnPos = 0; // the current position in birdorder the player is currently at
	private Texture red, blue, yellow, big; // bird textures
	private Texture[] birdPics = {red, blue, yellow, big}; // list of textures
	private int redr, bluer, yellowr, bigr; // length of bird radii
	private int[] birdRadius = {redr, bluer, yellowr, bigr}; // list of bird radii
//	private int[] birdLaunchForces = {4000, 2000, 4800, 10000}; //
//	private Body[] objects;
	private ArrayList<Body> boxes1 = new ArrayList<Body>(); // list of 2x4 blocks
	private ArrayList<Body> boxes2 = new ArrayList<Body>(); // list of 1x8 blocks
	private ArrayList<Body> boxes3 = new ArrayList<Body>(); // list of 2x2 blocks
	private ArrayList[] AllBlocks = {boxes1, boxes2, boxes3}; // superlist of all block lists
	private ArrayList<Body> pigs0 = new ArrayList<>(); // list of normal pigs
	private ArrayList<Body> pigs1 = new ArrayList<>(); // list if helmet pigs
	private ArrayList[] AllPigs = {pigs0, pigs1}; // superlist of all pigs
	private ArrayList[][] AllObjects = {AllBlocks, AllPigs, AllBirds}; // superlist of superlists

	private SpriteBatch batch; // used for drawing textures on screen
	private Sprite mapSprite; // background
	private Texture ssBack, ssFront, ssSeat; // slingshot textures
	private Texture BKsky, BKground, BKtrees, BKgrass1, BKgrass2; // background textures
	private TextureRegion[] BKGsprites; // list of all background textures
	private float[] BKGscrollspeed = {1.2F, 0.5F, 0F, 0F}; // how much the background parts should move relative to camera movements
	private int[] BKGdupnums = {4, 4, 16, 8}; // list of how many times to duplicate background sprites
	private int[] BKGx = {0, 0, 0, 0}; // list of x positions to draw background sprites
	private int[] BKGy = {150, 150, 0, 170};// list of y positions to draw background sprites
	private int[] BKGw = {1000, 1000, 250, 550}; // list of background widths for sprites
	private int[] BKGh = {650, 450, 170, 100}; // list of background heights for sprites
	private int ssFl, ssFw, ssBl, ssBw, ssSl, ssSw; // length and width values for each slingshot sprite
	private Texture slingshot, wood00, wood01, wood02, wood10, wood11, wood12, wood20, wood21, wood22, wood30, wood31, wood32, // wood textures
	pigs00, pigs01, pigs10, pigs11, pigs20, pigs21, // pig textures
	trail0, trail1, trail2, trailAbility, // bird trail textures
	gameOverBack, starComplete, starEmpty; // end screen textures
	private Texture[] trailPics;
	private Texture[] pigPics;
	private Texture[] blockPics;
	//	private int redw, redl;
	private int numBlockTypes = 3; // number of different block types
	private int numPigTypes = 2; // number of different pig types
	private int blockType; // which block type player is currently using(for mapmaker)
	private int[] blockWidths = {40, 80, 20};// list of all block widths
	private int[] blockHeights = {20, 10, 20}; // list of all block heights
	private int pigRadius = 30; // radius of pigs
	private boolean isRotated = false; // if block player is currently using should be rotated(for mapmaker)
	private boolean bleftIsDown = false; // if left button is down
	private boolean isShooting = false; // if bird is on slingshot and aiming
	private boolean isScrolling = false; // if player is moving the camera
	private boolean shooted = false; // if bird has left the slingshot
	private boolean onSling = false; // if a bird is on slingshot
	private float mx, my; // mouse x/y positions
	private int slingshotx = 450/2; // position of slingshot x
	private int slingshoty = 340/2; // position of slingshot y
	private float slingx, slingy; // position of the sling seat x/y
	private double ang; // angle of the direction the bird is aiming at on the slingshot
	private float scrolloriginx, scrolloriginy; // original x/y positions of when player started moving the camera around
	private ArrayList<Body> currentBird = new ArrayList<Body>(); // the current bird(only a list for blue bird ability)
	private ArrayList<Body> prevBird = new ArrayList<Body>(); // the previous bird used
	private int Level; // level number
	private Button restartButton, gomenuButton, endRestartButton, endGomenuButton, endNextlevelButton; // buttons
	private float buttonHoverFactor = 1.1F; // how much to increase button size when hovering over it
	private Texture buttonRestart, buttonGomenu, buttonNextlevel; // button textures
	private static final int MAPMAKER = 0 ; // level number for map maker(magic number)
	private boolean gameOver = false; // if game is over
	private String winorlose; // if player won or if lose
	private BitmapFont font40, font30, font20; // fonts
	private List<float[]> pigDieAniPoints = new ArrayList<>(); // list of pig death animations
	private Texture[] smokeAni; // list of pig death animation frames
	private List<float[]> feathers = new ArrayList<>(); // list of bird contact animations
	private Texture[] featherPics; // list of bird contact pictures
	private ArrayList<Texture> allTextures = new ArrayList<>();
	
	public angryBirds(final Drop game, int level){ // initalize game, render textures
		this.game = game;
		Level = level;
		IP = new MyInputProcessor();
		Gdx.input.setInputProcessor(IP);

		// initalize fonts
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("assets/angrybirds-regular.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 40/2;
		parameter.borderColor.set(Color.BLACK);
		parameter.borderWidth = 2;
		parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS;
		font40 = generator.generateFont(parameter);
		parameter.size = 30/2;
		font30 = generator.generateFont(parameter);
		parameter.size = 20/2;
		parameter.borderWidth = 0;
		parameter.color = Color.BLACK;
		font20 = generator.generateFont(parameter);
		generator.dispose();

		// initalize default background
		float w = 1000;
		float h = 800;

		Texture bkg = new Texture(Gdx.files.internal("pictures/background.png"));
		allTextures.add(bkg);
		mapSprite = new Sprite(bkg);
		mapSprite.setPosition(0, 0);
		mapSprite.setSize(w/SCALE, h/SCALE);

		// initalize camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false, w/SCALE, h/SCALE);
		cameraScale = 1;
		camerax = 0;
		cameray = 0;
		cameraFollowPlayer = false;
		startCameraFollowPlayer = true;

		sr = new ShapeRenderer();// for drawing shapes

		// initalize world
		world = new World(new Vector2(0, -10f), false); // gravity is down
		b2dr = new Box2DDebugRenderer();
		world.setContactListener(CL);

		// make ground
		platform = createBox(-500, -15, 5000, 200, true, false, false, 1, new ObjectUserData(ObjectUserData.GROUND));

		// render background textures
		batch = new SpriteBatch();
		BKsky = new Texture("background/sky.png");
		allTextures.add(BKsky);
		BKtrees = new Texture("background/trees.png");
		allTextures.add(BKtrees);
		BKground = new Texture("background/ground.png");
		allTextures.add(BKgrass1);
		BKgrass1 = new Texture("background/grass1.png");
		allTextures.add(BKgrass1);
		TextureRegion bksky = new TextureRegion(BKsky);
		TextureRegion bktrees = new TextureRegion(BKtrees);
		TextureRegion bkground = new TextureRegion(BKground);
		TextureRegion bkgrass = new TextureRegion(BKgrass1);
		BKGsprites = new TextureRegion[] {bksky, bktrees, bkground, bkgrass};

		// render bird textures
		red = new Texture("birds/red1.png");
		allTextures.add(red);
		redr = 25;
		blue = new Texture("birds/blue.png");
		allTextures.add(blue);
		bluer = 16;
		yellow = new Texture("birds/yellow.png");
		allTextures.add(yellow);
		yellowr = 27;
		big = new Texture(("birds/big.png"));
		allTextures.add(big);
		bigr = 50;
		birdPics = new Texture[] {red, blue, yellow, big};
		birdRadius = new int[] {redr, bluer, yellowr, bigr};

		//slingshot textures
		ssSeat = new Texture("slingshot/ssSeat.png");
		allTextures.add(ssSeat);
		ssSl = 9;
		ssSw = 12;
		ssFront = new Texture("slingshot/ssFront.png");
		allTextures.add(ssFront);
		ssFl = 21;
		ssFw = 62;
		ssBack = new Texture("slingshot/ssBack.png");
		allTextures.add(ssBack);
		ssBl = 19;
		ssBw = 100;
//		slingx = slingshotx;
//		slingy = slingshoty;

		// block textures
		wood00 = new Texture("blocks/wood00.png");
		wood01 = new Texture("blocks/wood01.png");
		wood02 = new Texture("blocks/wood02.png");
		wood10 = new Texture("blocks/wood10.png");
		wood11 = new Texture("blocks/wood11.png");
		wood12 = new Texture("blocks/wood12.png");
		wood20 = new Texture("blocks/wood20.png");
		wood21 = new Texture("blocks/wood21.png");
		wood22 = new Texture("blocks/wood22.png");
		wood30 = new Texture("blocks/wood30.png");
		wood31 = new Texture("blocks/wood31.png");
		wood32 = new Texture("blocks/wood32.png");
		blockPics = new Texture[] {wood00, wood01, wood02
				, wood10, wood11, wood12, wood20, wood21, wood22, wood30, wood31, wood32
		};
        allTextures.addAll(Arrays.asList(blockPics));
		blockType = 0;

		// pig textures
		pigs00 = new Texture("pigs/normal0.png");
		pigs01 = new Texture("pigs/helmet0.png");
		pigs10 = new Texture("pigs/normal1.png");
		pigs11 = new Texture("pigs/helmet1.png");
		pigs20 = new Texture("pigs/normal2.png");
		pigs21 = new Texture("pigs/helmet2.png");
		pigPics = new Texture[] {pigs00, pigs01, pigs10, pigs11, pigs20, pigs21};
		allTextures.addAll(Arrays.asList(pigPics));

		// bird trail textures
		trail0 = new Texture("other/trail0.png");
		trail1 = new Texture("other/trail1.png");
		trail2 = new Texture("other/trail2.png");
		trailPics = new Texture[] {trail0, trail1, trail2};
		trailAbility = new Texture("other/trailAbility.png");
		allTextures.addAll(Arrays.asList(trailPics));

		//button textures
		buttonRestart = new Texture("other/restartButton.png");
		allTextures.add(buttonRestart);
		buttonGomenu = new Texture("other/gomenuButton.png");
		allTextures.add(buttonGomenu);
		buttonNextlevel = new Texture("other/nextlevelButton.png");
		allTextures.add(buttonNextlevel);

		restartButton = new Button(150/SCALE, 750/SCALE, 75/SCALE, 75/SCALE);
		restartButton.setTexture(buttonRestart);
		gomenuButton = new Button(50/SCALE, 750/SCALE, 75/SCALE, 75/SCALE);
		gomenuButton.setTexture(buttonGomenu);

		endRestartButton = new Button(500/SCALE, 250/SCALE, 100/SCALE, 100/SCALE);
		endRestartButton.setTexture(buttonRestart);
		endGomenuButton = new Button(400/SCALE, 250/SCALE, 100/SCALE, 100/SCALE);
		endGomenuButton.setTexture(buttonGomenu);
		endNextlevelButton = new Button(600/SCALE, 250/SCALE, 100/SCALE, 100/SCALE);
		endNextlevelButton.setTexture(buttonNextlevel);

		// end screen textures
		gameOverBack = new Texture("background/gameOver.png");
		allTextures.add(gameOverBack);
		starComplete = new Texture("other/starComplete.png");
		allTextures.add(starComplete);
		starEmpty = new Texture("other/starEmpty.png");
		allTextures.add(starEmpty);

		// pig death textures
		Texture smoke0 = new Texture("animations/smoke0.png");
		Texture smoke1 = new Texture("animations/smoke1.png");
		Texture smoke2 = new Texture("animations/smoke2.png");
		Texture smoke3 = new Texture("animations/smoke3.png");
		Texture smoke4 = new Texture("animations/smoke4.png");
		smokeAni = new Texture[] {smoke0, smoke1, smoke2, smoke3, smoke4};
		allTextures.addAll(Arrays.asList(smokeAni));

		// bird contact textures
		Texture featherRed = new Texture("other/redfeather.png");
		Texture featherBlue = new Texture("other/bluefeather.png");
		Texture featherYellow = new Texture("other/yellowfeather.png");
		Texture featherBig = new Texture("other/bigfeather.png");
		featherPics = new Texture[] {featherRed, featherBlue, featherYellow, featherBig};
		allTextures.addAll(Arrays.asList(featherPics));

		// mouse position
		mx = Gdx.input.getX() / 2;
		my = (800 - Gdx.input.getY()) / 2;

		slingx = slingshotx-10; //reset sling
		slingy = slingshoty-10;//reset sling
		ang = 60*TORADIANS; // reset sling angle

		if(Level>0) { // if not mapmaker: load level, start level
			loadLevel(Level);
			startGame();
		}
	}

	@Override
	public void show() {}

	@Override
	public void render (float delta) { // method for rendering all textures

		// do game calculations first, then draw screen
		update(Gdx.graphics.getDeltaTime()); // amount of time between frame refreshes

//		Gdx.gl.glClearColor(0f, 0f, 0f, 1f); //forgot what this does
//		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin(); // start drawing stuff

		drawBackground();

		// draw the trail of birds
		for(Body bird : currentBird) {
			drawTraj(bird);
		}
		if(prevBird != null){
			for(Body bird : prevBird) {
				drawTraj(bird);
			}
		}

		// draw slingshot
		// backside of slingshot
		batch.draw(ssBack, slingshotx*SCALE/SCALE, slingshoty/SCALE, ssBl, ssBw);
		batch.end();
		drawSlingString(0);
		batch.begin();

		drawBirds();

		// frontside of slingshot
		batch.draw(ssFront, (slingshotx*SCALE-15*2)/SCALE, (slingshoty+84)/SCALE, ssFl, ssFw);
		batch.end();
		drawSlingString(1);
		batch.begin();
		TextureRegion tmp = new TextureRegion(ssSeat);
		batch.draw(tmp,slingx, slingy, ssSl, ssSw, ssSl, ssSw, 1, 1, (float)(ang*TODEGREES)); // seat of sling

		drawBlocks();
		drawPigs();

		drawAnimations();
		drawFeathers();

		drawBirdOrder();

		drawEndScreen(winorlose);
		drawButtons();

		if(Level == MAPMAKER){
			drawMapmakerUI();
		}

		batch.end(); // stop drawing

//		b2dr.render(world, camera.combined.scl(PPM)); //uncomment to show sides of all Body objects
	}
	private void drawBackground(){ // method for drawing the background images, has parts that move at different speeds
		for(int i=0; i<BKGsprites.length; i++){
			for(int j=0; j<BKGdupnums[i]; j++){
				batch.draw(BKGsprites[i], // picture
						(BKGx[i] + BKGscrollspeed[i]*camerax + 4000/BKGdupnums[i] * j) / SCALE, // x
						(BKGy[i] + BKGscrollspeed[i]*cameray) / SCALE, // y
						BKGw[i] / SCALE, BKGh[i] / SCALE); // width and height
			}
		}
	}

	private void drawBirds(){ // method for drawing all birds
		int i = 0;
		for(ArrayList<Body> list : AllBirds){
			TextureRegion img = new TextureRegion(birdPics[i]);
			for(Body bird : list){
				batch.draw(img, // picture
						bird.getPosition().x * PPM - birdRadius[i]/2, // x
						bird.getPosition().y * PPM - birdRadius[i]/2, // y
						birdRadius[i]/2, birdRadius[i]/2, //rotation origin
						birdRadius[i], birdRadius[i], // width and height
						1, 1, (float)(bird.getAngle()*TODEGREES)); //rotation angle
			}
			i++;
		}
	}
	private void drawBlocks(){ // method for drawing all blocks
		int i = 0;
		for(ArrayList<Body> list : AllBlocks) {
			for (Body block : list) {
				ObjectUserData data = (ObjectUserData) block.getUserData();
				drawBlock(blockPics[i + 4*numBlockTypes - data.getHealth()*numBlockTypes], block, blockWidths[i], blockHeights[i]);
			}
			i++;
		}
	}

	private void drawBlock(Texture t, Body b, int w, int h){ // method for drawing a specified block
		TextureRegion img = new TextureRegion(t);
		batch.draw(img, //picture
				b.getPosition().x * PPM - w/2, // x
				b.getPosition().y * PPM - h/2, // y
				w/2 , h/2, // rotation origin
				w, h, 1, 1, (float)(b.getAngle()*TODEGREES)); //length, height, rotation angle
	}

	private void drawPigs(){ // method for drawing all pigs
		int i=0;
		for(ArrayList<Body> list : AllPigs){
			for(Body pig : list){
				ObjectUserData data = (ObjectUserData) pig.getUserData();
				drawPig(pigPics[i + 3*numPigTypes - data.getHealth()*numPigTypes], pig, pigRadius);
			}
			i++;
		}
	}

	private void drawPig(Texture t, Body b, int r){ // method for drawiing a specified pig
		TextureRegion img = new TextureRegion(t);
		batch.draw(img, // picture
				b.getPosition().x * PPM - r/2, // x
				b.getPosition().y * PPM - r/2,// y
				r/2, r/2, // rotation origin
				r, r, 1, 1, (float)(b.getAngle()*TODEGREES));// width, height, rotation angle
	}

	private void drawBirdOrder(){ // method for drawing the next birds that are waiting to be launched
		int offset = 0; // distance to offset birds to give them space
		for(int j=birdSpawnPos+1; j<birdSpawnOrder.size(); j++){
			offset += birdRadius[birdSpawnOrder.get(j)-1]; // increase offset with bird size
			batch.draw(birdPics[birdSpawnOrder.get(j)-1], //picture
					400/SCALE - offset, 170/SCALE, // x, y
					birdRadius[birdSpawnOrder.get(j)-1], birdRadius[birdSpawnOrder.get(j)-1]); //width, height
		}
	}

	private void drawSlingString(int type){ // method for drawing the strings on sling, 'type' is for front or back string
		sr.setProjectionMatrix(camera.combined); //drawing settings
		sr.begin(ShapeRenderer.ShapeType.Filled);
		sr.setColor(48/256F, 23/256F, 8/256F, 1);

		//calculate the distance between mouse and middle of slingshot(max distance of 55)
		float dx = mx - slingshotx;
		float dy = my - slingshoty;
		double dist = Math.sqrt(dx * dx + dy * dy);
		if(dist>55){
			dist = 55;
		}

		float width = (float) (5 - 2 * dist/55); // width of line, gets thinner the farther it is

		if(onSling) { // draw if bird is on the slingshot
			if (type == 0) { //back sling
				sr.rectLine(480/SCALE, 170,
						(float) (currentBird.get(0).getPosition().x * PPM - (ssSl) * Math.cos(ang + 30 * TORADIANS)),//x
						(float) (currentBird.get(0).getPosition().y * PPM - ssSw * Math.sin(ang + 30 * TORADIANS)),// y
						width); // width of line
			}
			if (type == 1) { //front sling
				sr.rectLine(430/SCALE, 175,
						(float) (currentBird.get(0).getPosition().x * PPM - (ssSl) * Math.cos(ang + 30 * TORADIANS)),//x
						(float) (currentBird.get(0).getPosition().y * PPM - ssSw * Math.sin(ang + 30 * TORADIANS)),// y
						width); // width of line
			}
		}
		sr.end();// stop draw
	}

	private void drawTraj(Body obj){ // method for drawing the trail of bird's path
		ObjectUserData data = (ObjectUserData) obj.getUserData();
		ArrayList<Point> list = data.getTrail(); // list of points to draw

		if(data.getUseAbility() > 0){ // draw where the ability was used
			batch.draw(trailAbility, // picture
					data.getAbilityPoint().getX() * PPM - birdRadius[data.getType() - 1]/2, // x
					data.getAbilityPoint().getY() * PPM - birdRadius[data.getType() - 1]/2, // y
					trailAbility.getWidth()/SCALE, trailAbility.getHeight()/SCALE); // width, height
		}

		for(int i=0; i<list.size(); i++){ // draw trail
			int x = (int) (list.get(i).getX() * PPM);
			int y = (int) (list.get(i).getY() * PPM);

			batch.draw(trailPics[i%3], x, y, // picture, x, y
					trailPics[i%3].getWidth()/SCALE, trailPics[i%3].getHeight()/SCALE); // width, height
		}
	}

	private void drawButtons(){ // method for drawing all the buttons
		if(!gameOver) {
			drawButton(restartButton);
			drawButton(gomenuButton);
		}
		if(gameOver){
			drawButton(endRestartButton);
			drawButton(endGomenuButton);
			if(winorlose != null){
				if(winorlose.equals("W")){
					drawButton(endNextlevelButton);
				}
			}
		}
	}

	private void drawButton(Button b){ // method for drawing specified button, gets bigger when mouse is hovering over
		if (b.getIsHover()) {
			batch.draw(b.getTexture(), // picture
					b.getCenterX() - b.getWidth() * buttonHoverFactor / 2 + camerax, // x
					b.getCenterY() - b.getHeight() * buttonHoverFactor / 2 + cameray, // y
					b.getWidth() * buttonHoverFactor, b.getHeight() * buttonHoverFactor); // width, height
		}
		else {
			batch.draw(b.getTexture(), // picture
					b.getCenterX() - b.getWidth() / 2 + camerax, // x
					b.getCenterY() - b.getHeight() / 2 + cameray, // y
					b.getWidth(), b.getHeight()); // width, height
		}
	}

	private void drawEndScreen(String wl){ // method for drawing the end screen
		if(wl != null) {
			batch.draw(gameOverBack, camerax, cameray, 1000 / SCALE, 800 / SCALE); // black background
			font40.draw(batch, "Level " + Level, 425 / SCALE + camerax, 650 / SCALE + cameray); // level text
			if (wl.equals("W")) { // if game is win
				font30.draw(batch, "Cleared!", 425 / SCALE + camerax, 600 / SCALE + cameray); // win text
				batch.draw(starComplete, //picture
						500 / SCALE + camerax - 220 / 2 / SCALE, 425 / SCALE + cameray - 200 / 2 / SCALE,// x, y
						220 / SCALE, 200 / SCALE);// width, height
			}
			if (wl.equals("L")) { // if game is lose
				font30.draw(batch, "Failed!", 425 / SCALE + camerax, 600 / SCALE + cameray); // lose text
				batch.draw(starEmpty, //picture
						500 / SCALE + camerax - 220 / 2 / SCALE, 425 / SCALE + cameray - 200 / 2 / SCALE,// x, y
						220 / SCALE, 200 / SCALE); // width, height
			}
		}
	}

	private void drawMapmakerUI(){ // method for drawing the UI of mapmaker
		//draw the current 'block' type player is current using
		if(blockType < 6) { // wood blocks
			TextureRegion img = new TextureRegion(blockPics[blockType]);
			float w = blockWidths[blockType];
			float h = blockHeights[blockType];
			batch.draw(img,
					1000 / SCALE - (isRotated ? h : w) + camerax, // x
					800 / SCALE - (isRotated ? w : h) + cameray, // y
					(isRotated ? h / 2 : w / 2), (isRotated ? w / 2 : h / 2), w, h, // rotation origins, width, height
					1, 1, isRotated ? -90 : 0); // rotation angle
		}
		else{ // pigs
			Texture img = pigPics[blockType-8];
			batch.draw(img, // picture
					1000 / SCALE - pigRadius + camerax, // x
					800 / SCALE - pigRadius + cameray, // y
					pigRadius, pigRadius); // width, height
		}

		//draw instructions
		font20.draw(batch,
				"LEVEL EDITOR/SANDBOX\n" +
						"NUM_1 = 4x2 block\n" +
						"NUM_2 = 8x1 block\n" +
						"NUM_3 = 2x2 block\n" +
						"NUM_8 = normal pig\n" +
						"NUM_9 = helmet pig\n" +
						"KEY_TAB = rotate block\n" +
						"KEY_V = red bird\n" +
						"KEY_B = blue bird\n" +
						"KEY_N = yellow bird\n" +
						"KEY_M = big bird\n" +
						"KEY_ENTER = start physics\n" +
						"ARROW_UP = zoom out\n" +
						"ARROW_DOWN = reset zoom\n" +
						"ARROW_LEFT = move camera left\n" +
						"ARROW_RIGHT = move camera right",
				0+camerax, 700/2+cameray); // x, y
	}

	private void drawAnimations(){ // method for drawing pig death animations
		List<float[]> removeList = new ArrayList<>(); // list to remove finished animations
		for(float[] point : pigDieAniPoints){
			Texture img = smokeAni[(int) point[2]];
			batch.draw(img,
					point[0] * PPM - img.getWidth() / 2 / SCALE, point[1] * PPM - img.getHeight() / 2 / SCALE,//x,y
					img.getWidth()/SCALE, img.getHeight()/SCALE); // width, height
			point[2]+=0.25; // increase animation frame
			if(point[2]>4){ // if animation is finished, remove animation from list
				removeList.add(point);
			}
		}
		for(float[] removePoint : removeList){
			pigDieAniPoints.remove(removePoint); // remove from list
		}
	}

	private void drawFeathers(){ // method for drawing bird feathers
		List<float[]> removeList = new ArrayList<>(); // list for removing
		for(float[] feather : feathers){
			TextureRegion img = new TextureRegion(featherPics[(int) (feather[4]-1)]);
			batch.draw(img, // picture
					feather[0]*PPM, feather[1]*PPM, // x, y
					0, 0, // origin of rotation
					img.getRegionWidth()*feather[3]/SCALE, img.getRegionHeight()*feather[3]/SCALE, // width, height
					1, 1, feather[2]); // angle of rotation
			feather[3] -= 0.02; // decrease size
			if(feather[3] < 0.4){ // remove feather is too small
				if(Math.random() > 0.2){ // random chance to remove(so it doesnt all disappear at the same time)
					removeList.add(feather);
				}
			}
		}
		for(float[] removeFeather : removeList){ // remove from list
			feathers.remove(removeFeather);
		}
	}


	@Override
	public void resize(int width, int height) { // method for resizing the camera
		camera.setToOrtho(false, width/SCALE, height/SCALE);
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose () { // method for disposing of unused stuff, good for resource management,helps it run smoother
		world.dispose();
		b2dr.dispose();
		batch.dispose();
		font40.dispose();
		font30.dispose();
		font20.dispose();
		for(Texture t : allTextures){
			t.dispose();
		}
	}

	public void update(float delta){ // method for updating all game actions, delta is time between frames
		world.step(1/60f, 6, 2); // do stuff 60 times a second(60FPS)(always use 6, 2)

		mousePosUpdate();
		buttonUpdate();
		inputUpdate(delta);
		playerUpdate(delta);
		pigUpdate(delta);
		blockUpdate(delta);
		boundaryUpdate(delta);
		slingshotUpdate(delta);
		cameraUpdate(delta);
		if(!gameOver){nextbirdUpdate(delta);}
		checkGameEndUpdate(delta);

		batch.setProjectionMatrix(camera.combined);
	}

	public void mousePosUpdate(){ // method for updating mouse x,y positions
		mx = Gdx.input.getX() / 2F * cameraScale + camerax;
		my = (800 - Gdx.input.getY()) / 2F * cameraScale + cameray;
	}

	public void buttonUpdate(){ // method for checking if user is hovering over a button
		restartButton.checkIsHover(mx-camerax, my-cameray);
		gomenuButton.checkIsHover(mx-camerax, my-cameray);
		endRestartButton.checkIsHover(mx-camerax, my-cameray);
		endGomenuButton.checkIsHover(mx-camerax, my-cameray);
		endNextlevelButton.checkIsHover(mx-camerax, my-cameray);
	}

	public void boundaryUpdate(float delta) { // method for updating boundaries of game
		for (ArrayList[] superlist : AllObjects) { // check all objects
			for (ArrayList<Body> minilist : superlist) {
				for (Body object : minilist) {
					if(object.getPosition().x > 1800/PPM){
						object.setLinearVelocity(0, object.getLinearVelocity().y); // stop all x movements
					}
					else if(object.getPosition().x < 160/PPM){
						object.setLinearVelocity(0, object.getLinearVelocity().y);
					}
				}
			}
		}
	}

	public void slingshotUpdate(float delta){ // method for updating everything slingshot

		if(onSling) { // if bird is on sling
			float dx = slingshotx - currentBird.get(0).getPosition().x*PPM; // calculate angle of aiming
			float dy = slingshoty - currentBird.get(0).getPosition().y*PPM;
			ang = Math.atan2(dy, dx); // radians

			slingx = currentBird.get(0).getPosition().x * PPM - ssSl; // update sling x/y
			slingy = currentBird.get(0).getPosition().y * PPM - ssSw;
		}

		if(shooted){ // if bird has just been shot
			slingx = slingshotx-10; //reset sling
			slingy = slingshoty-10;//reset sling
			ang = 60*TORADIANS; // reset angle of sling

			onSling = false; // bird is no long on sling
			shooted = false; // dont want this to loop over and over
			ObjectUserData data = (ObjectUserData) currentBird.get(0).getUserData();
			data.setIsLaunching(true); // bird has launched!
		}
		if(!currentBird.isEmpty()) {
			ObjectUserData data = (ObjectUserData) currentBird.get(0).getUserData();
			if (data.getIsLaunching() == true) {
				data.increaseTimeInAir();

				if (data.getTimeInAir() % 3 == 0) { // add another smoke trail
					for (Body bird : currentBird) {
						Point point = new Point(bird.getPosition().x, bird.getPosition().y);
						data.addToTrail(point);
					}
				}
			}
		}
	}

	public void playerUpdate(float delta){// method for updating everything related to player's bird
		float dx = mx - slingshotx; // calculate distance on slingshot from slingshot
		float dy = my - slingshoty;
		double dist = Math.sqrt(dx * dx + dy * dy);
		if(dist>55){
			dist = 55;
		}

		if(!currentBird.isEmpty()) {
			ObjectUserData data = (ObjectUserData) currentBird.get(0).getUserData();
			if (shooted) { // if bird is just launched, apply force to launch bird
				int force = data.getLaunchForce();
				force *= dist / 55;
				currentBird.get(0).applyForceToCenter((float) (force * Math.cos(ang)), (float) (force * Math.sin(ang)), true);
			}

			if (data.getUseAbility() == 1) { // use bird ability
				birdUseAbility(data.getType());
				data.useAbility();
			}

			if(data.getLandPoint() != null){ // if bird has made contact with an object
				for(int i=0; i<5; i++){ // 5 feathers
					float[] tmp = {(float) (data.getLandPoint().getX() + (Math.random()*20 - 10)/PPM), // random x close to bird
							(float) (data.getLandPoint().getY() + (Math.random()*20 - 10)/PPM), // random y close to bird
							(float) (Math.random()*360), // random rotation
							1F, data.getType()}; // size scale, type of bird
					feathers.add(tmp);
				}
				data.clearLandPoint(); // dont want infinite feathers
			}
		}

		if(isShooting) { // if player is aiming on slingshot, move bird to mouse position
			double mang = Math.atan2(dy, dx);
			float newx = (float) ((slingshotx + 55 * Math.cos(mang)) / PPM);
			float newy = (float) ((slingshoty + 55 * Math.sin(mang)) / PPM);
			if (dist >= 55) {
				currentBird.get(0).setTransform(newx, newy, 0);
			}
			else {
				currentBird.get(0).setTransform(mx / PPM, my / PPM, 0);
			}
		}
	}

	public void blockUpdate(float delta){ // method for updating anything blocks

		for(ArrayList<Body> list : AllBlocks){// loop through all blocks and check if any need to be removed
			ArrayList<Body> removeList = new ArrayList<Body>();
			for(Body block : list){
				ObjectUserData data = (ObjectUserData) block.getUserData();
				if(data.getIsDead()){
					removeList.add(block);
				}
			}

			for(Body block : removeList){ // remove block
				list.remove(block);
				world.destroyBody(block);
			}
		}
	}

	public void pigUpdate(float delta){ // method for updating anything pigs

		for(ArrayList<Body> list : AllPigs){// loop through all pigs and check if any need to be removed
			ArrayList<Body> removeList = new ArrayList<Body>();
			for(Body pig : list){
				ObjectUserData data = (ObjectUserData) pig.getUserData();
				if(data.getIsDead()){
					removeList.add(pig);
				}
			}

			for(Body pig : removeList){ // remove pig
				float[] ani = {pig.getPosition().x, pig.getPosition().y, 0}; //x, y, animation frame
				pigDieAniPoints.add(ani); // add death animation
				list.remove(pig);
				world.destroyBody(pig);
			}
		}
	}

	public void inputUpdate(float delta){ // method for updating all player inputs

		if(Level == MAPMAKER) { // mapmaker special buttons
			if (Gdx.input.isKeyJustPressed(Input.Keys.V)) {
				spawnRed();
			}
			if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
				spawnBlue();
			}
			if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
				spawnYellow();
			}
			if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
				spawnBig();
			}

			/* // these keys are for dev only! (exporting level designs from mapmaker)
			if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
				for (ArrayList<Body> list : AllBlocks) {
					for (Body object : list) {
						ObjectUserData data = (ObjectUserData) object.getUserData();
						FM.writeFile("level12.txt", object.getPosition().x, object.getPosition().y, object.getAngle() == 0, data.getType(), 0);
					}
				}
				for (ArrayList<Body> list : AllPigs) {
					for (Body pig : list) {
						ObjectUserData data = (ObjectUserData) pig.getUserData();
						FM.writeFile("level12.txt", pig.getPosition().x, pig.getPosition().y, false, data.getType(), 1);
					}
				}
				for(Body bird : birdOrder){
					ObjectUserData data = (ObjectUserData) bird.getUserData();
					FM.writeFile("level12.txt", 0, 0, false, data.getType(), 2);
				}
			}
			if (Gdx.input.isKeyJustPressed(Input.Keys.O)) {
				loadLevel(7);
			}*/

			if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) { // zoom out
				cameraScale *= 3 / 2F;
				resize((int) (mapWidth * cameraScale), (int) (mapHeight * cameraScale));
			}

			if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) { // reset camera
				cameraScale = 1F;
				resize((int) (mapWidth * cameraScale), (int) (mapHeight * cameraScale));
			}

			if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) { // move camera left
				camerax -= 20;
				camera.translate(-20, 0);
			}

			if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) { // move camera right
				camerax += 20;
				camera.translate(20, 0);
			}

			if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) { // start rotation physics and block/pig health
				startGame();
			}

			if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) { // rotate block
				isRotated = !isRotated;
			}

			if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) { // right click
				if (blockType < 6) { // if type is blocks
					Body tmp = createBlock(mx, my, blockType, isRotated, 4, new ObjectUserData(blockType + 10));
					AllBlocks[blockType].add(tmp);
				}
				else { // type is pigs
					Body tmp = createPig(mx, my, blockType + 12, new ObjectUserData(blockType + 12));
					AllPigs[blockType - 8].add(tmp);
				}
			}
			// changing object placing types
			if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)){
				blockType = 0;
			}
			if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)){
				blockType = 1;
			}
			if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)){
				blockType = 2;
			}
			if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_8)){
				blockType = 8;
			}
			if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_9)){
				blockType = 9;
			}
		}

		bleftIsDown = false; // left mouse button not down
		if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)){ // left mouse button
			bleftIsDown = true; // left mouse button is down
			float dx = mx - slingshotx;
			float dy = my - slingshoty;
			double dist = Math.sqrt(dx * dx + dy * dy);
			if(dist<55 && onSling){ // aiming on slingshot
				isShooting = true;
			}
			else{
				if(!currentBird.isEmpty()) {
					ObjectUserData data = (ObjectUserData) currentBird.get(0).getUserData();
					if (data.getIsLaunching() == false) { // scrolling if player has collided
						cameraFollowPlayer = false;

						isScrolling = true;
						scrolloriginx = mx - camerax;
						scrolloriginy = my - cameray;
						cameradeltax = 0;
					}
					else { // use bird ability
						data.useAbility(currentBird.get(0).getPosition().x, currentBird.get(0).getPosition().y);
					}
				}
				else{ // scrolling camera view
					cameraFollowPlayer = false;

					isScrolling = true;
					scrolloriginx = mx - camerax;
					scrolloriginy = my - cameray;
					cameradeltax = 0;
				}
			}

			// on-screen button functions
			if(!gameOver) {
				if (restartButton.getIsHover()) { // restart button restarts level
					restartButton.setIsHover(false);
					game.setScreen(new angryBirds(game, Level));
				}
				if (gomenuButton.getIsHover()) { // gomenu button goes back to level selection menu
					gomenuButton.setIsHover(false);
					game.setScreen(new mainMenu(game, "levelselect"));
				}
			}
			if(gameOver){
				if(endRestartButton.getIsHover()){ // restart button restarts level
					endRestartButton.setIsHover(false);
					game.setScreen(new angryBirds(game, Level));
				}
				if(endGomenuButton.getIsHover()){ // gomenu button goes back to level selection menu
					endGomenuButton.setIsHover(false);
					game.setScreen(new mainMenu(game, "levelselect"));
				}
				if(winorlose != null){
					if(winorlose.equals("W")){
						if(endNextlevelButton.getIsHover()){ // go to next level button
							endNextlevelButton.setIsHover(false);
							game.setScreen(new angryBirds(game, Level+1));
						}
					}
				}
			}
		}
		if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
			bleftIsDown = true;
		}

		if(!bleftIsDown){ // if left mouse button is not down
			if(isShooting){ // if player was aiming bird, launch bird
				shooted = true;
				if(startCameraFollowPlayer) {
					cameraFollowPlayer = true;
					startCameraFollowPlayer = false;
				}
			}
			isShooting = false;

			isScrolling = false;
		}
	}

	public void startGame(){ // method for starting game functions
		for(ArrayList<Body> list : AllBlocks){ // blocks can now rotate and be damaged
			for(Body block : list){
				block.setFixedRotation(false);
				ObjectUserData data = (ObjectUserData) block.getUserData();
				data.setIsImmune(false);
			}
		}
		for(ArrayList<Body> list : AllPigs){ // pigs can now rotate and be damaged
			for(Body pig : list){
				pig.setFixedRotation(false);
				ObjectUserData data = (ObjectUserData) pig.getUserData();
				data.setIsImmune(false);
			}
		}
	}

	public void loadLevel(int level){ // method for loading a level by reading the level's txt file
		String[] file = FM.readFile("level"+level+".txt");

		// convert String into usable data, then add it to the world
		if (!file[0].isEmpty()) { // line 0 is blocks
			String[] line1 = file[0].split(" ");
			List<float[]> newblocks = new ArrayList<>();
			for (String blockinfo : line1) {
				String[] tmp = blockinfo.split(",");
				float[] toAdd = new float[4];
				for (int i = 0; i < 4; i++) {
					toAdd[i] = Float.parseFloat(tmp[i]);
				}
				newblocks.add(toAdd);
			}
			for (float[] info : newblocks) { // create all blocks
				Body block = createBlock(info[0] * PPM, info[1] * PPM, // x,y
						(int) (info[3] - 10), info[2] == 1, // type, if block is rotated
						4, new ObjectUserData((int) info[3])); // density, data
				AllBlocks[(int) (info[3] - 10)].add(block);
			}
		}
		if (!file[1].isEmpty()) { // line 1 is pigs
			String[] line2 = file[1].split(" ");
			List<float[]> newpigs = new ArrayList<>();
			for (String piginfo : line2) {
				String[] tmp = piginfo.split(",");
				float[] toAdd = new float[3];
				for (int i = 0; i < 3; i++) {
					toAdd[i] = Float.parseFloat(tmp[i]);
				}
				newpigs.add(toAdd);

			}
			for (float[] info : newpigs) {
				Body pig = createPig(info[0] * PPM, info[1] * PPM, //x, y
						4, new ObjectUserData((int) info[2])); // density, data
				AllPigs[(int) (info[2] - 20)].add(pig);
			}
		}
		if(!file[2].isEmpty()){ // line 2 is player birds
			String[] line3 = file[2].split(" ");
			for(String num : line3){
				birdSpawnOrder.add(Integer.parseInt(num));
			}
			if(!birdSpawnOrder.isEmpty()){
				Body tmp = spawnBird(birdSpawnOrder.get(birdSpawnPos)); // spawn first bird
			}
		}
	}
	private Body spawnBird(int type){ // method for spawning a specified bird type
		if(type == ObjectUserData.RED){
			return spawnRed();
		}
		if(type == ObjectUserData.BLUE){
			return spawnBlue();
		}
		if(type == ObjectUserData.YELLOW){
			return spawnYellow();
		}
		if(type == ObjectUserData.BIG){
			return spawnBig();
		}
		return null;
	}

	public void cameraUpdate(float delta) { // method for updating anything camera
		if(!gameOver) {
			if (!currentBird.isEmpty()) {
				ObjectUserData data = (ObjectUserData) currentBird.get(0).getUserData();
				if (!data.getIsLaunching()) { // if bird has hit an object, camera stops following bird
					cameraFollowPlayer = false;
				}
			}
			if (cameraFollowPlayer) { // move camera to follow bird
				camera.translate((int) (currentBird.get(0).getPosition().x * PPM - 350 / SCALE - camerax), 0);
				camerax = (int) (currentBird.get(0).getPosition().x * PPM - 350 / SCALE);
			}
			if (isScrolling) { // player moving camera
				int dx = (int) (mx - scrolloriginx - camerax);
				int borderoffset = 0;
				camerax += cameradeltax - dx;
				if (camerax < 0) {
					borderoffset = camerax;
					camerax = 0;
				}
				if (camerax > 1000) {
					borderoffset = camerax - 1000;
					camerax = 1000;
				}
				camera.translate(cameradeltax - dx - borderoffset, 0);
				cameradeltax = dx;
			}
		}
		camera.update();

	}

	public void nextbirdUpdate(float delta){ // method for loading up the next bird to launch
		// check if any objects are moving
		if(!onSling) {
			if (!currentBird.isEmpty()) {
				ObjectUserData data = (ObjectUserData) currentBird.get(0).getUserData();
				if (!data.getIsLaunching()) {
					int i = 0;
					boolean somethingismoving = false;
					for (ArrayList[] superlist : AllObjects) {
						for (ArrayList<Body> minilist : superlist) {
							for (Body object : minilist) {
								i++;
								float dx = object.getLinearVelocity().x; // calculate object's velocity
								float dy = object.getLinearVelocity().y;
								float dh = (float) Math.sqrt(dx * dx + dy * dy);
								if (dh > 0.1) {
									somethingismoving = true;
								}
							}
						}
					}

					if (!somethingismoving) { // nothing is moving, spawn next bird
						birdSpawnPos += 1;
						if(birdSpawnPos+1 <= birdSpawnOrder.size()) {
							spawnBird(birdSpawnOrder.get(birdSpawnPos));
						}
					}
				}
			}
		}
	}

	public void checkGameEndUpdate(float delta){ // method for checking if the game is over

		if(Level != MAPMAKER) { // mapmaker has no end..
			boolean pigGameOver = true;
			boolean birdGameOver = false;

			for (ArrayList<Body> list : AllPigs) { // check if game is over due to pigs all dying
				if (!list.isEmpty()) {
					pigGameOver = false;
				}
			}

			if (birdSpawnPos >= birdSpawnOrder.size()) { // check if game is over due to no more birds left
				birdGameOver = true;
			}

			if (pigGameOver || birdGameOver) {
				if (pigGameOver) { // win
					gameOver = true;
					winorlose = "W";
					FM.writeLevelProgress(Level);
				}
				else { // lose
					gameOver = true;
					winorlose = "L";
				}
			}
		}
	}

	public Body createBox(float x, float y, int width, int height, boolean isStatic, boolean isAwake, boolean isRotated, float density, ObjectUserData userData){
		// method for creating box shaped objects
		Body pBody;
		BodyDef def = new BodyDef();

		if(isStatic){
			def.type = BodyDef.BodyType.StaticBody;
		}
		else{
			def.type = BodyDef.BodyType.DynamicBody;
		}
		def.position.set(x / PPM, y / PPM);
		def.fixedRotation = true;
		def.awake = isAwake;
		if(isRotated){
			def.angle = (float)Math.PI/2;
		}
		pBody = world.createBody(def);

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width/2 / PPM, height/2 / PPM); // hx and hy is distance from center on both sides -> length = 2 x hx

		FixtureDef fd = new FixtureDef();
		fd.shape = shape;
		fd.density = density;
		fd.friction = 1F;
		pBody.createFixture(fd);

		pBody.setUserData(userData);
		shape.dispose(); // keeps resource management clean

		return pBody;
	}

	public Body createCircle(float x, float y, int radius, boolean isStatic, boolean isAwake, boolean isRotated, float density, ObjectUserData userData){
		// method for creating circle objects
		Body pBody;
		BodyDef def = new BodyDef();

		if(isStatic){
			def.type = BodyDef.BodyType.StaticBody;
		}
		else{
			def.type = BodyDef.BodyType.DynamicBody;
		}
		def.position.set(x / PPM, y / PPM);
		def.fixedRotation = false;
		def.awake = isAwake;
		if(isRotated){
			def.angle = (float)Math.PI/2;
		}

		pBody = world.createBody(def);

		CircleShape shape = new CircleShape();
		shape.setRadius(radius/2 / PPM); // hx and hy is distance from center on both sides -> length = 2 x hx

		FixtureDef fd = new FixtureDef();
		fd.shape = shape;
		fd.density = density;
		fd.restitution = 0.4F;
		fd.friction = 1F;
		pBody.setAngularDamping(3F);
		pBody.createFixture(fd);
		pBody.setUserData(userData);

		shape.dispose(); // keeps resource management clean

		return pBody;
	}

	public Body createBlock(float x, float y, int type, boolean isRotated, float density, ObjectUserData userData){
		// method for creating blocks with given type
		int width = blockWidths[type];
		int height = blockHeights[type];

		return createBox(x, y, width, height, false, true, isRotated, density, userData);
	}

	public Body createPig(float x, float y, float density, ObjectUserData data){ // method for creating pigs with given type
		return createCircle(x, y, pigRadius, false, true, false, density, data);
	}

	public Body spawnRed(){ // method for spawning a red bird on slingshot
		Body tmp = createCircle(slingshotx, slingshoty, redr, false, false, false, 8, new ObjectUserData(ObjectUserData.RED));
		redBirds.add(tmp);
		birdOrder.add(tmp);

		prevBird.clear();
		prevBird = (ArrayList<Body>) currentBird.clone();

		currentBird.clear();
		currentBird.add(tmp);

		resetSling();
		return tmp;
	}
	public Body spawnBlue(){// method for spawning a blue bird on slingshot
		Body tmp = createCircle(slingshotx, slingshoty, bluer, false, false, false, 8, new ObjectUserData(ObjectUserData.BLUE));
		blueBirds.add(tmp);
		birdOrder.add(tmp);

		prevBird.clear();
		prevBird = (ArrayList<Body>) currentBird.clone();

		currentBird.clear();
		currentBird.add(tmp);

		resetSling();
		return tmp;
	}
	public void spawnBlue(boolean ability){ // method for spawning blue birds from it's ability
		Body obird = currentBird.get(0);
		float dx = obird.getLinearVelocity().x;
		float dy = obird.getLinearVelocity().y;
		float dh = (float) Math.sqrt(dx*dx+dy*dy);
		float dang = (float) Math.atan2(dy, dx); // radians
		float ang1 = (float) (dang + 15*TORADIANS);
		float ang2 = (float) (dang - 15*TORADIANS);
		Body tmp1 = createCircle(obird.getPosition().x*PPM, obird.getPosition().y*PPM+10, bluer, false, true, false, 8, new ObjectUserData(ObjectUserData.BLUE));
		Body tmp2 = createCircle(obird.getPosition().x*PPM, obird.getPosition().y*PPM-10, bluer, false, true, false, 8, new ObjectUserData(ObjectUserData.BLUE));
		tmp1.setLinearVelocity((float) (dh * Math.cos(ang1)), (float) (dh * Math.sin(ang1)));
		tmp2.setLinearVelocity((float) (dh * Math.cos(ang2)), (float) (dh * Math.sin(ang2)));
		blueBirds.add(tmp1);
		blueBirds.add(tmp2);

		currentBird.add(tmp1);
		currentBird.add(tmp2);
	}
	public Body spawnYellow(){// method for spawning a yellow bird on slingshot
		Body tmp = createCircle(slingshotx, slingshoty, yellowr, false, false, false, 8, new ObjectUserData(ObjectUserData.YELLOW));
		yellowBirds.add(tmp);
		birdOrder.add(tmp);

		prevBird.clear();
		prevBird = (ArrayList<Body>) currentBird.clone();

		currentBird.clear();
		currentBird.add(tmp);

		resetSling();
		return tmp;
	}
	public Body spawnBig(){// method for spawning a big bird on slingshot
		Body tmp = createCircle(slingshotx, slingshoty, bigr, false, false, false, 8, new ObjectUserData(ObjectUserData.BIG));
		bigBirds.add(tmp);
		birdOrder.add(tmp);

		prevBird.clear();
		prevBird = (ArrayList<Body>) currentBird.clone();

		currentBird.clear();
		currentBird.add(tmp);

		resetSling();
		return tmp;
	}

	public void resetSling(){ // method for reseting the slingshot
		isShooting = false;
		shooted = false;
		onSling = true;
		cameraFollowPlayer = false;
		startCameraFollowPlayer = true;
		isScrolling = false;
		resetCamera();
	}

	public void resetCamera(){ // method for reseting camera back to 0,0
		camera.translate(-camerax, -cameray);
		camerax = 0;
		cameray = 0;
	}

	public void birdUseAbility(int type){ // method for using bird abilities
		if(type == ObjectUserData.RED){return;} // red bird has no ability
		if(type == ObjectUserData.BLUE){ // blue bird ability spawns 2 duplicates
			spawnBlue(true);
		}
		if(type == ObjectUserData.YELLOW){ // yellow bird ability increases its velocity
			ObjectUserData data = (ObjectUserData) currentBird.get(0).getUserData();
			Body bird = currentBird.get(0);
			bird.applyForceToCenter((float) (data.getLaunchForce() * Math.cos(bird.getAngle())),
					(float) (data.getLaunchForce() * Math.sin(bird.getAngle())), true);
		}
		if(type == ObjectUserData.BIG){return;} // big bird is big. no ability. just big.
	}
}
