import game.robots.*;

public class TestRobots {
    public static void main(String[] args) {
        RobotType Drone = MyRobotTypes.getDrone();
        RobotType Roues = MyRobotTypes.getRoues();
        RobotType Chenilles = MyRobotTypes.getChenilles();
        RobotType Pattes = MyRobotTypes.getPattes();

        Robot drone = Drone.newRobot();
        Robot roues = Roues.newRobot();
        Robot chenilles = Chenilles.newRobot();
        Robot pattes = Pattes.newRobot();

        System.out.println(drone);
        System.out.println(roues);
        System.out.println(chenilles);
        System.out.println(pattes);
    }
}
