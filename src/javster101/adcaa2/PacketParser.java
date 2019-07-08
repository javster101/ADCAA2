package javster101.adcaa2;

import com.opengg.core.console.GGConsole;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.thread.ThreadManager;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.Time;
import javster101.adcaa2.components.FlightManager;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class PacketParser {
    List<DataFrame> elements = new ArrayList<>();
    MulticastSocket socket;

    float currentTime = 0f;

    boolean fullyInitialized = false;
    Time time;

    public void run(){
        ThreadManager.runDaemon(this::receivePackets, "PacketReceiver");
    }

    public void receivePackets(){
        try {
            time = new Time();
            socket = new MulticastSocket(42055);
            var group = InetAddress.getByName("237.7.7.7");
            GGConsole.log("Listening on multicast address " + group);
            socket.joinGroup(group);
            socket.setSoTimeout(3000);

            while(true){
                byte[] buffer = new byte[176];
                var packet = new DatagramPacket(buffer, buffer.length);
                try {
                    socket.receive(packet);
                    var newFrame = DataParser.parseFromPacket(new GGInputStream(packet.getData(), true), currentTime);

                    elements.add(newFrame);
                    if(fullyInitialized){
                        currentTime += time.getDeltaSec();
                        newFrame.time = currentTime;
                        FlightManager.currentManager.setCurrentFrame(newFrame);
                    }else {
                        GGConsole.log("Received first packet, running simulation");
                        OpenGG.syncExec(() -> ADCAA2.initializeNetworkFlightManager(newFrame));
                        fullyInitialized = true;
                        time.getDeltaSec();
                    }

                   // GGConsole.debug("Received network packet");
                }catch (SocketTimeoutException e){
                    if(!fullyInitialized){
                        GGConsole.log("Failed to find first packet initially, restarting connection");
                    }else{
                        GGConsole.log("Completed packet reception, running full simulation");
                        break;
                    }

                }
            }
            socket.leaveGroup(group);
            socket.close();

            OpenGG.asyncExec(() -> ADCAA2.initializeFromFrameList(elements));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
