import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import processing.core.PApplet;
import org.json.simple.parser.JSONParser;
import toxi.geom.Polygon2D;
import toxi.geom.Vec2D;
import toxi.geom.mesh.TriangleMesh;
import toxi.processing.ToxiclibsSupport;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by dimitris on 11/4/15.
 */
public class Main extends PApplet {
    String geoJSON = "./data/mike.geojson";
    String topoJSON = "/home/dimitris/data/topo/world-10m.json";
    String t = "/home/dimitris/data/shp/geo-boundaries-world-110m/countries.geojson";
    ToxiclibsSupport toxi;
    ArrayList<Country> countries;

    public void setup() {
        size(displayWidth, displayHeight);
        toxi = new ToxiclibsSupport(this);
        Object in = null;
        try {
            in = new JSONParser().parse(new FileReader(t));
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (FileNotFoundException r) {
            r.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject start = (JSONObject) in;
        JSONArray features = (JSONArray) start.get("features");
        int count = 0;
        countries = new ArrayList<>();
        for (int i = 0; i < features.size(); i++) {
            JSONObject pr = (JSONObject) features.get(i);
            JSONObject properties = (JSONObject) pr.get("properties");
            String name = (String) properties.get("name");
            String condinent = (String) properties.get("continent");
            String fips = (String) properties.get("FIPS_10_");
            JSONObject geometry = ((JSONObject) pr.get("geometry"));
            JSONArray coordinates = (JSONArray) geometry.get("coordinates");
            String type = geometry.get("type").toString();

            if (!name.equals("Antarctica") && !name.equals("Fr. S. Antarctic Lands") && !name.equals("Greenland")){
            //   if (name.equals("Australia") || name.equals("Brazil") || name.equals("Russia") || name.equals("China") || name.equals("Morocco"))
                countries.add(new Country(this, name, type, coordinates));
            }

        }
    }


    public void draw() {
        background(100);
        //stroke((float) .5);
        noStroke();
        noFill();
        for (Country c : countries) {
            for (Polygon2D p : c.getPolygons()) {
                if (c.getType().equals("Polygon")){
                    //GREEN
                    fill(0,200,0);
                }
                else{
                    //RED MULTIPOLYGON
                    fill(200,0,0);
                }
               // toxi.polygon2D(p);
            }
            c.drawTriangles();
        }

//        pushStyle();
//        fill(255, 90);
//        noStroke();
//        ellipse(frameCount % width, height / 2, 200, 200);
//        popStyle();

    }


    public static void main(String[] args) {
        PApplet.main(new String[]{Main.class.getName()});

    }
}
