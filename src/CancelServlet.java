import java.io.*;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import org.json.JSONObject;

/**
 * Servlet implementation class CancelConnection
 */
public class CancelServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private String ID;
    private Timestamp cancelTime;
    public   Connection conn =null;
    public   Statement stmt;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public CancelServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    private Timestamp int_To_timestamp(int year,int month,int day,int hour,int minute,int second)
    {
        String time=String.valueOf(year)+"-"+String.valueOf(month)+"-"+String.valueOf(day)+" "+String.valueOf(hour)+":"+
                String.valueOf(minute)+":"+String.valueOf(second);
        return Timestamp.valueOf(time);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        try
        {
            request.setCharacterEncoding("UTF-8");
            System.out.println(request.getContentType());//得到客户端发送过来内容的类型
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
            //请一定按照实例的编码格式写！！！不然中文会乱码
            //String title = new String(json.getString("title").getBytes(),"UTF-8");//从json对象中得到相应key的值
            //String content = new String(json.getString("content").getBytes(),"UTF-8");
            ID = new String(json.getString("ID").getBytes(),"UTF-8");//json.getString("ID");//从json对象中得到相应key的值
            int year=new Integer(json.getInt("year"));
            int month=new Integer(json.getInt("month"));
            int day=new Integer(json.getInt("day"));
            int hour=new Integer(json.getInt("hour"));
            int minute=new Integer(json.getInt("minute"));
            int second=new Integer(json.getInt("second"));

            cancelTime=int_To_timestamp(year,month,day,hour,minute,second);

            //数据库操作
            try{
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection("jdbc:mysql://123.206.125.166:3306/helpme", "root","54321");//权限和密码
                stmt = conn.createStatement(); //创建Statement对象

                String query = "update mission set state=3,cancelTime='"+cancelTime.toString()+"' where missionID='"+ID+"';";
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
