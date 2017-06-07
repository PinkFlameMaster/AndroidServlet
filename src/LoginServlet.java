import java.io.*;
import java.sql.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import org.json.JSONObject;
import com.google.gson.Gson;

/**
 * Servlet implementation class LoginServlet
 */
public class LoginServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    private String ID;
    private String password;
    private String loginResult;
    private List<Map<String, Object>> listForLogin = new ArrayList<Map<String, Object>>();
    public   Connection conn =null;
    public   Statement stmt;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        try
        {
            request.setCharacterEncoding("UTF-8");
            System.out.println(request.getContentType());
            System.out.println(request.getRemoteAddr());

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            request.getInputStream()));
            String line = null;
            StringBuffer s = new StringBuffer();
            while ((line = br.readLine()) != null)
            {
                s.append(line);
            }
            br.close();
            JSONObject json = new JSONObject(s.toString());

            ID = new String(json.getString("ID").getBytes(),"UTF-8");//json.getString("ID");
            password = new String(json.getString("password").getBytes(),"UTF-8");
            System.out.println("ID"+ID);
            System.out.println("password"+password);


            try
            {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection("jdbc:mysql://123.206.125.166:3306/helpme", "root","54321");
                stmt = conn.createStatement();

                String query = "select password from person where schoolNum='"+ID+"';";
                ResultSet result = stmt.executeQuery(query);

                while(result.next())
                {
                    System.out.println("密码："+result.getString("password"));
                    if(password.equals(result.getString("password")))
                        loginResult="success";
                    else
                        loginResult="failed";
                }
                System.out.println("结果："+loginResult);


                if(loginResult.equals("success"))
                {
                    query = "select * from person where schoolNum='"+ID+"';";
                    result = stmt.executeQuery(query);
                    ResultSetMetaData rm = result.getMetaData();

                    while (result.next())
                    {
                        Map<String, Object> mapForLogin = new HashMap<String, Object>();
                        mapForLogin.put("result",loginResult);
                        listForLogin.add(mapForLogin);

                        for (int i = 1; i <= rm.getColumnCount()-1; i++)
                        {
                            mapForLogin.put(rm.getColumnName(i), result.getObject(i));
                            System.out.println(result.getObject(i));
                        }
                        listForLogin.add(mapForLogin);
                    }
                }
                else
                {
                    Map<String, Object> mapForLogin = new HashMap<String, Object>();
                    mapForLogin.put("result",loginResult);
                    listForLogin.add(mapForLogin);
                }

                stmt.close();
                conn.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

            Gson gson = new Gson();
            JSONObject rjson = new JSONObject();
            String gs = gson.toJson(listForLogin);

            rjson.put("loginResult", gs);
            System.out.println(gs);
            System.out.println(rjson);
            OutputStream out = response.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out,"UTF-8"));
            bw.write(rjson.toString());
            bw.flush();
            out.close();
            bw.close();
            listForLogin.removeAll(listForLogin);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}