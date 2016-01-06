package activity;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.network.Ip4;
import util.Tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NetWorkCapture {
    static StringBuilder err = new StringBuilder();
    static HashMap<String, String> map = new HashMap<>();
    static PcapIf select;

    public static void main(String[] args) {
        List<PcapIf> devices = new ArrayList<>();
        int status = Pcap.findAllDevs(devices, err);
        if (status != Pcap.OK || devices.isEmpty()) {
            System.out.println("Error log " + err.toString());
            return;
        }

        int len = 64 * 1000;
        int flag = Pcap.MODE_PROMISCUOUS;
        int timeout = 10 * 1000;

        PcapPacketHandler<String> handler = (pcapPacket, s) -> {
            byte[] sIP = new byte[4];
            byte[] dIP = new byte[4];
            Ip4 ip = pcapPacket.getHeader(new Ip4());
            if (!pcapPacket.hasHeader(ip)) {
                map.put("err", "没有捕获到数据报");
                return;
            }
            if (pcapPacket.size() < 32) {
                map.put("err", "没有捕获到数据报");
                return;
            }
            List<String> list = new ArrayList<>();
            for (byte b : pcapPacket.getByteArray(0, pcapPacket.size())) {
                list.add(String.valueOf(b));
            }
            sIP = ip.source();
            dIP = ip.destination();
            map.put("version", String.valueOf(ip.version()));
            map.put("head length", String.valueOf(ip.getHeaderLength()));
            map.put("TOS", String.valueOf(ip.tos()));
            map.put("length", String.valueOf(pcapPacket.getTotalSize()));
            map.put("identification", Tools.decToHex(list.get(18)) + Tools.decToHex(list.get(19)));
            map.put("flags", String.valueOf(ip.flags()));
            map.put("offset", String.valueOf(ip.offset()));
            map.put("ttl", String.valueOf(ip.ttl()));
            map.put("type", String.valueOf(ip.type()));
            map.put("check", String.valueOf(ip.checksum()));
            map.put("source", FormatUtils.ip(sIP));
            map.put("des", FormatUtils.ip(dIP));
            System.out.println(map);
        };
        for (PcapIf device : devices) {
            System.out.println(device.getName() + " " + device.getDescription());
            if (!device.getAddresses().get(0).getNetmask().toString().equals("[0]")) {
                select = devices.get(devices.indexOf(device));
                break;
            }
        }

        Pcap pcap = Pcap.openLive(select.getName(), len, flag, timeout, err);
        if (pcap == null) {
            System.err.println("Error while opening device for capture: " + err.toString());
        } else {
            pcap.loop(1, handler, "jNetCap");
            pcap.close();
        }
    }

    public static HashMap<String, String> getMap() {
        return map;
    }
}