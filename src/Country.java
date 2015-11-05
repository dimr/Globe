import org.json.simple.JSONArray;
import processing.core.PApplet;
import processing.core.PVector;
import toxi.geom.Polygon2D;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;

import java.util.ArrayList;

/**
 * Created by dimitris on 11/4/15.
 */
public class Country {
    private ArrayList<Polygon2D> polygons;
    private String name;
    private String fips;
    private int numberOfPolygons;
    private PApplet pa;

    public Country(PApplet pa, String name, String fips, int numberOfPolygons, JSONArray data) {
        this.pa = pa;
        this.name = name;
        this.fips = fips;
        this.numberOfPolygons = numberOfPolygons;
        this.polygons = new ArrayList<>(this.numberOfPolygons);
        for (int i = 0; i < data.size(); i++) {
            Polygon2D poly = new Polygon2D();
            JSONArray t = (JSONArray) data.get(i);
            int size = ((JSONArray) t.get(0)).size();
            //System.out.println( size+" " +t +" ");

            for (int j = 0; j < size; j++) {
                Object temp = ((JSONArray) t.get(0)).get(j);
//                JSONArray temp2 =(JSONArray)t.get(j);
//                System.out.println(temp2.get(0)+" "+temp2.get(1));
                JSONArray f = (JSONArray) temp;
                Double p1 = (double) f.get(0);
                Double p2 = (double) f.get(1);

                poly.add(toApplicationDimension(new Vec2D(p1.floatValue(), p2.floatValue())));
            }

            polygons.add(poly);

        }

    } //CONSTRUCTOR

    public ArrayList<Polygon2D> getPolygons() {
        return this.polygons;
    }

    public String getName() {
        return this.name;
    }

    private Vec2D toApplicationDimension(Vec2D v) {
        Vec2D result = new Vec2D();
        float x = this.pa.width * (v.x() - (-180)) / (180 - (-180));
        float y = (this.pa.height - this.pa.height * (v.y() - (-90)) / (90 - (-90)));
        result.set(x, y);
        Vec3D vv= new Vec3D(x,y,0).toCartesian();
        return result;

    }


    @Override
    public String toString() {
        return "Country{" +
                "polygons=" + polygons.size() +
                ", name='" + name + '\'' +
                ", fips='" + fips + '\'' +
                ", numberOfPolygons=" + numberOfPolygons +
                '}';
    }
}
