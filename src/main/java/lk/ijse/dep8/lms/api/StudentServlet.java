package lk.ijse.dep8.lms.api;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import lk.ijse.dep8.lms.dto.StudentDTO;
import lk.ijse.dep8.lms.exception.ValidationException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StudentServlet extends HttpServlet {

    private volatile DataSource pool;

    public StudentServlet() {
    }

    @Override
    public void init() throws ServletException {
        try {
            InitialContext ctx = new InitialContext();
            pool = (DataSource) ctx.lookup("java:comp/env/jdbc/pool4lms");
        } catch (NamingException e) {
            e.printStackTrace();
        }
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

            if (student.getName() == null || !student.getName().matches("[A-Za-z ]+")){
                throw new ValidationException("Invalid student name");
            }else if (student.getNic() == null || !student.getNic().matches("\\d{9}[Vv]")){
                throw new ValidationException("Invalid student nic");
            }else if (student.getEmail() == null || !student.getEmail().matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")){
                throw new ValidationException("Invalid student email");
            }

            try(Connection connection = pool.getConnection()){
                PreparedStatement stm = connection.prepareStatement("INSERT INTO student (name, nic, email) VALUES (?,?,?)");
                stm.setString(1, student.getName());
                stm.setString(2, student.getNic());
                stm.setString(3, student.getEmail());
                if (stm.executeUpdate() != 1){
                    throw new RuntimeException("Failed to save the student");
                }
                resp.setStatus(HttpServletResponse.SC_CREATED);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }catch (JsonbException e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON");
        }catch (ValidationException e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }catch (Throwable t){
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
