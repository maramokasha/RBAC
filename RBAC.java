import java.util.*;

/**
 * Simple Role-Based Access Control (RBAC) program - Java only.
 * Chapter 4 - Access Control, Software Security (SDEV 4303)
 *
 * Idea:  users --assigned to--> roles --have--> permissions on the "Account" object
 * A user never gets a permission directly. He gets it only through his role.
 */
public class RBAC {

    private final Map<String, Set<String>> rolePermissions = new LinkedHashMap<>(); // role -> permissions
    private final Map<String, Set<String>> userRoles = new LinkedHashMap<>();       // user -> roles

    // ---------- role / permission management ----------
    public void addRole(String role) {
        rolePermissions.putIfAbsent(role, new TreeSet<>());
    }

    public void assignPermissionToRole(String role, String permission) {
        if (!rolePermissions.containsKey(role))
            throw new IllegalArgumentException("Role '" + role + "' does not exist");
        rolePermissions.get(role).add(permission);
    }

    public void removePermissionFromRole(String role, String permission) {
        rolePermissions.get(role).remove(permission);
    }

    // ---------- user management ----------
    public void addUser(String user) {
        userRoles.putIfAbsent(user, new TreeSet<>());
    }

    public void assignRoleToUser(String user, String role) {
        if (!userRoles.containsKey(user))
            throw new IllegalArgumentException("User '" + user + "' does not exist");
        if (!rolePermissions.containsKey(role))
            throw new IllegalArgumentException("Role '" + role + "' does not exist");
        userRoles.get(user).add(role);
    }

    // ---------- the access check ----------
    public boolean isAllowed(String user, String permission) {
        for (String role : userRoles.getOrDefault(user, Collections.emptySet())) {
            if (rolePermissions.getOrDefault(role, Collections.emptySet()).contains(permission))
                return true;
        }
        return false;
    }

    public boolean userExists(String user) {
        return userRoles.containsKey(user);
    }

    // ---------- build the system from the assignment ----------
    static RBAC buildSystem() {
        RBAC rbac = new RBAC();

        // 1) create the three roles
        for (String role : new String[]{"Admin", "Manager", "Customer"})
            rbac.addRole(role);

        // 2) give permissions (access rights) to roles - object is "Account"
        for (String p : new String[]{"Create", "View", "Update", "Delete"})
            rbac.assignPermissionToRole("Admin", p);
        for (String p : new String[]{"View", "Update"})
            rbac.assignPermissionToRole("Manager", p);
        rbac.assignPermissionToRole("Customer", "View");

        // 3) create users and assign them to roles
        String[][] users = {{"Alice", "Admin"}, {"Bob", "Manager"}, {"Carol", "Customer"}};
        for (String[] u : users) {
            rbac.addUser(u[0]);
            rbac.assignRoleToUser(u[0], u[1]);
        }
        return rbac;
    }

    void showTables() {
        System.out.println("=== Roles and their permissions on the 'Account' object ===");
        for (Map.Entry<String, Set<String>> e : rolePermissions.entrySet())
            System.out.printf("  %-9s: %s%n", e.getKey(), String.join(", ", e.getValue()));
        System.out.println("\n=== Users and their roles ===");
        for (Map.Entry<String, Set<String>> e : userRoles.entrySet())
            System.out.printf("  %-6s-> %s%n", e.getKey(), String.join(", ", e.getValue()));
    }

    static String answer(boolean ok) {
        return ok ? "ALLOWED" : "DENIED";
    }

    void demo() {
        showTables();
        System.out.println("\n=== Access checks ===");
        for (String user : userRoles.keySet()) {
            System.out.println("\nUser: " + user + " (role: " + String.join(", ", userRoles.get(user)) + ")");
            for (String action : new String[]{"Create", "View", "Update", "Delete"})
                System.out.printf("   %-7s account -> %s%n", action, answer(isAllowed(user, action)));
        }
    }

    static void interactive(RBAC rbac) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Type: <user> <action>   (example: Bob Update)   |  q to quit");
        while (true) {
            System.out.print("> ");
            if (!sc.hasNextLine()) break;
            String line = sc.nextLine().trim();
            if (line.equalsIgnoreCase("q") || line.equalsIgnoreCase("quit")) break;
            String[] parts = line.split("\\s+");
            if (parts.length != 2) { System.out.println("Please write: <user> <action>"); continue; }
            if (!rbac.userExists(parts[0])) { System.out.println("Unknown user '" + parts[0] + "'"); continue; }
            String action = parts[1].substring(0, 1).toUpperCase() + parts[1].substring(1).toLowerCase();
            System.out.println(answer(rbac.isAllowed(parts[0], action)));
        }
    }

    public static void main(String[] args) {
        RBAC system = buildSystem();
        String mode = args.length > 0 ? args[0] : "";
        if (mode.equals("--interactive")) {
            interactive(system);
        } else if (mode.equals("--change")) {
            // extra demo: change a role's permission and see the effect on all its users
            system.showTables();
            System.out.println("\n>> Admin removes 'Update' from the Manager role");
            system.removePermissionFromRole("Manager", "Update");
            System.out.println("   Can Bob (Manager) Update now?  " + answer(system.isAllowed("Bob", "Update")));
            System.out.println("   Can Bob (Manager) View now?    " + answer(system.isAllowed("Bob", "View")));
        } else {
            system.demo();
        }
    }
}
