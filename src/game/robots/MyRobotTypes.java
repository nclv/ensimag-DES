package game.robots;

import java.util.EnumMap;
import java.util.Map;

import game.NatureTerrain;

public class MyRobotTypes {
    private static final RobotType Drone = new RobotType(
        Type.DRONE, Filling.ON, 
        100.0, 10000.0, 
        10000, 30, 
        30 * 60,
        new EnumMap<NatureTerrain, Double>(Map.of(
            NatureTerrain.EAU, 1.0, 
            NatureTerrain.FORET, 1.0,
            NatureTerrain.ROCHE, 1.0, 
            NatureTerrain.TERRAIN_LIBRE, 1.0, 
            NatureTerrain.HABITAT, 1.0
            )
        )
    );

    private static final RobotType Roues = new RobotType(
        Type.ROUES, Filling.NEXT, 
        80.0, 5000.0, 
        100, 5, 
        10 * 60,
        new EnumMap<NatureTerrain, Double>(Map.of(
            NatureTerrain.TERRAIN_LIBRE, 1.0, 
            NatureTerrain.HABITAT, 1.0
            )
        )
    );

    private static final RobotType Chenilles = new RobotType(
        Type.CHENILLES, Filling.NEXT, 
        60.0, 2000.0, 
        100, 8, 
        5 * 60,
        new EnumMap<NatureTerrain, Double>(Map.of(
            NatureTerrain.FORET, 0.5, 
            NatureTerrain.TERRAIN_LIBRE, 1.0,
            NatureTerrain.HABITAT, 1.0
            )
        )
    );

    private static final RobotType Pattes = new RobotType(
        Type.PATTES, Filling.NONE, 
        30.0, Double.POSITIVE_INFINITY, 
        10, 1, 
        0,
        new EnumMap<NatureTerrain, Double>(Map.of(
            NatureTerrain.FORET, 1.0, 
            NatureTerrain.ROCHE, (double) 1 / 3,
            NatureTerrain.TERRAIN_LIBRE, 1.0, 
            NatureTerrain.HABITAT, 1.0
            )
        )
    );

    static final EnumMap<Type, RobotType> typeMap = new EnumMap<Type, RobotType>(Map.of(
        Type.DRONE, Drone,
        Type.ROUES, Roues,
        Type.CHENILLES, Chenilles,
        Type.PATTES, Pattes
    ));

    public static RobotType getDrone() {
        return Drone;
    }

    public static RobotType getRoues() {
        return Roues;
    }

    public static RobotType getChenilles() {
        return Chenilles;
    }

    public static RobotType getPattes() {
        return Pattes;
    }

    public static RobotType getType(Type type) {
        return typeMap.get(type);
    }
}
