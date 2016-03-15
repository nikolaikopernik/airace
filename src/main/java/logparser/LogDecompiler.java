package logparser;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.xml.internal.ws.util.MetadataUtil;
import logparser.GameInfo;
import model.*;
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
        path.resolve(outname).toFile().delete();
        try(Writer writer = new BufferedWriter(new FileWriter(path.resolve(outname).toFile()))) {
            writer.write("TICK,FORWARD1,FORWARD2,FORWARD3,FORWARD4,HEALTH,SPEED,DISTANCE_BORDER,ANGLE_DIRECTION," +
                    "ANGEL_BONUS,DISTANCE_BONUS,BONUS_TYPE_REPAIR,BONUS_TYPE_AMMO,BONUS_TYPE_NITRO,BONUS_TYPE_OIL,BONUS_TYPE_SCORE,Y_WHEEL,Y_POWER\n");
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
                writer.write(tick.getTick()+", "+parameters+'\n');
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
        if(tick.getTick()<180 || me.isFinishedTrack() || me.getDurability()==0){
            return null;
        }
        try {
            if(tick.getTick()>4571){
                System.out.println(1);
            }
            MTile[] nextway = log.getNextWay();
            MTile current = nextway[0];
            int[] wayparams = wayParams(nextway, me);
            int borderDistance = getBorderDistance(current, me);
            double directionAngle = getDirectionAngle(current, me);
            Unit closer = chooseCloser(me, tick.getBonuses());
            double[] angleDstToBonus = getAngleDistanceTo(me, closer);
            byte[] bonusConverted = convertBonusType((Bonus) closer, log);
            return String.format("%d, %d, %d, %d," +
                            " %.3f, %.3f, %d, %.3f, %.3f, %d," +
                            " %d, %d, %d, %d, %d," +
                            " %.3f, %.3f",
                    wayparams[0], wayparams[1], wayparams[2], wayparams[3],
                    me.getDurability(), me.getSpeedX() + me.getSpeedY(), borderDistance, directionAngle, angleDstToBonus[0], (int) angleDstToBonus[1],
                    bonusConverted[0], bonusConverted[1], bonusConverted[2], bonusConverted[3], bonusConverted[4],
                    me.getWheelTurn(), me.getEnginePower());
        }catch (Exception e){
            System.out.println("tick is "+tick.getTick());
            throw e;
        }
    }

    private static byte[] convertBonusType(Bonus closer, GameLog log) {
        byte[] b = new byte[5];
        if(closer == null){
            return b;
        }
        BonusType t = log.findBonus(closer.getId());
        if(t==null){
            return b;
        }
        b[t.ordinal()] = 1;
        return b;
    }

    private static double[] getAngleDistanceTo(Car me, Unit unit) {
        if(unit == null){
            return new double[]{-1,-1};
        }
        return new double[]{me.getAngleTo(unit), me.getDistanceTo(unit)};
    }

    private static Unit chooseCloser(Car me, Bonus[] bonuses) {
        double min = 100000;
        Unit minimal = null;
        for(Bonus b:bonuses){
            if(me.getAngleTo(b)<Math.PI/2){
                double distance = me.getDistanceTo(b);
                if(distance<min){
                    minimal = b;
                    min = distance;
                }
            }
        }
        return minimal;
    }

    private static double getDirectionAngle(MTile current, Car me) {
        switch (current.direction){
            case UP:
                return me.getAngleTo(me.getX(), me.getY()-10);
            case DOWN:
                return me.getAngleTo(me.getX(), me.getY()+10);
            case LEFT:
                return me.getAngleTo(me.getX()-10, me.getY());
            case RIGHT:
                return me.getAngleTo(me.getX()+10, me.getY());
        }
        return 0;
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
        analyze(Paths.get("D:\\LocalRunner\\log\\"), "out.csv");
    }
}
