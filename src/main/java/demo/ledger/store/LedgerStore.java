package demo.ledger.store;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;

import java.io.File;
import java.io.IOException;

import static org.fusesource.leveldbjni.JniDBFactory.factory;

/**
 *
 *
 */
public class LedgerStore {
    public BlockStore blockStore = null;
    public StateStore stateStore = null;
    public EventStore eventStore = null;
    private DB blockDb;
    private DB stateDb;
    private DB eventDb;
    public LedgerStore(String filePath){
        blockDb = init(filePath+"/block");
        stateDb = init(filePath+"/states");
        eventDb = init(filePath+"/ledgerevent");
        blockStore = new BlockStore(blockDb);
        stateStore = new StateStore(stateDb);
        eventStore = new EventStore(eventDb);
    }
    public DB init(String filePath){
        Options options = new Options();
        options.createIfMissing(true);
        File file = new File(filePath);
        if (!file.exists()) {
            file = new File(filePath);
        }
        try {
            return factory.open(file, options);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
