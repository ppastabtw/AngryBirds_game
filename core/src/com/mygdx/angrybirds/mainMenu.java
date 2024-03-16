/*
File Name:
Author: Kevin Yan

This class is where the mainmenu code is run. It draws all the pictures and icons for it's menu and lets all the buttons
work.
 */
package com.mygdx.angrybirds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.mygdx.angrybirds.utils.Button;
import com.mygdx.angrybirds.utils.FileManager;

import java.util.ArrayList;

public class mainMenu implements Screen {
    final Drop game;
    private FileManager FM = new FileManager(); // reads/writes to file
    public BitmapFont font; // font stuff

    OrthographicCamera camera; // camera
//    Box2DDebugRenderer b2dr;
    private SpriteBatch batch; // used for drawing textures on screen
    private Texture BKmenu0, BKmenu1, BKLS, BKRS, BKlogo, BKtrans, // background sprites
            ButtonPlay, SelectLevel, ButtonBack ,ButtonLevel0, ButtonLevel1, ButtonMapmaker; // button sprites
    private ArrayList<Button> buttonsMenu0 = new ArrayList<>(); // list of buttons for mainmenu
    private ArrayList<Button> buttonsMenu1 = new ArrayList<>(); // list of buttons for level selection
    private ArrayList<Button> buttonsLevels = new ArrayList<>(); // level buttons
    private Button playButton, backButton, mapmakerButton;
    private Button level1Button, level2Button,level3Button,level4Button,level5Button,level6Button,level7Button,level8Button,level9Button,level10Button,level11Button,level12Button;
    private Button[] buttontmp = {level1Button, level2Button,level3Button,level4Button,level5Button,level6Button,level7Button,level8Button,level9Button,level10Button,level11Button,level12Button};
    private float buttonHoverFactor = 1.1F; // factor for increasing size of button when hovering over
    private float mx, my; // mouse x/y position
    String screen; // which screen it should be

    public mainMenu(final Drop game, String sscreen){ // initalize
        this.game = game;
        screen = sscreen;

        // initialize font
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("assets/angrybirds-regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 40;
        parameter.borderColor.set(Color.BLACK);
        parameter.borderWidth = 2;
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS;

        font = generator.generateFont(parameter);
        generator.dispose();

        // screen width and height
        float w = 1000;
        float h = 800;

        // initialize background
        Texture bkg = new Texture(Gdx.files.internal("pictures/background.png"));
        Sprite mapSprite = new Sprite(bkg);
        mapSprite.setPosition(0, 0);
        mapSprite.setSize(w/2, h/2);

        // initialize camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, w/2, h/2);

//        b2dr = new Box2DDebugRenderer();

        // render sprites
        batch = new SpriteBatch();
        BKmenu0 = new Texture("background/Menu0.png");
        BKmenu1 = new Texture("background/Menu1.png");
        BKLS = new Texture("background/LS.png");
        BKRS = new Texture("background/RS.png");
        BKlogo = new Texture("other/logo.png");
        ButtonPlay = new Texture("other/playButton.png");
        SelectLevel = new Texture("other/SelectLevel.png");
        BKtrans = new Texture("background/transparentBack.png");
        ButtonBack = new Texture("other/backButton.png");
        ButtonLevel0 = new Texture("other/levelButton0.png");
        ButtonLevel1 = new Texture("other/levelButton1.png");
        ButtonMapmaker = new Texture("other/mapMakeButton.png");

        // initialze buttons
        playButton = new Button(500, 350, 360, 240);
        playButton.setTexture(ButtonPlay);
        buttonsMenu0.add(playButton);

        backButton = new Button(15, 15, 35, 35);
        backButton.setTexture(ButtonBack);
        buttonsMenu1.add(backButton);

        mapmakerButton = new Button(900, 350, 60, 60);
        mapmakerButton.setTexture(ButtonMapmaker);
        buttonsMenu1.add(mapmakerButton);

        // initalize level buttons
        boolean[] levelprogress = FM.readLevelProgress();
        for(int i=0; i<12; i++){
            Button tmp = buttontmp[i];
            tmp = new Button(250 + (i%6)*100, 800 - (380 + ((int)i/6)*125), 60, 75);
            if(levelprogress[i]){
                tmp.setTexture(ButtonLevel1); // level completed texture
            }
            else{
                tmp.setTexture(ButtonLevel0); // level imcomplete texture
            }
            buttonsLevels.add(tmp);
        }
    }

    @Override
    public void render (float delta) { // method for drawing all pictures on screen

        update(Gdx.graphics.getDeltaTime()); // update stuff

        batch.begin();
        if(screen.equals("mainmenu")) {
            batch.draw(BKmenu0, 0, 0); // draw background
            batch.draw(BKlogo, 175, 525); // draw logo
            // draw play button
            if (playButton.getIsHover()) {
                batch.draw(playButton.getTexture(), playButton.getCenterX() - playButton.getWidth() * buttonHoverFactor/2,
                        playButton.getCenterY() - playButton.getHeight() * buttonHoverFactor/2,
                        playButton.getWidth() * buttonHoverFactor,
                        playButton.getHeight() * buttonHoverFactor);
            }
            else {
                batch.draw(playButton.getTexture(), playButton.getX(), playButton.getY(), playButton.getWidth(), playButton.getHeight());
            }
        }

        if(screen.equals("levelselect")){
            batch.draw(BKmenu1, 0, 0, 1000, 800); // level selection background
            batch.draw(BKLS, 0, 0, 175, 200); // birds
            batch.draw(BKRS, 1000-195, 0, 195, 165); // pigs
            batch.draw(SelectLevel, 500-320*3/2/2, 800-325+60*3/2, 320*3/2, 60*3/2); // select level logo
            batch.draw(BKtrans, 150, 800-650, 700, 400); // level back plate
            // draw back button
            if(backButton.getIsHover()){
                batch.draw(backButton.getTexture(), backButton.getX(), backButton.getY(),
                        100*buttonHoverFactor, 100*buttonHoverFactor);
            }
            else{
                batch.draw(backButton.getTexture(), backButton.getX(), backButton.getY(),
                        100, 100);
            }
            //draw mapmaker button
            if(mapmakerButton.getIsHover()){
                batch.draw(mapmakerButton.getTexture(), mapmakerButton.getCenterX()- mapmakerButton.getWidth() * buttonHoverFactor/2,
                        mapmakerButton.getCenterY()- mapmakerButton.getHeight() * buttonHoverFactor/2, mapmakerButton.getWidth() * buttonHoverFactor, mapmakerButton.getHeight() * buttonHoverFactor);
            }
            else{
                batch.draw(mapmakerButton.getTexture(), mapmakerButton.getX(), mapmakerButton.getY(), mapmakerButton.getWidth(), mapmakerButton.getHeight());
            }
            //draw all level buttons
            int i = 1;
            for(Button button : buttonsLevels){
                if(button.getIsHover()) {
                    batch.draw(button.getTexture(), button.getCenterX() - button.getWidth() * buttonHoverFactor / 2,
                            button.getCenterY() - button.getHeight() * buttonHoverFactor / 2,
                            button.getWidth() * buttonHoverFactor, button.getHeight() * buttonHoverFactor);
                }
                else{
                    batch.draw(button.getTexture(), button.getX(), button.getY(), button.getWidth(), button.getHeight());
                }
                font.draw(batch, i+"", button.getCenterX()-(i<10 ? 10 : 20), button.getCenterY()+23);
                i++;
            }
        }
        batch.end();

    }

    @Override
    public void show() {}


    @Override
    public void resize(int width, int height) { // method for resizing camera
        camera.setToOrtho(false, width/2, height/2);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose () { // method for disposing of unused stuff, good for resource management,helps it run smoother
        batch.dispose();
        font.dispose();
    }
    public void update(float delta){// method for updating all user actions, delta is time between frames
        mousePosUpdate();
        buttonsUpdate();
        inputUpdate();
        startGameUpdate();
    }

    public void mousePosUpdate(){ // method for updating mouse position on screen
        mx = Gdx.input.getX() / 2F;
        my = (800 - Gdx.input.getY()) / 2F;
    }

    public void buttonsUpdate(){// method for checking if user is hovering over a button
        if(screen.equals("mainmenu")) {
            playButton.checkIsHover(mx*2, my*2);
        }

        if(screen.equals("levelselect")){
            backButton.checkIsHover(mx, my);
            mapmakerButton.checkIsHover(mx*2, my*2);
            for(Button button : buttonsLevels){
                button.checkIsHover(mx*2, my*2);
            }
        }

    }

    public void inputUpdate(){ // method for updating all player inputs
        // checks if click is on button and performs the buttons action
        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)){ // left click
            if(screen.equals("mainmenu")) {
                if (playButton.getIsHover()) {
                    playButton.setIsHover(false);
                    screen = "levelselect";
                }
            }

            if(screen.equals("levelselect")){
                if(backButton.getIsHover()){
                    backButton.setIsHover(false);
                    screen = "mainmenu";
                }
                if(mapmakerButton.getIsHover()){
                    mapmakerButton.setIsHover(false);
                    screen = "makemaker";
                }

                for(int i=0; i<12;i++){ // level buttons
                    if(buttonsLevels.get(i).getIsHover()){
                        buttonsLevels.get(i).setIsHover(false);
                        screen = "gamelevel"+(i+1<10 ? "0" : "")+(i+1);
                    }
                }
            }

        }
    }

    public void startGameUpdate(){ // function to check if it is time to start game!
        if(screen.equals("makemaker")){
            game.setScreen(new angryBirds(game, 0));
        }
        if(screen.length() >= 9) {
            if (screen.substring(0, 9).equals("gamelevel")) {
                game.setScreen(new angryBirds(game, Integer.parseInt(screen.substring(9), screen.length() - 1)));
            }
        }
    }
}
