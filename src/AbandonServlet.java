import java.io.*;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import org.json.JSONObject;
/**
 * Servlet implementation class AbandonServlet
 */
public class AbandonServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    private String ID;
    public   Statement stmt;
    public   Connection conn =null;

    /**
     * Default constructor.
     */
    public AbandonServlet()
    {
        super();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        try
        {
            request.setCharacterEncoding("UTF-8");
            System.out.println(request.getContentType());//得到客户端发送过来内容的类型，application/json;charset=UTF-8
            System.out.println(request.getRemoteAddr());//得到客户端的ip地址，

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(//使用字符流读取客户端发过来的数据
                            request.getInputStream()));
            String line = null;
            StringBuffer s = new StringBuffer();
            while ((line = br.readLine()) != null)
            {
                s.append(line);
            }
            br.close();
            System.out.println(s.toString());//{"password":"123456","name":"admin"}
            JSONObject json = new JSONObject(s.toString());

            //接收从前端传过来的数据，下面是示例
            ID = new String(json.getString("ID").getBytes(),"UTF-8");//json.getString("ID");//从json对象中得到相应key的值

            //数据库操作
            try
            {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection("jdbc:mysql://123.206.125.166:3306/helpme", "root","54321");//权限和密码
                stmt = conn.createStatement(); //创建Statement对象

                String query = "update mission set state=0 where missionID="+ID+"';";
                stmt.executeUpdate(query);

                stmt.close();
                conn.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}






