/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Home Tech
 */
import java.util.*;

public class RBAC {
    public static void main(String[] args) {

        // Assign permissions to roles
        HashMap<String, ArrayList<String>> rolePermissions = new HashMap<>(); 

        rolePermissions.put("Admin", new ArrayList<>(
                Arrays.asList("Create", "View", "Update", "Delete")));

        rolePermissions.put("Manager", new ArrayList<>(
                Arrays.asList("View", "Update")));

        rolePermissions.put("Customer", new ArrayList<>(
                Arrays.asList("View")));

        // Assign users to roles
        HashMap<String, String> userRoles = new HashMap<>();

        userRoles.put("Ahmad", "Admin");
        userRoles.put("Sara", "Manager");
        userRoles.put("Lina", "Customer");

        Scanner input = new Scanner(System.in);

        System.out.println("Role-Based Access Control (RBAC)");
        System.out.println("--------------------------------");

        System.out.print("Enter username (Ahmad, Sara, Lina): ");
        String username = input.nextLine();

        if (!userRoles.containsKey(username)) {
            System.out.println("User not found.");
            input.close();
            return;
        }

        String role = userRoles.get(username);

        System.out.println("User Role: " + role);
        System.out.println("Available actions: Create, View, Update, Delete");

        System.out.print("Enter action: ");
        String action = input.nextLine();

        ArrayList<String> permissions = rolePermissions.get(role);

        // Check access permission
        if (permissions.contains(action)) {
            System.out.println("Access Granted: " + username
                    + " can " + action + " accounts.");
        } else {
            System.out.println("Access Denied: " + username
                    + " cannot " + action + " accounts.");
        }

        input.close();
    }
}
