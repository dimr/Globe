import processing.core.PApplet;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;

/**
 * Created by dimitris on 11/19/15.
 */
public class Capital {
    private String country;
    private String name;
    private Vec2D coordinate;
    private PApplet pa;

    @Override
    public String toString() {
        return "Capital{" +
                "country='" + country + '\'' +
                ", name='" + name + '\'' +
                ", coordinate=" + coordinate +
                '}';
    }

    public Capital(PApplet pa) {
        this.pa=pa;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Vec2D getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Vec2D coordinate) {
        this.coordinate = coordinate;
    }

    public Vec3D toSpherical(){
        return new Vec3D(400,this.pa.radians(this.getCoordinate().x),this.pa.radians(this.getCoordinate().y)).toCartesian();
    }

    public Vec3D toSpherical(float radius){
        return new Vec3D(radius,(float)this.pa.radians(this.getCoordinate().x),(float)this.pa.radians(this.getCoordinate().y)).toCartesian();
    }
}
