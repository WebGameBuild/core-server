package commands;

import datastore.DS;
import models.db.Map;

import java.util.Random;

public class BuildMap {

    public static void main(String[] args) {

        // initialize data store
        DS.morphia.mapPackage("models.db");
        DS.getDatastore().ensureIndexes();

        Random random = new Random();
        for (Integer x = -1000; x <= 1000; x++) {
            for (Integer y = -1000; y <= 1000; y++) {
                Map cell = new Map();
                cell.x = x;
                cell.y = y;
                cell.landType = (byte) random.nextInt(11);
                DS.getDatastore().save(cell);
            }
            System.out.println("x = " + x);
        }

    }

}
