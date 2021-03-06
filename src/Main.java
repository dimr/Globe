import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import processing.core.PApplet;
import org.json.simple.parser.JSONParser;
import processing.core.PGraphics;
import processing.core.PVector;
import shapes3d.Ellipsoid;
import shapes3d.Shape3D;
import toxi.geom.Line3D;
import toxi.geom.Polygon2D;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;
import toxi.geom.mesh.TriangleMesh;
import toxi.processing.ToxiclibsSupport;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dimitris on 11/4/15.
 */
public class Main extends PApplet {
    String geoJSON = "./data/mike.geojson";
    String topoJSON = "/home/dimitris/data/topo/world-10m.json";
    String t = "/home/dimitris/data/shp/geo-boundaries-world-110m/countries.geojson";
    ToxiclibsSupport toxi;
    ArrayList<Country> countries;
    private Vec3D athens, city;
    Line3D l;
    List<Vec3D> points;
    float test;

    Ellipsoid earth;

    boolean showInfo = false;
    PGraphics info;
    int z = -2000;
    float scaleFactor=1;
    float scaleValue=1;

    public void setup() {
        size(displayWidth, displayHeight - 60, P3D);
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

            //if (!name.equals("Antarctica") && !name.equals("Fr. S. Antarctic Lands") && !name.equals("Greenland")) {
            //   if (name.equals("Australia") || name.equals("Brazil") || name.equals("Russia") || name.equals("China") || name.equals("Morocco"))
            countries.add(new Country(this, name, type, coordinates));
            // }

        }

        athens = new Vec3D(400, radians((float) 23.73), radians((float) 37.98)).toCartesian();
        city = new Vec3D(400, radians((float) 6.12), radians((float) 49.62)).toCartesian();
        l = new Line3D(athens, city);
        points = new ArrayList<>();
        points = l.splitIntoSegments(points, 10, true);


        earth = new Ellipsoid(this, 16, 16);
        earth.setTexture("./data/world32k.jpg");
        //earth.setTexture("./data/Clouds.png");
        earth.setRadius(398);
        earth.moveTo(new PVector(0, 0, 0));
        earth.strokeWeight(1.0f);
        earth.stroke(color(255, 255, 0));
        //earth.moveTo(20, 40, -80);
        earth.tag = "Earth";
        earth.drawMode(Shape3D.TEXTURE);


        info = createGraphics(400, 200, P3D);

    }


    public void draw() {
        background(50);
        //stroke((float) .5);
//        noStroke();
//        noFill();
//        for (Country c : countries) {
//            for (Polygon2D p : c.getPolygons()) {
//                if (c.getType().equals("Polygon")){
//                    //GREEN
//                    fill(0,200,0);
//                }
//                else{
//                    //RED MULTIPOLYGON
//                    fill(200,0,0);
//                }
//               // toxi.polygon2D(p);
//            }
//            c.drawTriangles();
//        }

        pushMatrix();
        if (z > 0)
            translate(width / 2, height / 2, 0);
        else
            translate(width / 2, height / 2, z);
        z+=50;
        rotateX(mouseY * (float) 0.01);
        if (mouseX == 0 && mouseY == 0)
            rotateX(radians(180));
        rotateY(radians(-frameCount % 360));
        pushStyle();
        noStroke();
        lights();

        fill(20, 49, 91);
        sphere(398);


        //texturedEarth();
        pushMatrix();
        scale(scaleFactor);
        popStyle();
        for (Country c : countries) {
            c.drawMeshes();

        }
        popMatrix();


        pushStyle();
        stroke(255, 0, 0);
        strokeWeight(5);
        point(athens.x(), athens.y(), athens.z());
        point(city.x(), city.y(), city.z());
//        for (Vec3D v:points)
//        toxi.point(v);
        //toxi.line(new Line3D(new Vec3D(0,0,0),athens));
        toxi.point(l.getMidPoint());
        toxi.line(new Line3D(athens, new Vec3D(500, radians((float) 23.73), radians((float) 37.98)).toCartesian()));
        toxi.line(new Line3D(city, new Vec3D(600, radians((float) 6.12), radians((float) 49.62)).toCartesian()));
        popStyle();
//        pushStyle();
//        fill(255, 90);
//        noStroke();
//        ellipse(frameCount % width, height / 2, 200, 200);
//        popStyle();
        noLights();
        popMatrix();

        //EARTH TEXTURE
        if (showInfo) {
            info.beginDraw();
            info.background(100);
            info.noLights();
            info.pushMatrix();
            info.translate(info.width / 2, info.height / 2);
            info.rotate(radians(frameCount % 360));
            info.noStroke();
            info.smooth();
            info.rectMode(CENTER);
            info.rect(0, 0, 40, 40);
            info.popMatrix();
            info.text(scaleFactor, 30, 100);
            info.endDraw();
            image(info, width - info.width, height - info.height);
        }
    }

    public void texturedEarth() {
        pushStyle();
        // Change the rotations before drawing
        // earth.rotateBy(0, radians(-1.8f), 0);
        pushMatrix();
        rotateX(radians(180));
        rotateY(radians(180));

        earth.draw();
        popMatrix();

        // popMatrix();
        popStyle();
    }

    public void keyPressed() {
        if (keyCode == ' ') {
            showInfo = !showInfo;
        }
        else if(keyCode==UP){
            scaleFactor+=(float).05;
        }else if (keyCode==DOWN){
            scaleFactor-=(float).05;
        }

    }


    public Vec3D to(Vec2D v, int radius) {
        return new Vec3D(radius, v.x, v.y).toSpherical();
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{Main.class.getName()});

    }
}
