package util;

import model.Car;
import model.TileType;
import model.World;

import java.util.*;

/**
 * Created by Nikolai_Bogdanov on 11/16/2015.
 */
public class MUtil {


    public static final TileType[] TILES_LEFT_OPENED = new TileType[]{
            TileType.HORIZONTAL,
            TileType.RIGHT_BOTTOM_CORNER,
            TileType.RIGHT_TOP_CORNER,
            TileType.LEFT_HEADED_T,
            TileType.CROSSROADS};

    public static final TileType[] TILES_RIGHT_OPENED = new TileType[]{
            TileType.HORIZONTAL,
            TileType.LEFT_BOTTOM_CORNER,
            TileType.LEFT_TOP_CORNER,
            TileType.RIGHT_HEADED_T,
            TileType.CROSSROADS};

    public static final TileType[] TILES_TOP_OPENED = new TileType[]{
            TileType.VERTICAL,
            TileType.LEFT_BOTTOM_CORNER,
            TileType.RIGHT_BOTTOM_CORNER,
            TileType.TOP_HEADED_T,
            TileType.CROSSROADS};

    public static final TileType[] TILES_BOTTOM_OPENED = new TileType[]{
            TileType.VERTICAL,
            TileType.LEFT_TOP_CORNER,
            TileType.RIGHT_TOP_CORNER,
            TileType.BOTTOM_HEADED_T,
            TileType.CROSSROADS};



    /**
     * Ищет путь между двумя вейпоинтами в плитках
     * @param current
     * @param tar
     * @return
     */
    public static <T extends Collection<MTile>> T findTheWay(MTile current, MTile tar, World world, T way){
        MTile cur = current;
        TileType[][] tilesXY = world.getTilesXY();
        int distance = current.distance(tar);
        mloop:for (int i = 0; i <=distance; i++) {
            for (MTile nextStep : cur.getDirectionsOrdered(tar)) {
                if (!way.contains(nextStep) &&
                        hasRoad(cur, nextStep, world.getWidth(), world.getHeight(), tilesXY)) {
                    way.add(nextStep);
                    if(nextStep.equals(tar)){
                        return way;
                    }
                    cur = nextStep;
                    continue mloop;
                }
            }
        }
        return way;
    }

    /**
     * Проверяет, есть ли дорога между 2 соседними клетками
     * @param cur
     * @param nextStep
     * @param width
     * @param height
     * @param tiles
     * @return
     */
    private static boolean hasRoad(MTile cur, MTile nextStep, int width, int height, TileType[][] tiles) {
        if(nextStep.x < 0 || nextStep.x>=width || nextStep.y<0 || nextStep.y>=height){
            return false;
        }
        int dx = nextStep.x-cur.x;
        int dy = nextStep.y-cur.y;
        TileType typeCur =  tiles[cur.x][cur.y];
        TileType typeNext = tiles[nextStep.x][nextStep.y];
        if(dx > 0 && (contains(typeCur,TILES_RIGHT_OPENED) && contains(typeNext, TILES_LEFT_OPENED))) return true;
        if(dx < 0 && (contains(typeCur,TILES_LEFT_OPENED) && contains(typeNext, TILES_RIGHT_OPENED))) return true;
        if(dy < 0 && (contains(typeCur,TILES_TOP_OPENED) && contains(typeNext, TILES_BOTTOM_OPENED))) return true;
        if(dy > 0 && (contains(typeCur,TILES_BOTTOM_OPENED) && contains(typeNext, TILES_TOP_OPENED))) return true;
        return false;
    }

    public static Car findCarWithMinID(World world){
        Car m = world.getCars()[0];
        for(Car c:world.getCars()){
            if(c.getId()<m.getId()){
                m = c;
            }
        }
        return m;
    }

    public static <T> boolean contains(T v,T[] array){
        for (T t :array) {
            if (v.equals(t)){
                return true;
            }
        }
        return false;
    }

    public static int scaled(double x, double scale){
        return (int)(x*scale);

    }



}
