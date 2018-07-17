package demo.ledger.common;

import com.github.ontio.common.Address;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.io.Serializable;

import java.io.IOException;
import java.util.List;

/**
 *
 *
 */
public class NotifyEventInfo {
    public byte[] ContractAddress;
    public List<Object> States;
}
