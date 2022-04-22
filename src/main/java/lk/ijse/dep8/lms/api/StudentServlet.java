package lk.ijse.dep8.lms.api;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import lk.ijse.dep8.lms.dto.StudentDTO;
import lk.ijse.dep8.lms.exception.ValidationException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;

@WebServlet(name = "StudentServlet", urlPatterns = {"/students", "/students/"}, loadOnStartup = 1)
public class StudentServlet extends HttpServlet {

    @Resource(name = "java:comp/env/jdbc/pool4lms")
    private volatile DataSource pool;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        String origin = req.getHeader("Origin");
//        if (origin.contains("localhost:1234")){
//            resp.setHeader("Access-Control-Allow-Origin", origin);
//            if (req.getMethod().equals("OPTIONS")) {
//                resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, HEAD");
//                resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
//                resp.setHeader("Access-Control-Expose-Headers", "Content-Type");
//            }
//        }
        super.service(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getContentType() == null ||
                !req.getContentType().toLowerCase().startsWith("application/json")) {
            resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            return;
        }

        Jsonb jsonb = JsonbBuilder.create();
        try {
            StudentDTO student = jsonb.fromJson(req.getReader(), StudentDTO.class);

            if (student.getName() == null || !student.getName().matches("[A-Za-z ]+")) {
                throw new ValidationException("Invalid student name");
            } else if (student.getNic() == null || !student.getNic().matches("\\d{9}[Vv]")) {
                throw new ValidationException("Invalid student nic");
            } else if (student.getEmail() == null || !student.getEmail().matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")) {
                throw new ValidationException("Invalid student email");
            }

            try (Connection connection = pool.getConnection()) {
                PreparedStatement stm = connection.
                        prepareStatement("INSERT INTO student (name, nic, email) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS);
                stm.setString(1, student.getName());
                stm.setString(2, student.getNic());
                stm.setString(3, student.getEmail());
                if (stm.executeUpdate() != 1) {
                    throw new RuntimeException("Failed to save the student");
                }
                ResultSet rst = stm.getGeneratedKeys();
                rst.next();
                student.setId(rst.getString(1));

                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.setContentType("application/json");
                jsonb.toJson(student, resp.getWriter());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (JsonbException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON");
        } catch (ValidationException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, t.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }
}
