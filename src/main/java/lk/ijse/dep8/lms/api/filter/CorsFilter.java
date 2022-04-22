package lk.ijse.dep8.lms.api.filter;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "CorsFilter", urlPatterns = "/*")
public class CorsFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String origin = req.getHeader("Origin");
        if (origin != null && origin.contains(getServletContext().getInitParameter("origin"))){
            res.setHeader("Access-Control-Allow-Origin", origin);
            if (req.getMethod().equals("OPTIONS")){
                res.setHeader("Access-Control-Allow-Methods", "GET, PUT, POST, DELETE, HEAD, OPTIONS");
                res.setHeader("Access-Control-Allow-Headers", "Content-Type");
                res.setHeader("Access-Control-Expose-Headers", "Content-Type");
            }
        }
        chain.doFilter(req,res);
    }
}
