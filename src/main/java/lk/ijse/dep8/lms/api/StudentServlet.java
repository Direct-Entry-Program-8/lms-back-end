package lk.ijse.dep8.lms.api;

import jakarta.json.*;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import lk.ijse.dep8.lms.util.Customer;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class StudentServlet extends HttpServlet {

    public StudentServlet() {
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getContentType() == null ||
                !req.getContentType().toLowerCase().startsWith("application/json")){
            resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            return;
        }

//        List<Customer> customerList = new ArrayList<>();

//        JsonReader jsonReader = Json.createReader(req.getReader());
//        JsonArray jsonArray = jsonReader.readArray();
//        for (int i = 0; i < jsonArray.size(); i++) {
//            JsonObject jsonObject = jsonArray.getJsonObject(i);
//            String id = jsonObject.getString("id");
//            String name = jsonObject.getString("name");
//            String address = jsonObject.getString("address");
//            customerList.add(new Customer(id, name, address));
//        }

        Jsonb jsonb = JsonbBuilder.create();
        List<Customer> customerList = jsonb.fromJson(req.getReader(),
                new ArrayList<Customer>(){}.getClass().getGenericSuperclass());

        customerList.forEach(System.out::println);

        List<Customer> anotherCustomerList = new ArrayList<>();
        anotherCustomerList.add(new Customer("C001", "Nuwan", "Galle"));
        anotherCustomerList.add(new Customer("C001", "Nuwan", "Galle"));
        anotherCustomerList.add(new Customer("C001", "Nuwan", "Galle"));
        anotherCustomerList.add(new Customer("C001", "Nuwan", "Galle"));
        anotherCustomerList.add(new Customer("C001", "Nuwan", "Galle"));

        resp.setContentType("application/json");
        jsonb.toJson(anotherCustomerList, resp.getWriter());

    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }
}
