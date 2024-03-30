package application;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/*
class DataMsg
{	private Hashtable ht;

	public DataMsg()
	{   ht = new Hashtable();
	}

	public String get(String id)
	{	String st = (String)ht.get(id);
		ht.remove(id);
		return st;
	}

	public void put(String id, String msg)
	{	ht.put(id, msg);
	}
}
*/
class RecieveData implements Serializable{
    int data;

}

class MapData implements Serializable {

    int MAP_WIDTH = 15;
    int MAP_HEIGHT = 15;
    int flag = -2;
    int[][] map =  {
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 2, 0, 0, 0, 1},
            {1, 0, 0, 2, 0, 0, 1, 0, 0, 0, 1, 0, 0, 2, 1},
            {1, 0, 2, 1, 0, 1, 2, 1, 0, 0, 0, 1, 0, 0, 1},
            {1, 1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 1, 1},
            {1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 1, 2, 0, 1},
            {1, 1, 1, 0, 0, 0, 0, 0, 0, 2, 1, 0, 0, 2, 1},
            {1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1},
            {1, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 0, 2, 0, 1},
            {1, 0, 0, 1, 0, 1, 0, 2, 0, 0, 1, 0, 1, 1, 1},
            {1, 0, 0, 1, 0, 1, 0, 0, 0, 2, 0, 0, 0, 0, 1},
            {1, 1, 0, 2, 0, 1, 1, 0,2, 0, 0, 2, 2, 0, 1},
            {1, 0, 0, 0, 0, 0, 2, 0, 1, 0, 1, 0, 0, 0, 1},
            {1, 0, 2, 0, 0, 0, 2, 0, 0, 0, 0, 2, 1, 0, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
};
    int[][] bombTimers = new int[map.length][map[0].length];
    int[][] players = {
            {1, 1,0},
            {1, MAP_WIDTH - 2,0},
            {MAP_HEIGHT - 2, 1,0},
            {MAP_HEIGHT - 2, MAP_WIDTH - 2,0},
    };
}
class SocketThread extends Thread {
    private static final int MAP_WIDTH = 15;
    private static final int MAP_HEIGHT = 15;

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private static  MapData md;
    private RecieveData rd;
    int nn = 0;


    public SocketThread(Socket s, int n, MapData m) throws IOException {
        socket = s;
        md = m;
        nn = n;

        in = new ObjectInputStream(socket.getInputStream());
        out = new ObjectOutputStream(socket.getOutputStream());
        //ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        start();
    }

    public void run() {
        try {
            // String serverString = null;
            // String clientString = null;
            ScheduledExecutorService bombScheduler = Executors.newScheduledThreadPool(3);
            bombScheduler.scheduleAtFixedRate(() -> sendMapData(), 0, 30, TimeUnit.MILLISECONDS);
            bombScheduler.scheduleAtFixedRate(() -> updateBombs(), 0, 2000, TimeUnit.MILLISECONDS);
         //  System.out.println("玩家: " + nn + "  connected");

            while (true) {    //serverString = Data.get(other);
                Thread.currentThread().sleep(100);
                //out.writeObject(md);
// System.out.println(in.available());
                Object o=in.readObject();
                if(o!=null){
//                    System.out.println("接收到信息");
                    // System.out.println(in.available());
                    rd=(RecieveData)o;
                    switch (rd.data){
                        case 0:
			boolean flag=true;
                             for(int i=0;i<4;i++){
                                 if(md.players[i][2]==0) flag=false;
                             }
                           if(flag) md.flag=5;

           //                 System.out.println("玩家:"+nn+"选择开始游戏");
                            break;
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                            md.players[nn-1][2]=rd.data;
             //               System.out.println("玩家:"+nn+" 选择角色:"+rd.data);
                            break;
                        case 5:
                            if (isValidMove(md.players[nn-1][0], md.players[nn-1][1] - 1)) {
                                md.players[nn-1][1]--;
                            }
               //             System.out.println("玩家:"+nn+" 上");
                            break;
                        case 6:
                            if (isValidMove(md.players[nn-1][0], md.players[nn-1][1] + 1)) {
                                md.players[nn-1][1]++;
                            }
                 //           System.out.println("玩家:"+nn+" 下");
                            break;
                        case 7:
                            if (isValidMove(md.players[nn-1][0]-1, md.players[nn-1][1])) {
                                md.players[nn-1][0]--;
                            }
                   //         System.out.println("玩家:"+nn+"左");
                            break;
                        case 8:
                            if (isValidMove(md.players[nn-1][0]+1, md.players[nn-1][1])) {
                                md.players[nn-1][0]++;
                            }
                   //         System.out.println("玩家:"+nn+"右");
                            break;
                        case 9:
                            md.bombTimers[md.players[nn-1][1]][md.players[nn-1][0]]=5;
                            md.map[md.players[nn-1][1]][md.players[nn-1][0]]=3;
               //               System.out.println("玩家:"+nn+" 放了一个炸弹");
                            break;

                    }}

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private boolean isValidMove(int x, int y) {
        boolean isPeople=false;
        for(int i=0;i<4;i++){
            if(x==md.players[i][0]&&y==md.players[i][1])isPeople=true;
        }
        return x >= 0 && x < MAP_WIDTH && y >= 0 && y < MAP_HEIGHT && (md.map[y][x] ==0)&&(!isPeople);
    }

    private void updateBombs() {
     //  System.out.println("炸弹更新了");
        for (int y = 0; y < md.bombTimers.length; y++) {
            for (int x = 0; x < md.bombTimers[y].length; x++) {
                if (md.bombTimers[y][x] > 0) {
                    md.bombTimers[y][x]-=1;
                    if (md.bombTimers[y][x] == 2) {
                                             
                       explode(x, y);
                    }
                    if(md.bombTimers[y][x]==0){
                   int [][]bomb= {{0,-1},{1,0},{-1,0},{0,1},{0,0}};
                  for(int i=0;i<5;i++)
                  {
           	 int y1=y+bomb[i][1];
           	 int x1=x+bomb[i][0];
           	 if(md.map[y1][x1]!=1){
             	   md.map[y1][x1] = 0;
           		 }
    		  } 
                   }
                }
            }
        }
    } 
public void sendMapData()
{
        MapData sent=new MapData();
 //       System.out.println("fasongshujvbao");
        sent.flag=md.flag;
                for(int i=0;i<sent.MAP_WIDTH;i++){
                    for(int j=0;j<sent.MAP_HEIGHT;j++){
                        sent.map[i][j]=md.map[i][j];
                        sent.bombTimers[i][j]=md.bombTimers[i][j];
                            }
                }
                for(int i=0;i<4;i++){
                    for(int j=0;j<3;j++){
                        sent.players[i][j]=md.players[i][j];
                    }
                }
try {  //     System.out.println(sent.flag);
            out.writeObject(sent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

}   
 private void explode(int bombX, int bombY) {
        // 澶勭悊鐖嗙偢鐨勯 昏緫锛屼緥濡傜偢姣佸懆鍥寸殑闅滅鐗┿ 佷激瀹崇帺瀹剁瓑
        // 杩欓噷绠 鍖栦负灏嗙偢寮圭垎鐐哥殑浣嶇疆璁句负鍙
   //     System.out.println("炸弹位置:"+bombY+bombX+"  爆炸");
 int [][]bomb= {{0,-1},{1,0},{-1,0},{0,1},{0,0}};
	 for(int i=0;i<5;i++) 
	{  
 			int x1=bombX+bomb[i][0];
	              	int y1=bombY+bomb[i][1];
	      		if(md.map[y1][x1]!=1){            
	               	md.map[y1][x1] = 4;
	            		}
	              	 
                        for(int k=0;k<4;k++){
			if(md.players[k][0]==x1&&md.players[k][1]==y1){
                                     
                                     md.players[k][0]=-1;
                                     md.players[k][1]=-1;
                       
        	                }
	       		}
	}     
}
public class ChatServer3 {
    private static final int MAP_WIDTH = 15;
    private static final int MAP_HEIGHT = 15;
    private  int nn = 0;


    public  MapData md = new MapData();

    public void main(String args[]) throws IOException {


        ServerSocket serverSocket = new ServerSocket(3434);
        System.out.println("Readly Hello! Enter exit to exit.");


        try {
            while (true) {
                Socket server = serverSocket.accept();
                try {
                    nn++;

                    new SocketThread(server, nn, md);
                } catch (IOException e) {
                    server.close();
                }
            }
        } finally {
            serverSocket.close();
        }
    }
}
}
