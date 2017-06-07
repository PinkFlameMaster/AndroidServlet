import java.io.*;
import java.sql.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import org.json.JSONObject;
import com.google.gson.Gson;

/**
 * Servlet implementation class MyMission
 */
public class MyMissionServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    private String schoolNum;
    private int state;
    public   Connection conn =null;
    public   Statement stmt;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public MyMissionServlet()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    public String returnString(Timestamp ts)
    {
        int year=ts.getYear();
        int month=ts.getMonth();
        int day=ts.getDay();
        int hour=ts.getHours();
        int minute=ts.getMinutes();
        int second=ts.getSeconds();
        String time=String.valueOf(year)+"-"+String.valueOf(month)+"-"+String.valueOf(day)+" "+String.valueOf(hour)+":"+
                String.valueOf(minute)+":"+String.valueOf(second);
        return time;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        try
        {
            request.setCharacterEncoding("UTF-8");

            List<Map<String, Object>> listForMission = new ArrayList<Map<String, Object>>();
            List<Map<String, Object>> listForReceivePerson = new ArrayList<Map<String, Object>>();
            List<Map<String, Object>> listForPublishPerson = new ArrayList<Map<String, Object>>();

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
            System.out.println(s.toString());
            JSONObject json = new JSONObject(s.toString());

            schoolNum=new String(json.getString("schoolNumber").getBytes(),"UTF-8");
            state=new Integer(json.getInt("state"));

            ResultSet resultCase1_1=null;
            ResultSet resultCase1_2=null;
            ResultSet resultCase1Info=null;//当前用户信息，可能作为发布者或者接收者传回前端
            ResultSet resultCase2=null;
            switch(state)
            {
                case 1:
                    try{
                        Class.forName("com.mysql.jdbc.Driver");
                        conn = DriverManager.getConnection("jdbc:mysql://123.206.125.166:3306/helpme", "root","54321");
                        stmt = conn.createStatement();

                        String query1 = "select missionID,title,content,state,mission.gender,attribute,scope,createTime,"
                                + "receiveTime,finishTime,cancelTime,name,schoolNum,person.gender,departmentName,introduction"
                                + " from mission left join person on mission.recipientNum=person.schoolNum where publisherNum='"
                                + schoolNum+"';";

                        resultCase1_1 = stmt.executeQuery(query1);

                        String query2= "select missionID,title,content,state,mission.gender,attribute,scope,createTime,"
                                + "receiveTime,finishTime,cancelTime,name,schoolNum,person.gender,departmentName,introduction"
                                + " from mission left join person on mission.publisherNum=person.schoolNum where recipientNum='"
                                + schoolNum+"';";
                        resultCase1_2 = stmt.executeQuery(query2);

                        String query3="select name,schoolNum,gender,departmentName,introduction from person where schoolNum='"
                                +schoolNum+"';";
                        resultCase1Info=stmt.executeQuery(query3);
                    }
                    catch (SQLException e)
                    {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    try{
                        Class.forName("com.mysql.jdbc.Driver");
                        conn = DriverManager.getConnection("jdbc:mysql://123.206.125.166:3306/helpme", "root","54321");
                        stmt = conn.createStatement();

                        String query = "select missionID,title,content,state,mission.gender,attribute,scope,createTime,"
                                + "receiveTime,finishTime,cancelTime,name,schoolNum,person.gender,departmentName,introduction"
                                + " from mission left join person on mission.publisherNum=person.schoolNum"
                                + " where state=0 and mission.publisherNum<>'"+schoolNum+"';";

                        resultCase2 = stmt.executeQuery(query);
                    }
                    catch (SQLException e)
                    {
                        e.printStackTrace();
                    }
                    break;
            }

            switch(state)
            {
                case 1:
                    ResultSetMetaData rmCase1_1 = resultCase1_1.getMetaData();
                    ResultSetMetaData rmCase1_2 = resultCase1_2.getMetaData();
                    ResultSetMetaData rmCase1Info = resultCase1Info.getMetaData();

                    while (resultCase1_1.next())//我发布过的任务
                    {
                        Map<String, Object> mapForMission = new HashMap<String, Object>();
                        Map<String, Object> mapForReceivePerson = new HashMap<String, Object>();
                        Map<String, Object> mapForPublishPerson = new HashMap<String, Object>();
                        for (int i = 1; i <= rmCase1_1.getColumnCount(); i++)
                        {
                            if(i<=11)
                            {
                                if(i>=8)
                                {
                                    String time="";
                                    if(resultCase1_1.getTimestamp(i)!=null) {
                                        time = returnString(resultCase1_1.getTimestamp(i));

                                    }
                                    mapForMission.put(rmCase1_1.getColumnName(i), time);
                                }
                                else
                                {
                                    mapForMission.put(rmCase1_1.getColumnName(i), resultCase1_1.getObject(i));
                                }
                                System.out.println(rmCase1_1.getColumnName(i));
                                System.out.println(resultCase1_1.getObject(i));
                            }
                            else
                            {
                                mapForPublishPerson.put(rmCase1Info.getColumnName(i-11),resultCase1Info.getObject(i-11));
                                mapForReceivePerson.put(rmCase1_1.getColumnName(i), resultCase1_1.getObject(i));
                            }
                        }
                        listForMission.add(mapForMission);
                        listForReceivePerson.add(mapForReceivePerson);
                        listForPublishPerson.add(mapForPublishPerson);
                    }

                    while (resultCase1_2.next())//我接受过得任务
                    {
                        Map<String, Object> mapForMission = new HashMap<String, Object>();
                        Map<String, Object> mapForReceivePerson = new HashMap<String, Object>();
                        Map<String, Object> mapForPublishPerson = new HashMap<String, Object>();
                        for (int i = 1; i <= rmCase1_2.getColumnCount(); i++)
                        {
                            if(i<=11)
                            {
                                if(i>=8)
                                {
                                    String time="";
                                    if(resultCase1_2.getTimestamp(i)!=null) {
                                        time = returnString(resultCase1_2.getTimestamp(i));

                                    }
                                    mapForMission.put(rmCase1_2.getColumnName(i), time);
                                }
                                else
                                {
                                    mapForMission.put(rmCase1_2.getColumnName(i), resultCase1_2.getObject(i));
                                }
                                System.out.println(rmCase1_2.getColumnName(i));
                                System.out.println(resultCase1_2.getObject(i));
                            }
                            else
                            {
                                mapForReceivePerson.put(rmCase1Info.getColumnName(i-11),resultCase1Info.getObject(i-11));
                                mapForPublishPerson.put(rmCase1_2.getColumnName(i), resultCase1_2.getObject(i));
                            }
                        }
                        listForMission.add(mapForMission);
                        listForReceivePerson.add(mapForReceivePerson);
                        listForPublishPerson.add(mapForPublishPerson);
                    }
                    break;

                case 2:
                    ResultSetMetaData rmCase2 = resultCase2.getMetaData();

                    while (resultCase2.next())
                    {
                        Map<String, Object> mapForMission = new HashMap<String, Object>();
                        Map<String, Object> mapForReceivePerson = new HashMap<String, Object>();
                        Map<String, Object> mapForPublishPerson = new HashMap<String, Object>();

                        for (int i = 1; i <= rmCase2.getColumnCount(); i++)
                        {
                            if(i<=11)
                            {
                                if (i >= 8) {
                                    String time = "";
                                    if (resultCase2.getTimestamp(i) != null) {
                                        time = returnString(resultCase2.getTimestamp(i));
                                    }
                                    mapForMission.put(rmCase2.getColumnName(i), time);
                                } else {
                                    mapForMission.put(rmCase2.getColumnName(i), resultCase2.getObject(i));
                                }
                            }
                            else
                            {
                                mapForPublishPerson.put(rmCase2.getColumnName(i), resultCase2.getObject(i));
                            }
                            System.out.println(rmCase2.getColumnName(i));
                            System.out.println(resultCase2.getObject(i));
                        }
                        listForMission.add(mapForMission);
                        listForReceivePerson.add(mapForReceivePerson);
                        listForPublishPerson.add(mapForPublishPerson);
                    }
                    break;
            }

            stmt.close();
            conn.close();
            Gson gson = new Gson();
            JSONObject rjson = new JSONObject();

            String gs1 = gson.toJson(listForMission);
            String gs2 = gson.toJson(listForReceivePerson);
            String gs3 = gson.toJson(listForPublishPerson);

            rjson.put("listForMission", gs1);
            rjson.put("listForReceivePerson", gs2);
            rjson.put("listForPublishPerson",gs3);
            OutputStream out = response.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out,"UTF-8"));
            bw.write(rjson.toString());
            bw.flush();
            out.close();
            bw.close();
            listForMission.removeAll(listForMission);
            listForReceivePerson.removeAll(listForReceivePerson);
            listForPublishPerson.removeAll(listForPublishPerson);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}

