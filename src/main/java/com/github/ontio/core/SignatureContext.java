package com.github.ontio.core;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.github.ontio.common.Address;
import com.github.ontio.core.asset.Sig;
import org.bouncycastle.math.ec.ECPoint;

import com.github.ontio.common.Helper;
import com.github.ontio.core.scripts.ScriptBuilder;
import com.github.ontio.crypto.ECC;
import com.github.ontio.io.json.JArray;
import com.github.ontio.io.json.JBoolean;
import com.github.ontio.io.json.JObject;
import com.github.ontio.io.json.JString;

/**
 *  签名上下文
 *  
 */
public class SignatureContext {
    /**
     *  要签名的数据
     */
    public final Signable signable;
    /**
     *  要验证的脚本散列值
     */
    public final Address[] addressU160;
    /**
     * 公钥-签名数据
     */
    private final Map<ECPoint, byte[]>[] signatures;
    
    private final boolean[] completed;

    /**
     *  判断签名是否完成
     */
    public boolean isCompleted() {
        for (boolean b : completed) {
        	if (!b) {
        		return false;
        	}
        }
        return true;
    }

    /**
     *  对指定的数据构造签名上下文
     *  <param name="signable">要签名的数据</param>
     */
    @SuppressWarnings("unchecked")
	public SignatureContext(Signable signable) {
        this.signable = signable;
        this.addressU160 = signable.getAddressU160ForVerifying();
        this.signatures = (Map<ECPoint, byte[]>[]) Array.newInstance(Map.class, addressU160.length);
        this.completed = new boolean[addressU160.length];
    }

    /**
     *  添加一个签名
     *  <param name="contract">该签名所对应的合约</param>
     *  <param name="pubkey">该签名所对应的公钥</param>
     *  <param name="signature">签名</param>
     *  <returns>返回签名是否已成功添加</returns>
     */
    public boolean add(Address address, ECPoint pubkey, byte[] signature) {
        for (int i = 0; i < addressU160.length; i++) {
            if (addressU160[i].equals(address)) {
                if (signatures[i] == null) {
                	signatures[i] = new HashMap<ECPoint, byte[]>();
                }
                signatures[i].put(pubkey, signature);
                completed[i] = true;
                return true;
            }
        }
        return false;
    }

    public Sig[] getSigs() {
        if (!isCompleted()) {
            throw new IllegalStateException();
        }
        Sig[] scripts = new Sig[signatures.length];
        for (int i = 0; i < scripts.length; i++) {
                scripts[i] = new Sig();
                scripts[i].M++;
                scripts[i].pubKeys = new ECPoint[signatures[i].size()];
                scripts[i].sigData = new byte[signatures[i].size()][];
                int j = 0;
                for (Map.Entry e:signatures[i].entrySet()){
                    scripts[i].pubKeys[j] = (ECPoint) e.getKey();
                    scripts[i].sigData[j++] = (byte[])e.getValue();
                }
        }
        return scripts;
    }

    /**
     *  把签名上下文转为json对象
     *  <returns>返回json对象</returns>
     */
    public JObject json() {
        JObject json = new JObject();
        json.set("type", new JString(signable.getClass().getTypeName()));
        json.set("hex", new JString(Helper.toHexString(signable.getHashData())));
        JArray scripts = new JArray();
        for (int i = 0; i < signatures.length; i++) {
            if (signatures[i] == null) {
                scripts.add(null);
            } else {
                scripts.add(new JObject());
                JArray sigs = new JArray();
                for (Entry<ECPoint, byte[]> pair : signatures[i].entrySet()) {
                    JObject signature = new JObject();
                    signature.set("pubkey", new JString(Helper.toHexString(pair.getKey().getEncoded(true))));
                    signature.set("signature", new JString(Helper.toHexString(pair.getValue())));
                    sigs.add(signature);
                }
                scripts.get(i).set("signatures", sigs);
                scripts.get(i).set("completed", new JBoolean(completed[i]));
            }
        }
        json.set("scripts", scripts);
        return json;
    }

    @Override 
    public String toString() {
        return json().toString();
    }
}
