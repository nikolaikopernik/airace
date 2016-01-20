package logparser;

import com.google.gson.Gson;
import model.Car;
import model.Game;
import model.Player;
import model.World;
import util.MTile;

import java.io.*;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Nikolai_Bogdanov on 1/18/2016.
 */
public class GameLog {
    private static final int ARM = 5;
    private final Gson gson;
    private final BufferedReader reader;
    private final World start;
    private ArrayList<MTile> way = new ArrayList<>(100);
    private Queue<World> buffer = new LinkedList<>();
    private int currentTileIDX = 0;
    private long myCarID = 0;

    public GameLog(File file) throws IOException {
        gson = new Gson();
        reader = new BufferedReader(new FileReader(file));
        String line = reader.readLine();
        start = parseGameInfo(line, gson);
        Car my = initMyCar(start);
        initTheWay(my);
    }

    private void initTheWay(Car start) {
        MTile s = new MTile(start);
        way.add(s);
    }

    private Car initMyCar(World start) {
        long plID = -1;
        for(Player p:start.getPlayers()){
            if(p.getName().equals("KeyboardPlayer")){
                plID = p.getId();
                break;
            }
        }
        if(plID==-1){
            throw  new RuntimeException("Car not found");
        }
        for(Car c:start.getCars()){
            if(c.getPlayerId() == plID){
                myCarID = c.getId();
                return c;
            }
        }
        throw  new RuntimeException("Car not found");
    }

    public World nextTick() throws IOException {
        if(buffer.isEmpty()){
            String line = reader.readLine();
            if(line==null){
                reader.close();
                return null;
            }
            buffer.add(parseGameInfo(line, gson));
        }
        World w = buffer.poll();
        checkCurrentTile(w);
        return w;
    }

    /**
     * If current position
     * @param w
     */
    private void checkCurrentTile(World w) throws IOException {
        if(!new MTile(findMyCar(w)).equals(way.get(currentTileIDX))){
            currentTileIDX++;
        }
        way:while(way.size() - currentTileIDX < ARM){
            MTile last = way.get(way.size()-1);
            while (true) {
                String line = reader.readLine();
                if(line == null){
                    break way;
                }
                World tick = parseGameInfo(line, gson);
                buffer.add(tick);
                MTile next = new MTile(findMyCar(tick));
                if(!next.equals(last)){
                    last.setDirectionTo(next);
                    way.add(next);
                    break;
                }
            }
        }
    }



    public Car findMyCar(World tick) {
        for(Car car:tick.getCars()){
            if(car.getId() == myCarID){
                return car;
            }
        }
        return null;
    }

    public World getInfo() {
        return start;
    }

    private static World parseGameInfo(String line, Gson gson) {
        return (World)gson.fromJson(line, World.class);
    }

    public MTile[] getNextWay() {
        MTile[] w = new MTile[ARM-1];
        for(int i = 0; i<ARM; i++){
            w[i] = way.get(currentTileIDX+i);
        }
        return w;
    }
}
