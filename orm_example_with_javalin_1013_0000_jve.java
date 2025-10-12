// 代码生成时间: 2025-10-13 00:00:44
import io.javalin.Javalin;
import io.javalin.plugin.json.JavalinJackson;
import org.json.JSONObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// ORM类
public class ORM {
    private Connection connection;

    public ORM(String url, String user, String password) {
        try {
            this.connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<User> getUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            users.add(new User(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getInt("age")
            ));
        }

        return users;
    }

    public User getUserById(int id) throws SQLException {
        String query = "SELECT * FROM users WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, id);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return new User(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getInt("age")
            );
        }

        return null;
    }

    public void addUser(User user) throws SQLException {
        String query = "INSERT INTO users(name, age) VALUES (?, ?)";
        PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, user.getName());
        statement.setInt(2, user.getAge());
        statement.executeUpdate();

        ResultSet generatedKeys = statement.getGeneratedKeys();
        if (generatedKeys.next()) {
            user.setId(generatedKeys.getInt(1));
        }
    }
}

// User实体类
class User {
    private int id;
    private String name;
    private int age;

    public User(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    // getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}

// Javalin应用程序
public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7000);
        app.use(JavalinJackson.create());

        // ORM实例
        ORM orm = new ORM("jdbc:mysql://localhost:3306/your_database", "user", "password");

        // 获取用户列表
        app.get("/users", ctx -> {
            try {
                List<User> users = orm.getUsers();
                ctx.json(users);
            } catch (SQLException e) {
                ctx.status(500);
                ctx.result("Database error");
            }
        });

        // 根据ID获取用户
        app.get("/users/:id", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            try {
                User user = orm.getUserById(id);
                if (user != null) {
                    ctx.json(user);
                } else {
                    ctx.status(404);
                    ctx.result("User not found");
                }
            } catch (SQLException e) {
                ctx.status(500);
                ctx.result("Database error");
            }
        });

        // 添加用户
        app.post("/users", ctx -> {
            JSONObject reqBody = ctx.bodyAsJson();
            try {
                User user = new User(0, reqBody.getString("name"), reqBody.getInt("age"));
                orm.addUser(user);
                ctx.json(user);
            } catch (SQLException e) {
                ctx.status(500);
                ctx.result("Database error");
            }
        });
    }
}