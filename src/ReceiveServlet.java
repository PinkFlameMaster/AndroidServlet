import java.io.*;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import org.json.JSONObject;
/**
 * Servlet implementation class ReceiveServlet
 */
public class ReceiveServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private String ID,schoolNum;
    private Timestamp receiveTime;
    public   Connection conn =null;
    public   Statement stmt;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReceiveServlet()
    {
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
            System.out.println(s.toString());//{"password":"123456","name":"admin"}
            JSONObject json = new JSONObject(s.toString());

            ID = new String(json.getString("ID").getBytes(),"UTF-8");
            schoolNum=new String("schoolNumber");
            int year=new Integer(json.getInt("year"));
            int month=new Integer(json.getInt("month"));
            int day=new Integer(json.getInt("day"));
            int hour=new Integer(json.getInt("hour"));
            int minute=new Integer(json.getInt("minute"));
            int second=new Integer(json.getInt("second"));

            receiveTime=int_To_timestamp(year,month,day,hour,minute,second);
            try{
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection("jdbc:mysql://123.206.125.166:3306/helpme", "root","54321");
                stmt = conn.createStatement();

                String query = "update mission set state=1,receiveTime='"+receiveTime.toString()+"',recipientNum='"+
                        schoolNum+"' where missionID='"+ID+"';";
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
