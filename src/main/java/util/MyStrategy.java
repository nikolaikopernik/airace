package util;

import model.*;

import static java.lang.StrictMath.*;

public final class MyStrategy implements Strategy {
    private MPlan plan = new MPlan();

    @Override
    public void move(Car self, World world, Game game, Move move) {
        int x = (int)(self.getX()/game.getTrackTileSize());
        int y = (int)(self.getY()/game.getTrackTileSize());

        plan.append(x, y, (int)game.getTrackTileSize(), self, world);

        MPlan.PlanPoint p = plan.nextPoint();
        double angleToTarget = self.getAngleTo(p.x, p.y);
        if(world.getTick()%50==0) {
            System.out.println("next point " + p.x + "," + p.y+" angle="+angleToTarget);
        }
        if(Math.abs(angleToTarget)<PI/2){
            //едем норм
            move.setWheelTurn(angleToTarget*2/(PI));
        }else{
            //надо развернуться
        }
        move.setEnginePower(1.0D);
//        if (world.getTick() > game.getInitialFreezeDurationTicks()) {
//            move.setUseNitro(true);
//        }
    }


}
