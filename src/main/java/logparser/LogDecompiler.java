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
    /**
     * P1-P4 Forward map
     * P5    Health
     * P6    Speed
     * P6    Distance to boarder
     * P7    Boarder angle
     * P8,P9 Angle, distance to next waypoint
     * P10,P11 Angle, distance to next bonus
     * P12   next bonus type
     *
     * Y1    Wheel Angle
     * Y2    Power
     */

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
        while(((tick = log.nextTick())!=null){
            String parameters = getParametersFromTick(tick, log);
            if(parameters!=null){
                System.out.println(parameters);
            }
        }
    }

    private static String getParametersFromTick(World tick, GameLog log) {
        Car me = log.findMyCar(tick);
        if(tick.getTick()<180){
            return null;
        }
        int[] nextWay = wayParams(me, log);
        return String.format("%d, %d, %d, %d, %.3f, %.3f", nextWay[0],nextWay[1],nextWay[2],nextWay[3], me.getDurability(), me.getSpeedX()+me.getSpeedY());
    }

    /**
     * Find out p1-p4 params
     * @param me
     * @param info
     * @return
     */
    private static int[] wayParams(Car me, GameLog info) {
        MTile[] nextway = info.getNextWay();

        return new int[4];
    }





    public static void main(String...args){
        analyze(Paths.get("D:\\LocalRunner\\log\\"), "out.log");
    }
}
