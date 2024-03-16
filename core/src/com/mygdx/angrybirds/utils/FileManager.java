/*
File Name:
Author: Kevin Yan

This class will read and write to given txt files.
 */
package com.mygdx.angrybirds.utils;

import com.badlogic.gdx.Gdx;

import java.io.*;
import java.util.Scanner;

public class FileManager {

    public String[] readFile(String file){ // this method reads a level file and returns a String array of each line
        try {
            String filename = "assets/"+file;
            Scanner inFile = new Scanner(new BufferedReader(new FileReader(String.valueOf(Gdx.files.internal(filename)))));
            String out[] = new String[10];
            for(int i=0; i<10; i++){
                out[i] = inFile.nextLine();
            }
            inFile.close();

            return out;
        }
        catch(IOException ex){
            System.out.println(ex);
            return null;
        }
    }

    public void writeFile(String file, float x, float y, boolean isRotated, int type, int typeOBJ){
        // this method writes to a level file
        try{
            String filename = "assets/"+file;
            String[] oldfile = readFile(file);
            PrintWriter outFile = new PrintWriter(new BufferedWriter(new FileWriter(String.valueOf(Gdx.files.internal(filename)))));
            for(int i=0; i<oldfile.length; i++){
                outFile.print(oldfile[i]);
                if(i == typeOBJ){
                    if(typeOBJ == 0) {
                        outFile.print(x + "," + y + "," + (isRotated ? 0 : 1) + "," + type + " ");
                    }
                    if(typeOBJ == 1){
                        outFile.print(x + "," + y + "," + type + " ");
                    }
                    if(typeOBJ == 2){
                        outFile.print(type+" ");
                    }
                }
                outFile.println();
            }
//            outFile.println("test");

            outFile.close();
        }
        catch(IOException ex){
            System.out.println(ex);
//            return null;
        }
    }

    public boolean[] readLevelProgress(){ // this method reads the level progress file and returns a boolean array of levels completed
        try {
            String filename = "assets/levelProgress.txt";
            Scanner inFile = new Scanner(new BufferedReader(new FileReader(String.valueOf(Gdx.files.internal(filename)))));
            String tmp1 = inFile.nextLine();
            String[] tmp2 = tmp1.split(" ");
            boolean[] out = new boolean[12];
            for(int i=0; i<12; i++){
                if(tmp2[i].equals("0")){
                    out[i] = false;
                }
                else{
                    out[i] = true;
                }
            }

            inFile.close();

            return out;
        }
        catch(IOException ex){
            System.out.println(ex);
            return null;
        }
    }
    public void writeLevelProgress(int level){ // this method writes to the level progress file
        try {
            level -= 1;
            boolean[] oldFile = readLevelProgress();
            oldFile[level] = true;
            String filename = "assets/levelProgress.txt";
            PrintWriter outFile = new PrintWriter(new BufferedWriter(new FileWriter(String.valueOf(Gdx.files.internal(filename)))));
            for(boolean lev : oldFile){
                outFile.print((lev ? 1 : 0) + " ");
            }
            outFile.close();
        }
        catch(IOException ex){
            System.out.println(ex);
//            return null;
        }
    }
}
