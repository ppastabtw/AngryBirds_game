/*
File Name:
Author: Kevin Yan

This class is for storing all data related to Body objects.
 */
package com.mygdx.angrybirds.utils;

import java.util.ArrayList;

public class ObjectUserData {
    // object types
    public final static int GROUND = 0;
    public final static int RED = 1;
    public final static int BLUE = 2;
    public final static int YELLOW = 3;
    public final static int BIG = 4;
    public final static int WOOD0 = 10;
    public final static int WOOD1 = 11;
    public final static int WOOD2 = 12;
    public final static int NORMALPIG = 20;
    public final static int HELMETPIG = 21;

    private int objType; // this object type
    private boolean isLaunching; // if the bird is in air
    private int timeInAir; // number of frames the bird is flying in the air
    private ArrayList<Point> trail; // list of a bird's trail smoke
    private int health; // object's heatlh
    private boolean isDead = false; // object is not dead
    private int launchForce; // bird's launch force from slingshot
    private int abilityUse = 0; // if bird has used ability yet(0=not used, 1=activate ability, 2=used)
    private Point abilityPoint; // point where ability was used
    private boolean isImmune; // is object is immune(does not lose health)
    private Point landPoint; // point where bird landed
    public ObjectUserData(int type){ // initialize data
        objType = type;
        if(type == GROUND){}
        if(type == RED || type == BLUE || type == YELLOW || type == BIG){ // bird initialize
            isLaunching = false;
            timeInAir = 0;
            trail = new ArrayList<Point>();
        }
        // launch force initialize
        if(type == RED){launchForce = 4000;}
        if(type == BLUE){launchForce = 2000;}
        if(type == YELLOW){launchForce = 4800;}
        if(type == BIG){launchForce = 16000;}
        if(type == WOOD0 || type == WOOD1 || type == WOOD2){ // blocks(wood) initalize
            health = 4;
            isImmune = true;
        }
        if(type == NORMALPIG || type == HELMETPIG){ // pig initialize
            health = 3;
            isImmune = true;
        }
    }

    public int getType(){
        return objType;
    }
    public boolean getIsLaunching(){return isLaunching;}
    public void setIsLaunching(boolean b){isLaunching = b;}
    public int getTimeInAir(){return timeInAir;}
    public void increaseTimeInAir(){timeInAir++;}
    public ArrayList<Point> getTrail(){return trail;}
    public void addToTrail(Point point){trail.add(point);}
    public int getHealth(){return health;}
    public void loseHealth(){health--;};
    public boolean getIsDead(){return isDead;}
    public void setDead(boolean b){isDead = b;}
    public int getLaunchForce(){return launchForce;}
    public void useAbility(){abilityUse++;}
    public void useAbility(float mx, float my){
        if(abilityUse == 0){
            abilityPoint = new Point(mx, my);
        }
        useAbility();
    }
    public int getUseAbility(){return abilityUse;}
    public Point getAbilityPoint(){return abilityPoint;}
    public boolean getIsImmune(){return isImmune;}
    public void setIsImmune(boolean b){isImmune = b;}
    public Point getLandPoint(){return landPoint;}
    public void setLandPoint(float x, float y){landPoint = new Point(x, y);}
    public void clearLandPoint(){landPoint = null;}
}
