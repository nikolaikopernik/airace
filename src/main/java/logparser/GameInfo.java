package logparser;

import model.Car;
import model.World;
import util.MTile;
import util.MUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Nikolai_Bogdanov on 1/15/2016.
 */
public class GameInfo {
    public World world;
    public long myCarId = 0;
    public MTile[] way = null;

    public GameInfo(List<World> ticks) {
        World world = ticks.get(0);
        Car[] cars = world.getCars();
        myCarId = cars[0].getId();
        for(Car car:cars){
            if(car.getId() < myCarId){
                myCarId = car.getId();
            }
        }
        this.world = world;

        List<MTile> list = new ArrayList<>(100);
        for(World tick:ticks){
            if(tick.getTick()<=180){
                continue;
            }
            Car my = MUtil.findCarWithMinID(tick);
            MTile c = new MTile(my);
            if(list.isEmpty()){
                list.add(c);
                continue;
            }
            MTile last = list.get(list.size()-1);
            if(!last.equals(c)){
                list.add(c);
                last.setDirectionTo(c);
            }
        }
        way = list.toArray(new MTile[0]);
    }




}
