package ontology.core;

import java.time.Duration;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Map;
import java.util.stream.Stream;

import ontology.core.payload.Vote;
import org.bouncycastle.math.ec.ECPoint;

import ontology.common.Fixed8;
import ontology.common.Helper;
import ontology.common.Out;
import ontology.common.UInt160;
import ontology.common.UInt256;
import ontology.core.scripts.Program;
import ontology.crypto.ECC;
import ontology.core.contract.Contract;

/**
 *  实现区块链功能的基类
 */
public abstract class Blockchain implements AutoCloseable {
    /**
     *  产生每个区块的时间间隔，已秒为单位
     */
    public static final int SECONDS_PER_BLOCK = 15;
    /**
     *  小蚁币产量递减的时间间隔，以区块数量为单位
     */
    public static final int DECREMENT_INTERVAL = 2000000;
    /**
     *  每个区块产生的小蚁币的数量
     */
    public static final int[] MINTING_AMOUNT = { 8, 7, 6, 5, 4, 3, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
    /**
     *  产生每个区块的时间间隔
     */
    public static final Duration TIME_PER_BLOCK = Duration.ofSeconds(SECONDS_PER_BLOCK);
    /**
     *  后备记账人列表
     */
    public static final ECPoint[] STANDBY_MINERS = {
        ECC.secp256r1.getCurve().decodePoint(Helper.hexToBytes("0327da12b5c40200e9f65569476bbff2218da4f32548ff43b6387ec1416a231ee8")),
        ECC.secp256r1.getCurve().decodePoint(Helper.hexToBytes("026ce35b29147ad09e4afe4ec4a7319095f08198fa8babbe3c56e970b143528d22")),
        ECC.secp256r1.getCurve().decodePoint(Helper.hexToBytes("0209e7fd41dfb5c2f8dc72eb30358ac100ea8c72da18847befe06eade68cebfcb9")),
        ECC.secp256r1.getCurve().decodePoint(Helper.hexToBytes("039dafd8571a641058ccc832c5e2111ea39b09c0bde36050914384f7a48bce9bf9")),
        ECC.secp256r1.getCurve().decodePoint(Helper.hexToBytes("038dddc06ce687677a53d54f096d2591ba2302068cf123c1f2d75c2dddc5425579")),
        ECC.secp256r1.getCurve().decodePoint(Helper.hexToBytes("02d02b1873a0863cd042cc717da31cea0d7cf9db32b74d4c72c01b0011503e2e22")),
        ECC.secp256r1.getCurve().decodePoint(Helper.hexToBytes("034ff5ceeac41acf22cd5ed2da17a6df4dd8358fcb2bfb1a43208ad0feaab2746b")),
    };

    /**
     *  区块链所提供的功能
     */
    public abstract EnumSet<BlockchainAbility> ability();
    /**
     *  当前最新区块散列值
     * @throws Exception 
     */
    public abstract UInt256 currentBlockHash() throws Exception;
    /**
     *  当前最新区块头的散列值
     * @throws Exception 
     */
    public UInt256 currentHeaderHash() throws Exception{ return currentBlockHash(); }
    /**
     *  默认的区块链实例
     */
    private static Blockchain _default = null;
    public static Blockchain current() { return _default; }
    /**
     *  区块头高度
     * @throws Exception 
     */
    public int headerHeight() throws Exception { return height(); }
    /**
     *  区块高度
     * @throws Exception
     */
    public abstract int height() throws Exception;
    /**
     *  表示当前的区块链实现是否为只读的
     */
    public abstract boolean isReadOnly();

    /**
     *  将指定的区块添加到区块链中
     *  <param name="block">要添加的区块</param>
     *  <returns>返回是否添加成功</returns>
     */
    protected abstract boolean addBlock(Block block);

    /**
     *  将指定的区块头添加到区块头链中
     *  <param name="headers">要添加的区块头列表</param>
     */
    protected abstract void addHeaders(Iterable<Block> headers);
    
    @Override
    public abstract void close();

    /**
     *  判断区块链中是否包含指定的资产
     *  <param name="hash">资产编号</param>
     *  <returns>如果包含指定资产则返回true</returns>
     */
    public boolean containsAsset(UInt256 hash) { return false;}

    /**
     *  判断区块链中是否包含指定的区块
     *  <param name="hash">区块编号</param>
     *  <returns>如果包含指定区块则返回true</returns>
     */
    public boolean containsBlock(UInt256 hash){ return false;}

    /**
     *  判断区块链中是否包含指定的交易
     *  <param name="hash">交易编号</param>
     *  <returns>如果包含指定交易则返回true</returns>
     */
    public boolean containsTransaction(UInt256 hash){ return false;}


    public abstract boolean containsUnspent(UInt256 hash, int index) throws Exception;


    /**
     *  根据指定的高度，返回对应的区块信息
     *  <param name="height">区块高度</param>
     *  <returns>返回对应的区块信息</returns>
     * @throws Exception 
     */
    public Block getBlock(int height) throws Exception {
        return getBlock(getBlockHash(height));
    }

    /**
     *  根据指定的散列值，返回对应的区块信息
     *  <param name="hash">散列值</param>
     *  <returns>返回对应的区块信息</returns>
     * @throws Exception 
     */
    public abstract Block getBlock(UInt256 hash) throws Exception;

    /**
     *  根据指定的高度，返回对应区块的散列值
     *  <param name="height">区块高度</param>
     *  <returns>返回对应区块的散列值</returns>
     * @throws Exception 
     */
    public abstract UInt256 getBlockHash(int height);

    /**
     *  根据指定的高度，返回对应的区块头信息
     *  <param name="height">区块高度</param>
     *  <returns>返回对应的区块头信息</returns>
     * @throws Exception 
     */
    public Block getHeader(int height) throws Exception {
        return getHeader(getBlockHash(height));
    }

    /**
     *  根据指定的散列值，返回对应的区块头信息
     *  <param name="hash">散列值</param>
     *  <returns>返回对应的区块头信息</returns>
     * @throws Exception 
     */
    public Block getHeader(UInt256 hash) throws Exception {
        Block b = getBlock(hash);
        return b == null ? null : b.header();
    }

    public abstract UInt256[] getLeafHeaderHashes();

    /**
     *  获取记账人的合约地址
     *  <param name="miners">记账人的公钥列表</param>
     *  <returns>返回记账人的合约地址</returns>
     */
    public static UInt160 getMinerAddress(ECPoint[] miners) {
        return Contract.addressFromMultiPubKeys(miners.length - (miners.length - 1) / 3, miners);
    }

    private ArrayList<ECPoint> _miners = new ArrayList<ECPoint>();
    /**
     *  获取下一个区块的记账人列表
     *  <returns>返回一组公钥，表示下一个区块的记账人列表</returns>
     */
    public ECPoint[] getMiners() {
        synchronized (_miners) {
            if (_miners.size() == 0) {
            	// ...
            }
            return _miners.toArray(new ECPoint[_miners.size()]);
        }
    }

    /**
     *  根据指定的散列值，返回下一个区块的信息
     *  <param name="hash">散列值</param>
     *  <returns>返回下一个区块的信息>
     */
    public abstract Block getNextBlock(UInt256 hash);

    /**
     *  根据指定的散列值，返回下一个区块的散列值
     *  <param name="hash">散列值</param>
     *  <returns>返回下一个区块的散列值</returns>
     */
    public abstract UInt256 getNextBlockHash(UInt256 hash);

    /**
     *  根据指定的资产编号，返回对应资产的发行量
     *  <param name="asset_id">资产编号</param>
     *  <returns>返回对应资产的当前已经发行的数量</returns>
     */
    public abstract Fixed8 getQuantityIssued(UInt256 asset_id);

    /**
     *  根据指定的区块高度，返回对应区块及之前所有区块中包含的系统费用的总量
     *  <param name="height">区块高度</param>
     *  <returns>返回对应的系统费用的总量</returns>
     * @throws Exception 
     */
    public long getSysFeeAmount(int height) throws Exception {
        return getSysFeeAmount(getBlockHash(height));
    }

    /**
     *  根据指定的区块散列值，返回对应区块及之前所有区块中包含的系统费用的总量
     *  <param name="hash">散列值</param>
     *  <returns>返回系统费用的总量</returns>
     */
    public abstract long getSysFeeAmount(UInt256 hash);

    /**
     *  根据指定的散列值，返回对应的交易信息
     *  <param name="hash">散列值</param>
     *  <returns>返回对应的交易信息</returns>
     * @throws Exception 
     */
    public Transaction getTransaction(UInt256 hash) throws Exception {
        Out<Integer> height = new Out<Integer>();
        return getTransaction(hash, height);
    }

    /**
     *  根据指定的散列值，返回对应的交易信息与该交易所在区块的高度
     *  <param name="hash">交易散列值</param>
     *  <param name="height">返回该交易所在区块的高度</param>
     *  <returns>返回对应的交易信息</returns>
     */
    public Transaction getTransaction(UInt256 hash, Out<Integer> height) { return null; }


    /**
     *  根据指定的散列值和索引，获取对应的未花费的资产
     *  <param name="hash">交易散列值</param>
     *  <param name="index">输出的索引</param>
     *  <returns>返回一个交易输出，表示一个未花费的资产</returns>
     * @throws Exception 
     */

    /**
     *  获取选票信息
     *  <returns>返回一个选票列表，包含当前区块链中所有有效的选票</returns>
     */
    public Stream<Vote> getVotes() {
        return getVotes(Stream.empty());
    }

    public abstract Stream<Vote> getVotes(Stream<Transaction> others);

    /**
     *  判断交易是否双花
     *  <param name="tx">交易</param>
     *  <returns>返回交易是否双花</returns>
     */
    public abstract boolean isDoubleSpend(Transaction tx);

    /**
     *  注册默认的区块链实例
     *  <param name="blockchain">区块链实例</param>
     *  <returns>返回注册后的区块链实例</returns>
     */
    public static Blockchain register(Blockchain blockchain) {
        if (blockchain == null) {
        	throw new NullPointerException();
        }
        if (_default != null) {
        	_default.close();
        }
        _default = blockchain;
        return blockchain;
    }
    
    public int getBlockHeightFromDb() throws Exception {return 0;}
    public Block getBlockFromDb(int height) throws Exception {return null;}
    
    
    
}
