package tests;

import game.robots.MyRobotTypes;
import game.robots.Robot;
import game.robots.RobotType;

public class TestRobots {
    public static void main(final String[] args) {
        final RobotType Drone = MyRobotTypes.getDrone();
        // RobotType Roues = MyRobotTypes.getRoues();
        // RobotType Chenilles = MyRobotTypes.getChenilles();
        // RobotType Pattes = MyRobotTypes.getPattes();

        final Robot drone = Drone.newRobot(50);
        final Robot drone1 = Drone.newRobot(60);
        // Robot roues = Roues.newRobot(0);
        // Robot chenilles = Chenilles.newRobot(1);
        // Robot pattes = Pattes.newRobot(3);

        // System.out.println(drone);
        // System.out.println(drone1);
        // System.out.println(roues);
        // System.out.println(chenilles);
        // System.out.println(pattes);

        drone1.setVitesse(50.0);
        System.out.println(drone1);
        System.out.println(drone);
    }
}
