package demo.ledger;


import com.alibaba.fastjson.JSON;
import com.github.ontio.OntSdk;
import com.github.ontio.common.Address;
import com.github.ontio.common.Helper;
import com.github.ontio.common.UInt256;
import com.github.ontio.core.payload.DeployCode;
import demo.ledger.common.BookkeeperState;
import demo.ledger.common.ExecuteNotify;
import demo.ledger.p2p.*;
import demo.ledger.store.LedgerStore;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Base64;
import java.util.Map;


/**
 *
 * 
 */

public class P2pDemo {

    public static void main(String[] args) {

        try {
            if (false) {
                String data0 = "60ab778c70696e67000000000000000008000000e1cc25030a00000000000000";
                byte[] bys = Helper.hexToBytes(data0);
                Message msg = new Message();
                msg.deserialization(bys);
                System.out.println(Helper.toHexString(msg.message));
                System.exit(0);
            }

            Socket s = new Socket("seed1.ont.io",20338);
            OutputStream out = s.getOutputStream();
            InputStream in = s.getInputStream();
            byte[] buf = new byte[1024*256];
            int len = 0;

            VersionReq versionReq = new VersionReq();
            out.write(versionReq.msgSerialization());

            len = in.read(buf);

            if(true) {
                Thread.sleep(500);
                PingReq pingReq = new PingReq(1000);
                out.write(pingReq.msgSerialization());
                len = in.read(buf);
                byte[] data = new byte[len];
                System.arraycopy(buf, 0, data, 0, len);
                System.out.println("recv:" + Helper.toHexString(data));
                Message msg2 = new Message();
                msg2.deserialization(data);
            }

            if(false) {
                Thread.sleep(500);
                DataReq dataReq = new DataReq((byte)2,Helper.reverse(Helper.hexToBytes("a0cc1573b46b5ed9de71691120312af2cbf75edafc47d9e602930941da382104")));
                out.write(dataReq.msgSerialization());
                len = in.read(buf);
                byte[] data = new byte[len];
                System.arraycopy(buf, 0, data, 0, len);
                System.out.println("recv:" + Helper.toHexString(data));
                Message msg2 = new Message();
                msg2.deserialization(data);
            }

            if(false) {
                Thread.sleep(500);
                HeadersReq headersReq = new HeadersReq();
                headersReq.len = 1;
                headersReq.hashEnd = Helper.reverse(Helper.hexToBytes("0000000000000000000000000000000000000000000000000000000000000000"));
                headersReq.hashStart = Helper.reverse(Helper.hexToBytes("7ce53556b7e44410c02e214eb699af68a4047801d4cf5ba589dec4124558e7c5"));
                out.write(headersReq.msgSerialization());

                len = in.read(buf);
                byte[] data = new byte[len];
                System.arraycopy(buf, 0, data, 0, len);
                System.out.println("recv:" + Helper.toHexString(data));
                Message msg2 = new Message();
                msg2.deserialization(data);
            }

            s.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
