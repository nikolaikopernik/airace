package util;

import model.Car;
import model.World;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Created by Nikolai_Bogdanov on 11/16/2015.
 */
public class MPlan {
    public Queue<PlanPoint> points = new ArrayDeque<>(50);
    public Queue<MTile> way = new ArrayDeque<>(50);
    public MPlan() {}

    public PlanPoint nextPoint(){
        return points.peek();
    }

    public void pointReched(){
        points.poll();
    }

    public void append(int x, int y, int scale, Car self, World world) {
        if(isEmpty(x,y)) {
            points.clear();
            way.clear();
            int idx = self.getNextWaypointIndex();
            int[][] waypoints = world.getWaypoints();
            for(int i = 0; i<2 || way.size()<3;i++) {
                MUtil.findTheWay(new MTile(x, y), new MTile(waypoints[(idx + i) % waypoints.length]), world, way);
                x = way.peek().x;
                y = way.peek().y;
            }
            MDirection d = way.peek().direction;
            int i = 0;
            for (MTile tile: way){
                if (d != tile.direction || i == way.size() - 1) {
                    points.add(new PlanPoint(tile.x * scale + scale / 2, tile.y * scale + scale / 2, 1d));
                }
                i++;
            }
        }
    }

    /**
     * нужно ли досчитывать путь ?
     * Путь должен соделжать 2 ближайших ввейпоинта или 3 клетки вперед на выбор
     * @param x
     * @param y
     * @return
     */
    public boolean isEmpty(int x, int y) {
        if(way.peek()==null)  return true;
        if(way.peek().equals(new MTile(x,y))) return true;
        if(way.size()<3)  return true;
        if(points.size()<2) return true;
        return false;
    }

    public class PlanPoint{
        public int x;
        public int y;
        public double speed;

        public PlanPoint(int x, int y, double speed) {
            this.x = x;
            this.y = y;
            this.speed = speed;
        }
    }
}
