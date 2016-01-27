package logparser;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.xml.internal.ws.util.MetadataUtil;
import logparser.GameInfo;
import model.Car;
import model.Game;
import model.World;
import util.MTile;
import util.MUtil;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by Nikolai_Bogdanov on 1/15/2016.
 */
public class LogDecompiler {


    public static void analyze(Path path, String outname){
        try(Writer writer = new BufferedWriter(new FileWriter(outname))) {
            for (File f : path.toFile().listFiles()) {
                if (f.getName().endsWith(".log")) {
                    parseGameLog(f, writer);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void parseGameLog(File f, Writer writer) throws IOException {
        GameLog log = new GameLog(f);
        World tick = null;
        while(((tick = log.nextTick())!=null)){
            String parameters = getParametersFromTick(tick, log);
            if(parameters!=null){
                System.out.println(parameters);
            }
        }
    }

    /**
     * P1-P4 Forward map
     * P5    Health
     * P6    Speed
     * P7    Distance to boarder (-400:400)
     * P8    Direction angle
     * P9,P10 Angle, distance to next waypoint
     * P11,P12 Angle, distance to next bonus
     * P13   next bonus type
     *
     * Y1    Wheel Angle
     * Y2    Power
     */
    private static String getParametersFromTick(World tick, GameLog log) {
        Car me = log.findMyCar(tick);
        if(tick.getTick()<180){
            return null;
        }
        MTile[] nextway = log.getNextWay();
        MTile current = nextway[0];
        int[] wayparams = wayParams(nextway, me);
        int borderDistance = getBorderDistance(current, me);
        double directionAngle = getDirectionAngle(current, me);
        return String.format("%d, %d, %d, %d, %.3f, %.3f, %d",
                wayparams[0],wayparams[1],wayparams[2],wayparams[3],
                me.getDurability(), me.getSpeedX()+me.getSpeedY(), borderDistance);
    }

    private static double getDirectionAngle(MTile current, Car me) {
        return me.getAngleTo();
    }

    private static int getBorderDistance(MTile current, Car me) {
        switch (current.direction){
            case UP:
                return (int)me.getX() - current.x*800+400;
            case RIGHT:
                return (int)me.getY() - current.y*800+400;
            case DOWN:
                return current.x*800+400 - (int)me.getX();
            case LEFT:
                return current.x*800+400 - (int)me.getX();
        }
        return 0;
    }

    /**
     * Find out p1-p4 params
     * @param me
     * @param nextway
     * @return
     */
    private static int[] wayParams( MTile[] nextway, Car me) {
        MTile prev = nextway[0];
        int[] params = new int[nextway.length-1];
        for(int i=1;i<nextway.length;i++){
            int f = MUtil.direction2int(nextway[i].direction) - MUtil.direction2int(prev.direction);
            prev = nextway[i];
            params[i-1] = Math.abs(f)>1?-f/3:f;
        }
        return params;
    }





    public static void main(String...args){
        analyze(Paths.get("D:\\LocalRunner\\log\\"), "out.log");
    }
}
