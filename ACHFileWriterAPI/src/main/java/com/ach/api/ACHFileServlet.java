package com.ach.api;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

@WebServlet("/gACHFile")
public class ACHFileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Set response type to JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Read the JSON request body
        StringBuilder jsonStr = new StringBuilder();
        String line;
        while ((line = request.getReader().readLine()) != null) {
            jsonStr.append(line);
        }

        JSONObject jsonRequest = new JSONObject(jsonStr.toString());
        String fileName = jsonRequest.getString("FILE_NAME");
        String settlementDate = jsonRequest.getString("SETTLEMENT_DATE");

        // Call the ACH file writer logic
        ACHFileWriter writer = new ACHFileWriter();
        writer.writeACHFile(fileName, settlementDate);

        // Send response
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("status", "success");
        jsonResponse.put("message", "ACH file generated successfully.");
        out.print(jsonResponse.toString());
        out.flush();
    }
}
