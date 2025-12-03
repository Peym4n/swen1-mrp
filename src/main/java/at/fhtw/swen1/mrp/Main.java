package at.fhtw.swen1.mrp;

import at.fhtw.swen1.mrp.data.DatabaseConnection;

public class Main {
    public static void main(String[] args) {
        DatabaseConnection.executeInitScript();
    }
}
