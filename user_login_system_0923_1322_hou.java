// 代码生成时间: 2025-09-23 13:22:02
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class UserLoginSystem {
    // Mock user database for demonstration purposes
    private static final Map<String, String> userDatabase = new HashMap<>();
    static {
        // Adding test users to the database
        userDatabase.put("user1", "password1");
        userDatabase.put("user2", "password2");
    }
    
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7000);

        // Endpoint for login
        app.post("/login", new Handler<Context>() {
            @Override
            public void handle(Context ctx) {
                String username = ctx.formParam("username");
                String password = ctx.formParam("password");

                try {
                    // Check if the user exists in the database
                    if (userDatabase.containsKey(username) && userDatabase.get(username).equals(password)) {
                        ctx.status(200).result("Login successful");
                    } else {
                        ctx.status(401).result("Invalid username or password");
                    }
                } catch (Exception e) {
                    ctx.status(500).result("Internal server error: " + e.getMessage());
                }
            }
        });
    }
}
