package logparser;

import com.google.gson.Gson;
import model.*;
import util.MTile;

import java.io.*;
import java.util.*;

/**
 * Created by Nikolai_Bogdanov on 1/18/2016.
 */
public class GameLog {
    private static final int ARM = 6;
    private final Gson gson;
    private final BufferedReader reader;
    private final World start;
    private ArrayList<MTile> way = new ArrayList<>(100);
    private Queue<World> buffer = new LinkedList<>();
    private Map<Long, BonusType> bonuses = new HashMap<>();
    private int currentTileIDX = 0;
    private long myCarID = 0;

    public GameLog(File file) throws IOException {
        gson = new Gson();
        reader = new BufferedReader(new FileReader(file));
        String line = reader.readLine();
        start = parseGameInfo(line, gson, file.getName());
        Car my = initMyCar(start);
        initBonusesMap();
        initTheWay(my);
    }

    private void initBonusesMap() {
        for(Bonus b:start.getBonuses()){
            bonuses.put(b.getId(), b.getType());
        }
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
            buffer.add(parseGameInfo(line, gson, "B"));
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
                World tick = parseGameInfo(line, gson, "A");
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

    public BonusType findBonus(Long id){
        return bonuses.get(id);
    }

    public World getInfo() {
        return start;
    }

    private static World parseGameInfo(String line, Gson gson, String name) {
        try {
            return (World) gson.fromJson(line, World.class);
        }catch (RuntimeException e){
            System.out.println("Some error in parsing world object in "+name);
            throw e;
        }
    }

    public MTile[] getNextWay() {
        MTile[] w = new MTile[ARM-1];
        for(int i = 0; i<ARM-1; i++){
            w[i] = way.get((currentTileIDX+i)%way.size());
        }
        if(w[0].direction==null){
            w[0].direction = w[1].direction;
        }
        return w;
    }
}
