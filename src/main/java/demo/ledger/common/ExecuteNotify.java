package demo.ledger.common;

import com.github.ontio.common.UInt256;
import com.github.ontio.io.BinaryReader;

import java.io.IOException;


/**
 *
 *
 */
public class ExecuteNotify {
    public byte[] TxHash;
    public byte State;
    public long GasConsumed;
    public NotifyEventInfo[] Notify;
}
