import java.io.*;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import org.json.JSONObject;

/**
 * Servlet implementation class ReleaseServlet
 */
public class ReleaseServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    private String ID,title,content,publisherNum;
    private int gender,attribute,range;
    private Timestamp createTime;
    public   Connection conn =null;
    public   Statement stmt;

    /**
     * Default constructor. 
     */
    public ReleaseServlet()
    {
        super();
    }

    private Timestamp int_To_timestamp(int year,int month,int day,int hour,int minute,int second)
    {
        //"2012-1-14 08:11:00"
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
            System.out.println(s.toString());
            JSONObject json = new JSONObject(s.toString());

            //接收从前端传过来的数据，下面是示例
            //String title = json.getString("title");//从json对象中得到相应key的值
            //String content = json.getString("content");
            ID = new String(json.getString("ID").getBytes(),"UTF-8");//json.getString("ID");//从json对象中得到相应key的值
            publisherNum=new String(json.getString("publisherSchoolNumber").getBytes(),"UTF-8");
            title=new String(json.getString("title").getBytes(),"UTF-8");
            content=new String(json.getString("content").getBytes(),"UTF-8");

            gender=new Integer(json.getInt("gender"));
            attribute=new Integer(json.getInt("attribute"));
            range=new Integer(json.getInt("range"));

            System.out.println(ID);


            int year=new Integer(json.getInt("year"));
            int month=new Integer(json.getInt("month"));
            int day=new Integer(json.getInt("day"));
            int hour=new Integer(json.getInt("hour"));
            int minute=new Integer(json.getInt("minute"));
            int second=new Integer(json.getInt("second"));

            createTime=int_To_timestamp(year,month,day,hour,minute,second);

            //数据库操作
            try{
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection("jdbc:mysql://123.206.125.166:3306/helpme", "root","54321");//权限和密码
                stmt = conn.createStatement(); //创建Statement对象

                String query = "insert into mission(missionID,title,content,state,gender,attribute,scope,publisherNum,createTime)  values('"+
                        ID+"','"+title+"','"+content+"',0,'"+String.valueOf(gender)+"','"+String.valueOf(attribute)+
                        "','"+String.valueOf(range)+"','"+publisherNum+"','"+createTime.toString()+"');";

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