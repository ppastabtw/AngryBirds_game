/*
File Name:
Author: Kevin Yan

This class resolves all collision in the game(the real physics being done is inside box2D, but this class lets me
perform actions when contacts happen)
 */
package com.mygdx.angrybirds.utils;

import com.badlogic.gdx.physics.box2d.*;

public class ListenerClass implements ContactListener {

    @Override
    public void beginContact(Contact contact) { // method for when two objects just start to contact
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        if(fa == null || fb == null) return;
        ObjectUserData faData = (ObjectUserData) fa.getBody().getUserData();
        ObjectUserData fbData = (ObjectUserData) fb.getBody().getUserData();
        if(faData == null || fbData == null){return;}

        // if contact is a bird,
        if(faData.getType() == ObjectUserData.RED || fbData.getType() == ObjectUserData.RED ||
                faData.getType() == ObjectUserData.BLUE || fbData.getType() == ObjectUserData.BLUE ||
                faData.getType() == ObjectUserData.YELLOW || fbData.getType() == ObjectUserData.YELLOW ||
                faData.getType() == ObjectUserData.BIG || fbData.getType() == ObjectUserData.BIG){
            if (faData.getType() == ObjectUserData.RED || faData.getType() == ObjectUserData.BLUE ||
                    faData.getType() == ObjectUserData.YELLOW || faData.getType() == ObjectUserData.BIG) {
                faData.setIsLaunching(false);
                faData.setLandPoint(fa.getBody().getPosition().x, fa.getBody().getPosition().y);
            }
            else {
                fbData.setIsLaunching(false);
                fbData.setLandPoint(fb.getBody().getPosition().x, fb.getBody().getPosition().y);
            }
        }


    }
    @Override
    public void endContact(Contact contact) { // method for when two objects are no longer in contact
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        if(fa == null || fb == null) return;
//        if(fa.getUserData() == null || fb.getUserData() == null) return;

//        System.out.println("contact end");
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {}

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) { // method for when two objects collide
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();
        ObjectUserData faData = (ObjectUserData) fa.getBody().getUserData();
        ObjectUserData fbData = (ObjectUserData) fb.getBody().getUserData();

        // if object is a block(wood), if impact impulse is large enough, block lose health
        if(faData.getType() == ObjectUserData.WOOD0 || fbData.getType() == ObjectUserData.WOOD0 ||
                faData.getType() == ObjectUserData.WOOD1 || fbData.getType() == ObjectUserData.WOOD1 ||
                faData.getType() == ObjectUserData.WOOD2 || fbData.getType() == ObjectUserData.WOOD2){
            if (faData.getType() == ObjectUserData.WOOD0 || faData.getType() == ObjectUserData.WOOD1 || faData.getType() == ObjectUserData.WOOD2) {
                if(!faData.getIsImmune()) {
                    float impact = impulse.getNormalImpulses()[0];
                    while (impact > 8) {
                        impact -= 8;
                        faData.loseHealth();
                        if (faData.getHealth() <= 0) {
                            faData.setDead(true);
                        }
                    }
                }
            }
            else {
                if(!fbData.getIsImmune()) {
                    float impact = impulse.getNormalImpulses()[0];
                    while (impact > 8) {
                        impact -= 8;
                        fbData.loseHealth();
                        if (fbData.getHealth() <= 0) {
                            fbData.setDead(true);
                        }
                    }
                }
            }
        }

        //if object is a pig, if contact impulse is large enough, pig lose health
        if(faData.getType() == ObjectUserData.NORMALPIG || fbData.getType() == ObjectUserData.NORMALPIG ||
                faData.getType() == ObjectUserData.HELMETPIG || fbData.getType() == ObjectUserData.HELMETPIG){
            if(faData.getType() == ObjectUserData.NORMALPIG){
                if(!faData.getIsImmune()) {
                    float impact = impulse.getNormalImpulses()[0];
                    while (impact > 3) {
                        impact -= 3;
                        faData.loseHealth();
                        if (faData.getHealth() <= 0) {
                            faData.setDead(true);
                        }
                    }
                }
            }
            if(faData.getType() == ObjectUserData.HELMETPIG){
                if(!faData.getIsImmune()) {
                    float impact = impulse.getNormalImpulses()[0];
                    while (impact > 6) {
                        impact -= 6;
                        faData.loseHealth();
                        if (faData.getHealth() <= 0) {
                            faData.setDead(true);
                        }
                    }
                }
            }
            if(fbData.getType() == ObjectUserData.NORMALPIG){
                if(!fbData.getIsImmune()) {
                    float impact = impulse.getNormalImpulses()[0];
                    while (impact > 3) {
                        impact -= 3;
                        fbData.loseHealth();
                        if (fbData.getHealth() <= 0) {
                            fbData.setDead(true);
                        }
                    }
                }
            }
            if(fbData.getType() == ObjectUserData.HELMETPIG){
                if(!fbData.getIsImmune()) {
                    float impact = impulse.getNormalImpulses()[0];
                    while (impact > 6) {
                        impact -= 6;
                        fbData.loseHealth();
                        if (fbData.getHealth() <= 0) {
                            fbData.setDead(true);
                        }
                    }
                }
            }
        }
    }

};