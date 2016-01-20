package util;

import model.Car;

import java.util.Arrays;

/**
 * Ячейка трассы в терминах сетки (800х800).
 */
public class MTile {
    public int x = 0;
    public int y = 0;
    public MDirection direction = null;

    public MTile(int[] pair){
        this.x = pair[0];
        this.y = pair[1];
    }

    public MTile(int x, int y){
        this.x = x;
        this.y = y;
    }

    public MTile(MTile cur, MDirection dir) {
        switch (dir) {
            case UP:
                this.x = cur.x;
                this.y = cur.y - 1;
                break;
            case DOWN:
                this.x = cur.x;
                this.y = cur.y + 1;
                break;
            case LEFT:
                this.x = cur.x - 1;
                this.y = cur.y;
                break;
            case RIGHT:
                this.x = cur.x + 1;
                this.y = cur.y;
        }
        this.direction = dir;
    }

    public MTile(double xd, double yd) {
        this((int)xd, (int)yd);
    }



    public MTile(Car c) {
        this(c.getX()/800, c.getY()/800);
    }



    /**
     *
     * @param to
     * @return
     */
    public int distance(MTile to){
        return Math.abs(to.x-this.x) + Math.abs(to.y-this.y);
    }

    /**
     * Все возможные следующие шаги
     * @param cur
     * @param tar
     * @return
     */
    public MTile[] getDirectionsOrdered(final MTile tar) {
        MTile[] result = new MTile[4];
        result[0] = new MTile(this, MDirection.UP);
        result[1] = new MTile(this, MDirection.LEFT);
        result[2] = new MTile(this, MDirection.RIGHT);
        result[3] = new MTile(this, MDirection.DOWN);
        Arrays.sort(result,
                (MTile o1, MTile o2) -> o1.distance(tar) - o2.distance(tar));
        return result;
    }

    public void setDirectionTo(MTile next){
        if(next.x-x>0){
            this.direction = MDirection.RIGHT;
        }else if(next.x-x<0){
            this.direction = MDirection.LEFT;
        }else if(next.y-y<0){
            this.direction = MDirection.UP;
        }else if(next.y-y>0){
            this.direction = MDirection.DOWN;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MTile mTile = (MTile) o;

        if (x != mTile.x) return false;
        return y == mTile.y;

    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }
}
